package no.nav.bidrag.beregn.saertilskudd.beregning

import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning

fun interface SaertilskuddBeregning {

    fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning

    companion object {
        fun getInstance(): SaertilskuddBeregning = SaertilskuddBeregningImpl()
    }
}
