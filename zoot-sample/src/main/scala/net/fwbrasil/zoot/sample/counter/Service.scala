package net.fwbrasil.zoot.sample.counter

import scala.concurrent.Future
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import scala.collection.JavaConversions._
import net.fwbrasil.zoot.core.response.ExceptionResponse
import scala.concurrent.ExecutionContext.Implicits.global
import net.fwbrasil.zoot.core.RequestAwareness

class CounterService extends CounterApi {

    private val counters = new ConcurrentHashMap[String, AtomicLong]

    def create(name: String) = Future {
        counters.put(name, new AtomicLong(0))
    }

    def value(name: String) = Future {
        counter(name).get
    }

    def increment(name: String) = Future {
        counter(name).incrementAndGet
    }

    def decrement(name: String) = Future {
        counter(name).decrementAndGet
    }

    private def counter(name: String) =
        counters.getOrElse(name, throw ExceptionResponse(body = s"Invalid counter $name"))
}

