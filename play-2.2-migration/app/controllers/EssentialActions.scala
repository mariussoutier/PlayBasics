package controllers

import scala.concurrent.Future
// Import the method statically and rename it
import scala.concurrent.Future.{successful => resolve}
import scala.concurrent.ExecutionContext.Implicits.global

import play.api._
import play.api.mvc._

import play.api.libs.iteratee.{Iteratee,Done}
import play.api.libs.json._

import models._
import models.json._


/*
 * 1. EssentialActions
 * ~~~~
 * EssentialActions replace Play 2.0/2.1's Action composition.
 * Put your 'high-level' actions in a trait that can be mixed into every controller. Stuff like
 * authentication and authorization will be used by every controller throughout your app.
 * EssentialActions allow you to easily compose functions that depend on the request but ignore
 * the body.
 * However check first if you can solve the problem at the EssentialFilter level.
 */

object EssentialActions extends Controller {

  /* Let's start with a simple example where you just wrap any other action, be it essential or not */

  /** Prints the time elapsed for a wrapped action to execute */
  def TimeElapsed(action: EssentialAction): EssentialAction = EssentialAction { requestHeader =>
    val start = System.currentTimeMillis
    action(requestHeader).map { res =>
      val totalTime = System.currentTimeMillis - start
      println("Elapsed time: %1d ms".format(totalTime))
      res
    }
  }

  def short = TimeElapsed {
    Action {
      val res = for (i <- 0 until 100000) yield i
      Ok(res.mkString(", "))
    }
  }
  def shortAsync = TimeElapsed {
    Action.async {
      Future {
        val res = for (i <- 0 until 100000) yield i
        Ok(res.mkString(", "))
      }
    }
  }

  /*
   * Next up is authorization handling. This is such a good use-case for EssentialActions because
   * not only is the body completely uninteresting if the user is not authenticated or authorized,
   * but also because EssentialActions are just functions, and so we can build one on top of the
   * other.
   */

  /**
   * Reads the security token from the RequestHeader. If the header is correct, the enclosed Action
   * will be executed. Otherwise returns a 401 Unauthorized response without invoking the action.
   * We not only wrap an action but pass the extracted token to that action.
   */
  def HasToken(action: String => EssentialAction): EssentialAction = EssentialAction { requestHeader =>
    val maybeToken = requestHeader.headers.get("X-SECRET-TOKEN")
    maybeToken map { token =>
      action(token)(requestHeader) // apply requestHeader to EssentialAction produces the Iteratee[Array[Byte], SimpleResult]
    } getOrElse {
      Done(Unauthorized("401 No Security Token\n")) // 'Done' means the Iteratee has completed its computations
    }
  }

  /**
   * HasPermission directly builds on top of HasToken. If there is no token, this action will never
   * be executed. Implementing this in an OO-style proves to be cumbersome.
   * An example for an OO-EssentialAction is [[play.api.cache.Cached]].
   */
  def HasPermission(permissions: Permission*)(action: User => EssentialAction): EssentialAction =
    HasToken { token => // We compose with HasToken to make sure there is a valid token, and then use the token
      EssentialAction { requestHeader => // EssentialAction again, because we need the header and we must return an EA
        val user = User.getByToken(token) // Use the token to the retrieve the User
        user map { user =>
          if (permissions.contains(user.permission)) { // The actual permissions check
            action(user)(requestHeader)  // Execute only if permissions match
          } else {
            Done[Array[Byte], SimpleResult](Forbidden) // Must be typed because the compiler cannot infer
          }
        } getOrElse(Done[Array[Byte], SimpleResult](NotFound)) // Must be typed because the compiler cannot infer
      }
    }

  /*
   * Should pass: curl -H "X-SECRET-TOKEN: secret-123" localhost:9000/ea/token
   * Should fail: curl localhost:9000/ea/token
   */
  def withToken = HasToken { token =>
    Action { request =>
      Ok("Access granted.\n")
    }
  }

  /*
   * Should pass: curl -H "X-SECRET-TOKEN: secret-123" localhost:9000/ea/token-async
   * Should fail: curl localhost:9000/ea/token-async
   */
  def withTokenLongRunning = HasToken { token =>
    Action.async {
      Future.successful(Ok("Access granted.\n"))
    }
  }

  def adminAction = HasPermission(AdminPermission) { user =>
    Action { request =>
      Ok("Admin Access granted.\n")
    }
  }

  def adminActionLong = HasPermission(AdminPermission) { user =>
    Action.async { request =>
      Future.successful(Ok("Admin Access granted.\n"))
    }
  }

  // Compose on the spot; this action reduces boilerplate
  def CanEditUser(id: Long)(action: User => EssentialAction): EssentialAction = HasPermission(UserPermission) { currentUser =>
    EssentialAction { requestHeader =>
      if (currentUser.id.exists(_ == id)) {
        action(currentUser)(requestHeader)
      } else {
        Done(Forbidden("403 Not allowed to access this user.\n"))
      }
    }
  }

  /** An action without body, should use the empty body parser to avoid problems with Content-Type. */
  def fetchUser(id: Long) = CanEditUser(id) { user =>
    // If we specify a body parser, Action must have a parameter (Request[A]), which we choose to ignore
    // `parse` can be found in [[play.api.mvc.BodyParsers]]
    Action(parse.empty) { _ =>  // The underscore signals to the reader that the parameter isn't used
      Ok
    }
  }

  def updateUser(id: Long) = CanEditUser(id) { user =>
    Action(parse.json) { request =>
      request.body.validate[User] match {
        case JsSuccess(user, _) => {
          // User.update(user)
          Ok
        }
        case JsError(err) => BadRequest
      }
    }
  }

  /** An action without body, should use the empty body parser to avoid problems with Content-Type. */
  def delete(id: Long) = HasPermission(UserPermission) { _ =>
    Action(parse.empty) { _ =>
      Ok(s"Deleted $id\n")
    }
  }

  /**
   * We could also use EssentialAction directly, but it's a little too ugly.
   * This approach is not recommended.
   */
  def deleteWithEssentials(id: Long) = HasPermission(UserPermission) { _ =>
    EssentialAction { _ =>
      Done(Ok(s"Deleted $id\n"))
    }
  }

  // Inventory - another example of nesting EssentialActions and applying business rules

  def CanAccessInventory(id: Long)(action: User => Inventory => EssentialAction): EssentialAction =
    HasPermission(AdminPermission) { user =>
      EssentialAction { requestHeader =>
        val maybeInventory = Inventory.findOne()
        maybeInventory.map { inventory =>
          // Business rule: Inventory must be associated with matching department
          if (user.department == inventory.department) {
            action(user)(inventory)(requestHeader)
          } else {
            Done[Array[Byte], SimpleResult](Forbidden)
          }
        }.getOrElse(Done[Array[Byte], SimpleResult](NotFound))
      }
    }

  def getInventory(id: Long) = CanAccessInventory(id) { user => inventory =>
    TimeElapsed {
      Action(parse.empty) { request =>
        Ok
      }
    }
  }

}
