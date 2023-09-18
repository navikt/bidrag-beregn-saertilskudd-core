package no.nav.bidrag.beregn.bidragsevne

import no.nav.bidrag.beregn.TestUtil
import no.nav.bidrag.beregn.bidragsevne.beregning.BidragsevneBeregningImpl
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.util.SjablonUtil
import no.nav.bidrag.domain.enums.BostatusKode
import no.nav.bidrag.domain.enums.InntektType
import no.nav.bidrag.domain.enums.SaerfradragKode
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import java.time.LocalDate

internal class BidragsevneBeregningTest {

    private val sjablonListe = TestUtil.byggSjabloner()
    private val sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe()
    private val bidragsevneberegning = BidragsevneBeregningImpl()

    @ParameterizedTest
    @CsvSource(
        // Test på beregning med ulike inntekter
        "1000000, 1, ALENE, 1.0, INGEN, 31859",
        "520000, 1, ALENE, 1.0, INGEN, 8322",
        "666000, 1, ALENE, 3.0, INGEN, 8424",
        "480000, 1, ALENE, 0.0, INGEN, 9976",
        // Test på at beregnet bidragsevne blir satt til 0 når evne er negativ
        "100000, 1, MED_ANDRE, 3.0, HELT, 0",
        // Test av halvt særfradrag
        "666000, 1, ALENE, 3.0, HALVT, 8965",
        // Test av bostatus MED_FLERE
        "666000, 1, MED_ANDRE, 3.0, HALVT, 14253"
    )
    fun testBidragsevneBeregningStandardSjabloner(
        inntektBelop: BigDecimal,
        skatteklasse: Int,
        bostatusKode: BostatusKode,
        antallBarn: Double,
        saerfradragKode: SaerfradragKode,
        expectedResult: Int
    ) {
        val inntekter = listOf(Inntekt(TestUtil.INNTEKT_REFERANSE, InntektType.LONN_SKE, inntektBelop))
        val grunnlagBeregning = GrunnlagBeregning(
            inntektListe = inntekter,
            skatteklasse = Skatteklasse(TestUtil.SKATTEKLASSE_REFERANSE, skatteklasse),
            bostatus = Bostatus(TestUtil.BOSTATUS_REFERANSE, bostatusKode),
            barnIHusstand = BarnIHusstand(TestUtil.BARN_I_HUSSTAND_REFERANSE, antallBarn),
            saerfradrag = Saerfradrag(TestUtil.SAERFRADRAG_REFERANSE, saerfradragKode),
            sjablonListe = sjablonPeriodeListe
        )
        val result = bidragsevneberegning.beregn(grunnlagBeregning).belop
        assertEquals(expectedResult.toBigDecimal(), result)
    }

    @Test
    fun testBidragsevneBeregningSpesifikkeSjabloner() {
        val inntekter = mutableListOf<Inntekt>()

        // Test at fordel skatteklasse 2 ikke legges til beregnet evne når skatteklasse = 1
        inntekter.add(
            Inntekt(
                referanse = TestUtil.INNTEKT_REFERANSE,
                inntektType = InntektType.LONN_SKE,
                inntektBelop = BigDecimal.valueOf(666000)
            )
        )
        sjablonPeriodeListe[0] = SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(12000)))
            )
        )
        var grunnlagBeregning = GrunnlagBeregning(
            inntektListe = inntekter,
            skatteklasse = Skatteklasse(referanse = TestUtil.SKATTEKLASSE_REFERANSE, skatteklasse = 1),
            bostatus = Bostatus(referanse = TestUtil.BOSTATUS_REFERANSE, kode = BostatusKode.ALENE),
            barnIHusstand = BarnIHusstand(referanse = TestUtil.BARN_I_HUSSTAND_REFERANSE, antallBarn = 3.0),
            saerfradrag = Saerfradrag(referanse = TestUtil.SAERFRADRAG_REFERANSE, kode = SaerfradragKode.INGEN),
            sjablonListe = sjablonPeriodeListe
        )
        assertEquals(BigDecimal.valueOf(8424), bidragsevneberegning.beregn(grunnlagBeregning).belop)

        // Test at fordel skatteklasse 2 legges til beregnet evne når skatteklasse = 2
        inntekter[0] = Inntekt(
            referanse = TestUtil.INNTEKT_REFERANSE,
            inntektType = InntektType.LONN_SKE,
            inntektBelop = BigDecimal.valueOf(666000)
        )
        sjablonPeriodeListe[0] = SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(12000)))
            )
        )
        grunnlagBeregning = GrunnlagBeregning(
            inntektListe = inntekter,
            skatteklasse = Skatteklasse(referanse = TestUtil.SKATTEKLASSE_REFERANSE, skatteklasse = 2),
            bostatus = Bostatus(referanse = TestUtil.BOSTATUS_REFERANSE, kode = BostatusKode.ALENE),
            barnIHusstand = BarnIHusstand(referanse = TestUtil.BARN_I_HUSSTAND_REFERANSE, antallBarn = 3.0),
            saerfradrag = Saerfradrag(referanse = TestUtil.SAERFRADRAG_REFERANSE, kode = SaerfradragKode.INGEN),
            sjablonListe = sjablonPeriodeListe
        )
        assertEquals(BigDecimal.valueOf(9424), bidragsevneberegning.beregn(grunnlagBeregning).belop)

        // Test at personfradrag skatteklasse 2 brukes hvis skatteklasse 2 er angitt
        sjablonPeriodeListe[0] = SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.ZERO))
            )
        )
        sjablonPeriodeListe[1] = SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(24000)))
            )
        )
        grunnlagBeregning = GrunnlagBeregning(
            inntektListe = inntekter,
            skatteklasse = Skatteklasse(referanse = TestUtil.SKATTEKLASSE_REFERANSE, skatteklasse = 2),
            bostatus = Bostatus(referanse = TestUtil.BOSTATUS_REFERANSE, kode = BostatusKode.ALENE),
            barnIHusstand = BarnIHusstand(referanse = TestUtil.BARN_I_HUSSTAND_REFERANSE, antallBarn = 3.0),
            saerfradrag = Saerfradrag(referanse = TestUtil.SAERFRADRAG_REFERANSE, kode = SaerfradragKode.INGEN),
            sjablonListe = sjablonPeriodeListe
        )
        assertEquals(BigDecimal.valueOf(7923), bidragsevneberegning.beregn(grunnlagBeregning).belop)
    }

    @Test
    fun beregnMinstefradrag() {
        val inntekter = mutableListOf<Inntekt>()

        inntekter.add(
            Inntekt(
                referanse = TestUtil.INNTEKT_REFERANSE,
                inntektType = InntektType.LONN_SKE,
                inntektBelop = BigDecimal.valueOf(200000)
            )
        )
        var grunnlagBeregning = GrunnlagBeregning(
            inntektListe = inntekter,
            skatteklasse = Skatteklasse(referanse = TestUtil.SKATTEKLASSE_REFERANSE, skatteklasse = 1),
            bostatus = Bostatus(referanse = TestUtil.BOSTATUS_REFERANSE, kode = BostatusKode.ALENE),
            barnIHusstand = BarnIHusstand(referanse = TestUtil.BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
            saerfradrag = Saerfradrag(referanse = TestUtil.SAERFRADRAG_REFERANSE, kode = SaerfradragKode.HELT),
            sjablonListe = sjablonPeriodeListe
        )

        assertThat(
            bidragsevneberegning.beregnMinstefradrag(
                grunnlag = grunnlagBeregning,
                minstefradragInntektSjablonBelop = SjablonUtil.hentSjablonverdi(
                    sjablonListe = sjablonListe,
                    sjablonTallNavn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP
                ),
                minstefradragInntektSjablonProsent = SjablonUtil.hentSjablonverdi(
                    sjablonListe = sjablonListe,
                    sjablonTallNavn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT
                )
            )
        )
            .isEqualTo(BigDecimal.valueOf(62000))

        inntekter[0] = Inntekt(
            referanse = TestUtil.INNTEKT_REFERANSE,
            inntektType = InntektType.LONN_SKE,
            inntektBelop = BigDecimal.valueOf(1000000)
        )
        grunnlagBeregning = GrunnlagBeregning(
            inntektListe = inntekter,
            skatteklasse = Skatteklasse(referanse = TestUtil.SKATTEKLASSE_REFERANSE, skatteklasse = 1),
            bostatus = Bostatus(referanse = TestUtil.BOSTATUS_REFERANSE, kode = BostatusKode.ALENE),
            barnIHusstand = BarnIHusstand(referanse = TestUtil.BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
            saerfradrag = Saerfradrag(referanse = TestUtil.SAERFRADRAG_REFERANSE, kode = SaerfradragKode.HELT),
            sjablonListe = sjablonPeriodeListe
        )

        assertThat(
            bidragsevneberegning.beregnMinstefradrag(
                grunnlag = grunnlagBeregning,
                minstefradragInntektSjablonBelop = SjablonUtil.hentSjablonverdi(
                    sjablonListe = sjablonListe,
                    sjablonTallNavn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP
                ),
                minstefradragInntektSjablonProsent = SjablonUtil.hentSjablonverdi(
                    sjablonListe = sjablonListe,
                    sjablonTallNavn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT
                )
            )
        )
            .isEqualTo(BigDecimal.valueOf(87450))
    }

    @Test
    fun beregnSkattetrinnBelop() {
        val inntekter = mutableListOf<Inntekt>()

        inntekter.add(
            Inntekt(
                referanse = TestUtil.INNTEKT_REFERANSE,
                inntektType = InntektType.LONN_SKE,
                inntektBelop = BigDecimal.valueOf(666000)
            )
        )
        var grunnlagBeregning = GrunnlagBeregning(
            inntektListe = inntekter,
            skatteklasse = Skatteklasse(referanse = TestUtil.SKATTEKLASSE_REFERANSE, skatteklasse = 1),
            bostatus = Bostatus(referanse = TestUtil.BOSTATUS_REFERANSE, kode = BostatusKode.ALENE),
            barnIHusstand = BarnIHusstand(referanse = TestUtil.BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
            saerfradrag = Saerfradrag(referanse = TestUtil.SAERFRADRAG_REFERANSE, kode = SaerfradragKode.HELT),
            sjablonListe = sjablonPeriodeListe
        )
        assertEquals(BigDecimal.valueOf((1400 + 16181 + 3465 + 0).toLong()), bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregning))

        inntekter[0] = Inntekt(
            referanse = TestUtil.INNTEKT_REFERANSE,
            inntektType = InntektType.LONN_SKE,
            inntektBelop = BigDecimal.valueOf(174600)
        )
        grunnlagBeregning = GrunnlagBeregning(
            inntektListe = inntekter,
            skatteklasse = Skatteklasse(referanse = TestUtil.SKATTEKLASSE_REFERANSE, skatteklasse = 1),
            bostatus = Bostatus(referanse = TestUtil.BOSTATUS_REFERANSE, kode = BostatusKode.ALENE),
            barnIHusstand = BarnIHusstand(referanse = TestUtil.BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
            saerfradrag = Saerfradrag(referanse = TestUtil.SAERFRADRAG_REFERANSE, kode = SaerfradragKode.HELT),
            sjablonListe = sjablonPeriodeListe
        )
        assertEquals(BigDecimal.ZERO, bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregning))

        inntekter[0] = Inntekt(
            referanse = TestUtil.INNTEKT_REFERANSE,
            inntektType = InntektType.LONN_SKE,
            inntektBelop = BigDecimal.valueOf(250000)
        )
        grunnlagBeregning = GrunnlagBeregning(
            inntektListe = inntekter,
            skatteklasse = Skatteklasse(referanse = TestUtil.SKATTEKLASSE_REFERANSE, skatteklasse = 1),
            bostatus = Bostatus(referanse = TestUtil.BOSTATUS_REFERANSE, kode = BostatusKode.ALENE),
            barnIHusstand = BarnIHusstand(referanse = TestUtil.BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
            saerfradrag = Saerfradrag(referanse = TestUtil.SAERFRADRAG_REFERANSE, kode = SaerfradragKode.HELT),
            sjablonListe = sjablonPeriodeListe
        )
        assertEquals(BigDecimal.valueOf(1315), bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregning))
    }
}
