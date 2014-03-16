package net.fwbrasil.zoot.core.util

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.reflect.runtime.universe
import net.fwbrasil.zoot.core.Spec

class StubSpec extends Spec {

    implicit val mirror = scala.reflect.runtime.currentMirror

    "Stub" - {
        "should stub trait" - {
            "and call method without parameter" in {
                Stub[SomeTrait] {
                    (method, args) =>
                        method shouldBe someTraitMethod("method1")
                        args shouldBe Array[Object]()
                        Some(Future.successful({}))
                }.method1
            }
            "and call method with parameter" in {
                val a = "a"
                val future =
                    Stub[SomeTrait] {
                        (method, args) =>
                            method shouldBe someTraitMethod("method2")
                            args shouldBe Array[Object](a)
                            Some(Future.successful(a))
                    }.method2(a)
                await(future) shouldBe a
            }
        }
    }

    private def someTraitMethod(name: String) =
        classOf[SomeTrait].getMethods.find(_.getName == name).get

    private def await[R](future: Future[R]) =
        Await.result(future, Duration.Inf)
}

trait SomeTrait {
    def method1: Future[Unit]
    def method2(string: String): Future[String]
}