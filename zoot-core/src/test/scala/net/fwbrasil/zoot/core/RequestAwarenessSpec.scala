package net.fwbrasil.zoot.core

import scala.concurrent.Future
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.endpoint.Endpoint
import net.fwbrasil.zoot.core.endpoint.RequestConsumer
import net.fwbrasil.zoot.core.mapper.JacksonStringMapper
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class RequestAwarenessSpec extends Spec {

    implicit val mirror = scala.reflect.runtime.currentMirror
    implicit val mapper = new JacksonStringMapper

    trait TestApi extends Api {
        @endpoint(path = "/test1")
        def someAction: Future[Response[Request]]
        @endpoint(path = "/test2")
        def badAction: Future[Int]
    }

    class TestService extends TestApi with RequestAwareness {
        def someAction =
            withRequest { request =>
                Future.successful(Response(body = request))
            }
        def badAction =
            Future {
                withRequest { request =>
                    1
                }
            }
    }

    val server = Server[TestApi](new TestService)

    def responseFor(request: Request) =
        Await.result(server(request), Duration.Inf)

    "executes the action with the original request" in {
        val request = Request(path = "/test1")
        responseFor(request).body === mapper.encode(request)
    }

    "fail if withRequest is used in a future" in {
        intercept[IllegalStateException] {
            responseFor(Request(path = "/test2"))
        }
    }

}