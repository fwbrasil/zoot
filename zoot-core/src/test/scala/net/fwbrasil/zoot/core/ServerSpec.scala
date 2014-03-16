package net.fwbrasil.zoot.core

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import net.fwbrasil.zoot.core.mapper.JacksonStringMapper
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.response.ExceptionResponse
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.response.ResponseStatus

class ServerSpec extends Spec {

    implicit val mirror = scala.reflect.runtime.currentMirror
    implicit val mapper = new JacksonStringMapper

    "Server" - {
        "should consume requests" - {
            "match" - {
                "static path" in {
                    val request = Request(RequestMethod.GET, "/endpoint1/")
                    server(
                        new TestApi {
                            override def endpoint1 = Future.successful("a")
                        }
                    )(request).map(await) shouldBe Some(Response(ResponseStatus.OK, "\"a\""))
                }
                "parametrized path" in {
                    val request = Request(RequestMethod.POST, "/21/endpoint2")
                    server(
                        new TestApi {
                            override def endpoint2(pathValue: Int) = Future.successful(pathValue)
                        }
                    )(request).map(await) shouldBe Some(Response(ResponseStatus.OK, "21"))
                }
                "use the last endpoint that matches" in {
                    val request = Request(RequestMethod.GET, "/endpoint3/")
                    server(
                        new TestApi {
                            override def endpoint3 = Future.successful("a")
                        }
                    )(request).map(await) shouldBe Some(Response(ResponseStatus.OK, "\"a\""))
                }
                "propagate non-ok response" in {
                    val status = ResponseStatus.BAD_REQUEST
                    val description = "Bad parameter"
                    val request = Request(RequestMethod.GET, "/endpoint3/")
                    server(
                        new TestApi {
                            override def endpoint3 = throw new ExceptionResponse(status, description)
                        }
                    )(request).map(await) shouldBe Some(ExceptionResponse(status, description))
                }
            }
            "mismatch" in {
                val request = Request(RequestMethod.POST, "/invalid/")
                server(new TestApi {})(request).map(await) shouldBe None
            }
        }
    }

    def server(instance: TestApi): Server[TestApi] =
        Server[TestApi](instance)

    trait TestApi extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint1/")
        def endpoint1: Future[String] = ???

        @endpoint(method = RequestMethod.POST, path = "/:pathValue/endpoint2")
        def endpoint2(pathValue: Int): Future[Int] = ???

        @endpoint(method = RequestMethod.GET, path = "/endpoint3/")
        def endpoint3(p: Int): Future[String] = ???

        @endpoint(method = RequestMethod.GET, path = "/endpoint3/")
        def endpoint3: Future[String] = ???
    }

    private def await[R](future: Future[R]) =
        Await.result(future, Duration.Inf)

}