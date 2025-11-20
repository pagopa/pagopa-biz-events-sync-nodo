package it.gov.pagopa.bizevents.sync.nodo.model.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
    name = "ctIstitutoAttestante",
    propOrder = {
      "identificativoUnivocoAttestante",
      "denominazioneAttestante",
      "codiceUnitOperAttestante",
      "denomUnitOperAttestante",
      "indirizzoAttestante",
      "civicoAttestante",
      "capAttestante",
      "localitaAttestante",
      "provinciaAttestante",
      "nazioneAttestante"
    })
public class CtIstitutoAttestante {

  protected CtIdentificativoUnivoco identificativoUnivocoAttestante;

  protected String denominazioneAttestante;

  protected String codiceUnitOperAttestante;

  protected String denomUnitOperAttestante;

  protected String indirizzoAttestante;

  protected String civicoAttestante;

  protected String capAttestante;

  protected String localitaAttestante;

  protected String provinciaAttestante;

  protected String nazioneAttestante;
}
