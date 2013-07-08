package controllers

import play.api._
import play.api.mvc._

import play.api.libs.oauth._
import play.api.libs.ws._
import play.api.libs.iteratee._


// 4. Putting it all together - Twitter live streaming
object Twitter extends Controller {

  // Twitter OAuth keys - retrieve them from dev.twitter.com
  // TODO Remove my keys -> ??? , ???
  val consumerKey = ConsumerKey(
    "hrke9iMzlIK2X0BgjD4TSQ", "Io0eXN4JRQGQ85GY7nXkW0wteaB2xuYNOdd1YjKzk"
  )

  val accessToken = RequestToken(
    "111932963-bi9a4pc467c5vqZjP4g6QaQpXzYX5gN0fiGELVzY", "AxMdWJ07dHqchTAJ8GF1xI8F25YsrVeqFEFmDopJA"
  )

  // -- Server Sent Events --

  val bytesToString: Enumeratee[Array[Byte], String] = Enumeratee.map[Array[Byte]] { bytes: Array[Byte] =>
    new String(bytes)
  }

  val filterRTs = Enumeratee.filter[String] (!_.startsWith("RT"))

  val toServerEvent = Enumeratee.map[String] { tweet =>
    "data: " + tweet + "\n"
  }

  def streamForKeyword(track: String) = Action { request =>
    // Stream to output using an Iteratee
    Ok stream { socket: Socket.Out[String] => //Socket.Out = Iteratee[Array[Byte], ?]
      WS.url("https://stream.twitter.com/1/statuses/filter.json?track=" + track) // withQueryString seems to have a bug
        .sign(OAuthCalculator(consumerKey, accessToken))
        // Compose the Enumeratees (><> or compose) and apply the result to the Iteratee
        .get(_ => bytesToString ><> filterRTs ><> toServerEvent &> socket)
    } as "text/event-stream"
  }

  def showStream = Action { implicit request =>
    Ok(views.html.twitterStream())
  }

}
