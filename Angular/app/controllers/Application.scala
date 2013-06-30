package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def jsRoutes(varName: String = "jsRoutes") = Action { implicit request =>
    Ok(
      Routes.javascriptRouter(varName)(
        routes.javascript.Application.login,
        routes.javascript.Users.user
      )
    ).as(JAVASCRIPT)
  }

  def login() = Action(parse.json) { implicit request =>
    // Check credentials and so on...
    Ok(Json.obj("token" -> java.util.UUID.randomUUID().toString))
  }

}
