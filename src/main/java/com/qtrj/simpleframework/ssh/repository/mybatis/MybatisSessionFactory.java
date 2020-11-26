package com.qtrj.simpleframework.ssh.repository.mybatis;

import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class MybatisSessionFactory {
  private static String key = "mybatis_key";
  
  private static String key_autocommit = "autocommit";
  
  private static String key_junit = "junit";
  
  private static Map<String, SqlSessionFactory> factoryMap = new HashMap<>();
  
  private static Map<String, Boolean> autoCommitMap = new HashMap<>();
  
  private static Map<String, Boolean> junitMap = new HashMap<>();
  
  public static SqlSessionFactory getFactory() {
    return factoryMap.get(key);
  }
  
  public static void putFactory(SqlSessionFactory factory) {
    factoryMap.put(key, factory);
  }
  
  public static boolean isAutoCommit() {
    Boolean b = autoCommitMap.get(key_autocommit);
    if (null != b)
      return b.booleanValue(); 
    return true;
  }
  
  public static void setAutoCommit(boolean b) {
    autoCommitMap.put(key_autocommit, Boolean.valueOf(b));
  }
  
  public static boolean isJunit() {
    Boolean b = junitMap.get(key_junit);
    if (null != b)
      return b.booleanValue(); 
    return false;
  }
  
  public static void setJunit(boolean b) {
    junitMap.put(key_junit, Boolean.valueOf(b));
  }
  
  public static SqlSession getSqlSession(boolean b) {
    return getFactory().openSession(b);
  }
}
