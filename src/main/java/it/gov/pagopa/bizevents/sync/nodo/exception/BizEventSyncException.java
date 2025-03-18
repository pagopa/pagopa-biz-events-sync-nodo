package it.gov.pagopa.bizevents.sync.nodo.exception;

public class BizEventSyncException extends RuntimeException {

  private final String customMessage;

  public BizEventSyncException(String msg, Throwable e) {
    super(e);
    this.customMessage = msg;
  }

  public String getCustomMessage() {
    return this.customMessage;
  }
}
