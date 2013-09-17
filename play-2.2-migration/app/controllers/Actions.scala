package controllers

import scala.concurrent.Future
// Import the method statically and rename it
import scala.concurrent.Future.{successful => resolve}
import scala.concurrent.ExecutionContext.Implicits.global

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.validation.ValidationError
import play.api.data.Forms._
import play.api.data.format.Formats._

import models._

/*
 * 3.) Actions
 * ~~~~
 * ActionsBuilders are only helpers to ultimately build Actions. In some cases, they are too simple
 * so you have to write an Action directly.
 */

/*
 * When you are writing a Single Page Application, you'll deal with a lot of JSON input and output.
 * Let's try to reduce the amount of boilerplate as much as possible.
 */

// JSON
import play.api.libs.json._
import models.json._
// We need a ClassTag to avoid losing the type to erasure
import scala.reflect.ClassTag

object JsonActions extends Controller {

 /** Helper that maps JsError errors to a JSON object */
  private def errToJson(errors: Seq[(JsPath, Seq[ValidationError])]): JsValue = {
    val jsonErrors: Seq[(String, JsValue)] = errors map {
      case (path, errs) => path.toJsonString -> Json.toJson(errs.map(_.message))
    }
    JsObject(jsonErrors)
  }

  /** This is an example for a simple wrapper function that only returns a SimpleResult */
  def WithJson[T](json: JsValue)(action: T => Future[SimpleResult])
     (implicit reads: Reads[T], classTag: ClassTag[T]): Future[SimpleResult] =
    json.validate[T] match {
      case JsSuccess(value, _) => action(value)
      case JsError(err) => resolve(BadRequest(Json.obj("err" -> errToJson(err))))
  }

 /**
  * Alternatively, integrate it as an Action. An Action is extremely well suited in this case
  * because we know already that we need a JSON body parser.
  * This makes the action harder to compose on the other hand.
  */
  object MyAction {
    def json[T](action: T => Future[SimpleResult])(implicit reads: Reads[T], classTag: ClassTag[T]): Action[JsValue] =
      new Action[JsValue] {
        def apply(request: Request[JsValue]): Future[SimpleResult] = WithJson(request.body)(action)

        def parser: BodyParser[JsValue] = parse.json
     }
  }


  case class UserSearchFilter(name: Option[String], department: Option[String])

  implicit val format: Format[UserSearchFilter] = Json.format[UserSearchFilter]

  /*
   * Combine Action with convenience method
   * curl -H "Content-type: application/json" -X POST -d '{"name":"John"}' http://localhost:9000/a/users/search/1
   */
  def searchForUsersJson() = Action.async(parse.json) { parsedRequest =>
    WithJson[UserSearchFilter](parsedRequest.body) { searchFilter =>
      // ... do something with searchFilter ..
      resolve(Ok(Json.obj("found" -> 1, "name" -> searchFilter.name)))
    }
  }

  /*
   * Use convenience action
   * curl -H "Content-type: application/json" -X POST -d '{"name":"John"}' http://localhost:9000/a/users/search/2
   */
  def searchForUsersJsonAction() = MyAction.json[UserSearchFilter] { searchFilter =>
    // ... do something with searchFilter ..
    resolve(Ok(Json.obj("found" -> 1, "name" -> searchFilter.name)))
  }

}


/*
 * The form handling code from the ActionBuilders example would have to be duplicated for every
 * value we wanted to wrap. Let's try to build a generic version.
 */

object FormActions extends Controller {

  /** This request takes any value, based on the form's type, and wraps it */
  case class FormRequest[T,A](formValue: T, request: Request[A]) extends WrappedRequest(request)

  /**
   * ActionBuilder is specifically typed to a Request with one type parameter. In Scala you can solve
   * this with a structural type à la [({ type R[A] = FormRequest[T,A] })#R].
   * A more readable approach is to write a custom Action directly.
   */
  case class FormAction[T,A](form: Form[T], bodyParser: BodyParser[A] = parse.anyContent)(block: FormRequest[T,A] => Future[SimpleResult])
    extends Action[A] {
      def apply(request: Request[A]): Future[SimpleResult] = {
        form.bindFromRequest()(request).fold(
          err => resolve(BadRequest(err.errorsAsJson)),
          (parsedObject: T) => block(FormRequest(parsedObject, request))
        )
      }
      def parser = bodyParser
   }

  case class UserSearchFilter(name: Option[String], department: Option[String])

  val userSearchFilterForm: Form[UserSearchFilter] = Form(
    mapping(
      "name" -> optional(text),
      "department" -> optional(text)
    )(UserSearchFilter.apply _)(UserSearchFilter.unapply _)
  )

  /*
   * Just pass the form to the action and the request will contain the bound form.
   * curl -X POST -d "name=John" http://localhost:9000/a/users/search/3
   */
  def searchForUsers() = FormAction(userSearchFilterForm) { request =>
    // ... do something with searchFilter ..
    resolve(Ok(Json.obj("found" -> 1, "name" -> request.formValue.name)))
  }

  /*
   * Also works when parsing binding the form to a JSON body.
   * curl -H "Content-type: application/json" -X POST -d ' {"name":"John"}' http://localhost:9000/a/users/search/4
   */
  def searchForUsersJson() = FormAction(userSearchFilterForm, parse.json) { request =>
    resolve(Ok(Json.obj("found" -> 1, "name" -> request.formValue.name)))
  }

}
