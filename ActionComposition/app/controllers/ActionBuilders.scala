package controllers

import models._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.{successful => resolve}


/*
 * 2.) ActionBuilders
 * ~~~~
 * ActionBuilders come handy when you want to write reusable actions that require a parsed
 * body, i.e. a Request[A] instead of a RequestHeader.
 * They can be used for convenience, to reduce boilerplate, to unify responses, and so on.
 * Parsing the body is required for form submissions, JSON input, streaming input, uploading files,
 * and more.
 */


object ActionBuilders extends Controller {

 /**
  * Let's start with our easy example again that doesn't manipulate the request nor the result.
  */

  /** Prints the time elapsed for a wrapped action to execute */
  object TimeElapsed extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      val start = System.currentTimeMillis
      val res = block(request) // No need to handle the future, invokeBlock does it for us
      val totalTime = System.currentTimeMillis - start
      println("Elapsed time: %1d ms".format(totalTime))
      res
    }
  }

  /*
   * Don't get confused, we're not calling invokeBlock directly, but rather ActionBuilder.apply().
   * All ActionBuilder#apply methods call ActionBuilder#async, which in turn calls invokeBlock.
   */
  def short = TimeElapsed(parse.empty) { _ =>
    val res = for (i <- 0 until 100000) yield i
    Ok(res.mkString(", "))
  }

  /*
   * We can use ActionBuilder#async to handle Future[Result].
   */

  def shortAsync = TimeElapsed.async(parse.empty) { _ =>
    Future {
      val res = for (i <- 0 until 100000) yield i
      Ok(res.mkString(", "))
    }
  }

  /**
   * Now let's see how we can use ActionBuilders to reduce boilerplate, by simplify the mapping of
   * form input to case classes.
   */

  /** Adds the parsed object to the Request */
  case class DepartmentRequest[A](department: Department, request: Request[A]) extends WrappedRequest(request)

  /**
   * An Action that automatically binds a form with type Department, adds the parsed value to the
   * request if successful, or returning BadRequest otherwise
   */
  case class DepartmentFromForm(form: Form[Department]) extends ActionBuilder[DepartmentRequest] {
    def invokeBlock[A](request: Request[A], block: DepartmentRequest[A] => Future[Result]): Future[Result] = {
      form.bindFromRequest()(request).fold(
        err => resolve(BadRequest(err.errorsAsJson)),
        (department: Department) => block(DepartmentRequest(department, request))
      )
    }
  }

  val departmentForm = Form(
    mapping(
      "name" -> nonEmptyText
    )(Department.apply _)(Department.unapply _)
  )

  /**
   * Update the department for the given id.
   * Uses the `DepartmentFromForm` Action to automatically bind to the Department form.
   * Test with: curl -X PUT http://localhost:9000/ab/departments/1 -d "name=foo"
   */
  def updateDepartment(id: Long) = DepartmentFromForm(departmentForm) { request =>
    // ~ Department.update(department)
    Ok(s"Received ${request.department.name}\n")
  }

  def updateDepartmentAsync(id: Long) = DepartmentFromForm(departmentForm).async { request =>
    // ~ Department.update(department)
    resolve(Ok(s"Received ${request.department.name}\n"))
  }

}
