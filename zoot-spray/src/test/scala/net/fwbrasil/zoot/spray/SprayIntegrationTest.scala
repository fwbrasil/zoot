package net.fwbrasil.zoot.spray

import net.fwbrasil.zoot.core.Api
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import net.fwbrasil.zoot.core._
import net.fwbrasil.zoot.core.mapper.JacksonStringMapper
import java.net.InetSocketAddress
import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.duration._
import akka.actor.Props
import akka.io.IO
import spray.can.Http

trait TestApi extends Api {
    @endpoint(path = "/path")
    def someMethod(value: Int): Future[Int]
}

class TestService extends TestApi {
    def someMethod(value: Int) = Future(value + 1)
}

class SprayIntegrationTest extends Spec {

    implicit val mirror = scala.reflect.runtime.currentMirror
    implicit val mapper = new JacksonStringMapper

    val host = "localhost"
    val port = 9999

    implicit val system = ActorSystem("SomeSystem")
    implicit val timeout = Timeout(1000 millis)

    "works integrated" in {
        val client = Client[TestApi](SprayClient(host, port))
        val server = Server[TestApi](new TestService)
        val sprayActor = system.actorOf(Props(SprayServer(server)))
        IO(Http) ! Http.Bind(sprayActor, host, port)
        try await(client.someMethod(1)) shouldBe 2
        finally system.shutdown
    }
}