package no.nav.bidrag.beregn.bpsandelsaertilskudd.periode;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.beregning.BPsAndelSaertilskuddBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddResultat;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.NettoSaertilskuddPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.felles.InntektUtil;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.inntekt.InntektPeriodeGrunnlag;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;

public class BPsAndelSaertilskuddPeriodeImpl implements BPsAndelSaertilskuddPeriode {

  public BPsAndelSaertilskuddPeriodeImpl(BPsAndelSaertilskuddBeregning bPsAndelSaertilskuddBeregning) {
    this.bPsAndelSaertilskuddBeregning = bPsAndelSaertilskuddBeregning;
  }

  private final BPsAndelSaertilskuddBeregning bPsAndelSaertilskuddBeregning;

  public BeregnBPsAndelSaertilskuddResultat beregnPerioder(BeregnBPsAndelSaertilskuddGrunnlag beregnBPsAndelSaertilskuddGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)

    var justertSjablonPeriodeListe = beregnBPsAndelSaertilskuddGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertNettoSaertilskuddPeriodeListe = beregnBPsAndelSaertilskuddGrunnlag.getNettoSaertilskuddPeriodeListe()
        .stream()
        .map(NettoSaertilskuddPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBPPeriodeListe = beregnBPsAndelSaertilskuddGrunnlag.getInntektBPPeriodeListe()
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBMPeriodeListe = behandlUtvidetBarnetrygd(beregnBPsAndelSaertilskuddGrunnlag.getInntektBMPeriodeListe(),
        justertSjablonPeriodeListe)
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBBPeriodeListe = beregnBPsAndelSaertilskuddGrunnlag.getInntektBBPeriodeListe()
        .stream()
        .map(InntektPeriode::new)
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
    if ((perioder.size() > 1) &&
        (perioder.get(perioder.size() - 2).getDatoTil().equals(beregnBPsAndelSaertilskuddGrunnlag.getBeregnDatoTil())) &&
        (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
      var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFom(), null);
      perioder.remove(perioder.size() - 1);
      perioder.remove(perioder.size() - 1);
      perioder.add(nyPeriode);
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var nettoSaertilskuddBelop = justertNettoSaertilskuddPeriodeListe.stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(NettoSaertilskuddPeriode::getNettoSaertilskuddBelop)
          .findFirst()
          .orElseThrow(
              () -> new IllegalArgumentException("Grunnlagsobjekt NETTO_SAERTILSKUDD mangler data for periode: " + beregningsperiode.getPeriode()));

      var inntektBP = justertInntektBPPeriodeListe.stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getReferanse(), inntektPeriode.getInntektType(), inntektPeriode.getInntektBelop(), false,
              false))
          .toList();

      var inntektBM = justertInntektBMPeriodeListe.stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getReferanse(), inntektPeriode.getInntektType(), inntektPeriode.getInntektBelop(), false,
              false))
          .toList();

      var inntektBB = justertInntektBBPeriodeListe.stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getReferanse(), inntektPeriode.getInntektType(), inntektPeriode.getInntektBelop(), false,
              false))
          .toList();

      var sjablonliste = justertSjablonPeriodeListe.stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .toList();

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnBPsAndelSaertilskuddGrunnlagPeriodisert = new GrunnlagBeregning(nettoSaertilskuddBelop, inntektBP, inntektBM, inntektBB,
          sjablonliste);

      // Beregner
      resultatPeriodeListe.add(
          new ResultatPeriode(beregningsperiode, bPsAndelSaertilskuddBeregning.beregn(beregnBPsAndelSaertilskuddGrunnlagPeriodisert),
              beregnBPsAndelSaertilskuddGrunnlagPeriodisert));
    }

    // Slår sammen perioder med samme resultat
    return new BeregnBPsAndelSaertilskuddResultat(resultatPeriodeListe);
  }

  // Sjekker om det skal legges til inntekt for fordel særfradrag enslig forsørger og skatteklasse 2 (kun BM)
  private List<InntektPeriode> behandlUtvidetBarnetrygd(List<InntektPeriode> inntektPeriodeListe, List<SjablonPeriode> sjablonPeriodeListe) {

    if (inntektPeriodeListe.isEmpty()) {
      return inntektPeriodeListe;
    }

    var justertInntektPeriodeListe = InntektUtil.behandlUtvidetBarnetrygd(inntektPeriodeListe.stream()
            .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getPeriode(), inntektPeriode.getInntektType(),
                inntektPeriode.getInntektBelop(), inntektPeriode.getDeltFordel(), inntektPeriode.getSkatteklasse2()))
            .toList(),
        sjablonPeriodeListe);

    return justertInntektPeriodeListe.stream()
        .map(inntektGrunnlag -> new InntektPeriode(inntektGrunnlag.getReferanse(), inntektGrunnlag.getPeriode(), inntektGrunnlag.getType(),
            inntektGrunnlag.getBelop(), inntektGrunnlag.getDeltFordel(), inntektGrunnlag.getSkatteklasse2()))
        .sorted(comparing(inntektPeriode -> inntektPeriode.getPeriodeDatoFraTil().getDatoFom()))
        .toList();
  }

  // Validerer at input-verdier til BPsAndelSaertilskuddsberegning er gyldige
  public List<Avvik> validerInput(BeregnBPsAndelSaertilskuddGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getPeriode());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe",
            sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for inntektBP
    var inntektBPPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBPPeriode : grunnlag.getInntektBPPeriodeListe()) {
      inntektBPPeriodeListe.add(inntektBPPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBPPeriodeListe",
        inntektBPPeriodeListe, false, true, false, true));

    // Sjekk perioder for inntektBM
    var inntektBMPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBMPeriode : grunnlag.getInntektBMPeriodeListe()) {
      inntektBMPeriodeListe.add(inntektBMPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBMPeriodeListe",
        inntektBMPeriodeListe, false, true, false, true));

    // Sjekk perioder for inntektBB
    var inntektBBPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBBPeriode : grunnlag.getInntektBBPeriodeListe()) {
      inntektBBPeriodeListe.add(inntektBBPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBBPeriodeListe",
        inntektBBPeriodeListe, false, true, false, true));

    return avvikListe;
  }
}
