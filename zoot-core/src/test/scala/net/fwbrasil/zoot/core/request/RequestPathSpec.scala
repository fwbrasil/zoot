package net.fwbrasil.zoot.core.request

import net.fwbrasil.zoot.core.Spec

class RequestPathSpec extends Spec {

    "RequestPath" - {
        test("/some/path")
        test("/some/:param")
        test("")
        test("/")
        test("///")
        test("rootless")
        test("rootless/path")
        test(":rootless")
        test("/path/")
    }

    private def test(path: String) =
        path in {
            RequestPath(path).toString shouldBe path
        }
}