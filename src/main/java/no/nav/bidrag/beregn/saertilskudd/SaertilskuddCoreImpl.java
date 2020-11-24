package no.nav.bidrag.beregn.saertilskudd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.saertilskudd.dto.BPsAndelSaertilskuddCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddResultatCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BidragsevneCore;
import no.nav.bidrag.beregn.saertilskudd.dto.GrunnlagBeregningCore;
import no.nav.bidrag.beregn.saertilskudd.dto.LopendeBidragCore;
import no.nav.bidrag.beregn.saertilskudd.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.saertilskudd.dto.ResultatPeriodeCore;
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
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode;


public class SaertilskuddCoreImpl implements SaertilskuddCore {

  public SaertilskuddCoreImpl(SaertilskuddPeriode saertilskuddPeriode) {
    this.saertilskuddPeriode = saertilskuddPeriode;
  }

  private final SaertilskuddPeriode saertilskuddPeriode;

  public BeregnSaertilskuddResultatCore beregnSaertilskudd(
      BeregnSaertilskuddGrunnlagCore beregnSaertilskuddGrunnlagCore) {
    var beregnSaertilskuddGrunnlag = mapTilBusinessObject(beregnSaertilskuddGrunnlagCore);
    var beregnSaertilskuddResultat = new BeregnSaertilskuddResultat(Collections.emptyList());
    var avvikListe = saertilskuddPeriode.validerInput(beregnSaertilskuddGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnSaertilskuddResultat = saertilskuddPeriode.beregnPerioder(beregnSaertilskuddGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnSaertilskuddResultat);
  }

  private BeregnSaertilskuddGrunnlag mapTilBusinessObject(BeregnSaertilskuddGrunnlagCore beregnSaertilskuddGrunnlagCore) {
    var beregnDatoFra = beregnSaertilskuddGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnSaertilskuddGrunnlagCore.getBeregnDatoTil();
    var soknadsbarnPersonId = beregnSaertilskuddGrunnlagCore.getSoknadsbarnPersonId();
    var bidragsevne = mapBidragsevne(beregnSaertilskuddGrunnlagCore.getBidragsevne());
    var bPsAndelSaertilskudd = mapBPsAndelSaertilskudd(beregnSaertilskuddGrunnlagCore.getBPsAndelSaertilskudd());
    var lopendeBidrag = mapLopendeBidrag(beregnSaertilskuddGrunnlagCore.getLopendeBidrag());
    var samvaersfradragBelop = beregnSaertilskuddGrunnlagCore.getSamvaersfradragBelop();
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnSaertilskuddGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
        bidragsevne, bPsAndelSaertilskudd, lopendeBidrag, samvaersfradragBelop,
        sjablonPeriodeListe);
  }

  private Bidragsevne mapBidragsevne(BidragsevneCore bidragsevneCore) {
    return new Bidragsevne(bidragsevneCore.getBidragsevneBelop(),
        bidragsevneCore.getTjuefemProsentInntekt());
  }

  private BPsAndelSaertilskudd mapBPsAndelSaertilskudd(BPsAndelSaertilskuddCore bPsAndelSaertilskuddCore) {
    return new BPsAndelSaertilskudd(bPsAndelSaertilskuddCore.getBPsAndelSaertilskuddProsent(),
        bPsAndelSaertilskuddCore.getBPsAndelSaertilskuddBelop(),
        bPsAndelSaertilskuddCore.getBarnetErSelvforsorget());
  }

  private LopendeBidrag mapLopendeBidrag(LopendeBidragCore lopendeBidragCore) {
    return new LopendeBidrag(lopendeBidragCore.getLopendeBidragBelop(),
        lopendeBidragCore.getResultatkode());
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

  private BeregnSaertilskuddResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnSaertilskuddResultat resultat) {
    return new BeregnSaertilskuddResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<AvvikCore> mapAvvik(List<Avvik> avvikListe) {
    var avvikCoreListe = new ArrayList<AvvikCore>();
    for (Avvik avvik : avvikListe) {
      avvikCoreListe.add(new AvvikCore(avvik.getAvvikTekst(), avvik.getAvvikType().toString()));
    }
    return avvikCoreListe;
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
      var saertilskuddResultatGrunnlag = resultatPeriode.getResultatGrunnlagBeregning();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          resultatPeriode.getSoknadsbarnPersonId(),
          new PeriodeCore(resultatPeriode.getResultatDatoFraTil().getDatoFra(), resultatPeriode.getResultatDatoFraTil().getDatoTil()),
          mapResultatBeregning(resultatPeriode.getResultatBeregning()),
          new GrunnlagBeregningCore(
              new BidragsevneCore(saertilskuddResultatGrunnlag.getBidragsevne().getBidragsevneBelop(),
              saertilskuddResultatGrunnlag.getBidragsevne().getTjuefemProsentInntekt()),
              new BPsAndelSaertilskuddCore(
                  saertilskuddResultatGrunnlag.getBPsAndelSaertilskudd().getBPsAndelSaertilskuddProsent(),
                  saertilskuddResultatGrunnlag.getBPsAndelSaertilskudd().getBPsAndelSaertilskuddBelop(),
                  saertilskuddResultatGrunnlag.getBPsAndelSaertilskudd().getBarnetErSelvforsorget()),
              new LopendeBidragCore(
                  saertilskuddResultatGrunnlag.getLopendeBidrag().getLopendeBidragBelop(),
                  saertilskuddResultatGrunnlag.getLopendeBidrag().getResultatkode()),
              saertilskuddResultatGrunnlag.getSamvaersfradragBelop(),
              mapResultatGrunnlagSjabloner(resultatPeriode.getResultatBeregning().getSjablonListe()))));
    }
    return resultatPeriodeCoreListe;
  }

  private ResultatBeregningCore mapResultatBeregning(ResultatBeregning resultatBeregning) {
    var resultatBeregningListeCore = new ArrayList<ResultatBeregningCore>();
    return new ResultatBeregningCore(resultatBeregning.getResultatBelop(),
        resultatBeregning.getResultatkode().toString(),resultatBeregning.getSjablonListe());
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
