package com.qtrj.simpleframework.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelUtil {
  public void createExcel(String templateFileName, List<?> list, String resultFileName) {
    try {
      XLSTransformer transformer = new XLSTransformer();
      URL url = getClass().getClassLoader().getResource("");
      String srcFilePath = url.getPath() + templateFileName;
      Map<String, Object> map = new HashMap<>();
      map.put("list", list);
      String destFilePath = url.getPath() + resultFileName;
      transformer.transformXLS(srcFilePath, map, destFilePath);
    } catch (Exception e) {
      throw new RuntimeException("error happens...", e);
    } 
  }
  
  public void writeExcel(String templateFileName, List<?> list, OutputStream outputStream) {
    try {
      XLSTransformer transformer = new XLSTransformer();
      URL url = getClass().getClassLoader().getResource("");
      String srcFilePath = url.getPath() + templateFileName;
      Map<String, Object> map = new HashMap<>();
      map.put("list", list);
      BufferedInputStream is = new BufferedInputStream(new FileInputStream(srcFilePath));
      Workbook workbook = transformer.transformXLS(is, map);
      workbook.write(outputStream);
      is.close();
      outputStream.flush();
      outputStream.close();
    } catch (Exception e) {
      throw new RuntimeException("error happens...", e);
    } 
  }
}
