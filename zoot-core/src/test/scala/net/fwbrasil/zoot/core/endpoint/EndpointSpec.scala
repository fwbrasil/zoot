package net.fwbrasil.zoot.core.endpoint

import scala.reflect.runtime.universe._
import scala.concurrent.Future
import scala.reflect.runtime.universe
import net.fwbrasil.smirror.sClassOf
import net.fwbrasil.zoot.core.Api
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable
import net.fwbrasil.zoot.core.Spec
import net.fwbrasil.zoot.core.response.Response

class EndpointSpec extends Spec {

    implicit val mirror = scala.reflect.runtime.currentMirror

    "Endpoint" - {
        "listFor" - {
            "happy day" - {
                "with the commom case" - {
                    "should parse the endpoint annotations to templates" in {
                        Endpoint.listFor[TestApi1].map(_.template) shouldBe
                            List(EndpointTemplate(RequestMethod.GET, "/endpoint1"),
                                EndpointTemplate(RequestMethod.POST, "/endpoint2"),
                                EndpointTemplate(RequestMethod.DELETE, "/endpoint3"))
                    }
                    "should relect endpoints methods" in {
                        Endpoint.listFor[TestApi1].map(_.sMethod) shouldBe
                            List("endpoint1", "endpoint2", "endpoint3")
                            .map(name => sClassOf(classOf[TestApi1]).methods.find(_.name == name).get)
                    }
                }
                "with default param" - {

                    "should ignore the synthetic default param method" in {
                        val sMethod = sClassOf(classOf[TestApi2]).methods.filter(_.name == "endpoint").onlyOne
                        val endpoint = Endpoint.listFor[TestApi2].onlyOne
                        endpoint shouldBe
                            Endpoint(EndpointTemplate(RequestMethod.GET, "/endpoint"), sMethod)
                    }
                }
                "with reference to the outer instance" - {
                    "should ignore the synthetic $outer method" in {
                        val sMethod = sClassOf(classOf[TestApi3]).methods.filter(_.name == "endpoint").onlyOne
                        Endpoint.listFor[TestApi3].onlyOne shouldBe
                            Endpoint(EndpointTemplate(RequestMethod.GET, "/endpoint"), sMethod)
                    }
                }
            }
            "sorry day" - {
                "non future return" in {
                    val exception =
                        intercept[IllegalArgumentException] {
                            Endpoint.listFor[TestApi5]
                        }
                    exception.getMessage.contains("'endpoint' should return scala.concurrent.Future.") shouldBe true
                }
                "non abstract endpoint method" in {
                    intercept[IllegalArgumentException] {
                        Endpoint.listFor[TestApi6]
                    }
                }
                "abstract non-endpoint method" in {
                    intercept[IllegalArgumentException] {
                        Endpoint.listFor[TestApi7]
                    }
                }
            }
        }
        "payloadTypeTag" - {
            "boolean" in {
                val equals =
                    uniqueEndpoint[TestApi8].payloadTypeTag.tpe =:=
                        typeTag[Boolean].tpe
                equals shouldBe true
            }
            "option boolean" in {
                val equals =
                    uniqueEndpoint[TestApi9].payloadTypeTag.tpe =:=
                        typeTag[Option[Boolean]].tpe
                equals shouldBe true
            }
            "case class" in {
                uniqueEndpoint[TestApi10].payloadTypeTag shouldBe
                    typeTag[Test]
            }
            "option case class" in {
                val equals =
                    uniqueEndpoint[TestApi11].payloadTypeTag.tpe =:=
                        typeTag[Option[Test]].tpe
                equals shouldBe true
            }
        }
        "payloadIsResponse" - {
            "true" in {
                uniqueEndpoint[TestApi13].payloadIsResponse shouldBe true
            }
            "false" in {
                uniqueEndpoint[TestApi12].payloadIsResponse shouldBe false
            }
        }
        "payloadIsResponseString" - {
            "true" in {
                uniqueEndpoint[TestApi13].payloadIsResponseString shouldBe true
            }
            "false" - {
                "not response" in {
                    uniqueEndpoint[TestApi12].payloadIsResponse shouldBe false
                }
                "not response string" in {
                    uniqueEndpoint[TestApi14].payloadIsResponseString shouldBe false
                }
            }
        }
    }

    trait TestApi1 extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint1")
        def endpoint1: Future[Unit]

        @endpoint(method = RequestMethod.POST, path = "/endpoint2")
        def endpoint2(string: String): Future[String]

        @endpoint(method = RequestMethod.DELETE, path = "/endpoint3")
        def endpoint3(string: String, int: Int = 12): Future[(String, Int)]
    }

    trait TestApi2 extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint")
        def endpoint(a: String = "a"): Future[String]
    }

    trait TestApi3 extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint")
        def endpoint: Future[String]
    }

    trait TestApi4 extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint")
        def endpoint(a: String): Future[String]
        def evil = "evil"
    }

    trait TestApi5 extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint")
        def endpoint(a: String = "a"): String
    }

    trait TestApi6 extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint")
        def endpoint(a: String = "a") = Future.successful(a)
    }

    trait TestApi7 extends Api {
        @endpoint(method = RequestMethod.GET, path = "/endpoint")
        def endpoint(a: String = "a"): Future[String]

        def wrong: Boolean
    }

    trait TestApi8 extends Api {
        @endpoint(path = "/path")
        def goodendpoint: Future[Boolean]
    }

    trait TestApi9 extends Api {
        @endpoint(path = "/path")
        def goodendpoint: Future[Option[Boolean]]
    }

    trait TestApi10 extends Api {
        @endpoint(path = "/path")
        def goodendpoint: Future[Test]
    }

    trait TestApi11 extends Api {
        @endpoint(path = "/path")
        def goodendpoint: Future[Option[Test]]
    }

    trait TestApi12 extends Api {
        @endpoint(method = RequestMethod.POST, path = "/endpoint2/:pathParam/")
        def endpoint2(pathParam: Int, param: String): Future[(Int, String)]
    }

    trait TestApi13 extends Api {
        @endpoint(path = "/path")
        def goodendpoint: Future[Response[String]]
    }

    trait TestApi14 extends Api {
        @endpoint(path = "/path")
        def goodendpoint: Future[Response[Int]]
    }

    private def uniqueEndpoint[A <: Api: TypeTag] =
        Endpoint.listFor[A].onlyOne
}