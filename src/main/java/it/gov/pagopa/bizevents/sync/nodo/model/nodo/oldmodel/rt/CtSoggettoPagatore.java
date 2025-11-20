package it.gov.pagopa.bizevents.sync.nodo.model.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
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
    name = "ctSoggettoPagatore",
    propOrder = {
      "identificativoUnivocoPagatore",
      "anagraficaPagatore",
      "indirizzoPagatore",
      "civicoPagatore",
      "capPagatore",
      "localitaPagatore",
      "provinciaPagatore",
      "nazionePagatore",
      "eMailPagatore"
    })
public class CtSoggettoPagatore {

  protected CtIdentificativoUnivocoPersonaFG identificativoUnivocoPagatore;

  protected String anagraficaPagatore;

  protected String indirizzoPagatore;

  protected String civicoPagatore;

  protected String capPagatore;

  protected String localitaPagatore;

  protected String provinciaPagatore;

  protected String nazionePagatore;

  @XmlElement(name = "e-mailPagatore")
  protected String eMailPagatore;
}
