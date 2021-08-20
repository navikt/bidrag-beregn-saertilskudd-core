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
    val referanse: String,
    val periodeDatoFraTil: PeriodeCore,
    val bidragsevneBelop: BigDecimal
)

data class BPsAndelSaertilskuddPeriodeCore(
    val referanse: String,
    val periodeDatoFraTil: PeriodeCore,
    val bPsAndelSaertilskuddProsent: BigDecimal,
    val bPsAndelSaertilskuddBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class LopendeBidragPeriodeCore(
    val referanse: String,
    val periodeDatoFraTil: PeriodeCore,
    val barnPersonId: Int,
    val lopendeBidragBelop: BigDecimal,
    val opprinneligBPsAndelUnderholdskostnadBelop: BigDecimal,
    val opprinneligBidragBelop: BigDecimal,
    val opprinneligSamvaersfradragBelop: BigDecimal
)

data class SamvaersfradragPeriodeCore(
    val referanse: String,
    val periodeDatoFraTil: PeriodeCore,
    val barnPersonId: Int,
    val samvaersfradragBelop: BigDecimal
)

// Resultatperiode
data class BeregnSaertilskuddResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val periode: PeriodeCore,
    val soknadsbarnPersonId: Int,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val belop: BigDecimal,
    val kode: String,
)

data class ResultatGrunnlagCore(
    val bidragsevne: BidragsevneCore,
    val bPsAndelSaertilskudd: BPsAndelSaertilskuddCore,
    val lopendeBidragListe: List<LopendeBidragCore>,
    val samvaersfradragListe: List<SamvaersfradragCore>
)

data class BidragsevneCore(
    val bidragsevneBelop: BigDecimal
)

data class BPsAndelSaertilskuddCore(
    val bPsAndelSaertilskuddProsent: BigDecimal,
    val bPsAndelSaertilskuddBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class LopendeBidragCore(
    val barnPersonId: Int,
    val lopendeBidragBelop: BigDecimal,
    val opprinneligBPsAndelUnderholdskostnadBelop: BigDecimal,
    val opprinneligBidragBelop: BigDecimal,
    val opprinneligSamvaersfradragBelop: BigDecimal
)

data class SamvaersfradragCore(
    val barnPersonId: Int,
    val samvaersfradragBelop: BigDecimal
)