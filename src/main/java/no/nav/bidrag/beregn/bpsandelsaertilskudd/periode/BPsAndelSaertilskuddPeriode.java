package no.nav.bidrag.beregn.bpsandelsaertilskudd.periode;

import java.util.List;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.beregning.BPsAndelSaertilskuddBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddResultat;
import no.nav.bidrag.beregn.felles.bo.Avvik;

public interface BPsAndelSaertilskuddPeriode {

  BeregnBPsAndelSaertilskuddResultat beregnPerioder(BeregnBPsAndelSaertilskuddGrunnlag beregnBPsAndelSaertilskuddGrunnlag);

  List<Avvik> validerInput(BeregnBPsAndelSaertilskuddGrunnlag beregnBPsAndelSaertilskuddGrunnlag);

  static BPsAndelSaertilskuddPeriode getInstance() {
    return new BPsAndelSaertilskuddPeriodeImpl(BPsAndelSaertilskuddBeregning.getInstance());
  }
}
