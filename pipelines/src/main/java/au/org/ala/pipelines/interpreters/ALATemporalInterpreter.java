package au.org.ala.pipelines.interpreters;

import au.org.ala.pipelines.vocabulary.ALAOccurrenceIssue;
import com.google.common.collect.Range;
import org.apache.commons.lang3.StringUtils;
import org.gbif.api.vocabulary.OccurrenceIssue;
import org.gbif.common.parsers.core.OccurrenceParseResult;
import org.gbif.common.parsers.core.ParseResult;
import org.gbif.common.parsers.date.DateParsers;
import org.gbif.common.parsers.date.TemporalAccessorUtils;
import org.gbif.common.parsers.date.TemporalParser;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.pipelines.core.interpreters.core.TemporalInterpreter;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.TemporalRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import static org.gbif.pipelines.core.interpreters.core.TemporalInterpreter.interpretLocalDate;
import static org.gbif.pipelines.parsers.utils.ModelUtils.*;

public class ALATemporalInterpreter{
    protected static final LocalDate MIN_LOCAL_DATE = LocalDate.of(1600, 1, 1);


    public static void interpretTemporal(ExtendedRecord er, TemporalRecord tr) {
        TemporalInterpreter.interpretTemporal(er,tr);
    }

    /**
     * Code copied from GBIF.  Add an extra assertion
     *
     * Raise Missing_COLLECTION_DATE ASSERTION
     * @param er
     * @param tr
     */
    public static void checkNullRecordDate(ExtendedRecord er, TemporalRecord tr) {
        final String year = extractValue(er, DwcTerm.year);
        final String month = extractValue(er, DwcTerm.month);
        final String day = extractValue(er, DwcTerm.day);
        final String dateString = extractValue(er, DwcTerm.eventDate);
        boolean atomizedDateProvided = StringUtils.isNotBlank(year) || StringUtils.isNotBlank(month)
                || StringUtils.isNotBlank(day);
        boolean dateStringProvided = StringUtils.isNotBlank(dateString);

        if (!atomizedDateProvided && !dateStringProvided) {
            addIssue(tr, ALAOccurrenceIssue.MISSING_COLLECTION_DATE.name());
        }

    }



    /**
     * All verification process require TemporalInterpreter.interpretTemporal has been called.
     * @param tr
     */
    public static void verifyDateIdentified(TemporalRecord tr) {
        if (tr.getEventDate() != null && tr.getDateIdentified() != null){
            TemporalParser TEXTDATE_PARSER = DateParsers.defaultTemporalParser();
            ParseResult<TemporalAccessor> parsedIdentifiedResult = TEXTDATE_PARSER.parse(tr.getDateIdentified());
            ParseResult<TemporalAccessor> parsedEventDateResult = TEXTDATE_PARSER.parse(tr.getEventDate().getGte());

            if (parsedEventDateResult.isSuccessful() && parsedIdentifiedResult.isSuccessful()){
                if (TemporalAccessorUtils.toDate(parsedEventDateResult.getPayload()).after(TemporalAccessorUtils.toDate(parsedIdentifiedResult.getPayload())))
                    addIssue(tr, ALAOccurrenceIssue.ID_PRE_OCCURRENCE.name());
            }
        }
    }

    /**
     * All verification process require TemporalInterpreter.interpretTemporal has been called.
     * @param tr
     */
/*    public static void verifyDateGeoreferenced(TemporalRecord tr) {
        if (tr.getEventDate() != null && tr.getDateIdentified() != null){
            TemporalParser TEXTDATE_PARSER = DateParsers.defaultTemporalParser();
            ParseResult<TemporalAccessor> parsedGeoreferencedResult = TEXTDATE_PARSER.parse(tr.getGeoreferencedDate());
            ParseResult<TemporalAccessor> parsedEventDateResult = TEXTDATE_PARSER.parse(tr.getEventDate().getGte());

            if (parsedEventDateResult.isSuccessful() && parsedGeoreferencedResult.isSuccessful()){
                if (TemporalAccessorUtils.toDate(parsedEventDateResult.getPayload()).before(TemporalAccessorUtils.toDate(parsedGeoreferencedResult.getPayload())))
                    addIssue(tr, ALAOccurrenceIssue.GEOREFERENCE_POST_OCCURRENCE.name());
            }
        }
    }*/



}
