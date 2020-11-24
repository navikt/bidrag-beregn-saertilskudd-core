package no.nav.bidrag.beregn.bpsandelsaertilskudd;

import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.BeregnBPsAndelSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.BeregnBPsAndelSaertilskuddResultatCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.periode.BPsAndelSaertilskuddPeriode;

public interface BPsAndelSaertilskuddCore {

  BeregnBPsAndelSaertilskuddResultatCore beregnBPsAndelSaertilskudd (
      BeregnBPsAndelSaertilskuddGrunnlagCore beregnBPsAndelSaertilskuddGrunnlagCore);

  static BPsAndelSaertilskuddCore getInstance() {
    return new BPsAndelSaertilskuddCoreImpl(BPsAndelSaertilskuddPeriode.getInstance());
  }
}