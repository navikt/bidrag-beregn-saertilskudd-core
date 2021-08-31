package no.nav.bidrag.beregn.bidragsevne.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.InntektType
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnBidragsevneGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val inntektPeriodeListe: List<InntektPeriode>,
    val skatteklassePeriodeListe: List<SkatteklassePeriode>,
    val bostatusPeriodeListe: List<BostatusPeriode>,
    val antallBarnIEgetHusholdPeriodeListe: List<BarnIHustandPeriode>,
    val saerfradragPeriodeListe: List<SaerfradragPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnBidragsevneResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlagBeregning: GrunnlagBeregning
)

data class ResultatBeregning(
    val resultatEvneBelop: BigDecimal,
    val sjablonListe: List<SjablonPeriodeNavnVerdi>
)

// Grunnlag beregning
data class GrunnlagBeregning(
    val inntektListe: List<Inntekt>,
    val skatteklasse: Skatteklasse,
    val bostatus: Bostatus,
    val barnIHusstand: BarnIHusstand,
    val saerfradrag: Saerfradrag,
    val sjablonListe: List<SjablonPeriode>
)

data class Inntekt(
    val referanse: String,
    val inntektType: InntektType,
    val inntektBelop: BigDecimal
)

data class Skatteklasse(
    val referanse: String,
    val skatteklasse: Int
)

data class Bostatus(
    val referanse: String,
    val kode: BostatusKode
)

data class BarnIHusstand(
    val referanse: String,
    val antallBarn: Double
)

data class Saerfradrag(
    val referanse: String,
    val kode: SaerfradragKode
)