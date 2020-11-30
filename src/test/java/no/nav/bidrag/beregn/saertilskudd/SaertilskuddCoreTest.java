package no.nav.bidrag.beregn.saertilskudd;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.saertilskudd.dto.BPsAndelSaertilskuddPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BidragsevnePeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.LopendeBidragPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.dto.SamvaersklassePeriodeCore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("SaertilskuddCore (dto-test)")
public class SaertilskuddCoreTest {

  private SaertilskuddCore SaertilskuddCore;

  @Mock
  private SaertilskuddPeriode SaertilskuddPeriodeMock;

  private BeregnSaertilskuddGrunnlagCore beregnSaertilskuddGrunnlagCore;
  private BeregnSaertilskuddResultat beregnSaertilskuddPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    SaertilskuddCore = new SaertilskuddCoreImpl(SaertilskuddPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne Saertilskudd")
  void skalBeregneSaertilskudd() {
    byggSaertilskuddPeriodeGrunnlagCore();
    byggSaertilskuddPeriodeResultat();

    when(SaertilskuddPeriodeMock.beregnPerioder(any())).thenReturn(
        beregnSaertilskuddPeriodeResultat);
    var beregnSaertilskuddResultatCore = SaertilskuddCore.beregnSaertilskudd(
        beregnSaertilskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnSaertilskuddResultatCore).isNotNull(),
        () -> assertThat(beregnSaertilskuddResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),

        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe().get(0)
            .getSjablonVerdi()).isEqualTo(BigDecimal.valueOf(22))

    );
  }

  @Test
  @DisplayName("Skal ikke beregne Saertilskudd ved avvik")
  void skalIkkeBeregneSaertilskuddVedAvvik() {
    byggSaertilskuddPeriodeGrunnlagCore();
    byggAvvik();

    when(SaertilskuddPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = SaertilskuddCore.beregnSaertilskudd(
        beregnSaertilskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnbidragsevneResultatCore).isNotNull(),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe()).isNotEmpty(),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe()).hasSize(1),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(
            AvvikType.DATO_FRA_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }


  private void byggSaertilskuddPeriodeGrunnlagCore() {

    var bidragsevnePeriode = new BidragsevnePeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(100000), BigDecimal.valueOf(20000));
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriodeCore>();
    bidragsevnePeriodeListe.add(bidragsevnePeriode);

    var bPsAndelSaertilskuddPeriode = new BPsAndelSaertilskuddPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(100000), BigDecimal.valueOf(20000), false);
    var bPsAndelSaertilskuddPeriodeListe = new ArrayList<BPsAndelSaertilskuddPeriodeCore>();
    bPsAndelSaertilskuddPeriodeListe.add(bPsAndelSaertilskuddPeriode);

    var lopendeBidragPeriode = new LopendeBidragPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(1000), ResultatKode.BIDRAG_REDUSERT_AV_EVNE);
    var lopendeBidragPeriodeListe = new ArrayList<LopendeBidragPeriodeCore>();
    lopendeBidragPeriodeListe.add(lopendeBidragPeriode);

    var samvaersfradragPeriode = new SamvaersfradragPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(1000));
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriodeCore>();
    samvaersfradragPeriodeListe.add(samvaersfradragPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnSaertilskuddGrunnlagCore = new BeregnSaertilskuddGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        1, bidragsevnePeriodeListe, bPsAndelSaertilskuddPeriodeListe, lopendeBidragPeriodeListe,
        samvaersfradragPeriodeListe, sjablonPeriodeListe);
  }

  private void byggSaertilskuddPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(1000), ResultatKode.KOSTNADSBEREGNET_BIDRAG,
            singletonList(new SjablonNavnVerdi(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(12000)),
            new BPsAndelSaertilskudd(BigDecimal.valueOf(60), BigDecimal.valueOf(8000), false),
            new LopendeBidrag(BigDecimal.valueOf(100), ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
            BigDecimal.valueOf(100),
            singletonList(new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))))))));

    beregnSaertilskuddPeriodeResultat = new BeregnSaertilskuddResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }

}
