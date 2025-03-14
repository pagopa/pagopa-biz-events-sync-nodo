package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.rt;

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
    name = "ctSoggettoVersante",
    propOrder = {
      "identificativoUnivocoVersante",
      "anagraficaVersante",
      "indirizzoVersante",
      "civicoVersante",
      "capVersante",
      "localitaVersante",
      "provinciaVersante",
      "nazioneVersante",
      "eMailVersante"
    })
public class CtSoggettoVersante {

  protected CtIdentificativoUnivocoPersonaFG identificativoUnivocoVersante;

  protected String anagraficaVersante;

  protected String indirizzoVersante;

  protected String civicoVersante;

  protected String capVersante;

  protected String localitaVersante;

  protected String provinciaVersante;

  protected String nazioneVersante;

  @XmlElement(name = "e-mailVersante")
  protected String eMailVersante;
}
