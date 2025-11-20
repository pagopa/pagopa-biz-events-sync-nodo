package it.gov.pagopa.bizevents.sync.nodo.model.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ctDatiSingoloPagamentoRT",
    propOrder = {
      "singoloImportoPagato",
      "esitoSingoloPagamento",
      "dataEsitoSingoloPagamento",
      "identificativoUnivocoRiscossione",
      "causaleVersamento",
      "datiSpecificiRiscossione",
      "commissioniApplicatePSP",
      "commissioniApplicatePA",
      "allegatoRicevuta"
    })
public class CtDatiSingoloPagamentoRT {

  protected BigDecimal singoloImportoPagato;

  protected String esitoSingoloPagamento;

  @XmlSchemaType(name = "date")
  protected XMLGregorianCalendar dataEsitoSingoloPagamento;

  protected String identificativoUnivocoRiscossione;

  protected String causaleVersamento;

  protected String datiSpecificiRiscossione;

  protected BigDecimal commissioniApplicatePSP;

  protected BigDecimal commissioniApplicatePA;

  protected CtAllegatoRicevuta allegatoRicevuta;
}
