package controllers

import play.api.i18n._
import play.api.Logger
import play.api.mvc._

case class LocalizedRequest[A](request: Request[A], lang: Lang) extends WrappedRequest(request)

trait Localized extends Controller {

  // The current language is inferred from this implicit method by the controller
  override implicit def lang(implicit request: RequestHeader): Lang =
    langFromWrappedRequest.orElse(langFromHeader).getOrElse(defaultLang)

  def langFromWrappedRequest(implicit request: RequestHeader): Option[Lang] = request match {
    case lr: LocalizedRequest[_] => Some(lr.lang)
    case _ => None
  }

  def langFromHeader(implicit request: RequestHeader): Option[Lang] =
    play.api.Play.maybeApplication.flatMap { implicit app =>
      request.acceptLanguages.find(Lang.availables.contains(_))
    }

  def defaultLang: Lang = play.api.Play.maybeApplication.flatMap { implicit app =>
    Lang.availables.headOption
  }.getOrElse(Lang(language = "en"))

  def ActionWithLanguage(language: String)(f: LocalizedRequest[AnyContent] => Result): Action[AnyContent] = Action { implicit request =>
    f(new LocalizedRequest(request, (Lang(language))))
  }

  def ActionWithLang(language: Lang)(f: LocalizedRequest[AnyContent] => Result): Action[AnyContent] = Action { implicit request =>
    f(new LocalizedRequest(request, language))
  }
}
