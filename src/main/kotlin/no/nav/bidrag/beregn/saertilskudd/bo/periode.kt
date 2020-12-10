package no.nav.bidrag.beregn.saertilskudd.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.math.BigDecimal

data class BidragsevnePeriode(
  val periodeDatoFraTil: Periode,
  val bidragsevneBelop: BigDecimal
) : PeriodisertGrunnlag {
  constructor(bidragsevnePeriode: BidragsevnePeriode)
      : this(
    bidragsevnePeriode.periodeDatoFraTil.justerDatoer(),
    bidragsevnePeriode.bidragsevneBelop
  )
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class BPsAndelSaertilskuddPeriode(
    val periodeDatoFraTil: Periode,
    val bPsAndelSaertilskuddProsent: BigDecimal,
    val bPsAndelSaertilskuddBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean) : PeriodisertGrunnlag {
  constructor(bPsAndelSaertilskuddPeriode: BPsAndelSaertilskuddPeriode)
      : this(
      bPsAndelSaertilskuddPeriode.periodeDatoFraTil.justerDatoer(),
      bPsAndelSaertilskuddPeriode.bPsAndelSaertilskuddProsent,
      bPsAndelSaertilskuddPeriode.bPsAndelSaertilskuddBelop,
      bPsAndelSaertilskuddPeriode.barnetErSelvforsorget,
  )
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class LopendeBidragPeriode(
  val periodeDatoFraTil: Periode,
  val barnPersonId: Int,
  val lopendeBidragBelop: BigDecimal,
  val opprinneligBPsAndelUnderholdskostnadBelop: BigDecimal,
  val opprinneligBidragBelop: BigDecimal,
  val opprinneligSamvaersfradragBelop: BigDecimal
) : PeriodisertGrunnlag {
  constructor(lopendeBidragPeriode: LopendeBidragPeriode)
      : this(
    lopendeBidragPeriode.periodeDatoFraTil.justerDatoer(),
    lopendeBidragPeriode.barnPersonId,
    lopendeBidragPeriode.lopendeBidragBelop,
    lopendeBidragPeriode.opprinneligBPsAndelUnderholdskostnadBelop,
    lopendeBidragPeriode.opprinneligBidragBelop,
    lopendeBidragPeriode.opprinneligSamvaersfradragBelop
  )
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class SamvaersfradragGrunnlagPeriode(
  val barnPersonId: Int,
  val periodeDatoFraTil: Periode,
  val samvaersfradragBelop: BigDecimal) : PeriodisertGrunnlag {
  constructor(samvaersfradragGrunnlagPeriode: SamvaersfradragGrunnlagPeriode)
      : this(samvaersfradragGrunnlagPeriode.barnPersonId,
      samvaersfradragGrunnlagPeriode.periodeDatoFraTil.justerDatoer(),
      samvaersfradragGrunnlagPeriode.samvaersfradragBelop)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}
