package no.nav.bidrag.beregn.saertilskudd.beregning;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlag;

public class SaertilskuddBeregningImpl implements SaertilskuddBeregning {

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning) {

    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregning.getSjablonListe());

    var resultatkode = ResultatKode.SAERTILSKUDD_INNVILGET;
    var totaltBidragBleRedusertMedBelop = BigDecimal.ZERO;
    var totaltLopendeBidragBelop = BigDecimal.ZERO;
    var totaltSamvaersfradragBelop = BigDecimal.ZERO;
    for (LopendeBidrag lopendeBidrag : grunnlagBeregning.getLopendeBidragListe()) {
      totaltBidragBleRedusertMedBelop = totaltBidragBleRedusertMedBelop.add(
          lopendeBidrag.getOpprinneligBPsAndelUnderholdskostnadBelop()
              .subtract(
                  lopendeBidrag.getOpprinneligBidragBelop().add(
                      lopendeBidrag.getOpprinneligSamvaersfradragBelop()
                  )));

      totaltLopendeBidragBelop = totaltLopendeBidragBelop.add(
          lopendeBidrag.getLopendeBidragBelop()
      );
    }

    for (SamvaersfradragGrunnlag samvaersfradragGrunnlag : grunnlagBeregning.getSamvaersfradragGrunnlagListe()) {
      totaltSamvaersfradragBelop = totaltSamvaersfradragBelop.add(
          samvaersfradragGrunnlag.getSamvaersfradragBelop()
      );
    }

    var totaltBidragBelop = totaltBidragBleRedusertMedBelop.add(
        totaltLopendeBidragBelop.add(totaltSamvaersfradragBelop));

    System.out.println("Beregning særtilskudd:");
    System.out.println("totaltBidragBleRedusertMedBelop: " + totaltBidragBleRedusertMedBelop);
    System.out.println("totaltLopendeBidragBelop: " + totaltLopendeBidragBelop);
    System.out.println("totaltSamvaersfradragBelop: " + totaltSamvaersfradragBelop);
    System.out.println("totaltBidragBelop: " + totaltBidragBelop);
    System.out.println("bidragsevne: " + grunnlagBeregning.getBidragsevne().getBidragsevneBelop());

    if (grunnlagBeregning.getBidragsevne().getBidragsevneBelop().compareTo(
        totaltBidragBelop) < 0) {
      resultatkode = ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE;
    }

    if (grunnlagBeregning.getBPsAndelSaertilskudd().getBarnetErSelvforsorget()) {
      resultatkode = ResultatKode.BARNET_ER_SELVFORSORGET;
      System.out.println("resultatkode: " + resultatkode.toString());
      return
          new ResultatBeregning(BigDecimal.ZERO, resultatkode, byggSjablonResultatListe(sjablonNavnVerdiMap, grunnlagBeregning.getSjablonListe()));
    } else {
      System.out.println("resultatkode: " + resultatkode.toString());
      return
          new ResultatBeregning(
              grunnlagBeregning.getBPsAndelSaertilskudd().getBPsAndelSaertilskuddBelop(),
              resultatkode, byggSjablonResultatListe(sjablonNavnVerdiMap, grunnlagBeregning.getSjablonListe()));
    }
  }

  protected List<SjablonPeriodeNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap,
      List<SjablonPeriode> sjablonPeriodeListe) {
    var sjablonPeriodeNavnVerdiListe = new ArrayList<SjablonPeriodeNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) ->
        sjablonPeriodeNavnVerdiListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe, sjablonNavn), sjablonNavn, sjablonVerdi)));
    return sjablonPeriodeNavnVerdiListe.stream().sorted(comparing(SjablonPeriodeNavnVerdi::getNavn)).collect(toList());
  }

  private Periode hentPeriode(List<SjablonPeriode> sjablonPeriodeListe, String sjablonNavn) {
    return sjablonPeriodeListe.stream()
        .filter(sjablonPeriode -> sjablonPeriode.getSjablon().getNavn().equals(modifiserSjablonNavn(sjablonNavn)))
        .map(SjablonPeriode::getPeriode)
        .findFirst()
        .orElse(new Periode(LocalDate.MIN, LocalDate.MAX));
  }

  // Enkelte sjablonnavn må justeres for å finne riktig dato
  private String modifiserSjablonNavn(String sjablonNavn) {
    if (sjablonNavn.equals("UnderholdBeløp")) {
      return "Bidragsevne";
    }
    if (sjablonNavn.equals("BoutgiftBeløp")) {
      return "Bidragsevne";
    }
    if (sjablonNavn.startsWith("Trinnvis")) {
      return "TrinnvisSkattesats";
    }
    return sjablonNavn;
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<SjablonPeriode> sjablonPeriodeListe) {

    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .collect(toList());

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP));
    sjablonNavnVerdiMap.put(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP));

    return sjablonNavnVerdiMap;
  }
}
