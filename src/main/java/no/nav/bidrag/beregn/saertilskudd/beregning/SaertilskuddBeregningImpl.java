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
          lopendeBidrag.getOpprinneligBPsAndelSaertilskuddBelop()
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
        totaltLopendeBidragBelop.add(totaltSamvaersfradragBelop));

    System.out.println("Beregning særtilskudd:");
    System.out.println("totaltBidragBleRedusertMedBelop: " + totaltBidragBleRedusertMedBelop);
    System.out.println("totaltLopendeBidragBelop: " + totaltLopendeBidragBelop);
    System.out.println("totaltSamvaersfradragBelop: " + totaltSamvaersfradragBelop);
    System.out.println("totaltBidragBelop: " + totaltBidragBelop);
    System.out.println("bidragsevne: " + grunnlagBeregning.getBidragsevne().getBidragsevneBelop());

    if (grunnlagBeregning.getBidragsevne().getBidragsevneBelop().compareTo(
        totaltBidragBelop) < 0) {
      resultatkode = ResultatKode.SAERTILSKUDD_IKKE_FULL_BIDRAGSEVNE;
    }

    if (grunnlagBeregning.getBPsAndelSaertilskudd().getBarnetErSelvforsorget()){
      resultatkode = ResultatKode.BARNET_ER_SELVFORSORGET;
      System.out.println("resultatkode: " + resultatkode.toString());
      return
          new ResultatBeregning(BigDecimal.ZERO, resultatkode);
    } else {
      System.out.println("resultatkode: " + resultatkode.toString());
      return
          new ResultatBeregning(
          grunnlagBeregning.getBPsAndelSaertilskudd().getBPsAndelSaertilskuddBelop(),
          resultatkode);
    }




  }
}
