package com.qtrj.simpleframework.ssh.repository.mybatis;

import java.util.HashMap;
import java.util.Map;

public class RegistorDynamicDb {
  private static final String KEY = "KEY";
  
  private static ThreadLocal<Map> threadLocal = new ThreadLocal();
  
  private Map<String, String> sqlSessionMap;
  
  public static String getKey() {
    Map<String, Object> map = threadLocal.get();
    if (null != map) {
      String sqlSession = (String)map.get("KEY");
      if (null != sqlSession)
        return sqlSession; 
    } 
    return null;
  }
  
  public static void setKey(String key) {
    Map<String, String> map = threadLocal.get();
    if (null == map) {
      map = new HashMap<>();
      threadLocal.set(map);
    } 
    map.put("KEY", key);
  }
}
