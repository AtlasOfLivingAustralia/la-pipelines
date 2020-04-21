package au.org.ala.kvs.client;

import java.io.Closeable;

public interface ALANameMatchService extends Closeable {

    /**
     * Fuzzy matches scientific names against the GBIF Backbone Taxonomy with the optional classification provided.
     * If a classification is provided and strict is not set to true, the default matching will also try to match against
     * these if no direct match is found for the name parameter alone.
     *
     * @return a possible null name match
     */
    ALANameUsageMatch match(ALASpeciesMatchRequest key);
}
