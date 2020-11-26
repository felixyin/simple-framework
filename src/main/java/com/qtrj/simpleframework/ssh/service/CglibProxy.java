package com.qtrj.simpleframework.ssh.service;

import com.qtrj.simpleframework.ssh.controller.WebUtil;
import com.qtrj.simpleframework.ssh.repository.mybatis.DataTablePager;
import com.qtrj.simpleframework.ssh.repository.mybatis.MybatisSessionFactory;
import com.qtrj.simpleframework.util.MemcachedUtil;
import java.lang.reflect.Method;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class CglibProxy implements MethodInterceptor {
  private static Logger log = Logger.getLogger(CglibProxy.class);
  
  private Enhancer enhancer = new Enhancer();
  
  public <T> T get(Class<T> clazz) {
    this.enhancer.setSuperclass(clazz);
    this.enhancer.setCallback((Callback)this);
    return (T)this.enhancer.create();
  }
  
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    Object result = null;
    boolean isOk = true;
    Transaction transactionAnno = null;
    Memcached memcachedAnno = null;
    DataTablePager annotation = null;
    try {
      transactionAnno = method.<Transaction>getAnnotation(Transaction.class);
      memcachedAnno = method.<Memcached>getAnnotation(Memcached.class);
      annotation = method.<DataTablePager>getAnnotation(DataTablePager.class);
    } catch (Exception e) {
      log.error("error:", e);
    } 
    if (null == transactionAnno)
      transactionAnno = method.getDeclaringClass().<Transaction>getAnnotation(Transaction.class); 
    if (null != memcachedAnno) {
      MemcachedType type = memcachedAnno.type();
      if (type == MemcachedType.SEL) {
        String key = memcachedAnno.key();
        try {
          if (null != args && args.length > 0) {
            Object param1 = args[memcachedAnno.keyIdx()];
            key = key + param1.toString();
          } 
        } catch (Exception e) {}
        result = MemcachedUtil.get(key);
        if (null != result)
          return result; 
      } 
    } 
    if (null != annotation)
      WebUtil.prePageForDataTable(); 
    if (null != transactionAnno) {
      TrancationType jdbcTrancationType = transactionAnno.jdbc();
      SqlSession sqlSession = Context.getSqlSession();
      try {
        if (jdbcTrancationType != TrancationType.NONE) {
          if (null == sqlSession) {
            if (jdbcTrancationType == TrancationType.OPEN) {
              sqlSession = MybatisSessionFactory.getSqlSession(false);
            } else {
              sqlSession = MybatisSessionFactory.getSqlSession(true);
            } 
            Context.setSqlSession(sqlSession);
          } else {
            isOk = false;
          } 
        } else {
          isOk = false;
        } 
        result = proxy.invokeSuper(obj, args);
        memcachedLogic(result, memcachedAnno, args);
        if (isOk)
          if (jdbcTrancationType == TrancationType.OPEN && 
            null != sqlSession)
            sqlSession.commit();  
      } catch (Exception e) {
        log.error("error:", e);
        if (isOk && 
          jdbcTrancationType == TrancationType.OPEN && 
          null != sqlSession)
          sqlSession.rollback(); 
        throw e;
      } finally {
        if (isOk && 
          null != sqlSession) {
          sqlSession.close();
          sqlSession = null;
          Context.setSqlSession(sqlSession);
        } 
      } 
    } else {
      result = proxy.invokeSuper(obj, args);
      memcachedLogic(result, memcachedAnno, args);
    } 
    return result;
  }
  
  private void memcachedLogic(Object result, Memcached memcachedAnno, Object[] args) {
    if (null != memcachedAnno) {
      MemcachedType type = memcachedAnno.type();
      String key = memcachedAnno.key();
      try {
        if (null != args && args.length > 0) {
          Object param1 = args[memcachedAnno.keyIdx()];
          key = key + param1.toString();
        } 
      } catch (Exception e) {}
      if (type == MemcachedType.SEL)
        MemcachedUtil.put(key, result); 
      if (type == MemcachedType.UPD)
        MemcachedUtil.replace(key, result); 
      if (type == MemcachedType.DEL)
        MemcachedUtil.remove(key); 
    } 
  }
}
