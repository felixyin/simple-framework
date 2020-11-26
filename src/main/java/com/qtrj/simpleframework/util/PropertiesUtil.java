package com.qtrj.simpleframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class PropertiesUtil {
  private static Logger log = Logger.getLogger(Logger.class);
  
  private static Properties p = new Properties();
  
  public static void main(String[] args) throws IOException {}
  
  public static void init(String configPath) {
    Properties p1 = new Properties();
    InputStream is = PropertiesUtil.class.getResourceAsStream(configPath);
    try {
      p1.load(is);
      p.putAll(p1);
    } catch (IOException ex) {
      log.error(ex);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException ex) {
          log.error(ex);
        } 
        is = null;
      } 
    } 
  }
  
  public static void initExtiact(String extiactConfigPath) {
    Properties p1 = new Properties();
    InputStream is = null;
    try {
      is = new FileInputStream(new File(extiactConfigPath));
      p1.load(is);
      p.putAll(p1);
    } catch (IOException ex) {
      log.error(ex);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException ex) {
          log.error(ex);
        } 
        is = null;
      } 
    } 
  }
  
  public static String getProperties(String key) {
    String result = null;
    if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(p.getProperty(key)))
      result = p.getProperty(key).trim(); 
    return result;
  }
  
  public static Integer getNumber(String key) {
    if (StringUtils.isBlank(key) || StringUtils.isBlank(getProperties(key)))
      return Integer.valueOf(0); 
    return Integer.valueOf(Integer.parseInt(getProperties(key)));
  }
  
  public static boolean getBoolean(String key) {
    boolean result = false;
    if (StringUtils.isBlank(key) || StringUtils.isBlank(getProperties(key))) {
      result = false;
    } else {
      result = Boolean.parseBoolean(getProperties(key));
    } 
    return result;
  }
}
