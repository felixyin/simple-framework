package com.qtrj.simpleframework.ssh.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Memcached {
  String key();
  
  MemcachedType type() default MemcachedType.SEL;
  
  int keyIdx() default -1;
}
