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
import no.nav.bidrag.beregn.saertilskudd.bo.Samvaersfradrag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av barnebidrag")
public class SaertilskuddBeregningTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Beregner enkelt særtilskudd med full evne")
  @Test
  void testEnkelBeregningFullEvne() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(2500), // lopendeBidragBelop
        BigDecimal.valueOf(2958), // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500), // opprinneligBidragBelop
        BigDecimal.valueOf(457),  // opprinneligSamvaersfradragBelop
        ResultatKode.KOSTNADSBEREGNET_BIDRAG));

    var samvaersfradragListe = new ArrayList<Samvaersfradrag>();

    samvaersfradragListe.add(new Samvaersfradrag(1,
        BigDecimal.valueOf(457)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(11069), BigDecimal.valueOf(10417)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242),
            false), lopendeBidragListe, samvaersfradragListe, sjablonListe);

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
        BigDecimal.valueOf(2500), // lopendeBidragBelop
        BigDecimal.valueOf(2958), // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500), // opprinneligBidragBelop
        BigDecimal.valueOf(457),  // opprinneligSamvaersfradragBelop
        ResultatKode.KOSTNADSBEREGNET_BIDRAG));

    var samvaersfradragListe = new ArrayList<Samvaersfradrag>();

    samvaersfradragListe.add(new Samvaersfradrag(1,
        BigDecimal.valueOf(800)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(3100), BigDecimal.valueOf(10417)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242),
            false), lopendeBidragListe, samvaersfradragListe, sjablonListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4242, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.getResultatkode());
  }

  @DisplayName("Beregner særtilskudd som får manglende evne pga diff mellom opprinnelig og løpende bidragsbeløp")
  @Test
  void testBeregningManglendeEvnePgaHoyereLopendeBidrag() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(3000), // lopendeBidragBelop
        BigDecimal.valueOf(2958), // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500), // opprinneligBidragBelop
        BigDecimal.valueOf(457),  // opprinneligSamvaersfradragBelop
        ResultatKode.KOSTNADSBEREGNET_BIDRAG));

    var samvaersfradragListe = new ArrayList<Samvaersfradrag>();

    samvaersfradragListe.add(new Samvaersfradrag(1,
        BigDecimal.valueOf(457)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(3456), BigDecimal.valueOf(10417)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242),
            false), lopendeBidragListe, samvaersfradragListe, sjablonListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4242, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.getResultatkode());
  }

  @DisplayName("Ingen beregning skal gjøres når barnet er selvforsørget")
  @Test
  void testIngenBeregningBarnetErSelvforsoerget() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(3000), // lopendeBidragBelop
        BigDecimal.valueOf(2958), // opprinneligBPsAndelSaertilskuddBelop
        BigDecimal.valueOf(2500), // opprinneligBidragBelop
        BigDecimal.valueOf(457),  // opprinneligSamvaersfradragBelop
        ResultatKode.KOSTNADSBEREGNET_BIDRAG));

    var samvaersfradragListe = new ArrayList<Samvaersfradrag>();

    samvaersfradragListe.add(new Samvaersfradrag(1,
        BigDecimal.valueOf(457)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10417)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242),
            true), lopendeBidragListe, samvaersfradragListe, sjablonListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4242, resultat.getResultatBelop().doubleValue());
    assertEquals(ResultatKode.BARNET_ER_SELVFORSORGET, resultat.getResultatkode());
  }
}
