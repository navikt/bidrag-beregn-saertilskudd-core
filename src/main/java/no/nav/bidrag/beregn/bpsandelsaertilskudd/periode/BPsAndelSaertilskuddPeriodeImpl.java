package no.nav.bidrag.beregn.bpsandelsaertilskudd.periode;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.beregning.BPsAndelSaertilskuddBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddResultat;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.felles.InntektUtil;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.Rolle;
import no.nav.bidrag.beregn.felles.enums.SoknadType;
import no.nav.bidrag.beregn.felles.inntekt.InntektGrunnlag;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;

public class BPsAndelSaertilskuddPeriodeImpl implements BPsAndelSaertilskuddPeriode {

  public BPsAndelSaertilskuddPeriodeImpl(
      BPsAndelSaertilskuddBeregning bPsAndelSaertilskuddBeregning) {
    this.bPsAndelSaertilskuddBeregning = bPsAndelSaertilskuddBeregning;
  }

  private BPsAndelSaertilskuddBeregning bPsAndelSaertilskuddBeregning;

  public BeregnBPsAndelSaertilskuddResultat beregnPerioder(
      BeregnBPsAndelSaertilskuddGrunnlag beregnBPsAndelSaertilskuddGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)

    var justertInntektBPPeriodeListe = justerInntekter(beregnBPsAndelSaertilskuddGrunnlag.getInntektBPPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBMPeriodeListe = justerInntekter(beregnBPsAndelSaertilskuddGrunnlag.getInntektBMPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBBPeriodeListe = justerInntekter(beregnBPsAndelSaertilskuddGrunnlag.getInntektBBPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnBPsAndelSaertilskuddGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Regler for beregning av BPs andel ble endret fra 01.01.2009, alle perioder etter da skal beregnes på ny måte.
    // Det må derfor legges til brudd på denne datoen
    var datoRegelendringer = new ArrayList<Periode>();
    datoRegelendringer.add(new Periode(LocalDate.parse("2009-01-01"), LocalDate.parse("2009-01-01")));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnBPsAndelSaertilskuddGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(datoRegelendringer)
        .addBruddpunkter(justertInntektBPPeriodeListe)
        .addBruddpunkter(justertInntektBMPeriodeListe)
        .addBruddpunkter(justertInntektBBPeriodeListe)
        .addBruddpunkt(beregnBPsAndelSaertilskuddGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnBPsAndelSaertilskuddGrunnlag.getBeregnDatoFra(), beregnBPsAndelSaertilskuddGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnBPsAndelSaertilskuddGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var inntektBP = justertInntektBPPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getInntektType(),
              inntektPeriode.getInntektBelop())).collect(toList());

      var inntektBM = justertInntektBMPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getInntektType(),
              inntektPeriode.getInntektBelop())).collect(toList());

      var inntektBB = justertInntektBBPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getInntektType(),
              inntektPeriode.getInntektBelop())).collect(toList());

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnBPsAndelSaertilskuddGrunnlagPeriodisert = new GrunnlagBeregning(
          inntektBP, inntektBM, inntektBB, sjablonliste);

      // Beregner
        resultatPeriodeListe.add(new ResultatPeriode(
            beregnBPsAndelSaertilskuddGrunnlag.getSoknadsbarnPersonId(),
            beregningsperiode, bPsAndelSaertilskuddBeregning.beregn(beregnBPsAndelSaertilskuddGrunnlagPeriodisert),
            beregnBPsAndelSaertilskuddGrunnlagPeriodisert));

    }

    //Slår sammen perioder med samme resultat
    return new BeregnBPsAndelSaertilskuddResultat(resultatPeriodeListe);
  }

  // Justerer inntekter basert på regler definert i InntektUtil (bidrag-beregn-felles)
  private List<InntektPeriode> justerInntekter(List<InntektPeriode> inntektPeriodeListe) {

    if (inntektPeriodeListe.isEmpty()) {
      return inntektPeriodeListe;
    }

    var justertInntektPeriodeListe = InntektUtil.justerInntekter(inntektPeriodeListe.stream()
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList()));

    return justertInntektPeriodeListe.stream()
        .map(inntektGrunnlag -> new InntektPeriode(inntektGrunnlag.getInntektDatoFraTil(), inntektGrunnlag.getInntektType(),
            inntektGrunnlag.getInntektBelop()))
        .sorted(comparing(inntektPeriode -> inntektPeriode.getInntektDatoFraTil().getDatoFra()))
        .collect(toList());
  }


  // Validerer at input-verdier til BPsAndelSaertilskuddsberegning er gyldige
  public List<Avvik> validerInput(BeregnBPsAndelSaertilskuddGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe",
            sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for inntektBP
    var inntektBPPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBPPeriode : grunnlag.getInntektBPPeriodeListe()) {
      inntektBPPeriodeListe.add(inntektBPPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBPPeriodeListe",
        inntektBPPeriodeListe, false, true, false, true));

    // Sjekk perioder for inntektBM
    var inntektBMPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBMPeriode : grunnlag.getInntektBMPeriodeListe()) {
      inntektBMPeriodeListe.add(inntektBMPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBMPeriodeListe",
        inntektBMPeriodeListe, false, true, false, true));

    // Sjekk perioder for inntektBB
    var inntektBBPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBBPeriode : grunnlag.getInntektBBPeriodeListe()) {
      inntektBBPeriodeListe.add(inntektBBPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBBPeriodeListe",
        inntektBPPeriodeListe, false, true, false, true));

    // Valider inntekter BP
    var inntektGrunnlagListe = grunnlag.getInntektBPPeriodeListe().stream()
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.BIDRAGSPLIKTIG));

    // Valider inntekter BM
    inntektGrunnlagListe = grunnlag.getInntektBMPeriodeListe().stream()
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.BIDRAGSMOTTAKER));

    // Valider inntekter BB
    inntektGrunnlagListe = grunnlag.getInntektBBPeriodeListe().stream()
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.SOKNADSBARN));

    return avvikListe;
  }
}
