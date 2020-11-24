package no.nav.bidrag.beregn.saertilskudd.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag beregning
data class BeregnSaertilskuddGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val bidragsevne: Bidragsevne,
    val bPsAndelSaertilskudd: BPsAndelSaertilskudd,
    val lopendeBidrag: LopendeBidrag,
    val samvaersfradragBelop: BigDecimal,
    val sjablonPeriodeListe: List<SjablonPeriode>
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
    val resultatkode: ResultatKode,
    val sjablonListe: List<SjablonNavnVerdi>
)

// Grunnlag beregning
data class GrunnlagBeregning(
    val bidragsevne: Bidragsevne,
    val bPsAndelSaertilskudd: BPsAndelSaertilskudd,
    val lopendeBidrag: LopendeBidrag,
    val samvaersfradragBelop: BigDecimal,
    val sjablonListe: List<Sjablon>
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
    val lopendeBidragBelop: BigDecimal,
    val resultatkode: ResultatKode
)
