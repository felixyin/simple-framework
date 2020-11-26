package com.qtrj.simpleframework.ssh.repository.jdbc;

public class JdbcException extends Exception {
  public JdbcException(String msg) {
    super("fy jdbc exception : " + msg);
  }
  
  public JdbcException(String message, Throwable cause) {
    super("fy jdbc exception : " + message, cause);
  }
}
