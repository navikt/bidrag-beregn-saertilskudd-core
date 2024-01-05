package no.nav.bidrag.beregn.bidragsevne

import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHustandPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode.Companion.getInstance
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.domene.enums.beregning.Avvikstype
import no.nav.bidrag.domene.enums.beregning.Særfradragskode
import no.nav.bidrag.domene.enums.person.Bostatuskode
import no.nav.bidrag.domene.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonNøkkelNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

internal class BidragsevnePeriodeTest {

    private val bidragsevnePeriode = getInstance()

    @Test
    @DisplayName("Test med OK grunnlag")
    fun testGrunnlagOk() {
        val grunnlag = lagGrunnlag()
        val resultat = bidragsevnePeriode.beregnPerioder(grunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(6) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatDatoFraTil.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatDatoFraTil.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatBeregning.belop).isEqualTo(BigDecimal.valueOf(3749)) },
            Executable {
                assertThat(resultat.resultatPeriodeListe[0].resultatGrunnlagBeregning.inntektListe[0].inntektBelop).isEqualTo(
                    BigDecimal.valueOf(444000),
                )
            },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatDatoFraTil.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatDatoFraTil.datoTil).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatBeregning.belop).isEqualTo(BigDecimal.valueOf(15604)) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatGrunnlagBeregning.bostatus.kode).isEqualTo(Bostatuskode.ALENE) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatDatoFraTil.datoFom).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatDatoFraTil.datoTil).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatBeregning.belop).isEqualTo(BigDecimal.valueOf(20536)) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultatDatoFraTil.datoFom).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultatDatoFraTil.datoTil).isEqualTo(LocalDate.parse("2019-05-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultatBeregning.belop).isEqualTo(BigDecimal.valueOf(20536)) },
            Executable {
                assertThat(resultat.resultatPeriodeListe[3].resultatGrunnlagBeregning.inntektListe[0].inntektBelop).isEqualTo(
                    BigDecimal.valueOf(666001),
                )
            },
            Executable { assertThat(resultat.resultatPeriodeListe[4].resultatDatoFraTil.datoFom).isEqualTo(LocalDate.parse("2019-05-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].resultatDatoFraTil.datoTil).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].resultatBeregning.belop).isEqualTo(BigDecimal.valueOf(20536)) },
            Executable {
                assertThat(resultat.resultatPeriodeListe[4].resultatGrunnlagBeregning.inntektListe[0].inntektBelop).isEqualTo(
                    BigDecimal.valueOf(666001),
                )
            },
            Executable {
                assertThat(resultat.resultatPeriodeListe[4].resultatGrunnlagBeregning.inntektListe[1].inntektBelop).isEqualTo(
                    BigDecimal.valueOf(2),
                )
            },
            Executable { assertThat(resultat.resultatPeriodeListe[5].resultatDatoFraTil.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].resultatDatoFraTil.datoTil).isNull() },
            Executable { assertThat(resultat.resultatPeriodeListe[5].resultatBeregning.belop).isEqualTo(BigDecimal.valueOf(20063)) },
            Executable {
                assertThat(resultat.resultatPeriodeListe[5].resultatGrunnlagBeregning.inntektListe[0].inntektBelop).isEqualTo(
                    BigDecimal.valueOf(666001),
                )
            },
            Executable {
                assertThat(resultat.resultatPeriodeListe[5].resultatGrunnlagBeregning.inntektListe[1].inntektBelop).isEqualTo(
                    BigDecimal.valueOf(2),
                )
            },
            Executable {
                assertThat(resultat.resultatPeriodeListe[5].resultatGrunnlagBeregning.inntektListe[2].inntektBelop).isEqualTo(
                    BigDecimal.valueOf(3),
                )
            },
        )
    }

    @Test
    @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
    fun testGrunnlagMedAvvik() {
        val grunnlagMedAvvik = lagGrunnlagMedAvvik()
        val avvikListe = bidragsevnePeriode.validerInput(grunnlagMedAvvik)

        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe).hasSize(6) },
            Executable {
                assertThat(
                    avvikListe[0].avvikTekst,
                ).isEqualTo("Første dato i inntektPeriodeListe (2003-01-01) er etter beregnDatoFom (2001-07-01)")
            },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(
                    avvikListe[1].avvikTekst,
                ).isEqualTo("Siste dato i inntektPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)")
            },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(
                    avvikListe[2].avvikTekst,
                ).isEqualTo("Første dato i skatteklassePeriodeListe (2003-01-01) er etter beregnDatoFom (2001-07-01)")
            },
            Executable { assertThat(avvikListe[2].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(
                    avvikListe[3].avvikTekst,
                ).isEqualTo("Siste dato i bostatusPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)")
            },
            Executable { assertThat(avvikListe[3].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(
                    avvikListe[4].avvikTekst,
                ).isEqualTo("Siste dato i antallBarnIEgetHusholdPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)")
            },
            Executable { assertThat(avvikListe[4].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(
                    avvikListe[5].avvikTekst,
                ).isEqualTo("Siste dato i saerfradragPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)")
            },
            Executable { assertThat(avvikListe[5].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
        )
    }

    private fun lagGrunnlag() = BeregnBidragsevneGrunnlag(
        beregnDatoFra = LocalDate.parse("2018-07-01"),
        beregnDatoTil = LocalDate.parse("2020-01-01"),
        inntektPeriodeListe = lagInntektGrunnlag(),
        skatteklassePeriodeListe = lagSkatteklasseGrunnlag(),
        bostatusPeriodeListe = lagBostatusGrunnlag(),
        antallBarnIEgetHusholdPeriodeListe = lagAntallBarnIEgetHusholdGrunnlag(),
        saerfradragPeriodeListe = lagSaerfradragGrunnlag(),
        sjablonPeriodeListe = lagSjablonGrunnlag(),
    )

    private fun lagGrunnlagMedAvvik() = BeregnBidragsevneGrunnlag(
        beregnDatoFra = LocalDate.parse("2001-07-01"),
        beregnDatoTil = LocalDate.parse("2021-01-01"),
        inntektPeriodeListe = lagInntektGrunnlag(),
        skatteklassePeriodeListe = lagSkatteklasseGrunnlag(),
        bostatusPeriodeListe = lagBostatusGrunnlag(),
        antallBarnIEgetHusholdPeriodeListe = lagAntallBarnIEgetHusholdGrunnlag(),
        saerfradragPeriodeListe = lagSaerfradragGrunnlag(),
        sjablonPeriodeListe = lagSjablonGrunnlag(),
    )

    private fun lagInntektGrunnlag(): List<InntektPeriode> {
        val inntektPeriodeListe = mutableListOf<InntektPeriode>()
        inntektPeriodeListe
            .add(
                InntektPeriode(
                    referanse = "Inntekt_20030101",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2003-01-01"), datoTil = LocalDate.parse("2004-01-01")),
                    inntektType = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                    inntektBelop = BigDecimal.valueOf(666000),
                ),
            )
        inntektPeriodeListe
            .add(
                InntektPeriode(
                    referanse = "Inntekt_20040101",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2004-01-01"), datoTil = LocalDate.parse("2016-01-01")),
                    inntektType = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                    inntektBelop = BigDecimal.valueOf(555000),
                ),
            )
        inntektPeriodeListe
            .add(
                InntektPeriode(
                    referanse = "Inntekt_20160101",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2016-01-01"), datoTil = LocalDate.parse("2019-01-01")),
                    inntektType = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                    inntektBelop = BigDecimal.valueOf(444000),
                ),
            )
        inntektPeriodeListe
            .add(
                InntektPeriode(
                    referanse = "Inntekt_20190101",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-04-01")),
                    inntektType = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                    inntektBelop = BigDecimal.valueOf(666000),
                ),
            )
        inntektPeriodeListe
            .add(
                InntektPeriode(
                    referanse = "Inntekt_20190401",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-04-01"), datoTil = LocalDate.parse("2020-01-01")),
                    inntektType = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                    inntektBelop = BigDecimal.valueOf(666001),
                ),
            )
        inntektPeriodeListe
            .add(
                InntektPeriode(
                    referanse = "20190501",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-05-01"), datoTil = LocalDate.parse("2020-01-01")),
                    inntektType = "OVERGANGSSTONAD",
                    inntektBelop = BigDecimal.valueOf(2),
                ),
            )
        inntektPeriodeListe
            .add(
                InntektPeriode(
                    referanse = "20190701",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-01-01")),
                    inntektType = "KONTANTSTOTTE",
                    inntektBelop = BigDecimal.valueOf(3),
                ),
            )

        return inntektPeriodeListe
    }

    private fun lagSkatteklasseGrunnlag(): List<SkatteklassePeriode> {
        val skatteklassePeriodeListe = mutableListOf<SkatteklassePeriode>()
        skatteklassePeriodeListe.add(
            SkatteklassePeriode(
                referanse = "Skatteklasse",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2003-01-01"), datoTil = LocalDate.parse("2004-01-01")),
                skatteklasse = 2,
            ),
        )
        skatteklassePeriodeListe.add(
            SkatteklassePeriode(
                referanse = "Skatteklasse",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2004-01-01"), datoTil = LocalDate.parse("2016-01-01")),
                skatteklasse = 2,
            ),
        )
        skatteklassePeriodeListe.add(
            SkatteklassePeriode(
                referanse = "Skatteklasse",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2016-01-01"), datoTil = LocalDate.parse("2019-01-01")),
                skatteklasse = 1,
            ),
        )
        skatteklassePeriodeListe.add(
            SkatteklassePeriode(
                referanse = "Skatteklasse",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-04-01")),
                skatteklasse = 1,
            ),
        )
        skatteklassePeriodeListe.add(
            SkatteklassePeriode(
                referanse = "Skatteklasse",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-04-01"), datoTil = LocalDate.parse("2020-01-01")),
                skatteklasse = 1,
            ),
        )
        skatteklassePeriodeListe.add(
            SkatteklassePeriode(
                referanse = "Skatteklasse",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2020-01-01"), datoTil = null),
                skatteklasse = 1,
            ),
        )

        return skatteklassePeriodeListe
    }

    private fun lagBostatusGrunnlag(): List<BostatusPeriode> {
        val bostatusPeriodeListe = mutableListOf<BostatusPeriode>()
        bostatusPeriodeListe.add(
            BostatusPeriode(
                referanse = "Bostatus",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2001-01-01"), datoTil = LocalDate.parse("2017-01-01")),
                bostatusKode = Bostatuskode.IKKE_MED_FORELDER,
            ),
        )
        bostatusPeriodeListe.add(
            BostatusPeriode(
                referanse = "Bostatus",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2019-02-01")),
                bostatusKode = Bostatuskode.ALENE,
            ),
        )
        bostatusPeriodeListe.add(
            BostatusPeriode(
                referanse = "Bostatus",
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-02-01"), datoTil = LocalDate.parse("2020-01-01")),
                bostatusKode = Bostatuskode.IKKE_MED_FORELDER,
            ),
        )

        return bostatusPeriodeListe
    }

    private fun lagAntallBarnIEgetHusholdGrunnlag(): List<BarnIHustandPeriode> {
        val antallBarnIEgetHusholdPeriodeListe = mutableListOf<BarnIHustandPeriode>()
        antallBarnIEgetHusholdPeriodeListe
            .add(
                BarnIHustandPeriode(
                    referanse = "BarnIHusstand",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2001-01-01"), datoTil = LocalDate.parse("2017-01-01")),
                    antallBarn = 1.0,
                ),
            )
        antallBarnIEgetHusholdPeriodeListe
            .add(
                BarnIHustandPeriode(
                    referanse = "BarnIHusstand",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                    antallBarn = 2.0,
                ),
            )

        return antallBarnIEgetHusholdPeriodeListe
    }

    private fun lagSaerfradragGrunnlag(): List<SaerfradragPeriode> {
        val saerfradragPeriodeListe = mutableListOf<SaerfradragPeriode>()
        saerfradragPeriodeListe
            .add(
                SaerfradragPeriode(
                    referanse = "Saerfradrag",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2001-01-01"), datoTil = LocalDate.parse("2017-01-01")),
                    saerfradragKode = Særfradragskode.HELT,
                ),
            )
        saerfradragPeriodeListe
            .add(
                SaerfradragPeriode(
                    referanse = "Saerfradrag",
                    periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                    saerfradragKode = Særfradragskode.HELT,
                ),
            )

        return saerfradragPeriodeListe
    }

    private fun lagSjablonGrunnlag(): List<SjablonPeriode> {
        val sjablonPeriodeListe = mutableListOf<SjablonPeriode>()
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2003-01-01"), datoTil = LocalDate.parse("2003-12-31")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(8848))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2013-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(0))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2003-01-01"), datoTil = LocalDate.parse("2013-12-31")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.TRYGDEAVGIFT_PROSENT.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(7.8))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2014-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.TRYGDEAVGIFT_PROSENT.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(8.2))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(3417))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(3487))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2005-01-01"), datoTil = LocalDate.parse("2005-05-31")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(57400))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-07-01"), datoTil = LocalDate.parse("2017-12-31")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(75000.0))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2018-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(75000))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(83000))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(85050))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(31))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(54750))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(56550))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(54750))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(56550))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2018-12-31")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORDEL_SÆRFRADRAG_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(13132))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORDEL_SÆRFRADRAG_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(12977))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2018-12-31")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(23))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(22))),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2018-12-31")),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(169000)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(1.4)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2018-12-31")),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(237900)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(3.3)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2018-12-31")),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(598050)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(12.4)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2018-12-31")),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(962050)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(15.4)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-12-31")),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(174500)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(1.9)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-12-31")),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(245650)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(4.2)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-12-31")),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(617500)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(13.2)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-12-31")),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(964800)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(16.2)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2020-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(180800)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(1.9)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2020-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(254500)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(4.2)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2020-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(639750)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(13.2)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2020-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.INNTEKTSGRENSE_BELØP.navn, verdi = BigDecimal.valueOf(999550)),
                        SjablonInnhold(navn = SjablonInnholdNavn.SKATTESATS_PROSENT.navn, verdi = BigDecimal.valueOf(16.2)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-06-30")),
                sjablon = Sjablon(
                    navn = SjablonNavn.BIDRAGSEVNE.navn,
                    nokkelListe = listOf(SjablonNokkel(SjablonNøkkelNavn.BOSTATUS.navn, "EN")),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.BOUTGIFT_BELØP.navn, verdi = BigDecimal.valueOf(9303)),
                        SjablonInnhold(navn = SjablonInnholdNavn.UNDERHOLD_BELØP.navn, verdi = BigDecimal.valueOf(8657)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-06-30")),
                sjablon = Sjablon(
                    navn = SjablonNavn.BIDRAGSEVNE.navn,
                    nokkelListe = listOf(SjablonNokkel(SjablonNøkkelNavn.BOSTATUS.navn, "GS")),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.BOUTGIFT_BELØP.navn, verdi = BigDecimal.valueOf(5698)),
                        SjablonInnhold(navn = SjablonInnholdNavn.UNDERHOLD_BELØP.navn, verdi = BigDecimal.valueOf(7330)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonNavn.BIDRAGSEVNE.navn,
                    nokkelListe = listOf(SjablonNokkel(SjablonNøkkelNavn.BOSTATUS.navn, "EN")),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.BOUTGIFT_BELØP.navn, verdi = BigDecimal.valueOf(9591)),
                        SjablonInnhold(navn = SjablonInnholdNavn.UNDERHOLD_BELØP.navn, verdi = BigDecimal.valueOf(8925)),
                    ),
                ),
            ),
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonNavn.BIDRAGSEVNE.navn,
                    nokkelListe = listOf(SjablonNokkel(SjablonNøkkelNavn.BOSTATUS.navn, "GS")),
                    innholdListe = listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.BOUTGIFT_BELØP.navn, verdi = BigDecimal.valueOf(5875)),
                        SjablonInnhold(navn = SjablonInnholdNavn.UNDERHOLD_BELØP.navn, verdi = BigDecimal.valueOf(7557)),
                    ),
                ),
            ),
        )
        return sjablonPeriodeListe
    }
}
