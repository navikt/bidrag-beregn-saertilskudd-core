package no.nav.bidrag.beregn.samvaersfradrag.beregning;

import java.util.List;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;

public interface SamvaersfradragBeregning {

  List<ResultatBeregning> beregn(GrunnlagBeregningPeriodisert resultatGrunnlag);

  static SamvaersfradragBeregning getInstance(){
    return new SamvaersfradragBeregningImpl();
  }

}


