package com.mariussoutier.example.ng.controllers

import play.api.mvc.{Action, Controller}
import play.api.routing.JavaScriptReverseRouter

class JavaScript extends Controller {

  /*(for {
    routes <- Play.current.routes
  } yield ...).getOrElse(Seq())*/

  def jsRoutes(varName: String = "jsRoutes") = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter(varName)(
        com.mariussoutier.example.ng.controllers.routes.javascript.Application.login,
        com.mariussoutier.example.ng.controllers.routes.javascript.Users.user
      )
    ).as(JAVASCRIPT)
  }
}
