package no.nav.bidrag.beregn.bpsandelsaertilskudd;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddGrunnlag;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.BeregnBPsAndelSaertilskuddResultat;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.bo.NettoSaertilskuddPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.Rolle;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.felles.enums.SoknadType;
import no.nav.bidrag.beregn.bpsandelsaertilskudd.periode.BPsAndelSaertilskuddPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BPsAndelSaertilskuddPeriodeTest {

  private BeregnBPsAndelSaertilskuddGrunnlag grunnlag;

  private BPsAndelSaertilskuddPeriode bPsAndelSaertilskuddPeriode = BPsAndelSaertilskuddPeriode.getInstance();

  @Test
  @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  void testPeriodisering() {

    lagGrunnlag("2018-07-01", "2020-08-01");

    var resultat = bPsAndelSaertilskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(35.2)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-07-01")),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isNull()
    );

    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

    lagGrunnlag("2016-01-01", "2021-01-01");
    var avvikListe = bPsAndelSaertilskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(6),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i inntektBPPeriodeListe (2018-01-01) er etter beregnDatoFra (2016-01-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i inntektBPPeriodeListe (2020-08-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );

    printAvvikListe(avvikListe);
  }

  @Test
  @DisplayName("Test med ugyldig inntekt som skal resultere i avvik")
  void testGrunnlagMedAvvikUgyldigInntekt() {

    lagGrunnlagMedAvvikUgyldigInntekt();
    var avvikListe = bPsAndelSaertilskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(4),

        () -> assertThat(avvikListe.get(0).getAvvikTekst()).isEqualTo("inntektType " + InntektType.SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG.toString() +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG.toString() + " og rolle " + Rolle.BIDRAGSPLIKTIG.toString()),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE),

        () -> assertThat(avvikListe.get(1).getAvvikTekst()).isEqualTo("inntektType " + InntektType.PENSJON_KORRIGERT_BARNETILLEGG.toString() +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG.toString() + " og rolle " + Rolle.BIDRAGSMOTTAKER.toString()),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE),

        () -> assertThat(avvikListe.get(2).getAvvikTekst()).isEqualTo("inntektType " + InntektType.PENSJON_KORRIGERT_BARNETILLEGG.toString() +
            " er kun gyldig fom. 2015-01-01 tom. 2015-12-31"),
        () -> assertThat(avvikListe.get(2).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_PERIODE),

        () -> assertThat(avvikListe.get(3).getAvvikTekst()).isEqualTo("inntektType " + InntektType.BARNETRYGD_MANUELL_VURDERING.toString() +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG.toString() + " og rolle " + Rolle.SOKNADSBARN.toString()),
        () -> assertThat(avvikListe.get(3).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE)
    );

    printAvvikListe(avvikListe);
  }

  @Test
  @DisplayName("Test justering av inntekter BP")
  void testJusteringAvInntekterBP() {

    lagGrunnlagMedInntekterTilJustering("BP");
    var resultat = bPsAndelSaertilskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(250000))
    );
  }

  @Test
  @DisplayName("Test justering av inntekter BM")
  void testJusteringAvInntekterBM() {

    lagGrunnlagMedInntekterTilJustering("BM");
    var resultat = bPsAndelSaertilskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(250000))
    );
  }

  @Test
  @DisplayName("Test justering av inntekter BB")
  void testJusteringAvInntekterBB() {

    lagGrunnlagMedInntekterTilJustering("BB");
    var resultat = bPsAndelSaertilskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(250000))
    );
  }

  private void lagGrunnlag(String beregnDatoFra, String beregnDatoTil) {

    var soknadsbarnPersonId = 1;
    var nettoSaertilskuddPeriodeListe = new ArrayList<NettoSaertilskuddPeriode>();
    var inntektBPPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriode>();

    nettoSaertilskuddPeriodeListe.add(new NettoSaertilskuddPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        BigDecimal.valueOf(1000)));

    inntektBPPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(217666), false, false));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000), false, false));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(40000), false, false));

    grunnlag = new BeregnBPsAndelSaertilskuddGrunnlag(LocalDate.parse(beregnDatoFra), LocalDate.parse(beregnDatoTil),
        soknadsbarnPersonId, nettoSaertilskuddPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe,
        inntektBBPeriodeListe, lagSjablonGrunnlag());
  }

  private void lagGrunnlagMedAvvikUgyldigInntekt() {

    var soknadsbarnPersonId = 1;
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");
    var nettoSaertilskuddPeriodeListe = new ArrayList<NettoSaertilskuddPeriode>();


    var inntektBPPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriode>();

    nettoSaertilskuddPeriodeListe.add(new NettoSaertilskuddPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        BigDecimal.valueOf(1000)));

    inntektBPPeriodeListe
        .add(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG,
            BigDecimal.valueOf(666001), false, false));

    inntektBMPeriodeListe.add(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.PENSJON_KORRIGERT_BARNETILLEGG,
        BigDecimal.valueOf(400000), false, false));

    inntektBBPeriodeListe.add(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.BARNETRYGD_MANUELL_VURDERING,
        BigDecimal.valueOf(40000), false, false));

    grunnlag = new BeregnBPsAndelSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
        nettoSaertilskuddPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, lagSjablonGrunnlag());
  }

  private void lagGrunnlagMedInntekterTilJustering(String rolle) {

    var soknadsbarnPersonId = 1;
    var beregnDatoFra = LocalDate.parse("2018-01-01");
    var beregnDatoTil = LocalDate.parse("2020-07-01");
    var nettoSaertilskuddPeriodeListe = new ArrayList<NettoSaertilskuddPeriode>();

    nettoSaertilskuddPeriodeListe.add(new NettoSaertilskuddPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        BigDecimal.valueOf(1000)));

    List<InntektPeriode> inntektBPPeriodeListe;
    List<InntektPeriode> inntektBMPeriodeListe;
    List<InntektPeriode> inntektBBPeriodeListe;

    if (rolle.equals("BP")) {
      inntektBPPeriodeListe = lagJustertInntektGrunnlag();
    } else {
      inntektBPPeriodeListe = singletonList(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil),
          InntektType.SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(666001), false, false));
    }

    if (rolle.equals("BM")) {
      inntektBMPeriodeListe = lagJustertInntektGrunnlag();
    } else {
      inntektBMPeriodeListe = singletonList(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil),
          InntektType.PENSJON_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(400000), false, false));
    }

    if (rolle.equals("BB")) {
      inntektBBPeriodeListe = lagJustertInntektGrunnlag();
    } else {
      inntektBBPeriodeListe = emptyList();
    }

    grunnlag = new BeregnBPsAndelSaertilskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
        nettoSaertilskuddPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, lagSjablonGrunnlag());
  }


  private List<SjablonPeriode> lagSjablonGrunnlag() {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1600))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1640))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2020-07-01"), null),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1670))))));

    return sjablonPeriodeListe;
  }

  private List<InntektPeriode> lagJustertInntektGrunnlag() {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();

    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2018-01-01"), null),
            InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(200000), false, false));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2018-06-01"), LocalDate.parse("2018-12-31")),
            InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
            BigDecimal.valueOf(150000), false, false));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null),
            InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT, BigDecimal.valueOf(300000), false, false));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null),
            InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER, BigDecimal.valueOf(100000), false, false));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2020-01-01"), null),
            InntektType.ATTFORING_AAP, BigDecimal.valueOf(250000), false, false));

    return inntektPeriodeListe;
  }

  private void printGrunnlagResultat(
      BeregnBPsAndelSaertilskuddResultat beregnBPsAndelSaertilskuddResultat) {
    beregnBPsAndelSaertilskuddResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                + sortedPR.getResultatDatoFraTil().getDatoTil()
                + "; " + "Prosentandel: " + sortedPR.getResultatBeregning().getResultatAndelProsent()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
