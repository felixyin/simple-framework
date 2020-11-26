package com.qtrj.simpleframework.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class DateJsonValueProcessor implements JsonValueProcessor {
  private String format = "yyyy-MM-dd";
  
  public DateJsonValueProcessor() {}
  
  public DateJsonValueProcessor(String format) {
    this.format = format;
  }
  
  public Object processArrayValue(Object value, JsonConfig jsonConfig) {
    String[] obj = new String[0];
    if (value != null)
      if (value instanceof Date[]) {
        SimpleDateFormat sf = new SimpleDateFormat(this.format);
        Date[] dates = (Date[])value;
        obj = new String[dates.length];
        for (int i = 0; i < dates.length; i++)
          obj[i] = sf.format(dates[i]); 
      } else if (value instanceof Timestamp[]) {
        SimpleDateFormat sf = new SimpleDateFormat(this.format);
        Timestamp[] timestamps = (Timestamp[])value;
        obj = new String[timestamps.length];
        for (int i = 0; i < timestamps.length; i++)
          obj[i] = sf.format(timestamps[i]); 
      }  
    return obj;
  }
  
  public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
    if (value != null) {
      if (value instanceof Date) {
        String str = (new SimpleDateFormat(this.format)).format((Date)value);
        return str;
      } 
      if (value instanceof Timestamp) {
        String str = (new SimpleDateFormat(this.format)).format((Date)value);
        return str;
      } 
    } 
    return value;
  }
  
  public String getFormat() {
    return this.format;
  }
  
  public void setFormat(String format) {
    this.format = format;
  }
}
