package no.nav.bidrag.beregn.saertilskudd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskuddPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;
import no.nav.bidrag.beregn.saertilskudd.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidragPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlagPeriode;
import no.nav.bidrag.beregn.saertilskudd.dto.BPsAndelSaertilskuddCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BPsAndelSaertilskuddPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddResultatCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BidragsevneCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BidragsevnePeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.LopendeBidragCore;
import no.nav.bidrag.beregn.saertilskudd.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.saertilskudd.dto.SamvaersfradragCore;
import no.nav.bidrag.beregn.saertilskudd.dto.LopendeBidragPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.saertilskudd.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.SamvaersfradragPeriodeCore;
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

  private BeregnSaertilskuddGrunnlag mapTilBusinessObject(
      BeregnSaertilskuddGrunnlagCore beregnSaertilskuddGrunnlagCore) {
    var beregnDatoFra = beregnSaertilskuddGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnSaertilskuddGrunnlagCore.getBeregnDatoTil();
    var soknadsbarnPersonId = beregnSaertilskuddGrunnlagCore.getSoknadsbarnPersonId();
    var bidragsevne = mapBidragsevnePeriodeListe(
        beregnSaertilskuddGrunnlagCore.getBidragsevnePeriodeListe());
    var bPsAndelSaertilskudd = mapBPsAndelSaertilskuddPeriodeListe(
        beregnSaertilskuddGrunnlagCore.getBPsAndelSaertilskuddPeriodeListe());
    var lopendeBidrag = mapLopendeBidragPeriodeListe(
        beregnSaertilskuddGrunnlagCore.getLopendeBidragPeriodeListe());
    var samvaersfradragBelop = mapSamvaersfradragPeriodeListe(
        beregnSaertilskuddGrunnlagCore.getSamvaersfradragPeriodeListe());

    return new BeregnSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
        bidragsevne,
        bPsAndelSaertilskudd, lopendeBidrag, samvaersfradragBelop);
  }

  private List<BidragsevnePeriode> mapBidragsevnePeriodeListe(
      List<BidragsevnePeriodeCore> bidragsevnePeriodeListeCore) {
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    for (BidragsevnePeriodeCore bidragsevnePeriodeCore : bidragsevnePeriodeListeCore) {
      bidragsevnePeriodeListe.add(new BidragsevnePeriode(
          bidragsevnePeriodeCore.getReferanse(),
          new Periode(bidragsevnePeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoFra(),
              bidragsevnePeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoTil()),
          bidragsevnePeriodeCore.getBidragsevneBelop()
      ));
    }
    return bidragsevnePeriodeListe.stream()
        .sorted(Comparator.comparing(bidragsevnePeriode -> bidragsevnePeriode
            .getPeriodeDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }


  private List<BPsAndelSaertilskuddPeriode> mapBPsAndelSaertilskuddPeriodeListe(
      List<BPsAndelSaertilskuddPeriodeCore> bPsAndelSaertilskuddPeriodeListeCore) {
    var bPsAndelSaertilskuddPeriodeListe = new ArrayList<BPsAndelSaertilskuddPeriode>();
    for (BPsAndelSaertilskuddPeriodeCore bPsAndelSaertilskuddPeriodeCore : bPsAndelSaertilskuddPeriodeListeCore) {
      bPsAndelSaertilskuddPeriodeListe.add(new BPsAndelSaertilskuddPeriode(
          bPsAndelSaertilskuddPeriodeCore.getReferanse(),
          new Periode(bPsAndelSaertilskuddPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoFra(),
              bPsAndelSaertilskuddPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoTil()),
          bPsAndelSaertilskuddPeriodeCore.getBPsAndelSaertilskuddProsent(),
          bPsAndelSaertilskuddPeriodeCore.getBPsAndelSaertilskuddBelop(),
          bPsAndelSaertilskuddPeriodeCore.getBarnetErSelvforsorget()));
    }
    return bPsAndelSaertilskuddPeriodeListe.stream()
        .sorted(Comparator.comparing(bPsAndelSaertilskuddPeriode -> bPsAndelSaertilskuddPeriode
            .getPeriodeDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private List<LopendeBidragPeriode> mapLopendeBidragPeriodeListe(
      List<LopendeBidragPeriodeCore> lopendeBidragPeriodeListeCore) {
    var lopendeBidragPeriodeListe = new ArrayList<LopendeBidragPeriode>();
    for (LopendeBidragPeriodeCore lopendeBidragPeriodeCore : lopendeBidragPeriodeListeCore) {
      lopendeBidragPeriodeListe.add(new LopendeBidragPeriode(lopendeBidragPeriodeCore.getReferanse(),
          new Periode(lopendeBidragPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoFra(),
              lopendeBidragPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoTil()),
          lopendeBidragPeriodeCore.getBarnPersonId(),
          lopendeBidragPeriodeCore.getLopendeBidragBelop(),
          lopendeBidragPeriodeCore.getOpprinneligBPsAndelUnderholdskostnadBelop(),
          lopendeBidragPeriodeCore.getOpprinneligBidragBelop(),
          lopendeBidragPeriodeCore.getOpprinneligSamvaersfradragBelop()
      ));
    }
    return lopendeBidragPeriodeListe.stream()
        .sorted(Comparator.comparing(lopendeBidragPeriode -> lopendeBidragPeriode
            .getPeriodeDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private List<SamvaersfradragGrunnlagPeriode> mapSamvaersfradragPeriodeListe(
      List<SamvaersfradragPeriodeCore> samvaersfradragPeriodeCoreListe) {
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragGrunnlagPeriode>();
    for (SamvaersfradragPeriodeCore samvaersfradragPeriodeCore : samvaersfradragPeriodeCoreListe) {
      samvaersfradragPeriodeListe.add(new SamvaersfradragGrunnlagPeriode(
          samvaersfradragPeriodeCore.getReferanse(),
          samvaersfradragPeriodeCore.getBarnPersonId(),
          new Periode(samvaersfradragPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoFra(),
              samvaersfradragPeriodeCore.getPeriodeDatoFraTil().getPeriodeDatoTil()),
          samvaersfradragPeriodeCore.getSamvaersfradragBelop()));
    }
    return samvaersfradragPeriodeListe.stream()
        .sorted(Comparator.comparing(samvaersfradragGrunnlagPeriode -> samvaersfradragGrunnlagPeriode
            .getPeriodeDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private BeregnSaertilskuddResultatCore mapFraBusinessObject(List<Avvik> avvikListe,
      BeregnSaertilskuddResultat resultat) {
    return new BeregnSaertilskuddResultatCore(
        mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var saertilskuddResultat = resultatPeriode.getResultat();
      var saertilskuddResultatGrunnlag = resultatPeriode.getGrunnlag();

      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFra(),
              resultatPeriode.getPeriode().getDatoTil()),
          resultatPeriode.getSoknadsbarnPersonId(),
          mapResultatBeregning(resultatPeriode.getResultat()),
          new ResultatGrunnlagCore(
              new BidragsevneCore(
                  saertilskuddResultatGrunnlag.getBidragsevne().getBidragsevneBelop()
              ),
              new BPsAndelSaertilskuddCore(
                  saertilskuddResultatGrunnlag.getBPsAndelSaertilskudd()
                      .getBPsAndelSaertilskuddProsent(),
                  saertilskuddResultatGrunnlag.getBPsAndelSaertilskudd()
                      .getBPsAndelSaertilskuddBelop(),
                  saertilskuddResultatGrunnlag.getBPsAndelSaertilskudd()
                      .getBarnetErSelvforsorget()),
              mapResultatGrunnlagLopendeBidrag(
                  saertilskuddResultatGrunnlag.getLopendeBidragListe()),
              mapResultatGrunnlagSamvaersfradrag(
                  saertilskuddResultatGrunnlag.getSamvaersfradragGrunnlagListe())
          )));
    }
    return resultatPeriodeCoreListe;
  }

  private List<LopendeBidragCore> mapResultatGrunnlagLopendeBidrag(
      List<LopendeBidrag> resultatGrunnlagLopendeBidragListe) {
    var resultatGrunnlagLopendeBidragListeCore = new ArrayList<LopendeBidragCore>();
    for (LopendeBidrag resultatGrunnlagLopendeBidrag : resultatGrunnlagLopendeBidragListe) {
      resultatGrunnlagLopendeBidragListeCore
          .add(new LopendeBidragCore(resultatGrunnlagLopendeBidrag.getBarnPersonId(),
              resultatGrunnlagLopendeBidrag.getLopendeBidragBelop(),
              resultatGrunnlagLopendeBidrag.getOpprinneligBPsAndelUnderholdskostnadBelop(),
              resultatGrunnlagLopendeBidrag.getOpprinneligBidragBelop(),
              resultatGrunnlagLopendeBidrag.getOpprinneligSamvaersfradragBelop()
          ));
    }
    return resultatGrunnlagLopendeBidragListeCore;
  }


  private List<SamvaersfradragCore> mapResultatGrunnlagSamvaersfradrag(
      List<SamvaersfradragGrunnlag> resultatGrunnlagSamvaersfradragListe) {
    var resultatGrunnlagSamvaersfradragListeCore = new ArrayList<SamvaersfradragCore>();
    for (SamvaersfradragGrunnlag resultatGrunnlagSamvaersfradrag : resultatGrunnlagSamvaersfradragListe) {
      resultatGrunnlagSamvaersfradragListeCore
          .add(new SamvaersfradragCore(resultatGrunnlagSamvaersfradrag.getBarnPersonId(),
              resultatGrunnlagSamvaersfradrag.getSamvaersfradragBelop()));
    }
    return resultatGrunnlagSamvaersfradragListeCore;
  }

  private ResultatBeregningCore mapResultatBeregning(ResultatBeregning resultatBeregning) {
    return new ResultatBeregningCore(resultatBeregning.getResultatBelop(),
        resultatBeregning.getResultatkode().toString());
  }
}
