package no.nav.bidrag.beregn.saertilskudd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregningImpl;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd;
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.saertilskudd.bo.Samvaersfradrag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av barnebidrag")
public class SaertilskuddBeregningTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Beregner ved full evne, ett barn, ingen barnetillegg")
  @Test
  void testBeregningEttBarnMedFullEvneIngenBarnetillegg() {
    SaertilskuddBeregningImpl barnebidragBeregning = new SaertilskuddBeregningImpl();

    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(1,
        BigDecimal.valueOf(1000),
        BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000),
        ResultatKode.BIDRAG_REDUSERT_AV_EVNE));

    var samvaersfradragListe = new ArrayList<Samvaersfradrag>();

    samvaersfradragListe.add(new Samvaersfradrag(1,
        BigDecimal.valueOf(100)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        new BPsAndelSaertilskudd(BigDecimal.valueOf(17), BigDecimal.valueOf(1000),
            false), lopendeBidragListe, samvaersfradragListe, sjablonListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
//    assertEquals(8000, resultat.getResultatBelop());
//    assertEquals(ResultatKode.KOSTNADSBEREGNET_BIDRAG, resultat.getResultatkode());
  }
}
