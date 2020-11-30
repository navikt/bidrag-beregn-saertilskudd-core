package no.nav.bidrag.beregn.bpsandelsaertilskudd.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnBPsAndelSaertilskuddGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val nettoSaertilskuddPeriodeListe: List<NettoSaertilskuddPeriodeCore>,
    val inntektBPPeriodeListe: List<InntektPeriodeCore>,
    val inntektBMPeriodeListe: List<InntektPeriodeCore>,
    val inntektBBPeriodeListe: List<InntektPeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>
)


data class NettoSaertilskuddPeriodeCore(
    val periodeDatoFraTil: PeriodeCore,
    val nettoSaertilskuddBelop: BigDecimal
)

data class InntektPeriodeCore(
    val periodeDatoFraTil: PeriodeCore,
    val inntektType: String,
    val inntektBelop: BigDecimal
)

// Resultatperiode
data class BeregnBPsAndelSaertilskuddResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatAndelProsent: BigDecimal
)

// Grunnlag beregning
data class ResultatGrunnlagCore(
    val nettoSaertilskuddBelop: BigDecimal,
    val inntektBPListe: List<InntektCore>,
    val inntektBMListe: List<InntektCore>,
    val inntektBBListe: List<InntektCore>,
    val sjablonListe: List<SjablonNavnVerdiCore>
)

data class InntektCore(
    val inntektType: String,
    val inntektBelop: BigDecimal
)

