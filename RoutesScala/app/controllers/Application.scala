package controllers

import play.api._
import play.api.mvc._

import play.api.Play
import play.api.Play.current


object Application extends Controller {

  // Add a cookie if the site was opened with a referral code
  def index(ref: Option[String] = None) = Action { implicit request =>
    ref map { code =>
      val cookie = Cookie(name = "REFERRAL", value = code, httpOnly = true, maxAge = 60 * 60)
      Ok(views.html.index("")).withCookies(cookie)
    } getOrElse Ok(views.html.index(""))
  }

  def languages(implicit request: RequestHeader) =
    Play.configuration.getString("application.langs").getOrElse("en").split(",")

}
