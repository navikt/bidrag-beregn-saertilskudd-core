package no.nav.bidrag.beregn.saertilskudd.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnSaertilskuddGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val bidragsevnePeriodeListe: List<BidragsevnePeriodeCore>,
    val bPsAndelSaertilskuddPeriodeListe: List<BPsAndelSaertilskuddPeriodeCore>,
    val lopendeBidragPeriodeListe: List<LopendeBidragPeriodeCore>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriodeCore>
)

data class BidragsevnePeriodeCore(
    val periodeDatoFraTil: PeriodeCore,
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class BPsAndelSaertilskuddPeriodeCore(
    val periodeDatoFraTil: PeriodeCore,
    val bPsAndelSaertilskuddProsent: BigDecimal,
    val bPsAndelSaertilskuddBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class LopendeBidragPeriodeCore(
    val soknadsbarnPersonId: Int,
    val periodeDatoFraTil: PeriodeCore,
    val lopendeBidragBelop: BigDecimal,
    val opprinneligBPsAndelUnderholdskostnadBelop: BigDecimal,
    val opprinneligBidragBelop: BigDecimal,
    val opprinneligSamvaersfradragBelop: BigDecimal,
    val resultatkode: String
)

data class SamvaersfradragPeriodeCore(
    val soknadsbarnPersonId: Int,
    val periodeDatoFraTil: PeriodeCore,
    val samvaersfradragBelop: BigDecimal
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
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatBelop: BigDecimal,
    val resultatkode: String,
)

data class ResultatGrunnlagCore(
    val bidragsevne: BidragsevneCore,
    val bPsAndelSaertilskudd: BPsAndelSaertilskuddCore,
    val lopendeBidragListe: List<LopendeBidragCore>,
    val samvaersfradragListe: List<SamvaersfradragCore>
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
    val soknadsbarnPersonId: Int,
    val lopendeBidragBelop: BigDecimal,
    val opprinneligBPsAndelSaertilskuddBelop: BigDecimal,
    val opprinneligBidragBelop: BigDecimal,
    val opprinneligSamvaersfradragBelop: BigDecimal,
    val resultatkode: String
)

data class SamvaersfradragCore(
    val soknadsbarnPersonId: Int,
    val samvaersfradragBelop: BigDecimal
)