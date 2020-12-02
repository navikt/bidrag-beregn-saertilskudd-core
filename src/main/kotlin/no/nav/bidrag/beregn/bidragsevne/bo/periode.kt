package no.nav.bidrag.beregn.bidragsevne.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.InntektType
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode
import java.math.BigDecimal

data class InntektPeriode(
    val periodeDatoFraTil: Periode,
    val inntektType: InntektType,
    val inntektBelop: BigDecimal) : PeriodisertGrunnlag {
  constructor(inntektPeriode: InntektPeriode) : this(inntektPeriode.periodeDatoFraTil.justerDatoer(), inntektPeriode.inntektType,
      inntektPeriode.inntektBelop)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class SkatteklassePeriode(
    val periodeDatoFraTil: Periode,
    val skatteklasse: Int) : PeriodisertGrunnlag {
  constructor(skatteklassePeriode: SkatteklassePeriode) : this(skatteklassePeriode.periodeDatoFraTil.justerDatoer(),
      skatteklassePeriode.skatteklasse)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class BostatusPeriode(
    val periodeDatoFraTil: Periode,
    val bostatusKode: BostatusKode) : PeriodisertGrunnlag {
  constructor(bostatusPeriode: BostatusPeriode) : this(bostatusPeriode.periodeDatoFraTil.justerDatoer(), bostatusPeriode.bostatusKode)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class AntallBarnIEgetHusholdPeriode(
    val periodeDatoFraTil: Periode,
    val antallBarn: BigDecimal) : PeriodisertGrunnlag {
  constructor(antallBarnIEgetHusholdPeriode: AntallBarnIEgetHusholdPeriode) : this(antallBarnIEgetHusholdPeriode.periodeDatoFraTil.justerDatoer(),
      antallBarnIEgetHusholdPeriode.antallBarn)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class SaerfradragPeriode(
    val periodeDatoFraTil: Periode,
    val saerfradragKode: SaerfradragKode) : PeriodisertGrunnlag {
  constructor(saerfradragPeriode: SaerfradragPeriode) : this(saerfradragPeriode.periodeDatoFraTil.justerDatoer(),
      saerfradragPeriode.saerfradragKode)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}