package au.org.ala.pipelines.common;

import org.gbif.pipelines.common.PipelinesVariables;

public enum ALARecordTypes implements PipelinesVariables.Pipeline.Interpretation.InterpretationType {
    ALL,
    ALA_TAXONOMY;
    ALARecordTypes() { }
    @Override
    public String all() {
        return ALL.name();
    }
}
