package controllers

import play.api._
import play.api.mvc._

// 2. Iteratees process chunks of data
object Iteratees extends Controller {

  def index = Action {
    Ok
  }

  // Iteratees are fed data from Enumerators

  // Enumeratees transform Iteratees and Enumerators


}
