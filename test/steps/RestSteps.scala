package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import play.api.libs.json.Json
import scalaj.http.Http

import scala.annotation.tailrec

class RestSteps extends ScalaDsl with EN with Matchers with World {

  And("""^a request is made to (.*)""") { endpoint: String =>
    response = Http(s"$host$endpoint")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)
      .asString
  }

  When("""^I attempt validation at endpoint (.*)$""") { endpoint: String =>
    response = Http(s"$host$endpoint")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)
      .asString
  }

  And("""^a (\d+) status code is received$""") { status: Int =>
    response.code shouldBe status
  }

  And("""^the payload has a "(.*)" of "(.*)"$""") { (key: String, value: String) =>
    val json = Json.parse(response.body)
    val status = (json \ key).as[String]
    status shouldBe value
  }

  And("""^the response body is "(.*)"$""") { body: String =>
    response.body shouldBe body
  }

  And("""^the response body is$""") { body: String =>
    strip(response.body) shouldBe body.stripMargin
  }

  And("""^the rendered text is:""") { expectedBody: String =>
    response.body shouldBe expectedBody.stripMargin
  }

  private def strip(s: String): String = s.split("\\n").map(trimEnd).mkString("\n")

  @tailrec
  private def trimEnd(s: String): String = s.takeRight(1) match {
    case " " => trimEnd(s.dropRight(1))
    case _ => s
  }
}
