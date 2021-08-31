package no.nav.bidrag.beregn.bidragsevne;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.BARN_I_HUSSTAND_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BOSTATUS_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SAERFRADRAG_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SKATTEKLASSE_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneResultat;
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag;
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse;
import no.nav.bidrag.beregn.bidragsevne.dto.AntallBarnIEgetHusholdPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SaerfradragPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SkatteklassePeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode;
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
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("BidragsevneCore (dto-test)")
public class BidragsevnePeriodeCoreTest {

  private BidragsevneCore bidragsevneCore;

  @Mock
  private BidragsevnePeriode bidragsevnePeriodeMock;

  private BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore;
  private BeregnBidragsevneResultat bidragsevnePeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    bidragsevneCore = new BidragsevneCoreImpl(bidragsevnePeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne bidragsevne")
  void skalBeregnebidragsevne() {
    byggBidragsevnePeriodeGrunnlagCore();
    byggBidragsevnePeriodeResultat();

    when(bidragsevnePeriodeMock.beregnPerioder(any())).thenReturn(bidragsevnePeriodeResultat);
    var beregnbidragsevneResultatCore = bidragsevneCore.beregnBidragsevne(
        beregnBidragsevneGrunnlagCore);

    assertAll(
        () -> assertThat(beregnbidragsevneResultatCore).isNotNull(),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatEvneBelop())
            .isEqualTo(BigDecimal.valueOf(666)),

        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(0))
            .isEqualTo(BARN_I_HUSSTAND_REFERANSE),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(1))
            .isEqualTo(BOSTATUS_REFERANSE),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(2))
            .isEqualTo(INNTEKT_REFERANSE),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(3))
            .isEqualTo(SAERFRADRAG_REFERANSE),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(4))
            .isEqualTo(SKATTEKLASSE_REFERANSE),

        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatEvneBelop())
            .isEqualTo(BigDecimal.valueOf(667)),

        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatEvneBelop())
            .isEqualTo(BigDecimal.valueOf(668))
    );
  }

  @Test
  @DisplayName("Skal ikke beregne bidragsevne ved avvik")
  void skalIkkeBeregneBidragsevneVedAvvik() {
    byggBidragsevnePeriodeGrunnlagCore();
    byggAvvik();

    when(bidragsevnePeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = bidragsevneCore.beregnBidragsevne(
        beregnBidragsevneGrunnlagCore);

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


  private void byggBidragsevnePeriodeGrunnlagCore() {

    var inntektPeriode = new InntektPeriodeCore(INNTEKT_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), InntektType.LONN_SKE.toString(),
        BigDecimal.valueOf(666000));
    var inntektPeriodeListe = new ArrayList<InntektPeriodeCore>();
    inntektPeriodeListe.add(inntektPeriode);

    var skatteklassePeriode = new SkatteklassePeriodeCore(TestUtil.SKATTEKLASSE_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), 1);
    var skatteklassePeriodeListe = new ArrayList<SkatteklassePeriodeCore>();
    skatteklassePeriodeListe.add(skatteklassePeriode);

    var bostatusPeriode = new BostatusPeriodeCore(TestUtil.BOSTATUS_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BostatusKode.MED_ANDRE.toString());
    var bostatusPeriodeListe = new ArrayList<BostatusPeriodeCore>();
    bostatusPeriodeListe.add(bostatusPeriode);

    var antallEgneBarnIHusstandPeriode = new AntallBarnIEgetHusholdPeriodeCore(TestUtil.BARN_I_HUSSTAND_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), 1);
    var antallEgneBarnIHusstandPeriodeListe = new ArrayList<AntallBarnIEgetHusholdPeriodeCore>();
    antallEgneBarnIHusstandPeriodeListe.add(antallEgneBarnIHusstandPeriode);

    var saerfradragPeriode = new SaerfradragPeriodeCore(TestUtil.SAERFRADRAG_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), SaerfradragKode.HELT.toString());
    var saerfradragPeriodeListe = new ArrayList<SaerfradragPeriodeCore>();
    saerfradragPeriodeListe.add(saerfradragPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnBidragsevneGrunnlagCore = new BeregnBidragsevneGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        inntektPeriodeListe, skatteklassePeriodeListe, bostatusPeriodeListe, antallEgneBarnIHusstandPeriodeListe,
        saerfradragPeriodeListe, sjablonPeriodeListe);
  }

  private void byggBidragsevnePeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(666),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
            new Skatteklasse(SKATTEKLASSE_REFERANSE, 1), new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE),
            new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1), new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(667),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
            new Skatteklasse(SKATTEKLASSE_REFERANSE, 1), new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE),
            new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1), new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(668),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
            new Skatteklasse(SKATTEKLASSE_REFERANSE, 1), new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE),
            new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1), new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    bidragsevnePeriodeResultat = new BeregnBidragsevneResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }
}
