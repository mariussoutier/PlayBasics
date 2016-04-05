package com.mariussoutier.playbasics.controllers

import play.api.libs.iteratee.{Enumeratee, Enumerator}
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.WSAPI
import play.api.mvc._
import play.api.{Configuration, Environment}

import scala.concurrent.ExecutionContext

// 2. Iteratees process chunks of data
// Changed in 2.5: Iteratees are no longer the primary streaming mechanism in Play
class Iteratees(environment: Environment)(implicit val ec: ExecutionContext) extends Controller {

  def index = Action {
    Ok
  }

  // Iteratees are fed data from Enumerators
  def enum() = Action {
    val enumerator = Enumerator("1", "2", "3", "4", "5")
    Ok.feed(enumerator)
  }

  // Read a file from input stream and send to output
  def enumInputStream() = Action {
    environment.resourceAsStream("/data.txt").map { stream =>
      val enumerator = Enumerator.fromStream(stream)
      Ok.chunked(enumerator).as(TEXT)
    }.getOrElse(InternalServerError("Resource not found"))
  }

  // Enumeratees transform Iteratees and Enumerators
  def enumeratee() = Action {
    val enumerator = Enumerator("1", "2", "3", "4", "5")
    val timesTen = Enumeratee.map[String](x => (x.toInt * 10).toString)
    Ok.feed(enumerator &> timesTen)
  }
}

/**
  * Example - Twitter live streaming using Server Sent Events
  */
class Twitter(configuration: Configuration, ws: WSAPI)(implicit val ec: ExecutionContext) extends Controller {

  val bytesToString: Enumeratee[Array[Byte], String] = Enumeratee.map[Array[Byte]] { bytes: Array[Byte] =>
    new String(bytes)
  }

  val filterRTs = Enumeratee.filter[String](!_.startsWith("RT"))

  val toServerEvent = Enumeratee.map[String] { tweet =>
    "data: " + tweet + "\n"
  }

  val consumerKey = ConsumerKey(
    configuration.getString("twitter.consumer.key").getOrElse("???"),
    configuration.getString("twitter.consumer.secret").getOrElse("???")
  )
  val accessToken = RequestToken(
    configuration.getString("twitter.token.key").getOrElse("???"),
    configuration.getString("twitter.token.secret").getOrElse("???")
  )

  def streamForKeyword(track: String) = Action.async { request =>
    // Stream to output using an Iteratee
    ws.url("https://stream.twitter.com/1/statuses/filter.json?track=" + track) // withQueryString seems to have a bug
      .sign(OAuthCalculator(consumerKey, accessToken))
      .getStream()
      .map { case (headers, enumerator) =>
        // Compose the Enumeratees (><> or compose) and apply the result to the Iteratee (done by Ok.chunked)
        val twitterStreamEvent = enumerator &> bytesToString ><> filterRTs ><> toServerEvent
        Ok chunked (twitterStreamEvent) as "text/event-stream"
      }
  }

  def showStream = Action { implicit request =>
    Ok(com.mariussoutier.playbasics.views.html.twitterStream())
  }

}
