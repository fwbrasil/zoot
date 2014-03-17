package net.fwbrasil.zoot.finagle

import com.twitter.util.{ Future => TwitterFuture }
import scala.concurrent.{ Future => ScalaFuture }
import scala.concurrent.ExecutionContext.Implicits.global

class FutureBridgeSpec extends Spec {

    import FutureBridge._

    "success" - {

        "twitterToScala" in {
            val twitter = TwitterFuture.value(1)
            val scala: ScalaFuture[Int] = twitter
            await(scala) shouldBe 1
        }

        "scalaToTwitter" in {
            val scala = ScalaFuture(1)
            val twitter: TwitterFuture[Int] = scala
            await(twitter) shouldBe 1
        }
    }

    "failure" - {

        "twitterToScala" in {
            val twitter = TwitterFuture.exception(new IllegalStateException)
            val scala: ScalaFuture[Int] = twitter
            intercept[IllegalStateException] {
                await(scala)
            }
        }

        "scalaToTwitter" in {
            val scala = ScalaFuture.failed(new IllegalStateException)
            val twitter: TwitterFuture[Int] = scala
            intercept[IllegalStateException] {
                await(twitter)
            }
        }
    }
}
    