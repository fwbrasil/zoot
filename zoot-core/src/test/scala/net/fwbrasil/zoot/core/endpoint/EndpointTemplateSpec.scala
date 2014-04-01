package net.fwbrasil.zoot.core.endpoint

import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.Spec

class EndpointTemplateSpec extends Spec {

    "EndpointTemplate" - {
        test(
            pathTemplate = "/some/path",
            requestPath = "/some/path",
            expected = Some(Map()))

        test(
            pathTemplate = "/path/with/:param",
            requestPath = "/path/with/test",
            expected = Some(Map("param" -> "test")))

        test(
            pathTemplate = "/:two/:params",
            requestPath = "/one/two",
            expected = Some(Map("two" -> "one", "params" -> "two")))

        test(
            pathTemplate = "rootless/path",
            requestPath = "rootless/path",
            expected = Some(Map()))

        test(
            pathTemplate = ":root/param",
            requestPath = "value/param",
            expected = Some(Map("root" -> "value")))

        test(
            pathTemplate = "/:paramonly",
            requestPath = "/value",
            expected = Some(Map("paramonly" -> "value")))
    }

    private def test(pathTemplate: String, requestPath: String, expected: Option[Map[String, String]]) = {
        pathTemplate - {
            "successful parse" in {
                for (method <- RequestMethod.values) {
                    val template = EndpointTemplate(method, pathTemplate)
                    val request = Request(requestPath, method)
                    template.tryParse(request) shouldBe expected
                }
            }
            "wrong method" in {
                for (method <- RequestMethod.values) {
                    val otherMethod = RequestMethod.values.filter(_ != method).head
                    val template = EndpointTemplate(method, pathTemplate)
                    val request = Request(requestPath, otherMethod)
                    template.tryParse(request) shouldBe None
                }
            }
            "wrong request path" in {
                for (method <- RequestMethod.values) {
                    val template = EndpointTemplate(method, pathTemplate)
                    val request = Request("/some/other/path", method)
                    template.tryParse(request) shouldBe None
                }
            }
            "wrong method and request path" in {
                for (method <- RequestMethod.values) {
                    val otherMethod = RequestMethod.values.filter(_ != method).head
                    val template = EndpointTemplate(method, pathTemplate)
                    val request = Request("/some/other/path", otherMethod)
                    template.tryParse(request) shouldBe None
                }
            }
        }
    }
}