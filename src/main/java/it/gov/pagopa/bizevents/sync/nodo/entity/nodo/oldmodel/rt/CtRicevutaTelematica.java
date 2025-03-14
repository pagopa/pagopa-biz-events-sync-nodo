package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
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
    name = "ctRicevutaTelematica",
    propOrder = {
      "versioneOggetto",
      "dominio",
      "identificativoMessaggioRicevuta",
      "dataOraMessaggioRicevuta",
      "riferimentoMessaggioRichiesta",
      "riferimentoDataRichiesta",
      "istitutoAttestante",
      "enteBeneficiario",
      "soggettoVersante",
      "soggettoPagatore",
      "datiPagamento"
    })
public class CtRicevutaTelematica {

  protected String versioneOggetto;

  protected CtDominio dominio;

  protected String identificativoMessaggioRicevuta;

  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar dataOraMessaggioRicevuta;

  protected String riferimentoMessaggioRichiesta;

  @XmlSchemaType(name = "date")
  protected XMLGregorianCalendar riferimentoDataRichiesta;

  protected CtIstitutoAttestante istitutoAttestante;

  protected CtEnteBeneficiario enteBeneficiario;

  protected CtSoggettoVersante soggettoVersante;

  protected CtSoggettoPagatore soggettoPagatore;

  protected CtDatiVersamentoRT datiPagamento;
}
