package com.qtrj.simpleframework.ssh.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SetCharacterEncodingFilter implements Filter {
  private String encoding;
  
  private String contentType;
  
  public void init(FilterConfig filterConfig) throws ServletException {
    this.encoding = filterConfig.getServletContext().getInitParameter("encoding");
    this.contentType = filterConfig.getServletContext().getInitParameter("contentType");
  }
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    request.setCharacterEncoding(this.encoding);
    response.setContentType(this.contentType);
    response.setCharacterEncoding(this.encoding);
    chain.doFilter(request, response);
  }
  
  public void destroy() {}
}
