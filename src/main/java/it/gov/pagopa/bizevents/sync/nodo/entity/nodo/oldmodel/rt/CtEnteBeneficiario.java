package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.rt;

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
    name = "ctEnteBeneficiario",
    propOrder = {
      "identificativoUnivocoBeneficiario",
      "denominazioneBeneficiario",
      "codiceUnitOperBeneficiario",
      "denomUnitOperBeneficiario",
      "indirizzoBeneficiario",
      "civicoBeneficiario",
      "capBeneficiario",
      "localitaBeneficiario",
      "provinciaBeneficiario",
      "nazioneBeneficiario"
    })
public class CtEnteBeneficiario {

  protected CtIdentificativoUnivocoPersonaG identificativoUnivocoBeneficiario;

  protected String denominazioneBeneficiario;

  protected String codiceUnitOperBeneficiario;

  protected String denomUnitOperBeneficiario;

  protected String indirizzoBeneficiario;

  protected String civicoBeneficiario;

  protected String capBeneficiario;

  protected String localitaBeneficiario;

  protected String provinciaBeneficiario;

  protected String nazioneBeneficiario;
}
