package no.nav.bidrag.beregn.saertilskudd.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskuddPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidragPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.saertilskudd.bo.Samvaersfradrag;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragPeriode;


public class SaertilskuddPeriodeImpl implements SaertilskuddPeriode {
  public SaertilskuddPeriodeImpl(
      SaertilskuddBeregning saertilskuddBeregning) {
    this.saertilskuddBeregning = saertilskuddBeregning;
  }

  private final SaertilskuddBeregning saertilskuddBeregning;

  public BeregnSaertilskuddResultat beregnPerioder(
      BeregnSaertilskuddGrunnlag beregnSaertilskuddGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertBidragsevnePeriodeListe = beregnSaertilskuddGrunnlag.getBidragsevnePeriodeListe()
        .stream()
        .map(BidragsevnePeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBPsAndelSaertilskuddPeriodeListe = beregnSaertilskuddGrunnlag.getBPsAndelSaertilskuddPeriodeListe()
        .stream()
        .map(BPsAndelSaertilskuddPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertLopendeBidragPeriodeListe = beregnSaertilskuddGrunnlag.getLopendeBidragPeriodeListe()
        .stream()
        .map(LopendeBidragPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSamvaersfradragPeriodeListe = beregnSaertilskuddGrunnlag.getSamvaersfradragPeriodeListe()
        .stream()
        .map(SamvaersfradragPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnSaertilskuddGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkt(beregnSaertilskuddGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnSaertilskuddGrunnlag.getBeregnDatoFra(), beregnSaertilskuddGrunnlag.getBeregnDatoTil());


    // Bygger opp grunnlag til beregning og kaller beregningsmodulen, det skal kun være én periode for særtilskudd
    for (Periode beregningsperiode : perioder) {

      var bidragsevne = justertBidragsevnePeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(bidragsevnePeriode -> new Bidragsevne(bidragsevnePeriode.getBidragsevneBelop(),
              bidragsevnePeriode.getTjuefemProsentInntekt())).findFirst().orElse(null);

      var bPsAndelSaertilskudd = justertBPsAndelSaertilskuddPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(bPsAndelSaertilskuddPeriode -> new BPsAndelSaertilskudd(
              bPsAndelSaertilskuddPeriode.getBPsAndelSaertilskuddProsent(),
              bPsAndelSaertilskuddPeriode.getBPsAndelSaertilskuddBelop(),
              bPsAndelSaertilskuddPeriode.getBarnetErSelvforsorget())).findFirst().orElse(null);

      var lopendeBidragListe = justertLopendeBidragPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(lopendeBidragPeriode -> new LopendeBidrag(
              lopendeBidragPeriode.getSoknadsbarnPersonId(),
              lopendeBidragPeriode.getLopendeBidragBelop(),
              lopendeBidragPeriode.getOpprinneligBPsAndelUnderholdskostnadBelop(),
              lopendeBidragPeriode.getOpprinneligBidragBelop(),
              lopendeBidragPeriode.getOpprinneligSamvaersfradragBelop(),
              lopendeBidragPeriode.getResultatkode())).collect(toList());

      var samvaersfradragListe= justertSamvaersfradragPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(samvaersfradragPeriode -> new Samvaersfradrag(
              samvaersfradragPeriode.getSoknadsbarnPersonId(),
              samvaersfradragPeriode.getSamvaersfradragBelop())).collect(toList());


      // Kaller beregningsmodulen for beregningsperioden

      var grunnlagBeregning = new GrunnlagBeregning(bidragsevne, bPsAndelSaertilskudd, lopendeBidragListe,
          samvaersfradragListe);

        resultatPeriodeListe.add(new ResultatPeriode(
            beregnSaertilskuddGrunnlag.getSoknadsbarnPersonId(),
            beregningsperiode,
            saertilskuddBeregning.beregn(grunnlagBeregning), grunnlagBeregning));
    }

    return new BeregnSaertilskuddResultat(resultatPeriodeListe);
  }

  

  // Validerer at input-verdier til beregn-saertilskudd er gyldige
  public List<Avvik> validerInput(BeregnSaertilskuddGrunnlag grunnlag) {

    // Sjekk perioder for bidragsevne
    var bidragsevnePeriodeListe = new ArrayList<Periode>();
    for (BidragsevnePeriode bidragsevnePeriode : grunnlag.getBidragsevnePeriodeListe()) {
      bidragsevnePeriodeListe.add(bidragsevnePeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"bidragsevnePeriodeListe",
        bidragsevnePeriodeListe, true, true, true, true));

    // Sjekk perioder for BPs andel av saertilskudd
    var bPsAndelSaertilskuddPeriodeListe = new ArrayList<Periode>();
    for (BPsAndelSaertilskuddPeriode bPsAndelSaertilskuddPeriode : grunnlag.getBPsAndelSaertilskuddPeriodeListe()) {
      bPsAndelSaertilskuddPeriodeListe.add(bPsAndelSaertilskuddPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "bPsAndelSaertilskuddPeriodeListe",
        bPsAndelSaertilskuddPeriodeListe, true, true, true, true));

    // Sjekk perioder for lopende bidrag
    var lopendeBidragPeriodeListe = new ArrayList<Periode>();
    for (LopendeBidragPeriode lopendeBidragPeriode : grunnlag.getLopendeBidragPeriodeListe()) {
      lopendeBidragPeriodeListe.add(lopendeBidragPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "lopendeBidragPeriodeListe",
        lopendeBidragPeriodeListe, true, true, true, true));

    // Sjekk perioder for samværsfradrag
    var samvaersfradragPeriodeListe = new ArrayList<Periode>();
    for (SamvaersfradragPeriode samvaersfradragPeriode : grunnlag.getSamvaersfradragPeriodeListe()) {
      samvaersfradragPeriodeListe.add(samvaersfradragPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "samvaersfradragPeriodeListe",
        samvaersfradragPeriodeListe, true, true, true, true));

    return avvikListe;
  }
}
