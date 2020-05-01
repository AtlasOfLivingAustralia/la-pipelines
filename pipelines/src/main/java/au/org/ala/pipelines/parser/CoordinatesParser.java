package au.org.ala.pipelines.parser;

import au.org.ala.pipelines.vocabulary.ALAOccurrenceIssue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gbif.api.vocabulary.OccurrenceIssue;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.parsers.parsers.common.ParsedField;
import org.gbif.pipelines.parsers.parsers.location.parser.CoordinateParseUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import static org.gbif.pipelines.parsers.utils.ModelUtils.extractValue;

/**
 * Copy from  org.gbif.pipelines.parsers.parsers.location.parser.CoordinateParser -
 *  - cannot be used by outside package, since it is defined as private
 *
 *  New added:
 *  LOCATION_NOT_SUPPLIED assertion
 *
 *
 * Parser for the Dwc Terms related to the coordinates.
 * */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CoordinatesParser {

    // parses decimal latitude and longitude fields
    private static final Function<ExtendedRecord, ParsedField<LatLng>> DECIMAL_LAT_LNG_FN =
            (er ->
                    CoordinateParseUtils.parseLatLng(
                            extractValue(er, DwcTerm.decimalLatitude),
                            extractValue(er, DwcTerm.decimalLongitude)));

    // parses verbatim latitude and longitude fields
    private static final Function<ExtendedRecord, ParsedField<LatLng>> VERBATIM_LAT_LNG_FN =
            (er ->
                    CoordinateParseUtils.parseLatLng(
                            extractValue(er, DwcTerm.verbatimLatitude),
                            extractValue(er, DwcTerm.verbatimLongitude)));

    // parses verbatim coordinates fields
    private static final Function<ExtendedRecord, ParsedField<LatLng>> VERBATIM_COORDS_FN =
            (er ->
                    CoordinateParseUtils.parseVerbatimCoordinates(
                            extractValue(er, DwcTerm.verbatimCoordinates)));

    // list with all the parsing functions
    private static final List<Function<ExtendedRecord, ParsedField<LatLng>>> PARSING_FUNCTIONS =
            Arrays.asList(DECIMAL_LAT_LNG_FN, VERBATIM_LAT_LNG_FN, VERBATIM_COORDS_FN);

    /**
     * Parses the coordinates fields of a {@link ExtendedRecord}.
     *
     * <p>It tries with these fields, in this order, and returns the first successful one:
     *
     * <ol>
     * <li>{@link DwcTerm#decimalLatitude} and {@link DwcTerm#decimalLongitude}
     * <li>{@link DwcTerm#verbatimLatitude} and {@link DwcTerm#verbatimLongitude}
     * <li>{@link DwcTerm#verbatimCoordinates}
     * </ol>
     *
     * @param extendedRecord {@link ExtendedRecord} with the fields to parse.
     * @return {@link ParsedField<LatLng>} for the coordinates parsed.
     */
    public static ParsedField<LatLng> parseCoords(ExtendedRecord extendedRecord) {
        Set<String> issues = new TreeSet<>();
        for (Function<ExtendedRecord, ParsedField<LatLng>> parsingFunction : PARSING_FUNCTIONS) {
            ParsedField<LatLng> result = parsingFunction.apply(extendedRecord);
            if (result.isSuccessful()) {
                // return the first successful result
                return result;
            }
            issues.addAll(result.getIssues());
        }
        issues.add(ALAOccurrenceIssue.LOCATION_NOT_SUPPLIED.name());
        return ParsedField.fail(issues);
    }
}
