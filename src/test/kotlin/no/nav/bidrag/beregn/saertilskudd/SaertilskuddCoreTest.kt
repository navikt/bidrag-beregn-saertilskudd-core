package no.nav.bidrag.beregn.saertilskudd

import no.nav.bidrag.beregn.TestUtil
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregningImpl
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatPeriode
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlag
import no.nav.bidrag.beregn.saertilskudd.dto.BPsAndelSaertilskuddPeriodeCore
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddGrunnlagCore
import no.nav.bidrag.beregn.saertilskudd.dto.BidragsevnePeriodeCore
import no.nav.bidrag.beregn.saertilskudd.dto.LopendeBidragPeriodeCore
import no.nav.bidrag.beregn.saertilskudd.dto.SamvaersfradragPeriodeCore
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriodeImpl
import no.nav.bidrag.domain.enums.AvvikType
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeSaertilskudd
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
internal class SaertilskuddCoreTest {

    private lateinit var saertilskuddCoreWithMock: SaertilskuddCore

    @Mock
    private lateinit var saertilskuddPeriodeMock: SaertilskuddPeriode

    private lateinit var saertilskuddCore: SaertilskuddCore
    private lateinit var beregnSaertilskuddGrunnlagCore: BeregnSaertilskuddGrunnlagCore
    private lateinit var beregnSaertilskuddPeriodeResultat: BeregnSaertilskuddResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        saertilskuddCoreWithMock = SaertilskuddCoreImpl(saertilskuddPeriodeMock)
        val saertilskuddBeregning = SaertilskuddBeregningImpl()
        val saertilskuddPeriode = SaertilskuddPeriodeImpl(saertilskuddBeregning)
        saertilskuddCore = SaertilskuddCoreImpl(saertilskuddPeriode)
    }

    @Test
    @DisplayName("Skal beregne særtilskudd")
    fun skalBeregneSaertilskudd() {
        byggSaertilskuddPeriodeGrunnlagCore()
        byggSaertilskuddPeriodeResultat()

        `when`(saertilskuddPeriodeMock.beregnPerioder(any())).thenReturn(beregnSaertilskuddPeriodeResultat)

        val resultatCore = saertilskuddCoreWithMock.beregnSaertilskudd(beregnSaertilskuddGrunnlagCore)

        assertAll(
            Executable { assertThat(resultatCore).isNotNull() },
            Executable { assertThat(resultatCore.avvikListe).isEmpty() },
            Executable { assertThat(resultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultatCore.resultatPeriodeListe).hasSize(1) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2018-01-01")) }
        )
    }

    @Test
    @DisplayName("Skal beregne særtilskudd uten mocks")
    fun skalBeregneSaertilskuddUtenMocks() {
        byggSaertilskuddPeriodeGrunnlagCore()

        val resultatCore = saertilskuddCore.beregnSaertilskudd(beregnSaertilskuddGrunnlagCore)

        assertAll(
            Executable { assertThat(resultatCore).isNotNull() },
            Executable { assertThat(resultatCore.avvikListe).isEmpty() },
            Executable { assertThat(resultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultatCore.resultatPeriodeListe).hasSize(1) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(resultatCore.resultatPeriodeListe[0].grunnlagReferanseListe).hasSize(4) }
        )
    }

    @Test
    @DisplayName("Skal ikke beregne særtilskudd ved avvik")
    fun skalIkkeBeregneSaertilskuddVedAvvik() {
        byggSaertilskuddPeriodeGrunnlagCore()
        byggAvvik()

        `when`(saertilskuddPeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val resultatCore = saertilskuddCoreWithMock.beregnSaertilskudd(beregnSaertilskuddGrunnlagCore)

        assertAll(
            Executable { assertThat(resultatCore).isNotNull() },
            Executable { assertThat(resultatCore.avvikListe).isNotEmpty() },
            Executable { assertThat(resultatCore.avvikListe).hasSize(1) },
            Executable { assertThat(resultatCore.avvikListe[0].avvikTekst).isEqualTo("beregnDatoTil må være etter beregnDatoFra") },
            Executable { assertThat(resultatCore.avvikListe[0].avvikType).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()) },
            Executable { assertThat(resultatCore.resultatPeriodeListe).isEmpty() }
        )
    }

    private fun byggSaertilskuddPeriodeGrunnlagCore() {
        val bidragsevnePeriodeListe = listOf(
            BidragsevnePeriodeCore(
                referanse = TestUtil.BIDRAGSEVNE_REFERANSE,
                periodeDatoFraTil = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                bidragsevneBelop = BigDecimal.valueOf(100000)
            )
        )

        val bPsAndelSaertilskuddPeriodeListe = listOf(
            BPsAndelSaertilskuddPeriodeCore(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                periodeDatoFraTil = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(100000),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(20000),
                barnetErSelvforsorget = false
            )
        )

        val lopendeBidragPeriodeListe = listOf(
            LopendeBidragPeriodeCore(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                periodeDatoFraTil = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(1000),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(1000),
                opprinneligBidragBelop = BigDecimal.valueOf(1000),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1000)
            )
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragPeriodeCore(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                periodeDatoFraTil = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(1000)
            )
        )

        val sjablonPeriodeListe = mapSjablonSjablontall(TestUtil.byggSjablonPeriodeListe())

        beregnSaertilskuddGrunnlagCore = BeregnSaertilskuddGrunnlagCore(
            beregnDatoFra = LocalDate.parse("2017-01-01"),
            beregnDatoTil = LocalDate.parse("2020-01-01"),
            soknadsbarnPersonId = 1,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            bPsAndelSaertilskuddPeriodeListe = bPsAndelSaertilskuddPeriodeListe,
            lopendeBidragPeriodeListe = lopendeBidragPeriodeListe,
            samvaersfradragPeriodeListe = samvaersfradragPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )
    }

    private fun mapSjablonSjablontall(sjablonPeriodeListe: List<SjablonPeriode>): MutableList<SjablonPeriodeCore> =
        sjablonPeriodeListe.stream()
            .map { sjablon: SjablonPeriode ->
                SjablonPeriodeCore(
                    periode = PeriodeCore(datoFom = sjablon.getPeriode().datoFom, datoTil = sjablon.getPeriode().datoTil),
                    navn = sjablon.sjablon.navn,
                    nokkelListe = sjablon.sjablon.nokkelListe!!.stream()
                        .map { (navn, verdi): SjablonNokkel ->
                            SjablonNokkelCore(
                                navn = navn,
                                verdi = verdi
                            )
                        }
                        .toList(),
                    innholdListe = sjablon.sjablon.innholdListe.stream()
                        .map { (navn, verdi): SjablonInnhold ->
                            SjablonInnholdCore(
                                navn = navn,
                                verdi = verdi
                            )
                        }
                        .toList()
                )
            }
            .toList()

    private fun byggSaertilskuddPeriodeResultat() {
        val lopendeBidragListe = listOf(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(100),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(1000),
                opprinneligBidragBelop = BigDecimal.valueOf(1000),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1000)
            )
        )

        val samvaersfradragListe = listOf(
            SamvaersfradragGrunnlag(referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE, barnPersonId = 1, samvaersfradragBelop = BigDecimal.valueOf(100))
        )

        val periodeResultatListe = listOf(
            ResultatPeriode(
                periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                soknadsbarnPersonId = 1,
                resultat = ResultatBeregning(resultatBelop = BigDecimal.valueOf(1000), resultatkode = ResultatKodeSaertilskudd.SAERTILSKUDD_INNVILGET),
                grunnlag = GrunnlagBeregning(
                    bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(1000)),
                    bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                        referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                        bPsAndelSaertilskuddProsent = BigDecimal.valueOf(60),
                        bPsAndelSaertilskuddBelop = BigDecimal.valueOf(8000),
                        barnetErSelvforsorget = false
                    ),
                    lopendeBidragListe = lopendeBidragListe,
                    samvaersfradragGrunnlagListe = samvaersfradragListe
                )
            )
        )

        beregnSaertilskuddPeriodeResultat = BeregnSaertilskuddResultat(periodeResultatListe)
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
