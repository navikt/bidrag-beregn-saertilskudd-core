package no.nav.bidrag.beregn.saertilskudd

import no.nav.bidrag.beregn.TestUtil
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregningImpl
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlag
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeSaertilskudd
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal

internal class SaertilskuddBeregningTest {

    private val saertilskuddBeregning = SaertilskuddBeregningImpl()

    @DisplayName("Beregner enkelt særtilskudd med full evne")
    @Test
    fun testEnkelBeregningFullEvne() {
        val lopendeBidragListe = listOf(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(2500),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(2957),
                opprinneligBidragBelop = BigDecimal.valueOf(2500),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(457)
            )
        )
        val samvaersfradragListe = listOf(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(457)
            )
        )
        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(11069)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(60.6),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(4242),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(4242.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_INNVILGET, resultatkode) }
        )
    }

    @DisplayName("Beregner særtilskudd som får manglende evne pga diff mellom opprinnelig og nytt samværsfradragbeløp")
    @Test
    fun testBeregningManglendeEvneOktSamvaersfradrag() {
        val lopendeBidragListe = listOf(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(2500),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(2958),
                opprinneligBidragBelop = BigDecimal.valueOf(2500),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(457)
            )
        )
        val samvaersfradragListe = listOf(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(800)
            )
        )
        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(3100)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(60.6),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(4242),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(0.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultatkode) }
        )
    }

    @DisplayName("Beregner særtilskudd som får manglende evne pga diff mellom opprinnelig og løpende bidragsbeløp")
    @Test
    fun testBeregningManglendeEvnePgaHoyereLopendeBidrag() {
        val lopendeBidragListe = listOf(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(3000),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(2958),
                opprinneligBidragBelop = BigDecimal.valueOf(2500),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(457)
            )
        )
        val samvaersfradragListe = listOf(
            SamvaersfradragGrunnlag(referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE, barnPersonId = 1, samvaersfradragBelop = BigDecimal.valueOf(457))
        )
        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(3456)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(60.6),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(4242),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(0.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultatkode) }
        )
    }

    @DisplayName("Ingen beregning skal gjøres når barnet er selvforsørget")
    @Test
    fun testIngenBeregningBarnetErSelvforsoerget() {
        val lopendeBidragListe = listOf(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(3000),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(2958),
                opprinneligBidragBelop = BigDecimal.valueOf(2500),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(457)
            )
        )
        val samvaersfradragListe = listOf(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(457)
            )
        )
        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(10000)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(60.6),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(4242),
                barnetErSelvforsorget = true
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(0.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.BARNET_ER_SELVFORSORGET, resultatkode) }
        )
    }

    @DisplayName("Beregning med data fra 2 barn")
    @Test
    fun testOkBeregningMedDataFraToBarn() {
        val lopendeBidragListe = mutableListOf<LopendeBidrag>()
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(1700),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(3215),
                opprinneligBidragBelop = BigDecimal.valueOf(1700),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 2,
                lopendeBidragBelop = BigDecimal.valueOf(1700),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(3215),
                opprinneligBidragBelop = BigDecimal.valueOf(1700),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val samvaersfradragListe = mutableListOf<SamvaersfradragGrunnlag>()
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 2,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(6696)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(49.7),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(2982),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(2982.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_INNVILGET, resultatkode) }
        )
    }

    @DisplayName("Beregning med data fra 2 barn, lavere evne")
    @Test
    fun testOkBeregningMedDataFraToBarnLavereEvne() {
        val lopendeBidragListe = mutableListOf<LopendeBidrag>()
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(1500),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(3015),
                opprinneligBidragBelop = BigDecimal.valueOf(1500),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 2,
                lopendeBidragBelop = BigDecimal.valueOf(1500),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(3015),
                opprinneligBidragBelop = BigDecimal.valueOf(1500),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val samvaersfradragListe = mutableListOf<SamvaersfradragGrunnlag>()
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 2,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(6149)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(55.7),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(6684),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(6684.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_INNVILGET, resultatkode) }
        )
    }

    @DisplayName("Beregning med data fra 2 barn, manglende evne")
    @Test
    fun testManglendeEvneBeregningMedDataFraToBarn() {
        val lopendeBidragListe = mutableListOf<LopendeBidrag>()
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(1800),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(3315),
                opprinneligBidragBelop = BigDecimal.valueOf(1800),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 2,
                lopendeBidragBelop = BigDecimal.valueOf(1800),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(3315),
                opprinneligBidragBelop = BigDecimal.valueOf(1800),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val samvaersfradragListe = mutableListOf<SamvaersfradragGrunnlag>()
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 2,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(6149)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(55.7),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(6684),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(0.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultatkode) }
        )
    }

    @DisplayName("Løpende bidrag ble begrenset av evne og er senere indeksregulert, samværsfradrag har økt fra 600 til 700")
    @Test
    fun testIndeksregulertBidragEndringBelopSamvaersfradrag() {
        val lopendeBidragListe = listOf(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(1300),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(2600),
                opprinneligBidragBelop = BigDecimal.valueOf(1200),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(600)
            )
        )
        val samvaersfradragListe = listOf(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(700)
            )
        )
        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(2700)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(70),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(5000),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(0.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultatkode) }
        )
    }

    @DisplayName("Indeksregulert bidrag, høyere samværsfradrag, manglende evne")
    @Test
    fun testIndeksregulertBidragHoyereSamvaersfradragManglendeEvne() {
        val lopendeBidragListe = mutableListOf<LopendeBidrag>()
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(1800),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(5000),
                opprinneligBidragBelop = BigDecimal.valueOf(1700),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1323)
            )
        )
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 2,
                lopendeBidragBelop = BigDecimal.valueOf(1800),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(5000),
                opprinneligBidragBelop = BigDecimal.valueOf(1700),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1323)
            )
        )

        val samvaersfradragListe = mutableListOf<SamvaersfradragGrunnlag>()
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 2,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(9962)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(62.8),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(7536),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(0.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultatkode) }
        )
    }

    @DisplayName("Indeksregulert bidrag, høyere samværsfradrag, full evne")
    @Test
    fun testIndeksregulertBidragHoyereSamvaersfradragFullEvne() {
        val lopendeBidragListe = mutableListOf<LopendeBidrag>()
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(1800),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(5000),
                opprinneligBidragBelop = BigDecimal.valueOf(1700),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1323)
            )
        )
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 2,
                lopendeBidragBelop = BigDecimal.valueOf(1800),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(5000),
                opprinneligBidragBelop = BigDecimal.valueOf(1700),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1323)
            )
        )

        val samvaersfradragListe = mutableListOf<SamvaersfradragGrunnlag>()
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 2,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(10891)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(55.1),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(6612),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(6612.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_INNVILGET, resultatkode) }
        )
    }

    @DisplayName("Indeksregulert bidrag, høyere samværsfradrag, manglende evne")
    @Test
    fun testIndeksregulertBidragHoyereSamvaersfradragManglendeEvne2() {
        val lopendeBidragListe = mutableListOf<LopendeBidrag>()
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(1800),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(4000),
                opprinneligBidragBelop = BigDecimal.valueOf(1700),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1323)
            )
        )
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 2,
                lopendeBidragBelop = BigDecimal.valueOf(1800),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(4000),
                opprinneligBidragBelop = BigDecimal.valueOf(1700),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1323)
            )
        )

        val samvaersfradragListe = mutableListOf<SamvaersfradragGrunnlag>()
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 2,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(6149)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(55.7),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(6684),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )

        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(0.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultatkode) }
        )
    }

    @DisplayName("Indeksregulert bidrag, høyere samværsfradrag, manglende evne")
    @Test
    fun testIndeksregulertBidragHoyereSamvaersfradragManglendeEvne3() {
        val lopendeBidragListe = mutableListOf<LopendeBidrag>()
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 1,
                lopendeBidragBelop = BigDecimal.valueOf(2900),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(4000),
                opprinneligBidragBelop = BigDecimal.valueOf(2800),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1323)
            )
        )
        lopendeBidragListe.add(
            LopendeBidrag(
                referanse = TestUtil.LOPENDE_BIDRAG_REFERANSE,
                barnPersonId = 2,
                lopendeBidragBelop = BigDecimal.valueOf(2900),
                opprinneligBPsAndelUnderholdskostnadBelop = BigDecimal.valueOf(4000),
                opprinneligBidragBelop = BigDecimal.valueOf(2800),
                opprinneligSamvaersfradragBelop = BigDecimal.valueOf(1323)
            )
        )

        val samvaersfradragListe = mutableListOf<SamvaersfradragGrunnlag>()
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 1,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )
        samvaersfradragListe.add(
            SamvaersfradragGrunnlag(
                referanse = TestUtil.SAMVAERSFRADRAG_REFERANSE,
                barnPersonId = 2,
                samvaersfradragBelop = BigDecimal.valueOf(1513)
            )
        )

        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(referanse = TestUtil.BIDRAGSEVNE_REFERANSE, bidragsevneBelop = BigDecimal.valueOf(6149)),
            bPsAndelSaertilskudd = BPsAndelSaertilskudd(
                referanse = TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
                bPsAndelSaertilskuddProsent = BigDecimal.valueOf(55.7),
                bPsAndelSaertilskuddBelop = BigDecimal.valueOf(6684),
                barnetErSelvforsorget = false
            ),
            lopendeBidragListe = lopendeBidragListe,
            samvaersfradragGrunnlagListe = samvaersfradragListe
        )
        val (resultatBelop, resultatkode) = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertEquals(0.0, resultatBelop.toDouble()) },
            Executable { assertEquals(ResultatKodeSaertilskudd.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultatkode) }
        )
    }
}
