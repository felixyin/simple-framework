package com.qtrj.simpleframework.ssh.controller;

import java.lang.reflect.Method;

public class Cache {
  private Object object;
  
  private Method method;
  
  public Cache() {}
  
  public Cache(Object object, Method method) {
    this.object = object;
    this.method = method;
  }
  
  public Object getObject() {
    return this.object;
  }
  
  public void setObject(Object object) {
    this.object = object;
  }
  
  public Method getMethod() {
    return this.method;
  }
  
  public void setMethod(Method method) {
    this.method = method;
  }
}
