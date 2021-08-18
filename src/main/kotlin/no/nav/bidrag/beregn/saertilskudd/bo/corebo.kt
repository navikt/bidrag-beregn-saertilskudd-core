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
        val samvaersfradragGrunnlagPeriodeListe: List<SamvaersfradragGrunnlagPeriode>
)

// Resultat
data class BeregnSaertilskuddResultat(
        val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
        val periode: Periode,
        val resultat: ResultatBeregning,
        val grunnlag: GrunnlagBeregning
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
        val samvaersfradragGrunnlagListe: List<SamvaersfradragGrunnlag>
)

data class Bidragsevne(
        val referanse: String,
        val bidragsevneBelop: BigDecimal
)

data class BPsAndelSaertilskudd(
        val referanse: String,
        val bPsAndelSaertilskuddProsent: BigDecimal,
        val bPsAndelSaertilskuddBelop: BigDecimal,
        val barnetErSelvforsorget: Boolean
)

data class LopendeBidrag(
        val referanse: String,
        val barnPersonId: Int,
        val lopendeBidragBelop: BigDecimal,
        val opprinneligBPsAndelUnderholdskostnadBelop: BigDecimal,
        val opprinneligBidragBelop: BigDecimal,
        val opprinneligSamvaersfradragBelop: BigDecimal
)

data class SamvaersfradragGrunnlag(
        val referanse: String,
        val barnPersonId: Int,
        val samvaersfradragBelop: BigDecimal
)