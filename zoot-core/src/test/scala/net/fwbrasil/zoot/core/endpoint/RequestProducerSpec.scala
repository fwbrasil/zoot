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
import net.fwbrasil.zoot.core.Encoder
import scala.reflect.ClassTag

class RequestProducerSpec extends Spec {

    implicit val mirror = scala.reflect.runtime.currentMirror

    "RequestProducer" - {
        "javaMethod" in {
            uniqueEndpointProducer[TestApi5]().javaMethod shouldBe
                classOf[TestApi5].getMethod("goodendpoint")
        }
        "produceRequest" - {
            val mapper = new JacksonStringMapper
            "fixed path" in {
                uniqueEndpointProducer[TestApi6]()
                    .produceRequest(
                        args = List("a"),
                        mapper = mapper
                    ) shouldBe
                        Request(
                            method = RequestMethod.GET,
                            path = "/endpoint1",
                            params = Map("param" -> "a"),
                            headers =
                                Map("Content-Type" -> mapper.contentType,
                                    "Host" -> "undefined"))
            }
            "path using param" in {
                uniqueEndpointProducer[TestApi7]()
                    .produceRequest(
                        args = List("21", "a"),
                        mapper = mapper
                    ) shouldBe
                        Request(
                            method = RequestMethod.POST,
                            path = "/endpoint2/21/",
                            params = Map("pathParam" -> "21", "param" -> "a"),
                            headers =
                                Map("Content-Type" -> mapper.contentType,
                                    "Host" -> "undefined"))
            }
            "adds the specified host header" in {
                val host = "host.com"
                uniqueEndpointProducer[TestApi6](Some(host))
                    .produceRequest(
                        args = List("a"),
                        mapper = mapper
                    ) shouldBe
                        Request(
                            method = RequestMethod.GET,
                            path = "/endpoint1",
                            params = Map("param" -> "a"),
                            headers = Map("Content-Type" -> mapper.contentType, "Host" -> host))
            }
            "supports encoders" in {
                val header = "headerValue"
                val param = "paramValue"
                val request =
                    uniqueEndpointProducer[TestApi10](encoders = List(new SessionEncoder))
                        .produceRequest(
                            args = List(Session(header, param)),
                            mapper = mapper
                        )
                request.headers should contain("header" -> header)
                request.params should contain("param" -> param)
            }
        }
    }

    trait TestApi5 extends Api {
        @endpoint(path = "/path")
        def goodendpoint: Future[Test]
    }

    trait TestApi6 extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint1")
        def endpoint1(param: String): Future[String]
    }

    trait TestApi7 extends Api {
        @endpoint(method = RequestMethod.POST, path = "/endpoint2/:pathParam/")
        def endpoint2(pathParam: Int, param: String): Future[(Int, String)]
    }

    trait TestApi8 extends Api {
        @endpoint(path = "/path")
        def goodendpoint: Future[Response[String]]
    }

    trait TestApi9 extends Api {
        @endpoint(path = "/path")
        def goodendpoint: Future[Response[Int]]
    }

    trait TestApi10 extends Api {
        @endpoint(path = "/path")
        def endpoint(value: Session): Future[Response[Int]]
    }

    private def uniqueEndpointProducer[A <: Api: ClassTag](hostHeader: Option[String] = None, encoders: List[Encoder[_]] = List()) =
        RequestProducer(uniqueEndpoint[A], hostHeader, encoders.asInstanceOf[List[Encoder[Any]]])

    private def uniqueEndpoint[A <: Api: ClassTag] =
        Endpoint.listFor[A].onlyOne
}