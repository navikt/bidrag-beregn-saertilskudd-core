package no.nav.bidrag.beregn.bidragsevne;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.bidragsevne.beregning.BidragsevneberegningImpl;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BidragsevneBeregningTest")
class BidragsevneberegningTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @Test
  void beregn() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();

    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    // Tester beregning med ulike inntekter
    inntekter.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(1000000)));
    GrunnlagBeregning grunnlagBeregning
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(1),
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(31859),
        bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(520000)));
    GrunnlagBeregning grunnlagBeregning2
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(1),
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(8322),
        bidragsevneberegning.beregn(grunnlagBeregning2).getResultatEvneBelop());

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(666000)));
    GrunnlagBeregning grunnlagBeregning3
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(3),
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(8424),
        bidragsevneberegning.beregn(grunnlagBeregning3).getResultatEvneBelop());

    // Test på at beregnet bidragsevne blir satt til 0 når evne er negativ
    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(100000)));
    GrunnlagBeregning grunnlagBeregning4
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.MED_ANDRE, BigDecimal.valueOf(1),
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.ZERO,
        bidragsevneberegning.beregn(grunnlagBeregning4).getResultatEvneBelop());

    // Test at fordel skatteklasse 2 ikke legges til beregnet evne når skatteklasse = 1
    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(666000)));
    sjablonListe.set(0, new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12000)))));

    GrunnlagBeregning grunnlagBeregning5
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(3),
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(8424),
        bidragsevneberegning.beregn(grunnlagBeregning5).getResultatEvneBelop());

    // Test at fordel skatteklasse 2 legges til beregnet evne når skatteklasse = 2
    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(666000)));
    sjablonListe.set(0, new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12000)))));

    GrunnlagBeregning grunnlagBeregning6
        = new GrunnlagBeregning(inntekter, 2, BostatusKode.ALENE, BigDecimal.valueOf(3),
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(9424),
        bidragsevneberegning.beregn(grunnlagBeregning6).getResultatEvneBelop());

    // Test at personfradrag skatteklasse 2 brukes hvis skatteklasse 2 er angitt
    sjablonListe.set(0, new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.ZERO))));

    sjablonListe.set(1, new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(24000)))));

    GrunnlagBeregning grunnlagBeregning7
        = new GrunnlagBeregning(inntekter, 2, BostatusKode.ALENE, BigDecimal.valueOf(3),
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(7923),
        bidragsevneberegning.beregn(grunnlagBeregning7).getResultatEvneBelop());


    // Test av halvt særfradrag
    GrunnlagBeregning grunnlagBeregning8
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(3),
        SaerfradragKode.HALVT, sjablonListe);
    assertEquals(BigDecimal.valueOf(8965),
        bidragsevneberegning.beregn(grunnlagBeregning8).getResultatEvneBelop());

    // Test av bostatus MED_FLERE
    GrunnlagBeregning grunnlagBeregning9
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.MED_ANDRE, BigDecimal.valueOf(3),
        SaerfradragKode.HALVT, sjablonListe);
    assertEquals(BigDecimal.valueOf(14253),
        bidragsevneberegning.beregn(grunnlagBeregning9).getResultatEvneBelop());

  }

  @Test
  void beregnMinstefradrag() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();
    inntekter.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(200000)));

    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    GrunnlagBeregning grunnlagBeregning
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(1),
        SaerfradragKode.HELT, sjablonListe);
    System.out.println(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregning,
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)));
    assertThat(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregning,
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)))
        .isEqualTo(BigDecimal.valueOf(62000));

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(1000000)));
    GrunnlagBeregning grunnlagBeregning2
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(1),
        SaerfradragKode.HELT, sjablonListe);
    System.out.println(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregning2,
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)));
    assertThat(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregning2,
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)))
        .isEqualTo(BigDecimal.valueOf(87450));
  }

  @Test
  void beregnSkattetrinnBelop() {

    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    ArrayList<Inntekt> inntekter = new ArrayList<>();
    inntekter.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(666000)));

    GrunnlagBeregning grunnlagBeregning
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(1),
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.valueOf(1400+16181+3465+0),
        bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregning));

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(174600)));
    GrunnlagBeregning grunnlagBeregning2
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(1),
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.ZERO,
        bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregning2));

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(250000)));
    GrunnlagBeregning grunnlagBeregning3
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(1),
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.valueOf(1315),
        bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregning3));
  }

  @Test
  void TestFraJohn() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();

    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    // Tester beregning med ulike inntekter
    inntekter.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(300000)));
    GrunnlagBeregning grunnlagBeregning
        = new GrunnlagBeregning(inntekter, 1, BostatusKode.ALENE, BigDecimal.valueOf(0),
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.valueOf(1217),
        bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

  }
}
