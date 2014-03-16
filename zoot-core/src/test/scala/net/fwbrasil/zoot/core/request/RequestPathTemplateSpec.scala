package net.fwbrasil.zoot.core.request

import net.fwbrasil.zoot.core.Spec

class RequestPathTemplateSpec extends Spec {

    "RequestPathTemplate" - {
        test(
            path = "/some/path",
            request = "/some/path",
            expected = Some(Map()))

        test(
            path = "/path/with/:param",
            request = "/path/with/test",
            expected = Some(Map("param" -> "test")))

        test(
            path = "/:two/:params",
            request = "/one/two",
            expected = Some(Map("two" -> "one", "params" -> "two")))

        test(
            path = "rootless/path",
            request = "rootless/path",
            expected = Some(Map()),
            invalidRequest = "/rootless/path")

        test(
            path = ":root/param",
            request = "value/param",
            expected = Some(Map("root" -> "value")))

        test(
            path = "/:paramonly",
            request = "/value",
            expected = Some(Map("paramonly" -> "value")))
    }

    private def test(path: String,
                     request: String,
                     expected: Option[Map[String, String]],
                     invalidRequest: String = "/some/invalid/path") = {
        path - {
            val template = this.template(path)
            "tryParse valid" in {
                template
                    .tryParse(request)
                    .shouldBe(expected)
            }
            "tryParse invalid" in {
                template
                    .tryParse(invalidRequest)
                    .shouldBe(None)
            }
            "forParameters valid" in {
                val params = expected.getOrElse(Map())
                val parsed = template.forParameters(params(_))
                parsed.toString shouldBe request
            }
            "forParameters invalid" in
                expected.filter(_.nonEmpty).map { _ =>
                    val invalid = Map("invalid" -> "params")
                    intercept[NoSuchElementException] {
                        template.forParameters(invalid(_))
                    }
                }
            "forParameters extra param" in {
                val params = expected.getOrElse(Map()) ++ Map("some" -> "value")
                val parsed = template.forParameters(params(_))
                parsed.toString shouldBe request
            }
        }
    }

    private implicit def stringToPath(string: String): RequestPath =
        RequestPath(string)

    private def template(path: String) =
        RequestPathTemplate(RequestPath(path))
}