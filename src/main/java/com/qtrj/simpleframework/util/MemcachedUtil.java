package com.qtrj.simpleframework.util;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedUtil {
  private static MemCachedClient cachedClient = new MemCachedClient();
  
  static {
    PropertiesUtil.initExtiact("/usr/local/etc/lcsf/memcached.cfg");
    SockIOPool pool = SockIOPool.getInstance();
    String[] servers = { PropertiesUtil.getProperties("servers") };
    Integer[] weights = { PropertiesUtil.getNumber("weights") };
    pool.setServers(servers);
    pool.setWeights(weights);
    pool.setInitConn(PropertiesUtil.getNumber("initConn").intValue());
    pool.setMinConn(PropertiesUtil.getNumber("minConn").intValue());
    pool.setMaxConn(PropertiesUtil.getNumber("maxConn").intValue());
    pool.setMaxIdle(PropertiesUtil.getNumber("maxIdle").intValue());
    pool.setMaintSleep(PropertiesUtil.getNumber("maintSleep").intValue());
    pool.setNagle(PropertiesUtil.getBoolean("nagle"));
    pool.setSocketTO(PropertiesUtil.getNumber("socketTO").intValue());
    pool.setSocketConnectTO(PropertiesUtil.getNumber("socketConnectTO").intValue());
    pool.setFailback(PropertiesUtil.getBoolean("failback"));
    pool.setFailover(PropertiesUtil.getBoolean("failover"));
    pool.setAliveCheck(PropertiesUtil.getBoolean("aliveCheck"));
    pool.initialize();
  }
  
  public static boolean add(String key, Object value) {
    return cachedClient.add(key, value);
  }
  
  public static boolean add(String key, Object value, Integer expire) {
    return cachedClient.add(key, value, expire);
  }
  
  public static boolean put(String key, Object value) {
    return cachedClient.set(key, value);
  }
  
  public static boolean put(String key, Object value, Integer expire) {
    return cachedClient.set(key, value, expire);
  }
  
  public static boolean replace(String key, Object value) {
    return cachedClient.replace(key, value);
  }
  
  public static boolean replace(String key, Object value, Integer expire) {
    return cachedClient.replace(key, value, expire);
  }
  
  public static Object get(String key) {
    return cachedClient.get(key);
  }
  
  public static boolean remove(String key) {
    return cachedClient.delete(key);
  }
}
