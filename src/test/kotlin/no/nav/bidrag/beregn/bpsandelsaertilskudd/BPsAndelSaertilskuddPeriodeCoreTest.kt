package no.nav.bidrag.beregn.bpsandelsaertilskudd

import no.nav.bidrag.beregn.TestUtil
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddResultat
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.Inntekt
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatBeregning
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatPeriode
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.BeregnBPsAndelSaertilskuddGrunnlagCore
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.InntektPeriodeCore
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.NettoSaertilskuddPeriodeCore
import no.nav.bidrag.beregn.bpsandelsaertilskudd.periode.BPsAndelSaertilskuddPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.domain.enums.AvvikType
import no.nav.bidrag.domain.enums.InntektType
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
internal class BPsAndelSaertilskuddPeriodeCoreTest {

    private lateinit var bPsAndelSaertilskuddCore: BPsAndelSaertilskuddCore

    @Mock
    private lateinit var bPsAndelSaertilskuddPeriodeMock: BPsAndelSaertilskuddPeriode

    private lateinit var beregnBPsAndelSaertilskuddGrunnlagCore: BeregnBPsAndelSaertilskuddGrunnlagCore
    private lateinit var bPsAndelSaertilskuddPeriodeResultat: BeregnBPsAndelSaertilskuddResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        bPsAndelSaertilskuddCore = BPsAndelSaertilskuddCoreImpl(bPsAndelSaertilskuddPeriodeMock)
    }

    @Test
    @DisplayName("Skal beregne BPs andel av særtilskudd")
    fun skalBeregneBPsAndelSaertilskudd() {
        byggBPsAndelSaertilskuddPeriodeGrunnlagCore()
        byggBPsAndelSaertilskuddPeriodeResultat()

        `when`(bPsAndelSaertilskuddPeriodeMock.beregnPerioder(any())).thenReturn(bPsAndelSaertilskuddPeriodeResultat)

        val resultatCore = bPsAndelSaertilskuddCore.beregnBPsAndelSaertilskudd(beregnBPsAndelSaertilskuddGrunnlagCore)

        assertAll(
            Executable { assertThat(resultatCore).isNotNull() },
            Executable { assertThat(resultatCore.avvikListe).isEmpty() },
            Executable { assertThat(resultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultatCore.resultatPeriodeListe).hasSize(3) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[0].resultatBeregning.resultatAndelProsent).isEqualTo(BigDecimal.valueOf(10)) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[1].resultatBeregning.resultatAndelProsent).isEqualTo(BigDecimal.valueOf(20)) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[2].resultatBeregning.resultatAndelProsent).isEqualTo(BigDecimal.valueOf(30)) },
            Executable { assertThat(resultatCore.sjablonListe[0].verdi).isEqualTo(BigDecimal.valueOf(1600)) }
        )
    }

    @Test
    @DisplayName("Skal ikke beregne BPs andel av Saertilskudd ved avvik")
    fun skalIkkeBeregneAndelVedAvvik() {
        byggBPsAndelSaertilskuddPeriodeGrunnlagCore()
        byggAvvik()

        `when`(bPsAndelSaertilskuddPeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val resultatCore = bPsAndelSaertilskuddCore.beregnBPsAndelSaertilskudd(beregnBPsAndelSaertilskuddGrunnlagCore)

        assertAll(
            Executable { assertThat(resultatCore).isNotNull() },
            Executable { assertThat(resultatCore.avvikListe).isNotEmpty() },
            Executable { assertThat(resultatCore.avvikListe).hasSize(1) },
            Executable { assertThat(resultatCore.avvikListe[0].avvikTekst).isEqualTo("beregnDatoTil må være etter beregnDatoFra") },
            Executable { assertThat(resultatCore.avvikListe[0].avvikType).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()) },
            Executable { assertThat(resultatCore.resultatPeriodeListe).isEmpty() }
        )
    }

    private fun byggBPsAndelSaertilskuddPeriodeGrunnlagCore() {
        val nettoSaertilskuddPeriodeListe = listOf(
            NettoSaertilskuddPeriodeCore(
                referanse = TestUtil.NETTO_SAERTILSKUDD_REFERANSE,
                periodeDatoFraTil = PeriodeCore(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                nettoSaertilskuddBelop = BigDecimal.valueOf(1000)
            )
        )

        val inntektBPPeriodeListe = listOf(
            InntektPeriodeCore(
                referanse = TestUtil.INNTEKT_REFERANSE,
                periodeDatoFraTil = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                inntektType = InntektType.LONN_SKE.toString(),
                inntektBelop = BigDecimal.valueOf(111),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val inntektBMPeriodeListe = listOf(
            InntektPeriodeCore(
                referanse = TestUtil.INNTEKT_REFERANSE,
                periodeDatoFraTil = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                inntektType = InntektType.LONN_SKE.toString(),
                inntektBelop = BigDecimal.valueOf(222),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val inntektBBPeriodeListe = listOf(
            InntektPeriodeCore(
                referanse = TestUtil.INNTEKT_REFERANSE,
                periodeDatoFraTil = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                inntektType = InntektType.LONN_SKE.toString(),
                inntektBelop = BigDecimal.valueOf(333),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val sjablonPeriodeListe = listOf(
            SjablonPeriodeCore(
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnholdCore(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1600)))
            )
        )

        beregnBPsAndelSaertilskuddGrunnlagCore = BeregnBPsAndelSaertilskuddGrunnlagCore(
            beregnDatoFra = LocalDate.parse("2017-01-01"),
            beregnDatoTil = LocalDate.parse("2020-01-01"),
            nettoSaertilskuddPeriodeListe = nettoSaertilskuddPeriodeListe,
            inntektBPPeriodeListe = inntektBPPeriodeListe,
            inntektBMPeriodeListe = inntektBMPeriodeListe,
            inntektBBPeriodeListe = inntektBBPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )
    }

    private fun byggBPsAndelSaertilskuddPeriodeResultat() {
        val inntektBPListe = listOf(
            Inntekt(
                referanse = TestUtil.INNTEKT_REFERANSE,
                inntektType = InntektType.LONN_SKE,
                inntektBelop = BigDecimal.valueOf(111.0),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        val inntektBMListe = listOf(
            Inntekt(
                referanse = TestUtil.INNTEKT_REFERANSE,
                inntektType = InntektType.LONN_SKE,
                inntektBelop = BigDecimal.valueOf(222.0),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        val inntektBBListe = listOf(
            Inntekt(
                referanse = TestUtil.INNTEKT_REFERANSE,
                inntektType = InntektType.LONN_SKE,
                inntektBelop = BigDecimal.valueOf(333.0),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val periodeResultatListe = mutableListOf<ResultatPeriode>()
        periodeResultatListe.add(
            ResultatPeriode(
                resultatDatoFraTil = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                resultatBeregning = ResultatBeregning(
                    resultatAndelProsent = BigDecimal.valueOf(10),
                    resultatAndelBelop = BigDecimal.valueOf(1000),
                    barnetErSelvforsorget = false,
                    sjablonListe = listOf(
                        SjablonPeriodeNavnVerdi(
                            periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                            verdi = BigDecimal.valueOf(1600)
                        )
                    )
                ),
                resultatGrunnlagBeregning = GrunnlagBeregning(
                    nettoSaertilskuddBelop = BigDecimal.valueOf(1000),
                    inntektBPListe = inntektBPListe,
                    inntektBMListe = inntektBMListe,
                    inntektBBListe = inntektBBListe,
                    sjablonListe = listOf(
                        SjablonPeriode(
                            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            sjablon = Sjablon(
                                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                                nokkelListe = emptyList(),
                                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1600)))
                            )
                        )
                    )
                )
            )
        )
        periodeResultatListe.add(
            ResultatPeriode(
                resultatDatoFraTil = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-01-01")),
                resultatBeregning = ResultatBeregning(
                    resultatAndelProsent = BigDecimal.valueOf(20),
                    resultatAndelBelop = BigDecimal.valueOf(1000),
                    barnetErSelvforsorget = false,
                    sjablonListe = listOf(
                        SjablonPeriodeNavnVerdi(
                            periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                            verdi = BigDecimal.valueOf(1600)
                        )
                    )
                ),
                resultatGrunnlagBeregning = GrunnlagBeregning(
                    nettoSaertilskuddBelop = BigDecimal.valueOf(1000),
                    inntektBPListe = inntektBPListe,
                    inntektBMListe = inntektBMListe,
                    inntektBBListe = inntektBBListe,
                    sjablonListe = listOf(
                        SjablonPeriode(
                            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            sjablon = Sjablon(
                                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                                nokkelListe = emptyList(),
                                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1640)))
                            )
                        )
                    )
                )
            )
        )
        periodeResultatListe.add(
            ResultatPeriode(
                resultatDatoFraTil = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                resultatBeregning = ResultatBeregning(
                    resultatAndelProsent = BigDecimal.valueOf(30),
                    resultatAndelBelop = BigDecimal.valueOf(1000),
                    barnetErSelvforsorget = false,
                    sjablonListe = listOf(
                        SjablonPeriodeNavnVerdi(
                            periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                            verdi = BigDecimal.valueOf(1600)
                        )
                    )
                ),
                resultatGrunnlagBeregning = GrunnlagBeregning(
                    nettoSaertilskuddBelop = BigDecimal.valueOf(1000),
                    inntektBPListe = inntektBPListe,
                    inntektBMListe = inntektBMListe,
                    inntektBBListe = inntektBBListe,
                    sjablonListe = listOf(
                        SjablonPeriode(
                            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            sjablon = Sjablon(
                                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                                nokkelListe = emptyList(),
                                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1680)))
                            )
                        )
                    )
                )
            )
        )

        bPsAndelSaertilskuddPeriodeResultat = BeregnBPsAndelSaertilskuddResultat(periodeResultatListe)
    }

    private fun byggAvvik() {
        avvikListe = listOf(
            Avvik(avvikTekst = "beregnDatoTil må være etter beregnDatoFra", avvikType = AvvikType.DATO_FOM_ETTER_DATO_TIL)
        )
    }

    companion object MockitoHelper {
        fun <T> any(): T = Mockito.any()
    }
}
