package controllers

import com.google.inject.Inject
import models._
import play.api.cache._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nComponents, Messages}
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Environment}

/** General Application actions, mainly session management */
class Application @Inject() (val configuration: Configuration, val environment: Environment, val cache: CacheApi)
  extends Controller
  with Security
  with I18nComponents {

  import scala.concurrent.duration._

  lazy val CacheExpiration =
    configuration.getInt("cache.expiration").getOrElse(60 /*seconds*/ * 2 /* minutes */).seconds

  /** Returns the index page */
  def index = Action {
    Ok(views.html.index())
  }

  case class Login(email: String, password: String)

  val loginForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply)
  )

  implicit class ResultWithToken(result: Result) {
    def withToken(token: (String, Long)): Result = {
      cache.set(token._1, token._2, CacheExpiration)
      result.withCookies(Cookie(AuthTokenCookieKey, token._1, None, httpOnly = false))
    }

    def discardingToken(token: String): Result = {
      cache.remove(token)
      result.discardingCookies(DiscardingCookie(name = AuthTokenCookieKey))
    }
  }

  /** Check credentials, generate token and serve it back as auth token in a Cookie */
  def login = Action(parse.json) { implicit request =>
    implicit val messages = Messages(request2lang, messagesApi)
    loginForm.bind(request.body).fold( // Bind JSON body to form values
      formErrors => BadRequest(Json.obj("err" -> formErrors.errorsAsJson)),
      loginData => {
        User.findByEmailAndPassword(loginData.email, loginData.password) map { user =>
          val token = java.util.UUID.randomUUID().toString
          Ok(Json.obj(
            "authToken" -> token,
            "userId" -> user.id.get
          )).withToken(token -> user.id.get)
        } getOrElse NotFound(Json.obj("err" -> "User Not Found or Password Invalid"))
      }
    )
  }

  /** Invalidate the token in the Cache and discard the cookie */
  def logout = Action { implicit request =>
    request.headers.get(AuthTokenHeader) map { token =>
      Redirect("/").discardingToken(token)
    } getOrElse BadRequest(Json.obj("err" -> "No Token"))
  }

  /**
   * Returns the current user's ID if a valid token is transmitted.
   * Also sets the cookie (useful in some edge cases).
   * This action can be used by the route service.
   */
  def ping() = HasToken() { token => userId => implicit request =>
    User.findOneById (userId) map { user =>
      Ok(Json.obj("userId" -> userId)).withToken(token -> userId)
    } getOrElse NotFound (Json.obj("err" -> "User Not Found"))
  }

}
