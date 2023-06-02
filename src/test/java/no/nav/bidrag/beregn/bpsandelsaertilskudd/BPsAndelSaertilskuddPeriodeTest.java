package no.nav.bidrag.beregn.bpsandelsaertilskudd;

import static java.util.Collections.emptyList;
import static no.nav.bidrag.beregn.TestUtil.NETTO_SAERTILSKUDD_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddResultat;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.NettoSaertilskuddPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.periode.BPsAndelSaertilskuddPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BPsAndelSaertilskuddPeriodeTest {

  private BeregnBPsAndelSaertilskuddGrunnlag grunnlag;

  private final BPsAndelSaertilskuddPeriode bPsAndelSaertilskuddPeriode = BPsAndelSaertilskuddPeriode.getInstance();

  @Test
  @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  void testPeriodisering() {

    lagGrunnlag("2018-07-01", "2020-08-01");

    var resultat = bPsAndelSaertilskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(3),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFom()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(35.2)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFom()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-07-01")),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFom()).isEqualTo(LocalDate.parse("2020-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isNull()
    );

    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

    lagGrunnlag("2016-01-01", "2021-01-01");
    var avvikListe = bPsAndelSaertilskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(6),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i inntektBPPeriodeListe (2018-01-01) er etter beregnDatoFra (2016-01-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i inntektBPPeriodeListe (2020-08-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );

    printAvvikListe(avvikListe);
  }

  private void lagGrunnlag(String beregnDatoFra, String beregnDatoTil) {

    var nettoSaertilskuddPeriodeListe = new ArrayList<NettoSaertilskuddPeriode>();
    var inntektBPPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriode>();

    nettoSaertilskuddPeriodeListe.add(new NettoSaertilskuddPeriode(NETTO_SAERTILSKUDD_REFERANSE,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")), BigDecimal.valueOf(1000)));

    inntektBPPeriodeListe.add(new InntektPeriode("Inntekt_20180101",
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(217666), false, false));

    inntektBMPeriodeListe.add(new InntektPeriode("Inntekt_20180101",
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000), false, false));

    inntektBBPeriodeListe.add(new InntektPeriode("Inntekt_20180101",
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(40000), false, false));

    grunnlag = new BeregnBPsAndelSaertilskuddGrunnlag(LocalDate.parse(beregnDatoFra), LocalDate.parse(beregnDatoTil),
        nettoSaertilskuddPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, lagSjablonGrunnlag());
  }

  private List<SjablonPeriode> lagSjablonGrunnlag() {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1600))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1640))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2020-07-01"), null),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1670))))));

    return sjablonPeriodeListe;
  }

  private void printGrunnlagResultat(BeregnBPsAndelSaertilskuddResultat beregnBPsAndelSaertilskuddResultat) {
    beregnBPsAndelSaertilskuddResultat.getResultatPeriodeListe().stream()
        .sorted(Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFom()))
        .forEach(sortedPR -> System.out.println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFom() + "; " + "Dato til: "
            + sortedPR.getResultatDatoFraTil().getDatoTil() + "; " + "Prosentandel: " + sortedPR.getResultatBeregning().getResultatAndelProsent()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
