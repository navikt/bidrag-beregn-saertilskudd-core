package no.nav.bidrag.beregn.saertilskudd.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal

data class BidragsevnePeriode(
    val periodeDatoFraTil: Periode,
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal) : PeriodisertGrunnlag {
  constructor(bidragsevnePeriode: BidragsevnePeriode)
      : this(bidragsevnePeriode.periodeDatoFraTil.justerDatoer(),
      bidragsevnePeriode.bidragsevneBelop,
      bidragsevnePeriode.tjuefemProsentInntekt)
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
    val lopendeBidragBelop: BigDecimal,
    val opprinneligBPsAndelUnderholdskostnadBelop: BigDecimal,
    val opprinneligBidragBelop: BigDecimal,
    val opprinneligSamvaersfradragBelop: BigDecimal,
    val resultatkode: ResultatKode) : PeriodisertGrunnlag {
  constructor(lopendeBidragPeriode: LopendeBidragPeriode)
      : this(lopendeBidragPeriode.periodeDatoFraTil.justerDatoer(),
      lopendeBidragPeriode.lopendeBidragBelop,
      lopendeBidragPeriode.opprinneligBPsAndelUnderholdskostnadBelop,
      lopendeBidragPeriode.opprinneligBidragBelop,
      lopendeBidragPeriode.opprinneligSamvaersfradragBelop,
      lopendeBidragPeriode.resultatkode)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class SamvaersfradragPeriode(
    val periodeDatoFraTil: Periode,
    val samvaersfradragBelop: BigDecimal) : PeriodisertGrunnlag {
  constructor(samvaersfradragPeriode: SamvaersfradragPeriode)
      : this(samvaersfradragPeriode.periodeDatoFraTil.justerDatoer(),
      samvaersfradragPeriode.samvaersfradragBelop)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}
