package no.nav.bidrag.beregn.bpsandelsaertilskudd

import no.nav.bidrag.beregn.TestUtil
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddGrunnlag
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.InntektPeriode
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.NettoSaertilskuddPeriode
import no.nav.bidrag.beregn.bpsandelsaertilskudd.periode.BPsAndelSaertilskuddPeriode.Companion.getInstance
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.domene.enums.beregning.Avvikstype
import no.nav.bidrag.domene.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

internal class BPsAndelSaertilskuddPeriodeTest {

    private val bPsAndelSaertilskuddPeriode = getInstance()

    @Test
    @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    fun testPeriodisering() {
        val grunnlag = lagGrunnlag("2018-07-01", "2020-08-01")
        val resultat = bPsAndelSaertilskuddPeriode.beregnPerioder(grunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(3) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatDatoFraTil.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatDatoFraTil.datoTil).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatBeregning.resultatAndelProsent).isEqualTo(BigDecimal.valueOf(35.2)) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatDatoFraTil.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatDatoFraTil.datoTil).isEqualTo(LocalDate.parse("2020-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatDatoFraTil.datoFom).isEqualTo(LocalDate.parse("2020-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatDatoFraTil.datoTil).isNull() },
        )
    }

    @Test
    @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
    fun testGrunnlagMedAvvik() {
        val grunnlag = lagGrunnlag("2016-01-01", "2021-01-01")
        val avvikListe = bPsAndelSaertilskuddPeriode.validerInput(grunnlag)

        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe).hasSize(8) },
            Executable {
                assertThat(
                    avvikListe[0].avvikTekst,
                ).isEqualTo("Første dato i nettoSaertilskuddPeriodeListe (2018-01-01) er etter beregnDatoFom (2016-01-01)")
            },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(
                    avvikListe[1].avvikTekst,
                ).isEqualTo("Siste dato i nettoSaertilskuddPeriodeListe (2020-08-01) er før beregnDatoTil (2021-01-01)")
            },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
        )
    }

    private fun lagGrunnlag(beregnDatoFra: String, beregnDatoTil: String): BeregnBPsAndelSaertilskuddGrunnlag {
        val nettoSaertilskuddPeriodeListe = listOf(
            NettoSaertilskuddPeriode(
                referanse = TestUtil.NETTO_SAERTILSKUDD_REFERANSE,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                nettoSaertilskuddBelop = BigDecimal.valueOf(1000),
            ),
        )
        val inntektBPPeriodeListe = listOf(
            InntektPeriode(
                referanse = "Inntekt_20180101",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                inntektType = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                inntektBelop = BigDecimal.valueOf(217666),
                deltFordel = false,
                skatteklasse2 = false,
            ),
        )
        val inntektBMPeriodeListe = listOf(
            InntektPeriode(
                referanse = "Inntekt_20180101",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                inntektType = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                inntektBelop = BigDecimal.valueOf(400000),
                deltFordel = false,
                skatteklasse2 = false,
            ),
        )
        val inntektBBPeriodeListe = listOf(
            InntektPeriode(
                referanse = "Inntekt_20180101",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                inntektType = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                inntektBelop = BigDecimal.valueOf(40000),
                deltFordel = false,
                skatteklasse2 = false,
            ),
        )

        return BeregnBPsAndelSaertilskuddGrunnlag(
            beregnDatoFra = LocalDate.parse(beregnDatoFra),
            beregnDatoTil = LocalDate.parse(beregnDatoTil),
            nettoSaertilskuddPeriodeListe = nettoSaertilskuddPeriodeListe,
            inntektBPPeriodeListe = inntektBPPeriodeListe,
            inntektBMPeriodeListe = inntektBMPeriodeListe,
            inntektBBPeriodeListe = inntektBBPeriodeListe,
            sjablonPeriodeListe = lagSjablonGrunnlag(),
        )
    }

    private fun lagSjablonGrunnlag(): List<SjablonPeriode> {
        val sjablonPeriodeListe = mutableListOf<SjablonPeriode>()
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORSKUDDSSATS_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1600))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORSKUDDSSATS_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1640))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2020-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORSKUDDSSATS_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1670))),
                ),
            ),
        )

        return sjablonPeriodeListe
    }
}
