package net.fwbrasil.zoot.core.mapper

class JacksonStringMapperSpec extends StringMapperSpec {

    implicit val mirror = scala.reflect.runtime.currentMirror

    def mapper = new JacksonStringMapper

}