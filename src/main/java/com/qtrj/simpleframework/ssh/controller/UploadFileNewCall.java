package com.qtrj.simpleframework.ssh.controller;

import com.oreilly.servlet.MultipartRequest;
import java.io.File;

public abstract class UploadFileNewCall {
  public abstract <T> void file(T paramT, File paramFile, String paramString1, String paramString2, long paramLong, String paramString3) throws Exception;
  
  public <T> void form(T obj, MultipartRequest multipartRequest) throws Exception {}
}
