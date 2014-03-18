package net.fwbrasil.zoot.sample.counter

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.util.Timeout
import net.fwbrasil.zoot.core.Client
import net.fwbrasil.zoot.core.Server
import net.fwbrasil.zoot.core.mapper.JacksonStringMapper
import net.fwbrasil.zoot.spray.SprayClient
import net.fwbrasil.zoot.spray.SprayServer
import akka.io.IO
import akka.pattern.ask
import spray.can.Http

object SprayMain extends App {

    private implicit val mirror = scala.reflect.runtime.currentMirror
    private implicit val mapper = new JacksonStringMapper
    private implicit val system = ActorSystem("BansuriSystem")
    private implicit val timeout = Timeout(1000 millis)
    private val port = 8888

    val client =
        Client[CounterApi](SprayClient("localhost", port))

    val server = {
        val server = Server[CounterApi](new CounterService)
        val handler = system.actorOf(Props(new SprayServer(server)))
        val bindFuture = IO(Http) ? Http.Bind(handler, interface = "localhost", port = port)
        Await.result(bindFuture, Duration.Inf)
        handler
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

