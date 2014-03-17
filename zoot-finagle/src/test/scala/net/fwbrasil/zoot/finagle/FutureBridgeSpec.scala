package net.fwbrasil.zoot.finagle

import net.fwbrasil.zoot.core.Spec
import com.twitter.util.{ Future => TwitterFuture }
import scala.concurrent.{ Future => ScalaFuture }
import scala.concurrent.ExecutionContext.Implicits.global

class FutureBridgeSpec extends Spec {

    import FutureBridge._

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