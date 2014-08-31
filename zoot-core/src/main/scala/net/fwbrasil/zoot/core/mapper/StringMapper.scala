package net.fwbrasil.zoot.core.mapper

import scala.reflect.runtime.universe._
import scala.runtime.BoxedUnit

trait StringMapper {

    val contentType: String

    def fromString[T: TypeTag](string: String): T = 
        string match {
            case "" if (typeOf[T].<:<(typeOf[Unit])) =>
                {}.asInstanceOf[T]
            case other =>
                try decode[T](string)
                catch {
                    case e: Exception if (typeOf[T] =:= typeOf[String]) =>
                        string.asInstanceOf[T]
                }
        }

    def toString(value: Any) =
        value match {
            case _: BoxedUnit =>
                ""
            case other =>
                encode(value)
        }

    def unescapeString(value: String): String
    protected def encode(value: Any): String
    protected def decode[T: TypeTag](value: String): T
}

