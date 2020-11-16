package no.nav.bidrag.beregn.bpsandelsaerbidrag.periode;

import java.util.List;
import no.nav.bidrag.beregn.bpsandelsaerbidrag.beregning.BPsAndelUnderholdskostnadBeregning;
import no.nav.bidrag.beregn.bpsandelsaerbidrag.bo.BeregnBPsAndelSaerbidragGrunnlag;
import no.nav.bidrag.beregn.bpsandelsaerbidrag.bo.BeregnBPsAndelSaerbidragResultat;
import no.nav.bidrag.beregn.felles.bo.Avvik;

public interface BPsAndelUnderholdskostnadPeriode {
  BeregnBPsAndelSaerbidragResultat beregnPerioder(
      BeregnBPsAndelSaerbidragGrunnlag beregnBPsAndelSaerbidragGrunnlag);

  List<Avvik> validerInput(BeregnBPsAndelSaerbidragGrunnlag beregnBPsAndelSaerbidragGrunnlag);

  static BPsAndelUnderholdskostnadPeriode getInstance() {
    return new BPsAndelUnderholdskostnadPeriodeImpl(BPsAndelUnderholdskostnadBeregning.getInstance());
  }

}
