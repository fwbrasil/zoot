package net.fwbrasil.zoot.core.util

import java.lang.reflect.Method
import scala.concurrent.Future
import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import scala.reflect._

object Stub {

    private var stubClassCache = Map[String, Class[_]]()

    def apply[T: ClassTag](callback: (Method, Array[Object]) => Option[Future[Any]])(implicit mirror: Mirror) =
        synchronized {
            val typeDescription = classTag[T].toString
            val stubClass = cachedStubClass(typeDescription)
            Enhancer.create(stubClass, interceptor(callback)).asInstanceOf[T]
        }

    private def cachedStubClass(typeDescription: String)(implicit mirror: Mirror) =
        stubClassCache.getOrElse(typeDescription, stubClass(typeDescription))

    private def stubClass(typeDescription: String)(implicit mirror: Mirror) = {
        val toolBox = mirror.mkToolBox()
        val tree = toolBox.parse(classBody(typeDescription))
        toolBox.eval(tree).asInstanceOf[Class[_]]
    }

    private def classBody(typeDescription: String) =
        s"""abstract class Stub extends $typeDescription
            scala.reflect.classTag[Stub].runtimeClass"""

    private def interceptor(callback: (Method, Array[Object]) => Option[Future[Any]]) =
        new MethodInterceptor {
            override def intercept(obj: Object, method: Method, args: Array[Object], proxy: MethodProxy) =
                callback(method, args).getOrElse(proxy.invokeSuper(obj, args))
        }
}