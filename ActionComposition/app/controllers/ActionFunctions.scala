package controllers

import play.api.mvc._

import scala.concurrent.Future


class ActionFunctions extends Controller {

  case class TimedRequest[A](request: Request[A], startTime: Long = System.currentTimeMillis()) {
    def timeElapsed = System.currentTimeMillis - startTime

    def printTimeElapsed(): Unit = println("Elapsed time: %1d ms".format(timeElapsed))
  }

  case class Container[A](request: TimedRequest[A], additionalInfo: String)

  trait FetchTransformer extends ActionTransformer[TimedRequest, Container] {
    override protected def transform[A](request: TimedRequest[A]): Future[Container[A]] =
      Future.successful(Container(request, "Hi"))
  }

  case class ParsedRequest[A](request: Request[A], userName: String, token: String)

  // Transform a request, however no way of aborting if we can't make sense of it
  object ParsedNaive extends ActionTransformer[Request, ParsedRequest] {
    override protected def transform[A](request: Request[A]): Future[ParsedRequest[A]] = {
      val user = request.headers.get("user").getOrElse("???")
      val token = request.headers.get("token").getOrElse("???")
      Future.successful(ParsedRequest(request, user, token))
    }
  }

  // Filter allows us to do abort processing when some conditions are met; in this case a bit clumsy
  object CheckHeader extends ActionFilter[ParsedRequest] {
    override protected def filter[A](request: ParsedRequest[A]): Future[Option[Result]] = {
      if (request.userName == "???") {
        Future.successful(Some(Unauthorized))
      } else if (request.token == "???") {
        Future.successful(Some(Unauthorized))
      }
      Future.successful(None)
    }
  }

  // We can do both at once using an ActionRefiner
//  object Parsed extends ActionRefiner[Request, ParsedRequest] {
//    override protected def refine[A](request: Request[A]): Future[Either[Result, ParsedRequest[A]]] = {
//      val maybeParsedRequest = for {
//        user <- request.headers.get("user")
//        token <- request.headers.get("token")
//      } yield Future.successful(ParsedRequest(request, user, token))
//      maybeParsedRequest.map(Right(_)).getOrElse(Left(Unauthorized))
//    }
//  }

  /*override def invokeBlock[A](request: TimedRequest[A], block: (Container[A]) => Future[Result]): Future[Result] = {
    block(Container(request, "Hi"))
  }*/
  //  val x = Timed.andThen(FetchAction)

  //  def fetchData = FetchAction { request =>
  //    Ok
  //  }

}
