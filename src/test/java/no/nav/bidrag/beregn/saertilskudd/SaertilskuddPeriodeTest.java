package no.nav.bidrag.beregn.saertilskudd;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskuddPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;
import no.nav.bidrag.beregn.saertilskudd.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidragPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SaertilskuddPeriodeTest {

  private BeregnSaertilskuddGrunnlag grunnlag;

  private SaertilskuddPeriode saertilskuddPeriode = SaertilskuddPeriode.getInstance();

  public ArrayList<SjablonPeriode> sjablonPeriodeListe = new ArrayList<>();

  @Test
  @DisplayName("Test at resultatperiode er lik beregn-fra-og-tilperiode i input")
  void testPaaPeriode() {

    LocalDate beregnDatoFra = LocalDate.parse("2019-08-01");
    LocalDate beregnDatoTil = LocalDate.parse("2019-09-01");

    lagSjablonliste();

    var bidragsevnePeriode = new BidragsevnePeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(15000), BigDecimal.valueOf(16000));
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    bidragsevnePeriodeListe.add(bidragsevnePeriode);

    var bPsAndelSaertilskuddPeriode = new BPsAndelSaertilskuddPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false);
    var bPsAndelSaertilskuddPeriodeListe = new ArrayList<BPsAndelSaertilskuddPeriode>();
    bPsAndelSaertilskuddPeriodeListe.add(bPsAndelSaertilskuddPeriode);

    var lopendeBidragPeriode = new LopendeBidragPeriode(1,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000),
            ResultatKode.BIDRAG_REDUSERT_AV_EVNE);
    var lopendeBidragPeriodeListe = new ArrayList<LopendeBidragPeriode>();
    lopendeBidragPeriodeListe.add(lopendeBidragPeriode);

    var samvaersfradragPeriode = new SamvaersfradragPeriode(1,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(100));
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriode>();
    samvaersfradragPeriodeListe.add(samvaersfradragPeriode);

    BeregnSaertilskuddGrunnlag beregnSaertilskuddGrunnlag =
        new BeregnSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil, 1,
            bidragsevnePeriodeListe, bPsAndelSaertilskuddPeriodeListe, lopendeBidragPeriodeListe,
            samvaersfradragPeriodeListe, sjablonPeriodeListe);

    var resultat = saertilskuddPeriode.beregnPerioder(beregnSaertilskuddGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-09-01"))

//        () -> assertThat(
//            resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBelop()).isEqualTo(BigDecimal.valueOf(15000)),
//        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatkode()).isEqualTo(
//            ResultatKode.BIDRAG_REDUSERT_AV_EVNE)

    );

    printGrunnlagResultat(resultat);
  }

  private void lagSjablonliste(){
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2021-06-30")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(5667))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2021-06-30")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(2334))))));
  }

/*
  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

//    lagGrunnlag("2016-01-01", "2021-01-01");
    LocalDate beregnDatoFra = LocalDate.parse("2016-08-01");
    LocalDate beregnDatoTil = LocalDate.parse("2022-01-01");

    lagSjablonliste();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(15000), BigDecimal.valueOf(16000));
    var bPsAndelSaertilskudd = new BPsAndelSaertilskudd(BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false);
    var lopendeBidrag = new LopendeBidrag(BigDecimal.valueOf(1000), ResultatKode.BIDRAG_REDUSERT_AV_EVNE);
    var samvaersfradrag = BigDecimal.valueOf(100);

    BeregnSaertilskuddGrunnlag beregnSaertilskuddGrunnlag =
        new BeregnSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil, 1,
            bidragsevne, bPsAndelSaertilskudd, lopendeBidrag, samvaersfradrag,
            sjablonPeriodeListe);

    var resultat = saertilskuddPeriode.beregnPerioder(beregnSaertilskuddGrunnlag);

    var avvikListe = saertilskuddPeriode.validerInput(beregnSaertilskuddGrunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i bidragsevnePeriodeListe (2019-08-01) er etter beregnDatoFra (2016-08-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i bidragsevnePeriodeListe (2020-01-01) er før beregnDatoTil (2022-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );

    printAvvikListe(avvikListe);
  }*/



  private void printGrunnlagResultat(
      BeregnSaertilskuddResultat beregnSaertilskuddResultat) {
    beregnSaertilskuddResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                + sortedPR.getResultatDatoFraTil().getDatoTil()
                + "; " + "Resultat: " + sortedPR.getResultatBeregning().getResultatBelop()
            + sortedPR.getResultatBeregning().getResultatkode()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
