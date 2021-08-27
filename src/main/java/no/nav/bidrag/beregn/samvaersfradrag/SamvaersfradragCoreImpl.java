package no.nav.bidrag.beregn.samvaersfradrag;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.felles.FellesCore;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragGrunnlagCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragResultatCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.SamvaersklassePeriodeCore;
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode;

public class SamvaersfradragCoreImpl extends FellesCore implements SamvaersfradragCore {

  public SamvaersfradragCoreImpl(SamvaersfradragPeriode samvaersfradragPeriode) {
    this.samvaersfradragPeriode = samvaersfradragPeriode;
  }

  private final SamvaersfradragPeriode samvaersfradragPeriode;

  public BeregnSamvaersfradragResultatCore beregnSamvaersfradrag(
      BeregnSamvaersfradragGrunnlagCore beregnSamvaersfradragGrunnlagCore) {
    var beregnSamvaersfradragGrunnlag = mapTilBusinessObject(beregnSamvaersfradragGrunnlagCore);
    var beregnSamvaersfradragResultat = new BeregnSamvaersfradragResultat(Collections.emptyList());
    var avvikListe = samvaersfradragPeriode.validerInput(beregnSamvaersfradragGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnSamvaersfradragResultat = samvaersfradragPeriode.beregnPerioder(beregnSamvaersfradragGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnSamvaersfradragResultat);
  }

  private BeregnSamvaersfradragGrunnlag mapTilBusinessObject(BeregnSamvaersfradragGrunnlagCore beregnSamvaersfradragGrunnlagCore) {
    var beregnDatoFra = beregnSamvaersfradragGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnSamvaersfradragGrunnlagCore.getBeregnDatoTil();
    var samvaersklassePeriodeListe = mapSamvaersklassePeriodeListe(beregnSamvaersfradragGrunnlagCore.getSamvaersklassePeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnSamvaersfradragGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnSamvaersfradragGrunnlag(beregnDatoFra, beregnDatoTil,
        samvaersklassePeriodeListe, sjablonPeriodeListe);
  }

  private List<SjablonPeriode> mapSjablonPeriodeListe(List<SjablonPeriodeCore> sjablonPeriodeListeCore) {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    for (SjablonPeriodeCore sjablonPeriodeCore : sjablonPeriodeListeCore) {
      var sjablonNokkelListe = new ArrayList<SjablonNokkel>();
      var sjablonInnholdListe = new ArrayList<SjablonInnhold>();
      for (SjablonNokkelCore sjablonNokkelCore : sjablonPeriodeCore.getNokkelListe()) {
        sjablonNokkelListe.add(new SjablonNokkel(sjablonNokkelCore.getNavn(), sjablonNokkelCore.getVerdi()));
      }
      for (SjablonInnholdCore sjablonInnholdCore : sjablonPeriodeCore.getInnholdListe()) {
        sjablonInnholdListe.add(new SjablonInnhold(sjablonInnholdCore.getNavn(), sjablonInnholdCore.getVerdi()));
      }
      sjablonPeriodeListe.add(new SjablonPeriode(
          new Periode(sjablonPeriodeCore.getPeriode().getDatoFom(),
              sjablonPeriodeCore.getPeriode().getDatoTil()),
          new Sjablon(sjablonPeriodeCore.getNavn(), sjablonNokkelListe, sjablonInnholdListe)));
    }
    return sjablonPeriodeListe;
  }

  private List<SamvaersfradragGrunnlagPeriode> mapSamvaersklassePeriodeListe(
      List<SamvaersklassePeriodeCore> samvaersklassePeriodeListeCore) {
    var samvaersklassePeriodeListe = new ArrayList<SamvaersfradragGrunnlagPeriode>();
    for (SamvaersklassePeriodeCore samvaersklassePeriodeCore : samvaersklassePeriodeListeCore) {
      samvaersklassePeriodeListe.add(new SamvaersfradragGrunnlagPeriode( samvaersklassePeriodeCore.getReferanse(),
          new Periode(samvaersklassePeriodeCore.getSamvaersklassePeriodeDatoFraTil().getDatoFom(),
              samvaersklassePeriodeCore.getSamvaersklassePeriodeDatoFraTil().getDatoTil()),
              samvaersklassePeriodeCore.getBarnPersonId(),
              samvaersklassePeriodeCore.getBarnFodselsdato(),
              samvaersklassePeriodeCore.getSamvaersklasse()));
    }
    return samvaersklassePeriodeListe;
  }

  private BeregnSamvaersfradragResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnSamvaersfradragResultat resultat) {
    return new BeregnSamvaersfradragResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapSjablonGrunnlagListe(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<AvvikCore> mapAvvik(List<Avvik> avvikListe) {
    var avvikCoreListe = new ArrayList<AvvikCore>();
    for (Avvik avvik : avvikListe) {
      avvikCoreListe.add(new AvvikCore(avvik.getAvvikTekst(),  avvik.getAvvikType().toString()));
    }
    return avvikCoreListe;
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
      var samvaersfradragResultat = resultatPeriode.getResultatBeregningListe();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(resultatPeriode.getResultatDatoFraTil().getDatoFom(), resultatPeriode.getResultatDatoFraTil().getDatoTil()),
          mapResultatBeregning(samvaersfradragResultat),
          mapReferanseListe(resultatPeriode)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getResultatGrunnlag();
    var sjablonListe = new ArrayList<SjablonPeriodeNavnVerdi>();
    resultatPeriode.getResultatBeregningListe().forEach(resultatBeregning -> sjablonListe.addAll(resultatBeregning.getSjablonListe()));

    var referanseListe = new ArrayList<String>();
    resultatGrunnlag.getSamvaersfradragGrunnlagPerBarnListe().forEach(samvaersfradragGrunnlagPerBarn ->  referanseListe.add(samvaersfradragGrunnlagPerBarn.getReferanse()));
    referanseListe.addAll(sjablonListe.stream().map(this::lagSjablonReferanse).distinct().collect(toList()));
    return referanseListe.stream().sorted().collect(toList());
  }

  private List<ResultatBeregningCore> mapResultatBeregning(List<ResultatBeregning> resultatBeregningListe) {
    var resultatBeregningCoreListe = new ArrayList<ResultatBeregningCore>();
    for (ResultatBeregning resultatBeregning : resultatBeregningListe) {
      resultatBeregningCoreListe.add(
          new ResultatBeregningCore(resultatBeregning.getBarnPersonId(),
              resultatBeregning.getResultatSamvaersfradragBelop()));
    }
    return resultatBeregningCoreListe;
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonGrunnlagListe(List<ResultatPeriode> resultatPeriodeListe) {
    return resultatPeriodeListe.stream()
        .map(resultatPeriode ->  resultatPeriode.getResultatBeregningListe())
        .flatMap(Collection::stream)
        .map(resultatBeregning -> mapSjablonListe(resultatBeregning.getSjablonListe()))
        .flatMap(Collection::stream)
        .distinct()
        .collect(toList());
  }
}
