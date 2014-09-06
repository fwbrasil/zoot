package net.fwbrasil.zoot.core.mapper

import scala.reflect.runtime.universe._
import scala.runtime.BoxedUnit

trait StringMapper {

    val contentType: String

    def fromString[T: TypeTag](string: String): T =
        string match {
            case _ if (typeOf[T] =:= typeOf[String]) =>
                string.asInstanceOf[T]
            case "" if (typeOf[T].<:<(typeOf[Unit])) =>
                {}.asInstanceOf[T]
            case other =>
                decode[T](string)
        }

    def toString(value: Any) =
        value match {
            case string: String =>
                value.asInstanceOf[String]
            case _: BoxedUnit =>
                ""
            case other =>
                encode(value)
        }

    def unescapeString(value: String): String
    protected def encode(value: Any): String
    protected def decode[T: TypeTag](value: String): T
}

