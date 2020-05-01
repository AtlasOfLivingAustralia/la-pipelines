package au.org.ala.pipelines.interpreters;

import java.util.Collection;

import java.util.Optional;
import java.util.Set;

import au.org.ala.pipelines.vocabulary.ALAOccurrenceIssue;
import au.org.ala.pipelines.vocabulary.StateCentrePoints;
import org.gbif.dwc.terms.DwcTerm;

import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.LocationRecord;

import org.gbif.pipelines.parsers.parsers.common.ParsedField;
import org.gbif.pipelines.parsers.parsers.location.parser.CoordinateParseUtils;


import org.gbif.rest.client.geocode.GeocodeResponse;
import org.gbif.rest.client.geocode.Location;

import static org.gbif.pipelines.parsers.utils.ModelUtils.addIssue;
import static org.gbif.pipelines.parsers.utils.ModelUtils.extractValue;
import lombok.extern.slf4j.Slf4j;

import au.org.ala.pipelines.parser.CoordinatesParser;

@Slf4j
public class ALALocationInterpreter {

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


    public static void main(String[] args){

    }
}

