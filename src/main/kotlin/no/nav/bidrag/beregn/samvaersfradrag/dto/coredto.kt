package no.nav.bidrag.beregn.samvaersfradrag.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnSamvaersfradragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val samvaersklassePeriodeListe: List<SamvaersklassePeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class SamvaersklassePeriodeCore(
    val referanse: String,
    val samvaersklassePeriodeDatoFraTil: PeriodeCore,
    val barnPersonId: Int,
    val barnFodselsdato: LocalDate,
    val samvaersklasse: String
)

// Resultatperiode
data class BeregnSamvaersfradragResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregningListe: List<ResultatBeregningCore>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisertCore
)

data class ResultatBeregningCore(
    val barnPersonId: Int,
    val resultatSamvaersfradragBelop: BigDecimal
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisertCore(
    val samvaersfradragGrunnlagPerBarnListe: List<SamvaersfradragGrunnlagPerBarnCore>,
    val sjablonListe: List<SjablonNavnVerdiCore>
)

data class SamvaersfradragGrunnlagPerBarnCore(
    val barnPersonId: Int,
    val barnAlder: Int,
    val samvaersklasse: String,
)
