import play.api._
import play.api.mvc._
import play.api.mvc.Results._

import controllers.routes

object Global extends GlobalSettings {

  // Taken from an example by James Roper, member of the Play team
  override def onHandlerNotFound(request: RequestHeader): Result =
    if (request.path.endsWith("/"))
      MovedPermanently(request.path.take(request.path.length - 1))
    else
      super.onHandlerNotFound(request)

  // Use this to implement your own router
  //override def onRouteRequest(request: RequestHeader): Option[Handler]

}
