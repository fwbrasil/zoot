package net.fwbrasil.zoot.core

import scala.reflect._
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.smirror.SClass

abstract class Encoder[T: ClassTag] {
    
    val cls = classTag[T].runtimeClass.asInstanceOf[Class[T]]
    
    def encode(value: T, request: Request): Request
    def decode(request: Request): T
}
