package no.nav.bidrag.beregn.saertilskudd.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;


public class SaertilskuddPeriodeImpl implements SaertilskuddPeriode {
  public SaertilskuddPeriodeImpl(
      SaertilskuddBeregning saertilskuddBeregning) {
    this.saertilskuddBeregning = saertilskuddBeregning;
  }

  private SaertilskuddBeregning saertilskuddBeregning;

  public BeregnSaertilskuddResultat beregnPerioder(
      BeregnSaertilskuddGrunnlag beregnSaertilskuddGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertSjablonPeriodeListe = beregnSaertilskuddGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnSaertilskuddGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkt(beregnSaertilskuddGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnSaertilskuddGrunnlag.getBeregnDatoFra(), beregnSaertilskuddGrunnlag.getBeregnDatoTil());


    // Bygger opp grunnlag til beregning og kaller beregningsmodulen, det skal kun være én periode for særtilskudd
    for (Periode beregningsperiode : perioder) {

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for beregningsperioden
      var grunnlagBeregning = new GrunnlagBeregning(
          new Bidragsevne(
          beregnSaertilskuddGrunnlag.getBidragsevne().getBidragsevneBelop(),
              beregnSaertilskuddGrunnlag.getBidragsevne().getTjuefemProsentInntekt()),
          new BPsAndelSaertilskudd(
              beregnSaertilskuddGrunnlag.getBPsAndelSaertilskudd().getBPsAndelSaertilskuddProsent(),
              beregnSaertilskuddGrunnlag.getBPsAndelSaertilskudd().getBPsAndelSaertilskuddBelop(),
              beregnSaertilskuddGrunnlag.getBPsAndelSaertilskudd().getBarnetErSelvforsorget()),
          new LopendeBidrag(
          beregnSaertilskuddGrunnlag.getLopendeBidrag().getLopendeBidragBelop(),
          beregnSaertilskuddGrunnlag.getLopendeBidrag().getResultatkode()),
          beregnSaertilskuddGrunnlag.getSamvaersfradragBelop(),
          sjablonliste);


        resultatPeriodeListe.add(new ResultatPeriode(
            beregnSaertilskuddGrunnlag.getSoknadsbarnPersonId(),
            beregningsperiode,
            saertilskuddBeregning.beregn(grunnlagBeregning), grunnlagBeregning));



    }

    return new BeregnSaertilskuddResultat(resultatPeriodeListe);
  }

  

  // Validerer at input-verdier til beregn-saertilskudd er gyldige
  public List<Avvik> validerInput(BeregnSaertilskuddGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe", sjablonPeriodeListe,
            false, false, false, false));

    return avvikListe;
  }
}
