package no.nav.bidrag.beregn.felles;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore;

public abstract class FellesCore {

  protected String lagSjablonReferanse(SjablonPeriodeNavnVerdi sjablon) {
    return "Sjablon_" + sjablon.getNavn() + "_" + sjablon.getPeriode().getDatoFom().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

  protected List<SjablonResultatGrunnlagCore> mapSjablonListe(List<SjablonPeriodeNavnVerdi> sjablonListe) {
    return sjablonListe.stream()
        .map(sjablon -> new SjablonResultatGrunnlagCore(lagSjablonReferanse(sjablon),
            new PeriodeCore(sjablon.getPeriode().getDatoFom(), sjablon.getPeriode().getDatoTil()),
            sjablon.getNavn(), sjablon.getVerdi()))
        .collect(toList());
  }
}
