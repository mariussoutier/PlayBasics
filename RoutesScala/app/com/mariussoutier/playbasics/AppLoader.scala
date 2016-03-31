package com.mariussoutier.playbasics

import com.mariussoutier.playbasics.controllers.{Application, Faq, Help, Users}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.http.{DefaultHttpErrorHandler, HttpErrorHandler}
import play.api.i18n.I18nComponents
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.core.SourceMapper

import scala.concurrent.Future

class AppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): play.api.Application = {
    LoggerConfigurator(context.environment.classLoader).foreach(_.configure(context.environment))
    new AppComponents(context).application
  }
}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with I18nComponents {

  override def router: Router = new _root_.router.Routes(
    httpErrorHandler,
    new Application(messagesApi, langs),
    new Users,
    new Help(messagesApi, langs),
    new Faq(messagesApi, langs),
    new _root_.controllers.Assets(httpErrorHandler)
  )

  override lazy val httpErrorHandler: HttpErrorHandler = new ErrorHandler(
    environment,
    configuration,
    sourceMapper,
    Some(router)
  )
}

class ErrorHandler(environment: Environment,
                   configuration: Configuration,
                   sourceMapper: Option[SourceMapper],
                   router: => Option[Router]) extends DefaultHttpErrorHandler(environment, configuration, sourceMapper, router) {

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] =
    if (request.path.endsWith("/"))
      Future.successful(MovedPermanently(request.path.take(request.path.length - 1)))
    else
      super.onNotFound(request, message)
}
