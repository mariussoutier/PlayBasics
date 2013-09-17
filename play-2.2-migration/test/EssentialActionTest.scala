package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import controllers._

/**

 */
class EssentialActionSpec extends Specification {

  "EssentialActions" should {

    "refuse request without a token" in new WithApplication {
      val request = FakeRequest(GET, "/...")
      // Must call `run` on an Iteratee to obtain the Future[SimpleResult]
      val response = EssentialActionsExamples.withToken()(request).run
      status(of = response) must equalTo (UNAUTHORIZED)
    }

    "accept a request with a token" in new WithApplication {
      val request = FakeRequest(GET, "/...").withHeaders("X-SECRET-TOKEN" -> "something")
      val response = EssentialActionsExamples.withToken()(request).run
      status(of = response) must equalTo (OK)
    }



  }
}
