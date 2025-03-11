package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "POSITION_RECEIPT")
public class PositionReceipt {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "RECEIPT_ID")
  private String receiptId;

  @Column(name = "NOTICE_ID")
  private String noticeId;

  @Column(name = "PA_FISCAL_CODE")
  private String paFiscalCode;

  @Column(name = "CREDITOR_REFERENCE_ID")
  private String creditorReferenceId;

  @Column(name = "PAYMENT_TOKEN")
  private String paymentToken;

  @Column(name = "OUTCOME")
  private String outcome;

  @Column(name = "PAYMENT_AMOUNT")
  private Long paymentAmount;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "COMPANY_NAME")
  private String companyName;

  @Column(name = "OFFICE_NAME")
  private String officeName;

  @Column(name = "DEBTOR_ID")
  private Long debtorId;

  @Column(name = "PSP_ID")
  private String pspId;

  @Column(name = "PSP_FISCAL_CODE")
  private String pspFiscalCode;

  @Column(name = "PSP_VAT_NUMBER")
  private String pspVatNumber;

  @Column(name = "PSP_COMPANY_NAME")
  private String pspCompanyName;

  @Column(name = "CHANNEL_ID")
  private String channelId;

  @Column(name = "CHANNEL_DESCRIPTION")
  private String channelDescription;

  @Column(name = "PAYER_ID")
  private Long payerId;

  @Column(name = "PAYMENT_METHOD")
  private String paymentMethod;

  @Column(name = "FEE")
  private Long fee;

  @Column(name = "PAYMENT_DATE_TIME")
  private LocalDateTime paymentDateTime;

  @Column(name = "APPLICATION_DATE")
  private LocalDateTime applicationDate;

  @Column(name = "METADATA")
  private String metadata;

  @Column(name = "RT_ID")
  private Long rtId;

  @Column(name = "FK_POSITION_PAYMENT")
  private Long fkPositionPayment;
}
