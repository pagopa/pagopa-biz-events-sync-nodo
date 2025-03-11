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
@Table(name = "RT")
public class RT {

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

  @Column(name = "COD_ESITO")
  private int codEsito;

  @Column(name = "ESITO")
  private String esito;

  @Column(name = "DATA_RICEVUTA")
  private LocalDateTime dataRicevuta;

  @Column(name = "DATA_RICHIESTA")
  private LocalDateTime dataRichiesta;

  @Column(name = "ID_RICEVUTA")
  private String idRicevuta;

  @Column(name = "ID_RICHIESTA")
  private String idRichiesta;

  @Column(name = "SOMMA_VERSAMENTI")
  private long sommaVersamenti;

  @Column(name = "INSERTED_TIMESTAMP")
  private LocalDateTime insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private LocalDateTime updatedTimestamp;

  @Column(name = "CANALE")
  private String canale;

  @Column(name = "NOTIFICA_PROCESSATA")
  private String notificaProcessata;

  @Column(name = "GENERATA_DA")
  private String generataDa;
}
