package no.nav.bidrag.beregn.saertilskudd;

import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddResultatCore;
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode;


public interface SaertilskuddCore {

  BeregnSaertilskuddResultatCore beregnSaertilskudd(BeregnSaertilskuddGrunnlagCore beregnSaertilskuddGrunnlagCore);

  static SaertilskuddCore getInstance() {
    return new SaertilskuddCoreImpl(SaertilskuddPeriode.getInstance());
  }
}
