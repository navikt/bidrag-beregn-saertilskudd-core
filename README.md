# bidrag-beregn-saertilskudd-core
![](https://github.com/navikt/bidrag-beregn-saertilskudd-core/workflows/maven%20deploy/badge.svg)

Repo for beregning av særtilskudd-core. Disse erstatter beregninger i BBM.

## Changelog:

Versjon | Endringstype | Beskrivelse
--------|--------------|------------
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
