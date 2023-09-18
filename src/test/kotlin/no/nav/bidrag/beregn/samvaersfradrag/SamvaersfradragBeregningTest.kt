package no.nav.bidrag.beregn.samvaersfradrag

import no.nav.bidrag.beregn.TestUtil
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregningImpl
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPerBarn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

internal class SamvaersfradragBeregningTest {

    private val sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe()
    private val samvaersfradragBeregning = SamvaersfradragBeregningImpl()

    @DisplayName("Test av beregning av samvaersfradrag for fireåring")
    @Test
    fun testFireAar() {
        val samvaersfradragGrunnlagPerBarnListe = listOf(
            SamvaersfradragGrunnlagPerBarn(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                barnAlder = 4,
                samvaersklasse = "03"
            )
        )
        val resultatGrunnlag = GrunnlagBeregningPeriodisert(
            samvaersfradragGrunnlagPerBarnListe = samvaersfradragGrunnlagPerBarnListe,
            sjablonListe = sjablonPeriodeListe
        )

        assertAll(
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[0].barnPersonId).isEqualTo(1) },
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[0].belop.toDouble()).isEqualTo(2272.0) }
        )
    }

    @DisplayName("Test av beregning av samvaersfradrag for seksåring")
    @Test
    fun testSeksAar() {
        val samvaersfradragGrunnlagPerBarnListe = listOf(
            SamvaersfradragGrunnlagPerBarn(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                barnAlder = 6,
                samvaersklasse = "03"
            )
        )
        val resultatGrunnlag = GrunnlagBeregningPeriodisert(
            samvaersfradragGrunnlagPerBarnListe = samvaersfradragGrunnlagPerBarnListe,
            sjablonListe = sjablonPeriodeListe
        )

        assertAll(
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[0].barnPersonId).isEqualTo(1) },
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[0].belop.toDouble()).isEqualTo(2716.0) }
        )
    }

    @DisplayName("Test av beregning av samvaersfradrag for fire-, seks- og elleveåring")
    @Test
    fun testFireSeksElleveAar() {
        val samvaersfradragGrunnlagPerBarnListe = mutableListOf<SamvaersfradragGrunnlagPerBarn>()
        samvaersfradragGrunnlagPerBarnListe.add(
            SamvaersfradragGrunnlagPerBarn(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                barnAlder = 4,
                samvaersklasse = "03"
            )
        )
        samvaersfradragGrunnlagPerBarnListe.add(
            SamvaersfradragGrunnlagPerBarn(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 3,
                barnAlder = 6,
                samvaersklasse = "03"
            )
        )
        samvaersfradragGrunnlagPerBarnListe.add(
            SamvaersfradragGrunnlagPerBarn(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 5,
                barnAlder = 11,
                samvaersklasse = "01"
            )
        )

        val resultatGrunnlag = GrunnlagBeregningPeriodisert(
            samvaersfradragGrunnlagPerBarnListe = samvaersfradragGrunnlagPerBarnListe,
            sjablonListe = sjablonPeriodeListe
        )

        assertAll(
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)).hasSize(3) },
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[0].barnPersonId).isEqualTo(1) },
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[1].barnPersonId).isEqualTo(3) },
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[2].barnPersonId).isEqualTo(5) },
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[0].belop.toDouble()).isEqualTo(2272.0) },
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[1].belop.toDouble()).isEqualTo(2716.0) },
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[2].belop.toDouble()).isEqualTo(457.0) }
        )
    }

    @DisplayName("Test av beregning av samvaersfradrag for fjortenåring")
    @Test
    fun testFjortenAar() {
        val samvaersfradragGrunnlagPerBarnListe = mutableListOf(
            SamvaersfradragGrunnlagPerBarn(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 2,
                barnAlder = 14,
                samvaersklasse = "01"
            )
        )
        val resultatGrunnlag = GrunnlagBeregningPeriodisert(
            samvaersfradragGrunnlagPerBarnListe = samvaersfradragGrunnlagPerBarnListe,
            sjablonListe = sjablonPeriodeListe
        )

        assertAll(
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[0].barnPersonId).isEqualTo(2) },
            Executable { assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag)[0].belop.toDouble()).isEqualTo(457.0) }
        )
    }
}
