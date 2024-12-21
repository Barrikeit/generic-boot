package org.barrikeit.config.security.util;

import org.barrikeit.config.security.model.domain.AuditInfo;

public final class AuditHolder {

  private static final ThreadLocal<AuditInfo> auditThreadLocal = new ThreadLocal<>();

  public static AuditInfo getAuditInfo() {
    return auditThreadLocal.get();
  }

  public static void setAuditInfo(AuditInfo auditInfo) {
    auditThreadLocal.set(auditInfo);
  }

  public static void clear() {
    auditThreadLocal.remove();
  }
}
