package test

import controllers.EssentialActions
import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test._

/**

 */
class EssentialActionSpec extends Specification {

  "EssentialActions" should {

    "refuse request without a token" in new WithApplication {
      val request = FakeRequest(GET, "/...")
      // Must call `run` on an Iteratee to obtain the Future[Result]
      val response = EssentialActions.withToken()(request).run
      status(of = response) must equalTo (UNAUTHORIZED)
    }

    "accept a request with a token" in new WithApplication {
      val request = FakeRequest(GET, "/...").withHeaders("X-SECRET-TOKEN" -> "something")
      val response = EssentialActions.withToken()(request).run
      status(of = response) must equalTo (OK)
    }

  }
}
