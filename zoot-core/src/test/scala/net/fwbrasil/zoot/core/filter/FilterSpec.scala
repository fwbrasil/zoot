package net.fwbrasil.zoot.core.filter

import net.fwbrasil.zoot.core.Spec
import net.fwbrasil.zoot.core.request._
import net.fwbrasil.zoot.core.response._
import scala.concurrent.Future
import scala.collection.mutable.ListBuffer
import org.scalatest.OneInstancePerTest

class FilterSpec extends Spec with OneInstancePerTest {

    val execution = ListBuffer[String]()
    val response = Response(body = "test")

    val service = (i: Request) => Future.successful {
        execution += "service"
        response
    }

    def filter(name: String) =
        new Filter {
            override def apply(input: Request, next: Service) = {
                execution += s"before-filter-$name"
                val res = next(input)
                execution += s"after-filter-$name"
                res
            }
        }

    "one filter" in {
        val function = (filter("1") andThen service)
        await(function(Request(path = "/path"))) shouldBe response
        execution.toList shouldBe List("before-filter-1", "service", "after-filter-1")
    }
    
    "two filters" in {
        val function = (filter("1") andThen filter("2") andThen service)
        await(function(Request(path = "/path"))) shouldBe response
        execution.toList shouldBe List("before-filter-1", "before-filter-2", "service", "after-filter-2", "after-filter-1")
    }
}