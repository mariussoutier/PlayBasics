package com.mariussoutier.playbasics

import com.mariussoutier.playbasics.components.DatabaseClientComponents
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ning.NingWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, Logger}
import play.filters.gzip.GzipFilter
import router.Routes

/**
 * The app loader assembles our application. It receives a [[Context]] and must return an Application.
 */
class AppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = {
    Logger.configure(context.environment)
    new AppComponents(context).application
  }
}

/**
 * Helper class to mix in components and instantiate controllers and the router.
 */
class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
  with NingWSComponents
  with DatabaseClientComponents {

  implicit val ec = actorSystem.dispatcher

  lazy val applicationController = new controllers.Application(wsClient, databaseClient)
  lazy val assets = new controllers.Assets(httpErrorHandler)

  override def router: Router = new Routes(httpErrorHandler, applicationController)

  // Filters are also handled by this helper
  val gzipFilter = new GzipFilter(shouldGzip =
    (request, response) => {
      val contentType = response.headers.get("Content-Type")
      contentType.exists(_.startsWith("text/html"))
    })

  override lazy val httpFilters: Seq[EssentialFilter] = Seq(gzipFilter)
}
