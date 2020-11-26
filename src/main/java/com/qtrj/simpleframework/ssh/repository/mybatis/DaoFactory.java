package com.qtrj.simpleframework.ssh.repository.mybatis;

import com.qtrj.simpleframework.ssh.service.Context;

public class DaoFactory {
  public static <T> T get(Class<T> clazz) {
    return (T) Context.getSqlSession().getMapper(clazz);
  }
  
}
