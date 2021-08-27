package no.nav.bidrag.beregn.bpsandelsaertilskudd;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddResultat;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.NettoSaertilskuddPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.BeregnBPsAndelSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.BeregnBPsAndelSaertilskuddResultatCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.NettoSaertilskuddPeriodeCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.periode.BPsAndelSaertilskuddPeriode;
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
import no.nav.bidrag.beregn.felles.enums.InntektType;

public class BPsAndelSaertilskuddCoreImpl extends FellesCore implements BPsAndelSaertilskuddCore{

  public BPsAndelSaertilskuddCoreImpl(
      BPsAndelSaertilskuddPeriode bPsAndelSaertilskuddPeriode) {
    this.bPsAndelSaertilskuddPeriode = bPsAndelSaertilskuddPeriode;
  }

  private final BPsAndelSaertilskuddPeriode bPsAndelSaertilskuddPeriode;

  public BeregnBPsAndelSaertilskuddResultatCore beregnBPsAndelSaertilskudd(
      BeregnBPsAndelSaertilskuddGrunnlagCore beregnBPsAndelSaertilskuddGrunnlagCore) {

    var beregnBPsAndelSaertilskuddGrunnlag = mapTilBusinessObject(beregnBPsAndelSaertilskuddGrunnlagCore);
    var beregnBPsAndelSaertilskuddResultat = new BeregnBPsAndelSaertilskuddResultat(Collections.emptyList());
    var avvikListe = bPsAndelSaertilskuddPeriode.validerInput(beregnBPsAndelSaertilskuddGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnBPsAndelSaertilskuddResultat = bPsAndelSaertilskuddPeriode.beregnPerioder(beregnBPsAndelSaertilskuddGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnBPsAndelSaertilskuddResultat);
  }

  private BeregnBPsAndelSaertilskuddGrunnlag mapTilBusinessObject(
      BeregnBPsAndelSaertilskuddGrunnlagCore beregnBPsAndelSaertilskuddGrunnlagCore) {
    var beregnDatoFra = beregnBPsAndelSaertilskuddGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnBPsAndelSaertilskuddGrunnlagCore.getBeregnDatoTil();
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getSjablonPeriodeListe());
    var nettoSaertilskudd = mapNettoSaertilskuddPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getNettoSaertilskuddPeriodeListe());
    var inntektBPPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getInntektBPPeriodeListe());
    var inntektBMPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getInntektBMPeriodeListe());
    var inntektBBPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getInntektBBPeriodeListe());

    return new BeregnBPsAndelSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil,
        nettoSaertilskudd, inntektBPPeriodeListe, inntektBMPeriodeListe,
        inntektBBPeriodeListe, sjablonPeriodeListe);
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

  private List<NettoSaertilskuddPeriode> mapNettoSaertilskuddPeriodeListe(
      List<NettoSaertilskuddPeriodeCore> nettoSaertilskuddPeriodeListeCore) {
    var nettoSaertilskuddPeriodeListe = new ArrayList<NettoSaertilskuddPeriode>();
    for (NettoSaertilskuddPeriodeCore nettoSaertilskuddPeriodeCore : nettoSaertilskuddPeriodeListeCore) {
      nettoSaertilskuddPeriodeListe.add(new NettoSaertilskuddPeriode(nettoSaertilskuddPeriodeCore.getReferanse(),
          new Periode(nettoSaertilskuddPeriodeCore.getPeriodeDatoFraTil().getDatoFom(),
              nettoSaertilskuddPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          nettoSaertilskuddPeriodeCore.getNettoSaertilskuddBelop()));
    }
    return nettoSaertilskuddPeriodeListe;
  }

  private List<InntektPeriode> mapInntektPeriodeListe(List<InntektPeriodeCore> inntekterPeriodeListeCore) {
    var inntekterPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore inntektPeriodeCore : inntekterPeriodeListeCore) {
      inntekterPeriodeListe.add(new InntektPeriode(inntektPeriodeCore.getReferanse(),
          new Periode(inntektPeriodeCore.getPeriodeDatoFraTil().getDatoFom(),
              inntektPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
              InntektType.valueOf(inntektPeriodeCore.getInntektType()),
              inntektPeriodeCore.getInntektBelop(),
              inntektPeriodeCore.getDeltFordel(),
              inntektPeriodeCore.getSkatteklasse2()));
    }
    return inntekterPeriodeListe;
  }

  private BeregnBPsAndelSaertilskuddResultatCore mapFraBusinessObject(
      List<Avvik> avvikListe, BeregnBPsAndelSaertilskuddResultat resultat) {
    return new BeregnBPsAndelSaertilskuddResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapSjablonGrunnlagListe(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var bPsAndelSaertilskuddResultat = periodeResultat.getResultatBeregning();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFom(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(bPsAndelSaertilskuddResultat.getResultatAndelProsent(),
              bPsAndelSaertilskuddResultat.getResultatAndelBelop(),
              bPsAndelSaertilskuddResultat.getBarnetErSelvforsorget()),
          mapReferanseListe(periodeResultat)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getResultatGrunnlagBeregning();
    var sjablonListe = resultatPeriode.getResultatBeregning().getSjablonListe();

    var referanseListe = new ArrayList<String>();
    resultatGrunnlag.getInntektBPListe().forEach(inntekt -> referanseListe.add(inntekt.getReferanse()));
    resultatGrunnlag.getInntektBMListe().forEach(inntekt -> referanseListe.add(inntekt.getReferanse()));
    resultatGrunnlag.getInntektBBListe().forEach(inntekt -> referanseListe.add(inntekt.getReferanse()));
    referanseListe.addAll(sjablonListe.stream().map(this::lagSjablonReferanse).distinct().toList());
    return referanseListe.stream().sorted().toList();
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonGrunnlagListe(List<ResultatPeriode> resultatPeriodeListe) {
    return resultatPeriodeListe.stream()
        .map(resultatPeriode -> mapSjablonListe(resultatPeriode.getResultatBeregning().getSjablonListe()))
        .flatMap(Collection::stream)
        .distinct()
        .collect(toList());
  }
}