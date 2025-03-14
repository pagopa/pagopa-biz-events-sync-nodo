package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RT_VERSAMENTI")
public class RtVersamenti {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "PROGRESSIVO")
  private Long progressivo;

  @Column(name = "IMPORTO_RT")
  private Double importoRt;

  @Column(name = "ESITO")
  private String esito;

  @Column(name = "CAUSALE_VERSAMENTO")
  private String causaleVersamento;

  @Column(name = "DATI_SPECIFICI_RISCOSSIONE")
  private String datiSpecificiRiscossione;

  @Column(name = "COMMISSIONE_APPLICATE_PSP")
  private Double commissioneApplicatePsp;

  @Column(name = "INSERTED_TIMESTAMP")
  private Timestamp insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private Timestamp updatedTimestamp;

  @Column(name = "COMMISSIONE_CARICO_PA")
  private Double commissioneCaricoPa;

  @Column(name = "COMMISSIONE_APPLICATE_PA")
  private Double commissioneApplicatePa;

  @OneToOne
  @JoinColumn(name = "FK_RT", referencedColumnName = "ID")
  private Rt rt;
}
