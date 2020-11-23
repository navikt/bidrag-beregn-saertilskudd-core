package no.nav.bidrag.beregn.saertilskudd.dto

import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi
import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnSaertilskuddGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val bidragsevne: BidragsevneCore,
    val bPsAndelSaertilskudd: BPsAndelSaertilskuddCore,
    val lopendeBidrag: LopendeBidragCore,
    val samvaersfradragBelop: BigDecimal,
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

// Resultatperiode
data class BeregnSaertilskuddResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val soknadsbarnPersonId: Int,
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: GrunnlagBeregningCore
)

data class ResultatBeregningCore(
    val resultatBelop: BigDecimal,
    val resultatkode: String,
    val sjablonListe: List<SjablonNavnVerdi>
)

// Grunnlag beregning
data class GrunnlagBeregningCore(
    val bidragsevne: BidragsevneCore,
    val bPsAndelSaertilskudd: BPsAndelSaertilskuddCore,
    val lopendeBidrag: LopendeBidragCore,
    val samvaersfradrag: BigDecimal,
    val sjablonListe: List<SjablonNavnVerdiCore>
)

data class BidragsevneCore(
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class BPsAndelSaertilskuddCore(
    val bPsAndelSaertilskuddProsent: BigDecimal,
    val bPsAndelSaertilskuddBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class LopendeBidragCore(
    val lopendeBidragBelop: BigDecimal,
    val resultatkode: ResultatKode
)