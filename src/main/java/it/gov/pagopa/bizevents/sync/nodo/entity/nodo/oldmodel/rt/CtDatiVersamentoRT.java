package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.List;
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
    name = "ctDatiVersamentoRT",
    propOrder = {
      "codiceEsitoPagamento",
      "importoTotalePagato",
      "identificativoUnivocoVersamento",
      "codiceContestoPagamento",
      "datiSingoloPagamento"
    })
public class CtDatiVersamentoRT {

  protected String codiceEsitoPagamento;

  protected BigDecimal importoTotalePagato;

  protected String identificativoUnivocoVersamento;

  @XmlElement(name = "CodiceContestoPagamento")
  protected String codiceContestoPagamento;

  protected List<CtDatiSingoloPagamentoRT> datiSingoloPagamento;
}
