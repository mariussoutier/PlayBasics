package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

object Users extends Controller {

  // CRUD

  def showAll(sortBy: String, sortDir: String, page: Int) = Action { implicit request =>
    Ok("Users sorted by %s, direction %s, page %d".format(sortBy, sortDir, page))
  }

  def show(id: Long) = Action {
    Ok("Showing user " + id)
  }

  def create = Action {
    Ok("created")
  }

  def update(id: Long) = Action {
    Ok("updated user " + id)
  }

  def delete(id: Long) = Action {
    Ok("deleted user " + id)
  }

  // RegEx

  def userImage(username: String, width: Int, height: Int) = Action {
    Ok("""<img width="%d" height="%d" title="User Image for %s" />""".format(width, height, username))
  }

  // Value types

  def setBoolPreference(key: String, value: Boolean) = Action {
    Ok("Prefence %s set to %s".format(key, value.toString))
  }

  def setLongPreference(key: String, value: Long) = Action {
    Ok("Prefence %s set to %s".format(key, value.toString))
  }

  def setIntPreference(key: String, value: Int) = Action {
    Ok("Prefence %s set to %s".format(key, value.toString))
  }

  def setDoublePreference(key: String, value: Double) = Action {
    Ok("Prefence %s set to %s".format(key, value.toString))
  }

  def setFloatPreference(key: String, value: Float) = Action {
    Ok("Prefence %s set to %s".format(key, value.toString))
  }

  def setUUIDPreference(key: String, value: java.util.UUID) = Action {
    Ok("Prefence %s set to %s".format(key, value.toString))
  }

}
