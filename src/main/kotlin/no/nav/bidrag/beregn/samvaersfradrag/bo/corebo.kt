package no.nav.bidrag.beregn.samvaersfradrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnSamvaersfradragGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val samvaersfradragGrunnlagPeriodeListe: List<SamvaersfradragGrunnlagPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnSamvaersfradragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregningListe: List<ResultatBeregning>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisert
)

data class ResultatBeregning(
    val barnPersonId: Int,
    val resultatSamvaersfradragBelop: BigDecimal,
    val sjablonListe: List<SjablonNavnVerdi>
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val samvaersfradragGrunnlagPerBarnListe: List<SamvaersfradragGrunnlagPerBarn>,
    val sjablonListe: List<Sjablon>
)

data class SamvaersfradragGrunnlagPerBarn(
    val barnPersonId: Int,
    val barnAlder: Int,
    val samvaersklasse: String,
)
