package com.qtrj.simpleframework.ssh.repository.jdbc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtil {
  public static Connection openConnection(String driverClass, String url, String username, String password) throws JdbcException {
    try {
      Class.forName(driverClass);
      Connection connection = DriverManager.getConnection(url, username, password);
      return connection;
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
  }
  
  public static void closeConnection(Connection con) throws JdbcException {
    try {
      if (null != con)
        try {
          con.close();
        } catch (SQLException ex) {
          Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
          throw new JdbcException(ex.getMessage(), ex.getCause());
        }  
    } finally {
      con = null;
      System.gc();
    } 
  }
  
  public static List<Map<String, Object>> queryMapList(Connection con, String sql) throws JdbcException {
    List<Map<String, Object>> lists = new ArrayList<>();
    try {
      Statement preStmt = null;
      ResultSet rs = null;
      try {
        preStmt = con.createStatement();
        rs = preStmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while (null != rs && rs.next()) {
          Map<String, Object> map = new HashMap<>();
          for (int i = 0; i < columnCount; i++) {
            String name = rsmd.getColumnName(i + 1);
            Object value = rs.getObject(name);
            map.put(name, value);
          } 
          lists.add(map);
        } 
      } finally {
        if (null != rs)
          rs.close(); 
        if (null != preStmt)
          preStmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
    return lists;
  }
  
  public static List<Map<String, Object>> queryMapList(Connection con, String sql, Object... params) throws JdbcException {
    List<Map<String, Object>> lists = new ArrayList<>();
    try {
      PreparedStatement preStmt = null;
      ResultSet rs = null;
      try {
        preStmt = con.prepareStatement(sql);
        for (int i = 0; i < params.length; i++)
          preStmt.setObject(i + 1, params[i]); 
        rs = preStmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while (null != rs && rs.next()) {
          Map<String, Object> map = new HashMap<>();
          for (int j = 0; j < columnCount; j++) {
            String name = rsmd.getColumnName(j + 1);
            Object value = rs.getObject(name);
            map.put(name, value);
          } 
          lists.add(map);
        } 
      } finally {
        if (null != rs)
          rs.close(); 
        if (null != preStmt)
          preStmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
    return lists;
  }
  
  public static <T> List<T> queryBeanList(Connection con, String sql, Class<T> beanClass) throws JdbcException {
    List<T> lists = new ArrayList<>();
    try {
      Statement stmt = null;
      ResultSet rs = null;
      Field[] fields = null;
      try {
        stmt = con.createStatement();
        rs = stmt.executeQuery(sql);
        fields = beanClass.getDeclaredFields();
        for (Field f : fields)
          f.setAccessible(true); 
        while (null != rs && rs.next()) {
          T t = beanClass.newInstance();
          for (Field f : fields) {
            String name = f.getName();
            try {
              Object value = rs.getObject(name);
              setValue(t, f, value);
            } catch (Exception e) {}
          } 
          lists.add(t);
        } 
      } finally {
        if (null != rs)
          rs.close(); 
        if (null != stmt)
          stmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
    return lists;
  }
  
  public static <T> List<T> queryBeanList(Connection con, String sql, Class<T> beanClass, Object... params) throws JdbcException {
    List<T> lists = new ArrayList<>();
    try {
      PreparedStatement preStmt = null;
      ResultSet rs = null;
      Field[] fields = null;
      try {
        preStmt = con.prepareStatement(sql);
        for (int i = 0; i < params.length; i++)
          preStmt.setObject(i + 1, params[i]); 
        rs = preStmt.executeQuery();
        fields = beanClass.getDeclaredFields();
        for (Field f : fields)
          f.setAccessible(true); 
        while (null != rs && rs.next()) {
          T t = beanClass.newInstance();
          for (Field f : fields) {
            String name = f.getName();
            try {
              Object value = rs.getObject(name);
              setValue(t, f, value);
            } catch (Exception e) {}
          } 
          lists.add(t);
        } 
      } finally {
        if (null != rs)
          rs.close(); 
        if (null != preStmt)
          preStmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
    return lists;
  }
  
  public static <T> List<T> queryBeanList(Connection con, String sql, IResultSetCall<T> qdi) throws JdbcException {
    List<T> lists = new ArrayList<>();
    try {
      Statement stmt = null;
      ResultSet rs = null;
      try {
        stmt = con.createStatement();
        rs = stmt.executeQuery(sql);
        while (null != rs && rs.next())
          lists.add(qdi.invoke(rs)); 
      } finally {
        if (null != rs)
          rs.close(); 
        if (null != stmt)
          stmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
    return lists;
  }
  
  public static <T> List<T> queryBeanList(Connection con, String sql, IResultSetCall<T> qdi, Object... params) throws JdbcException {
    List<T> lists = new ArrayList<>();
    try {
      PreparedStatement preStmt = null;
      ResultSet rs = null;
      try {
        preStmt = con.prepareStatement(sql);
        for (int i = 0; i < params.length; i++)
          preStmt.setObject(i + 1, params[i]); 
        rs = preStmt.executeQuery();
        while (null != rs && rs.next())
          lists.add(qdi.invoke(rs)); 
      } finally {
        if (null != rs)
          rs.close(); 
        if (null != preStmt)
          preStmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
    return lists;
  }
  
  public static <T> T queryBean(Connection con, String sql, Class<T> beanClass) throws JdbcException {
    List<T> lists = queryBeanList(con, sql, beanClass);
    return beanError(lists);
  }
  
  public static <T> T queryBean(Connection con, String sql, Class<T> beanClass, Object... params) throws JdbcException {
    List<T> lists = queryBeanList(con, sql, beanClass, params);
    return beanError(lists);
  }
  
  public static <T> List<T> queryObjectList(Connection con, String sql, Class<T> objClass) throws JdbcException {
    List<T> lists = new ArrayList<>();
    try {
      Statement stmt = null;
      ResultSet rs = null;
      try {
        stmt = con.createStatement();
        rs = stmt.executeQuery(sql);
        while (null != rs && rs.next()) {
          Constructor[] arrayOfConstructor = (Constructor[])objClass.getConstructors();
          for (Constructor<?> c : arrayOfConstructor) {
            Object value = rs.getObject(1);
            try {
              lists.add((T)c.newInstance(new Object[] { value }));
              break;
            } catch (Exception e) {}
          } 
        } 
      } finally {
        if (null != rs)
          rs.close(); 
        if (null != stmt)
          stmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
    return lists;
  }
  
  public static <T> List<T> queryObjectList(Connection con, String sql, Class<T> objClass, Object... params) throws JdbcException {
    List<T> lists = new ArrayList<>();
    try {
      PreparedStatement preStmt = null;
      ResultSet rs = null;
      try {
        preStmt = con.prepareStatement(sql);
        for (int i = 0; i < params.length; i++)
          preStmt.setObject(i + 1, params[i]); 
        rs = preStmt.executeQuery();
        while (null != rs && rs.next()) {
          Constructor[] arrayOfConstructor = (Constructor[])objClass.getConstructors();
          for (Constructor<?> c : arrayOfConstructor) {
            String value = rs.getObject(1).toString();
            try {
              T t = (T)c.newInstance(new Object[] { value });
              lists.add(t);
              break;
            } catch (Exception e) {}
          } 
        } 
      } finally {
        if (null != rs)
          rs.close(); 
        if (null != preStmt)
          preStmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
    return lists;
  }
  
  public static <T> T queryObject(Connection con, String sql, Class<T> objClass) throws JdbcException {
    List<T> lists = queryObjectList(con, sql, objClass);
    return beanError(lists);
  }
  
  public static <T> T queryObject(Connection con, String sql, Class<T> objClass, Object... params) throws JdbcException {
    List<T> lists = queryObjectList(con, sql, objClass, params);
    return beanError(lists);
  }
  
  public static int execute(Connection con, String sql) throws JdbcException {
    try {
      Statement stmt = null;
      try {
        stmt = con.createStatement();
        return stmt.executeUpdate(sql);
      } finally {
        if (null != stmt)
          stmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
  }
  
  public static int execute(Connection con, String sql, Object... params) throws JdbcException {
    try {
      PreparedStatement preStmt = null;
      try {
        System.out.println("SQL:\n" + sql);
        preStmt = con.prepareStatement(sql);
        int i;
        for (i = 0; i < params.length; i++) {
          preStmt.setObject(i + 1, params[i]);
          System.out.println("index," + (i + 1) + " : " + params[i]);
        } 
        i = preStmt.executeUpdate();
        return i;
      } finally {
        if (null != preStmt)
          preStmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
  }
  
  public static int[] executeAsBatch(Connection con, List<String> sqlList) throws JdbcException {
    return executeAsBatch(con, sqlList.<String>toArray(new String[0]));
  }
  
  public static int[] executeAsBatch(Connection con, String[] sqlArray) throws JdbcException {
    try {
      Statement stmt = null;
      try {
        stmt = con.createStatement();
        for (String sql : sqlArray)
          stmt.addBatch(sql); 
        return stmt.executeBatch();
      } finally {
        if (null != stmt)
          stmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
  }
  
  public static int[] executeAsBatch(Connection con, String sql, Object[][] params) throws JdbcException {
    try {
      PreparedStatement preStmt = null;
      try {
        preStmt = con.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
          Object[] rowParams = params[i];
          for (int k = 0; k < rowParams.length; k++) {
            Object obj = rowParams[k];
            preStmt.setObject(k + 1, obj);
          } 
          preStmt.addBatch();
        } 
        return preStmt.executeBatch();
      } finally {
        if (null != preStmt)
          preStmt.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
  }
  
  private static <T> void setValue(T t, Field f, Object v) throws IllegalArgumentException, IllegalAccessException {
    if (null == v)
      return; 
    f.set(t, v);
  }
  
  public static void executeProcedure(Connection con, String procedureName, Object... params) throws JdbcException {
    try {
      CallableStatement proc = null;
      try {
        proc = con.prepareCall(procedureName);
        for (int i = 0; i < params.length; i++)
          proc.setObject(i + 1, params[i]); 
        proc.execute();
      } finally {
        if (null != proc)
          proc.close(); 
      } 
    } catch (Exception ex) {
      Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new JdbcException(ex.getMessage(), ex.getCause());
    } 
  }
  
  public static <T> List<List<T>> listLimit(List<T> lists, int pageSize) {
    List<List<T>> llists = new ArrayList<>();
    for (int i = 0; i < lists.size(); i += pageSize) {
      try {
        List<T> list = lists.subList(i, i + pageSize);
        llists.add(list);
      } catch (IndexOutOfBoundsException e) {
        List<T> list = lists.subList(i, i + lists.size() % pageSize);
        llists.add(list);
      } 
    } 
    return llists;
  }
  
  public static String unWarpTableName(String simpleClassName) {
    return simpleClassName.replace("Po", "").replace("Entity", "");
  }
  
  public static Long getNextVal(Connection con, String req) throws JdbcException {
    return queryObject(con, "SELECT nextval(?) FROM DUAL", Long.class, new Object[] { req });
  }
  
  public static Map<String, Object> queryMap(Connection conn, String sql) throws JdbcException {
    List<Map<String, Object>> list = queryMapList(conn, sql);
    return beanError(list);
  }
  
  public static Map<String, Object> queryMap(Connection conn, String sql, Object... params) throws JdbcException {
    List<Map<String, Object>> list = queryMapList(conn, sql, params);
    return beanError(list);
  }
  
  public static <T> T beanError(List<T> list) throws JdbcException {
    if (list.size() > 1)
      throw new JdbcException("期待一行，却返回了" + list.size() + "行！"); 
    if (list.size() == 0)
      return null; 
    return list.get(0);
  }
}
