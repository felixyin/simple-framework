package com.qtrj.simpleframework.util;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import java.io.File;
import java.util.Collection;

public class MimeTypeUtil {
  static {
    MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
  }
  
  public static String getMimeTypeString(File f) {
    Collection<MimeType> mimeTypes = MimeUtil.getMimeTypes(f);
    MimeType mt = mimeTypes.iterator().next();
    return mt.toString();
  }
}
