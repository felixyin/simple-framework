package com.qtrj.simpleframework.ssh.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qtrj.simpleframework.ssh.controller.validate.ValidationException;
import com.qtrj.simpleframework.ssh.controller.validate.anno.NotNull;
import com.qtrj.simpleframework.ssh.controller.validate.anno.Regexp;
import com.qtrj.simpleframework.ssh.controller.validate.anno.RegexpType;
import com.qtrj.simpleframework.util.DateJsonValueProcessor;
import com.qtrj.simpleframework.util.ExcelUtil;
import com.qtrj.simpleframework.util.SshConstant;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

public class WebUtil {
  public static final String JSP_PATH = "path";
  
  public static final int MAX_POST_SIZE = 1073741824;
  
  public static final JsonConfig JSON_CONFIG = new JsonConfig();
  
  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  
  public static final String DATE_FORMAT_NO_SECOND = "yyyy-MM-dd HH:mm";
  
  public static final String DATE_FORMAT_NO_TIME = "yyyy-MM-dd";
  
  static {
    JSON_CONFIG.registerJsonValueProcessor(Date.class, (JsonValueProcessor)new DateJsonValueProcessor("yyyy-MM-dd"));
    JSON_CONFIG.registerJsonValueProcessor(Timestamp.class, (JsonValueProcessor)new DateJsonValueProcessor("yyyy-MM-dd"));
  }
  
  private static Logger log = Logger.getLogger(WebUtil.class);
  
  public static Date parseDate(String dateStr, String format) throws RuntimeException {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    try {
      return sdf.parse(dateStr);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static Date parseDate(String dateStr) throws RuntimeException {
    return parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
  }
  
  public static String format(Date date, String format) {
    return FastDateFormat.getInstance(format).format(date);
  }
  
  public static String format(Date date) {
    return FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(date);
  }
  
  public static void writeText(String Text) throws RuntimeException {
    write("text/html", Text);
  }
  
  public static void writeExcel(String templateFile, String fileName, List list) throws IOException {
    HttpServletResponse resp = WebUtil.getResp();
    resp.setContentType("application/vnd.ms-excel");
    resp.addHeader("Content-Disposition", "attachment;   filename=\"" + fileName + ".xls" + "\"");
    (new ExcelUtil()).writeExcel(templateFile, list, (OutputStream)resp.getOutputStream());
  }
  
  public static void write(InputStream is) throws RuntimeException {
    try {
      write(IOUtils.toByteArray(is));
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  @Deprecated
  private static void writeJson(Object obj) throws RuntimeException {
    try {
      HttpServletResponse response = ActionContext.getResp();
      response.setContentType("application/json;charset=UTF-8");
      PrintWriter out = response.getWriter();
      try {
        if (obj instanceof List || obj.getClass().isArray()) {
          JSONArray jSONArray = JSONArray.fromObject(obj);
          out.print(jSONArray);
        } else {
          JSONObject jSONObject = JSONObject.fromObject(obj);
          out.print(jSONObject);
        } 
      } finally {
        out.flush();
        out.close();
      } 
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void writeJson(Boolean b, String message) throws RuntimeException {
    Map<String, Object> map = new HashMap<>();
    map.put("success", b);
    map.put("message", message);
    writeJsonObject(map);
  }
  
  public static void writeJson(String str) {
    write("text/javascript", str);
  }
  
  public static String javaObjectConvertJson(Object obj) {
    return JSONObject.fromObject(obj, JSON_CONFIG).toString();
  }
  
  public static String javaArrayConvertJson(Object obj) {
    return JSONArray.fromObject(obj, JSON_CONFIG).toString();
  }
  
  public static <T> T jsonConvertJavaObject(Class<T> c, String json) {
    return (T)JSONObject.toBean(JSONObject.fromObject(json, JSON_CONFIG), c);
  }
  
  public static <T> Object jsonConvertJavaArray(Class<T> c, String json) {
    return JSONArray.toArray(JSONArray.fromObject(json, JSON_CONFIG), c);
  }
  
  public static void writeJsonObject(Object obj) throws RuntimeException {
    wirteJson(JSONObject.fromObject(obj, JSON_CONFIG));
  }
  
  public static String getJsonObjectString(Object obj) throws RuntimeException {
    return JSONObject.fromObject(obj, JSON_CONFIG).toString();
  }
  
  public static String getJsonArrayString(Object obj) throws RuntimeException {
    return JSONArray.fromObject(obj, JSON_CONFIG).toString();
  }
  
  public static void wirteJsonForDataTable(List list) throws RuntimeException {
    Map<String, Object> json = new HashMap<>();
    json.put("dataList", list);
    json.put("iTotalRecords", Long.valueOf(((Page)list).getTotal()));
    json.put("sEcho", getInteger(new String[] { "sEcho" }));
    json.put("iTotalDisplayRecords", Long.valueOf(((Page)list).getTotal()));
    writeJsonObject(json);
  }
  
  public static void writeJsonArray(Object obj) throws RuntimeException {
    wirteJson(JSONArray.fromObject(obj, JSON_CONFIG));
  }
  
  public static void writeJsonArray(List obj) throws RuntimeException {
    try {
      wirteJsonForDataTable(obj);
    } catch (Exception e) {
      wirteJson(JSONArray.fromObject(obj, JSON_CONFIG));
    } 
  }
  
  private static void wirteJson(Object obj) throws RuntimeException {
    write("application/json", obj);
  }
  
  public static void writeHtml(String html) throws RuntimeException {
    write("text/html", html);
  }
  
  public static void writeScript(String script) throws RuntimeException {
    writeHtml("<script type='text/javascript'>" + script + "</script>");
  }
  
  public static void write(byte[] attachFile, String fileName) throws RuntimeException {
    try {
      HttpServletResponse resp = ActionContext.getResp();
      resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);
      ServletOutputStream outputStream = resp.getOutputStream();
      outputStream.write(attachFile);
      outputStream.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void write(byte[] attachFile) throws RuntimeException {
    try {
      HttpServletResponse resp = ActionContext.getResp();
      ServletOutputStream outputStream = resp.getOutputStream();
      outputStream.write(attachFile);
      outputStream.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void download(byte[] attachFile, String fileName) throws RuntimeException {
    try {
      HttpServletResponse resp = ActionContext.getResp();
      resp.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
      resp.addHeader("Content-Length", "" + attachFile.length);
      ServletOutputStream outputStream = resp.getOutputStream();
      outputStream.write(attachFile);
      outputStream.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void download(File file, String fileName) throws RuntimeException {
    try {
      HttpServletResponse resp = ActionContext.getResp();
      resp.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
      ServletOutputStream outputStream = resp.getOutputStream();
      outputStream.write(IOUtils.toByteArray(new FileInputStream(file)));
      outputStream.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void write(String contentType, Object object) throws RuntimeException {
    try {
      HttpServletResponse response = ActionContext.getResp();
      response.setContentType(contentType + ";charset=UTF-8");
      PrintWriter writer = response.getWriter();
      writer.print(object);
      writer.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void write(String contentType, byte[] attachFile) throws RuntimeException {
    try {
      HttpServletResponse resp = ActionContext.getResp();
      resp.setContentType(contentType);
      ServletOutputStream outputStream = resp.getOutputStream();
      outputStream.write(attachFile);
      outputStream.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void redirect(String location) throws RuntimeException {
    try {
      getResp().sendRedirect(location);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void forward(String path) throws RuntimeException {
    ActionContext.getReq().setAttribute("path", path);
    forward();
  }
  
  private static void forward() throws RuntimeException {
    try {
      HttpServletRequest request = ActionContext.getReq();
      HttpServletResponse response = ActionContext.getResp();
      if (response.isCommitted())
        return; 
      String jspPath = null;
      String jspPathParam = request.getParameter("path");
      if (StringUtils.isEmpty(jspPathParam)) {
        Object jspPathObjAttr = request.getAttribute("path");
        if (null == jspPathObjAttr) {
          Object jspPathObjSess = request.getSession().getAttribute("path");
          if (null != jspPathObjSess)
            jspPath = (String)jspPathObjSess; 
        } else {
          jspPath = (String)jspPathObjAttr;
        } 
      } else {
        jspPath = jspPathParam;
      } 
      if (null == jspPath)
        throw new ServletException("程序错误，服务器端跳转页面，没有找到”jspPath“属性！"); 
      request.getRequestDispatcher(jspPath).forward((ServletRequest)request, (ServletResponse)response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static <T> T upload(Class<T> c, UploadFileCall ufc) throws RuntimeException {
    return upload("/upload", c, ufc);
  }
  
  public static <T> T upload(String path, Class<T> c, UploadFileCall ufc) throws RuntimeException {
    try {
      HttpServletRequest request = getReq();
      ServletContext servletContext = getServletContext();
      File file = new File(servletContext.getRealPath(path));
      if (!file.exists())
        file.mkdir(); 
      MultipartRequest multi = new MultipartRequest(request, file.getAbsolutePath(), 1073741824, "UTF-8", (FileRenamePolicy)new DefaultFileRenamePolicy());
      Enumeration<String> params = multi.getParameterNames();
      T obj = c.newInstance();
      Field[] declaredFields = c.getDeclaredFields();
      while (params.hasMoreElements()) {
        String name = params.nextElement();
        for (Field field : declaredFields) {
          field.setAccessible(true);
          String fieldName = field.getName();
          if (name.equals(fieldName)) {
            String value = multi.getParameter(name);
            if (null != value) {
              Class<?> type = field.getType();
              if (type == Long.class) {
                field.set(obj, Long.valueOf(Long.parseLong(value)));
                break;
              } 
              if (type == String.class) {
                field.set(obj, value);
                break;
              } 
              if (type == Byte.class) {
                field.set(obj, Byte.valueOf(Byte.parseByte(value)));
                break;
              } 
              if (type == Integer.class) {
                field.set(obj, Integer.valueOf(Integer.parseInt(value)));
                break;
              } 
              if (type == Character.class) {
                field.set(obj, Character.valueOf(value.charAt(0)));
                break;
              } 
              if (type == Boolean.class) {
                field.set(obj, Boolean.valueOf(Boolean.parseBoolean(value)));
                break;
              } 
              if (type == Double.class) {
                field.set(obj, Double.valueOf(Double.parseDouble(value)));
                break;
              } 
              if (type == Float.class)
                field.set(obj, Float.valueOf(Float.parseFloat(value))); 
              break;
            } 
          } 
        } 
      } 
      ufc.form(obj, multi);
      Enumeration<String> files = multi.getFileNames();
      while (files.hasMoreElements()) {
        String name = files.nextElement();
        String filename = multi.getFilesystemName(name);
        String originalFilename = multi.getOriginalFileName(name);
        String type = multi.getContentType(name);
        File f = multi.getFile(name);
        ufc.file(obj, f, filename, originalFilename, type);
      } 
      return obj;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void upload(String path, UploadFileCall ufc) throws RuntimeException {
    try {
      HttpServletRequest request = getReq();
      ServletContext servletContext = getServletContext();
      File file = new File(servletContext.getRealPath(path));
      if (!file.exists())
        file.mkdirs(); 
      MultipartRequest multi = new MultipartRequest(request, file.getAbsolutePath(), 1073741824, "UTF-8", (FileRenamePolicy)new DefaultFileRenamePolicy());
      Enumeration<String> files = multi.getFileNames();
      while (files.hasMoreElements()) {
        String name = files.nextElement();
        String filename = multi.getFilesystemName(name);
        String originalFilename = multi.getOriginalFileName(name);
        String type = multi.getContentType(name);
        File f = multi.getFile(name);
        ufc.file(null, f, filename, originalFilename, type);
      } 
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void uploadMulti(UploadFileCall ufc) throws RuntimeException {
    uploadMulti("/upload", ufc);
  }
  
  public static void uploadMulti(String path, UploadFileCall ufc) throws RuntimeException {
    try {
      HttpServletRequest request = getReq();
      ServletContext servletContext = getServletContext();
      File file = new File(servletContext.getRealPath(path));
      if (!file.exists())
        file.mkdir(); 
      DiskFileItemFactory fac = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload((FileItemFactory)fac);
      upload.setHeaderEncoding("UTF-8");
      List<FileItem> fileItems = upload.parseRequest(request);
      for (FileItem item : fileItems) {
        if (!item.isFormField()) {
          String name = item.getName();
          String type = item.getContentType();
          if (StringUtils.isNotBlank(name)) {
            File f = new File(file + File.separator + name);
            item.write(f);
            ufc.file(null, f, name, name, type);
          } 
        } 
      } 
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static <T> T uploadMultiAndProgress(String path, Class<T> c, UploadFileNewCall ufc, ProgressListener pl) throws RuntimeException {
    try {
      HttpServletRequest request = getReq();
      ServletContext servletContext = getServletContext();
      File file = new File(servletContext.getRealPath(path));
      if (!file.exists())
        file.mkdir(); 
      DiskFileItemFactory fac = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload((FileItemFactory)fac);
      upload.setHeaderEncoding("UTF-8");
      upload.setProgressListener(pl);
      List<FileItem> fileItems = upload.parseRequest(request);
      T obj = c.newInstance();
      Field[] declaredFields = c.getDeclaredFields();
      for (FileItem item : fileItems) {
        if (!item.isFormField()) {
          String str1 = item.getName();
          String type = item.getContentType();
          if (StringUtils.isNotBlank(str1)) {
            File f = new File(file + File.separator + str1);
            item.write(f);
            ufc.file(obj, f, str1, str1, item.getSize(), type);
          } 
          continue;
        } 
        String name = item.getFieldName();
        for (Field field : declaredFields) {
          field.setAccessible(true);
          String fieldName = field.getName();
          if (name.equals(fieldName)) {
            String value = item.getString("UTF-8");
            if (null != value) {
              Class<?> type = field.getType();
              if (type == Long.class) {
                field.set(obj, Long.valueOf(Long.parseLong(value)));
              } else if (type == String.class) {
                field.set(obj, value);
              } else if (type == Byte.class) {
                field.set(obj, Byte.valueOf(Byte.parseByte(value)));
              } else if (type == Integer.class) {
                field.set(obj, Integer.valueOf(Integer.parseInt(value)));
              } else if (type == Character.class) {
                field.set(obj, Character.valueOf(value.charAt(0)));
              } else if (type == Boolean.class) {
                field.set(obj, Boolean.valueOf(Boolean.parseBoolean(value)));
              } else if (type == Double.class) {
                field.set(obj, Double.valueOf(Double.parseDouble(value)));
              } else if (type == Float.class) {
                field.set(obj, Float.valueOf(Float.parseFloat(value)));
              } 
            } 
          } 
        } 
      } 
      return obj;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static <T> T packBean(Class<T> c) throws RuntimeException {
    try {
      HttpServletRequest request = ActionContext.getReq();
      T newInstance = c.newInstance();
      Field[] fields = c.getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        String name = field.getName();
        String value = request.getParameter(name);
        Class<?> type = field.getType();
        if (type.isArray()) {
          Class<?> componentType = type.getComponentType();
          if (componentType == String.class) {
            String[] values = request.getParameterValues(name);
            field.set(newInstance, values);
          } else if (componentType == File.class) {
            ServletContext servletContext = getServletContext();
            File file = new File(servletContext.getRealPath("upload"));
            if (!file.exists())
              file.mkdir(); 
            MultipartRequest multi = new MultipartRequest(request, file.getAbsolutePath(), 1073741824, "UTF-8", (FileRenamePolicy)new DefaultFileRenamePolicy());
            Enumeration<String> files = multi.getFileNames();
            List<File> fileList = new ArrayList<>();
            if (files.hasMoreElements()) {
              File f = multi.getFile(files.nextElement());
              fileList.add(f);
            } 
            field.set(newInstance, fileList.toArray(new File[0]));
          } 
        } 
        validation(field, name, value);
        if (null != value && !"".equals(value.trim()))
          if (type == Long.class) {
            field.set(newInstance, Long.valueOf(Long.parseLong(value)));
          } else if (type == String.class) {
            field.set(newInstance, value);
          } else if (type == Byte.class) {
            field.set(newInstance, Byte.valueOf(Byte.parseByte(value)));
          } else if (type == Integer.class) {
            field.set(newInstance, Integer.valueOf(Integer.parseInt(value)));
          } else if (type == Character.class) {
            field.set(newInstance, Character.valueOf(value.charAt(0)));
          } else if (type == Boolean.class) {
            field.set(newInstance, Boolean.valueOf(Boolean.parseBoolean(value)));
          } else if (type == Double.class) {
            field.set(newInstance, Double.valueOf(Double.parseDouble(value)));
          } else if (type == Float.class) {
            field.set(newInstance, Float.valueOf(Float.parseFloat(value)));
          } else if (type == Date.class) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            field.set(newInstance, sdf.parse(value));
          }  
      } 
      return newInstance;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  private static void validation(Field field, String name, String value) throws RuntimeException {
    try {
      NotNull notNull = field.<NotNull>getAnnotation(NotNull.class);
      if (null != notNull && (
        null == value || "".equals(value)))
        throw new ValidationException(notNull.message()); 
      if (null == value)
        return; 
      Regexp regexp = field.<Regexp>getAnnotation(Regexp.class);
      if (null != regexp) {
        RegexpType regexpType = regexp.value();
        String regEx = regexpType.getName();
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(value);
        if (!matcher.matches())
          throw new ValidationException(regexp.message()); 
      } 
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static <T> T packBeanRecurrence(Class<T> c) throws RuntimeException {
    return pbRecurrence(c, "");
  }
  
  private static <T> T pbRecurrence(Class<T> c, String prefix) throws RuntimeException {
    try {
      HttpServletRequest request = ActionContext.getReq();
      T newInstance = c.newInstance();
      Field[] declaredFields = c.getDeclaredFields();
      for (Field field : declaredFields) {
        field.setAccessible(true);
        String name = field.getName();
        String key = "".equals(prefix) ? name : (prefix + "." + name);
        String value = request.getParameter(key);
        Class<?> type = field.getType();
        if (type.isArray()) {
          Class<?> componentType = type.getComponentType();
          if (componentType == String.class) {
            String[] values = request.getParameterValues(name);
            field.set(newInstance, values);
          } else if (componentType == File.class) {
            ServletContext servletContext = getServletContext();
            File file = new File(servletContext.getRealPath("upload"));
            if (!file.exists())
              file.mkdir(); 
            MultipartRequest multi = new MultipartRequest(request, file.getAbsolutePath(), 1073741824, "UTF-8", (FileRenamePolicy)new DefaultFileRenamePolicy());
            Enumeration<String> files = multi.getFileNames();
            List<File> fileList = new ArrayList<>();
            if (files.hasMoreElements()) {
              File f = multi.getFile(files.nextElement());
              fileList.add(f);
            } 
            field.set(newInstance, fileList.toArray(new File[0]));
          } 
        } 
        validation(field, name, value);
        if (null != value)
          if (type == Long.class) {
            field.set(newInstance, Long.valueOf(Long.parseLong(value)));
          } else if (type == String.class) {
            field.set(newInstance, value);
          } else if (type == Byte.class) {
            field.set(newInstance, Byte.valueOf(Byte.parseByte(value)));
          } else if (type == Integer.class) {
            field.set(newInstance, Integer.valueOf(Integer.parseInt(value)));
          } else if (type == Character.class) {
            field.set(newInstance, Character.valueOf(value.charAt(0)));
          } else if (type == Boolean.class) {
            field.set(newInstance, Boolean.valueOf(Boolean.parseBoolean(value)));
          } else if (type == Double.class) {
            field.set(newInstance, Double.valueOf(Double.parseDouble(value)));
          } else if (type == Float.class) {
            field.set(newInstance, Float.valueOf(Float.parseFloat(value)));
          } else if (type == Date.class) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            field.set(newInstance, sdf.parse(value));
          } else {
            Object obj = pbRecurrence(field.getType(), name);
            field.set(newInstance, obj);
          }  
      } 
      return newInstance;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void callCtl(String class_method) throws RuntimeException {
    try {
      HttpServletRequest req = ActionContext.getReq();
      HttpServletResponse resp = ActionContext.getResp();
      if (StringUtils.isNotBlank(class_method)) {
        Cache cache = (Cache)SshConstant.CACHE_MAP.get(class_method);
        if (null != cache && !SshConstant.DEBUG_MODE.booleanValue()) {
          cache.getMethod().invoke(cache.getObject(), new Object[] { req, resp });
        } else {
          int lastIndex = class_method.lastIndexOf(".");
          String clazzTemp = class_method.substring(0, lastIndex);
          Class<?> ctlClazz = null;
          for (String packageName : SshConstant.PACKAGE_NAME) {
            try {
              String clazz = (clazzTemp.indexOf(".") == clazzTemp.lastIndexOf(".")) ? (packageName + "." + clazzTemp) : clazzTemp;
              ctlClazz = Class.forName(clazz);
            } catch (Exception e) {}
            if (ctlClazz != null)
              break; 
          } 
          Object ctlObj = ctlClazz.newInstance();
          String method = class_method.substring(++lastIndex);
          Method ctlMet = ctlClazz.getMethod(method, new Class[0]);
          SshConstant.CACHE_MAP.put(class_method, new Cache(ctlObj, ctlMet));
          ctlMet.invoke(ctlObj, new Object[0]);
        } 
      } else {
        ServletOutputStream out = resp.getOutputStream();
        out.print("error");
        out.flush();
        out.close();
      } 
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } 
  }
  
  public static void callCtl(Class clazz, String method) throws RuntimeException {
    callCtl(clazz.getSimpleName() + "." + method);
  }
  
  public static ServletContext getServletContext() {
    return getSession().getServletContext();
  }
  
  public static HttpSession getSession() {
    return ActionContext.getSession();
  }
  
  public static HttpServletRequest getReq() {
    return ActionContext.getReq();
  }
  
  public static HttpServletResponse getResp() {
    return ActionContext.getResp();
  }
  
  public static byte[] toByteArray(String path) throws RuntimeException {
    try {
      return IOUtils.toByteArray(new FileInputStream(getSession().getServletContext().getRealPath("/") + File.separator + path));
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static String getStringTrim(String... key) throws RuntimeException, ValidationException {
    try {
      HttpServletRequest req = getReq();
      String p = req.getParameter(key[0]);
      if (null != p)
        return p.trim(); 
      return p;
    } catch (Exception e) {
      if (key.length == 2)
        throw new ValidationException(key[1]); 
      throw new ValidationException(e);
    }
  }
  
  public static String getString(String... key) throws RuntimeException, ValidationException {
    try {
      HttpServletRequest req = getReq();
      return req.getParameter(key[0]);
    } catch (Exception e) {
      if (key.length == 2)
        throw new ValidationException(key[1]); 
      throw new ValidationException(e);
    }
  }
  
  public static Integer getInteger(String... key) throws RuntimeException, ValidationException {
    try {
      HttpServletRequest req = getReq();
      String attachId = req.getParameter(key[0]);
      return Integer.valueOf(Integer.parseInt(attachId));
    } catch (Exception e) {
      if (key.length == 2)
        throw new ValidationException(key[1]); 
      throw new ValidationException(e);
    }
  }
  
  public static Long getLong(String... key) throws RuntimeException, ValidationException {
    try {
      HttpServletRequest req = getReq();
      String attachId = req.getParameter(key[0]);
      return Long.valueOf(Long.parseLong(attachId));
    } catch (Exception e) {
      if (key.length == 2)
        throw new ValidationException(key[1]); 
      throw new ValidationException(e);
    }
  }
  
  public static Byte getByte(String... key) throws RuntimeException, ValidationException {
    try {
      HttpServletRequest req = getReq();
      String attachId = req.getParameter(key[0]);
      return Byte.valueOf(Byte.parseByte(attachId));
    } catch (Exception e) {
      if (key.length == 2)
        throw new ValidationException(key[1]); 
      throw new ValidationException(e);
    }
  }
  
  public static Short getShort(String... key) throws RuntimeException, ValidationException {
    try {
      HttpServletRequest req = getReq();
      String attachId = req.getParameter(key[0]);
      return Short.valueOf(Short.parseShort(attachId));
    } catch (Exception e) {
      if (key.length == 2)
        throw new ValidationException(key[1]); 
      throw new ValidationException(e);
    }
  }
  
  public static Boolean getBoolean(String... key) throws RuntimeException, ValidationException {
    try {
      HttpServletRequest req = getReq();
      String attachId = req.getParameter(key[0]);
      return Boolean.valueOf(Boolean.parseBoolean(attachId));
    } catch (Exception e) {
      if (key.length == 2)
        throw new ValidationException(key[1]); 
      throw new ValidationException(e);
    }
  }
  
  public static Double getDouble(String... key) throws RuntimeException {
    try {
      HttpServletRequest req = getReq();
      String attachId = req.getParameter(key[0]);
      return Double.valueOf(Double.parseDouble(attachId));
    } catch (Exception e) {
      if (key.length == 2)
        throw new ValidationException(key[1]);
      throw new ValidationException(e);
    }
  }
  
  public static Float getFloat(String... key) throws RuntimeException {
    try {
      HttpServletRequest req = getReq();
      String attachId = req.getParameter(key[0]);
      return Float.valueOf(Float.parseFloat(attachId));
    } catch (Exception e) {
      if (key.length == 2)
        throw new ValidationException(key[1]); 
      throw new ValidationException(e);
    }
  }
  
  public static boolean checkValidateCode(String path) throws RuntimeException {
    try {
      HttpServletRequest req = getReq();
      HttpSession session = getSession();
      String isAutoLogin = req.getParameter("isAutoLogin");
      String uInputAuthCode = req.getParameter("txtAuthCode");
      String uAuthCode = (String)session.getAttribute("ValidCode");
      if (!"true".equals(isAutoLogin)) {
        if (StringUtils.isBlank(uInputAuthCode)) {
          req.setAttribute("errorMsg", "验证码不能为空");
          forward(path);
          return false;
        } 
        if (!StringUtils.isBlank(uAuthCode) && 
          !uInputAuthCode.equalsIgnoreCase(uAuthCode)) {
          req.setAttribute("errorMsg", "验证码不匹配");
          forward(path);
          return false;
        } 
      } 
      return true;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static String getOrder() throws RuntimeException {
    String order = getString(new String[] { "sSortDir_0" });
    return order;
  }
  
  public static String getSort() throws RuntimeException {
    String sort = getString(new String[] { "iSortCol_0" });
    if (StringUtils.isBlank(sort))
      return null; 
    String s = getString(new String[] { "mDataProp_" + sort });
    return s;
  }
  
  public static void prePageForDataTable() throws RuntimeException {
    if (null != getReq()) {
      int pageSize = getInteger(new String[] { "iDisplayLength" }).intValue();
      int pageNum = getInteger(new String[] { "iDisplayStart" }).intValue() / pageSize + 1;
      String order = getOrder();
      String sortColumn = getSort();
      PageHelper.startPage(pageNum, pageSize);
    } else {
      PageHelper.startPage(1, 10);
    } 
  }
}
