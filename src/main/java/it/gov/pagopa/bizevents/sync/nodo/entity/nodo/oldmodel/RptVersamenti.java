package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RPT_VERSAMENTI")
public class RptVersamenti {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "PROGRESSIVO")
  private Long progressivo;

  @Column(name = "IMPORTO")
  private Double importo;

  @Column(name = "COMMISSIONE_CARICO_PA")
  private Double commissioneCaricoPa;

  @Column(name = "IBAN")
  private String iban;

  @Column(name = "IBAN_APPOGGIO")
  private String ibanAppoggio;

  @Column(name = "BIC_ACCREDITO")
  private String bicAccredito;

  @Column(name = "BIC_APPOGGIO")
  private String bicAppoggio;

  @Column(name = "CREDENZIALI_PAGATORE")
  private String credenzialiPagatore;

  @Column(name = "CAUSALE_VERSAMENTO")
  private String causaleVersamento;

  @Column(name = "TIPO_VERSAMENTO")
  private String tipoVersamento;

  @Column(name = "DATI_SPECIFICI_RISCOSSIONE")
  private String datiSpecificiRiscossione;

  @Column(name = "FK_RPT")
  private Long fkRpt;

  @Column(name = "INSERTED_TIMESTAMP")
  private Timestamp insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private Timestamp updatedTimestamp;
}
