package com.qtrj.simpleframework.ssh.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ActionContext<T> {
  private static final String KEY_REQUEST = "request";
  
  private static final String KEY_RESPONSE = "response";
  
  private static final String KEY_SESSION = "session";
  
  private static final String KEY_FOR_PAGER = "datatable_pager";
  
  private static final String KEY_SERVLET_CONTEXT = "servlet_context";
  
  private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();
  
  private Map<String, Object> sqlSessionMap;
  
  public static HttpSession getSession() {
    Map<String, Object> map = threadLocal.get();
    if (null != map) {
      HttpSession s = (HttpSession)map.get("session");
      if (null != s)
        return s; 
    } 
    return null;
  }
  
  public static void setSession(HttpSession s) {
    Map<String, Object> map = threadLocal.get();
    if (null == map) {
      map = new HashMap<>();
      threadLocal.set(map);
    } 
    map.put("session", s);
  }
  
  public static HttpServletRequest getReq() {
    Map<String, Object> map = threadLocal.get();
    if (null != map) {
      HttpServletRequest req = (HttpServletRequest)map.get("request");
      if (null != req)
        return req; 
    } 
    return null;
  }
  
  public static void setReq(HttpServletRequest req) {
    Map<String, Object> map = threadLocal.get();
    if (null == map) {
      map = new HashMap<>();
      threadLocal.set(map);
    } 
    map.put("request", req);
  }
  
  public static HttpServletResponse getResp() {
    Map<String, Object> map = threadLocal.get();
    if (null != map) {
      HttpServletResponse resp = (HttpServletResponse)map.get("response");
      if (null != resp)
        return resp; 
    } 
    return null;
  }
  
  public static void setResp(HttpServletResponse resp) {
    Map<String, Object> map = threadLocal.get();
    if (null == map) {
      map = new HashMap<>();
      threadLocal.set(map);
    } 
    map.put("response", resp);
  }
  
  public static boolean isForDataTable() {
    Map<String, Object> map = threadLocal.get();
    if (null != map) {
      Object resp = map.get("datatable_pager");
      if (null != resp)
        return ((Boolean)resp).booleanValue(); 
    } 
    return false;
  }
  
  public static void setForDataTable(boolean value) {
    Map<String, Object> map = threadLocal.get();
    if (null == map) {
      map = new HashMap<>();
      threadLocal.set(map);
    } 
    map.put("datatable_pager", Boolean.valueOf(value));
  }
  
  public static <T> T get(String key, T tt) {
    Map<String, Object> map = threadLocal.get();
    if (null != map) {
      T t = (T)map.get(key);
      if (null != t)
        return t; 
    } 
    return null;
  }
  
  public static <T> void set(String key, T t) {
    Map<String, Object> map = threadLocal.get();
    if (null == map) {
      map = new HashMap<>();
      threadLocal.set(map);
    } 
    map.put(key, t);
  }
}
