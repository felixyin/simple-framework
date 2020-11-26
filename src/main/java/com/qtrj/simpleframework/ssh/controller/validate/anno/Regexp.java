package com.qtrj.simpleframework.ssh.controller.validate.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Regexp {
  RegexpType value() default RegexpType.ALL;
  
  String message() default "不合法";
}
