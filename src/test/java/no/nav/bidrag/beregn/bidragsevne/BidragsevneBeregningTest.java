package no.nav.bidrag.beregn.bidragsevne;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.BARN_I_HUSSTAND_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BOSTATUS_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SAERFRADRAG_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SKATTEKLASSE_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.bidragsevne.beregning.BidragsevneBeregningImpl;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand;
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag;
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.Test;

class BidragsevneBeregningTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();
  private final List<SjablonPeriode> sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

  @Test
  void beregnBidragsevne() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();

    BidragsevneBeregningImpl bidragsevneberegning = new BidragsevneBeregningImpl();

    // Tester beregning med ulike inntekter
    inntekter.add(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(1000000)));
    GrunnlagBeregning grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN), sjablonPeriodeListe);
    assertEquals(BigDecimal.valueOf(31859), bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    inntekter.set(0, new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(520000)));
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN), sjablonPeriodeListe);
    assertEquals(BigDecimal.valueOf(8322), bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    inntekter.set(0, new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000)));
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN), sjablonPeriodeListe);
    assertEquals(BigDecimal.valueOf(8424), bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    // Test på at beregnet bidragsevne blir satt til 0 når evne er negativ
    inntekter.set(0, new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(100000)));
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT), sjablonPeriodeListe);
    assertEquals(BigDecimal.ZERO, bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    // Test at fordel skatteklasse 2 ikke legges til beregnet evne når skatteklasse = 1
    inntekter.set(0, new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000)));
    sjablonPeriodeListe.set(0, new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12000))))));
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN), sjablonPeriodeListe);
    assertEquals(BigDecimal.valueOf(8424), bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    // Test at fordel skatteklasse 2 legges til beregnet evne når skatteklasse = 2
    inntekter.set(0, new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000)));
    sjablonPeriodeListe.set(0, new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12000))))));
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 2),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN), sjablonPeriodeListe);
    assertEquals(BigDecimal.valueOf(9424), bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    // Test at personfradrag skatteklasse 2 brukes hvis skatteklasse 2 er angitt
    sjablonPeriodeListe.set(0, new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.ZERO)))));

    sjablonPeriodeListe.set(1, new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(24000))))));

    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 2),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN), sjablonPeriodeListe);
    assertEquals(BigDecimal.valueOf(7923), bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    // Test av halvt særfradrag
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HALVT), sjablonPeriodeListe);
    assertEquals(BigDecimal.valueOf(8965), bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    // Test av bostatus MED_FLERE
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HALVT), sjablonPeriodeListe);
    assertEquals(BigDecimal.valueOf(14253), bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());

    // Tester beregning med ulike inntekter
    inntekter = new ArrayList<>(singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(480000))));
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 0),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN), sjablonPeriodeListe);

    assertEquals(BigDecimal.valueOf(9976), bidragsevneberegning.beregn(grunnlagBeregning).getResultatEvneBelop());
  }

  @Test
  void beregnMinstefradrag() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();
    inntekter.add(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(200000)));

    BidragsevneBeregningImpl bidragsevneberegning = new BidragsevneBeregningImpl();

    GrunnlagBeregning grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT), sjablonPeriodeListe);

    System.out.println(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregning,
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)));
    assertThat(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregning,
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)))
        .isEqualTo(BigDecimal.valueOf(62000));

    inntekter.set(0, new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(1000000)));
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT), sjablonPeriodeListe);

    System.out.println(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregning,
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)));
    assertThat(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregning,
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)))
        .isEqualTo(BigDecimal.valueOf(87450));
  }

  @Test
  void beregnSkattetrinnBelop() {

    BidragsevneBeregningImpl bidragsevneberegning = new BidragsevneBeregningImpl();

    ArrayList<Inntekt> inntekter = new ArrayList<>();
    inntekter.add(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000)));

    GrunnlagBeregning grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT), sjablonPeriodeListe);

    assertEquals(BigDecimal.valueOf(1400 + 16181 + 3465 + 0), bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregning));

    inntekter.set(0, new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(174600)));
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT), sjablonPeriodeListe);

    assertEquals(BigDecimal.ZERO, bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregning));

    inntekter.set(0, new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(250000)));
    grunnlagBeregning = new GrunnlagBeregning(inntekter, new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE), new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT), sjablonPeriodeListe);

    assertEquals(BigDecimal.valueOf(1315), bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregning));
  }
}
