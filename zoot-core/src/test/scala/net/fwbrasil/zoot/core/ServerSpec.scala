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
                    val request = Request("/endpoint1/")
                    val a = await(
                        server(
                            new NotImplementedTestApi {
                                override def endpoint1 = Future.successful("a")
                            }
                        )(request)
                    ).body.toList shouldBe "a".getBytes.toList
                }
                "parametrized path" in {
                    val request = Request("/21/endpoint2", method = RequestMethod.POST)
                    await(
                        server(
                            new NotImplementedTestApi {
                                override def endpoint2(pathValue: Int) = Future.successful(pathValue)
                            }
                        )(request)
                    ).body.toList shouldBe "21".getBytes.toList
                }
                "use the last endpoint that matches" in {
                    val request = Request("/endpoint3/")
                    await(
                        server(
                            new NotImplementedTestApi {
                                override def endpoint3 = Future.successful("a")
                            }
                        )(request)
                    ).body.toList shouldBe "a".getBytes.toList
                }
                "propagate non-ok response" in {
                    val status = ResponseStatus.BAD_REQUEST
                    val description = "Bad parameter"
                    val request = Request("/endpoint3/")
                    val response = await(
                        server(
                            new NotImplementedTestApi {
                                override def endpoint3 = throw new ExceptionResponse(description, status)
                            }
                        )(request)
                    )
                    response.status shouldBe status
                    new String(response.body) shouldBe description
                }
                "return response byte array" in {
                    val request = Request("/endpoint5")
                    val response = Response("test".getBytes)
                    await(
                        server(
                            new NotImplementedTestApi {
                                override def endpoint5 = Future.successful(response)
                            }
                        )(request)
                    ) shouldBe response
                }
                "return not found for None" in {
                    val request = Request("/endpoint4")
                    await(
                        server(
                            new NotImplementedTestApi {
                                override def endpoint4 = Future.successful(None)
                            }
                        )(request)
                    ).status shouldBe ResponseStatus.NOT_FOUND
                }
                "return the value for Some" in {
                    val request = Request("/endpoint4")
                    await(
                        server(
                            new NotImplementedTestApi {
                                override def endpoint4 = Future.successful(Some(1))
                            }
                        )(request)
                    ).body.toList shouldBe "1".getBytes.toList
                }
            }
            "mismatch" in {
                val request = Request("/invalid/", method = RequestMethod.POST)
                await(
                    server(new NotImplementedTestApi {})(request)
                ).status shouldBe ResponseStatus.NOT_FOUND
            }
        }
    }

    def server(instance: TestApi): Server[TestApi] =
        Server[TestApi](instance)

    trait TestApi extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint1/")
        def endpoint1: Future[String]

        @endpoint(method = RequestMethod.POST, path = "/:pathValue/endpoint2")
        def endpoint2(pathValue: Int): Future[Int]

        @endpoint(method = RequestMethod.GET, path = "/endpoint3/")
        def endpoint3(p: Int): Future[String]

        @endpoint(method = RequestMethod.GET, path = "/endpoint3/")
        def endpoint3: Future[String]

        @endpoint(method = RequestMethod.GET, path = "/endpoint4")
        def endpoint4: Future[Option[Int]]
        
        @endpoint(method = RequestMethod.GET, path = "/endpoint5")
        def endpoint5: Future[Response[Array[Byte]]]
    }

    trait NotImplementedTestApi extends TestApi {
        def endpoint1: Future[String] = ???

        def endpoint2(pathValue: Int): Future[Int] = ???

        def endpoint3(p: Int): Future[String] = ???

        def endpoint3: Future[String] = ???

        def endpoint4: Future[Option[Int]] = ???
        
        def endpoint5: Future[Response[Array[Byte]]] = ???
    }
}