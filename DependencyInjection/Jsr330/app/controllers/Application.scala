package controllers

import javax.inject.Inject

import components.Repository
import play.api.mvc.{Action, Controller}
import play.api.libs.ws._

import scala.concurrent.ExecutionContext

/**
 *
 * @param ws WSClient will get injected,
 */
class Application @Inject() (ws: WSClient, repo: Repository, implicit val ec: ExecutionContext) extends Controller {

  // Application will be instantiated each time the route is called
  println("Hi from Application!")

  def index() = Action.async {
    ws.url("http://www.playframework.com").get().map { response =>
      Ok(response.body).as(HTML)
    }
  }

}
