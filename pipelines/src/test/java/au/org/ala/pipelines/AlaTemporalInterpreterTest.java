package au.org.ala.pipelines;

import au.org.ala.pipelines.interpreters.ALATemporalInterpreter;
import au.org.ala.pipelines.vocabulary.ALAOccurrenceIssue;
import org.gbif.api.vocabulary.OccurrenceIssue;
import org.gbif.common.parsers.core.OccurrenceParseResult;

import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.pipelines.core.interpreters.core.TemporalInterpreter;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.TemporalRecord;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AlaTemporalInterpreterTest {
    @Test
    public void testAllNulls() {
        Map<String, String> map = new HashMap<>();
        map.put(DwcTerm.year.qualifiedName(), "");
        map.put(DwcTerm.month.qualifiedName(), " "); //keep the space at the end
        map.put(DwcTerm.day.qualifiedName(), "");
        map.put(DwcTerm.eventDate.qualifiedName(), "");
        ExtendedRecord er = ExtendedRecord.newBuilder().setId("1").setCoreTerms(map).build();
        TemporalRecord tr = TemporalRecord.newBuilder().setId("1").build();

        ALATemporalInterpreter.checkNullRecordDate(er, tr);
        assertArrayEquals(tr.getIssues().getIssueList().toArray(),new String[]{ALAOccurrenceIssue.MISSING_COLLECTION_DATE.name()});
    }

    @Test
    public void testIdentifiedDate() {
        Map<String, String> map = new HashMap<>();
        map.put(DwcTerm.year.qualifiedName(), "");
        map.put(DwcTerm.month.qualifiedName(), " "); //keep the space at the end
        map.put(DwcTerm.day.qualifiedName(), "");
        map.put(DwcTerm.eventDate.qualifiedName(), "1980-1-1");
        map.put(DwcTerm.dateIdentified.qualifiedName(), "1979-1-1");
        ExtendedRecord er = ExtendedRecord.newBuilder().setId("1").setCoreTerms(map).build();
        TemporalRecord tr = TemporalRecord.newBuilder().setId("1").build();

        TemporalInterpreter.interpretTemporal(er,tr);
        ALATemporalInterpreter.verifyDateIdentified(tr);

        assertArrayEquals(tr.getIssues().getIssueList().toArray(),new String[]{ALAOccurrenceIssue.ID_PRE_OCCURRENCE.name()});
    }

/*    @Test
    public void testALASpec() {
        Map<String, String> map = new HashMap<>();
        map.put(DwcTerm.year.qualifiedName(), "");
        map.put(DwcTerm.month.qualifiedName(), " "); //keep the space at the end
        map.put(DwcTerm.day.qualifiedName(), "");
        map.put(DwcTerm.eventDate.qualifiedName(), "1980-1-1");
        map.put(DwcTerm.georeferencedDate.qualifiedName(), "1979-1-1");
        ExtendedRecord er = ExtendedRecord.newBuilder().setId("1").setCoreTerms(map).build();
        TemporalRecord tr = TemporalRecord.newBuilder().setId("1").build();


        ALATemporalInterpreter.interpretGeoreferenceDate(er,tr);

        assertEquals(tr.getGeoreferencedDate(), "1979-1-1T00:00");
    }*/

/*    @Test
    public void testBadDay() {
        OccurrenceParseResult<TemporalAccessor> result = interpretRecordedDate("1984", "31", "3", null);
        assertEquals(result, OccurrenceIssue.RECORDED_DATE_INVALID);
    }*/

    private OccurrenceParseResult<TemporalAccessor> interpretRecordedDate(String y, String m, String d, String date) {
        Map<String, String> map = new HashMap<>();
        map.put(DwcTerm.year.qualifiedName(), y);
        map.put(DwcTerm.month.qualifiedName(), m);
        map.put(DwcTerm.day.qualifiedName(), d);
        map.put(DwcTerm.eventDate.qualifiedName(), date);
        ExtendedRecord er = ExtendedRecord.newBuilder().setId("1").setCoreTerms(map).build();

        return TemporalInterpreter.interpretRecordedDate(er);
    }
}