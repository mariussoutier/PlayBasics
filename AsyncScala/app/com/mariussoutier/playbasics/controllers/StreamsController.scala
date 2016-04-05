package com.mariussoutier.playbasics.controllers

import akka.stream.scaladsl.Source
import play.api.Configuration
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.WSAPI
import play.api.mvc.{Action, Controller}

import scala.collection.immutable
import scala.concurrent.ExecutionContext

/**
  * TODO Example of using Akka Streams in Play 2.5.
  */
class StreamsController extends Controller {

  def index = Action {
    Ok
  }

  def enum() = Action {
    // `chunked` is the method that streams content to the browser
    // Data is streamed from an AkkaStreams Source
    // must be immutable.Seq because AkkaStreams messed up imports
    val source = Source(immutable.Seq("1", "2", "3", "4", "5"))
    val transformed = source.map(x => (x.toInt * 10).toString + "\n")
    Ok.chunked(transformed)
  }

  // Ok has some convenience stuff to stream static files and resources
  def streamFile() = Action {
    Ok.sendResource("data.txt", inline = true)
  }

}

/**
  * Example - Twitter live streaming using Server Sent Events
  */
class TwitterStreams(configuration: Configuration, ws: WSAPI)(implicit val ec: ExecutionContext) extends Controller {

  def filterRTs(tweet: String) = !tweet.startsWith("RT")

  def toServerEvent(tweet: String) = "data: " + tweet + "\n"

  val consumerKey = ConsumerKey(
    configuration.getString("twitter.consumer.key").getOrElse("???"),
    configuration.getString("twitter.consumer.secret").getOrElse("???")
  )
  val accessToken = RequestToken(
    configuration.getString("twitter.token.key").getOrElse("???"),
    configuration.getString("twitter.token.secret").getOrElse("???")
  )

  def streamForKeyword(track: String) = Action.async { request =>
    ws.url("https://stream.twitter.com/1/statuses/filter.json?track=" + track) // withQueryString seems to have a bug
      .sign(OAuthCalculator(consumerKey, accessToken))
      .stream()
      .map { streamedResponse =>
        // body is the Source
        val twitterStreamEvent = streamedResponse.body
          .map(_.utf8String)
          .filter(filterRTs)
          .map(toServerEvent)

        Ok chunked twitterStreamEvent as "text/event-stream"
      }
  }

  def showStream = Action { implicit request =>
    Ok(com.mariussoutier.playbasics.views.html.twitterStream())
  }

}
