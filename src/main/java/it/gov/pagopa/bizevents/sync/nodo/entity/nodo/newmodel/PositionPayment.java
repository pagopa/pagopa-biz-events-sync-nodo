package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "POSITION_PAYMENT")
public class PositionPayment {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "PA_FISCAL_CODE")
  private String paFiscalCode;

  @Column(name = "NOTICE_ID")
  private String noticeId;

  @Column(name = "CREDITOR_REFERENCE_ID")
  private String creditorReferenceId;

  @Column(name = "PAYMENT_TOKEN")
  private String paymentToken;

  @Column(name = "BROKER_PA_ID")
  private String brokerPaId;

  @Column(name = "STATION_ID")
  private String stationId;

  @Column(name = "STATION_VERSION")
  private Integer stationVersion;

  @Column(name = "PSP_ID")
  private String pspId;

  @Column(name = "BROKER_PSP_ID")
  private String brokerPspId;

  @Column(name = "CHANNEL_ID")
  private String channelId;

  @Column(name = "IDEMPOTENCY_KEY")
  private String idempotencyKey;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @Column(name = "FEE")
  private BigDecimal fee;

  @Column(name = "OUTCOME")
  private String outcome;

  @Column(name = "PAYMENT_METHOD")
  private String paymentMethod;

  @Column(name = "PAYMENT_CHANNEL")
  private String paymentChannel;

  @Column(name = "TRANSFER_DATE")
  private LocalDateTime transferDate;

  @Column(name = "APPLICATION_DATE")
  private LocalDateTime applicationDate;

  @Column(name = "INSERTED_TIMESTAMP")
  private LocalDateTime insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private LocalDateTime updatedTimestamp;

  @Column(name = "RPT_ID")
  private Long rptId;

  @Column(name = "PAYMENT_TYPE")
  private String paymentType;

  @Column(name = "CARRELLO_ID")
  private Long carrelloId;

  @Column(name = "ORIGINAL_PAYMENT_TOKEN")
  private String originalPaymentToken;

  @Column(name = "FLAG_IO")
  private String flagIo;

  @Column(name = "RICEVUTA_PM")
  private String ricevutaPm;

  @Column(name = "FLAG_ACTIVATE_RESP_MISSING")
  private String flagActivateRespMissing;

  @Column(name = "FLAG_PAYPAL")
  private String flagPaypal;

  @Column(name = "INSERTED_BY")
  private String insertedBy;

  @Column(name = "UPDATED_BY")
  private String updatedBy;

  @Column(name = "TRANSACTION_ID")
  private String transactionId;

  @Column(name = "CLOSE_VERSION")
  private String closeVersion;

  @Column(name = "FEE_PA")
  private BigDecimal feePa;

  @Column(name = "BUNDLE_ID")
  private String bundleId;

  @Column(name = "BUNDLE_PA_ID")
  private String bundlePaId;

  @Lob
  @Column(name = "PM_INFO", columnDefinition = "BYTEA")
  private byte[] pmInfo;

  @Column(name = "MBD")
  private String mbd;

  @Column(name = "FEE_SPO")
  private BigDecimal feeSpo;

  @Column(name = "PAYMENT_NOTE")
  private String paymentNote;

  @Column(name = "FLAG_STANDIN")
  private String flagStandin;

  @OneToOne
  @JoinColumn(name = "FK_PAYMENT_PLAN", referencedColumnName = "ID")
  private PositionPaymentPlan paymentPlan;

  @OneToOne
  @JoinColumn(name = "PAYER_ID", referencedColumnName = "ID")
  private PositionSubject payer;
}
