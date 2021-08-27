package no.nav.bidrag.beregn.bidragsevne.periode;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bidragsevne.beregning.Bidragsevneberegning;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHustandPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneResultat;
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus;
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag;
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse;
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode;
import no.nav.bidrag.beregn.felles.InntektUtil;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.Rolle;
import no.nav.bidrag.beregn.felles.enums.SoknadType;
import no.nav.bidrag.beregn.felles.inntekt.InntektPeriodeGrunnlag;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;


public class BidragsevnePeriodeImpl implements BidragsevnePeriode {

  public BidragsevnePeriodeImpl(Bidragsevneberegning bidragsevneberegning) {
    this.bidragsevneberegning = bidragsevneberegning;
  }

  //  private Bidragsevneberegning bidragsevneberegning = Bidragsevneberegning.getInstance();
  private Bidragsevneberegning bidragsevneberegning;

  public BeregnBidragsevneResultat beregnPerioder(
      BeregnBidragsevneGrunnlag beregnBidragsevneGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
    var justertSjablonPeriodeListe = beregnBidragsevneGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektPeriodeListe = justerInntekter(beregnBidragsevneGrunnlag.getInntektPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSkatteklassePeriodeListe = beregnBidragsevneGrunnlag.getSkatteklassePeriodeListe()
        .stream()
        .map(SkatteklassePeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBostatusPeriodeListe = beregnBidragsevneGrunnlag.getBostatusPeriodeListe()
        .stream()
        .map(BostatusPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertAntallBarnIEgetHusholdPeriodeListe = beregnBidragsevneGrunnlag.getAntallBarnIEgetHusholdPeriodeListe()
        .stream()
        .map(BarnIHustandPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSaerfradragPeriodeListe = beregnBidragsevneGrunnlag.getSaerfradragPeriodeListe()
        .stream()
        .map(SaerfradragPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnBidragsevneGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(justertInntektPeriodeListe)
        .addBruddpunkter(justertSkatteklassePeriodeListe)
        .addBruddpunkter(justertBostatusPeriodeListe)
        .addBruddpunkter(justertAntallBarnIEgetHusholdPeriodeListe)
        .addBruddpunkter(justertSaerfradragPeriodeListe)
        .addBruddpunkt(beregnBidragsevneGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnBidragsevneGrunnlag.getBeregnDatoFra(), beregnBidragsevneGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnBidragsevneGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFom(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    for (Periode beregningsperiode : perioder) {

      var inntektListe = justertInntektPeriodeListe.stream().filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getReferanse(), inntektPeriode.getInntektType(), inntektPeriode.getInntektBelop()))
          .collect(toList());

      var skatteklasse = justertSkatteklassePeriodeListe.stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(skatteklassePeriode -> new Skatteklasse(skatteklassePeriode.getReferanse(), skatteklassePeriode.getSkatteklasse())).findFirst().orElse(null);

      var bostatus = justertBostatusPeriodeListe.stream().filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(bostatusPeriode -> new Bostatus(bostatusPeriode.getReferanse(), bostatusPeriode.getBostatusKode())).findFirst().orElse(null);

      var barnIHusstand = justertAntallBarnIEgetHusholdPeriodeListe.stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode)).map(barnIHusstandPeriode -> new BarnIHusstand(barnIHusstandPeriode.getReferanse(), barnIHusstandPeriode.getAntallBarn())).findFirst().orElse(null);

      var saerfradrag = justertSaerfradragPeriodeListe.stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode)).map(saerfradragPeriode -> new Saerfradrag(saerfradragPeriode.getReferanse(), saerfradragPeriode.getSaerfradragKode())).findFirst().orElse(null);

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getPeriode().overlapperMed(beregningsperiode)).collect(toList());

//      System.out.println("Beregner bidragsevne for periode: " + beregningsperiode.getDatoFra() + " " + beregningsperiode.getDatoTil());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnBidragsevneGrunnlagPeriodisert = new GrunnlagBeregning(inntektListe, skatteklasse, bostatus,
          barnIHusstand,
          saerfradrag, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode, bidragsevneberegning.beregn(beregnBidragsevneGrunnlagPeriodisert),
          beregnBidragsevneGrunnlagPeriodisert));
    }

    //Slår sammen perioder med samme resultat
    return new BeregnBidragsevneResultat(resultatPeriodeListe);

  }

  // Justerer inntekter basert på regler definert i InntektUtil (bidrag-beregn-felles)
  private List<InntektPeriode> justerInntekter(List<InntektPeriode> inntektPeriodeListe) {

    if (inntektPeriodeListe.isEmpty()) {
      return inntektPeriodeListe;
    }

    var justertInntektPeriodeListe = InntektUtil.justerInntekter(inntektPeriodeListe.stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getPeriode(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop(), false, false))
        .collect(toList()));

    return justertInntektPeriodeListe.stream()
        .map(inntektGrunnlag -> new InntektPeriode(inntektGrunnlag.getReferanse(), inntektGrunnlag.getPeriode(), inntektGrunnlag.getType(),
            inntektGrunnlag.getBelop()))
        .sorted(comparing(inntektPeriode -> inntektPeriode.getPeriodeDatoFraTil().getDatoFom()))
        .collect(toList());
  }


  // Validerer at input-verdier til bidragsevneberegning er gyldige
  public List<Avvik> validerInput(BeregnBidragsevneGrunnlag beregnBidragsevneGrunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : beregnBidragsevneGrunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getPeriode());
    }
    var avvikListe = new ArrayList<>(PeriodeUtil.validerInputDatoer(beregnBidragsevneGrunnlag.getBeregnDatoFra(),
        beregnBidragsevneGrunnlag.getBeregnDatoTil(), "sjablonPeriodeListe", sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for inntekt
    var inntektPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektPeriode : beregnBidragsevneGrunnlag.getInntektPeriodeListe()) {
      inntektPeriodeListe.add(inntektPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(beregnBidragsevneGrunnlag.getBeregnDatoFra(),
        beregnBidragsevneGrunnlag.getBeregnDatoTil(), "inntektPeriodeListe", inntektPeriodeListe, false, true, false, true));

    // Sjekk perioder for skatteklasse
    var skatteklassePeriodeListe = new ArrayList<Periode>();
    for (SkatteklassePeriode skatteklassePeriode : beregnBidragsevneGrunnlag.getSkatteklassePeriodeListe()) {
      skatteklassePeriodeListe.add(skatteklassePeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(beregnBidragsevneGrunnlag.getBeregnDatoFra(),
        beregnBidragsevneGrunnlag.getBeregnDatoTil(), "skatteklassePeriodeListe", skatteklassePeriodeListe, true, true, true, true));

    // Sjekk perioder for bostatus
    var bostatusPeriodeListe = new ArrayList<Periode>();
    for (BostatusPeriode bostatusPeriode : beregnBidragsevneGrunnlag.getBostatusPeriodeListe()) {
      bostatusPeriodeListe.add(bostatusPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(beregnBidragsevneGrunnlag.getBeregnDatoFra(),
        beregnBidragsevneGrunnlag.getBeregnDatoTil(), "bostatusPeriodeListe", bostatusPeriodeListe, true, true, true, true));

    // Sjekk perioder for antall barn i eget hushold
    var antallBarnIEgetHusholdPeriodeListe = new ArrayList<Periode>();
    for (BarnIHustandPeriode barnIHustandPeriode : beregnBidragsevneGrunnlag.getAntallBarnIEgetHusholdPeriodeListe()) {
      antallBarnIEgetHusholdPeriodeListe.add(barnIHustandPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(beregnBidragsevneGrunnlag.getBeregnDatoFra(),
        beregnBidragsevneGrunnlag.getBeregnDatoTil(), "antallBarnIEgetHusholdPeriodeListe", antallBarnIEgetHusholdPeriodeListe, false, false,
        false, true));

    // Sjekk perioder for særfradrag
    var saerfradragPeriodeListe = new ArrayList<Periode>();
    for (SaerfradragPeriode saerfradragPeriode : beregnBidragsevneGrunnlag.getSaerfradragPeriodeListe()) {
      saerfradragPeriodeListe.add(saerfradragPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(beregnBidragsevneGrunnlag.getBeregnDatoFra(),
        beregnBidragsevneGrunnlag.getBeregnDatoTil(), "saerfradragPeriodeListe", saerfradragPeriodeListe, true, true, true, true));

    // Valider inntekter
    var inntektGrunnlagListe = beregnBidragsevneGrunnlag.getInntektPeriodeListe().stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getPeriode(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop(), false, false))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.BIDRAGSPLIKTIG));

    return avvikListe;
  }
}
