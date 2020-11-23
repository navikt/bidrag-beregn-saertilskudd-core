package no.nav.bidrag.beregn.saertilskudd.beregning;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class SaertilskuddBeregningImpl implements SaertilskuddBeregning {

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning) {

    var bidragRedusertAvBidragsevne = false;

//    var resultatBeregningListe = new ArrayList<ResultatBeregning>();

    var totaltBelopUnderholdskostnad = BigDecimal.ZERO;

    var maksBidragsbelop = BigDecimal.ZERO;

    if (grunnlagBeregning.getBidragsevne().getBidragsevneBelop().compareTo(
        grunnlagBeregning.getBidragsevne().getTjuefemProsentInntekt()) < 0) {
      maksBidragsbelop = grunnlagBeregning.getBidragsevne().getBidragsevneBelop();
      bidragRedusertAvBidragsevne = true;
    } else {
      maksBidragsbelop = grunnlagBeregning.getBidragsevne().getTjuefemProsentInntekt();
    }
//    System.out.println("maksBidragsbelop: " + maksBidragsbelop);

    var resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;

    var saertilskuddBeløp = BigDecimal.ZERO;

    // Sjekker om totalt bidragsbeløp er større enn bidragsevne eller 25% av månedsinntekt
    if (maksBidragsbelop.compareTo(totaltBelopUnderholdskostnad) < 0) {
      // Bidraget skal begrenses forholdsmessig pga manglende evne/25%-regel
      var andelProsent = grunnlagBeregning.getBPsAndelSaertilskudd()
          .getBPsAndelSaertilskuddBelop()
          .divide(totaltBelopUnderholdskostnad,
              new MathContext(10, RoundingMode.HALF_UP));
//        System.out.println("Andel av evne: " + andelProsent);

      saertilskuddBeløp = maksBidragsbelop.multiply(andelProsent);
      if (bidragRedusertAvBidragsevne) {
        resultatkode = ResultatKode.BIDRAG_REDUSERT_AV_EVNE;
      } else {
        resultatkode = ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT;
      }
    } else {
      saertilskuddBeløp = grunnlagBeregning.getBPsAndelSaertilskudd()
          .getBPsAndelSaertilskuddBelop();
    }

//    Trekker fra samværsfradrag
    saertilskuddBeløp = saertilskuddBeløp.subtract(grunnlagBeregning.getSamvaersfradrag());
//    System.out.println("Bidrag etter samværsfradrag: " + tempBarnebidrag);

    // Beløp for bidrag settes til 0 hvis bidraget er utregnet til negativt beløp etter samværsfradrag
    if (saertilskuddBeløp.compareTo(BigDecimal.ZERO) <= 0) {
      saertilskuddBeløp = BigDecimal.ZERO;
    }

    if (grunnlagBeregning.getBidragsevne().getBidragsevneBelop().compareTo(
        BigDecimal.ZERO) == 0) {
      resultatkode = ResultatKode.INGEN_EVNE;
    }

    if (grunnlagBeregning.getBPsAndelSaertilskudd().getBarnetErSelvforsorget()) {
      saertilskuddBeløp = BigDecimal.ZERO;
      resultatkode = ResultatKode.BARNET_ER_SELVFORSORGET;
    }

    // Bidrag skal avrundes til nærmeste tier
    saertilskuddBeløp = saertilskuddBeløp.setScale(-1, RoundingMode.HALF_UP);

    return new ResultatBeregning(saertilskuddBeløp, resultatkode, emptyList());
  }


  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<Sjablon> sjablonListe) {
    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe,
            SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP));
    sjablonNavnVerdiMap.put(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe,
            SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP));

    return sjablonNavnVerdiMap;
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonNavnVerdi> byggSjablonResultatListe(
      Map<String, BigDecimal> sjablonNavnVerdiMap) {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) -> sjablonNavnVerdiListe
        .add(new SjablonNavnVerdi(sjablonNavn, sjablonVerdi)));
    return sjablonNavnVerdiListe.stream().sorted(comparing(SjablonNavnVerdi::getSjablonNavn))
        .collect(toList());
  }
}
