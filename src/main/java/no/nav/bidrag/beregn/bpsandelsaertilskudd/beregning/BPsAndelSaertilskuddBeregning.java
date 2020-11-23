package no.nav.bidrag.beregn.bpsandelsaertilskudd.beregning;

import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatBeregning;

public interface BPsAndelSaertilskuddBeregning {

  ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  ResultatBeregning beregnMedGamleRegler(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  static BPsAndelSaertilskuddBeregning getInstance(){
    return new BPsAndelSaertilskuddBeregningImpl();
  }

}