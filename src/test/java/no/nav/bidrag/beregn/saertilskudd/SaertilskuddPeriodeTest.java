package no.nav.bidrag.beregn.saertilskudd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskuddPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;
import no.nav.bidrag.beregn.saertilskudd.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidragPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlagPeriode;
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SaertilskuddPeriodeTest {

  private BeregnSaertilskuddGrunnlag grunnlag;

  private SaertilskuddPeriode saertilskuddPeriode = SaertilskuddPeriode.getInstance();

  @Test
  @DisplayName("Test at resultatperiode er lik beregn-fra-og-tilperiode i input og ingen andre perioder dannes")
  void testPaaPeriode() {

    LocalDate beregnDatoFra = LocalDate.parse("2019-08-01");
    LocalDate beregnDatoTil = LocalDate.parse("2019-09-01");

    var bidragsevnePeriode = new BidragsevnePeriode(TestUtil.BIDRAGSEVNE_REFERANSE,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-07-01")),
        BigDecimal.valueOf(11000));
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    bidragsevnePeriodeListe.add(bidragsevnePeriode);

    bidragsevnePeriode = new BidragsevnePeriode(TestUtil.BIDRAGSEVNE_REFERANSE,
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(11069));
    bidragsevnePeriodeListe.add(bidragsevnePeriode);

    var bPsAndelSaertilskuddPeriode = new BPsAndelSaertilskuddPeriode(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(60.6), BigDecimal.valueOf(4242), false);
    var bPsAndelSaertilskuddPeriodeListe = new ArrayList<BPsAndelSaertilskuddPeriode>();
    bPsAndelSaertilskuddPeriodeListe.add(bPsAndelSaertilskuddPeriode);

    var lopendeBidragPeriode = new LopendeBidragPeriode(TestUtil.LOPENDE_BIDRAG_REFERANSE,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-01-01")), 1,
        BigDecimal.valueOf(2500), // lopendeBidragBelop
            BigDecimal.valueOf(2958), // opprinneligBPsAndelSaertilskuddBelop
            BigDecimal.valueOf(2500), // opprinneligBidragBelop
            BigDecimal.valueOf(457)  // opprinneligSamvaersfradragBelop
    );
    var lopendeBidragPeriodeListe = new ArrayList<LopendeBidragPeriode>();
    lopendeBidragPeriodeListe.add(lopendeBidragPeriode);

    var samvaersfradragPeriode = new SamvaersfradragGrunnlagPeriode(TestUtil.SAMVAERSFRADRAG_REFERANSE, 1,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(457));
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragGrunnlagPeriode>();
    samvaersfradragPeriodeListe.add(samvaersfradragPeriode);

    BeregnSaertilskuddGrunnlag beregnSaertilskuddGrunnlag =
        new BeregnSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil, 1,
            bidragsevnePeriodeListe, bPsAndelSaertilskuddPeriodeListe, lopendeBidragPeriodeListe,
            samvaersfradragPeriodeListe);

    var resultat = saertilskuddPeriode.beregnPerioder(beregnSaertilskuddGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFra()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getResultatBelop().doubleValue()).isEqualTo(4242),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getResultatkode())
            .isEqualTo(ResultatKode.SAERTILSKUDD_INNVILGET));

    printGrunnlagResultat(resultat);
  }

  private void printGrunnlagResultat(
      BeregnSaertilskuddResultat beregnSaertilskuddResultat) {
    beregnSaertilskuddResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getPeriode().getDatoFra()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getPeriode().getDatoFra() + "; " + "Dato til: "
                + sortedPR.getPeriode().getDatoTil()
                + "; " + "Resultat: " + sortedPR.getResultat().getResultatBelop()
            + sortedPR.getResultat().getResultatkode()));
  }
}
