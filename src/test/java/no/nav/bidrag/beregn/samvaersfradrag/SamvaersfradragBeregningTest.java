package no.nav.bidrag.beregn.samvaersfradrag;

import static no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregningImpl;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPerBarn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av samværsfradrag")
public class SamvaersfradragBeregningTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();
  private final List<SjablonPeriode> sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();


  @DisplayName("Test av beregning av samvaersfradrag for fireåring")
  @Test
  void testFireAar() {

    List<SamvaersfradragGrunnlagPerBarn> samvaersfradragGrunnlagPerBarnListe = new ArrayList<>();
    samvaersfradragGrunnlagPerBarnListe.add(
        new SamvaersfradragGrunnlagPerBarn(SAMVAERSFRADRAG_REFERANSE, 1, 4, "03")
    );

    SamvaersfradragBeregningImpl samvaersfradragBeregning = new SamvaersfradragBeregningImpl();
    GrunnlagBeregningPeriodisert resultatGrunnlag
        = new GrunnlagBeregningPeriodisert(samvaersfradragGrunnlagPerBarnListe,
        sjablonPeriodeListe);

    assertAll(
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(0).getBarnPersonId()).isEqualTo(1),
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(0)
            .getResultatSamvaersfradragBelop().doubleValue()).isEqualTo(2272));

  }

  @DisplayName("Test av beregning av samvaersfradrag for seksåring")
  @Test
  void testSeksAar() {

    List<SamvaersfradragGrunnlagPerBarn> samvaersfradragGrunnlagPerBarnListe = new ArrayList<>();
    samvaersfradragGrunnlagPerBarnListe.add(
        new SamvaersfradragGrunnlagPerBarn(SAMVAERSFRADRAG_REFERANSE, 1, 6, "03")
    );

    SamvaersfradragBeregningImpl samvaersfradragBeregning = new SamvaersfradragBeregningImpl();
    GrunnlagBeregningPeriodisert resultatGrunnlag
        = new GrunnlagBeregningPeriodisert(samvaersfradragGrunnlagPerBarnListe,
        sjablonPeriodeListe);

    assertAll(
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(0).getBarnPersonId()).isEqualTo(1),
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(0)
            .getResultatSamvaersfradragBelop().doubleValue()).isEqualTo(2716));

  }


  @DisplayName("Test av beregning av samvaersfradrag for fire-, seks- og elleveåring")
  @Test
  void testFireSeksElleveAar() {

    List<SamvaersfradragGrunnlagPerBarn> samvaersfradragGrunnlagPerBarnListe = new ArrayList<>();
    samvaersfradragGrunnlagPerBarnListe.add(
        new SamvaersfradragGrunnlagPerBarn(SAMVAERSFRADRAG_REFERANSE, 1, 4, "03")
    );

    samvaersfradragGrunnlagPerBarnListe.add(
        new SamvaersfradragGrunnlagPerBarn(SAMVAERSFRADRAG_REFERANSE, 3, 6, "03")
    );

    samvaersfradragGrunnlagPerBarnListe.add(
        new SamvaersfradragGrunnlagPerBarn(SAMVAERSFRADRAG_REFERANSE, 5, 11, "01")
    );

    SamvaersfradragBeregningImpl samvaersfradragBeregning = new SamvaersfradragBeregningImpl();
    GrunnlagBeregningPeriodisert resultatGrunnlag
        = new GrunnlagBeregningPeriodisert(samvaersfradragGrunnlagPerBarnListe,
        sjablonPeriodeListe);

    assertAll(
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).size()).isEqualTo(3),
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(0).getBarnPersonId()).isEqualTo(1),
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(1).getBarnPersonId()).isEqualTo(3),
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(2).getBarnPersonId()).isEqualTo(5),
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(0).getResultatSamvaersfradragBelop().doubleValue())
            .isEqualTo(2272),
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(1).getResultatSamvaersfradragBelop().doubleValue())
            .isEqualTo(2716),
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(2).getResultatSamvaersfradragBelop().doubleValue())
            .isEqualTo(457));

  }

  @DisplayName("Test fra John")
  @Test
  void testFraJohn() {
    List<SamvaersfradragGrunnlagPerBarn> samvaersfradragGrunnlagPerBarnListe = new ArrayList<>();
    samvaersfradragGrunnlagPerBarnListe.add(
        new SamvaersfradragGrunnlagPerBarn(SAMVAERSFRADRAG_REFERANSE, 2, 14, "01")
    );

    SamvaersfradragBeregningImpl samvaersfradragBeregning = new SamvaersfradragBeregningImpl();
    GrunnlagBeregningPeriodisert resultatGrunnlag
        = new GrunnlagBeregningPeriodisert(samvaersfradragGrunnlagPerBarnListe,
        sjablonPeriodeListe);

    assertAll(
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(0).getBarnPersonId()).isEqualTo(2),
        () -> assertThat(samvaersfradragBeregning.beregn(resultatGrunnlag).get(0)
            .getResultatSamvaersfradragBelop().doubleValue()).isEqualTo(457));

  }

}
