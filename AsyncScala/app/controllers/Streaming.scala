package controllers

import play.api._
import play.api.mvc._

// 3.
object Streaming extends Controller {

    /*

  // Comet

  def systemStatus() = Action { implicit request =>
    val out = Enumerator.fromCallback { () =>
      Promise.timeout("Ok", 3 seconds)
    }
    Ok.stream { socket: Socket.Out[String] =>

    }
  }

  def () = WebSocket.using[String] { implicit request =>
    val in = Iteratee.compose[String]
    val out = Enumerator.fromCallback { () =>
      Promise.timeout("Ok", 3 seconds)
    }
    (in, out)
  }



    // -- WebSocket + PushEnumerator --

    def byteStringIterateeEnum(enum: PushEnumerator[String]): Iteratee[Array[Byte], Unit] =
      Iteratee.foreach[Array[Byte]] { chunk =>
        enum.push(new String(chunk, "UTF-8"))
      }

    def streamWS = WebSocket.async[String] { implicit request =>
      val in = Iteratee.ignore[String]
      val out = Enumerator.imperative[String]()

      PurePromise(in, out)
    }
  */

}
