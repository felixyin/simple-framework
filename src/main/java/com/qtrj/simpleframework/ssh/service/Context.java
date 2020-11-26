package com.qtrj.simpleframework.ssh.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;

public class Context implements Serializable {
  private static final String SQL_SESSION = "SQL_SESSION";
  
  private static final String ISSHOWBANZU = "is_preview";
  
  private static ThreadLocal<Map> threadLocal = new ThreadLocal();
  
  private Map<String, Object> sqlSessionMap;
  
  public static SqlSession getSqlSession() {
    Map<String, Object> map = threadLocal.get();
    if (null != map) {
      SqlSession sqlSession = (SqlSession)map.get("SQL_SESSION");
      if (null != sqlSession)
        return sqlSession; 
    } 
    return null;
  }
  
  public static void setSqlSession(SqlSession sqlSession) {
    Map<String, Object> map = threadLocal.get();
    if (null == map) {
      map = new HashMap<>();
      threadLocal.set(map);
    } 
    map.put("SQL_SESSION", sqlSession);
  }
  
  public static String getShowBanzu() {
    Map<String, Object> map = threadLocal.get();
    if (null != map) {
      String isShowBanzu = (String)map.get("is_preview");
      if (null != isShowBanzu)
        return isShowBanzu; 
    } 
    return null;
  }
  
  public static void setShowBanzu(String val) {
    Map<String, Object> map = threadLocal.get();
    if (null == map) {
      map = new HashMap<>();
      threadLocal.set(map);
    } 
    map.put("is_preview", val);
  }
}
