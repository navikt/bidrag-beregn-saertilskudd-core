# bidrag-beregn-saertilskudd-core
![](https://github.com/navikt/bidrag-beregn-saertilskudd-core/workflows/maven%20deploy/badge.svg)

Repo for beregning av særtilskudd-core. Disse erstatter beregninger i BBM.
Disse beregningene gjøres:

###BeregnBidragsevne - Returnerer periodisert liste med BPs bidragsevne
|Felt | Kilde | Beskrivelse   
| ------------| ----- | ------------- |
beregnDatoFra                     | Bisys          | Dato satt i Bisys, dato fra og til skal være én måned og danner utgangspunktet for resultatperioden
beregnDatoTil                     | Bisys          | Dato satt i Bisys, dato fra og til skal være én måned og danner utgangspunktet for resultatperioden
inntektPeriodeListe               | Bisys          | Liste med BPs inntekter, periodisert
skatteklassePeriodeListe          | Bisys          | Liste med skatteklasse for BP, periodisert
bostatusPeriodeListe              | Bisys          | Liste med BPs bostatus, periodisert
antallBarnIEgetHusholdPeriodeListe| Bisys          | Liste med antall barn i BPs husholdning, periodisert
saerfradragPeriodeListe           | Bisys          | Liste over særfradrag, periodisert
sjablonPeriodeListe               | bidrag-sjablon | Sjabloner for beregningsperioden


###BeregnBPsAndelSaertilskudd - Returnerer periodisert liste med BPs andel av særtilskudd
|Felt | Kilde | Beskrivelse
| ------------| ----- | ------------- |
beregnDatoFra                     | Bisys          | Dato satt i Bisys, dato fra og til skal være én måned og danner utgangspunktet for resultatperioden
beregnDatoTil                     | Bisys          | Dato satt i Bisys, dato fra og til skal være én måned og danner utgangspunktet for resultatperioden
nettoSaertilskuddPeriodeListe     | Bisys          | Liste med netto særtilskudd, periodisert. Vil i praksis bestå av ett nettobeløp for særtilskudd
inntektBPPeriodeListe             | Bisys          | Liste med inntekter for BP, periodisert
inntektBMPeriodeListe             | Bisys          | Liste med inntekter for BM, periodisert
inntektBBPeriodeListe             | Bisys          | Liste med inntekter for BB (bidragsbarn), periodisert
sjablonPeriodeListe               | bidrag-sjablon | Sjabloner for beregningsperioden


###BeregnSamvaersfradrag - Returnerer periodisert liste med oppdaterte samværsfradragbeløp for alle BPs barn med bidrag
|Felt | Kilde | Beskrivelse
| ------------| ----- | ------------- |
beregnDatoFra                     | Bisys          | Dato satt i Bisys, dato fra og til skal være én måned og danner utgangspunktet for resultatperioden
beregnDatoTil                     | Bisys          | Dato satt i Bisys, dato fra og til skal være én måned og danner utgangspunktet for resultatperioden
samvaersklassePeriodeListe        | Bisys          | Liste med personId, fødselsdato og samværsklasser for alle BPs barn
sjablonPeriodeListe               | bidrag-sjablon | Sjabloner for beregningsperioden


###BeregnSaertilskudd - Sluttberegning som returnerer BPs andel av særtilskudd og resultatkode som sier om søknaden er innvilget
|Felt | Kilde | Beskrivelse
| ------------| ----- | ------------- |
beregnDatoFra                     | Bisys             | Dato satt i Bisys, dato fra og til skal være én måned og danner utgangspunktet for resultatperioden
beregnDatoTil                     | Bisys             | Dato satt i Bisys, dato fra og til skal være én måned og danner utgangspunktet for resultatperioden
soknadsbarnPersonId               | Bisys             | PersonId for søknadsbarnet
bidragsevnePeriodeListe           | BeregnBidragsevne | Liste med BPs bidragsevne, periodisert
bPsAndelSaertilskuddPeriodeListe  | BeregnBPsAndelSaertilskudd | Liste med BPs andel av netto særtilskudd, periodisert
lopendeBidragPeriodeListe         | Bisys             | Liste med info om alle BPs løpende bidrag. Skal inneholde løpende satser og info fra siste vedtak
samvaersklassePeriodeListe        | BeregnSamvaersfradrag      | Liste med personId, fødselsdato og samværsklasser for alle BPs barn



## Changelog:

Versjon | Endringstype | Beskrivelse
--------|--------------|------------
0.5.3   | Endret       | Oppdatert readme med beskrivelse av beregning og felter
0.5.2   | Endret       | Fjernet sjekk på opphold og overlapp på perioder i LopendeBidrag og Samvaersfradrag i input til beregn samvaersfradrag
0.5.1   | Endret       | Fjernet sjekk på opphold og overlapp på perioder i LopendeBidrag og Samvaersfradrag i input til beregn særtilskudd
0.5.0   | Endret       | Rettet feil funnet i test via rest, fjernet 25% av inntekt i resultat av bidragsevne siden det ikke brukes i særtilskudd, resultatkode i løpende bidrag også fjernet
0.4.0   | Endret       | Rettet feil logikk for å beregne samværsfradrag for alle BPs barn
0.3.0   | Endret       | Beregningsregler og tester lagt inn, sjabloner fjernet fra input til beregning av særtilskudd siden det ikke var i bruk
0.2.0   | Endret       | Lagt til første versjon av beregningslogikk og fjernet soknadsbarnPersonId fra BPs andel av saertilskudd, gjort om input til samvær og særtilskuddberegning til lister for å få med info om alle barn
0.1.0   | Endret       | Nye felter lagt til i input beregning av særtilskudd plus noen feilrettinger
0.0.4   | Endret       | Har lagt inn lik logikk for periodisering og fjernet personid for søknadsbarn fra input til beregninger
0.0.3   | Endret       | Lagt til beregning av samværsfradrag
0.0.2   | Endret       | Grunnstruktur er satt
0.0.1   | Opprettet    | Init commit for beregning av særtilskudd
