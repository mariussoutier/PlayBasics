package controllers

import play.api.cache.CacheApi
import play.api.libs.json.Json
import play.api.mvc._

/**
 * Security actions that should be used by all controllers that need to protect their actions.
 * Can be composed to fine-tune access control.
 */
trait Security { self: Controller =>

  val cache: CacheApi

  val AuthTokenHeader = "X-XSRF-TOKEN"
  val AuthTokenCookieKey = "XSRF-TOKEN"
  val AuthTokenUrlKey = "auth"

  /** Checks that a token is either in the header or in the query string */
  def HasToken[A](p: BodyParser[A] = parse.anyContent)(f: String => Long => Request[A] => Result): Action[A] =
    Action(p) { implicit request =>
      val maybeToken = request.headers.get(AuthTokenHeader).orElse(request.getQueryString(AuthTokenUrlKey))
      maybeToken flatMap { token =>
        cache.get[Long](token) map { userid =>
          f(token)(userid)(request)
        }
      } getOrElse Unauthorized(Json.obj("err" -> "No Token"))
    }

}
