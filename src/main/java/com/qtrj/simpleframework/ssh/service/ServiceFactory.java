package com.qtrj.simpleframework.ssh.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {
  private static Map serviceMap = new HashMap<>();
  
  public static <T> T getInstance(Class<T> clazz) {
    Object o = serviceMap.get(clazz);
    if (null == o) {
      o = (new CglibProxy()).get(clazz);
      serviceMap.put(clazz, o);
    } 
    return (T)o;
  }
}
