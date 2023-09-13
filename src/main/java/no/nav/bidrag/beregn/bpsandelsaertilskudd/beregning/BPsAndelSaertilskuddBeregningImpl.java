package no.nav.bidrag.beregn.bpsandelsaertilskudd.beregning;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.FellesBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BPsAndelSaertilskuddBeregningImpl extends FellesBeregning implements BPsAndelSaertilskuddBeregning {

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning) {

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

    // Legger sammen inntektene
    var inntektBM = grunnlagBeregning.getInntektBMListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Legger sammen inntektene
    var inntektBB = grunnlagBeregning.getInntektBBListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Test på om barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd. Hvis så så skal ikke BPs andel regnes ut.
    if (inntektBB.compareTo(sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()).multiply(BigDecimal.valueOf(100))) > 0) {
      barnetErSelvforsorget = true;
    } else {
      inntektBB = inntektBB.subtract(sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()).multiply(BigDecimal.valueOf(30)));

      if (inntektBB.compareTo(BigDecimal.ZERO) < 0) {
        inntektBB = BigDecimal.ZERO;
      }

      andelProsent = inntektBP
          .divide(inntektBP.add(inntektBM).add(inntektBB), new MathContext(10, RoundingMode.HALF_UP))
          .multiply(BigDecimal.valueOf(100));

      andelProsent = andelProsent.setScale(1, RoundingMode.HALF_UP);

      // Utregnet andel skal ikke være større en 5/6
      if (andelProsent.compareTo(BigDecimal.valueOf(83.3333333333)) > 0) {
        andelProsent = BigDecimal.valueOf(83.3333333333);
      }

      andelBelop = grunnlagBeregning.getNettoSaertilskuddBelop()
          .multiply(andelProsent)
          .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
    }

    return new ResultatBeregning(andelProsent, andelBelop, barnetErSelvforsorget,
        byggSjablonResultatListe(sjablonNavnVerdiMap, grunnlagBeregning.getSjablonListe()));
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<SjablonPeriode> sjablonPeriodeListe) {

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .toList();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORSKUDDSSATS_BELOP));

    return sjablonNavnVerdiMap;
  }
}
