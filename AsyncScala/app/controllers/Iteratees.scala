package controllers

import play.api.Play
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.{Enumeratee, Iteratee, Enumerator}
import play.api.libs.oauth.{OAuthCalculator, RequestToken, ConsumerKey}
import play.api.libs.ws.WS
import play.api.mvc._

// 2. Iteratees process chunks of data
// Largely TODO
object Iteratees extends Controller {

  def index = Action {
    Ok
  }

  // Iteratees are fed data from Enumerators
  def enum() = Action {
    val enumerator = Enumerator("1", "2", "3", "4", "5")
    Ok.feed(enumerator)
  }

  def enumInputStream() = Action {
    Play.resourceAsStream("/data.txt").map { stream =>
      val enumerator = Enumerator.fromStream(stream)
      Ok.chunked(enumerator)
    }.getOrElse(InternalServerError("Resource not found"))
  }

  // Enumeratees transform Iteratees and Enumerators
  def enumeratee() = Action {
    val enumerator = Enumerator("1", "2", "3", "4", "5")
    val timesTen = Enumeratee.map[String](x => (x.toInt * 10).toString)
    Ok.feed(enumerator &> timesTen)
  }

  /*def other = EssentialAction { headers =>
    Iteratee {}
  }*/

}

// Example - Twitter live streaming using Server Sent Events
object Twitter extends Controller {

  val bytesToString: Enumeratee[Array[Byte], String] = Enumeratee.map[Array[Byte]] { bytes: Array[Byte] =>
    new String(bytes)
  }

  val filterRTs = Enumeratee.filter[String] (!_.startsWith("RT"))

  val toServerEvent = Enumeratee.map[String] { tweet =>
    "data: " + tweet + "\n"
  }

  val consumerKey = ConsumerKey(
    current.configuration.getString("twitter.consumer.key").getOrElse("???"),
    current.configuration.getString("twitter.consumer.secret").getOrElse("???")
  )
  val accessToken = RequestToken(
    current.configuration.getString("twitter.token.key").getOrElse("???"),
    current.configuration.getString("twitter.token.secret").getOrElse("???")
  )

  def streamForKeyword(track: String) = Action.async { request =>
    // Stream to output using an Iteratee
    WS.url("https://stream.twitter.com/1/statuses/filter.json?track=" + track) // withQueryString seems to have a bug
      .sign(OAuthCalculator(consumerKey, accessToken))
      .getStream().map { case (headers, enumerator) =>
      // Compose the Enumeratees (><> or compose) and apply the result to the Iteratee (done by Ok.chunked)
      val x = enumerator &> bytesToString ><> filterRTs ><> toServerEvent
      Ok chunked (x) as "text/event-stream"
    }
  }

  /*def showStream = Action { implicit request =>
    Ok(views.html.twitterStream())
  }*/

}
