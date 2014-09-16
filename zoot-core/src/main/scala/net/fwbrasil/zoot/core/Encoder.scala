package net.fwbrasil.zoot.core

import scala.reflect.ClassTag
import scala.reflect.classTag

import net.fwbrasil.zoot.core.request.Request

abstract class Encoder[T: ClassTag] {
    
    val cls = classTag[T].runtimeClass.asInstanceOf[Class[T]]
    
    def encode(value: T, request: Request): Request
    def decode(request: Request): T
}
