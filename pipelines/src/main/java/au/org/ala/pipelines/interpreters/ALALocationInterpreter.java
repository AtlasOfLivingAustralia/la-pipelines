package au.org.ala.pipelines.interpreters;

import java.util.Collection;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import au.org.ala.pipelines.vocabulary.*;
import com.google.common.base.Strings;
import org.gbif.api.vocabulary.OccurrenceIssue;
import org.gbif.common.parsers.core.ParseResult;
import org.gbif.common.parsers.geospatial.MeterRangeParser;
import org.gbif.dwc.terms.DwcTerm;

import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.core.Interpretation;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.LocationRecord;

import org.gbif.pipelines.io.avro.MetadataRecord;
import org.gbif.pipelines.parsers.parsers.SimpleTypeParser;
import org.gbif.pipelines.parsers.parsers.common.ParsedField;
import org.gbif.pipelines.parsers.parsers.location.GeocodeService;
import org.gbif.pipelines.parsers.parsers.location.parser.CoordinateParseUtils;


import org.gbif.rest.client.geocode.GeocodeResponse;
import org.gbif.rest.client.geocode.Location;

import static org.gbif.api.vocabulary.OccurrenceIssue.COORDINATE_PRECISION_INVALID;
import static org.gbif.api.vocabulary.OccurrenceIssue.COORDINATE_UNCERTAINTY_METERS_INVALID;
import static org.gbif.pipelines.parsers.utils.ModelUtils.addIssue;
import static org.gbif.pipelines.parsers.utils.ModelUtils.extractNullAwareValue;

import lombok.extern.slf4j.Slf4j;

import au.org.ala.pipelines.parser.CoordinatesParser;
import org.gbif.pipelines.core.interpreters.core.LocationInterpreter;

@Slf4j
public class ALALocationInterpreter {

    //Copied from LocationInterpreter
    // COORDINATE_UNCERTAINTY_METERS bounds are exclusive bounds
    private static final double COORDINATE_UNCERTAINTY_METERS_LOWER_BOUND = 0d;
    // 5000 km seems safe
    private static final double COORDINATE_UNCERTAINTY_METERS_UPPER_BOUND = 5_000_000d;
    private static final double COORDINATE_PRECISION_LOWER_BOUND = 0d;
    // 45 close to 5000 km
    private static final double COORDINATE_PRECISION_UPPER_BOUND = 45d;


    /**
     *
     * @param service Provided by ALA coutry/state SHP file
     * @param er
     * @param lr
     */
    public static void interpretStateProvince(org.gbif.pipelines.parsers.parsers.location.GeocodeService service, ExtendedRecord er, LocationRecord lr){

        ParsedField<LatLng> parsedLatLon = CoordinatesParser.parseCoords(er);

        if (parsedLatLon.isSuccessful()) {
            // coords parsing failed
            org.gbif.kvs.geocode.LatLng latlng = parsedLatLon.getResult();

            GeocodeResponse gr = service.get(latlng);
            if(gr != null){
                Collection<Location> locations = gr.getLocations();

                Optional<Location> state = locations.stream().filter(location->location.getType().equalsIgnoreCase("State")).findFirst();
                if(state.isPresent()){
                    lr.setStateProvince(state.get().getCountryName());
                    //Check centre of State
                    if(StateCentrePoints.coordinatesMatchCentre(lr.getStateProvince(), latlng.getLatitude(),latlng.getLongitude()))
                        addIssue(lr, ALAOccurrenceIssue.COORDINATES_CENTRE_OF_STATEPROVINCE.name());
                }
            }else{
                log.warn( "No state is found on this cooridnate: " + parsedLatLon.getResult().getLatitude() +' ' + parsedLatLon.getResult().getLongitude());
            }
        }
        Set<String> issues = parsedLatLon.getIssues();
        addIssue(lr, issues);
    }

    /**
     * Extended from GBIF interpretCountryAndCoordinates
     *
     * Add centre of country check
     *
     * @param service GBIF country related service
     * @param mdr
     * @return
     */
    public static BiConsumer<ExtendedRecord, LocationRecord> interpretCountryAndCoordinates(
            GeocodeService service, MetadataRecord mdr){
        return (er, lr) -> {
            LocationInterpreter.interpretCountryAndCoordinates(service,mdr).accept(er, lr);
            //check country name
            if(lr.getCountry() !=null && lr.getHasCoordinate() ){
                if(CountryCentrePoints.coordinatesMatchCentre(lr.getCountry(),lr.getDecimalLatitude(),lr.getDecimalLongitude()))
                    addIssue(lr, ALAOccurrenceIssue.COORDINATES_CENTRE_OF_COUNTRY.name());
            }

        };
    }

/*
    Checking missing Geodetic fields
            GEODETIC_DATUM_ASSUMED_WGS84
            MISSING_GEODETICDATUM
            MISSING_GEOREFERENCE_DATE
            MISSING_GEOREFERENCEPROTOCOL
            MISSING_GEOREFERENCESOURCES
            MISSING_GEOREFERENCEVERIFICATIONSTATUS
*/

    public static void checkGeodetic(LocationRecord lr, ExtendedRecord er){
        //check for missing geodeticDatum
        if ( Strings.isNullOrEmpty(extractNullAwareValue(er, DwcTerm.geodeticDatum)))
             addIssue(lr, ALAOccurrenceIssue.MISSING_GEODETICDATUM.name());

        //check for missing georeferencedBy
        if( Strings.isNullOrEmpty(extractNullAwareValue(er, DwcTerm.georeferencedBy)))
            addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERNCEDBY.name());

        //check for missing georeferencedProtocol
        if ( Strings.isNullOrEmpty(extractNullAwareValue(er, DwcTerm.georeferenceProtocol)))
            addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERENCEPROTOCOL.name());
        //check for missing georeferenceSources
        if ( Strings.isNullOrEmpty(extractNullAwareValue(er, DwcTerm.georeferenceSources)))
            addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERENCESOURCES.name());

        if ( Strings.isNullOrEmpty(extractNullAwareValue(er, DwcTerm.georeferenceVerificationStatus)))
            addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERENCEVERIFICATIONSTATUS.name());
        //check for missing georeferenceVerificationStatus

        //check for missing georeferenceDate
        if ( Strings.isNullOrEmpty(extractNullAwareValue(er, DwcTerm.georeferencedDate)))
            addIssue(lr, ALAOccurrenceIssue.MISSING_GEOREFERENCE_DATE.name());

    }

    /**
     * Check if country name matches inlist
     * @param lr
     */
    public static void checkForCountryMismatch(LocationRecord lr){
       if(lr.getCountry() != null)
            if(!CountryMatch.matched(lr.getCountry()))
                addIssue(lr, ALAOccurrenceIssue.UNKNOWN_COUNTRY_NAME.name());

    }


    public static void checkForStateMismatch(LocationRecord lr){

        if(!StateProvince.matchTerm(lr.getStateProvince()))
            addIssue(lr, ALAOccurrenceIssue.STATE_COORDINATE_MISMATCH.name());
   }

    /**
     * Check coordinate uncertainty and precision
     *
     * Prerequisite : interpretCoordinateUncertaintyInMeters and interpretCoordinatePrecision MUST be run.
     * @param lr
     */
    public static void checkCoordinateUncertainty(LocationRecord lr){
            // If uncertainty NOT exists and Precision exits

            if (lr.getCoordinateUncertaintyInMeters() == null){
                addIssue(lr, ALAOccurrenceIssue.UNCERTAINTY_NOT_SPECIFIED.name());
                if(lr.getCoordinatePrecision() !=null){
                    addIssue(lr,  ALAOccurrenceIssue.UNCERTAINTY_IN_PRECISION.name());
                }
            }

            if(lr.getCoordinatePrecision() == null){
                addIssue(lr,  ALAOccurrenceIssue.MISSING_COORDINATEPRECISION.name());
            }else{
                //check coordinates range
                //Unimplemented
            }

    }

    public static void main(String[] args){

    }
}

