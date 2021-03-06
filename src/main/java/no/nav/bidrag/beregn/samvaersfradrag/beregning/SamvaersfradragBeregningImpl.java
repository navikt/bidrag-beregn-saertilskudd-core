package no.nav.bidrag.beregn.samvaersfradrag.beregning;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPerBarn;

public class SamvaersfradragBeregningImpl implements SamvaersfradragBeregning {

  @Override
  public List<ResultatBeregning> beregn(GrunnlagBeregningPeriodisert resultatGrunnlag) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();

    for (SamvaersfradragGrunnlagPerBarn grunnlag: resultatGrunnlag.getSamvaersfradragGrunnlagPerBarnListe()) {
      // Henter sjablonverdier
      var sjablonNavnVerdiMap = hentSjablonVerdier(resultatGrunnlag.getSjablonListe(), grunnlag.getSamvaersklasse(),
          grunnlag.getBarnAlder());

      var belopFradrag = sjablonNavnVerdiMap.get(SjablonNavn.SAMVAERSFRADRAG.getNavn());

      System.out.println("Samværsfradrag barnPersonId: " + grunnlag.getBarnPersonId());
      System.out.println("Beregnet samværsfradrag: " + belopFradrag);
      System.out.println("Alder: " + grunnlag.getBarnAlder());

      resultatBeregningListe.add(new ResultatBeregning(grunnlag.getBarnPersonId(),
          belopFradrag, byggSjablonResultatListe(sjablonNavnVerdiMap)));
    }

    return resultatBeregningListe;

  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<Sjablon> sjablonListe, String samvaersklasse, int soknadBarnAlder) {

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Samværsfradrag
    sjablonNavnVerdiMap.put(SjablonNavn.SAMVAERSFRADRAG.getNavn(), SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.SAMVAERSFRADRAG,
        singletonList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), samvaersklasse)), SjablonNokkelNavn.ALDER_TOM, soknadBarnAlder,
        SjablonInnholdNavn.FRADRAG_BELOP));

    return sjablonNavnVerdiMap;
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap) {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) -> sjablonNavnVerdiListe.add(new SjablonNavnVerdi(sjablonNavn, sjablonVerdi)));
    return sjablonNavnVerdiListe.stream().sorted(comparing(SjablonNavnVerdi::getSjablonNavn)).collect(toList());
  }
}
