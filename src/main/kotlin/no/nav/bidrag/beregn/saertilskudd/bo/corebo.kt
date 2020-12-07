package no.nav.bidrag.beregn.saertilskudd.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag beregning
data class BeregnSaertilskuddGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val bidragsevnePeriodeListe: List<BidragsevnePeriode>,
    val bPsAndelSaertilskuddPeriodeListe: List<BPsAndelSaertilskuddPeriode>,
    val lopendeBidragPeriodeListe: List<LopendeBidragPeriode>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriode>
)

// Resultat
data class BeregnSaertilskuddResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val soknadsbarnPersonId: Int,
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlagBeregning: GrunnlagBeregning
)

data class ResultatBeregning(
    val resultatBelop: BigDecimal,
    val resultatkode: ResultatKode
)

// Grunnlag beregning
data class GrunnlagBeregning(
    val bidragsevne: Bidragsevne,
    val bPsAndelSaertilskudd: BPsAndelSaertilskudd,
    val lopendeBidragListe: List<LopendeBidrag>,
    val samvaersfradragListe: List<Samvaersfradrag>
)

data class Bidragsevne(
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class BPsAndelSaertilskudd(
    val bPsAndelSaertilskuddProsent: BigDecimal,
    val bPsAndelSaertilskuddBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class LopendeBidrag(
    val soknadsbarnPersonId: Int,
    val lopendeBidragBelop: BigDecimal,
    val opprinneligBPsAndelSaertilskuddBelop: BigDecimal,
    val opprinneligBidragBelop: BigDecimal,
    val opprinneligSamvaersfradragBelop: BigDecimal,
    val resultatkode: ResultatKode
)
data class Samvaersfradrag(
    val soknadsbarnPersonId: Int,
    val samvaersfradragBelop: BigDecimal
)