package controllers

import play.api._
import play.api.mvc._

// 1. Promises allow asynchronous computation
object Promises extends Controller {

  def index = Action {
    Ok(views.html.promises())
  }

  def examples = Action {

    // -- Blocking --
    // Simple promise, just returns 1
    import play.api.libs.concurrent._
    val promise: Promise[Int] = Promise.pure(1)

    // Wait for the promise to redeem, blocks all operations
    val blocking = promise.await.get
    println(blocking)

    // -- Asynchronous --
    import akka.util.duration._
    val timedPromise = Promise.timeout("hi" , 3 seconds)
    timedPromise.onRedeem { res =>
      // Will show after 3 seconds
      println(res)
    }
    println("Prints before the promise")

    /*promise.fold(

    )*/

    Ok("Take a look at the console")
  }

  // -- Non-blocking --
  def asyncAction = Action { implicit request =>
    import play.api.libs.concurrent._
    import akka.util.duration._
    Async { // Async expects a Promise[Result]
      Promise.timeout("Hello World after 3 seconds", 3 seconds).map { res =>
        Ok(res)
      }
    }
  }

}
