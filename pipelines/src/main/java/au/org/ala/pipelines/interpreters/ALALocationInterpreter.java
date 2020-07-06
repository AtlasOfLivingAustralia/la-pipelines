package au.org.ala.pipelines.interpreters;

import au.org.ala.kvs.ALAPipelinesConfig;
import au.org.ala.pipelines.parser.CoordinatesParser;
import au.org.ala.pipelines.parser.DistanceRangeParser;
import au.org.ala.pipelines.vocabulary.*;
import com.google.common.base.Strings;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.gbif.api.vocabulary.Country;
import org.gbif.common.parsers.CountryParser;
import org.gbif.common.parsers.core.ParseResult;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.core.interpreters.core.LocationInterpreter;
import org.gbif.pipelines.core.interpreters.core.TemporalInterpreter;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.LocationRecord;
import org.gbif.pipelines.io.avro.MetadataRecord;
import org.gbif.pipelines.parsers.parsers.SimpleTypeParser;
import org.gbif.pipelines.parsers.parsers.common.ParsedField;
import org.gbif.pipelines.parsers.parsers.location.GeocodeKvStore;
import org.gbif.rest.client.geocode.GeocodeResponse;
import org.gbif.rest.client.geocode.Location;
import com.google.common.collect.Range;


import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

import static org.gbif.pipelines.parsers.utils.ModelUtils.addIssue;
import static org.gbif.pipelines.parsers.utils.ModelUtils.extractNullAwareValue;
import static org.gbif.pipelines.parsers.utils.ModelUtils.*;

import org.gbif.common.parsers.date.TemporalAccessorUtils;
import org.gbif.api.vocabulary.OccurrenceIssue;
import org.gbif.common.parsers.core.OccurrenceParseResult;

@Slf4j
public class ALALocationInterpreter {

  /**
   * Extended from GBIF interpretCountryAndCoordinates <p> Add centre of country check
   *
   * @param service GBIF country related service
   */
  public static BiConsumer<ExtendedRecord, LocationRecord> interpretCountryAndCoordinates(
      KeyValueStore<LatLng, GeocodeResponse> service, MetadataRecord mdr) {
    return (er, lr) -> {
      if (service != null) {
        ParsedField<LatLng> parsedLatLon = CoordinatesParser.parseCoords(er);
        if (parsedLatLon.isSuccessful()) {
          LatLng latlng = parsedLatLon.getResult();
          lr.setDecimalLatitude(latlng.getLatitude());
          lr.setDecimalLongitude(latlng.getLongitude());
          lr.setHasCoordinate(true);

          GeocodeResponse gr = service.get(latlng);
          if (gr != null) {
            Collection<Location> locations = gr.getLocations();
            Optional<Location> countryLocation = locations.stream()
                .filter(location -> location.getType().equalsIgnoreCase("Political") || location
                    .getType().equalsIgnoreCase("EEZ")).findFirst();
            if (countryLocation.isPresent()) {
              //SHP file supplied by ALA only contains country iso code.
              String countryIsoCode = countryLocation.get().getIsoCountryCode2Digit();
              lr.setCountryCode(countryIsoCode);
              //Find country name / coordinate centre from country file
              ParseResult<Country> parsedCountry = CountryParser.getInstance()
                  .parse(countryIsoCode);
              if (parsedCountry.isSuccessful()) {
                lr.setCountry(parsedCountry.getPayload().name());
              } else {
                addIssue(lr, ALAOccurrenceIssue.UNKNOWN_COUNTRY_NAME.name());
                if (log.isDebugEnabled()) {
                  log.debug("Country ISO code {} not found!", countryIsoCode);
                }
              }
            } else {
              log.debug("Country at {}, {} is not found in SHP file", latlng.getLatitude(),
                  latlng.getLongitude());
            }
          }
        }
        Set<String> latLonIssues = parsedLatLon.getIssues();
        addIssue(lr, latLonIssues);
      } else {
        log.error("Geoservice for Country is not initialized!");
      }
    };
  }

  /**
   * @param service Provided by ALA country/state SHP file
   */
  public static BiConsumer<ExtendedRecord, LocationRecord> interpretStateProvince(
KeyValueStore<LatLng, GeocodeResponse> service) {
    return (er, lr) -> {
      ParsedField<LatLng> parsedLatLon = CoordinatesParser.parseCoords(er);
      if (parsedLatLon.isSuccessful()) {
        org.gbif.kvs.geocode.LatLng latlng = parsedLatLon.getResult();
        lr.setDecimalLatitude(latlng.getLatitude());
        lr.setDecimalLongitude(latlng.getLongitude());
        lr.setHasCoordinate(true);
        GeocodeResponse gr = service.get(latlng);
        if (gr != null) {
          Collection<Location> locations = gr.getLocations();
          Optional<Location> state = locations.stream()
              .filter(location -> location.getType().equalsIgnoreCase("State")).findFirst();

          if (state.isPresent()) {
            lr.setStateProvince(state.get().getCountryName());
            //Check centre of State

          } else {
            if (log.isDebugEnabled()) {
              log.debug("Current stateProvince SHP file does not contain a state at {}, {}",
                      latlng.getLatitude(), latlng.getLongitude());
            }
          }
        } else {
          if (log.isDebugEnabled()) {
            log.debug("No recognised stateProvince  is found at : {},{}", parsedLatLon.getResult().getLatitude(),
                    parsedLatLon.getResult().getLongitude());
          }
        }
      }
      //Assign state from source if no state is fetched from coordinates
      if (Strings.isNullOrEmpty(lr.getStateProvince())) {
        LocationInterpreter.interpretStateProvince(er, lr);
      }

      Set<String> issues = parsedLatLon.getIssues();
      addIssue(lr, issues);
    };
  }

  public static BiConsumer<ExtendedRecord, LocationRecord> verifyLocationInfo(
     ALAPipelinesConfig alaConfig) {
    return (er, lr) -> {
      if (lr.getDecimalLongitude() !=null && lr.getDecimalLatitude() !=null){
        if (!Strings.isNullOrEmpty(lr.getCountry())){
          try{
            if (CountryCentrePoints.getInstance(alaConfig.getLocationInfoConfig().getCountryCentrePointsFile())
                .coordinatesMatchCentre(lr.getCountry(), lr.getDecimalLatitude(),
                    lr.getDecimalLongitude())) {
              addIssue(lr, ALAOccurrenceIssue.COORDINATES_CENTRE_OF_COUNTRY.name());
            }

            if (!CountryMatch.getInstance(alaConfig.getLocationInfoConfig().getCountryNamesFile()).matched(lr.getCountry())) {
              addIssue(lr, ALAOccurrenceIssue.UNKNOWN_COUNTRY_NAME.name());
            }
          }catch(FileNotFoundException fnfe){
            String error = "FATAL：" + fnfe.getMessage();

            error = joptsimple.internal.Strings.LINE_SEPARATOR + joptsimple.internal.Strings
                .repeat('*',128) + joptsimple.internal.Strings.LINE_SEPARATOR + error + joptsimple.internal.Strings.LINE_SEPARATOR ;
            error += joptsimple.internal.Strings.LINE_SEPARATOR + "The following properties are mandatory in the pipelines.yaml for location interpretation:";
            error += joptsimple.internal.Strings.LINE_SEPARATOR + "Those properties need to be defined in a property file given by -- properties argument.";
            error += joptsimple.internal.Strings.LINE_SEPARATOR;
            error += joptsimple.internal.Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s","locationInfoConfig.countryNamesFile","Country name matching file.");
            error += joptsimple.internal.Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s","locationInfoConfig.countryCentrePointsFile","Contry centres file");
            error +=  joptsimple.internal.Strings.LINE_SEPARATOR + joptsimple.internal.Strings
                .repeat('*',128);
            log.error( error);
            throw new RuntimeException(error);
          }
        }

        if(!Strings.isNullOrEmpty(lr.getStateProvince())){
          try {
            if (StateCentrePoints.getInstance(alaConfig.getLocationInfoConfig().getStateProvinceCentrePointsFile())
                .coordinatesMatchCentre(lr.getStateProvince(), lr.getDecimalLatitude(),
                    lr.getDecimalLongitude())) {
              addIssue(lr, ALAOccurrenceIssue.COORDINATES_CENTRE_OF_STATEPROVINCE.name());
            } else {
              log.debug("{},{} is not the centre of {}!", lr.getDecimalLatitude(),
                  lr.getDecimalLongitude(), lr.getStateProvince());
            }

            if (!StateProvince.getInstance(alaConfig.getLocationInfoConfig().getStateProvinceNamesFile()).matched(lr.getStateProvince())) {
              addIssue(lr, ALAOccurrenceIssue.STATE_COORDINATE_MISMATCH.name());
            }
          }catch(IOException fnfe){
            String error = "FATAL：" + fnfe.getMessage();
            error = joptsimple.internal.Strings.LINE_SEPARATOR + joptsimple.internal.Strings
                .repeat('*',128) + joptsimple.internal.Strings.LINE_SEPARATOR + error + joptsimple.internal.Strings.LINE_SEPARATOR ;
            error += joptsimple.internal.Strings.LINE_SEPARATOR + "The following properties are mandatory in the pipelines.yaml for location interpretation:";
            error += joptsimple.internal.Strings.LINE_SEPARATOR + "Those properties need to be defined in a property file given by -- properties argument.";
            error += joptsimple.internal.Strings.LINE_SEPARATOR;
            error += joptsimple.internal.Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s","locationInfoConfig.stateProvinceNamesFile","Country name matching file.");
            error += joptsimple.internal.Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s","locationInfoConfig.stateProvinceCentrePointsFile","Contry centres file");
            error +=  joptsimple.internal.Strings.LINE_SEPARATOR + joptsimple.internal.Strings
                .repeat('*',128);
            log.error( error);
            throw new RuntimeException(error);
          }
        }
      }
    };
  };


  /**
   * @param er
   * @param lr
   */
  public static void interpretGeoreferencedDate(ExtendedRecord er, LocationRecord lr) {
    if (hasValue(er, DwcTerm.georeferencedDate)) {
      LocalDate upperBound = LocalDate.now().plusDays(1);
      Range<LocalDate> validRecordedDateRange = Range
          .closed(ALATemporalInterpreter.MIN_LOCAL_DATE, upperBound);
      //GBIF TemporalInterpreter only accept OccurentIssue
      //Convert GBIF IDENTIFIED_DATE_UNLIKELY to ALA GEOREFERENCED_DATE_UNLIKELY
      OccurrenceParseResult<TemporalAccessor> parsed =
          TemporalInterpreter.interpretLocalDate(extractValue(er, DwcTerm.georeferencedDate),
              validRecordedDateRange, OccurrenceIssue.IDENTIFIED_DATE_UNLIKELY);
      if (parsed.isSuccessful()) {
        Optional
            .ofNullable(TemporalAccessorUtils.toEarliestLocalDateTime(parsed.getPayload(), false))
            .map(LocalDateTime::toString)
            .ifPresent(lr::setGeoreferencedDate);
      }

      if (parsed.getIssues().contains(OccurrenceIssue.IDENTIFIED_DATE_UNLIKELY)) {
        addIssue(lr, ALAOccurrenceIssue.GEOREFERENCED_DATE_UNLIKELY.name());
      }
    } else {
      addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERENCE_DATE.name());
    }
  }


/*
    Only Checking if Geodetic related fields are missing
    It does not interpret and assign value to LocationRecord
    GEODETIC_DATUM_ASSUMED_WGS84
    MISSING_GEODETICDATUM
    MISSING_GEOREFERENCE_DATE
    MISSING_GEOREFERENCEPROTOCOL
    MISSING_GEOREFERENCESOURCES
    MISSING_GEOREFERENCEVERIFICATIONSTATUS
*/

  public static void interpretGeoreferenceTerms(ExtendedRecord er, LocationRecord lr) {

    //check for missing georeferencedBy
    if (Strings.isNullOrEmpty(extractNullAwareValue(er, DwcTerm.georeferencedBy))) {
      addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERENCEDBY.name());
    }

    //check for missing georeferencedProtocol
    if (Strings.isNullOrEmpty(extractNullAwareValue(er, DwcTerm.georeferenceProtocol))) {
      addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERENCEPROTOCOL.name());
    }
    //check for missing georeferenceSources
    if (Strings.isNullOrEmpty(extractNullAwareValue(er, DwcTerm.georeferenceSources))) {
      addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERENCESOURCES.name());
    }
    //check for missing georeferenceVerificationStatus
    if (Strings
        .isNullOrEmpty(extractNullAwareValue(er, DwcTerm.georeferenceVerificationStatus))) {
      addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERENCEVERIFICATIONSTATUS.name());
    }
  }

  public static void interpretCoordinateUncertaintyInMeters(ExtendedRecord er, LocationRecord lr) {
    String uncertaintyValue = extractNullAwareValue(er, DwcTerm.coordinateUncertaintyInMeters);
    String precisionValue = extractNullAwareValue(er, DwcTerm.coordinatePrecision);
    //If uncertainty NOT exists
    if (Strings.isNullOrEmpty(uncertaintyValue)) {
      addIssue(lr, OccurrenceIssue.COORDINATE_UNCERTAINTY_METERS_INVALID.name());
      // And if precision exists, > 1
      // We need to check if uncertainty is misplaced to precision
      if (!Strings.isNullOrEmpty(precisionValue)) {
        try {
          //convert possible uom to meters
          double possiblePrecision = DistanceRangeParser.parse(precisionValue);
          if (possiblePrecision > 1){
            lr.setCoordinateUncertaintyInMeters(possiblePrecision);
            addIssue(lr, ALAOccurrenceIssue.UNCERTAINTY_IN_PRECISION.name());
          }
        }catch(Exception e){
          //Ignore precision/uncertainty process
        }
      }
    }else {
      //Uncertainty available
      try {
        lr.setCoordinateUncertaintyInMeters(DistanceRangeParser.parse(uncertaintyValue));
      } catch (Exception e) {
        addIssue(lr, OccurrenceIssue.COORDINATE_UNCERTAINTY_METERS_INVALID.name());
      }
    }
  }


  /**
   * Todo Trailing 0
   * @param er
   * @param lr
   * @return
   */
  private static boolean checkPrecision(ExtendedRecord er, LocationRecord lr){
    int precisionDecimal = lengthOfDecimal(lr.getCoordinatePrecision());
    int latDecimal = lengthOfDecimal(lr.getDecimalLatitude());
    int lngDecimal = lengthOfDecimal(lr.getDecimalLongitude());

    if (latDecimal!=precisionDecimal || lngDecimal != precisionDecimal)
      return false;
    else
      return true;
  }

  private static int lengthOfDecimal(double d){
    String text = Double.toString(Math.abs(d));
    int integerPlaces = text.indexOf('.');
    if(integerPlaces == -1)
      return 0;
    else
      return text.length() - integerPlaces - 1;
  }


}

