package au.org.ala.pipelines.interpreters;

import java.util.Collection;

import java.util.Optional;
import java.util.Set;

import org.gbif.dwc.terms.DwcTerm;

import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.LocationRecord;

import org.gbif.pipelines.parsers.parsers.common.ParsedField;
import org.gbif.pipelines.parsers.parsers.location.parser.CoordinateParseUtils;


import org.gbif.rest.client.geocode.Location;

import static org.gbif.pipelines.parsers.utils.ModelUtils.addIssue;
import static org.gbif.pipelines.parsers.utils.ModelUtils.extractValue;

import au.org.ala.pipelines.parser.CoordinatesParser;

public class ALALocationInterpreter {

    public static void interpretStateProvince(org.gbif.pipelines.parsers.parsers.location.GeocodeService service, ExtendedRecord er, LocationRecord lr){

        ParsedField<LatLng> parsedLatLon = CoordinatesParser.parseCoords(er);

        if (parsedLatLon.isSuccessful()) {
            // coords parsing failed
            org.gbif.kvs.geocode.LatLng latlng = parsedLatLon.getResult();
            Collection<Location> locations = service.get(latlng).getLocations();

            Optional<Location> state = locations.stream().filter(location->location.getType()=="State").findFirst();
            if(state.isPresent()){
                lr.setStateProvince(state.get().getCountryName());
            }
        }

        Set<String> issues = parsedLatLon.getIssues();
        addIssue(lr, issues);
    }


    public static void main(String[] args){

    }
}

