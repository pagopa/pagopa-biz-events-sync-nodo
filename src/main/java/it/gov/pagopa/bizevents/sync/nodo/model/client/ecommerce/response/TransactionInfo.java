package it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response;

import java.time.LocalDateTime;

public class TransactionInfo {

  private LocalDateTime creationDate;

  private String status;

  private String statusDetails;

  private String eventStatus;

  private Long amount;

  private Long fee;

  private Long grandTotal;

  private String rrn;

  private String authorizationCode;

  private String paymentMethodName;

  private String brand;

  private String authorizationRequestId;

  private String paymentGateway;

  private String correlationId;

  private String gatewayAuthorizationStatus;

  private String gatewayErrorCode;
}
