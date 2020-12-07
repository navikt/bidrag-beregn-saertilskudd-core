package no.nav.bidrag.beregn.saertilskudd.beregning;

import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.saertilskudd.bo.Samvaersfradrag;

public class SaertilskuddBeregningImpl implements SaertilskuddBeregning {

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning) {

    var resultatkode = ResultatKode.SAERTILSKUDD_INNVILGET;
    var totaltBidragBleRedusertMedBelop = BigDecimal.ZERO;
    var totaltLopendeBidragBelop = BigDecimal.ZERO;
    var totaltSamvaersfradragBelop = BigDecimal.ZERO;
    for (LopendeBidrag lopendeBidrag: grunnlagBeregning.getLopendeBidragListe()){
      totaltBidragBleRedusertMedBelop = totaltBidragBleRedusertMedBelop.add(
          lopendeBidrag.getOpprinneligBPsAndelUnderholdskostnadBelop()
          .subtract(
          lopendeBidrag.getOpprinneligBidragBelop().add(
              lopendeBidrag.getOpprinneligSamvaersfradragBelop()
              )));

      totaltLopendeBidragBelop = totaltLopendeBidragBelop.add(
          lopendeBidrag.getLopendeBidragBelop()
      );
    }

    for (Samvaersfradrag samvaersfradrag: grunnlagBeregning.getSamvaersfradragListe() ){
      totaltSamvaersfradragBelop = totaltSamvaersfradragBelop.add(
          samvaersfradrag.getSamvaersfradragBelop()
      );
    }

    var totaltBidragBelop = totaltBidragBleRedusertMedBelop.add(
        totaltLopendeBidragBelop.add(totaltSamvaersfradragBelop)
    );

    if (grunnlagBeregning.getBidragsevne().getBidragsevneBelop().compareTo(
        totaltBidragBelop) >= 0) {
      resultatkode = ResultatKode.SAERTILSKUDD_INNVILGET;
    } else {
      resultatkode = ResultatKode.BIDRAG_REDUSERT_AV_EVNE;
    }


    return new ResultatBeregning(
        grunnlagBeregning.getBPsAndelSaertilskudd().getBPsAndelSaertilskuddBelop(),
        resultatkode, emptyList());
  }
}
