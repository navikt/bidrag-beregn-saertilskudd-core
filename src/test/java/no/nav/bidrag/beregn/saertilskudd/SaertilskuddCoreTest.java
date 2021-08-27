package no.nav.bidrag.beregn.saertilskudd;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregning;
import no.nav.bidrag.beregn.saertilskudd.beregning.SaertilskuddBeregningImpl;
import no.nav.bidrag.beregn.saertilskudd.bo.BPsAndelSaertilskudd;
import no.nav.bidrag.beregn.saertilskudd.bo.BeregnSaertilskuddResultat;
import no.nav.bidrag.beregn.saertilskudd.bo.Bidragsevne;
import no.nav.bidrag.beregn.saertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.LopendeBidrag;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.saertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.saertilskudd.bo.SamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.saertilskudd.dto.BPsAndelSaertilskuddPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BeregnSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.saertilskudd.dto.BidragsevnePeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.LopendeBidragPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriode;
import no.nav.bidrag.beregn.saertilskudd.periode.SaertilskuddPeriodeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@DisplayName("SaertilskuddCore (dto-test)")
public class SaertilskuddCoreTest {

  private SaertilskuddCore SaertilskuddCoreWithMock;

  @Mock
  private SaertilskuddPeriode SaertilskuddPeriodeMock;

  private SaertilskuddPeriodeImpl saertilskuddPeriode;

  private SaertilskuddBeregning saertilskuddBeregning;

  private SaertilskuddCore saertilskuddCore;

  private BeregnSaertilskuddGrunnlagCore beregnSaertilskuddGrunnlagCore;
  private BeregnSaertilskuddResultat beregnSaertilskuddPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    SaertilskuddCoreWithMock = new SaertilskuddCoreImpl(SaertilskuddPeriodeMock);
    saertilskuddBeregning = new SaertilskuddBeregningImpl();
    saertilskuddPeriode = new SaertilskuddPeriodeImpl(saertilskuddBeregning);
    saertilskuddCore = new SaertilskuddCoreImpl(saertilskuddPeriode);
  }

  @Test
  @DisplayName("Skal beregne Saertilskudd")
  void skalBeregneSaertilskudd() {
    byggSaertilskuddPeriodeGrunnlagCore();
    byggSaertilskuddPeriodeResultat();

    when(SaertilskuddPeriodeMock.beregnPerioder(any())).thenReturn(
        beregnSaertilskuddPeriodeResultat);
    var beregnSaertilskuddResultatCore = SaertilskuddCoreWithMock.beregnSaertilskudd(
        beregnSaertilskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnSaertilskuddResultatCore).isNotNull(),
        () -> assertThat(beregnSaertilskuddResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01"))

    );
  }

  @Test
  @DisplayName("Skal beregne Saertilskudd uten mocks")
  void skalBeregneSaertilskuddUtenMocks() {
    byggSaertilskuddPeriodeGrunnlagCore();
    var beregnSaertilskuddResultatCore = saertilskuddCore.beregnSaertilskudd(
        beregnSaertilskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnSaertilskuddResultatCore).isNotNull(),
        () -> assertThat(beregnSaertilskuddResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().size()).isEqualTo(6)

    );
  }

  @Test
  @DisplayName("Skal ikke beregne Saertilskudd ved avvik")
  void skalIkkeBeregneSaertilskuddVedAvvik() {
    byggSaertilskuddPeriodeGrunnlagCore();
    byggAvvik();

    when(SaertilskuddPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = SaertilskuddCoreWithMock.beregnSaertilskudd(
        beregnSaertilskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnbidragsevneResultatCore).isNotNull(),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe()).isNotEmpty(),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe()).hasSize(1),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(
            AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }


  private void byggSaertilskuddPeriodeGrunnlagCore() {


    var bidragsevnePeriode = new BidragsevnePeriodeCore(TestUtil.BIDRAGSEVNE_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(100000));
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriodeCore>();
    bidragsevnePeriodeListe.add(bidragsevnePeriode);

    var bPsAndelSaertilskuddPeriode = new BPsAndelSaertilskuddPeriodeCore(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(100000), BigDecimal.valueOf(20000), false);
    var bPsAndelSaertilskuddPeriodeListe = new ArrayList<BPsAndelSaertilskuddPeriodeCore>();
    bPsAndelSaertilskuddPeriodeListe.add(bPsAndelSaertilskuddPeriode);

    var lopendeBidragPeriode = new LopendeBidragPeriodeCore(TestUtil.LOPENDE_BIDRAG_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), 1,
        BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000),
        BigDecimal.valueOf(1000));
    var lopendeBidragPeriodeListe = new ArrayList<LopendeBidragPeriodeCore>();
    lopendeBidragPeriodeListe.add(lopendeBidragPeriode);

    var samvaersfradragPeriode = new SamvaersfradragPeriodeCore(TestUtil.SAMVAERSFRADRAG_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), 1,
        BigDecimal.valueOf(1000));
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriodeCore>();
    samvaersfradragPeriodeListe.add(samvaersfradragPeriode);

    var sjablonPeriodeListe = mapSjablonSjablontall(TestUtil.byggSjablonPeriodeListe());

    beregnSaertilskuddGrunnlagCore = new BeregnSaertilskuddGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        1, bidragsevnePeriodeListe, bPsAndelSaertilskuddPeriodeListe, lopendeBidragPeriodeListe,
        samvaersfradragPeriodeListe, sjablonPeriodeListe);
  }

  public List<SjablonPeriodeCore> mapSjablonSjablontall(List<SjablonPeriode> sjablonPeriodeListe) {
    return sjablonPeriodeListe
        .stream()
        .map(sjablon -> new SjablonPeriodeCore(
            new PeriodeCore(sjablon.getPeriode().getDatoFom(), sjablon.getPeriode().getDatoTil()),
            sjablon.getSjablon().getNavn(),
            sjablon.getSjablon().getNokkelListe().stream().map(sjablonNokkel -> new SjablonNokkelCore(sjablonNokkel.getNavn(), sjablonNokkel.getVerdi())).collect(toList()),
            sjablon.getSjablon().getInnholdListe().stream().map(sjablonInnhold -> new SjablonInnholdCore(sjablonInnhold.getNavn(), sjablonInnhold.getVerdi())).collect(toList())))
        .collect(toList());
  }

  private void byggSaertilskuddPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();
    var lopendeBidragListe = new ArrayList<LopendeBidrag>();

    lopendeBidragListe.add(new LopendeBidrag(TestUtil.LOPENDE_BIDRAG_REFERANSE,1,
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000)
    ));

    var samvaersfradragListe = new ArrayList<SamvaersfradragGrunnlag>();

    samvaersfradragListe.add(new SamvaersfradragGrunnlag(TestUtil.SAMVAERSFRADRAG_REFERANSE,1,
        BigDecimal.valueOf(100)));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), 1,
        new ResultatBeregning(BigDecimal.valueOf(1000), ResultatKode.KOSTNADSBEREGNET_BIDRAG, singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
            SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))
        ),
        new GrunnlagBeregning(new Bidragsevne(TestUtil.BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(1000)),
            new BPsAndelSaertilskudd(TestUtil.BPS_ANDEL_SAERTILSKUDD_REFERANSE, BigDecimal.valueOf(60), BigDecimal.valueOf(8000), false),
            lopendeBidragListe, samvaersfradragListe, TestUtil.byggSjablonPeriodeListe()
        )));

    beregnSaertilskuddPeriodeResultat = new BeregnSaertilskuddResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }

}
