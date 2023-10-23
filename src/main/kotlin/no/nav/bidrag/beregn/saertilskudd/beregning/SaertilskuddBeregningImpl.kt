package no.nav.bidrag.beregn.saertilskudd.beregning

import no.nav.bidrag.beregn.felles.FellesBeregning
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeSaertilskudd
import java.math.BigDecimal

class SaertilskuddBeregningImpl : FellesBeregning(), SaertilskuddBeregning {

    override fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning {
        val totaltBidragBleRedusertMedBelop = grunnlag.lopendeBidragListe.sumOf {
            it.opprinneligBPsAndelUnderholdskostnadBelop - (it.opprinneligBidragBelop + it.opprinneligSamvaersfradragBelop)
        }

        val totaltLopendeBidragBelop = grunnlag.lopendeBidragListe.sumOf { it.lopendeBidragBelop }

        val totaltSamvaersfradragBelop = grunnlag.samvaersfradragGrunnlagListe.sumOf { it.samvaersfradragBelop }

        val totaltBidragBelop = totaltBidragBleRedusertMedBelop + totaltLopendeBidragBelop + totaltSamvaersfradragBelop

        return when {
            grunnlag.bidragsevne.bidragsevneBelop < totaltBidragBelop -> ResultatBeregning(
                resultatBelop = BigDecimal.ZERO,
                resultatkode = ResultatKodeSaertilskudd.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE
            )
            grunnlag.bPsAndelSaertilskudd.barnetErSelvforsorget -> ResultatBeregning(
                resultatBelop = BigDecimal.ZERO,
                resultatkode = ResultatKodeSaertilskudd.BARNET_ER_SELVFORSORGET
            )
            else -> ResultatBeregning(
                resultatBelop = grunnlag.bPsAndelSaertilskudd.bPsAndelSaertilskuddBelop,
                resultatkode = ResultatKodeSaertilskudd.SAERTILSKUDD_INNVILGET
            )
        }
    }
}
