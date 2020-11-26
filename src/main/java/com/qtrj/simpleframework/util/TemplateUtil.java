package com.qtrj.simpleframework.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.ResourceLoader;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

public class TemplateUtil {
  private static GroupTemplate gt;
  
  static {
    StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
    Configuration cfg = null;
    try {
      cfg = Configuration.defaultConfiguration();
    } catch (IOException e) {
      e.printStackTrace();
    } 
    gt = new GroupTemplate((ResourceLoader)resourceLoader, cfg);
  }
  
  public static String makeHtml(String template, Map root) {
    Template t = gt.getTemplate(template);
    t.binding(root);
    return t.render();
  }
  
  public static String makeHtml(String template, List<Map> list) {
    Template t = gt.getTemplate(template);
    Map<Object, Object> m = new HashMap<>();
    m.put("list", list);
    t.binding(m);
    return t.render();
  }
}
