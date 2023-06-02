package no.nav.bidrag.beregn.samvaersfradrag;

import static no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SamvaersfradragPeriodeTest {

  private final SamvaersfradragPeriode samvaersfradragPeriode = SamvaersfradragPeriode.getInstance();

  @Test
  @DisplayName("Test av periodisering. Resultatperioden skal være lik beregnDatoFra -> beregnDatoTil")
  void testPeriodisering() {
    var beregnDatoFra = LocalDate.parse("2020-06-01");
    var beregnDatoTil = LocalDate.parse("2020-07-01");

    // Lag samværsinfo
    var samvaersfradragGrunnlagPeriodeListe = new ArrayList<SamvaersfradragGrunnlagPeriode>();
    samvaersfradragGrunnlagPeriodeListe.add(new SamvaersfradragGrunnlagPeriode(SAMVAERSFRADRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-10-01")), 1, LocalDate.parse("2016-03-17"), "02"));

    samvaersfradragGrunnlagPeriodeListe.add(new SamvaersfradragGrunnlagPeriode(SAMVAERSFRADRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-10-01")), 2, LocalDate.parse("2017-05-17"), "02"));

    // Lag sjabloner
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(727))))));

    BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag = new BeregnSamvaersfradragGrunnlag(beregnDatoFra, beregnDatoTil,
        samvaersfradragGrunnlagPeriodeListe, sjablonPeriodeListe);

    var resultat = samvaersfradragPeriode.beregnPerioder(beregnSamvaersfradragGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe()).hasSize(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFom()).isEqualTo(LocalDate.parse("2020-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-07-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatSamvaersfradragBelop())
            .isEqualByComparingTo(BigDecimal.valueOf(727)),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(1).getResultatSamvaersfradragBelop())
            .isEqualByComparingTo(BigDecimal.valueOf(727))
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2021-01-01");
    var barnPersonId = 1;
    var barnFodselsdato = LocalDate.parse("2014-03-17");

    // Lag samværsinfo
    var samvaersklassePeriodeListe = new ArrayList<SamvaersfradragGrunnlagPeriode>();
    samvaersklassePeriodeListe.add(new SamvaersfradragGrunnlagPeriode(SAMVAERSFRADRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-07-01")), barnPersonId, barnFodselsdato, "02"));

    // Lag sjabloner
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(727))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), null),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1052))))));

    BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag = new BeregnSamvaersfradragGrunnlag(beregnDatoFra, beregnDatoTil,
        samvaersklassePeriodeListe, sjablonPeriodeListe);

    var avvikListe = samvaersfradragPeriode.validerInput(beregnSamvaersfradragGrunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(2),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i samvaersklassePeriodeListe (2019-01-01) er etter beregnDatoFra (2018-07-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i samvaersklassePeriodeListe (2020-07-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );

    printAvvikListe(avvikListe);
  }


  private void printGrunnlagResultat(BeregnSamvaersfradragResultat beregnSamvaersfradragResultat) {
    beregnSamvaersfradragResultat.getResultatPeriodeListe().stream()
        .sorted(Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFom()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFom() + "; " + "Dato til: "
                + sortedPR.getResultatDatoFraTil().getDatoTil() + "; " + "Samvaersfradragsbeløp: "
                + sortedPR.getResultatBeregningListe().get(0).getResultatSamvaersfradragBelop()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
