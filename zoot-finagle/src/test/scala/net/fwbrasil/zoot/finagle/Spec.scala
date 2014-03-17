package net.fwbrasil.zoot.finagle

import org.scalatest.Matchers
import org.scalatest.FreeSpec
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

trait Spec extends FreeSpec with Matchers {
    
    protected def await[R](future: Future[R]) =
        Await.result(future, Duration.Inf)
} 
