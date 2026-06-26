package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "POSITION_ACTIVATE")
public class PositionActivate {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "PA_FISCAL_CODE")
  private String paFiscalCode;

  @Column(name = "NOTICE_ID")
  private String noticeNumber;

  @Column(name = "CREDITOR_REFERENCE_ID")
  private String creditorReference;

  @Column(name = "PSP_ID")
  private String idPsp;

  @Column(name = "IDEMPOTENCY_KEY")
  private String idempotencyKey;

  @Column(name = "PAYMENT_TOKEN")
  private String paymentToken;

  @Column(name = "TOKEN_VALID_FROM")
  private LocalDateTime paymentTokenValidFrom;

  @Column(name = "TOKEN_VALID_TO")
  private LocalDateTime paymentTokenValidTo;

  @Column(name = "DUE_DATE")
  private LocalDateTime dueDate;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @Column(name = "INSERTED_TIMESTAMP")
  private LocalDateTime insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private LocalDateTime updatedTimestamp;

  @Column(name = "INSERTED_BY")
  private String insertedBy;

  @Column(name = "UPDATED_BY")
  private String updatedBy;

  @Column(name = "PAYMENT_NOTE")
  private String paymentNote;

  @Column(name = "PAYMENT_METHOD")
  private String paymentMethod;

  @Column(name = "TOUCHPOINT")
  private String touchpoint;

  @Column(name = "SUGGESTED_IDBUNDLE")
  private String suggestedIdBundle;

  @Column(name = "SUGGESTED_IDCIBUNDLE")
  private String suggestedIdCiBundle;

  @Column(name = "SUGGESTED_USER_FEE")
  private BigDecimal suggestedUserFee;

  @Column(name = "SUGGESTED_PA_FEE")
  private BigDecimal suggestedPaFee;
}