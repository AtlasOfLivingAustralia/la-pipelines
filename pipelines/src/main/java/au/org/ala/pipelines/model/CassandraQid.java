package au.org.ala.pipelines.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.io.Serializable;

@Table(keyspace = "occ", name = "qid",
        readConsistency = "ONE")
public class CassandraQid implements Serializable {
    @PartitionKey
    @Column(name = "rowkey", caseSensitive = true)
    private String rowkey;
    @Column(name = "bbox", caseSensitive = true)
    private String bbox;
    @Column(name = "displayString", caseSensitive = true)
    private String displayString;
    @Column(name = "fqs", caseSensitive = true)
    private String fqs;
    @Column(name = "lastUse", caseSensitive = true)
    private String lastUse;
    @Column(name = "maxAge", caseSensitive = true)
    private String maxAge;
    @Column(name = "q", caseSensitive = true)
    private String q;
    @Column(name = "source", caseSensitive = true)
    private String source;
    @Column(name = "wkt", caseSensitive = true)
    private String wkt;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    public String getDisplayString() {
        return displayString;
    }

    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

    public String getFqs() {
        return fqs;
    }

    public void setFqs(String fqs) {
        this.fqs = fqs;
    }

    public String getLastUse() {
        return lastUse;
    }

    public void setLastUse(String lastUse) {
        this.lastUse = lastUse;
    }

    public String getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }
}
