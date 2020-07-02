package au.org.ala.pipelines.vocabulary;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StateProvinceVocabTest {

    @Test
    public void testStateProvince() {
        assertEquals(Optional.of("Australian Capital Territory"), StateProvince.matchTerm("ACT"));
    }
}
