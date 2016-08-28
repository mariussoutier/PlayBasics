package com.mariussoutier.playbasics

import com.mariussoutier.playbasics.actors.CounterActor
import com.mariussoutier.playbasics.controllers._
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}
import play.filters.gzip.GzipFilter

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

/**
 * The app loader assembles our application. It receives a [[Context]] and must return an Application.
 */
class AppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = new AppComponents(context).application
}

/**
 * Helper class to mix in components and instantiate controllers and the router.
 */
class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with AhcWSComponents {

  private def selectMessage =
    if (Random.nextBoolean()) CounterActor.Increase else CounterActor.Decrease

  implicit val ec = actorSystem.dispatcher

  val scheduledName = "ScheduledCounterActor"
  val scheduled = actorSystem.scheduler.schedule(1.second, 10.seconds, counterActor, selectMessage)
  lazy val counterActor = actorSystem.actorOf(CounterActor.props, scheduledName)

  applicationLifecycle.addStopHook(() => Future.successful(scheduled.cancel()))

  lazy val futuresController = new FuturesAndPromises
  lazy val iterateesController = new Iteratees(environment)
  lazy val twitterController = new TwitterStreams(configuration, wsApi)
  lazy val akkaExampleController = new AkkaExample(counterActor, actorSystem)
  lazy val streamsController = new StreamsController
  lazy val assets = new _root_.controllers.Assets(httpErrorHandler)

  override def router: Router = new _root_.router.Routes(
    httpErrorHandler,
    futuresController,
    iterateesController,
    twitterController,
    akkaExampleController,
    streamsController,
    assets
  )

  val gzipFilter = new GzipFilter(shouldGzip =
    (request, response) => {
      response.body.contentType.exists(_.startsWith("text/html"))
    })

  override lazy val httpFilters: Seq[EssentialFilter] = Seq(gzipFilter)
}
