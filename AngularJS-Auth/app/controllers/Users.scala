package controllers

import play.api._
import play.api.mvc._

import play.api.cache._
import play.api.libs.json._
import play.api.data._
import play.api.data.Forms._
import play.api.Logger

import models._

/** Access to users */
trait Users extends Controller with Security {

  /** Example for token protected access */
  def myUserInfo() = HasToken() { _ => currentId => implicit request =>
    User.findOneById (currentId) map { user =>
      Ok(Json.toJson(user))
    } getOrElse NotFound (Json.obj("err" -> "User Not Found"))
  }

  /** Just an example of a composed function that checks privileges to access given user */
  def CanEditUser[A](userId: Long, p: BodyParser[A] = parse.anyContent)(f: User => Request[A] => Result) =
    HasToken(p) { _ => currentId => request =>
      if (userId == currentId) { // Imagine role-based checks here
        User.findOneById (currentId) map { user =>
          f(user)(request)
        } getOrElse NotFound (Json.obj("err" -> "User Not Found"))
      } else {
        Forbidden (Json.obj("err" -> "You don't have sufficient privileges to access this user"))
      }
    }

  def getUser(id: Long) = CanEditUser(id) { user => _ =>
    Ok(Json.toJson(user))
  }

}

object Users extends Users
