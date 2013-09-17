package controllers

import scala.concurrent.Future
// Import the method statically and rename it
import scala.concurrent.Future.{successful => resolve}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

import play.api.libs.json._

import models._
import models.json._


/*
 * 4.) Action Composition
 * Actions can also be composed directly.
 * Let's see how we can re-write the EssentialActions to normal Actions.
 */

object ActionComposition extends Controller {

  /** Our basic example, now as an Action */
  case class TimeElapsed[A](action: Action[A]) extends Action[A] {
    def apply(request: Request[A]): Future[SimpleResult] = {
      val start = System.currentTimeMillis
      action(request).map { res =>
        val totalTime = System.currentTimeMillis - start
        println("Elapsed time: %1d ms".format(totalTime))
        res
      }
    }

    lazy val parser = action.parser
  }

  /*
   * Usage pattern is similar to EssentialAction, but we can ignore the Iteratee stuff.
   * This can be combined with any other action.
   */

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
   * The same can be expressed with ActionBuilders and their `composeAction` method.
   * This illustrates how ActionBuilders are meant to make building actions easier.
   */

  object TimeElapsedAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]) =
      block(request)

    override def composeAction[A](action: Action[A]): Action[A] = TimeElapsed(action)
  }

  def elapsedBuilder = TimeElapsedAction { request =>
    val res = for (i <- 0 until 100000) yield i
    Ok(res.mkString(", "))
  }

  def elapsedBuilderAsync = TimeElapsedAction.async {
    Future {
      val res = for (i <- 0 until 100000) yield i
      Ok(res.mkString(", "))
    }
  }

  object Security {

    /*
     * Maybe we'd also like to provide a better API for our security wrappers.
     * Since EssentialActions are functions, we made use of currying.
     * This is a little harder with ActionBuilders.
     */

    /** Contains the security token, extracted from the RequestHeader */
    case class TokenRequest[A](token: String, request: Request[A]) extends WrappedRequest[A](request)

    object hasToken extends ActionBuilder[TokenRequest] {
      def invokeBlock[A](request: Request[A], block: (TokenRequest[A]) => Future[SimpleResult]) = {
        val maybeToken = request.headers.get("X-SECRET-TOKEN")
        maybeToken map { token =>
          block(TokenRequest(token, request))
        } getOrElse {
          resolve(Unauthorized("401 No Security Token\n"))
        }
      }
    }

    /*
     * This works as expected, and is definitely easier to use than an EssentialAction.
     */

    hasToken(parse.json) { r =>
      Ok
    }

    hasToken.async(parse.json) { r =>
      resolve(Ok)
    }


    case class CurrentUserRequest[A](currentUser: User, request: Request[A]) extends WrappedRequest[A](request)

    private def checkPermissions[A](request: TokenRequest[A], permissions: Seq[Permission], block: (CurrentUserRequest[A]) => Future[SimpleResult]): Future[SimpleResult] = {
      val user: Option[User] = User.getByToken(request.token)
      user.map { user =>
        if (permissions.contains(user.permission))
          block(CurrentUserRequest(user, request))
        else
          resolve(Forbidden)
      }.getOrElse {
        resolve(BadRequest)
      }
    }

    /*
     * However now our problems begin. The `composeAction` method doesn't allow passing a parameter
     * as when we use currying, so all we have is the action we want to compose with. We wrap
     * ourselves with `hasToken`, but still have to pattern match on the incoming request.
     */

    def hasPermissionPatternMatching(permissions: Permission*) = new ActionBuilder[CurrentUserRequest] {
      def invokeBlock[A](request: Request[A], block: (CurrentUserRequest[A]) => Future[SimpleResult]): Future[SimpleResult] =
        request match {
          case r: TokenRequest[A] => checkPermissions(r, permissions.toSeq, block)
          case _ => resolve(InternalServerError)
        }

      override def composeAction[A](action: Action[A]) = hasToken.async(action.parser) { tokenRequest =>
        action(tokenRequest)
      }
    }

    def testMatching = hasPermissionPatternMatching(UserPermission)(parse.anyContent) { request =>
      Ok(Json.obj("name" -> request.currentUser.name))
    }

    def testMatchingAsync = hasPermissionPatternMatching(UserPermission).async(parse.anyContent) { request =>
      resolve(Ok(Json.obj("name" -> request.currentUser.name)))
    }


    /* Without composeAction, uses a pre-configured handler (closure) instead  */

    def hasPermission(permissions: Permission*) = new ActionBuilder[CurrentUserRequest] {
      def invokeBlock[A](request: Request[A], block: (CurrentUserRequest[A]) => Future[SimpleResult]): Future[SimpleResult] = {
        val hasTokenHandler: TokenRequest[A] => Future[SimpleResult] = { tokenRequest =>
          checkPermissions(tokenRequest, permissions.toSeq, block)
        }

        hasToken.invokeBlock(request, hasTokenHandler)
      }
    }

    def hasTokenAction[A](action: Action[A]) = hasToken.async(action.parser) { tokenRequest =>
      action(tokenRequest)
    }

    hasPermission(UserPermission)(parse.anyContent) { request =>
      Ok(Json.obj("name" -> request.currentUser.name))
    }

    hasPermission(UserPermission).async(parse.anyContent) { request =>
      resolve(Ok(Json.obj("name" -> request.currentUser.name)))
    }
  }

  def simplerAdminAction = Security.hasToken { request =>
    Ok(request.token)
  }

}
