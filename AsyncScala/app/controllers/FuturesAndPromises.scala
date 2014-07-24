package controllers

import play.api.libs.ws.WS
import play.api.mvc._
import play.api.Play.current
// Always import Play's EC
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._

/**
 * 1. Futures and Promises
 * A promise is a container that can be filled exactly once. To be notified of this filling (or failure to do so), it
 * exposes a future. The user only works on the future, defining operations on it that will be executed eventually, i.e.
 * when the underlying promise completes.
 *
 * Or to use the analogy by Johan Andren:
 * Father: I promise you a pony when you are old enough. (Promise[Pony])
 * Child: When I will get a pony in the future, I will ride on it. (Future[Pony].map(pony => pony.ride))
 */
object FuturesAndPromises extends Controller {

  def index = Action {
    Ok(views.html.promises())
  }

  def blockingPromiseExample = Action {
    def longComputation = {
      // Prepare the promise
      val prom = Promise[Int]()
      // An async call; imagine this would not support Futures already
      WS.url("http://www.playframework.com").get().foreach { _ =>
        // When the async call completes, we complete the Promises, thus fulfilling the Future
        prom.success(42)
      }
      // Return the future immediately
      prom.future
    }

    // Block the current thread to wait for the result (not good)
    val res = Await.result(longComputation, 3.seconds)
    Ok(res.toString)
  }

  def blockingFutureExample = Action {
    // We can also put a value into future directly
    // This is common if you need to deal with Futures but don't have an asynchronous call
    val future: Future[Int] = Future.successful(1)

    // Wait for the promise to redeem, blocks all operations
    val blocking = Await.result(future, 3.seconds)

    // Future {} runs the embedded computation in the Execution Context's thread pool
    val timed = Future {
      Thread.sleep(3000)
      println("Future is complete!")
    }
    println("Prints before the future")

    Ok("Take a look at the console")
  }

  // -- Non-blocking --

  // Always use async when handling futures
  // Must return Future[Result]
  def futuresCompose = Action.async {
    // Start computations independently
    val eventualSite1 = WS.url("http://www.playframework.com").get()
    val eventualSite2 = WS.url("http://www.typesafe.com").get()
    // Compose them using flatMap/map, or even easier, for-yield
    for {
      site1 <- eventualSite1
      site2 <- eventualSite2
    } yield Ok(site1.body + site2.body)
  }

}
