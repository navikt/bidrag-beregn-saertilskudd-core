package no.nav.bidrag.beregn.saertilskudd

import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddGrunnlagCore
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddResultatCore
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode

fun interface SaertilskuddCore {
    fun beregnSaertilskudd(grunnlag: BeregnSaertilskuddGrunnlagCore): BeregnSaertilskuddResultatCore

    companion object {
        fun getInstance(): SaertilskuddCore = SaertilskuddCoreImpl(SaertilskuddPeriode.getInstance())
    }
}
