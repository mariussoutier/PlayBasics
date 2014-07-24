package controllers

import actors.CounterActor
import akka.pattern.ask
import akka.util.Timeout
import com.mariussoutier.example.Global
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.mvc._

import scala.concurrent.duration._

// 3. Akka
object AkkaExample extends Controller {

  // As long as Play controllers are not actors themselves, we need to use the ask pattern
  // When the actor sends a message back to the controller, the ask is fulfilled

  // Each ask needs a timeout, so we don't wait indefinitely
  implicit val timeout = Timeout(5.seconds)

  // Ask returns a future, and mapping over a future requires an ExecutionContext
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  
  def computation() = Action.async {
    // Creates a new instance of the actor in Play's actor system
    val actor = Akka.system.actorOf(CounterActor.props)
    // We can now send it messages
    actor ! CounterActor.Increase
    actor ! CounterActor.Increase
    actor ! CounterActor.Increase
    actor ! CounterActor.Decrease
    actor ! CounterActor.Increase
    // Finally we want to know what the current count is
    (actor ? CounterActor.Status).mapTo[Int].map { counter =>
      Ok(s"Current counter value is $counter (should be 3)")
    }
  }

  // We can select an already running actor, in this case our scheduled actor, using actorSelection

  def askScheduledState() = Action.async {
    val actor = Akka.system.actorSelection(Global.scheduledName)
    (actor ? CounterActor.Status).mapTo[Int].map { counter =>
      Ok(s"Current counter value is $counter")
    }
  }

  // A special goodie in Play 2.3 is WebSocket support by just sending messages between an actor and a controller
  // This feels very natural in contrast to Iteratees

   /*def webSocket() = WebSocket.acceptWithActor[String, String] { request => out =>

   }*/

}
