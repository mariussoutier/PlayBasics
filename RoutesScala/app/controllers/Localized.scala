package controllers

import play.api.i18n._
import play.api.mvc._

case class LocalizedRequest[A](request: Request[A], lang: Lang) extends WrappedRequest(request)

/**
 * Helper trait to work with Messages. Builds on Play's i18n support.
 */
trait Localized extends Controller with I18nSupport {

  val langs: Langs
  val messagesApi: MessagesApi

  /*
  I18nSupport provides a method request2Messages that infers the language from the accept header.
  By adding this specialized method for LocalizedRequests, we can control the language in those cases where we want
  to set the language explicitly.
   */
  implicit def request2Messages(implicit request: LocalizedRequest[_]): Messages = Messages(request.lang, messagesApi)

  def ActionWithLanguage(language: String)(f: LocalizedRequest[AnyContent] => Result): Action[AnyContent] = Action { implicit request =>
    f(new LocalizedRequest(request, Lang(language)))
  }

  def ActionWithLang(language: Lang)(f: LocalizedRequest[AnyContent] => Result): Action[AnyContent] = Action { implicit request =>
    f(new LocalizedRequest(request, language))
  }

  def languages: Seq[String] = langs.availables.map(_.language)
}
