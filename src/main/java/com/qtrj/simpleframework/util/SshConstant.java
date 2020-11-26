package com.qtrj.simpleframework.util;

import com.qtrj.simpleframework.ssh.controller.Cache;

import java.util.HashMap;
import java.util.Map;

public class SshConstant {
  public static final Map<String, Cache> CACHE_MAP = new HashMap<>();
  
  public static final Boolean DEBUG_MODE = Boolean.valueOf(true);
  
  public static String[] PACKAGE_NAME = new String[0];
}
