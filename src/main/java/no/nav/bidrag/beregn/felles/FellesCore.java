package no.nav.bidrag.beregn.felles;

import java.time.format.DateTimeFormatter;
import java.util.List;
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
            new PeriodeCore(sjablon.getPeriode().getDatoFom(), sjablon.getPeriode().getDatoTil()), sjablon.getNavn(), sjablon.getVerdi()))
        .toList();
  }
}
