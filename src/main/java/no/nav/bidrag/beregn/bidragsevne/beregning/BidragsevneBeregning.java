package no.nav.bidrag.beregn.bidragsevne.beregning;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning;

public interface BidragsevneBeregning {

  ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning);

  BigDecimal beregnMinstefradrag(GrunnlagBeregning grunnlagBeregning, BigDecimal minstefradragInntektBelop,
      BigDecimal minstefradragInntektSjablonProsent);

  BigDecimal beregnSkattetrinnBelop(GrunnlagBeregning grunnlagBeregning);

  static BidragsevneBeregning getInstance() {
    return new BidragsevneBeregningImpl();
  }
}
