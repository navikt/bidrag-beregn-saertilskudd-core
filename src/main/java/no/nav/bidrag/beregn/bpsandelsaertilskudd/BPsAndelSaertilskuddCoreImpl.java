package no.nav.bidrag.beregn.bpsandelsaertilskudd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddResultat;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.NettoSaertilskuddPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.BeregnBPsAndelSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.BeregnBPsAndelSaertilskuddResultatCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.InntektCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.NettoSaertilskuddPeriodeCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.periode.BPsAndelSaertilskuddPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode;

public class BPsAndelSaertilskuddCoreImpl implements BPsAndelSaertilskuddCore{

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
    var soknadsbarnPersonId = beregnBPsAndelSaertilskuddGrunnlagCore.getSoknadsbarnPersonId();
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getSjablonPeriodeListe());
    var nettoSaertilskudd = mapNettoSaertilskuddPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getNettoSaertilskuddPeriodeListe());
    var inntektBPPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getInntektBPPeriodeListe());
    var inntektBMPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getInntektBMPeriodeListe());
    var inntektBBPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelSaertilskuddGrunnlagCore.getInntektBBPeriodeListe());

    return new BeregnBPsAndelSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
        nettoSaertilskudd, inntektBPPeriodeListe, inntektBMPeriodeListe,
        inntektBBPeriodeListe, sjablonPeriodeListe);
  }

  private List<SjablonPeriode> mapSjablonPeriodeListe(List<SjablonPeriodeCore> sjablonPeriodeListeCore) {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    for (SjablonPeriodeCore sjablonPeriodeCore : sjablonPeriodeListeCore) {
      var sjablonNokkelListe = new ArrayList<SjablonNokkel>();
      var sjablonInnholdListe = new ArrayList<SjablonInnhold>();
      for (SjablonNokkelCore sjablonNokkelCore : sjablonPeriodeCore.getSjablonNokkelListe()) {
        sjablonNokkelListe.add(new SjablonNokkel(sjablonNokkelCore.getSjablonNokkelNavn(), sjablonNokkelCore.getSjablonNokkelVerdi()));
      }
      for (SjablonInnholdCore sjablonInnholdCore : sjablonPeriodeCore.getSjablonInnholdListe()) {
        sjablonInnholdListe.add(new SjablonInnhold(sjablonInnholdCore.getSjablonInnholdNavn(), sjablonInnholdCore.getSjablonInnholdVerdi()));
      }
      sjablonPeriodeListe.add(new SjablonPeriode(
          new Periode(sjablonPeriodeCore.getSjablonPeriodeDatoFraTil().getPeriodeDatoFra(),
              sjablonPeriodeCore.getSjablonPeriodeDatoFraTil().getPeriodeDatoTil()),
          new Sjablon(sjablonPeriodeCore.getSjablonNavn(), sjablonNokkelListe, sjablonInnholdListe)));
    }
    return sjablonPeriodeListe;
  }

  private List<NettoSaertilskuddPeriode> mapNettoSaertilskuddPeriodeListe(
      List<NettoSaertilskuddPeriodeCore> nettoSaertilskuddPeriodeListeCore) {
    var nettoSaertilskuddPeriodeListe = new ArrayList<NettoSaertilskuddPeriode>();
    for (NettoSaertilskuddPeriodeCore nettoSaertilskuddPeriodeCore : nettoSaertilskuddPeriodeListeCore) {
      nettoSaertilskuddPeriodeListe.add(new NettoSaertilskuddPeriode(
          new Periode(nettoSaertilskuddPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoFra(),
              nettoSaertilskuddPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoTil()),
          nettoSaertilskuddPeriodeCore.getNettoSaertilskuddBelop()));
    }
    return nettoSaertilskuddPeriodeListe;
  }

  private List<InntektPeriode> mapInntektPeriodeListe(List<InntektPeriodeCore> inntekterPeriodeListeCore) {
    var inntekterPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore inntektPeriodeCore : inntekterPeriodeListeCore) {
      inntekterPeriodeListe.add(new InntektPeriode(
          new Periode(inntektPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoFra(),
              inntektPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoTil()),
              InntektType.valueOf(inntektPeriodeCore.getInntektType()),
              inntektPeriodeCore.getInntektBelop(),
              inntektPeriodeCore.getDeltFordel(),
              inntektPeriodeCore.getSkatteklasse2()));
    }
    return inntekterPeriodeListe;
  }

  private BeregnBPsAndelSaertilskuddResultatCore mapFraBusinessObject(
      List<Avvik> avvikListe, BeregnBPsAndelSaertilskuddResultat resultat) {
    return new BeregnBPsAndelSaertilskuddResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var bPsAndelSaertilskuddResultatGrunnlag = periodeResultat.getResultatGrunnlagBeregning();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          periodeResultat.getSoknadsbarnPersonId(),
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFra(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(bPsAndelSaertilskuddResultat.getResultatAndelProsent(),
              bPsAndelSaertilskuddResultat.getResultatAndelBelop(),
              bPsAndelSaertilskuddResultat.getBarnetErSelvforsorget()),
          new ResultatGrunnlagCore(bPsAndelSaertilskuddResultatGrunnlag.getNettoSaertilskuddBelop(),
              mapResultatGrunnlagInntekt(bPsAndelSaertilskuddResultatGrunnlag.getInntektBPListe()),
              mapResultatGrunnlagInntekt(bPsAndelSaertilskuddResultatGrunnlag.getInntektBMListe()),
              mapResultatGrunnlagInntekt(bPsAndelSaertilskuddResultatGrunnlag.getInntektBBListe()),
              mapResultatGrunnlagSjabloner(bPsAndelSaertilskuddResultat.getSjablonListe()))));
    }
    return resultatPeriodeCoreListe;
  }


  private List<InntektCore> mapResultatGrunnlagInntekt(List<Inntekt> resultatGrunnlagInntektListe) {
    var resultatGrunnlagInntektListeCore = new ArrayList<InntektCore>();
    for (Inntekt resultatGrunnlagInntekt : resultatGrunnlagInntektListe) {
      resultatGrunnlagInntektListeCore
          .add(new InntektCore(resultatGrunnlagInntekt.getInntektType().toString(), resultatGrunnlagInntekt.getInntektBelop(),
              resultatGrunnlagInntekt.getDeltFordel(), resultatGrunnlagInntekt.getSkatteklasse2()));
    }
    return resultatGrunnlagInntektListeCore;
  }

  private List<SjablonNavnVerdiCore> mapResultatGrunnlagSjabloner(List<SjablonNavnVerdi> resultatGrunnlagSjablonListe) {
    var resultatGrunnlagSjablonListeCore = new ArrayList<SjablonNavnVerdiCore>();
    for (SjablonNavnVerdi resultatGrunnlagSjablon : resultatGrunnlagSjablonListe) {
      resultatGrunnlagSjablonListeCore
          .add(new SjablonNavnVerdiCore(resultatGrunnlagSjablon.getSjablonNavn(), resultatGrunnlagSjablon.getSjablonVerdi()));
    }
    return resultatGrunnlagSjablonListeCore;
  }
}