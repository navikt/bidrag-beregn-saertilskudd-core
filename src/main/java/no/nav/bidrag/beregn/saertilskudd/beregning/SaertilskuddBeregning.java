package no.nav.bidrag.beregn.saertilskudd.beregning;

import java.util.List;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;

public interface SaertilskuddBeregning {

  ResultatBeregning beregn(
      GrunnlagBeregning grunnlagBeregning);

  static SaertilskuddBeregning getInstance() {
    return new SaertilskuddBeregningImpl();
  }

}
