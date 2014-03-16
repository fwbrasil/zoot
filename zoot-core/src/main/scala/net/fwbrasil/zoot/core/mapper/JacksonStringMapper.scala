package net.fwbrasil.zoot.core.mapper

import java.lang.reflect.ParameterizedType
import java.lang.reflect.{Type => JType}
import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConversions.mapAsScalaConcurrentMap
import scala.reflect.runtime.universe._

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class JacksonStringMapper(implicit mirror: Mirror) extends StringMapper {

    val contentType = "application/json"

    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

    def decode[T: TypeTag](string: String): T =
        mapper.readValue(string, typeReference[T])

    def encode(value: Any) =
        mapper.writeValueAsString(value)

    private val cache = new ConcurrentHashMap[TypeTag[_], TypeReference[_]]

    private def typeReference[T](implicit tag: TypeTag[T]) =
        cache.getOrElse(tag, new TypeReference[T] {
            override def getType = jType(tag.tpe)
        })

    private def jType(typ: Type): JType =
        synchronized {
            typ match {
                case TypeRef(_, sig, args) =>
                    new ParameterizedType {
                        val getRawType = mirror.runtimeClass(sig.asType.toType)
                        val getActualTypeArguments = args.map(jType).toArray
                        val getOwnerType = null
                    }
            }
        }
}