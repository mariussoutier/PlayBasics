package controllers

import javax.inject.Inject

import play.api.i18n.{MessagesApi, Langs}
import play.api.mvc._

class Application @Inject() (val messagesApi: MessagesApi, val langs: Langs) extends Controller with Localized {

  // Add a cookie if the site was opened with a referral code
  def index(ref: Option[String] = None) = Action { implicit request =>
    ref map { code =>
      val cookie = Cookie(name = "REFERRAL", value = code, httpOnly = true, maxAge = Some(60 * 60))
      Ok(views.html.index("", languages)).withCookies(cookie)
    } getOrElse Ok(views.html.index("", languages))
  }

  def movedPermanently(to: String) = Action {
    MovedPermanently(to)
  }

}
