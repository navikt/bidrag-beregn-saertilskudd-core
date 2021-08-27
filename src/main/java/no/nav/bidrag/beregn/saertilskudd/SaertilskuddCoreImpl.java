package no.nav.bidrag.beregn.saertilskudd;

import static java.util.stream.Collectors.toList;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.felles.FellesCore;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskuddPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;
import no.nav.bidrag.beregn.saertilskudd.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidragPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlagPeriode;
import no.nav.bidrag.beregn.saertilskudd.dto.BPsAndelSaertilskuddPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddResultatCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BidragsevnePeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.LopendeBidragPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.saertilskudd.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode;


public class SaertilskuddCoreImpl extends FellesCore implements SaertilskuddCore {

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
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnSaertilskuddGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
        bidragsevne,
        bPsAndelSaertilskudd, lopendeBidrag, samvaersfradragBelop, sjablonPeriodeListe);
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

  private List<BidragsevnePeriode> mapBidragsevnePeriodeListe(
      List<BidragsevnePeriodeCore> bidragsevnePeriodeListeCore) {
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    for (BidragsevnePeriodeCore bidragsevnePeriodeCore : bidragsevnePeriodeListeCore) {
      bidragsevnePeriodeListe.add(new BidragsevnePeriode(
          bidragsevnePeriodeCore.getReferanse(),
          new Periode(bidragsevnePeriodeCore.getPeriodeDatoFraTil().getDatoFom(),
              bidragsevnePeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          bidragsevnePeriodeCore.getBidragsevneBelop()
      ));
    }
    return bidragsevnePeriodeListe.stream()
        .sorted(Comparator.comparing(bidragsevnePeriode -> bidragsevnePeriode
            .getPeriodeDatoFraTil().getDatoFom())).collect(toList());
  }


  private List<BPsAndelSaertilskuddPeriode> mapBPsAndelSaertilskuddPeriodeListe(
      List<BPsAndelSaertilskuddPeriodeCore> bPsAndelSaertilskuddPeriodeListeCore) {
    var bPsAndelSaertilskuddPeriodeListe = new ArrayList<BPsAndelSaertilskuddPeriode>();
    for (BPsAndelSaertilskuddPeriodeCore bPsAndelSaertilskuddPeriodeCore : bPsAndelSaertilskuddPeriodeListeCore) {
      bPsAndelSaertilskuddPeriodeListe.add(new BPsAndelSaertilskuddPeriode(
          bPsAndelSaertilskuddPeriodeCore.getReferanse(),
          new Periode(bPsAndelSaertilskuddPeriodeCore.getPeriodeDatoFraTil().getDatoFom(),
              bPsAndelSaertilskuddPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          bPsAndelSaertilskuddPeriodeCore.getBPsAndelSaertilskuddProsent(),
          bPsAndelSaertilskuddPeriodeCore.getBPsAndelSaertilskuddBelop(),
          bPsAndelSaertilskuddPeriodeCore.getBarnetErSelvforsorget()));
    }
    return bPsAndelSaertilskuddPeriodeListe.stream()
        .sorted(Comparator.comparing(bPsAndelSaertilskuddPeriode -> bPsAndelSaertilskuddPeriode
            .getPeriodeDatoFraTil().getDatoFom())).collect(toList());
  }

  private List<LopendeBidragPeriode> mapLopendeBidragPeriodeListe(
      List<LopendeBidragPeriodeCore> lopendeBidragPeriodeListeCore) {
    var lopendeBidragPeriodeListe = new ArrayList<LopendeBidragPeriode>();
    for (LopendeBidragPeriodeCore lopendeBidragPeriodeCore : lopendeBidragPeriodeListeCore) {
      lopendeBidragPeriodeListe.add(new LopendeBidragPeriode(lopendeBidragPeriodeCore.getReferanse(),
          new Periode(lopendeBidragPeriodeCore.getPeriodeDatoFraTil().getDatoFom(),
              lopendeBidragPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          lopendeBidragPeriodeCore.getBarnPersonId(),
          lopendeBidragPeriodeCore.getLopendeBidragBelop(),
          lopendeBidragPeriodeCore.getOpprinneligBPsAndelUnderholdskostnadBelop(),
          lopendeBidragPeriodeCore.getOpprinneligBidragBelop(),
          lopendeBidragPeriodeCore.getOpprinneligSamvaersfradragBelop()
      ));
    }
    return lopendeBidragPeriodeListe.stream()
        .sorted(Comparator.comparing(lopendeBidragPeriode -> lopendeBidragPeriode
            .getPeriodeDatoFraTil().getDatoFom())).collect(toList());
  }

  private List<SamvaersfradragGrunnlagPeriode> mapSamvaersfradragPeriodeListe(
      List<SamvaersfradragPeriodeCore> samvaersfradragPeriodeCoreListe) {
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragGrunnlagPeriode>();
    for (SamvaersfradragPeriodeCore samvaersfradragPeriodeCore : samvaersfradragPeriodeCoreListe) {
      samvaersfradragPeriodeListe.add(new SamvaersfradragGrunnlagPeriode(
          samvaersfradragPeriodeCore.getReferanse(),
          samvaersfradragPeriodeCore.getBarnPersonId(),
          new Periode(samvaersfradragPeriodeCore.getPeriodeDatoFraTil().getDatoFom(),
              samvaersfradragPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          samvaersfradragPeriodeCore.getSamvaersfradragBelop()));
    }
    return samvaersfradragPeriodeListe.stream()
        .sorted(Comparator.comparing(samvaersfradragGrunnlagPeriode -> samvaersfradragGrunnlagPeriode
            .getPeriodeDatoFraTil().getDatoFom())).collect(toList());
  }

  private BeregnSaertilskuddResultatCore mapFraBusinessObject(List<Avvik> avvikListe,
      BeregnSaertilskuddResultat resultat) {
    return new BeregnSaertilskuddResultatCore(
        mapResultatPeriode(resultat.getResultatPeriodeListe()), mapSjablonGrunnlagListe(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFom(),
              resultatPeriode.getPeriode().getDatoTil()),
          resultatPeriode.getSoknadsbarnPersonId(),
          new ResultatBeregningCore(saertilskuddResultat.getResultatBelop(), saertilskuddResultat.getResultatkode().toString()),
          mapReferanseListe(resultatPeriode)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getGrunnlag();
    var referanseListe = new ArrayList<String>();

    referanseListe.add(resultatGrunnlag.getBidragsevne().getReferanse());
    referanseListe.add(resultatGrunnlag.getBPsAndelSaertilskudd().getReferanse());
    resultatGrunnlag.getSamvaersfradragGrunnlagListe().forEach(samvaersfradragGrunnlag -> referanseListe.add(samvaersfradragGrunnlag.getReferanse()));
    resultatGrunnlag.getLopendeBidragListe().forEach(lopendeBidrag -> referanseListe.add(lopendeBidrag.getReferanse()));
    referanseListe.addAll(resultatPeriode.getResultat().getSjablonListe().stream().map(this::lagSjablonReferanse).distinct().collect(toList()));
    return referanseListe.stream().sorted().collect(toList());
  }

  protected String lagSjablonReferanse(SjablonPeriodeNavnVerdi sjablon) {
    return "Sjablon_" + sjablon.getNavn() + "_" + sjablon.getPeriode().getDatoFom().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonGrunnlagListe(List<ResultatPeriode> resultatPeriodeListe) {
    return resultatPeriodeListe.stream()
        .map(ResultatPeriode::getResultat)
        .map(resultat -> mapSjablonListe(resultat.getSjablonListe()))
        .flatMap(Collection::stream)
        .distinct()
        .collect(toList());
  }
}
