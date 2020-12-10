package no.nav.bidrag.beregn.bpsandelsaertilskudd.beregning;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BPsAndelSaertilskuddBeregningImpl implements BPsAndelSaertilskuddBeregning {

  @Override
  public ResultatBeregning beregn(
      GrunnlagBeregning grunnlagBeregning) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregning.getSjablonListe());

    var andelProsent = BigDecimal.ZERO;
    var andelBelop = BigDecimal.ZERO;
    var barnetErSelvforsorget = false;

    // Legger sammen inntektene
    var inntektBP = grunnlagBeregning.getInntektBPListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

//    System.out.println("BP: " + inntektBP);

    // Legger sammen inntektene
    var inntektBM = grunnlagBeregning.getInntektBMListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
//    System.out.println("BM: " + inntektBM);

    // Legger sammen inntektene
    var inntektBB = grunnlagBeregning.getInntektBBListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
//    System.out.println("BB: " + inntektBB);

    // Test på om barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd. Hvis så så skal ikke BPs andel regnes ut.
    if (inntektBB.compareTo(
        sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()).multiply(BigDecimal.valueOf(100))) > 0) {
      barnetErSelvforsorget = true;
    } else {
      inntektBB = inntektBB.subtract(
          sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()).multiply(BigDecimal.valueOf(30)));

/*      System.out.println(
          "30 * forhøyet forskudd: " + sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()).multiply(BigDecimal.valueOf(30)));
      System.out.println("InntektBB etter fratrekk av 30 * forhøyet forskudd: " + inntektBB); */

      if (inntektBB.compareTo(BigDecimal.ZERO) < 0) {
        inntektBB = BigDecimal.ZERO;
      }

      andelProsent = (inntektBP.divide(
          inntektBP.add(inntektBM).add(inntektBB),
          new MathContext(10, RoundingMode.HALF_UP))
              .multiply(BigDecimal.valueOf(100)));

      andelProsent = andelProsent.setScale(1, RoundingMode.HALF_UP);
//      System.out.println("andelProsent: " + andelProsent);

      // Utregnet andel skal ikke være større en 5/6
      if (andelProsent.compareTo(BigDecimal.valueOf(83.3333333333)) > 0) {
        andelProsent = BigDecimal.valueOf(83.3333333333);
      }

      andelBelop = grunnlagBeregning.getNettoSaertilskuddBelop().multiply(andelProsent)
          .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

    }

    return new ResultatBeregning(andelProsent, andelBelop, barnetErSelvforsorget, byggSjablonResultatListe(sjablonNavnVerdiMap));
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<Sjablon> sjablonListe) {

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORSKUDDSSATS_BELOP));

    return sjablonNavnVerdiMap;
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap) {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) -> sjablonNavnVerdiListe.add(new SjablonNavnVerdi(sjablonNavn, sjablonVerdi)));
    return sjablonNavnVerdiListe.stream().sorted(comparing(SjablonNavnVerdi::getSjablonNavn)).collect(toList());
  }
}
