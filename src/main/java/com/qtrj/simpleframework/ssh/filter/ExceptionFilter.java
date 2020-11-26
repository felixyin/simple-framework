package com.qtrj.simpleframework.ssh.filter;

import com.qtrj.simpleframework.ssh.controller.ActionContext;
import com.qtrj.simpleframework.ssh.controller.WebUtil;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExceptionFilter implements Filter {
  Log log = LogFactory.getLog(getClass());
  
  public void init(FilterConfig filterConfig) throws ServletException {}
  
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    try {
      filterChain.doFilter(servletRequest, servletResponse);
    } catch (Exception e) {
      this.log.error(e.getMessage(), e);
      e.printStackTrace();
      WebUtil.writeJson(Boolean.valueOf(false), (String)ActionContext.get("ValidationError", ""));
    } 
  }
  
  public void destroy() {}
}
