package no.nav.bidrag.beregn.saertilskudd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregningImpl;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd;
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SaertilskuddBeregningTest {

  @DisplayName("Beregner enkelt særtilskudd med full evne")
  @Test
  void testEnkelBeregningFullEvne() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(2500),  // lopendeBidragBelop
        BigDecimal.valueOf(2957),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500),  // opprinneligBidragBelop
        BigDecimal.valueOf(457)   // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();

    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(457)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(11069)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4242, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_INNVILGET, resultat.getResultatkode());
  }

  @DisplayName("Beregner særtilskudd som får manglende evne pga diff mellom opprinnelig og nytt samværsfradragbeløp")
  @Test
  void testBeregningManglemdeEvneØktSamvaersfradrag() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(2500),  // lopendeBidragBelop
        BigDecimal.valueOf(2958),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500),  // opprinneligBidragBelop
        BigDecimal.valueOf(457)   // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();

    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(800)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(3100)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(0d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Beregner særtilskudd som får manglende evne pga diff mellom opprinnelig og løpende bidragsbeløp")
  @Test
  void testBeregningManglendeEvnePgaHoyereLopendeBidrag() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(3000),  // lopendeBidragBelop
        BigDecimal.valueOf(2958),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500),  // opprinneligBidragBelop
        BigDecimal.valueOf(457)   // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();

    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(457)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(3456)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(0d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Ingen beregning skal gjøres når barnet er selvforsørget")
  @Test
  void testIngenBeregningBarnetErSelvforsoerget() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(3000),  // lopendeBidragBelop
        BigDecimal.valueOf(2958),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500),  // opprinneligBidragBelop
        BigDecimal.valueOf(457)   // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();

    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(457)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242), true),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(0d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.BARNET_ER_SELVFORSORGET, resultat.getResultatkode());
  }

  @DisplayName("Beregning med data fra 2 barn")
  @Test
  void testOkBeregningMedDataFraToBarn() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(1700),  // lopendeBidragBelop
        BigDecimal.valueOf(3215),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1700),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 2,
        BigDecimal.valueOf(1700),  // lopendeBidragBelop
        BigDecimal.valueOf(3215),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1700),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 2, BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(6696)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(49.7), BigDecimal.valueOf(2982), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(2982d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_INNVILGET, resultat.getResultatkode());
  }

  @DisplayName("Beregning med data fra 2 barn, lavere evne")
  @Test
  void testOkBeregningMedDataFraToBarnLavereEvne() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(1500),  // lopendeBidragBelop
        BigDecimal.valueOf(3015),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1500),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 2,
        BigDecimal.valueOf(1500),  // lopendeBidragBelop
        BigDecimal.valueOf(3015),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1500),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 2, BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(6149)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(55.7), BigDecimal.valueOf(6684), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(6684d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_INNVILGET, resultat.getResultatkode());
  }

  @DisplayName("Beregning med data fra 2 barn, manglende evne")
  @Test
  void testManglendeEvneBeregningMedDataFraToBarn() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(1800),  // lopendeBidragBelop
        BigDecimal.valueOf(3315),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1800),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 2,
        BigDecimal.valueOf(1800),  // lopendeBidragBelop
        BigDecimal.valueOf(3315),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1800),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 2, BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(6149)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(55.7), BigDecimal.valueOf(6684), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(0d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Løpende bidrag ble begrenset av evne og er senere indeksregulert, samværsfradrag har økt fra 600 til 700")
  @Test
  void testIndeksregulertBidragEndringBelopSamvaersfradrag() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(1300), // lopendeBidragBelop
        BigDecimal.valueOf(2600), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1200), // opprinneligBidragBelop
        BigDecimal.valueOf(600)  // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(700)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(2700)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(70), BigDecimal.valueOf(5000), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(0d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Indeksregulert bidrag, høyere samværsfradrag, manglende evne")
  @Test
  void testIndeksregulertBidragHoyereSamvaersfradragManglendeEvne() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(5000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 2,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(5000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 2, BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(9962)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(62.8), BigDecimal.valueOf(7536), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(0d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Indeksregulert bidrag, høyere samværsfradrag, full evne")
  @Test
  void testIndeksregulertBidragHoyereSamvaersfradragFullEvne() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(5000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 2,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(5000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 2, BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10891)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(55.1), BigDecimal.valueOf(6612), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(6612, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_INNVILGET, resultat.getResultatkode());
  }

  @DisplayName("Indeksregulert bidrag, høyere samværsfradrag, manglende evne")
  @Test
  void testIndeksregulertBidragHoyereSamvaersfradragManglendeEvne2() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(4000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 2,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(4000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 2, BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(6149)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(55.7), BigDecimal.valueOf(6684), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(0d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Indeksregulert bidrag, høyere samværsfradrag, manglende evne")
  @Test
  void testIndeksregulertBidragHoyereSamvaersfradragManglendeEvne3() {
    SaertilskuddBeregningImpl saertilskuddBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 1,
        BigDecimal.valueOf(2900), // lopendeBidragBelop
        BigDecimal.valueOf(4000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(2800), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE, 2,
        BigDecimal.valueOf(2900), // lopendeBidragBelop
        BigDecimal.valueOf(4000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(2800), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1, BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE, 2, BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(6149)),
        new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(55.7), BigDecimal.valueOf(6684), false),
        lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = saertilskuddBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(0d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }
}
