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
    val soknadsbarnPersonId: Int,
    val periodeDatoFraTil: Periode,
    val bPsAndelSaertilskuddProsent: BigDecimal,
    val bPsAndelSaertilskuddBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean) : PeriodisertGrunnlag {
  constructor(bPsAndelSaertilskuddPeriode: BPsAndelSaertilskuddPeriode)
      : this(bPsAndelSaertilskuddPeriode.soknadsbarnPersonId,
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
    val soknadsbarnPersonId: Int,
    val periodeDatoFraTil: Periode,
    val lopendeBidragBelop: BigDecimal,
    val resultatkode: ResultatKode) : PeriodisertGrunnlag {
  constructor(lopendeBidragPeriode: LopendeBidragPeriode)
      : this(lopendeBidragPeriode.soknadsbarnPersonId,
      lopendeBidragPeriode.periodeDatoFraTil.justerDatoer(),
      lopendeBidragPeriode.lopendeBidragBelop,
      lopendeBidragPeriode.resultatkode)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}

data class SamvaersfradragPeriode(
    val soknadsbarnPersonId: Int,
    val periodeDatoFraTil: Periode,
    val samvaersfradragBelop: BigDecimal) : PeriodisertGrunnlag {
  constructor(samvaersfradragPeriode: SamvaersfradragPeriode)
      : this(samvaersfradragPeriode.soknadsbarnPersonId,
      samvaersfradragPeriode.periodeDatoFraTil.justerDatoer(),
      samvaersfradragPeriode.samvaersfradragBelop)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}
