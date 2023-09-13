package no.nav.bidrag.beregn.bpsandelsaertilskudd.beregning;

import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatBeregning;

public interface BPsAndelSaertilskuddBeregning {

  ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning);

  static BPsAndelSaertilskuddBeregning getInstance() {
    return new BPsAndelSaertilskuddBeregningImpl();
  }

}