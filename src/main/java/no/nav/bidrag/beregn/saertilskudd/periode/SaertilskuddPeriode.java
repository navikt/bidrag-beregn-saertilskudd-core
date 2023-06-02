package no.nav.bidrag.beregn.saertilskudd.periode;

import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;

public interface SaertilskuddPeriode {

  BeregnSaertilskuddResultat beregnPerioder(BeregnSaertilskuddGrunnlag beregnSaertilskuddGrunnlag);

  List<Avvik> validerInput(BeregnSaertilskuddGrunnlag beregnSaertilskuddGrunnlag);

  static SaertilskuddPeriode getInstance() {
    return new SaertilskuddPeriodeImpl(SaertilskuddBeregning.getInstance());
  }
}
