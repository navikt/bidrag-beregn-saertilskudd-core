package no.nav.bidrag.beregn.samvaersfradrag;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersfradragGrunnlagPerBarn;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragGrunnlagCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.SamvaersklassePeriodeCore;
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("SamvaersfradragCore (dto-test)")
public class SamvaersfradragCoreTest {

  private SamvaersfradragCore samvaersfradragCore;

  @Mock
  private SamvaersfradragPeriode samvaersfradragPeriodeMock;

  private BeregnSamvaersfradragGrunnlagCore beregnSamvaersfradragGrunnlagCore;
  private BeregnSamvaersfradragResultat samvaersfradragPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    samvaersfradragCore = new SamvaersfradragCoreImpl(
        samvaersfradragPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne samvaersfradrag")
  void skalBeregneSamvaersfradrag() {
    byggSamvaersfradragPeriodeGrunnlagCore();
    byggSamvaersfradragPeriodeResultat();

    when(samvaersfradragPeriodeMock.beregnPerioder(any())).thenReturn(
        samvaersfradragPeriodeResultat);
    var beregnSamvaersfradragResultatCore = samvaersfradragCore.beregnSamvaersfradrag(
        beregnSamvaersfradragGrunnlagCore);

    assertAll(
        () -> assertThat(beregnSamvaersfradragResultatCore).isNotNull(),
        () -> assertThat(beregnSamvaersfradragResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatSamvaersfradragBelop())
            .isEqualTo(BigDecimal.valueOf(666)),

        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatSamvaersfradragBelop())
            .isEqualTo(BigDecimal.valueOf(667)),

        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe().get(2).getResultatBeregningListe().get(0).getResultatSamvaersfradragBelop())
            .isEqualTo(BigDecimal.valueOf(668))
    );
  }

  @Test
  @DisplayName("Skal ikke beregne samvaersfradrag ved avvik")
  void skalIkkeBeregneAndelVedAvvik() {
    byggSamvaersfradragPeriodeGrunnlagCore();
    byggAvvik();

    when(samvaersfradragPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnSamvaersfradragResultatCore = samvaersfradragCore.beregnSamvaersfradrag(
        beregnSamvaersfradragGrunnlagCore);

    assertAll(
        () -> assertThat(beregnSamvaersfradragResultatCore).isNotNull(),
        () -> assertThat(beregnSamvaersfradragResultatCore.getAvvikListe()).isNotEmpty(),
        () -> assertThat(beregnSamvaersfradragResultatCore.getAvvikListe()).hasSize(1),
        () -> assertThat(beregnSamvaersfradragResultatCore.getAvvikListe().get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(beregnSamvaersfradragResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(
            AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnSamvaersfradragResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }


  private void byggSamvaersfradragPeriodeGrunnlagCore() {

    var samvaersklassePeriode = new SamvaersklassePeriodeCore(TestUtil.SAMVAERSKLASSE_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), 1,
        LocalDate.parse("2017-01-01"), "03");
    var samvaersklassePeriodeListe = new ArrayList<SamvaersklassePeriodeCore>();
    samvaersklassePeriodeListe.add(samvaersklassePeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1600))));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnSamvaersfradragGrunnlagCore = new BeregnSamvaersfradragGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        samvaersklassePeriodeListe, sjablonPeriodeListe);
  }

  private void byggSamvaersfradragPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        singletonList(new ResultatBeregning(1, BigDecimal.valueOf(666),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")), SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22))))),
        new GrunnlagBeregningPeriodisert(
            singletonList(new SamvaersfradragGrunnlagPerBarn(SAMVAERSFRADRAG_REFERANSE,1, 4, "02")),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")), new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                    BigDecimal.valueOf(1600)))))))));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        singletonList(new ResultatBeregning(1, BigDecimal.valueOf(667),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")), SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22))))),
        new GrunnlagBeregningPeriodisert(
            singletonList(new SamvaersfradragGrunnlagPerBarn(SAMVAERSFRADRAG_REFERANSE,1, 4, "02")),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")), new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                    BigDecimal.valueOf(1600)))))))));
    

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        singletonList(new ResultatBeregning(1, BigDecimal.valueOf(668),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")), SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22))))),
        new GrunnlagBeregningPeriodisert(
            singletonList(new SamvaersfradragGrunnlagPerBarn(SAMVAERSFRADRAG_REFERANSE,1, 4, "02")),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")), new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                    BigDecimal.valueOf(1600)))))))));

    samvaersfradragPeriodeResultat = new BeregnSamvaersfradragResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }

}
