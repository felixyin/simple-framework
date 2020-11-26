package com.qtrj.simpleframework.ssh.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transaction {
  String catalog() default "commondb";
  
  TrancationType jdbc() default TrancationType.OPEN;
}
