package no.nav.bidrag.beregn.bidragsevne;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHustandPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneResultat;
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode;
import no.nav.bidrag.beregn.bidragsevne.dto.AntallBarnIEgetHusholdPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneResultatCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.bidragsevne.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SaerfradragPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SkatteklassePeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode;
import no.nav.bidrag.beregn.felles.FellesCore;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;

public class BidragsevneCoreImpl extends FellesCore implements BidragsevneCore {

  public BidragsevneCoreImpl(BidragsevnePeriode bidragsevnePeriode) {
    this.bidragsevnePeriode = bidragsevnePeriode;
  }

  private final BidragsevnePeriode bidragsevnePeriode;

  public BeregnBidragsevneResultatCore beregnBidragsevne(BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore) {
    var beregnBidragsevneGrunnlag = mapTilBusinessObject(beregnBidragsevneGrunnlagCore);
    var beregnBidragsevneResultat = new BeregnBidragsevneResultat(Collections.emptyList());
    var avvikListe = bidragsevnePeriode.validerInput(beregnBidragsevneGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnBidragsevneResultat = bidragsevnePeriode.beregnPerioder(beregnBidragsevneGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnBidragsevneResultat);
  }

  private BeregnBidragsevneGrunnlag mapTilBusinessObject(BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore) {
    var beregnDatoFra = beregnBidragsevneGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnBidragsevneGrunnlagCore.getBeregnDatoTil();
    var inntektPeriodeListe = mapInntektPeriodeListe(beregnBidragsevneGrunnlagCore.getInntektPeriodeListe());
    var skatteklassePeriodeListe = mapSkatteklassePeriodeListe(beregnBidragsevneGrunnlagCore.getSkatteklassePeriodeListe());
    var bostatusPeriodeListe = mapBostatusPeriodeListe(beregnBidragsevneGrunnlagCore.getBostatusPeriodeListe());
    var antallBarnIEgetHusholdPeriodeListe = mapAntallBarnIEgetHusholdPeriodeListe(
        beregnBidragsevneGrunnlagCore.getAntallBarnIEgetHusholdPeriodeListe());
    var saerfradragPeriodeListe = mapSaerfradragPeriodeListe(beregnBidragsevneGrunnlagCore.getSaerfradragPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBidragsevneGrunnlagCore.getSjablonPeriodeListe());
    return new BeregnBidragsevneGrunnlag(beregnDatoFra, beregnDatoTil, inntektPeriodeListe, skatteklassePeriodeListe, bostatusPeriodeListe,
        antallBarnIEgetHusholdPeriodeListe, saerfradragPeriodeListe, sjablonPeriodeListe);
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
          new Periode(sjablonPeriodeCore.getPeriode().getDatoFom(), sjablonPeriodeCore.getPeriode().getDatoTil()),
          new Sjablon(sjablonPeriodeCore.getNavn(), sjablonNokkelListe, sjablonInnholdListe)));
    }
    return sjablonPeriodeListe;
  }

  private List<InntektPeriode> mapInntektPeriodeListe(List<InntektPeriodeCore> inntektPeriodeListeCore) {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore inntektPeriodeCore : inntektPeriodeListeCore) {
      inntektPeriodeListe.add(new InntektPeriode(
          inntektPeriodeCore.getReferanse(),
          new Periode(inntektPeriodeCore.getPeriodeDatoFraTil().getDatoFom(), inntektPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          InntektType.valueOf(inntektPeriodeCore.getInntektType()),
          inntektPeriodeCore.getInntektBelop()));
    }
    return inntektPeriodeListe;
  }

  private List<SkatteklassePeriode> mapSkatteklassePeriodeListe(List<SkatteklassePeriodeCore> skatteklassePeriodeListeCore) {
    var skatteklassePeriodeListe = new ArrayList<SkatteklassePeriode>();
    for (SkatteklassePeriodeCore skatteklassePeriodeCore : skatteklassePeriodeListeCore) {
      skatteklassePeriodeListe.add(new SkatteklassePeriode(
          skatteklassePeriodeCore.getReferanse(),
          new Periode(skatteklassePeriodeCore.getPeriodeDatoFraTil().getDatoFom(), skatteklassePeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          skatteklassePeriodeCore.getSkatteklasse()));
    }
    return skatteklassePeriodeListe;
  }

  private List<BostatusPeriode> mapBostatusPeriodeListe(List<BostatusPeriodeCore> bostatusPeriodeListeCore) {
    var bostatusPeriodeListe = new ArrayList<BostatusPeriode>();
    for (BostatusPeriodeCore bostatusPeriodeCore : bostatusPeriodeListeCore) {
      bostatusPeriodeListe.add(new BostatusPeriode(
          bostatusPeriodeCore.getReferanse(),
          new Periode(bostatusPeriodeCore.getPeriodeDatoFraTil().getDatoFom(), bostatusPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          BostatusKode.valueOf(bostatusPeriodeCore.getBostatusKode())));
    }
    return bostatusPeriodeListe;
  }

  private List<BarnIHustandPeriode> mapAntallBarnIEgetHusholdPeriodeListe(
      List<AntallBarnIEgetHusholdPeriodeCore> antallBarnIEgetHusholdPeriodeListeCore) {
    var antallBarnIEgetHusholdPeriodeListe = new ArrayList<BarnIHustandPeriode>();
    for (AntallBarnIEgetHusholdPeriodeCore antallBarnIEgetHusholdPeriodeCore : antallBarnIEgetHusholdPeriodeListeCore) {
      antallBarnIEgetHusholdPeriodeListe.add(new BarnIHustandPeriode(
          antallBarnIEgetHusholdPeriodeCore.getReferanse(),
          new Periode(antallBarnIEgetHusholdPeriodeCore.getPeriodeDatoFraTil().getDatoFom(),
              antallBarnIEgetHusholdPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          antallBarnIEgetHusholdPeriodeCore.getAntallBarn()));
    }
    return antallBarnIEgetHusholdPeriodeListe;
  }

  private List<SaerfradragPeriode> mapSaerfradragPeriodeListe(List<SaerfradragPeriodeCore> saerfradragPeriodeListeCore) {
    var saerfradragPeriodeListe = new ArrayList<SaerfradragPeriode>();
    for (SaerfradragPeriodeCore saerfradragPeriodeCore : saerfradragPeriodeListeCore) {
      saerfradragPeriodeListe.add(new SaerfradragPeriode(
          saerfradragPeriodeCore.getReferanse(),
          new Periode(saerfradragPeriodeCore.getPeriodeDatoFraTil().getDatoFom(), saerfradragPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          SaerfradragKode.valueOf(saerfradragPeriodeCore.getSaerfradragKode())));
    }
    return saerfradragPeriodeListe;
  }

  private BeregnBidragsevneResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnBidragsevneResultat resultat) {
    return new BeregnBidragsevneResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()),
        mapSjablonGrunnlagListe(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<AvvikCore> mapAvvik(List<Avvik> avvikListe) {
    var avvikCoreListe = new ArrayList<AvvikCore>();
    for (Avvik avvik : avvikListe) {
      avvikCoreListe.add(new AvvikCore(avvik.getAvvikTekst(), avvik.getAvvikType().toString()));
    }
    return avvikCoreListe;
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> periodeResultatListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode periodeResultat : periodeResultatListe) {
      var bidragsevneResultat = periodeResultat.getResultatBeregning();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFom(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(bidragsevneResultat.getResultatEvneBelop()),
          mapReferanseListe(periodeResultat)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getResultatGrunnlagBeregning();
    var sjablonListe = resultatPeriode.getResultatBeregning().getSjablonListe();

    var referanseListe = new ArrayList<String>();
    resultatGrunnlag.getInntektListe().forEach(inntekt -> referanseListe.add(inntekt.getReferanse()));
    referanseListe.add(resultatGrunnlag.getSkatteklasse().getReferanse());
    referanseListe.add(resultatGrunnlag.getBostatus().getReferanse());
    referanseListe.add(resultatGrunnlag.getBarnIHusstand().getReferanse());
    referanseListe.add(resultatGrunnlag.getSaerfradrag().getReferanse());
    referanseListe.addAll(sjablonListe.stream().map(this::lagSjablonReferanse).distinct().toList());
    return referanseListe.stream().sorted().toList();
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonGrunnlagListe(List<ResultatPeriode> resultatPeriodeListe) {
    return resultatPeriodeListe.stream()
        .map(resultatPeriode -> mapSjablonListe(resultatPeriode.getResultatBeregning().getSjablonListe()))
        .flatMap(Collection::stream)
        .distinct()
        .toList();
  }
}
