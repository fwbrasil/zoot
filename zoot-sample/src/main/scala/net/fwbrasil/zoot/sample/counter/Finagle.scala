package net.fwbrasil.zoot.sample.counter

import net.fwbrasil.zoot.core.mapper.JacksonStringMapper
import java.net.InetSocketAddress
import com.twitter.finagle.builder.ServerBuilder
import net.fwbrasil.zoot.finagle.FinagleServer
import com.twitter.finagle.builder.ClientBuilder
import net.fwbrasil.zoot.core.Client
import com.twitter.finagle.http.Http
import net.fwbrasil.zoot.core.Server
import scala.concurrent.ExecutionContext.Implicits.global
import net.fwbrasil.zoot.finagle.FinagleClient
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.twitter.finagle.http.RichHttp
import com.twitter.finagle.http.Request

object FinagleMain extends App {

    private implicit val mirror = scala.reflect.runtime.currentMirror
    private implicit val mapper = new JacksonStringMapper
    private val port = 8888

    val client = {
        val clientBuilder = ClientBuilder()
            .codec(RichHttp[Request](Http()))
            .hosts(s"localhost:$port")
            .hostConnectionLimit(5)

        Client[CounterApi](FinagleClient(clientBuilder.build))
    }

    val server = {
        val serverBuilder =
            ServerBuilder()
                .codec(RichHttp[Request](Http()))
                .bindTo(new InetSocketAddress(port))
                .name("CounterServer")

        new FinagleServer(Server[CounterApi](new CounterService), serverBuilder.build)
    }

    val counterName = "someCounter"

    val future =
        for (
            _ <- client.create(counterName);
            value1 <- client.increment(counterName);
            value2 <- client.increment(counterName);
            value3 <- client.decrement(counterName)
        ) yield {
            println(value1, value2, value3)
        }

    Await.result(future, Duration.Inf)
}

