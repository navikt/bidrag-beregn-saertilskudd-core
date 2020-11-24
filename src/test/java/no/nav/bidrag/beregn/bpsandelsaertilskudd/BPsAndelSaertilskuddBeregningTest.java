package no.nav.bidrag.beregn.bpsandelsaertilskudd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.beregning.BPsAndelSaertilskuddBeregningImpl;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av BPs andel av særtilskudd")
public class BPsAndelSaertilskuddBeregningTest {

    private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

    @DisplayName("Beregning med inntekter for alle parter")
    @Test
    void testBeregningMedInntekterForAlle() {
      var bPsAndelUnderholdskostnadBeregning = new BPsAndelSaertilskuddBeregningImpl();

      var inntektBP = new ArrayList<Inntekt>();
      var inntektBM = new ArrayList<Inntekt>();
      var inntektBB = new ArrayList<Inntekt>();

      inntektBP.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(217666)));
      inntektBM.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(400000)));
      inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(40000)));

      var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
          new GrunnlagBeregning(inntektBP, inntektBM, inntektBB, sjablonListe);

      ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

      assertAll(
          () -> assertThat(resultat).isNotNull(),
          () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(BigDecimal.valueOf(35.2))
      );
    }

  @DisplayName("Beregning med flere inntekter for alle parter, tester også det kalkuleres"
      + "riktig etter fratrekk av 30 * forhøyet forskudd på barnets inntekt")
  @Test
  void testBeregningMedFlereInntekterForAlle() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelSaertilskuddBeregningImpl();

    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(200000)));
    inntektBP.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(17666)));
    inntektBM.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(100000)));
    inntektBM.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(200000)));
    inntektBM.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(100000)));
    inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(10000)));
    inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(10000)));
    inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(10000)));
    inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(10000)));
    inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(10000)));
    inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(10000)));

    var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new GrunnlagBeregning(inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(BigDecimal.valueOf(34.7)),
        () -> assertThat(resultat.getBarnetErSelvforsorget()).isFalse()
    );
  }

  @DisplayName("Beregning der barnets inntekter er høyere enn 100 * forhøyet forskuddssats. Andel skal da bli 0")
  @Test
  void testAndelLikNullVedHoyInntektBarn() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelSaertilskuddBeregningImpl();

    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(217666)));
    inntektBM.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(400000)));
    inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(400000)));

    var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new GrunnlagBeregning(inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(BigDecimal.ZERO),
        () -> assertThat(resultat.getBarnetErSelvforsorget()).isTrue()
    );
  }


  @DisplayName("Test at beregnet andel ikke settes høyere enn 5/6 (83,3333333333). Legger inn 10 desimaler "
      + "etter ønske fra John for å få likt resultat som i Bidragskalkulator")
  @Test
  void testAtMaksAndelSettes() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelSaertilskuddBeregningImpl();

    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(1000000)));
    inntektBM.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(40000)));
    inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(40000)));


    // Beregnet andel skal da bli 92,6%, overstyres til 5/6 (83,3333333333%)
    var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new GrunnlagBeregning(inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(BigDecimal.valueOf(83.3333333333))
    );
  }


  @DisplayName("Beregning med 0 i inntekt for barn")
  @Test
  void testBeregningMedNullInntektBarn() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelSaertilskuddBeregningImpl();

    var underholdskostnad = BigDecimal.valueOf(1000);
    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(502000)));
    inntektBM.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(500000)));
    inntektBB.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.ZERO));

   var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new GrunnlagBeregning(inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(BigDecimal.valueOf(50.1))
    );
  }

}
