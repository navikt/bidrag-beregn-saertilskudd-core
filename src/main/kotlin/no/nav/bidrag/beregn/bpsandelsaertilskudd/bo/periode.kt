package no.nav.bidrag.beregn.bpsandelsaertilskudd.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.InntektType
import java.math.BigDecimal

data class InntektPeriode(
    val inntektDatoFraTil: Periode,
    val inntektType: InntektType,
    val inntektBelop: BigDecimal) : PeriodisertGrunnlag {
  constructor(inntektPeriode: InntektPeriode)
      : this(inntektPeriode.inntektDatoFraTil.justerDatoer(),
      inntektPeriode.inntektType,
      inntektPeriode.inntektBelop)
  override fun getDatoFraTil(): Periode {
    return inntektDatoFraTil
  }
}
