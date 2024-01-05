package no.nav.bidrag.beregn.saertilskudd

import no.nav.bidrag.beregn.TestUtil
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskuddPeriode
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddGrunnlag
import no.nav.bidrag.beregn.saertilskudd.bo.BidragsevnePeriode
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidragPeriode
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlagPeriode
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode.Companion.getInstance
import no.nav.bidrag.domene.enums.beregning.ResultatkodeSærtilskudd
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

class SaertilskuddPeriodeTest {

    private val saertilskuddPeriode = getInstance()

    @Test
    @DisplayName("Test at resultatperiode er lik beregn-fra-og-til-periode i input og ingen andre perioder dannes")
    fun testPaaPeriode() {
        val bidragsevnePeriodeListe = mutableListOf<BidragsevnePeriode>()

        var bidragsevnePeriode = BidragsevnePeriode(
            referanse = TestUtil.BIDRAGSEVNE_REFERANSE,
            periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2019-07-01")),
            bidragsevneBelop = BigDecimal.valueOf(11000),
        )
        bidragsevnePeriodeListe.add(bidragsevnePeriode)

        bidragsevnePeriode = BidragsevnePeriode(
            referanse = TestUtil.BIDRAGSEVNE_REFERANSE,
            periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-01-01")),
            bidragsevneBelop = BigDecimal.valueOf(11069),
        )
        bidragsevnePeriodeListe.add(bidragsevnePeriode)

        val bPsAndelSaertilskuddPeriodeListe = listOf(
            BPsAndelSaertilskuddPeriode(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(60.6),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(4242),
                barnetErSelvforsorget = false,
            ),
        )

        val lopendeBidragPeriodeListe = listOf(
            LopendeBidragPeriode(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(2500),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(2958),
                opprinneligBidragBelop = BigDecimal.valueOf(2500),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(457),
            ),
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragGrunnlagPeriode(
                referanse = TestUtil.SAMVÆRSFRADRAG_REFERANSE,
                barnPersonId = 1,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                samvaersfradragBelop = BigDecimal.valueOf(457),
            ),
        )

        val beregnSaertilskuddGrunnlag = BeregnSaertilskuddGrunnlag(
            beregnDatoFra = LocalDate.parse("2019-08-01"),
            beregnDatoTil = LocalDate.parse("2019-09-01"),
            soknadsbarnPersonId = 1,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            bPsAndelSaertilskuddPeriodeListe = bPsAndelSaertilskuddPeriodeListe,
            lopendeBidragPeriodeListe = lopendeBidragPeriodeListe,
            samvaersfradragGrunnlagPeriodeListe = samvaersfradragPeriodeListe,
            sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe(),
        )

        val resultat = saertilskuddPeriode.beregnPerioder(beregnSaertilskuddGrunnlag)

        assertAll(
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-09-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.resultatBelop.toDouble()).isEqualTo(4242.0) },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[0].resultat.resultatkode,
                ).isEqualTo(ResultatkodeSærtilskudd.SÆRTILSKUDD_INNVILGET)
            },
        )
    }
}
