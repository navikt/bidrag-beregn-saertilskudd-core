package no.nav.bidrag.beregn.saertilskudd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregningImpl;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd;
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av barnebidrag")
public class SaertilskuddBeregningTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Beregner enkelt særtilskudd med full evne, dette er eksempel 1 fra John")
  @Test
  void testEnkelBeregningFullEvne() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(2500),  // lopendeBidragBelop
        BigDecimal.valueOf(2957),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500),  // opprinneligBidragBelop
        BigDecimal.valueOf(457)   // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();

    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(457)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(11069)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4242, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_INNVILGET, resultat.getResultatkode());
  }

  @DisplayName("Beregner særtilskudd som får manglende evne pga diff mellom opprinnelig og nytt samværsfradragbeløp")
  @Test
  void testBeregningManglemdeEvneØktSamvaersfradrag() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(2500),  // lopendeBidragBelop
        BigDecimal.valueOf(2958),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500),  // opprinneligBidragBelop
        BigDecimal.valueOf(457)   // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();

    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(800)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(3100)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4242, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Beregner særtilskudd som får manglende evne pga diff mellom opprinnelig og løpende bidragsbeløp")
  @Test
  void testBeregningManglendeEvnePgaHoyereLopendeBidrag() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(3000),  // lopendeBidragBelop
        BigDecimal.valueOf(2958),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500),  // opprinneligBidragBelop
        BigDecimal.valueOf(457)   // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();

    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(457)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(3456)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4242, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Ingen beregning skal gjøres når barnet er selvforsørget")
  @Test
  void testIngenBeregningBarnetErSelvforsoerget() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(3000),  // lopendeBidragBelop
        BigDecimal.valueOf(2958),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500),  // opprinneligBidragBelop
        BigDecimal.valueOf(457)   // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();

    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(457)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(10000)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242),
            true), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(0d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.BARNET_ER_SELVFORSORGET, resultat.getResultatkode());
  }

  @DisplayName("Test at særtilskudd beregnes med data fra 2 barn, dette er eksempel 2 fra John")
  @Test
  void testOkBeregningMedDataFraT0Barn() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(1700),  // lopendeBidragBelop
        BigDecimal.valueOf(3215),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1700),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));
    lopendeBidragListe.add(new LopendeBidrag(2,
        BigDecimal.valueOf(1700),  // lopendeBidragBelop
        BigDecimal.valueOf(3215),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1700),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(2,
        BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(6696)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(49.7), BigDecimal.valueOf(2982),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(2982d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_INNVILGET, resultat.getResultatkode());
  }

  @DisplayName("Test at særtilskudd beregnes med data fra 2 barn, dette er eksempel 3 fra John")
  @Test
  void testOkBeregningMedDataFraT0BarnLavereEvne() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(1500),  // lopendeBidragBelop
        BigDecimal.valueOf(3015),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1500),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));
    lopendeBidragListe.add(new LopendeBidrag(2,
        BigDecimal.valueOf(1500),  // lopendeBidragBelop
        BigDecimal.valueOf(3015),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1500),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(2,
        BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(6149)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(55.7), BigDecimal.valueOf(6684),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(6684d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_INNVILGET, resultat.getResultatkode());
  }

  @DisplayName("Test at resultatkode settes til manglende evne, data fra 2 barn, dette er eksempel 4 fra John")
  @Test
  void testManglendeEvneBeregningMedDataFraT0Barn() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(1800),  // lopendeBidragBelop
        BigDecimal.valueOf(3315),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1800),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));
    lopendeBidragListe.add(new LopendeBidrag(2,
        BigDecimal.valueOf(1800),  // lopendeBidragBelop
        BigDecimal.valueOf(3315),  // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(1800),  // opprinneligBidragBelop
        BigDecimal.valueOf(1513)  // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(2,
        BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(6149)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(55.7), BigDecimal.valueOf(6684),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(6684d, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Test fra John, løpende bidrag ble begrenset av evne og er senere indeksregulert"
      + ", samværsfradrag har økt fra 600 til 700.-")
  @Test
  void testIndeksregulertBidragEndringBelopSamvaersfradrag() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(1300), // lopendeBidragBelop
        BigDecimal.valueOf(2600), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1200), // opprinneligBidragBelop
        BigDecimal.valueOf(600)  // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(700)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(2700)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(70), BigDecimal.valueOf(5000),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(5000, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Test 5 fra John, BP har to barn")
  @Test
  void testIndeksregulertBidragHoyereSamvaersfradragManglendeEvneEksempel5FraJohn() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(5000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(2,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(5000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(2,
        BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(9962)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(62.8), BigDecimal.valueOf(7536),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(7536, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Test 6 fra John, BP har to barn")
  @Test
  void testIndeksregulertBidragHoyereSamvaersfradragFullEvneEksempel6FraJohn() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(5000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(2,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(5000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(2,
        BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(10891)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(55.1), BigDecimal.valueOf(6612),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(6612, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_INNVILGET, resultat.getResultatkode());
  }

  @DisplayName("Test 7 fra John, BP har to barn")
  @Test
  void testIndeksregulertBidragHoyereSamvaersfradragManglendeEvneEksempel7FraJohn() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(4000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(2,
        BigDecimal.valueOf(1800), // lopendeBidragBelop
        BigDecimal.valueOf(4000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(1700), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(2,
        BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(6149)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(55.7), BigDecimal.valueOf(6684),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(6684, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

  @DisplayName("Test 8 fra John, BP har to barn")
  @Test
  void testIndeksregulertBidragHoyereSamvaersfradragManglendeEvneEksempel8FraJohn() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();
    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(2900), // lopendeBidragBelop
        BigDecimal.valueOf(4000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(2800), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    lopendeBidragListe.add(new LopendeBidrag(2,
        BigDecimal.valueOf(2900), // lopendeBidragBelop
        BigDecimal.valueOf(4000), // opprinneligBPsAndelUnderholdskostnadBelop
        BigDecimal.valueOf(2800), // opprinneligBidragBelop
        BigDecimal.valueOf(1323) // opprinneligSamvaersfradragBelop
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(1,
        BigDecimal.valueOf(1513)));
    samvaersfradragListe.add(new SamvaersfradragGrunnlag(2,
        BigDecimal.valueOf(1513)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(6149)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(55.7), BigDecimal.valueOf(6684),
            false), lopendeBidragListe, samvaersfradragListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(6684, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE, resultat.getResultatkode());
  }

}
