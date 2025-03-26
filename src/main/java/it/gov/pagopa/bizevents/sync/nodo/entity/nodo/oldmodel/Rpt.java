package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel;

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
@Table(name = "RPT")
public class Rpt {

  @Id
  @Column(name = "ID")
  private long id;

  @Column(name = "ID_SESSIONE")
  private String idSessione;

  @Column(name = "CCP")
  private String ccp;

  @Column(name = "IDENT_DOMINIO")
  private String identDominio;

  @Column(name = "IUV")
  private String iuv;

  @Column(name = "BIC_ADDEBITO")
  private String bicAddebito;

  @Column(name = "DATA_MSG_RICH")
  private LocalDateTime dataMsgRich;

  @Column(name = "FLAG_CANC")
  private String flagCanc;

  @Column(name = "IBAN_ADDEBITO")
  private String ibanAddebito;

  @Column(name = "ID_MSG_RICH")
  private String idMsgRich;

  @Column(name = "STAZ_INTERMEDIARIOPA")
  private String stazIntermediariopa;

  @Column(name = "INTERMEDIARIOPA")
  private String intermediariopa;

  @Column(name = "CANALE")
  private String canale;

  @Column(name = "PSP")
  private String psp;

  @Column(name = "INTERMEDIARIOPSP")
  private String intermediarioPsp;

  @Column(name = "TIPO_VERSAMENTO")
  private String tipoVersamento;

  @Column(name = "NUM_VERSAMENTI")
  private long numVersamenti;

  @Column(name = "RT_SIGNATURE_CODE")
  private long rtSignatureCode;

  @Column(name = "SOMMA_VERSAMENTI")
  private long sommaVersamenti;

  @Column(name = "PARAMETRI_PROFILO_PAGAMENTO")
  private String parametriProfiloPagamento;

  @Column(name = "FK_CARRELLO")
  private long fkCarrello;

  @Column(name = "INSERTED_TIMESTAMP")
  private LocalDateTime insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private LocalDateTime updatedTimestamp;

  @Column(name = "RICEVUTA_PM")
  private String ricevutaPm;

  @Column(name = "WISP_2")
  private String wisp2;

  @Column(name = "FLAG_SECONDA")
  private String flagSeconda;

  @Column(name = "FLAG_IO")
  private String flagIo;
}
