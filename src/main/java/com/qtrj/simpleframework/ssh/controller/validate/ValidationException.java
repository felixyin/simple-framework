package com.qtrj.simpleframework.ssh.controller.validate;

import com.qtrj.simpleframework.ssh.controller.ActionContext;

public class ValidationException extends RuntimeException{
  private static final long serialVersionUID = 1L;
  
  public ValidationException(String message) {
    super(message);
    ActionContext.set("ValidationError", message);
  }
  
  public ValidationException(Throwable exception) {
    super(exception);
  }
  
  public ValidationException(String message, Throwable exception) {
    super(message, exception);
  }
}
