package net.fwbrasil.zoot.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.fwbrasil.zoot.core.request.RequestMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EndpointAnnotation {
	String method() default RequestMethod.GET;
	String path();
}
