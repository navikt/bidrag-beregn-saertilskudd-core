package no.nav.bidrag.beregn.saertilskudd.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal

data class BidragsevnePeriode(
    val bidragsevneDatoFraTil: Periode,
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal) : PeriodisertGrunnlag {
  constructor(bidragsevnePeriode: BidragsevnePeriode)
      : this(bidragsevnePeriode.bidragsevneDatoFraTil.justerDatoer(),
      bidragsevnePeriode.bidragsevneBelop,
      bidragsevnePeriode.tjuefemProsentInntekt)
  override fun getDatoFraTil(): Periode {
    return bidragsevneDatoFraTil
  }
}

data class BPsAndelSaertilskuddPeriode(
    val soknadsbarnPersonId: Int,
    val bPsAndelSaertilskuddDatoFraTil: Periode,
    val bPsAndelSaertilskuddProsent: BigDecimal,
    val bPsAndelSaertilskuddBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean) : PeriodisertGrunnlag {
  constructor(bPsAndelSaertilskuddPeriode: BPsAndelSaertilskuddPeriode)
      : this(bPsAndelSaertilskuddPeriode.soknadsbarnPersonId,
      bPsAndelSaertilskuddPeriode.bPsAndelSaertilskuddDatoFraTil.justerDatoer(),
      bPsAndelSaertilskuddPeriode.bPsAndelSaertilskuddProsent,
      bPsAndelSaertilskuddPeriode.bPsAndelSaertilskuddBelop,
      bPsAndelSaertilskuddPeriode.barnetErSelvforsorget,
  )
  override fun getDatoFraTil(): Periode {
    return bPsAndelSaertilskuddDatoFraTil
  }
}

data class LopendeBidragPeriode(
    val soknadsbarnPersonId: Int,
    val lopendeBidragDatoFraTil: Periode,
    val lopendeBidragBelop: BigDecimal,
    val resultatkode: ResultatKode) : PeriodisertGrunnlag {
  constructor(lopendeBidragPeriode: LopendeBidragPeriode)
      : this(lopendeBidragPeriode.soknadsbarnPersonId,
      lopendeBidragPeriode.lopendeBidragDatoFraTil.justerDatoer(),
      lopendeBidragPeriode.lopendeBidragBelop,
      lopendeBidragPeriode.resultatkode)
  override fun getDatoFraTil(): Periode {
    return lopendeBidragDatoFraTil
  }
}

data class SamvaersfradragPeriode(
    val soknadsbarnPersonId: Int,
    val samvaersfradragDatoFraTil: Periode,
    val samvaersfradragBelop: BigDecimal) : PeriodisertGrunnlag {
  constructor(samvaersfradragPeriode: SamvaersfradragPeriode)
      : this(samvaersfradragPeriode.soknadsbarnPersonId,
      samvaersfradragPeriode.samvaersfradragDatoFraTil.justerDatoer(),
      samvaersfradragPeriode.samvaersfradragBelop)
  override fun getDatoFraTil(): Periode {
    return samvaersfradragDatoFraTil
  }
}
