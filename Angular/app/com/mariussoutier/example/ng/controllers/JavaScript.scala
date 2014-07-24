package com.mariussoutier.example.ng.controllers

import play.api.Routes
import play.api.mvc.{Action, Controller}

object JavaScript extends Controller {

  /*(for {
    routes <- Play.current.routes
  } yield ...).getOrElse(Seq())*/

  def jsRoutes(varName: String = "jsRoutes") = Action { implicit request =>
    Ok(
      Routes.javascriptRouter(varName)(
        com.mariussoutier.example.ng.controllers.routes.javascript.Application.login,
        com.mariussoutier.example.ng.controllers.routes.javascript.Users.user
      )
    ).as(JAVASCRIPT)
  }
}
