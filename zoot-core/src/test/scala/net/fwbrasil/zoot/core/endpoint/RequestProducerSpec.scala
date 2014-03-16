package net.fwbrasil.zoot.core.endpoint

import scala.concurrent.Future
import scala.reflect.runtime.universe._
import net.fwbrasil.zoot.core.Api
import net.fwbrasil.zoot.core.mapper.JacksonStringMapper
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable
import net.fwbrasil.zoot.core.Spec
import net.fwbrasil.zoot.core.response.Response

class RequestProducerSpec extends Spec {

    implicit val mirror = scala.reflect.runtime.currentMirror

    "RequestProducer" - {
        "payloadTypeTag" - {
            "boolean" in {
                val equals =
                    uniqueEndpointProducer[TestApi1].payloadTypeTag.tpe =:=
                        typeTag[Boolean].tpe
                equals shouldBe true
            }
            "option boolean" in {
                val equals =
                    uniqueEndpointProducer[TestApi2].payloadTypeTag.tpe =:=
                        typeTag[Option[Boolean]].tpe
                equals shouldBe true
            }
            "case class" in {
                uniqueEndpointProducer[TestApi3].payloadTypeTag shouldBe
                    typeTag[Test]
            }
            "option case class" in {
                val equals =
                    uniqueEndpointProducer[TestApi4].payloadTypeTag.tpe =:=
                        typeTag[Option[Test]].tpe
                equals shouldBe true
            }
        }
        "payloadIsResponse" - {
            "true" in {
                uniqueEndpointProducer[TestApi8].payloadIsResponse shouldBe true
            }
            "false" in {
                uniqueEndpointProducer[TestApi7].payloadIsResponse shouldBe false
            }
        }
        "payloadIsResponseString" - {
            "true" in {
                uniqueEndpointProducer[TestApi8].payloadIsResponseString shouldBe true
            }
            "false" - {
                "not response" in {
                    uniqueEndpointProducer[TestApi7].payloadIsResponse shouldBe false
                }
                "not response string" in {
                    uniqueEndpointProducer[TestApi9].payloadIsResponseString shouldBe false
                }
            }
        }
        "javaMethod" in {
            uniqueEndpointProducer[TestApi5].javaMethod shouldBe
                classOf[TestApi5].getMethod("goodendpoint")
        }
        "produceRequest" - {
            val mapper = new JacksonStringMapper
            "fixed path" in {
                uniqueEndpointProducer[TestApi6]
                    .produceRequest(
                        args = List("a"),
                        mapper = mapper
                    ) shouldBe
                        Request(
                            method = RequestMethod.GET,
                            path = "/endpoint1",
                            params = Map("param" -> "a"),
                            headers = Map("Content-Type" -> mapper.contentType))
            }
            "path using param" in {
                uniqueEndpointProducer[TestApi7]
                    .produceRequest(
                        args = List("21", "a"),
                        mapper = mapper
                    ) shouldBe
                        Request(
                            method = RequestMethod.POST,
                            path = "/endpoint2/21/",
                            params = Map("pathParam" -> "21", "param" -> "a"),
                            headers = Map("Content-Type" -> mapper.contentType))
            }
        }
    }

    trait TestApi1 extends Api {
        @endpoint(path = "/path")
        def goodendpoint = Future.successful(true)
    }

    trait TestApi2 extends Api {
        @endpoint(path = "/path")
        def goodendpoint = Future.successful(Option(true))
    }

    trait TestApi3 extends Api {
        @endpoint(path = "/path")
        def goodendpoint = Future.successful(Test("a"))
    }

    trait TestApi4 extends Api {
        @endpoint(path = "/path")
        def goodendpoint = Future.successful(Option(Test("a")))
    }

    trait TestApi5 extends Api {
        @endpoint(path = "/path")
        def goodendpoint = Future.successful(Test("a"))
    }

    trait TestApi6 extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint1")
        def endpoint1(param: String) = Future.successful(param)
    }

    trait TestApi7 extends Api {
        @endpoint(method = RequestMethod.POST, path = "/endpoint2/:pathParam/")
        def endpoint2(pathParam: Int, param: String) = Future.successful((pathParam, param))
    }

    trait TestApi8 extends Api {
        @endpoint(path = "/path")
        def goodendpoint = Future.successful(Response())
    }

    trait TestApi9 extends Api {
        @endpoint(path = "/path")
        def goodendpoint = Future.successful(Response(body = 1))
    }

    private def uniqueEndpointProducer[A <: Api: TypeTag] =
        RequestProducer(uniqueEndpoint[A])

    private def uniqueEndpoint[A <: Api: TypeTag] =
        Endpoint.listFor[A].onlyOne
}