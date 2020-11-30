package no.nav.bidrag.beregn.bpsandelsaertilskudd.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.InntektType
import java.math.BigDecimal

data class NettoSaertilskuddPeriode(
    val periodeDatoFraTil: Periode,
    val nettoSaertilskuddBelop: BigDecimal) : PeriodisertGrunnlag {
  constructor(nettoSaertilskuddPeriode: NettoSaertilskuddPeriode)
      : this(nettoSaertilskuddPeriode.periodeDatoFraTil.justerDatoer(),
      nettoSaertilskuddPeriode.nettoSaertilskuddBelop)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class InntektPeriode(
    val periodeDatoFraTil: Periode,
    val inntektType: InntektType,
    val inntektBelop: BigDecimal) : PeriodisertGrunnlag {
  constructor(inntektPeriode: InntektPeriode)
      : this(inntektPeriode.periodeDatoFraTil.justerDatoer(),
      inntektPeriode.inntektType,
      inntektPeriode.inntektBelop)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}
