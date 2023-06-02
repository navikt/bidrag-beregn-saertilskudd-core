package no.nav.bidrag.beregn.saertilskudd.beregning;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.felles.FellesBeregning;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlag;

public class SaertilskuddBeregningImpl extends FellesBeregning implements SaertilskuddBeregning {

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning) {

    var resultatkode = ResultatKode.SAERTILSKUDD_INNVILGET;
    var totaltBidragBleRedusertMedBelop = BigDecimal.ZERO;
    var totaltLopendeBidragBelop = BigDecimal.ZERO;
    var totaltSamvaersfradragBelop = BigDecimal.ZERO;
    for (LopendeBidrag lopendeBidrag : grunnlagBeregning.getLopendeBidragListe()) {
      totaltBidragBleRedusertMedBelop = totaltBidragBleRedusertMedBelop
          .add(lopendeBidrag.getOpprinneligBPsAndelUnderholdskostnadBelop()
              .subtract(lopendeBidrag.getOpprinneligBidragBelop()
                  .add(lopendeBidrag.getOpprinneligSamvaersfradragBelop())));

      totaltLopendeBidragBelop = totaltLopendeBidragBelop.add(lopendeBidrag.getLopendeBidragBelop());
    }

    for (SamvaersfradragGrunnlag samvaersfradragGrunnlag : grunnlagBeregning.getSamvaersfradragGrunnlagListe()) {
      totaltSamvaersfradragBelop = totaltSamvaersfradragBelop.add(samvaersfradragGrunnlag.getSamvaersfradragBelop());
    }

    var totaltBidragBelop = totaltBidragBleRedusertMedBelop.add(totaltLopendeBidragBelop.add(totaltSamvaersfradragBelop));

    if (grunnlagBeregning.getBidragsevne().getBidragsevneBelop().compareTo(totaltBidragBelop) < 0) {
      resultatkode = ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE;
      return new ResultatBeregning(BigDecimal.ZERO, resultatkode);
    }

    if (grunnlagBeregning.getBPsAndelSaertilskudd().getBarnetErSelvforsorget()) {
      return new ResultatBeregning(BigDecimal.ZERO, ResultatKode.BARNET_ER_SELVFORSORGET);
    } else {
      return new ResultatBeregning(grunnlagBeregning.getBPsAndelSaertilskudd().getBPsAndelSaertilskuddBelop(), resultatkode);
    }
  }
}
