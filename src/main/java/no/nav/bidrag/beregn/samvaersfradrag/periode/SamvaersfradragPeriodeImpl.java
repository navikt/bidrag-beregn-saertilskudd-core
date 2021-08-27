package no.nav.bidrag.beregn.samvaersfradrag.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPerBarn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPeriode;

public class SamvaersfradragPeriodeImpl implements SamvaersfradragPeriode {

  public SamvaersfradragPeriodeImpl(SamvaersfradragBeregning samvaersfradragBeregning) {
    this.samvaersfradragBeregning = samvaersfradragBeregning;
  }

  private SamvaersfradragBeregning samvaersfradragBeregning;

  public BeregnSamvaersfradragResultat beregnPerioder(
      BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertSamvaersfradragPeriodeListe = beregnSamvaersfradragGrunnlag.getSamvaersfradragGrunnlagPeriodeListe()
        .stream()
        .map(SamvaersfradragGrunnlagPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnSamvaersfradragGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnSamvaersfradragGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkt(beregnSamvaersfradragGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnSamvaersfradragGrunnlag.getBeregnDatoFra(), beregnSamvaersfradragGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var samvaersfradragGrunnnlagPerBarnliste = justertSamvaersfradragPeriodeListe
          .stream().filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(samvaersfradragGrunnnlagPerBarn -> new SamvaersfradragGrunnlagPerBarn(samvaersfradragGrunnnlagPerBarn.getReferanse(),
              samvaersfradragGrunnnlagPerBarn.getBarnPersonId(),
//              alderBarn,
              beregnBarnAlder(samvaersfradragGrunnnlagPerBarn.getBarnFodselsdato(), beregningsperiode.getDatoFom()),
              samvaersfradragGrunnnlagPerBarn.getSamvaersklasse())).collect(toList());

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getPeriode().overlapperMed(beregningsperiode)).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnSamvaersfradragGrunnlagPeriodisert = new GrunnlagBeregningPeriodisert(
          samvaersfradragGrunnnlagPerBarnliste, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(
          beregningsperiode,
          samvaersfradragBeregning.beregn(beregnSamvaersfradragGrunnlagPeriodisert),
          beregnSamvaersfradragGrunnlagPeriodisert));

    }

    //Slår sammen perioder med samme resultat
    return new BeregnSamvaersfradragResultat(resultatPeriodeListe);

  }


  public Integer beregnBarnAlder(LocalDate barnFodselsdato, LocalDate beregnDatoFra) {
    Integer beregnetAlder = Period.between(barnFodselsdato, beregnDatoFra).getYears();

    System.out.println("Beregnet alder: " + beregnetAlder);

    return beregnetAlder;
  }


  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(BeregnSamvaersfradragGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getPeriode());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe", sjablonPeriodeListe,
            false, false, false, false));

    // Sjekk perioder for samværsklasse
    var samvaersklassePeriodeListe = new ArrayList<Periode>();
    for (SamvaersfradragGrunnlagPeriode samvaersfradragGrunnlagPeriode : grunnlag.getSamvaersfradragGrunnlagPeriodeListe()) {
      samvaersklassePeriodeListe.add(samvaersfradragGrunnlagPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "samvaersklassePeriodeListe",
        samvaersklassePeriodeListe, false, false, true, true));

    return avvikListe;
  }
}


