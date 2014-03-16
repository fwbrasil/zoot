package net.fwbrasil.zoot.core.util

import scala.reflect.runtime.universe._
import net.fwbrasil.zoot.core.Spec

class RichIterableSpec extends Spec {

    import RichIterable._

    "RichIterable" - {
        "findDefined" - {
            "should return the first defined value" in {
                val values = Map(1 -> None, 2 -> Some(2), 3 -> Some(3))
                val result = values.keys.findDefined(values(_))
                result shouldBe Some(2)
            }
            "should return None if all elements are None" in {
                val values = Map(1 -> None, 2 -> None)
                val result = values.keys.findDefined(values(_))
                result shouldBe None
            }
            "evaluate only until find a defined value" in {
                val values = Map(1 -> None, 2 -> Some(2))
                val result = (values.keys ++ List(3)).findDefined(values(_))
                result shouldBe Some(2)
            }
            "evaluate all values if there are only Nones" in {
                val values = Map(1 -> None, 2 -> None)
                intercept[NoSuchElementException] {
                    (values.keys ++ List(3)).findDefined(values(_))
                }
            }
        }

        "zipWith" - {
            "should zip values" in {
                val values = Map(1 -> "a", 2 -> "b", 3 -> "c")
                val result = values.keys.zipWith(values(_)).toMap
                result shouldBe values
            }
        }

        "groupByUnique" - {
            case class Test(s: String)
            "should group by unique values" in {
                val values = List(Test("a"), Test("b"))
                val grouped = values.groupByUnique(_.s)
                grouped shouldBe Map("a" -> Test("a"), "b" -> Test("b"))
            }
            "throw exception for non-unique values" in {
                val values = List(Test("a"), Test("a"))
                intercept[IllegalStateException] {
                    values.groupByUnique(_.s).toList
                }
            }
        }

        "ifNonEmpty" - {
            "should not execute the function if the iterable is empty" in {
                var executed = false
                List().ifNonEmpty(_ => executed = true)
                executed shouldBe false
            }
            "should execute the function if the iterable is non-empty" in {
                var executed = false
                List(1).ifNonEmpty(_ => executed = true)
                executed shouldBe true
            }
            "should propagate exception when executing the function" in
                intercept[IllegalStateException] {
                    List(1).ifNonEmpty(_ => throw new IllegalStateException)
                }
        }
    }
}