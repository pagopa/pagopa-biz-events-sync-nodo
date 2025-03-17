package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RPT_SOGGETTI")
public class RptSoggetti {

  @EmbeddedId private RptSoggettiId id;

  @Column(name = "TIPO_IDENTIFICATIVO_UNIVOCO")
  private String tipoIdentificativoUnivoco;

  @Column(name = "CODICE_IDENTIFICATIVO_UNIVOCO")
  private String codiceIdentificativoUnivoco;

  @Column(name = "ANAGRAFICA")
  private String anagrafica;

  @Column(name = "INDIRIZZO")
  private String indirizzo;

  @Column(name = "CIVICO")
  private String civico;

  @Column(name = "CAP")
  private String cap;

  @Column(name = "LOCALITA")
  private String localita;

  @Column(name = "PROVINCIA")
  private String provincia;

  @Column(name = "NAZIONE")
  private String nazione;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "CODICE_UNITOPER")
  private String codiceUnitoper;

  @Column(name = "DENOMIN_UNITOPER")
  private String denominUnitoper;

  @Column(name = "INSERTED_TIMESTAMP")
  private Timestamp insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private Timestamp updatedTimestamp;
}
