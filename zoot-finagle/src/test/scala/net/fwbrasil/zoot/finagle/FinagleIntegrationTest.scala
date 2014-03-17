package net.fwbrasil.zoot.finagle

import net.fwbrasil.zoot.core.Api
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.Http
import com.twitter.util.TimeConversions._
import net.fwbrasil.zoot.core._
import net.fwbrasil.zoot.core.mapper.JacksonStringMapper
import com.twitter.finagle.builder.ServerBuilder
import java.net.InetSocketAddress

trait TestApi extends Api {
    @endpoint(path = "/path")
    def someMethod(value: Int): Future[Int]
}

class TestService extends TestApi {
    def someMethod(value: Int) = Future(value + 1)
}

class FinagleIntegrationTest extends Spec {

    implicit val mirror = scala.reflect.runtime.currentMirror
    implicit val mapper = new JacksonStringMapper

    val host = "localhost"
    val port = 9999

    val clientBuilder =
        ClientBuilder()
            .codec(Http())
            .hosts(s"$host:$port")
            .hostConnectionLimit(10)
            .requestTimeout(1000 millis)

    val serverBuilder =
        ServerBuilder()
            .codec(Http())
            .bindTo(new InetSocketAddress(port))
            .keepAlive(true)
            .name("SomeServer")

    "works integrated" in {
        val client = Client[TestApi](FinagleClient(clientBuilder.build))
        val server = Server[TestApi](new TestService)
        val finagleServer = FinagleServer(server, serverBuilder.build)
        try await(client.someMethod(1)) shouldBe 2
        finally finagleServer.close
    }

}