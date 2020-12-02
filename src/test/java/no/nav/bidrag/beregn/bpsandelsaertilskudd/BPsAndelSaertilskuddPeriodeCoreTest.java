package no.nav.bidrag.beregn.bpsandelsaertilskudd;

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
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddResultat;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatBeregning;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.BeregnBPsAndelSaertilskuddGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.dto.NettoSaertilskuddPeriodeCore;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.periode.BPsAndelSaertilskuddPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("BPsAndelSaertilskuddCore (dto-test)")
public class BPsAndelSaertilskuddPeriodeCoreTest {

  private BPsAndelSaertilskuddCore bPsAndelSaertilskuddCore;

  @Mock
  private BPsAndelSaertilskuddPeriode bPsAndelSaertilskuddPeriodeMock;

  private BeregnBPsAndelSaertilskuddGrunnlagCore beregnBPsAndelSaertilskuddGrunnlagCore;
  private BeregnBPsAndelSaertilskuddResultat bPsAndelSaertilskuddPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    bPsAndelSaertilskuddCore = new BPsAndelSaertilskuddCoreImpl(
        bPsAndelSaertilskuddPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne BPsAndel av Saertilskudd")
  void skalBeregneBPsAndelSaertilskudd() {
    byggBPsAndelSaertilskuddPeriodeGrunnlagCore();
    byggBPsAndelSaertilskuddPeriodeResultat();

    when(bPsAndelSaertilskuddPeriodeMock.beregnPerioder(any())).thenReturn(
        bPsAndelSaertilskuddPeriodeResultat);
    var beregnBPsAndelSaertilskuddResultatCore = bPsAndelSaertilskuddCore.beregnBPsAndelSaertilskudd(
        beregnBPsAndelSaertilskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore).isNotNull(),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(10)),

        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(20)),

        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(30)),
        () -> assertThat(beregnBPsAndelSaertilskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe().get(0)
            .getSjablonVerdi()).isEqualTo(BigDecimal.valueOf(1600))

    );
  }

  @Test
  @DisplayName("Skal ikke beregne BPs andel av Saertilskudd ved avvik")
  void skalIkkeBeregneAndelVedAvvik() {
    byggBPsAndelSaertilskuddPeriodeGrunnlagCore();
    byggAvvik();

    when(bPsAndelSaertilskuddPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = bPsAndelSaertilskuddCore.beregnBPsAndelSaertilskudd(
        beregnBPsAndelSaertilskuddGrunnlagCore);

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


  private void byggBPsAndelSaertilskuddPeriodeGrunnlagCore() {

    var nettoSaertilskuddPeriode = new NettoSaertilskuddPeriodeCore(
        new PeriodeCore(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        BigDecimal.valueOf(1000));

    var inntektBPPeriode = new InntektPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), InntektType.LONN_SKE.toString(),
        BigDecimal.valueOf(111), false, false);

    var inntektBMPeriode = new InntektPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), InntektType.LONN_SKE.toString(),
        BigDecimal.valueOf(222), false, false);

    var inntektBBPeriode = new InntektPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), InntektType.LONN_SKE.toString(),
        BigDecimal.valueOf(333), false, false);

    var nettoSaertilskuddPeriodeListe = new ArrayList<NettoSaertilskuddPeriodeCore>();
    var inntektBPPeriodeListe = new ArrayList<InntektPeriodeCore>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriodeCore>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriodeCore>();

    nettoSaertilskuddPeriodeListe.add(new NettoSaertilskuddPeriodeCore(
        new PeriodeCore(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        BigDecimal.valueOf(1000)));

    inntektBPPeriodeListe.add(inntektBPPeriode);
    inntektBMPeriodeListe.add(inntektBMPeriode);
    inntektBBPeriodeListe.add(inntektBBPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1600))));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnBPsAndelSaertilskuddGrunnlagCore = new BeregnBPsAndelSaertilskuddGrunnlagCore(LocalDate.parse("2017-01-01"),
        LocalDate.parse("2020-01-01"), nettoSaertilskuddPeriodeListe,
        inntektBPPeriodeListe, inntektBMPeriodeListe,
        inntektBBPeriodeListe, sjablonPeriodeListe);
  }

  private void byggBPsAndelSaertilskuddPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    var nettoSaertilskuddBelop = BigDecimal.valueOf(1000);
    var inntektBPListe = new ArrayList<Inntekt>();
    var inntektBMListe = new ArrayList<Inntekt>();
    var inntektBBListe = new ArrayList<Inntekt>();

    inntektBPListe.add(new Inntekt(InntektType.LONN_SKE,BigDecimal.valueOf(111d), false, false));
    inntektBMListe.add(new Inntekt(InntektType.LONN_SKE,BigDecimal.valueOf(222d), false, false));
    inntektBBListe.add(new Inntekt(InntektType.LONN_SKE,BigDecimal.valueOf(333d), false, false));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(10), BigDecimal.valueOf(1000),false,
            singletonList(new SjablonNavnVerdi(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), BigDecimal.valueOf(1600)))),
        new GrunnlagBeregning(nettoSaertilskuddBelop, inntektBPListe, inntektBMListe, inntektBBListe,
            singletonList(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                    BigDecimal.valueOf(1600))))))));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(20), BigDecimal.valueOf(1000), false,
            singletonList(new SjablonNavnVerdi(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), BigDecimal.valueOf(1600)))),
        new GrunnlagBeregning(nettoSaertilskuddBelop,inntektBPListe, inntektBMListe, inntektBBListe,
            singletonList(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                    BigDecimal.valueOf(1640))))))));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(30), BigDecimal.valueOf(1000), false,
            singletonList(new SjablonNavnVerdi(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), BigDecimal.valueOf(1600)))),
        new GrunnlagBeregning(nettoSaertilskuddBelop, inntektBPListe, inntektBMListe, inntektBBListe,
            singletonList(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                    BigDecimal.valueOf(1680))))))));

    bPsAndelSaertilskuddPeriodeResultat = new BeregnBPsAndelSaertilskuddResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }

}
