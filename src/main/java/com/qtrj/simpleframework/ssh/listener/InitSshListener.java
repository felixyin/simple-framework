package com.qtrj.simpleframework.ssh.listener;

import com.qtrj.simpleframework.ssh.repository.mybatis.MybatisSessionFactory;
import com.qtrj.simpleframework.util.PropertiesUtil;
import com.qtrj.simpleframework.util.SshConstant;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class InitSshListener implements ServletContextListener {
  private static final Log log = LogFactory.getLog(InitSshListener.class);
  
  public void contextInitialized(final ServletContextEvent servletContextEvent) {
    PropertiesUtil.initExtiact("/usr/local/etc/topzhuxue/zhuxue.cfg");
    initControllerSettings(servletContextEvent);
    (new Thread(new Runnable() {
          public void run() {
            InitSshListener.this.initFileMonitorForDao(servletContextEvent);
          }
        })).start();
    (new Thread(new Runnable() {
          public void run() {
            InitSshListener.this.initFileMonitorForService(servletContextEvent);
          }
        })).start();
    initMybatis(servletContextEvent);
  }
  
  private void initControllerSettings(ServletContextEvent servletContextEvent) {
    String controllerPackage = servletContextEvent.getServletContext().getInitParameter("controller_package");
    if (null == controllerPackage || "".equals(controllerPackage)) {
      log.error("控制层包路径没有配置，进程退出！");
      System.exit(1);
    } 
    SshConstant.PACKAGE_NAME = controllerPackage.split(",");
  }
  
  private void initFileMonitorForService(ServletContextEvent servletContextEvent) {
    String debug = servletContextEvent.getServletContext().getInitParameter("debug");
    if (null == debug || !"true".equalsIgnoreCase(debug))
      return; 
    String monitor_service_path = servletContextEvent.getServletContext().getInitParameter("monitor_service_path");
    if (null == monitor_service_path || "".equals(monitor_service_path))
      log.error("文件监听初始化失败！"); 
    String monitor_service_factory_path = servletContextEvent.getServletContext().getInitParameter("monitor_service_factory_path");
    if (null == monitor_service_factory_path || "".equals(monitor_service_factory_path))
      log.error("文件监听初始化失败！"); 
    String folder = monitor_service_path;
    String target = monitor_service_factory_path;
    try {
      writeServiceFactory(folder, target);
      Path myDir = Paths.get(folder, new String[0]);
      WatchService watcher = myDir.getFileSystem().newWatchService();
      myDir.register(watcher, (WatchEvent.Kind<?>[])new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY });
      while (true)
        writeServiceFactory(folder, target); 
    } catch (Exception e) {
      log.error("监听文件变化时出现异常", e);
      return;
    } 
  }
  
  private void writeServiceFactory(String folder, String target) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("package com.hzc.top.util;\n\nimport com.tangiatech.lms.service.*;\n\n/**\n * @author yinbin\n */\n\npublic class ServiceFactory {\n\n\tpublic static <T> T get(Class<T> t) {\n\t\treturn ServiceFactory.getInstance(t);\n\t}\n\n");
    File javaFolder = new File(folder);
    File[] listFiles = javaFolder.listFiles(new FilenameFilter() {
          public boolean accept(File dir, String name) {
            if (name.endsWith("Service.java"))
              return true; 
            return false;
          }
        });
    for (File file1 : listFiles) {
      String name = file1.getName().replace(".java", "");
      String javaMethod = "\tpublic static " + name + " " + StringUtils.uncapitalize(name) + "(){\n\t\treturn get(" + name + ".class);\n\t}\n";
      sb.append(javaMethod);
    } 
    sb.append("\n}\n");
    File file = new File(target);
    if (!file.exists())
      file.createNewFile(); 
    FileUtils.write(file, sb.toString(), "UTF-8");
  }
  
  private void initFileMonitorForDao(ServletContextEvent servletContextEvent) {
    String debug = servletContextEvent.getServletContext().getInitParameter("debug");
    if (null == debug || !"true".equalsIgnoreCase(debug))
      return; 
    String monitor_dao_path = servletContextEvent.getServletContext().getInitParameter("monitor_dao_path");
    String monitor_dao_factory_path = servletContextEvent.getServletContext().getInitParameter("monitor_dao_factory_path");
    String folder = monitor_dao_path;
    String target = monitor_dao_factory_path;
    try {
      writeDaoFactory(folder, target);
      Path myDir = Paths.get(folder, new String[0]);
      WatchService watcher = myDir.getFileSystem().newWatchService();
      myDir.register(watcher, (WatchEvent.Kind<?>[])new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY });
      while (true)
        writeDaoFactory(folder, target); 
    } catch (Exception e) {
      log.error("监听dao发生异常", e);
      return;
    } 
  }
  
  private void writeDaoFactory(String folder, String target) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("package com.hzc.top.util;\n\nimport Context;\nimport com.tangiatech.lms.dao.*;\n\n/**\n * Created by YinBin on 14-4-21.\n */\npublic class DaoFactory {\n\n\tpublic static <T> T get(Class<T> t) {\n\t\treturn Context.getSqlSession().getMapper(t);\n\t}\n\n");
    File javaFolder = new File(folder);
    File[] listFiles = javaFolder.listFiles(new FilenameFilter() {
          public boolean accept(File dir, String name) {
            if (name.endsWith("Mapper.java"))
              return true; 
            return false;
          }
        });
    for (File file1 : listFiles) {
      String name = file1.getName().replace(".java", "");
      String javaMethod = "\tpublic static " + name + " " + StringUtils.uncapitalize(name) + "(){\n\t\treturn get(" + name + ".class);\n\t}\n";
      sb.append(javaMethod);
    } 
    sb.append("\n}\n");
    File file = new File(target);
    if (!file.exists())
      file.createNewFile(); 
    FileUtils.write(file, sb.toString(), "UTF-8");
  }
  
  private void initMybatis(ServletContextEvent servletContextEvent) {
    SqlSessionFactory dbFactory = null;
    try {
      String commonConfigPath = servletContextEvent.getServletContext().getInitParameter("mybatis_config_path");
      if (null != commonConfigPath && !"".equals(commonConfigPath)) {
        InputStream is = Resources.getResourceAsStream(commonConfigPath);
        dbFactory = (new SqlSessionFactoryBuilder()).build(is);
        MybatisSessionFactory.putFactory(dbFactory);
      } 
      if (dbFactory == null)
        log.error("文件监听初始化失败！"); 
    } catch (Exception e) {
      log.error("数据库初始化失败，进程退出，请检查您的配置文件！", e);
      System.exit(1);
    } 
  }
  
  public void contextDestroyed(ServletContextEvent servletContextEvent) {}
}
