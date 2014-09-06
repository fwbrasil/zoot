package net.fwbrasil.zoot.core.mapper

import scala.reflect.runtime.universe._
import net.fwbrasil.zoot.core.Spec

trait StringMapperSpec extends Spec {

    def mapper: StringMapper

    private def mapperName = mapper.getClass.getSimpleName

    s"The $mapperName should support" - {
        "Unit" in
            testValue({})
        "String" in
            testValue("value")
        "Tuple" in
            testValue(("a", 1))
        "Map" in
            testValue(Map("a" -> 1))
        "Seq" in
            testValue(Seq(1, 2, 3))
        "Set" in
            testValue(Seq("a", "b"))
        "Option" in
            testValue(Option(1))
        "Case class" in
            testValue(Simple(31))
        "Nested case class" in
            testValue(Nested("a", Simple(32)))
        "Complext type" in
            testValue(Option(Map("a" -> Option(Set(Nested("a", Simple(1)))))))
        "Extra field" in {
            val decoded = mapper.fromString[Simple]("""{"i": 1, "extra": 2}""")
            decoded.i shouldBe 1
        }
        "Unescape string" in {
            val string = "someString"
            mapper.toString(string) shouldBe string
        }
    }

    private def testValue[T: TypeTag](value: T) = {
        val encoded = mapper.toString(value)
        val decoded = mapper.fromString[T](encoded)
        decoded shouldBe value
    }
}

case class Simple(i: Int)
case class Nested(a: String, s: Simple)