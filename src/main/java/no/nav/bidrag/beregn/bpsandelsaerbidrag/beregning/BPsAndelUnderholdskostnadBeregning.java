package no.nav.bidrag.beregn.bpsandelsaerbidrag.beregning;

import no.nav.bidrag.beregn.bpsandelsaerbidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bpsandelsaerbidrag.bo.ResultatBeregning;

public interface BPsAndelUnderholdskostnadBeregning {

  ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  ResultatBeregning beregnMedGamleRegler(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  static BPsAndelUnderholdskostnadBeregning getInstance(){
    return new BPsAndelUnderholdskostnadBeregningImpl();
  }

}