package net.fwbrasil.zoot.core.endpoint

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.reflect.runtime.universe._
import net.fwbrasil.zoot.core.Api
import net.fwbrasil.zoot.core.mapper.JacksonStringMapper
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable
import net.fwbrasil.zoot.core.Spec
import net.fwbrasil.zoot.core.response.ExceptionResponse
import net.fwbrasil.zoot.core.response.ExceptionResponse
import net.fwbrasil.zoot.core.Encoder

class RequestConsumerSpec extends Spec {

    implicit val mirror = scala.reflect.runtime.currentMirror

    "RequestConsumer" - {
        "consumeRequest" - {
            "mismatch" - {
                "wrong path" in {
                    consumeRequest(
                        endpointName = "endpoint1",
                        method = RequestMethod.GET,
                        path = "/invalid"
                    ) shouldBe None
                }
                "wrong method" in {
                    consumeRequest(
                        endpointName = "endpoint2",
                        method = RequestMethod.PUT
                    ) shouldBe None
                }
                "wrong method and path" in {
                    consumeRequest(
                        endpointName = "endpoint3",
                        method = RequestMethod.GET,
                        path = "/invalid"
                    ) shouldBe None
                }
                "missing path param" in {
                    consumeRequest(
                        endpointName = "endpoint7",
                        method = RequestMethod.POST
                    ) shouldBe None
                }
            }
            "match" - {
                "valid parameters" - {
                    "no parameters" in {
                        consumeRequest(
                            endpointName = "endpoint1",
                            method = RequestMethod.GET
                        ) shouldBe Some({})
                    }
                    "empty parameters list" in {
                        consumeRequest(
                            endpointName = "endpoint2",
                            method = RequestMethod.DELETE
                        ) shouldBe Some("a")
                    }
                    "primitive parameters" in {
                        consumeRequest(
                            endpointName = "endpoint3",
                            method = RequestMethod.POST,
                            params = Map("int" -> "3", "bool" -> "false", "float" -> "1.2")
                        ) shouldBe Some((3, false, 1.2f))
                    }
                    "civilized parameter" in {
                        consumeRequest(
                            endpointName = "endpoint4",
                            method = RequestMethod.PUT,
                            params = Map("test" -> """{ "a": "b" }""")
                        ) shouldBe Some(Test("b"))
                    }
                    "default parameter" - {
                        "skip the default value" in {
                            consumeRequest(
                                endpointName = "endpoint5",
                                method = RequestMethod.GET,
                                params = Map("string" -> "someString", "int" -> "321")
                            ) shouldBe Some(("someString", 321))
                        }
                        "use the default value" in {
                            consumeRequest(
                                endpointName = "endpoint5",
                                method = RequestMethod.GET,
                                params = Map("string" -> "someString")
                            ) shouldBe Some(("someString", 12))
                        }
                    }
                    "optional parameter" - {
                        "not specified" in {
                            consumeRequest(
                                endpointName = "endpoint6",
                                method = RequestMethod.POST
                            ) shouldBe Some(21)
                        }
                        "specified" in {
                            consumeRequest(
                                endpointName = "endpoint6",
                                method = RequestMethod.POST,
                                params = Map("optional" -> "23")
                            ) shouldBe Some(23)
                        }
                    }
                    "path parameter" in {
                        consumeRequest(
                            endpointName = "endpoint7",
                            method = RequestMethod.POST,
                            path = "/endpoint7/someValue"
                        ) shouldBe Some("someValue")
                    }
                    "custom encoder" in {
                        consumeRequest(
                            endpointName = "endpoint8",
                            method = RequestMethod.POST,
                            path = "/endpoint8",
                            params = Map("param" -> "paramValue"),
                            headers = Map("header" -> "headerValue"),
                            encoders = List(new SessionEncoder)
                        ) shouldBe Some(Session("headerValue", "paramValue"))
                    }
                }
                "invalid parameters" - {
                    "missing" in {
                        val exception =
                            intercept[ExceptionResponse] {
                                consumeRequest(
                                    endpointName = "endpoint4",
                                    method = RequestMethod.PUT
                                ) shouldBe Some(Test("b"))
                            }
                        exception.body.contains("Missing parameters List(test: net.fwbrasil.zoot.core.endpoint.Test)")
                    }
                    "wrong value" - {
                        "primitive" in {
                            val exception =
                                intercept[ExceptionResponse] {
                                    consumeRequest(
                                        endpointName = "endpoint5",
                                        method = RequestMethod.GET,
                                        params = Map("string" -> "\"someString\"", "int" -> "notanumber")
                                    )
                                }
                            exception.body.contains("Invalid value notanumber")
                        }
                        "civilized" in {
                            val exception =
                                intercept[ExceptionResponse] {
                                    consumeRequest(
                                        endpointName = "endpoint4",
                                        method = RequestMethod.PUT,
                                        params = Map("test" -> "\"{ }\"")
                                    )
                                }
                            exception.body.contains("Invalid value { }")
                        }
                    }
                }
            }
        }
    }

    trait TestApi extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint1")
        def endpoint1: Future[Unit]

        @endpoint(method = RequestMethod.DELETE, path = "/endpoint2")
        def endpoint2(): Future[String]

        @endpoint(method = RequestMethod.POST, path = "/endpoint3")
        def endpoint3(int: Int, bool: Boolean, float: Float): Future[(Int, Boolean, Float)]

        @endpoint(method = RequestMethod.PUT, path = "/endpoint4")
        def endpoint4(test: Test): Future[Test]

        @endpoint(method = RequestMethod.GET, path = "/endpoint5")
        def endpoint5(string: String, int: Int = 12): Future[(String, Int)]

        @endpoint(method = RequestMethod.POST, path = "/endpoint6")
        def endpoint6(optional: Option[Int]): Future[Int]

        @endpoint(method = RequestMethod.POST, path = "/endpoint7/:pathParam")
        def endpoint7(pathParam: String): Future[String]
        
        @endpoint(method = RequestMethod.POST, path = "/endpoint8")
        def endpoint8(session: Session): Future[Session]
    }

    def subject = new TestApi {

        def endpoint1 = Future.successful()

        def endpoint2() = Future.successful("a")

        def endpoint3(int: Int, bool: Boolean, float: Float) = Future.successful((int, bool, float))

        def endpoint4(test: Test) = Future.successful(test)

        def endpoint5(string: String, int: Int = 12) = Future.successful((string, int))

        def endpoint6(optional: Option[Int]) = Future.successful(optional.getOrElse(21))

        def endpoint7(pathParam: String) = Future.successful(pathParam)
        
        def endpoint8(session: Session) = Future.successful(session)
    }

    def consumeRequest(endpointName: String, method: String, path: String, params: Map[String, String] = Map(), headers: Map[String, String] = Map(), encoders: List[Encoder[_]] = List()): Option[Any] =
        Endpoint.listFor[TestApi]
            .find(_.sMethod.name == endpointName).map(RequestConsumer(_, encoders.asInstanceOf[List[Encoder[Any]]])).get
            .consumeRequest(Request(path, method, params, headers), subject, new JacksonStringMapper)
            .map(await)

    def consumeRequest(endpointName: String, method: String, params: Map[String, String]): Option[Any] =
        consumeRequest(endpointName, method, "/" + endpointName, params)

    def consumeRequest(endpointName: String, method: String): Option[Any] =
        consumeRequest(endpointName, method, "/" + endpointName, Map())

    private def uniqueEndpointConsumer[A <: Api: TypeTag] =
        RequestConsumer(uniqueEndpoint[A], List())

    private def uniqueEndpoint[A <: Api: TypeTag] =
        Endpoint.listFor[A].onlyOne
}