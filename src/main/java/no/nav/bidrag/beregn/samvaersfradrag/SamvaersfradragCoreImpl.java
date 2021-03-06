package no.nav.bidrag.beregn.samvaersfradrag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPerBarn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragGrunnlagCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragResultatCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.GrunnlagBeregningPeriodisertCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.SamvaersfradragGrunnlagPerBarnCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.SamvaersklassePeriodeCore;
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode;

public class SamvaersfradragCoreImpl implements SamvaersfradragCore {

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

  private List<SamvaersfradragGrunnlagPeriode> mapSamvaersklassePeriodeListe(
      List<SamvaersklassePeriodeCore> samvaersklassePeriodeListeCore) {
    var samvaersklassePeriodeListe = new ArrayList<SamvaersfradragGrunnlagPeriode>();
    for (SamvaersklassePeriodeCore samvaersklassePeriodeCore : samvaersklassePeriodeListeCore) {
      samvaersklassePeriodeListe.add(new SamvaersfradragGrunnlagPeriode(
          new Periode(samvaersklassePeriodeCore.getSamvaersklassePeriodeDatoFraTil().getPeriodeDatoFra(),
              samvaersklassePeriodeCore.getSamvaersklassePeriodeDatoFraTil().getPeriodeDatoTil()),
              samvaersklassePeriodeCore.getBarnPersonId(),
              samvaersklassePeriodeCore.getBarnFodselsdato(),
              samvaersklassePeriodeCore.getSamvaersklasse()));
    }
    return samvaersklassePeriodeListe;
  }

  private BeregnSamvaersfradragResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnSamvaersfradragResultat resultat) {
    return new BeregnSamvaersfradragResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var samvaersfradragResultat = resultatPeriode.getResultatBeregningListe();
      var samvaersfradragResultatGrunnlag = resultatPeriode.getResultatGrunnlag();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(resultatPeriode.getResultatDatoFraTil().getDatoFra(), resultatPeriode.getResultatDatoFraTil().getDatoTil()),
          mapResultatBeregning(resultatPeriode.getResultatBeregningListe()),
          new GrunnlagBeregningPeriodisertCore(
              mapSamvaersfradragPerBarn(resultatPeriode.getResultatGrunnlag().getSamvaersfradragGrunnlagPerBarnListe()),
              mapResultatGrunnlagSjabloner(resultatPeriode.getResultatBeregningListe().get(0).getSjablonListe()))));
    }
    return resultatPeriodeCoreListe;
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

  private List<SamvaersfradragGrunnlagPerBarnCore> mapSamvaersfradragPerBarn(
      List<SamvaersfradragGrunnlagPerBarn> samvaersfradragGrunnlagPerBarnListe) {
    var SamvaersfradragGrunnlagPerBarnListeCore = new ArrayList<SamvaersfradragGrunnlagPerBarnCore>();
    for (SamvaersfradragGrunnlagPerBarn samvaersfradragGrunnlagPerBarn : samvaersfradragGrunnlagPerBarnListe) {
      SamvaersfradragGrunnlagPerBarnListeCore
          .add(new SamvaersfradragGrunnlagPerBarnCore(samvaersfradragGrunnlagPerBarn.getBarnPersonId(),
              samvaersfradragGrunnlagPerBarn.getBarnAlder(), samvaersfradragGrunnlagPerBarn.getSamvaersklasse()));
    }
    return SamvaersfradragGrunnlagPerBarnListeCore;
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
