package au.org.ala.pipelines.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.apache.beam.sdk.values.KV;

import java.io.Serializable;
import java.util.*;

@Table(keyspace = "occ", name = "occ",
        readConsistency = "ONE")
public class CassandraOccurrence implements Serializable {
    @PartitionKey
    @Column(name = "rowkey")
    private String rowkey;
    @Column(name = "IdentificationQualifierProcessor_qa", caseSensitive = true)
    private String identificationQualifierProcessor_qa;
    @Column(name = "_class", caseSensitive = true)
    private String _class = "";
    @Column(name = "_qa", caseSensitive = true)
    private String _qa;
    @Column(name = "abcdIdentificationQualifier", caseSensitive = true)
    private String abcdIdentificationQualifier;
    @Column(name = "abcdIdentificationQualifierInsertionPoint", caseSensitive = true)
    private String abcdIdentificationQualifierInsertionPoint;
    @Column(name = "abcdIdentificationQualifier_p", caseSensitive = true)
    private String abcdIdentificationQualifier_p;
    @Column(name = "abcdTypeStatus", caseSensitive = true)
    private String abcdTypeStatus;
    @Column(name = "acceptedNameUsage", caseSensitive = true)
    private String acceptedNameUsage;
    @Column(name = "acceptedNameUsageID", caseSensitive = true)
    private String acceptedNameUsageID;
    @Column(name = "accessRights", caseSensitive = true)
    private String accessRights;
    @Column(name = "alaUserId", caseSensitive = true)
    private String alaUserId;
    @Column(name = "associatedMedia", caseSensitive = true)
    private String associatedMedia;
    @Column(name = "associatedOccurrences", caseSensitive = true)
    private String associatedOccurrences;
    @Column(name = "associatedOccurrences_p", caseSensitive = true)
    private String associatedOccurrences_p;
    @Column(name = "associatedReferences", caseSensitive = true)
    private String associatedReferences;
    @Column(name = "associatedReferences_p", caseSensitive = true)
    private String associatedReferences_p;
    @Column(name = "associatedSequences", caseSensitive = true)
    private String associatedSequences;
    @Column(name = "associatedTaxa", caseSensitive = true)
    private String associatedTaxa;
    @Column(name = "attr_qa", caseSensitive = true)
    private String attr_qa;
    @Column(name = "austConservation_p", caseSensitive = true)
    private String austConservation_p;
    @Column(name = "australianHerbariumRegion", caseSensitive = true)
    private String australianHerbariumRegion;
    @Column(name = "basisOfRecord", caseSensitive = true)
    private String basisOfRecord;
    @Column(name = "basisOfRecord_p", caseSensitive = true)
    private String basisOfRecord_p;
    @Column(name = "bbox", caseSensitive = true)
    private String bbox;
    @Column(name = "bbox_p", caseSensitive = true)
    private String bbox_p;
    @Column(name = "behavior", caseSensitive = true)
    private String behavior;
    @Column(name = "bibliographicCitation", caseSensitive = true)
    private String bibliographicCitation;
    @Column(name = "biome", caseSensitive = true)
    private String biome;
    @Column(name = "biome_p", caseSensitive = true)
    private String biome_p;
    @Column(name = "bor_qa", caseSensitive = true)
    private String bor_qa;
    @Column(name = "catalogNumber", caseSensitive = true)
    private String catalogNumber;
    @Column(name = "citation", caseSensitive = true)
    private String citation;
    @Column(name = "cl_p", caseSensitive = true)
    private String cl_p;
    @Column(name = "class", caseSensitive = true)
    private String classField = "";
    @Column(name = "classID", caseSensitive = true)
    private String classID;
    @Column(name = "classID_p", caseSensitive = true)
    private String classID_p;
    @Column(name = "class_p", caseSensitive = true)
    private String class_p;
    @Column(name = "class_qa", caseSensitive = true)
    private String class_qa;
    @Column(name = "classs", caseSensitive = true)
    private String classs = "";
    @Column(name = "classs_p", caseSensitive = true)
    private String classs_p;
    @Column(name = "collectionCode", caseSensitive = true)
    private String collectionCode;
    @Column(name = "collectionCode_p", caseSensitive = true)
    private String collectionCode_p;
    @Column(name = "collectionID", caseSensitive = true)
    private String collectionID;
    @Column(name = "collectionName", caseSensitive = true)
    private String collectionName;
    @Column(name = "collectionName_p", caseSensitive = true)
    private String collectionName_p;
    @Column(name = "collectionUid", caseSensitive = true)
    private String collectionUid;
    @Column(name = "collectionUid_p", caseSensitive = true)
    private String collectionUid_p;
    @Column(name = "collectorFieldNumber", caseSensitive = true)
    private String collectorFieldNumber;
    @Column(name = "continent", caseSensitive = true)
    private String continent;
    @Column(name = "continent_p", caseSensitive = true)
    private String continent_p;
    @Column(name = "coordinatePrecision", caseSensitive = true)
    private String coordinatePrecision;
    @Column(name = "coordinatePrecision_p", caseSensitive = true)
    private String coordinatePrecision_p;
    @Column(name = "coordinateUncertaintyInMeters", caseSensitive = true)
    private String coordinateUncertaintyInMeters;
    @Column(name = "coordinateUncertaintyInMeters_p", caseSensitive = true)
    private String coordinateUncertaintyInMeters_p;
    @Column(name = "country", caseSensitive = true)
    private String country;
    @Column(name = "countryCode", caseSensitive = true)
    private String countryCode;
    @Column(name = "countryCode_p", caseSensitive = true)
    private String countryCode_p;
    @Column(name = "countryConservation", caseSensitive = true)
    private String countryConservation;
    @Column(name = "countryConservation_p", caseSensitive = true)
    private String countryConservation_p;
    @Column(name = "country_p", caseSensitive = true)
    private String country_p;
    @Column(name = "county", caseSensitive = true)
    private String county;
    @Column(name = "cultivarName", caseSensitive = true)
    private String cultivarName;
    @Column(name = "cultivated", caseSensitive = true)
    private String cultivated;
    @Column(name = "dataGeneralizations", caseSensitive = true)
    private String dataGeneralizations;
    @Column(name = "dataGeneralizations_p", caseSensitive = true)
    private String dataGeneralizations_p;
    @Column(name = "dataHubName", caseSensitive = true)
    private String dataHubName;
    @Column(name = "dataHubUid", caseSensitive = true)
    private String dataHubUid;
    @Column(name = "dataHubUid_p", caseSensitive = true)
    private String dataHubUid_p;
    @Column(name = "dataProviderName", caseSensitive = true)
    private String dataProviderName;
    @Column(name = "dataProviderName_p", caseSensitive = true)
    private String dataProviderName_p;
    @Column(name = "dataProviderUid", caseSensitive = true)
    private String dataProviderUid;
    @Column(name = "dataProviderUid_p", caseSensitive = true)
    private String dataProviderUid_p;
    @Column(name = "dataResourceName", caseSensitive = true)
    private String dataResourceName;
    @Column(name = "dataResourceName_p", caseSensitive = true)
    private String dataResourceName_p;
    @Column(name = "dataResourceUid", caseSensitive = true)
    private String dataResourceUid;
    @Column(name = "datasetID", caseSensitive = true)
    private String datasetID;
    @Column(name = "datasetName", caseSensitive = true)
    private String datasetName;
    @Column(name = "dateDeleted", caseSensitive = true)
    private String dateDeleted;
    @Column(name = "dateIdentified", caseSensitive = true)
    private String dateIdentified;
    @Column(name = "dateIdentified_p", caseSensitive = true)
    private String dateIdentified_p;
    @Column(name = "datePrecision", caseSensitive = true)
    private String datePrecision;
    @Column(name = "datePrecision_p", caseSensitive = true)
    private String datePrecision_p;
    @Column(name = "day", caseSensitive = true)
    private String day;
    @Column(name = "day_p", caseSensitive = true)
    private String day_p;
    @Column(name = "decimalLatitude", caseSensitive = true)
    private String decimalLatitude;
    @Column(name = "decimalLatitude_p", caseSensitive = true)
    private String decimalLatitude_p;
    @Column(name = "decimalLatitudelatitude", caseSensitive = true)
    private String decimalLatitudelatitude;
    @Column(name = "decimalLongitude", caseSensitive = true)
    private String decimalLongitude;
    @Column(name = "decimalLongitude_p", caseSensitive = true)
    private String decimalLongitude_p;
    @Column(name = "defaultValuesUsed", caseSensitive = true)
    private String defaultValuesUsed;
    @Column(name = "default_qa", caseSensitive = true)
    private String default_qa;
    @Column(name = "deleted", caseSensitive = true)
    private String deleted;
    @Column(name = "disposition", caseSensitive = true)
    private String disposition;
    @Column(name = "distanceOutsideExpertRange", caseSensitive = true)
    private String distanceOutsideExpertRange;
    @Column(name = "distanceOutsideExpertRange_p", caseSensitive = true)
    private String distanceOutsideExpertRange_p;
    @Column(name = "duplicates", caseSensitive = true)
    private String duplicates;
    @Column(name = "duplicatesOriginalInstitutionID", caseSensitive = true)
    private String duplicatesOriginalInstitutionID;
    @Column(name = "duplicatesOriginalUnitID", caseSensitive = true)
    private String duplicatesOriginalUnitID;
    @Column(name = "duplicates_qa", caseSensitive = true)
    private String duplicates_qa;
    @Column(name = "duplicationStatus", caseSensitive = true)
    private String duplicationStatus;
    @Column(name = "duplicationStatus_p", caseSensitive = true)
    private String duplicationStatus_p;
    @Column(name = "duplicationType", caseSensitive = true)
    private String duplicationType;
    @Column(name = "duplicationType_p", caseSensitive = true)
    private String duplicationType_p;
    @Column(name = "dynamicProperties", caseSensitive = true)
    private String dynamicProperties;
    @Column(name = "easting", caseSensitive = true)
    private String easting;
    @Column(name = "easting_p", caseSensitive = true)
    private String easting_p;
    @Column(name = "el_p", caseSensitive = true)
    private String el_p;
    @Column(name = "endDayOfYear", caseSensitive = true)
    private String endDayOfYear;
    @Column(name = "endYear", caseSensitive = true)
    private String endYear;
    @Column(name = "establishmentMeans", caseSensitive = true)
    private String establishmentMeans;
    @Column(name = "establishmentMeans_p", caseSensitive = true)
    private String establishmentMeans_p;
    @Column(name = "eventAttributes", caseSensitive = true)
    private String eventAttributes;
    @Column(name = "eventDate", caseSensitive = true)
    private String eventDate;
    @Column(name = "eventDateEnd", caseSensitive = true)
    private String eventDateEnd;
    @Column(name = "eventDateEnd_p", caseSensitive = true)
    private String eventDateEnd_p;
    @Column(name = "eventDate_p", caseSensitive = true)
    private String eventDate_p;
    @Column(name = "eventID", caseSensitive = true)
    private String eventID;
    @Column(name = "eventRemarks", caseSensitive = true)
    private String eventRemarks;
    @Column(name = "eventTime", caseSensitive = true)
    private String eventTime;
    @Column(name = "event_qa", caseSensitive = true)
    private String event_qa;
    @Column(name = "family", caseSensitive = true)
    private String family;
    @Column(name = "familyID", caseSensitive = true)
    private String familyID;
    @Column(name = "familyID_p", caseSensitive = true)
    private String familyID_p;
    @Column(name = "family_p", caseSensitive = true)
    private String family_p;
    @Column(name = "fieldNotes", caseSensitive = true)
    private String fieldNotes;
    @Column(name = "fieldNumber", caseSensitive = true)
    private String fieldNumber;
    @Column(name = "firstLoaded", caseSensitive = true)
    private String firstLoaded;
    @Column(name = "footprintSRS", caseSensitive = true)
    private String footprintSRS;
    @Column(name = "footprintSpatialFit", caseSensitive = true)
    private String footprintSpatialFit;
    @Column(name = "footprintWKT", caseSensitive = true)
    private String footprintWKT;
    @Column(name = "generalisationToApplyInMetres", caseSensitive = true)
    private String generalisationToApplyInMetres;
    @Column(name = "generalisedLocality", caseSensitive = true)
    private String generalisedLocality;
    @Column(name = "genus", caseSensitive = true)
    private String genus;
    @Column(name = "genusID", caseSensitive = true)
    private String genusID;
    @Column(name = "genusID_p", caseSensitive = true)
    private String genusID_p;
    @Column(name = "genus_p", caseSensitive = true)
    private String genus_p;
    @Column(name = "geodeticDatum", caseSensitive = true)
    private String geodeticDatum;
    @Column(name = "geodeticDatum_p", caseSensitive = true)
    private String geodeticDatum_p;
    @Column(name = "georeferenceProtocol", caseSensitive = true)
    private String georeferenceProtocol;
    @Column(name = "georeferenceProtocol_p", caseSensitive = true)
    private String georeferenceProtocol_p;
    @Column(name = "georeferenceRemarks", caseSensitive = true)
    private String georeferenceRemarks;
    @Column(name = "georeferenceSources", caseSensitive = true)
    private String georeferenceSources;
    @Column(name = "georeferenceSources_p", caseSensitive = true)
    private String georeferenceSources_p;
    @Column(name = "georeferenceVerificationStatus", caseSensitive = true)
    private String georeferenceVerificationStatus;
    @Column(name = "georeferenceVerificationStatus_p", caseSensitive = true)
    private String georeferenceVerificationStatus_p;
    @Column(name = "georeferencedBy", caseSensitive = true)
    private String georeferencedBy;
    @Column(name = "georeferencedBy_p", caseSensitive = true)
    private String georeferencedBy_p;
    @Column(name = "georeferencedDate", caseSensitive = true)
    private String georeferencedDate;
    @Column(name = "georeferencedDate_p", caseSensitive = true)
    private String georeferencedDate_p;
    @Column(name = "geospatialIssue", caseSensitive = true)
    private String geospatialIssue;
    @Column(name = "geospatiallyKosher", caseSensitive = true)
    private String geospatiallyKosher;
    @Column(name = "globalConservation", caseSensitive = true)
    private String globalConservation;
    @Column(name = "gridReference", caseSensitive = true)
    private String gridReference;
    @Column(name = "habitat", caseSensitive = true)
    private String habitat;
    @Column(name = "habitat_p", caseSensitive = true)
    private String habitat_p;
    @Column(name = "higherClassification", caseSensitive = true)
    private String higherClassification;
    @Column(name = "higherGeography", caseSensitive = true)
    private String higherGeography;
    @Column(name = "higherGeographyID", caseSensitive = true)
    private String higherGeographyID;
    @Column(name = "ibra", caseSensitive = true)
    private String ibra;
    @Column(name = "ibraSubregion", caseSensitive = true)
    private String ibraSubregion;
    @Column(name = "ibra_p", caseSensitive = true)
    private String ibra_p;
    @Column(name = "identificationID", caseSensitive = true)
    private String identificationID;
    @Column(name = "identificationQualifier", caseSensitive = true)
    private String identificationQualifier;
    @Column(name = "identificationQualifier_p", caseSensitive = true)
    private String identificationQualifier_p;
    @Column(name = "identificationReferences", caseSensitive = true)
    private String identificationReferences;
    @Column(name = "identificationReferences_p", caseSensitive = true)
    private String identificationReferences_p;
    @Column(name = "identificationRemarks", caseSensitive = true)
    private String identificationRemarks;
    @Column(name = "identificationVerificationStatus", caseSensitive = true)
    private String identificationVerificationStatus;
    @Column(name = "identificationVerificationStatus_p", caseSensitive = true)
    private String identificationVerificationStatus_p;
    @Column(name = "identification_qa", caseSensitive = true)
    private String identification_qa;
    @Column(name = "identifiedBy", caseSensitive = true)
    private String identifiedBy;
    @Column(name = "identifiedBy_p", caseSensitive = true)
    private String identifiedBy_p;
    @Column(name = "identifier", caseSensitive = true)
    private String identifier;
    @Column(name = "identifierBy", caseSensitive = true)
    private String identifierBy;
    @Column(name = "identifierRole", caseSensitive = true)
    private String identifierRole;
    @Column(name = "image_qa", caseSensitive = true)
    private String image_qa;
    @Column(name = "images", caseSensitive = true)
    private String images;
    @Column(name = "images_p", caseSensitive = true)
    private String images_p;
    @Column(name = "imcra_p", caseSensitive = true)
    private String imcra_p;
    @Column(name = "individualCount", caseSensitive = true)
    private String individualCount;
    @Column(name = "individualID", caseSensitive = true)
    private String individualID;
    @Column(name = "informationWithheld", caseSensitive = true)
    private String informationWithheld;
    @Column(name = "informationWithheld_p", caseSensitive = true)
    private String informationWithheld_p;
    @Column(name = "infraspecificEpithet", caseSensitive = true)
    private String infraspecificEpithet;
    @Column(name = "infraspecificEpithet_p", caseSensitive = true)
    private String infraspecificEpithet_p;
    @Column(name = "infraspecificMarker", caseSensitive = true)
    private String infraspecificMarker;
    @Column(name = "institutionCode", caseSensitive = true)
    private String institutionCode;
    @Column(name = "institutionCode_p", caseSensitive = true)
    private String institutionCode_p;
    @Column(name = "institutionID", caseSensitive = true)
    private String institutionID;
    @Column(name = "institutionID_p", caseSensitive = true)
    private String institutionID_p;
    @Column(name = "institutionName", caseSensitive = true)
    private String institutionName;
    @Column(name = "institutionName_p", caseSensitive = true)
    private String institutionName_p;
    @Column(name = "institutionUid", caseSensitive = true)
    private String institutionUid;
    @Column(name = "institutionUid_p", caseSensitive = true)
    private String institutionUid_p;
    @Column(name = "interactions", caseSensitive = true)
    private String interactions;
    @Column(name = "interactions_p", caseSensitive = true)
    private String interactions_p;
    @Column(name = "island", caseSensitive = true)
    private String island;
    @Column(name = "islandGroup", caseSensitive = true)
    private String islandGroup;
    @Column(name = "kingdom", caseSensitive = true)
    private String kingdom;
    @Column(name = "kingdomID", caseSensitive = true)
    private String kingdomID;
    @Column(name = "kingdomID_p", caseSensitive = true)
    private String kingdomID_p;
    @Column(name = "kingdom_p", caseSensitive = true)
    private String kingdom_p;
    @Column(name = "language", caseSensitive = true)
    private String language;
    @Column(name = "lastModifiedTime", caseSensitive = true)
    private String lastModifiedTime;
    @Column(name = "lastModifiedTime_p", caseSensitive = true)
    private String lastModifiedTime_p;
    @Column(name = "lastUserAssertionDate", caseSensitive = true)
    private String lastUserAssertionDate;
    @Column(name = "left", caseSensitive = true)
    private String left;
    @Column(name = "left_p", caseSensitive = true)
    private String left_p;
    @Column(name = "lga", caseSensitive = true)
    private String lga;
    @Column(name = "lga_p", caseSensitive = true)
    private String lga_p;
    @Column(name = "license", caseSensitive = true)
    private String license;
    @Column(name = "license_p", caseSensitive = true)
    private String license_p;
    @Column(name = "lifeStage", caseSensitive = true)
    private String lifeStage;
    @Column(name = "lifeStage_p", caseSensitive = true)
    private String lifeStage_p;
    @Column(name = "loanDate", caseSensitive = true)
    private String loanDate;
    @Column(name = "loanDestination", caseSensitive = true)
    private String loanDestination;
    @Column(name = "loanForBotanist", caseSensitive = true)
    private String loanForBotanist;
    @Column(name = "loanIdentifier", caseSensitive = true)
    private String loanIdentifier;
    @Column(name = "loanReturnDate", caseSensitive = true)
    private String loanReturnDate;
    @Column(name = "loanSequenceNumber", caseSensitive = true)
    private String loanSequenceNumber;
    @Column(name = "loc_qa", caseSensitive = true)
    private String loc_qa;
    @Column(name = "locality", caseSensitive = true)
    private String locality;
    @Column(name = "locality_p", caseSensitive = true)
    private String locality_p;
    @Column(name = "locationAccordingTo", caseSensitive = true)
    private String locationAccordingTo;
    @Column(name = "locationAttributes", caseSensitive = true)
    private String locationAttributes;
    @Column(name = "locationDetermined", caseSensitive = true)
    private String locationDetermined;
    @Column(name = "locationID", caseSensitive = true)
    private String locationID;
    @Column(name = "locationRemarks", caseSensitive = true)
    private String locationRemarks;
    @Column(name = "maximumDepthInMeters", caseSensitive = true)
    private String maximumDepthInMeters;
    @Column(name = "maximumDepthInMeters_p", caseSensitive = true)
    private String maximumDepthInMeters_p;
    @Column(name = "maximumDistanceAboveSurfaceInMeters", caseSensitive = true)
    private String maximumDistanceAboveSurfaceInMeters;
    @Column(name = "maximumElevationInMeters", caseSensitive = true)
    private String maximumElevationInMeters;
    @Column(name = "maximumElevationInMeters_p", caseSensitive = true)
    private String maximumElevationInMeters_p;
    @Column(name = "measurementAccuracy", caseSensitive = true)
    private String measurementAccuracy;
    @Column(name = "measurementDeterminedBy", caseSensitive = true)
    private String measurementDeterminedBy;
    @Column(name = "measurementDeterminedDate", caseSensitive = true)
    private String measurementDeterminedDate;
    @Column(name = "measurementID", caseSensitive = true)
    private String measurementID;
    @Column(name = "measurementMethod", caseSensitive = true)
    private String measurementMethod;
    @Column(name = "measurementRemarks", caseSensitive = true)
    private String measurementRemarks;
    @Column(name = "measurementType", caseSensitive = true)
    private String measurementType;
    @Column(name = "measurementUnit", caseSensitive = true)
    private String measurementUnit;
    @Column(name = "measurementValue", caseSensitive = true)
    private String measurementValue;
    @Column(name = "minimumDepthInMeters", caseSensitive = true)
    private String minimumDepthInMeters;
    @Column(name = "minimumDepthInMeters_p", caseSensitive = true)
    private String minimumDepthInMeters_p;
    @Column(name = "minimumDistanceAboveSurfaceInMeters", caseSensitive = true)
    private String minimumDistanceAboveSurfaceInMeters;
    @Column(name = "minimumElevationInMeters", caseSensitive = true)
    private String minimumElevationInMeters;
    @Column(name = "minimumElevationInMeters_p", caseSensitive = true)
    private String minimumElevationInMeters_p;
    @Column(name = "miscProperties", caseSensitive = true)
    private String miscProperties;
    @Column(name = "modified", caseSensitive = true)
    private String modified;
    @Column(name = "modified_p", caseSensitive = true)
    private String modified_p;
    @Column(name = "month", caseSensitive = true)
    private String month;
    @Column(name = "month_p", caseSensitive = true)
    private String month_p;
    @Column(name = "municipality", caseSensitive = true)
    private String municipality;
    @Column(name = "mytest", caseSensitive = true)
    private String mytest;
    @Column(name = "nameAccordingTo", caseSensitive = true)
    private String nameAccordingTo;
    @Column(name = "nameAccordingToID", caseSensitive = true)
    private String nameAccordingToID;
    @Column(name = "nameAccordingTo_p", caseSensitive = true)
    private String nameAccordingTo_p;
    @Column(name = "nameMatchMetric", caseSensitive = true)
    private String nameMatchMetric;
    @Column(name = "nameMatchMetric_p", caseSensitive = true)
    private String nameMatchMetric_p;
    @Column(name = "nameParseType", caseSensitive = true)
    private String nameParseType;
    @Column(name = "nameParseType_p", caseSensitive = true)
    private String nameParseType_p;
    @Column(name = "namePublishedIn", caseSensitive = true)
    private String namePublishedIn;
    @Column(name = "namePublishedInID", caseSensitive = true)
    private String namePublishedInID;
    @Column(name = "namePublishedInYear", caseSensitive = true)
    private String namePublishedInYear;
    @Column(name = "naturalOccurrence", caseSensitive = true)
    private String naturalOccurrence;
    @Column(name = "nearNamedPlaceRelationTo", caseSensitive = true)
    private String nearNamedPlaceRelationTo;
    @Column(name = "nomenclaturalCode", caseSensitive = true)
    private String nomenclaturalCode;
    @Column(name = "nomenclaturalCode_p", caseSensitive = true)
    private String nomenclaturalCode_p;
    @Column(name = "nomenclaturalStatus", caseSensitive = true)
    private String nomenclaturalStatus;
    @Column(name = "northing", caseSensitive = true)
    private String northing;
    @Column(name = "northing_p", caseSensitive = true)
    private String northing_p;
    @Column(name = "occurrenceAttributes", caseSensitive = true)
    private String occurrenceAttributes;
    @Column(name = "occurrenceDetails", caseSensitive = true)
    private String occurrenceDetails;
    @Column(name = "occurrenceID", caseSensitive = true)
    private String occurrenceID;
    @Column(name = "occurrenceRemarks", caseSensitive = true)
    private String occurrenceRemarks;
    @Column(name = "occurrenceStatus", caseSensitive = true)
    private String occurrenceStatus;
    @Column(name = "occurrenceStatus_p", caseSensitive = true)
    private String occurrenceStatus_p;
    @Column(name = "offline_qa", caseSensitive = true)
    private String offline_qa;
    @Column(name = "order", caseSensitive = true)
    private String order;
    @Column(name = "orderID", caseSensitive = true)
    private String orderID;
    @Column(name = "orderID_p", caseSensitive = true)
    private String orderID_p;
    @Column(name = "order_p", caseSensitive = true)
    private String order_p;
    @Column(name = "organismQuantity", caseSensitive = true)
    private String organismQuantity;
    @Column(name = "organismQuantityType", caseSensitive = true)
    private String organismQuantityType;
    @Column(name = "originalDecimalLatitude", caseSensitive = true)
    private String originalDecimalLatitude;
    @Column(name = "originalDecimalLongitude", caseSensitive = true)
    private String originalDecimalLongitude;
    @Column(name = "originalNameUsage", caseSensitive = true)
    private String originalNameUsage;
    @Column(name = "originalNameUsageID", caseSensitive = true)
    private String originalNameUsageID;
    @Column(name = "originalSensitiveValues", caseSensitive = true)
    private String originalSensitiveValues;
    @Column(name = "otherCatalogNumbers", caseSensitive = true)
    private String otherCatalogNumbers;
    @Column(name = "outlierForLayers", caseSensitive = true)
    private String outlierForLayers;
    @Column(name = "outlierForLayers_p", caseSensitive = true)
    private String outlierForLayers_p;
    @Column(name = "ownerInstitutionCode", caseSensitive = true)
    private String ownerInstitutionCode;
    @Column(name = "parentNameUsage", caseSensitive = true)
    private String parentNameUsage;
    @Column(name = "parentNameUsageID", caseSensitive = true)
    private String parentNameUsageID;
    @Column(name = "phenology", caseSensitive = true)
    private String phenology;
    @Column(name = "photoPageUrl", caseSensitive = true)
    private String photoPageUrl;
    @Column(name = "photographer", caseSensitive = true)
    private String photographer;
    @Column(name = "phylum", caseSensitive = true)
    private String phylum;
    @Column(name = "phylumID", caseSensitive = true)
    private String phylumID;
    @Column(name = "phylumID_p", caseSensitive = true)
    private String phylumID_p;
    @Column(name = "phylum_p", caseSensitive = true)
    private String phylum_p;
    @Column(name = "pointRadiusSpatialFit", caseSensitive = true)
    private String pointRadiusSpatialFit;
    @Column(name = "portalId", caseSensitive = true)
    private String portalId;
    @Column(name = "preferredFlag", caseSensitive = true)
    private String preferredFlag;
    @Column(name = "preparations", caseSensitive = true)
    private String preparations;
    @Column(name = "previousIdentifications", caseSensitive = true)
    private String previousIdentifications;
    @Column(name = "provenance", caseSensitive = true)
    private String provenance;
    @Column(name = "provenance_p", caseSensitive = true)
    private String provenance_p;
    @Column(name = "qualityAssertion", caseSensitive = true)
    private String qualityAssertion;
    @Column(name = "queryAssertions_p", caseSensitive = true)
    private String queryAssertions_p;
    @Column(name = "recordNumber", caseSensitive = true)
    private String recordNumber;
    @Column(name = "recordedBy", caseSensitive = true)
    private String recordedBy;
    @Column(name = "recordedBy_p", caseSensitive = true)
    private String recordedBy_p;
    @Column(name = "relatedResourceID", caseSensitive = true)
    private String relatedResourceID;
    @Column(name = "relationshipAccordingTo", caseSensitive = true)
    private String relationshipAccordingTo;
    @Column(name = "relationshipEstablishedDate", caseSensitive = true)
    private String relationshipEstablishedDate;
    @Column(name = "relationshipOfResource", caseSensitive = true)
    private String relationshipOfResource;
    @Column(name = "relationshipRemarks", caseSensitive = true)
    private String relationshipRemarks;
    @Column(name = "reprocessing_qa", caseSensitive = true)
    private String reprocessing_qa;
    @Column(name = "reproductiveCondition", caseSensitive = true)
    private String reproductiveCondition;
    @Column(name = "resourceID", caseSensitive = true)
    private String resourceID;
    @Column(name = "resourceRelationshipID", caseSensitive = true)
    private String resourceRelationshipID;
    @Column(name = "right", caseSensitive = true)
    private String right;
    @Column(name = "right_p", caseSensitive = true)
    private String right_p;
    @Column(name = "rights", caseSensitive = true)
    private String rights;
    @Column(name = "rightsholder", caseSensitive = true)
    private String rightsholder;
    @Column(name = "samplingEffort", caseSensitive = true)
    private String samplingEffort;
    @Column(name = "samplingProtocol", caseSensitive = true)
    private String samplingProtocol;
    @Column(name = "samplingProtocol_p", caseSensitive = true)
    private String samplingProtocol_p;
    @Column(name = "scientificName", caseSensitive = true)
    private String scientificName;
    @Column(name = "scientificNameAddendum", caseSensitive = true)
    private String scientificNameAddendum;
    @Column(name = "scientificNameAuthorship", caseSensitive = true)
    private String scientificNameAuthorship;
    @Column(name = "scientificNameAuthorship_p", caseSensitive = true)
    private String scientificNameAuthorship_p;
    @Column(name = "scientificNameID", caseSensitive = true)
    private String scientificNameID;
    @Column(name = "scientificNameWithoutAuthor", caseSensitive = true)
    private String scientificNameWithoutAuthor;
    @Column(name = "scientificName_p", caseSensitive = true)
    private String scientificName_p;
    @Column(name = "secondaryCollectors", caseSensitive = true)
    private String secondaryCollectors;
    @Column(name = "sensitive_qa", caseSensitive = true)
    private String sensitive_qa;
    @Column(name = "sex", caseSensitive = true)
    private String sex;
    @Column(name = "sounds", caseSensitive = true)
    private String sounds;
    @Column(name = "sounds_p", caseSensitive = true)
    private String sounds_p;
    @Column(name = "source", caseSensitive = true)
    private String source;
    @Column(name = "species", caseSensitive = true)
    private String species;
    @Column(name = "speciesGroups", caseSensitive = true)
    private String speciesGroups;
    @Column(name = "speciesGroups_p", caseSensitive = true)
    private String speciesGroups_p;
    @Column(name = "speciesHabitats", caseSensitive = true)
    private String speciesHabitats;
    @Column(name = "speciesHabitats_p", caseSensitive = true)
    private String speciesHabitats_p;
    @Column(name = "speciesID", caseSensitive = true)
    private String speciesID;
    @Column(name = "speciesID_p", caseSensitive = true)
    private String speciesID_p;
    @Column(name = "species_p", caseSensitive = true)
    private String species_p;
    @Column(name = "specificEpithet", caseSensitive = true)
    private String specificEpithet;
    @Column(name = "specificEpithet_p", caseSensitive = true)
    private String specificEpithet_p;
    @Column(name = "startDayOfYear", caseSensitive = true)
    private String startDayOfYear;
    @Column(name = "startYear", caseSensitive = true)
    private String startYear;
    @Column(name = "state", caseSensitive = true)
    private String state;
    @Column(name = "stateConservation", caseSensitive = true)
    private String stateConservation;
    @Column(name = "stateConservation_p", caseSensitive = true)
    private String stateConservation_p;
    @Column(name = "stateProvince", caseSensitive = true)
    private String stateProvince;
    @Column(name = "stateProvince_p", caseSensitive = true)
    private String stateProvince_p;
    @Column(name = "subfamily", caseSensitive = true)
    private String subfamily;
    @Column(name = "subgenus", caseSensitive = true)
    private String subgenus;
    @Column(name = "subgenusID", caseSensitive = true)
    private String subgenusID;
    @Column(name = "subspecies", caseSensitive = true)
    private String subspecies;
    @Column(name = "subspeciesID", caseSensitive = true)
    private String subspeciesID;
    @Column(name = "subspeciesID_p", caseSensitive = true)
    private String subspeciesID_p;
    @Column(name = "subspecies_p", caseSensitive = true)
    private String subspecies_p;
    @Column(name = "superfamily", caseSensitive = true)
    private String superfamily;
    @Column(name = "taxonConceptID", caseSensitive = true)
    private String taxonConceptID;
    @Column(name = "taxonConceptID_p", caseSensitive = true)
    private String taxonConceptID_p;
    @Column(name = "taxonID", caseSensitive = true)
    private String taxonID;
    @Column(name = "taxonRank", caseSensitive = true)
    private String taxonRank;
    @Column(name = "taxonRankID", caseSensitive = true)
    private String taxonRankID;
    @Column(name = "taxonRankID_p", caseSensitive = true)
    private String taxonRankID_p;
    @Column(name = "taxonRank_p", caseSensitive = true)
    private String taxonRank_p;
    @Column(name = "taxonRemarks", caseSensitive = true)
    private String taxonRemarks;
    @Column(name = "taxonomicIssue", caseSensitive = true)
    private String taxonomicIssue;
    @Column(name = "taxonomicIssue_p", caseSensitive = true)
    private String taxonomicIssue_p;
    @Column(name = "taxonomicStatus", caseSensitive = true)
    private String taxonomicStatus;
    @Column(name = "taxonomicallyKosher", caseSensitive = true)
    private String taxonomicallyKosher;
    @Column(name = "type", caseSensitive = true)
    private String type;
    @Column(name = "typeStatus", caseSensitive = true)
    private String typeStatus;
    @Column(name = "typeStatusQualifier", caseSensitive = true)
    private String typeStatusQualifier;
    @Column(name = "typeStatus_p", caseSensitive = true)
    private String typeStatus_p;
    @Column(name = "type_qa", caseSensitive = true)
    private String type_qa;
    @Column(name = "typifiedName", caseSensitive = true)
    private String typifiedName;
    @Column(name = "userAssertionStatus", caseSensitive = true)
    private String userAssertionStatus;
    @Column(name = "userId", caseSensitive = true)
    private String userId;
    @Column(name = "userId_p", caseSensitive = true)
    private String userId_p;
    @Column(name = "userQualityAssertion", caseSensitive = true)
    private String userQualityAssertion;
    @Column(name = "userVerified", caseSensitive = true)
    private String userVerified;
    @Column(name = "validDistribution", caseSensitive = true)
    private String validDistribution;
    @Column(name = "verbatimCoordinateSystem", caseSensitive = true)
    private String verbatimCoordinateSystem;
    @Column(name = "verbatimCoordinates", caseSensitive = true)
    private String verbatimCoordinates;
    @Column(name = "verbatimDateIdentified", caseSensitive = true)
    private String verbatimDateIdentified;
    @Column(name = "verbatimDepth", caseSensitive = true)
    private String verbatimDepth;
    @Column(name = "verbatimDepth_p", caseSensitive = true)
    private String verbatimDepth_p;
    @Column(name = "verbatimElevation", caseSensitive = true)
    private String verbatimElevation;
    @Column(name = "verbatimElevation_p", caseSensitive = true)
    private String verbatimElevation_p;
    @Column(name = "verbatimEventDate", caseSensitive = true)
    private String verbatimEventDate;
    @Column(name = "verbatimLatitude", caseSensitive = true)
    private String verbatimLatitude;
    @Column(name = "verbatimLocality", caseSensitive = true)
    private String verbatimLocality;
    @Column(name = "verbatimLongitude", caseSensitive = true)
    private String verbatimLongitude;
    @Column(name = "verbatimSRS", caseSensitive = true)
    private String verbatimSRS;
    @Column(name = "verbatimTaxonRank", caseSensitive = true)
    private String verbatimTaxonRank;
    @Column(name = "verbatimTaxonRank_p", caseSensitive = true)
    private String verbatimTaxonRank_p;
    @Column(name = "verificationDate", caseSensitive = true)
    private String verificationDate;
    @Column(name = "verificationNotes", caseSensitive = true)
    private String verificationNotes;
    @Column(name = "verifier", caseSensitive = true)
    private String verifier;
    @Column(name = "vernacularName", caseSensitive = true)
    private String vernacularName;
    @Column(name = "vernacularName_p", caseSensitive = true)
    private String vernacularName_p;
    @Column(name = "videos", caseSensitive = true)
    private String videos;
    @Column(name = "videos_p", caseSensitive = true)
    private String videos_p;
    @Column(name = "waterBody", caseSensitive = true)
    private String waterBody;
    @Column(name = "year", caseSensitive = true)
    private String year;
    @Column(name = "year_p", caseSensitive = true)
    private String year_p;
    @Column(name = "zone", caseSensitive = true)
    private String zone;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getIdentificationQualifierProcessor_qa() {
        return identificationQualifierProcessor_qa;
    }

    public void setIdentificationQualifierProcessor_qa(String identificationQualifierProcessor_qa) {
        this.identificationQualifierProcessor_qa = identificationQualifierProcessor_qa;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String get_qa() {
        return _qa;
    }

    public void set_qa(String _qa) {
        this._qa = _qa;
    }

    public String getAbcdIdentificationQualifier() {
        return abcdIdentificationQualifier;
    }

    public void setAbcdIdentificationQualifier(String abcdIdentificationQualifier) {
        this.abcdIdentificationQualifier = abcdIdentificationQualifier;
    }

    public String getAbcdIdentificationQualifierInsertionPoint() {
        return abcdIdentificationQualifierInsertionPoint;
    }

    public void setAbcdIdentificationQualifierInsertionPoint(String abcdIdentificationQualifierInsertionPoint) {
        this.abcdIdentificationQualifierInsertionPoint = abcdIdentificationQualifierInsertionPoint;
    }

    public String getAbcdIdentificationQualifier_p() {
        return abcdIdentificationQualifier_p;
    }

    public void setAbcdIdentificationQualifier_p(String abcdIdentificationQualifier_p) {
        this.abcdIdentificationQualifier_p = abcdIdentificationQualifier_p;
    }

    public String getAbcdTypeStatus() {
        return abcdTypeStatus;
    }

    public void setAbcdTypeStatus(String abcdTypeStatus) {
        this.abcdTypeStatus = abcdTypeStatus;
    }

    public String getAcceptedNameUsage() {
        return acceptedNameUsage;
    }

    public void setAcceptedNameUsage(String acceptedNameUsage) {
        this.acceptedNameUsage = acceptedNameUsage;
    }

    public String getAcceptedNameUsageID() {
        return acceptedNameUsageID;
    }

    public void setAcceptedNameUsageID(String acceptedNameUsageID) {
        this.acceptedNameUsageID = acceptedNameUsageID;
    }

    public String getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(String accessRights) {
        this.accessRights = accessRights;
    }

    public String getAlaUserId() {
        return alaUserId;
    }

    public void setAlaUserId(String alaUserId) {
        this.alaUserId = alaUserId;
    }

    public String getAssociatedMedia() {
        return associatedMedia;
    }

    public void setAssociatedMedia(String associatedMedia) {
        this.associatedMedia = associatedMedia;
    }

    public String getAssociatedOccurrences() {
        return associatedOccurrences;
    }

    public void setAssociatedOccurrences(String associatedOccurrences) {
        this.associatedOccurrences = associatedOccurrences;
    }

    public String getAssociatedOccurrences_p() {
        return associatedOccurrences_p;
    }

    public void setAssociatedOccurrences_p(String associatedOccurrences_p) {
        this.associatedOccurrences_p = associatedOccurrences_p;
    }

    public String getAssociatedReferences() {
        return associatedReferences;
    }

    public void setAssociatedReferences(String associatedReferences) {
        this.associatedReferences = associatedReferences;
    }

    public String getAssociatedReferences_p() {
        return associatedReferences_p;
    }

    public void setAssociatedReferences_p(String associatedReferences_p) {
        this.associatedReferences_p = associatedReferences_p;
    }

    public String getAssociatedSequences() {
        return associatedSequences;
    }

    public void setAssociatedSequences(String associatedSequences) {
        this.associatedSequences = associatedSequences;
    }

    public String getAssociatedTaxa() {
        return associatedTaxa;
    }

    public void setAssociatedTaxa(String associatedTaxa) {
        this.associatedTaxa = associatedTaxa;
    }

    public String getAttr_qa() {
        return attr_qa;
    }

    public void setAttr_qa(String attr_qa) {
        this.attr_qa = attr_qa;
    }

    public String getAustConservation_p() {
        return austConservation_p;
    }

    public void setAustConservation_p(String austConservation_p) {
        this.austConservation_p = austConservation_p;
    }

    public String getAustralianHerbariumRegion() {
        return australianHerbariumRegion;
    }

    public void setAustralianHerbariumRegion(String australianHerbariumRegion) {
        this.australianHerbariumRegion = australianHerbariumRegion;
    }

    public String getBasisOfRecord() {
        return basisOfRecord;
    }

    public void setBasisOfRecord(String basisOfRecord) {
        this.basisOfRecord = basisOfRecord;
    }

    public String getBasisOfRecord_p() {
        return basisOfRecord_p;
    }

    public void setBasisOfRecord_p(String basisOfRecord_p) {
        this.basisOfRecord_p = basisOfRecord_p;
    }

    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    public String getBbox_p() {
        return bbox_p;
    }

    public void setBbox_p(String bbox_p) {
        this.bbox_p = bbox_p;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public String getBibliographicCitation() {
        return bibliographicCitation;
    }

    public void setBibliographicCitation(String bibliographicCitation) {
        this.bibliographicCitation = bibliographicCitation;
    }

    public String getBiome() {
        return biome;
    }

    public void setBiome(String biome) {
        this.biome = biome;
    }

    public String getBiome_p() {
        return biome_p;
    }

    public void setBiome_p(String biome_p) {
        this.biome_p = biome_p;
    }

    public String getBor_qa() {
        return bor_qa;
    }

    public void setBor_qa(String bor_qa) {
        this.bor_qa = bor_qa;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getCl_p() {
        return cl_p;
    }

    public void setCl_p(String cl_p) {
        this.cl_p = cl_p;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getClassID_p() {
        return classID_p;
    }

    public void setClassID_p(String classID_p) {
        this.classID_p = classID_p;
    }

    public String getClassField() {
        return classField;
    }

    public void setClassField(String classField) {
        this.classField = classField;
    }

    public String getClass_p() {
        return class_p;
    }

    public void setClass_p(String class_p) {
        this.class_p = class_p;
    }

    public String getClass_qa() {
        return class_qa;
    }

    public void setClass_qa(String class_qa) {
        this.class_qa = class_qa;
    }

    public String getClasss() {
        return classs;
    }

    public void setClasss(String classs) {
        this.classs = classs;
    }

    public String getClasss_p() {
        return classs_p;
    }

    public void setClasss_p(String classs_p) {
        this.classs_p = classs_p;
    }

    public String getCollectionCode() {
        return collectionCode;
    }

    public void setCollectionCode(String collectionCode) {
        this.collectionCode = collectionCode;
    }

    public String getCollectionCode_p() {
        return collectionCode_p;
    }

    public void setCollectionCode_p(String collectionCode_p) {
        this.collectionCode_p = collectionCode_p;
    }

    public String getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(String collectionID) {
        this.collectionID = collectionID;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName_p() {
        return collectionName_p;
    }

    public void setCollectionName_p(String collectionName_p) {
        this.collectionName_p = collectionName_p;
    }

    public String getCollectionUid() {
        return collectionUid;
    }

    public void setCollectionUid(String collectionUid) {
        this.collectionUid = collectionUid;
    }

    public String getCollectionUid_p() {
        return collectionUid_p;
    }

    public void setCollectionUid_p(String collectionUid_p) {
        this.collectionUid_p = collectionUid_p;
    }

    public String getCollectorFieldNumber() {
        return collectorFieldNumber;
    }

    public void setCollectorFieldNumber(String collectorFieldNumber) {
        this.collectorFieldNumber = collectorFieldNumber;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getContinent_p() {
        return continent_p;
    }

    public void setContinent_p(String continent_p) {
        this.continent_p = continent_p;
    }

    public String getCoordinatePrecision() {
        return coordinatePrecision;
    }

    public void setCoordinatePrecision(String coordinatePrecision) {
        this.coordinatePrecision = coordinatePrecision;
    }

    public String getCoordinatePrecision_p() {
        return coordinatePrecision_p;
    }

    public void setCoordinatePrecision_p(String coordinatePrecision_p) {
        this.coordinatePrecision_p = coordinatePrecision_p;
    }

    public String getCoordinateUncertaintyInMeters() {
        return coordinateUncertaintyInMeters;
    }

    public void setCoordinateUncertaintyInMeters(String coordinateUncertaintyInMeters) {
        this.coordinateUncertaintyInMeters = coordinateUncertaintyInMeters;
    }

    public String getCoordinateUncertaintyInMeters_p() {
        return coordinateUncertaintyInMeters_p;
    }

    public void setCoordinateUncertaintyInMeters_p(String coordinateUncertaintyInMeters_p) {
        this.coordinateUncertaintyInMeters_p = coordinateUncertaintyInMeters_p;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode_p() {
        return countryCode_p;
    }

    public void setCountryCode_p(String countryCode_p) {
        this.countryCode_p = countryCode_p;
    }

    public String getCountryConservation() {
        return countryConservation;
    }

    public void setCountryConservation(String countryConservation) {
        this.countryConservation = countryConservation;
    }

    public String getCountryConservation_p() {
        return countryConservation_p;
    }

    public void setCountryConservation_p(String countryConservation_p) {
        this.countryConservation_p = countryConservation_p;
    }

    public String getCountry_p() {
        return country_p;
    }

    public void setCountry_p(String country_p) {
        this.country_p = country_p;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCultivarName() {
        return cultivarName;
    }

    public void setCultivarName(String cultivarName) {
        this.cultivarName = cultivarName;
    }

    public String getCultivated() {
        return cultivated;
    }

    public void setCultivated(String cultivated) {
        this.cultivated = cultivated;
    }

    public String getDataGeneralizations() {
        return dataGeneralizations;
    }

    public void setDataGeneralizations(String dataGeneralizations) {
        this.dataGeneralizations = dataGeneralizations;
    }

    public String getDataGeneralizations_p() {
        return dataGeneralizations_p;
    }

    public void setDataGeneralizations_p(String dataGeneralizations_p) {
        this.dataGeneralizations_p = dataGeneralizations_p;
    }

    public String getDataHubName() {
        return dataHubName;
    }

    public void setDataHubName(String dataHubName) {
        this.dataHubName = dataHubName;
    }

    public String getDataHubUid() {
        return dataHubUid;
    }

    public void setDataHubUid(String dataHubUid) {
        this.dataHubUid = dataHubUid;
    }

    public String getDataHubUid_p() {
        return dataHubUid_p;
    }

    public void setDataHubUid_p(String dataHubUid_p) {
        this.dataHubUid_p = dataHubUid_p;
    }

    public String getDataProviderName() {
        return dataProviderName;
    }

    public void setDataProviderName(String dataProviderName) {
        this.dataProviderName = dataProviderName;
    }

    public String getDataProviderName_p() {
        return dataProviderName_p;
    }

    public void setDataProviderName_p(String dataProviderName_p) {
        this.dataProviderName_p = dataProviderName_p;
    }

    public String getDataProviderUid() {
        return dataProviderUid;
    }

    public void setDataProviderUid(String dataProviderUid) {
        this.dataProviderUid = dataProviderUid;
    }

    public String getDataProviderUid_p() {
        return dataProviderUid_p;
    }

    public void setDataProviderUid_p(String dataProviderUid_p) {
        this.dataProviderUid_p = dataProviderUid_p;
    }

    public String getDataResourceName() {
        return dataResourceName;
    }

    public void setDataResourceName(String dataResourceName) {
        this.dataResourceName = dataResourceName;
    }

    public String getDataResourceName_p() {
        return dataResourceName_p;
    }

    public void setDataResourceName_p(String dataResourceName_p) {
        this.dataResourceName_p = dataResourceName_p;
    }

    public String getDataResourceUid() {
        return dataResourceUid;
    }

    public void setDataResourceUid(String dataResourceUid) {
        this.dataResourceUid = dataResourceUid;
    }

    public String getDatasetID() {
        return datasetID;
    }

    public void setDatasetID(String datasetID) {
        this.datasetID = datasetID;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(String dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    public String getDateIdentified() {
        return dateIdentified;
    }

    public void setDateIdentified(String dateIdentified) {
        this.dateIdentified = dateIdentified;
    }

    public String getDateIdentified_p() {
        return dateIdentified_p;
    }

    public void setDateIdentified_p(String dateIdentified_p) {
        this.dateIdentified_p = dateIdentified_p;
    }

    public String getDatePrecision() {
        return datePrecision;
    }

    public void setDatePrecision(String datePrecision) {
        this.datePrecision = datePrecision;
    }

    public String getDatePrecision_p() {
        return datePrecision_p;
    }

    public void setDatePrecision_p(String datePrecision_p) {
        this.datePrecision_p = datePrecision_p;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay_p() {
        return day_p;
    }

    public void setDay_p(String day_p) {
        this.day_p = day_p;
    }

    public String getDecimalLatitude() {
        return decimalLatitude;
    }

    public void setDecimalLatitude(String decimalLatitude) {
        this.decimalLatitude = decimalLatitude;
    }

    public String getDecimalLatitude_p() {
        return decimalLatitude_p;
    }

    public void setDecimalLatitude_p(String decimalLatitude_p) {
        this.decimalLatitude_p = decimalLatitude_p;
    }

    public String getDecimalLatitudelatitude() {
        return decimalLatitudelatitude;
    }

    public void setDecimalLatitudelatitude(String decimalLatitudelatitude) {
        this.decimalLatitudelatitude = decimalLatitudelatitude;
    }

    public String getDecimalLongitude() {
        return decimalLongitude;
    }

    public void setDecimalLongitude(String decimalLongitude) {
        this.decimalLongitude = decimalLongitude;
    }

    public String getDecimalLongitude_p() {
        return decimalLongitude_p;
    }

    public void setDecimalLongitude_p(String decimalLongitude_p) {
        this.decimalLongitude_p = decimalLongitude_p;
    }

    public String getDefaultValuesUsed() {
        return defaultValuesUsed;
    }

    public void setDefaultValuesUsed(String defaultValuesUsed) {
        this.defaultValuesUsed = defaultValuesUsed;
    }

    public String getDefault_qa() {
        return default_qa;
    }

    public void setDefault_qa(String default_qa) {
        this.default_qa = default_qa;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getDistanceOutsideExpertRange() {
        return distanceOutsideExpertRange;
    }

    public void setDistanceOutsideExpertRange(String distanceOutsideExpertRange) {
        this.distanceOutsideExpertRange = distanceOutsideExpertRange;
    }

    public String getDistanceOutsideExpertRange_p() {
        return distanceOutsideExpertRange_p;
    }

    public void setDistanceOutsideExpertRange_p(String distanceOutsideExpertRange_p) {
        this.distanceOutsideExpertRange_p = distanceOutsideExpertRange_p;
    }

    public String getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(String duplicates) {
        this.duplicates = duplicates;
    }

    public String getDuplicatesOriginalInstitutionID() {
        return duplicatesOriginalInstitutionID;
    }

    public void setDuplicatesOriginalInstitutionID(String duplicatesOriginalInstitutionID) {
        this.duplicatesOriginalInstitutionID = duplicatesOriginalInstitutionID;
    }

    public String getDuplicatesOriginalUnitID() {
        return duplicatesOriginalUnitID;
    }

    public void setDuplicatesOriginalUnitID(String duplicatesOriginalUnitID) {
        this.duplicatesOriginalUnitID = duplicatesOriginalUnitID;
    }

    public String getDuplicates_qa() {
        return duplicates_qa;
    }

    public void setDuplicates_qa(String duplicates_qa) {
        this.duplicates_qa = duplicates_qa;
    }

    public String getDuplicationStatus() {
        return duplicationStatus;
    }

    public void setDuplicationStatus(String duplicationStatus) {
        this.duplicationStatus = duplicationStatus;
    }

    public String getDuplicationStatus_p() {
        return duplicationStatus_p;
    }

    public void setDuplicationStatus_p(String duplicationStatus_p) {
        this.duplicationStatus_p = duplicationStatus_p;
    }

    public String getDuplicationType() {
        return duplicationType;
    }

    public void setDuplicationType(String duplicationType) {
        this.duplicationType = duplicationType;
    }

    public String getDuplicationType_p() {
        return duplicationType_p;
    }

    public void setDuplicationType_p(String duplicationType_p) {
        this.duplicationType_p = duplicationType_p;
    }

    public String getDynamicProperties() {
        return dynamicProperties;
    }

    public void setDynamicProperties(String dynamicProperties) {
        this.dynamicProperties = dynamicProperties;
    }

    public String getEasting() {
        return easting;
    }

    public void setEasting(String easting) {
        this.easting = easting;
    }

    public String getEasting_p() {
        return easting_p;
    }

    public void setEasting_p(String easting_p) {
        this.easting_p = easting_p;
    }

    public String getEl_p() {
        return el_p;
    }

    public void setEl_p(String el_p) {
        this.el_p = el_p;
    }

    public String getEndDayOfYear() {
        return endDayOfYear;
    }

    public void setEndDayOfYear(String endDayOfYear) {
        this.endDayOfYear = endDayOfYear;
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public String getEstablishmentMeans() {
        return establishmentMeans;
    }

    public void setEstablishmentMeans(String establishmentMeans) {
        this.establishmentMeans = establishmentMeans;
    }

    public String getEstablishmentMeans_p() {
        return establishmentMeans_p;
    }

    public void setEstablishmentMeans_p(String establishmentMeans_p) {
        this.establishmentMeans_p = establishmentMeans_p;
    }

    public String getEventAttributes() {
        return eventAttributes;
    }

    public void setEventAttributes(String eventAttributes) {
        this.eventAttributes = eventAttributes;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDateEnd() {
        return eventDateEnd;
    }

    public void setEventDateEnd(String eventDateEnd) {
        this.eventDateEnd = eventDateEnd;
    }

    public String getEventDateEnd_p() {
        return eventDateEnd_p;
    }

    public void setEventDateEnd_p(String eventDateEnd_p) {
        this.eventDateEnd_p = eventDateEnd_p;
    }

    public String getEventDate_p() {
        return eventDate_p;
    }

    public void setEventDate_p(String eventDate_p) {
        this.eventDate_p = eventDate_p;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventRemarks() {
        return eventRemarks;
    }

    public void setEventRemarks(String eventRemarks) {
        this.eventRemarks = eventRemarks;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEvent_qa() {
        return event_qa;
    }

    public void setEvent_qa(String event_qa) {
        this.event_qa = event_qa;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getFamilyID() {
        return familyID;
    }

    public void setFamilyID(String familyID) {
        this.familyID = familyID;
    }

    public String getFamilyID_p() {
        return familyID_p;
    }

    public void setFamilyID_p(String familyID_p) {
        this.familyID_p = familyID_p;
    }

    public String getFamily_p() {
        return family_p;
    }

    public void setFamily_p(String family_p) {
        this.family_p = family_p;
    }

    public String getFieldNotes() {
        return fieldNotes;
    }

    public void setFieldNotes(String fieldNotes) {
        this.fieldNotes = fieldNotes;
    }

    public String getFieldNumber() {
        return fieldNumber;
    }

    public void setFieldNumber(String fieldNumber) {
        this.fieldNumber = fieldNumber;
    }

    public String getFirstLoaded() {
        return firstLoaded;
    }

    public void setFirstLoaded(String firstLoaded) {
        this.firstLoaded = firstLoaded;
    }

    public String getFootprintSRS() {
        return footprintSRS;
    }

    public void setFootprintSRS(String footprintSRS) {
        this.footprintSRS = footprintSRS;
    }

    public String getFootprintSpatialFit() {
        return footprintSpatialFit;
    }

    public void setFootprintSpatialFit(String footprintSpatialFit) {
        this.footprintSpatialFit = footprintSpatialFit;
    }

    public String getFootprintWKT() {
        return footprintWKT;
    }

    public void setFootprintWKT(String footprintWKT) {
        this.footprintWKT = footprintWKT;
    }

    public String getGeneralisationToApplyInMetres() {
        return generalisationToApplyInMetres;
    }

    public void setGeneralisationToApplyInMetres(String generalisationToApplyInMetres) {
        this.generalisationToApplyInMetres = generalisationToApplyInMetres;
    }

    public String getGeneralisedLocality() {
        return generalisedLocality;
    }

    public void setGeneralisedLocality(String generalisedLocality) {
        this.generalisedLocality = generalisedLocality;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getGenusID() {
        return genusID;
    }

    public void setGenusID(String genusID) {
        this.genusID = genusID;
    }

    public String getGenusID_p() {
        return genusID_p;
    }

    public void setGenusID_p(String genusID_p) {
        this.genusID_p = genusID_p;
    }

    public String getGenus_p() {
        return genus_p;
    }

    public void setGenus_p(String genus_p) {
        this.genus_p = genus_p;
    }

    public String getGeodeticDatum() {
        return geodeticDatum;
    }

    public void setGeodeticDatum(String geodeticDatum) {
        this.geodeticDatum = geodeticDatum;
    }

    public String getGeodeticDatum_p() {
        return geodeticDatum_p;
    }

    public void setGeodeticDatum_p(String geodeticDatum_p) {
        this.geodeticDatum_p = geodeticDatum_p;
    }

    public String getGeoreferenceProtocol() {
        return georeferenceProtocol;
    }

    public void setGeoreferenceProtocol(String georeferenceProtocol) {
        this.georeferenceProtocol = georeferenceProtocol;
    }

    public String getGeoreferenceProtocol_p() {
        return georeferenceProtocol_p;
    }

    public void setGeoreferenceProtocol_p(String georeferenceProtocol_p) {
        this.georeferenceProtocol_p = georeferenceProtocol_p;
    }

    public String getGeoreferenceRemarks() {
        return georeferenceRemarks;
    }

    public void setGeoreferenceRemarks(String georeferenceRemarks) {
        this.georeferenceRemarks = georeferenceRemarks;
    }

    public String getGeoreferenceSources() {
        return georeferenceSources;
    }

    public void setGeoreferenceSources(String georeferenceSources) {
        this.georeferenceSources = georeferenceSources;
    }

    public String getGeoreferenceSources_p() {
        return georeferenceSources_p;
    }

    public void setGeoreferenceSources_p(String georeferenceSources_p) {
        this.georeferenceSources_p = georeferenceSources_p;
    }

    public String getGeoreferenceVerificationStatus() {
        return georeferenceVerificationStatus;
    }

    public void setGeoreferenceVerificationStatus(String georeferenceVerificationStatus) {
        this.georeferenceVerificationStatus = georeferenceVerificationStatus;
    }

    public String getGeoreferenceVerificationStatus_p() {
        return georeferenceVerificationStatus_p;
    }

    public void setGeoreferenceVerificationStatus_p(String georeferenceVerificationStatus_p) {
        this.georeferenceVerificationStatus_p = georeferenceVerificationStatus_p;
    }

    public String getGeoreferencedBy() {
        return georeferencedBy;
    }

    public void setGeoreferencedBy(String georeferencedBy) {
        this.georeferencedBy = georeferencedBy;
    }

    public String getGeoreferencedBy_p() {
        return georeferencedBy_p;
    }

    public void setGeoreferencedBy_p(String georeferencedBy_p) {
        this.georeferencedBy_p = georeferencedBy_p;
    }

    public String getGeoreferencedDate() {
        return georeferencedDate;
    }

    public void setGeoreferencedDate(String georeferencedDate) {
        this.georeferencedDate = georeferencedDate;
    }

    public String getGeoreferencedDate_p() {
        return georeferencedDate_p;
    }

    public void setGeoreferencedDate_p(String georeferencedDate_p) {
        this.georeferencedDate_p = georeferencedDate_p;
    }

    public String getGeospatialIssue() {
        return geospatialIssue;
    }

    public void setGeospatialIssue(String geospatialIssue) {
        this.geospatialIssue = geospatialIssue;
    }

    public String getGeospatiallyKosher() {
        return geospatiallyKosher;
    }

    public void setGeospatiallyKosher(String geospatiallyKosher) {
        this.geospatiallyKosher = geospatiallyKosher;
    }

    public String getGlobalConservation() {
        return globalConservation;
    }

    public void setGlobalConservation(String globalConservation) {
        this.globalConservation = globalConservation;
    }

    public String getGridReference() {
        return gridReference;
    }

    public void setGridReference(String gridReference) {
        this.gridReference = gridReference;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public String getHabitat_p() {
        return habitat_p;
    }

    public void setHabitat_p(String habitat_p) {
        this.habitat_p = habitat_p;
    }

    public String getHigherClassification() {
        return higherClassification;
    }

    public void setHigherClassification(String higherClassification) {
        this.higherClassification = higherClassification;
    }

    public String getHigherGeography() {
        return higherGeography;
    }

    public void setHigherGeography(String higherGeography) {
        this.higherGeography = higherGeography;
    }

    public String getHigherGeographyID() {
        return higherGeographyID;
    }

    public void setHigherGeographyID(String higherGeographyID) {
        this.higherGeographyID = higherGeographyID;
    }

    public String getIbra() {
        return ibra;
    }

    public void setIbra(String ibra) {
        this.ibra = ibra;
    }

    public String getIbraSubregion() {
        return ibraSubregion;
    }

    public void setIbraSubregion(String ibraSubregion) {
        this.ibraSubregion = ibraSubregion;
    }

    public String getIbra_p() {
        return ibra_p;
    }

    public void setIbra_p(String ibra_p) {
        this.ibra_p = ibra_p;
    }

    public String getIdentificationID() {
        return identificationID;
    }

    public void setIdentificationID(String identificationID) {
        this.identificationID = identificationID;
    }

    public String getIdentificationQualifier() {
        return identificationQualifier;
    }

    public void setIdentificationQualifier(String identificationQualifier) {
        this.identificationQualifier = identificationQualifier;
    }

    public String getIdentificationQualifier_p() {
        return identificationQualifier_p;
    }

    public void setIdentificationQualifier_p(String identificationQualifier_p) {
        this.identificationQualifier_p = identificationQualifier_p;
    }

    public String getIdentificationReferences() {
        return identificationReferences;
    }

    public void setIdentificationReferences(String identificationReferences) {
        this.identificationReferences = identificationReferences;
    }

    public String getIdentificationReferences_p() {
        return identificationReferences_p;
    }

    public void setIdentificationReferences_p(String identificationReferences_p) {
        this.identificationReferences_p = identificationReferences_p;
    }

    public String getIdentificationRemarks() {
        return identificationRemarks;
    }

    public void setIdentificationRemarks(String identificationRemarks) {
        this.identificationRemarks = identificationRemarks;
    }

    public String getIdentificationVerificationStatus() {
        return identificationVerificationStatus;
    }

    public void setIdentificationVerificationStatus(String identificationVerificationStatus) {
        this.identificationVerificationStatus = identificationVerificationStatus;
    }

    public String getIdentificationVerificationStatus_p() {
        return identificationVerificationStatus_p;
    }

    public void setIdentificationVerificationStatus_p(String identificationVerificationStatus_p) {
        this.identificationVerificationStatus_p = identificationVerificationStatus_p;
    }

    public String getIdentification_qa() {
        return identification_qa;
    }

    public void setIdentification_qa(String identification_qa) {
        this.identification_qa = identification_qa;
    }

    public String getIdentifiedBy() {
        return identifiedBy;
    }

    public void setIdentifiedBy(String identifiedBy) {
        this.identifiedBy = identifiedBy;
    }

    public String getIdentifiedBy_p() {
        return identifiedBy_p;
    }

    public void setIdentifiedBy_p(String identifiedBy_p) {
        this.identifiedBy_p = identifiedBy_p;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifierBy() {
        return identifierBy;
    }

    public void setIdentifierBy(String identifierBy) {
        this.identifierBy = identifierBy;
    }

    public String getIdentifierRole() {
        return identifierRole;
    }

    public void setIdentifierRole(String identifierRole) {
        this.identifierRole = identifierRole;
    }

    public String getImage_qa() {
        return image_qa;
    }

    public void setImage_qa(String image_qa) {
        this.image_qa = image_qa;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getImages_p() {
        return images_p;
    }

    public void setImages_p(String images_p) {
        this.images_p = images_p;
    }

    public String getImcra_p() {
        return imcra_p;
    }

    public void setImcra_p(String imcra_p) {
        this.imcra_p = imcra_p;
    }

    public String getIndividualCount() {
        return individualCount;
    }

    public void setIndividualCount(String individualCount) {
        this.individualCount = individualCount;
    }

    public String getIndividualID() {
        return individualID;
    }

    public void setIndividualID(String individualID) {
        this.individualID = individualID;
    }

    public String getInformationWithheld() {
        return informationWithheld;
    }

    public void setInformationWithheld(String informationWithheld) {
        this.informationWithheld = informationWithheld;
    }

    public String getInformationWithheld_p() {
        return informationWithheld_p;
    }

    public void setInformationWithheld_p(String informationWithheld_p) {
        this.informationWithheld_p = informationWithheld_p;
    }

    public String getInfraspecificEpithet() {
        return infraspecificEpithet;
    }

    public void setInfraspecificEpithet(String infraspecificEpithet) {
        this.infraspecificEpithet = infraspecificEpithet;
    }

    public String getInfraspecificEpithet_p() {
        return infraspecificEpithet_p;
    }

    public void setInfraspecificEpithet_p(String infraspecificEpithet_p) {
        this.infraspecificEpithet_p = infraspecificEpithet_p;
    }

    public String getInfraspecificMarker() {
        return infraspecificMarker;
    }

    public void setInfraspecificMarker(String infraspecificMarker) {
        this.infraspecificMarker = infraspecificMarker;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getInstitutionCode_p() {
        return institutionCode_p;
    }

    public void setInstitutionCode_p(String institutionCode_p) {
        this.institutionCode_p = institutionCode_p;
    }

    public String getInstitutionID() {
        return institutionID;
    }

    public void setInstitutionID(String institutionID) {
        this.institutionID = institutionID;
    }

    public String getInstitutionID_p() {
        return institutionID_p;
    }

    public void setInstitutionID_p(String institutionID_p) {
        this.institutionID_p = institutionID_p;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionName_p() {
        return institutionName_p;
    }

    public void setInstitutionName_p(String institutionName_p) {
        this.institutionName_p = institutionName_p;
    }

    public String getInstitutionUid() {
        return institutionUid;
    }

    public void setInstitutionUid(String institutionUid) {
        this.institutionUid = institutionUid;
    }

    public String getInstitutionUid_p() {
        return institutionUid_p;
    }

    public void setInstitutionUid_p(String institutionUid_p) {
        this.institutionUid_p = institutionUid_p;
    }

    public String getInteractions() {
        return interactions;
    }

    public void setInteractions(String interactions) {
        this.interactions = interactions;
    }

    public String getInteractions_p() {
        return interactions_p;
    }

    public void setInteractions_p(String interactions_p) {
        this.interactions_p = interactions_p;
    }

    public String getIsland() {
        return island;
    }

    public void setIsland(String island) {
        this.island = island;
    }

    public String getIslandGroup() {
        return islandGroup;
    }

    public void setIslandGroup(String islandGroup) {
        this.islandGroup = islandGroup;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public String getKingdomID() {
        return kingdomID;
    }

    public void setKingdomID(String kingdomID) {
        this.kingdomID = kingdomID;
    }

    public String getKingdomID_p() {
        return kingdomID_p;
    }

    public void setKingdomID_p(String kingdomID_p) {
        this.kingdomID_p = kingdomID_p;
    }

    public String getKingdom_p() {
        return kingdom_p;
    }

    public void setKingdom_p(String kingdom_p) {
        this.kingdom_p = kingdom_p;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getLastModifiedTime_p() {
        return lastModifiedTime_p;
    }

    public void setLastModifiedTime_p(String lastModifiedTime_p) {
        this.lastModifiedTime_p = lastModifiedTime_p;
    }

    public String getLastUserAssertionDate() {
        return lastUserAssertionDate;
    }

    public void setLastUserAssertionDate(String lastUserAssertionDate) {
        this.lastUserAssertionDate = lastUserAssertionDate;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getLeft_p() {
        return left_p;
    }

    public void setLeft_p(String left_p) {
        this.left_p = left_p;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public String getLga_p() {
        return lga_p;
    }

    public void setLga_p(String lga_p) {
        this.lga_p = lga_p;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicense_p() {
        return license_p;
    }

    public void setLicense_p(String license_p) {
        this.license_p = license_p;
    }

    public String getLifeStage() {
        return lifeStage;
    }

    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }

    public String getLifeStage_p() {
        return lifeStage_p;
    }

    public void setLifeStage_p(String lifeStage_p) {
        this.lifeStage_p = lifeStage_p;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public String getLoanDestination() {
        return loanDestination;
    }

    public void setLoanDestination(String loanDestination) {
        this.loanDestination = loanDestination;
    }

    public String getLoanForBotanist() {
        return loanForBotanist;
    }

    public void setLoanForBotanist(String loanForBotanist) {
        this.loanForBotanist = loanForBotanist;
    }

    public String getLoanIdentifier() {
        return loanIdentifier;
    }

    public void setLoanIdentifier(String loanIdentifier) {
        this.loanIdentifier = loanIdentifier;
    }

    public String getLoanReturnDate() {
        return loanReturnDate;
    }

    public void setLoanReturnDate(String loanReturnDate) {
        this.loanReturnDate = loanReturnDate;
    }

    public String getLoanSequenceNumber() {
        return loanSequenceNumber;
    }

    public void setLoanSequenceNumber(String loanSequenceNumber) {
        this.loanSequenceNumber = loanSequenceNumber;
    }

    public String getLoc_qa() {
        return loc_qa;
    }

    public void setLoc_qa(String loc_qa) {
        this.loc_qa = loc_qa;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getLocality_p() {
        return locality_p;
    }

    public void setLocality_p(String locality_p) {
        this.locality_p = locality_p;
    }

    public String getLocationAccordingTo() {
        return locationAccordingTo;
    }

    public void setLocationAccordingTo(String locationAccordingTo) {
        this.locationAccordingTo = locationAccordingTo;
    }

    public String getLocationAttributes() {
        return locationAttributes;
    }

    public void setLocationAttributes(String locationAttributes) {
        this.locationAttributes = locationAttributes;
    }

    public String getLocationDetermined() {
        return locationDetermined;
    }

    public void setLocationDetermined(String locationDetermined) {
        this.locationDetermined = locationDetermined;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getLocationRemarks() {
        return locationRemarks;
    }

    public void setLocationRemarks(String locationRemarks) {
        this.locationRemarks = locationRemarks;
    }

    public String getMaximumDepthInMeters() {
        return maximumDepthInMeters;
    }

    public void setMaximumDepthInMeters(String maximumDepthInMeters) {
        this.maximumDepthInMeters = maximumDepthInMeters;
    }

    public String getMaximumDepthInMeters_p() {
        return maximumDepthInMeters_p;
    }

    public void setMaximumDepthInMeters_p(String maximumDepthInMeters_p) {
        this.maximumDepthInMeters_p = maximumDepthInMeters_p;
    }

    public String getMaximumDistanceAboveSurfaceInMeters() {
        return maximumDistanceAboveSurfaceInMeters;
    }

    public void setMaximumDistanceAboveSurfaceInMeters(String maximumDistanceAboveSurfaceInMeters) {
        this.maximumDistanceAboveSurfaceInMeters = maximumDistanceAboveSurfaceInMeters;
    }

    public String getMaximumElevationInMeters() {
        return maximumElevationInMeters;
    }

    public void setMaximumElevationInMeters(String maximumElevationInMeters) {
        this.maximumElevationInMeters = maximumElevationInMeters;
    }

    public String getMaximumElevationInMeters_p() {
        return maximumElevationInMeters_p;
    }

    public void setMaximumElevationInMeters_p(String maximumElevationInMeters_p) {
        this.maximumElevationInMeters_p = maximumElevationInMeters_p;
    }

    public String getMeasurementAccuracy() {
        return measurementAccuracy;
    }

    public void setMeasurementAccuracy(String measurementAccuracy) {
        this.measurementAccuracy = measurementAccuracy;
    }

    public String getMeasurementDeterminedBy() {
        return measurementDeterminedBy;
    }

    public void setMeasurementDeterminedBy(String measurementDeterminedBy) {
        this.measurementDeterminedBy = measurementDeterminedBy;
    }

    public String getMeasurementDeterminedDate() {
        return measurementDeterminedDate;
    }

    public void setMeasurementDeterminedDate(String measurementDeterminedDate) {
        this.measurementDeterminedDate = measurementDeterminedDate;
    }

    public String getMeasurementID() {
        return measurementID;
    }

    public void setMeasurementID(String measurementID) {
        this.measurementID = measurementID;
    }

    public String getMeasurementMethod() {
        return measurementMethod;
    }

    public void setMeasurementMethod(String measurementMethod) {
        this.measurementMethod = measurementMethod;
    }

    public String getMeasurementRemarks() {
        return measurementRemarks;
    }

    public void setMeasurementRemarks(String measurementRemarks) {
        this.measurementRemarks = measurementRemarks;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(String measurementValue) {
        this.measurementValue = measurementValue;
    }

    public String getMinimumDepthInMeters() {
        return minimumDepthInMeters;
    }

    public void setMinimumDepthInMeters(String minimumDepthInMeters) {
        this.minimumDepthInMeters = minimumDepthInMeters;
    }

    public String getMinimumDepthInMeters_p() {
        return minimumDepthInMeters_p;
    }

    public void setMinimumDepthInMeters_p(String minimumDepthInMeters_p) {
        this.minimumDepthInMeters_p = minimumDepthInMeters_p;
    }

    public String getMinimumDistanceAboveSurfaceInMeters() {
        return minimumDistanceAboveSurfaceInMeters;
    }

    public void setMinimumDistanceAboveSurfaceInMeters(String minimumDistanceAboveSurfaceInMeters) {
        this.minimumDistanceAboveSurfaceInMeters = minimumDistanceAboveSurfaceInMeters;
    }

    public String getMinimumElevationInMeters() {
        return minimumElevationInMeters;
    }

    public void setMinimumElevationInMeters(String minimumElevationInMeters) {
        this.minimumElevationInMeters = minimumElevationInMeters;
    }

    public String getMinimumElevationInMeters_p() {
        return minimumElevationInMeters_p;
    }

    public void setMinimumElevationInMeters_p(String minimumElevationInMeters_p) {
        this.minimumElevationInMeters_p = minimumElevationInMeters_p;
    }

    public String getMiscProperties() {
        return miscProperties;
    }

    public void setMiscProperties(String miscProperties) {
        this.miscProperties = miscProperties;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getModified_p() {
        return modified_p;
    }

    public void setModified_p(String modified_p) {
        this.modified_p = modified_p;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMonth_p() {
        return month_p;
    }

    public void setMonth_p(String month_p) {
        this.month_p = month_p;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getMytest() {
        return mytest;
    }

    public void setMytest(String mytest) {
        this.mytest = mytest;
    }

    public String getNameAccordingTo() {
        return nameAccordingTo;
    }

    public void setNameAccordingTo(String nameAccordingTo) {
        this.nameAccordingTo = nameAccordingTo;
    }

    public String getNameAccordingToID() {
        return nameAccordingToID;
    }

    public void setNameAccordingToID(String nameAccordingToID) {
        this.nameAccordingToID = nameAccordingToID;
    }

    public String getNameAccordingTo_p() {
        return nameAccordingTo_p;
    }

    public void setNameAccordingTo_p(String nameAccordingTo_p) {
        this.nameAccordingTo_p = nameAccordingTo_p;
    }

    public String getNameMatchMetric() {
        return nameMatchMetric;
    }

    public void setNameMatchMetric(String nameMatchMetric) {
        this.nameMatchMetric = nameMatchMetric;
    }

    public String getNameMatchMetric_p() {
        return nameMatchMetric_p;
    }

    public void setNameMatchMetric_p(String nameMatchMetric_p) {
        this.nameMatchMetric_p = nameMatchMetric_p;
    }

    public String getNameParseType() {
        return nameParseType;
    }

    public void setNameParseType(String nameParseType) {
        this.nameParseType = nameParseType;
    }

    public String getNameParseType_p() {
        return nameParseType_p;
    }

    public void setNameParseType_p(String nameParseType_p) {
        this.nameParseType_p = nameParseType_p;
    }

    public String getNamePublishedIn() {
        return namePublishedIn;
    }

    public void setNamePublishedIn(String namePublishedIn) {
        this.namePublishedIn = namePublishedIn;
    }

    public String getNamePublishedInID() {
        return namePublishedInID;
    }

    public void setNamePublishedInID(String namePublishedInID) {
        this.namePublishedInID = namePublishedInID;
    }

    public String getNamePublishedInYear() {
        return namePublishedInYear;
    }

    public void setNamePublishedInYear(String namePublishedInYear) {
        this.namePublishedInYear = namePublishedInYear;
    }

    public String getNaturalOccurrence() {
        return naturalOccurrence;
    }

    public void setNaturalOccurrence(String naturalOccurrence) {
        this.naturalOccurrence = naturalOccurrence;
    }

    public String getNearNamedPlaceRelationTo() {
        return nearNamedPlaceRelationTo;
    }

    public void setNearNamedPlaceRelationTo(String nearNamedPlaceRelationTo) {
        this.nearNamedPlaceRelationTo = nearNamedPlaceRelationTo;
    }

    public String getNomenclaturalCode() {
        return nomenclaturalCode;
    }

    public void setNomenclaturalCode(String nomenclaturalCode) {
        this.nomenclaturalCode = nomenclaturalCode;
    }

    public String getNomenclaturalCode_p() {
        return nomenclaturalCode_p;
    }

    public void setNomenclaturalCode_p(String nomenclaturalCode_p) {
        this.nomenclaturalCode_p = nomenclaturalCode_p;
    }

    public String getNomenclaturalStatus() {
        return nomenclaturalStatus;
    }

    public void setNomenclaturalStatus(String nomenclaturalStatus) {
        this.nomenclaturalStatus = nomenclaturalStatus;
    }

    public String getNorthing() {
        return northing;
    }

    public void setNorthing(String northing) {
        this.northing = northing;
    }

    public String getNorthing_p() {
        return northing_p;
    }

    public void setNorthing_p(String northing_p) {
        this.northing_p = northing_p;
    }

    public String getOccurrenceAttributes() {
        return occurrenceAttributes;
    }

    public void setOccurrenceAttributes(String occurrenceAttributes) {
        this.occurrenceAttributes = occurrenceAttributes;
    }

    public String getOccurrenceDetails() {
        return occurrenceDetails;
    }

    public void setOccurrenceDetails(String occurrenceDetails) {
        this.occurrenceDetails = occurrenceDetails;
    }

    public String getOccurrenceID() {
        return occurrenceID;
    }

    public void setOccurrenceID(String occurrenceID) {
        this.occurrenceID = occurrenceID;
    }

    public String getOccurrenceRemarks() {
        return occurrenceRemarks;
    }

    public void setOccurrenceRemarks(String occurrenceRemarks) {
        this.occurrenceRemarks = occurrenceRemarks;
    }

    public String getOccurrenceStatus() {
        return occurrenceStatus;
    }

    public void setOccurrenceStatus(String occurrenceStatus) {
        this.occurrenceStatus = occurrenceStatus;
    }

    public String getOccurrenceStatus_p() {
        return occurrenceStatus_p;
    }

    public void setOccurrenceStatus_p(String occurrenceStatus_p) {
        this.occurrenceStatus_p = occurrenceStatus_p;
    }

    public String getOffline_qa() {
        return offline_qa;
    }

    public void setOffline_qa(String offline_qa) {
        this.offline_qa = offline_qa;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderID_p() {
        return orderID_p;
    }

    public void setOrderID_p(String orderID_p) {
        this.orderID_p = orderID_p;
    }

    public String getOrder_p() {
        return order_p;
    }

    public void setOrder_p(String order_p) {
        this.order_p = order_p;
    }

    public String getOrganismQuantity() {
        return organismQuantity;
    }

    public void setOrganismQuantity(String organismQuantity) {
        this.organismQuantity = organismQuantity;
    }

    public String getOrganismQuantityType() {
        return organismQuantityType;
    }

    public void setOrganismQuantityType(String organismQuantityType) {
        this.organismQuantityType = organismQuantityType;
    }

    public String getOriginalDecimalLatitude() {
        return originalDecimalLatitude;
    }

    public void setOriginalDecimalLatitude(String originalDecimalLatitude) {
        this.originalDecimalLatitude = originalDecimalLatitude;
    }

    public String getOriginalDecimalLongitude() {
        return originalDecimalLongitude;
    }

    public void setOriginalDecimalLongitude(String originalDecimalLongitude) {
        this.originalDecimalLongitude = originalDecimalLongitude;
    }

    public String getOriginalNameUsage() {
        return originalNameUsage;
    }

    public void setOriginalNameUsage(String originalNameUsage) {
        this.originalNameUsage = originalNameUsage;
    }

    public String getOriginalNameUsageID() {
        return originalNameUsageID;
    }

    public void setOriginalNameUsageID(String originalNameUsageID) {
        this.originalNameUsageID = originalNameUsageID;
    }

    public String getOriginalSensitiveValues() {
        return originalSensitiveValues;
    }

    public void setOriginalSensitiveValues(String originalSensitiveValues) {
        this.originalSensitiveValues = originalSensitiveValues;
    }

    public String getOtherCatalogNumbers() {
        return otherCatalogNumbers;
    }

    public void setOtherCatalogNumbers(String otherCatalogNumbers) {
        this.otherCatalogNumbers = otherCatalogNumbers;
    }

    public String getOutlierForLayers() {
        return outlierForLayers;
    }

    public void setOutlierForLayers(String outlierForLayers) {
        this.outlierForLayers = outlierForLayers;
    }

    public String getOutlierForLayers_p() {
        return outlierForLayers_p;
    }

    public void setOutlierForLayers_p(String outlierForLayers_p) {
        this.outlierForLayers_p = outlierForLayers_p;
    }

    public String getOwnerInstitutionCode() {
        return ownerInstitutionCode;
    }

    public void setOwnerInstitutionCode(String ownerInstitutionCode) {
        this.ownerInstitutionCode = ownerInstitutionCode;
    }

    public String getParentNameUsage() {
        return parentNameUsage;
    }

    public void setParentNameUsage(String parentNameUsage) {
        this.parentNameUsage = parentNameUsage;
    }

    public String getParentNameUsageID() {
        return parentNameUsageID;
    }

    public void setParentNameUsageID(String parentNameUsageID) {
        this.parentNameUsageID = parentNameUsageID;
    }

    public String getPhenology() {
        return phenology;
    }

    public void setPhenology(String phenology) {
        this.phenology = phenology;
    }

    public String getPhotoPageUrl() {
        return photoPageUrl;
    }

    public void setPhotoPageUrl(String photoPageUrl) {
        this.photoPageUrl = photoPageUrl;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getPhylum() {
        return phylum;
    }

    public void setPhylum(String phylum) {
        this.phylum = phylum;
    }

    public String getPhylumID() {
        return phylumID;
    }

    public void setPhylumID(String phylumID) {
        this.phylumID = phylumID;
    }

    public String getPhylumID_p() {
        return phylumID_p;
    }

    public void setPhylumID_p(String phylumID_p) {
        this.phylumID_p = phylumID_p;
    }

    public String getPhylum_p() {
        return phylum_p;
    }

    public void setPhylum_p(String phylum_p) {
        this.phylum_p = phylum_p;
    }

    public String getPointRadiusSpatialFit() {
        return pointRadiusSpatialFit;
    }

    public void setPointRadiusSpatialFit(String pointRadiusSpatialFit) {
        this.pointRadiusSpatialFit = pointRadiusSpatialFit;
    }

    public String getPortalId() {
        return portalId;
    }

    public void setPortalId(String portalId) {
        this.portalId = portalId;
    }

    public String getPreferredFlag() {
        return preferredFlag;
    }

    public void setPreferredFlag(String preferredFlag) {
        this.preferredFlag = preferredFlag;
    }

    public String getPreparations() {
        return preparations;
    }

    public void setPreparations(String preparations) {
        this.preparations = preparations;
    }

    public String getPreviousIdentifications() {
        return previousIdentifications;
    }

    public void setPreviousIdentifications(String previousIdentifications) {
        this.previousIdentifications = previousIdentifications;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getProvenance_p() {
        return provenance_p;
    }

    public void setProvenance_p(String provenance_p) {
        this.provenance_p = provenance_p;
    }

    public String getQualityAssertion() {
        return qualityAssertion;
    }

    public void setQualityAssertion(String qualityAssertion) {
        this.qualityAssertion = qualityAssertion;
    }

    public String getQueryAssertions_p() {
        return queryAssertions_p;
    }

    public void setQueryAssertions_p(String queryAssertions_p) {
        this.queryAssertions_p = queryAssertions_p;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    public String getRecordedBy_p() {
        return recordedBy_p;
    }

    public void setRecordedBy_p(String recordedBy_p) {
        this.recordedBy_p = recordedBy_p;
    }

    public String getRelatedResourceID() {
        return relatedResourceID;
    }

    public void setRelatedResourceID(String relatedResourceID) {
        this.relatedResourceID = relatedResourceID;
    }

    public String getRelationshipAccordingTo() {
        return relationshipAccordingTo;
    }

    public void setRelationshipAccordingTo(String relationshipAccordingTo) {
        this.relationshipAccordingTo = relationshipAccordingTo;
    }

    public String getRelationshipEstablishedDate() {
        return relationshipEstablishedDate;
    }

    public void setRelationshipEstablishedDate(String relationshipEstablishedDate) {
        this.relationshipEstablishedDate = relationshipEstablishedDate;
    }

    public String getRelationshipOfResource() {
        return relationshipOfResource;
    }

    public void setRelationshipOfResource(String relationshipOfResource) {
        this.relationshipOfResource = relationshipOfResource;
    }

    public String getRelationshipRemarks() {
        return relationshipRemarks;
    }

    public void setRelationshipRemarks(String relationshipRemarks) {
        this.relationshipRemarks = relationshipRemarks;
    }

    public String getReprocessing_qa() {
        return reprocessing_qa;
    }

    public void setReprocessing_qa(String reprocessing_qa) {
        this.reprocessing_qa = reprocessing_qa;
    }

    public String getReproductiveCondition() {
        return reproductiveCondition;
    }

    public void setReproductiveCondition(String reproductiveCondition) {
        this.reproductiveCondition = reproductiveCondition;
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public String getResourceRelationshipID() {
        return resourceRelationshipID;
    }

    public void setResourceRelationshipID(String resourceRelationshipID) {
        this.resourceRelationshipID = resourceRelationshipID;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getRight_p() {
        return right_p;
    }

    public void setRight_p(String right_p) {
        this.right_p = right_p;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getRightsholder() {
        return rightsholder;
    }

    public void setRightsholder(String rightsholder) {
        this.rightsholder = rightsholder;
    }

    public String getSamplingEffort() {
        return samplingEffort;
    }

    public void setSamplingEffort(String samplingEffort) {
        this.samplingEffort = samplingEffort;
    }

    public String getSamplingProtocol() {
        return samplingProtocol;
    }

    public void setSamplingProtocol(String samplingProtocol) {
        this.samplingProtocol = samplingProtocol;
    }

    public String getSamplingProtocol_p() {
        return samplingProtocol_p;
    }

    public void setSamplingProtocol_p(String samplingProtocol_p) {
        this.samplingProtocol_p = samplingProtocol_p;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getScientificNameAddendum() {
        return scientificNameAddendum;
    }

    public void setScientificNameAddendum(String scientificNameAddendum) {
        this.scientificNameAddendum = scientificNameAddendum;
    }

    public String getScientificNameAuthorship() {
        return scientificNameAuthorship;
    }

    public void setScientificNameAuthorship(String scientificNameAuthorship) {
        this.scientificNameAuthorship = scientificNameAuthorship;
    }

    public String getScientificNameAuthorship_p() {
        return scientificNameAuthorship_p;
    }

    public void setScientificNameAuthorship_p(String scientificNameAuthorship_p) {
        this.scientificNameAuthorship_p = scientificNameAuthorship_p;
    }

    public String getScientificNameID() {
        return scientificNameID;
    }

    public void setScientificNameID(String scientificNameID) {
        this.scientificNameID = scientificNameID;
    }

    public String getScientificNameWithoutAuthor() {
        return scientificNameWithoutAuthor;
    }

    public void setScientificNameWithoutAuthor(String scientificNameWithoutAuthor) {
        this.scientificNameWithoutAuthor = scientificNameWithoutAuthor;
    }

    public String getScientificName_p() {
        return scientificName_p;
    }

    public void setScientificName_p(String scientificName_p) {
        this.scientificName_p = scientificName_p;
    }

    public String getSecondaryCollectors() {
        return secondaryCollectors;
    }

    public void setSecondaryCollectors(String secondaryCollectors) {
        this.secondaryCollectors = secondaryCollectors;
    }

    public String getSensitive_qa() {
        return sensitive_qa;
    }

    public void setSensitive_qa(String sensitive_qa) {
        this.sensitive_qa = sensitive_qa;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSounds() {
        return sounds;
    }

    public void setSounds(String sounds) {
        this.sounds = sounds;
    }

    public String getSounds_p() {
        return sounds_p;
    }

    public void setSounds_p(String sounds_p) {
        this.sounds_p = sounds_p;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSpeciesGroups() {
        return speciesGroups;
    }

    public void setSpeciesGroups(String speciesGroups) {
        this.speciesGroups = speciesGroups;
    }

    public String getSpeciesGroups_p() {
        return speciesGroups_p;
    }

    public void setSpeciesGroups_p(String speciesGroups_p) {
        this.speciesGroups_p = speciesGroups_p;
    }

    public String getSpeciesHabitats() {
        return speciesHabitats;
    }

    public void setSpeciesHabitats(String speciesHabitats) {
        this.speciesHabitats = speciesHabitats;
    }

    public String getSpeciesHabitats_p() {
        return speciesHabitats_p;
    }

    public void setSpeciesHabitats_p(String speciesHabitats_p) {
        this.speciesHabitats_p = speciesHabitats_p;
    }

    public String getSpeciesID() {
        return speciesID;
    }

    public void setSpeciesID(String speciesID) {
        this.speciesID = speciesID;
    }

    public String getSpeciesID_p() {
        return speciesID_p;
    }

    public void setSpeciesID_p(String speciesID_p) {
        this.speciesID_p = speciesID_p;
    }

    public String getSpecies_p() {
        return species_p;
    }

    public void setSpecies_p(String species_p) {
        this.species_p = species_p;
    }

    public String getSpecificEpithet() {
        return specificEpithet;
    }

    public void setSpecificEpithet(String specificEpithet) {
        this.specificEpithet = specificEpithet;
    }

    public String getSpecificEpithet_p() {
        return specificEpithet_p;
    }

    public void setSpecificEpithet_p(String specificEpithet_p) {
        this.specificEpithet_p = specificEpithet_p;
    }

    public String getStartDayOfYear() {
        return startDayOfYear;
    }

    public void setStartDayOfYear(String startDayOfYear) {
        this.startDayOfYear = startDayOfYear;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateConservation() {
        return stateConservation;
    }

    public void setStateConservation(String stateConservation) {
        this.stateConservation = stateConservation;
    }

    public String getStateConservation_p() {
        return stateConservation_p;
    }

    public void setStateConservation_p(String stateConservation_p) {
        this.stateConservation_p = stateConservation_p;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getStateProvince_p() {
        return stateProvince_p;
    }

    public void setStateProvince_p(String stateProvince_p) {
        this.stateProvince_p = stateProvince_p;
    }

    public String getSubfamily() {
        return subfamily;
    }

    public void setSubfamily(String subfamily) {
        this.subfamily = subfamily;
    }

    public String getSubgenus() {
        return subgenus;
    }

    public void setSubgenus(String subgenus) {
        this.subgenus = subgenus;
    }

    public String getSubgenusID() {
        return subgenusID;
    }

    public void setSubgenusID(String subgenusID) {
        this.subgenusID = subgenusID;
    }

    public String getSubspecies() {
        return subspecies;
    }

    public void setSubspecies(String subspecies) {
        this.subspecies = subspecies;
    }

    public String getSubspeciesID() {
        return subspeciesID;
    }

    public void setSubspeciesID(String subspeciesID) {
        this.subspeciesID = subspeciesID;
    }

    public String getSubspeciesID_p() {
        return subspeciesID_p;
    }

    public void setSubspeciesID_p(String subspeciesID_p) {
        this.subspeciesID_p = subspeciesID_p;
    }

    public String getSubspecies_p() {
        return subspecies_p;
    }

    public void setSubspecies_p(String subspecies_p) {
        this.subspecies_p = subspecies_p;
    }

    public String getSuperfamily() {
        return superfamily;
    }

    public void setSuperfamily(String superfamily) {
        this.superfamily = superfamily;
    }

    public String getTaxonConceptID() {
        return taxonConceptID;
    }

    public void setTaxonConceptID(String taxonConceptID) {
        this.taxonConceptID = taxonConceptID;
    }

    public String getTaxonConceptID_p() {
        return taxonConceptID_p;
    }

    public void setTaxonConceptID_p(String taxonConceptID_p) {
        this.taxonConceptID_p = taxonConceptID_p;
    }

    public String getTaxonID() {
        return taxonID;
    }

    public void setTaxonID(String taxonID) {
        this.taxonID = taxonID;
    }

    public String getTaxonRank() {
        return taxonRank;
    }

    public void setTaxonRank(String taxonRank) {
        this.taxonRank = taxonRank;
    }

    public String getTaxonRankID() {
        return taxonRankID;
    }

    public void setTaxonRankID(String taxonRankID) {
        this.taxonRankID = taxonRankID;
    }

    public String getTaxonRankID_p() {
        return taxonRankID_p;
    }

    public void setTaxonRankID_p(String taxonRankID_p) {
        this.taxonRankID_p = taxonRankID_p;
    }

    public String getTaxonRank_p() {
        return taxonRank_p;
    }

    public void setTaxonRank_p(String taxonRank_p) {
        this.taxonRank_p = taxonRank_p;
    }

    public String getTaxonRemarks() {
        return taxonRemarks;
    }

    public void setTaxonRemarks(String taxonRemarks) {
        this.taxonRemarks = taxonRemarks;
    }

    public String getTaxonomicIssue() {
        return taxonomicIssue;
    }

    public void setTaxonomicIssue(String taxonomicIssue) {
        this.taxonomicIssue = taxonomicIssue;
    }

    public String getTaxonomicIssue_p() {
        return taxonomicIssue_p;
    }

    public void setTaxonomicIssue_p(String taxonomicIssue_p) {
        this.taxonomicIssue_p = taxonomicIssue_p;
    }

    public String getTaxonomicStatus() {
        return taxonomicStatus;
    }

    public void setTaxonomicStatus(String taxonomicStatus) {
        this.taxonomicStatus = taxonomicStatus;
    }

    public String getTaxonomicallyKosher() {
        return taxonomicallyKosher;
    }

    public void setTaxonomicallyKosher(String taxonomicallyKosher) {
        this.taxonomicallyKosher = taxonomicallyKosher;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeStatus() {
        return typeStatus;
    }

    public void setTypeStatus(String typeStatus) {
        this.typeStatus = typeStatus;
    }

    public String getTypeStatusQualifier() {
        return typeStatusQualifier;
    }

    public void setTypeStatusQualifier(String typeStatusQualifier) {
        this.typeStatusQualifier = typeStatusQualifier;
    }

    public String getTypeStatus_p() {
        return typeStatus_p;
    }

    public void setTypeStatus_p(String typeStatus_p) {
        this.typeStatus_p = typeStatus_p;
    }

    public String getType_qa() {
        return type_qa;
    }

    public void setType_qa(String type_qa) {
        this.type_qa = type_qa;
    }

    public String getTypifiedName() {
        return typifiedName;
    }

    public void setTypifiedName(String typifiedName) {
        this.typifiedName = typifiedName;
    }

    public String getUserAssertionStatus() {
        return userAssertionStatus;
    }

    public void setUserAssertionStatus(String userAssertionStatus) {
        this.userAssertionStatus = userAssertionStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId_p() {
        return userId_p;
    }

    public void setUserId_p(String userId_p) {
        this.userId_p = userId_p;
    }

    public String getUserQualityAssertion() {
        return userQualityAssertion;
    }

    public void setUserQualityAssertion(String userQualityAssertion) {
        this.userQualityAssertion = userQualityAssertion;
    }

    public String getUserVerified() {
        return userVerified;
    }

    public void setUserVerified(String userVerified) {
        this.userVerified = userVerified;
    }

    public String getValidDistribution() {
        return validDistribution;
    }

    public void setValidDistribution(String validDistribution) {
        this.validDistribution = validDistribution;
    }

    public String getVerbatimCoordinateSystem() {
        return verbatimCoordinateSystem;
    }

    public void setVerbatimCoordinateSystem(String verbatimCoordinateSystem) {
        this.verbatimCoordinateSystem = verbatimCoordinateSystem;
    }

    public String getVerbatimCoordinates() {
        return verbatimCoordinates;
    }

    public void setVerbatimCoordinates(String verbatimCoordinates) {
        this.verbatimCoordinates = verbatimCoordinates;
    }

    public String getVerbatimDateIdentified() {
        return verbatimDateIdentified;
    }

    public void setVerbatimDateIdentified(String verbatimDateIdentified) {
        this.verbatimDateIdentified = verbatimDateIdentified;
    }

    public String getVerbatimDepth() {
        return verbatimDepth;
    }

    public void setVerbatimDepth(String verbatimDepth) {
        this.verbatimDepth = verbatimDepth;
    }

    public String getVerbatimDepth_p() {
        return verbatimDepth_p;
    }

    public void setVerbatimDepth_p(String verbatimDepth_p) {
        this.verbatimDepth_p = verbatimDepth_p;
    }

    public String getVerbatimElevation() {
        return verbatimElevation;
    }

    public void setVerbatimElevation(String verbatimElevation) {
        this.verbatimElevation = verbatimElevation;
    }

    public String getVerbatimElevation_p() {
        return verbatimElevation_p;
    }

    public void setVerbatimElevation_p(String verbatimElevation_p) {
        this.verbatimElevation_p = verbatimElevation_p;
    }

    public String getVerbatimEventDate() {
        return verbatimEventDate;
    }

    public void setVerbatimEventDate(String verbatimEventDate) {
        this.verbatimEventDate = verbatimEventDate;
    }

    public String getVerbatimLatitude() {
        return verbatimLatitude;
    }

    public void setVerbatimLatitude(String verbatimLatitude) {
        this.verbatimLatitude = verbatimLatitude;
    }

    public String getVerbatimLocality() {
        return verbatimLocality;
    }

    public void setVerbatimLocality(String verbatimLocality) {
        this.verbatimLocality = verbatimLocality;
    }

    public String getVerbatimLongitude() {
        return verbatimLongitude;
    }

    public void setVerbatimLongitude(String verbatimLongitude) {
        this.verbatimLongitude = verbatimLongitude;
    }

    public String getVerbatimSRS() {
        return verbatimSRS;
    }

    public void setVerbatimSRS(String verbatimSRS) {
        this.verbatimSRS = verbatimSRS;
    }

    public String getVerbatimTaxonRank() {
        return verbatimTaxonRank;
    }

    public void setVerbatimTaxonRank(String verbatimTaxonRank) {
        this.verbatimTaxonRank = verbatimTaxonRank;
    }

    public String getVerbatimTaxonRank_p() {
        return verbatimTaxonRank_p;
    }

    public void setVerbatimTaxonRank_p(String verbatimTaxonRank_p) {
        this.verbatimTaxonRank_p = verbatimTaxonRank_p;
    }

    public String getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(String verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getVernacularName() {
        return vernacularName;
    }

    public void setVernacularName(String vernacularName) {
        this.vernacularName = vernacularName;
    }

    public String getVernacularName_p() {
        return vernacularName_p;
    }

    public void setVernacularName_p(String vernacularName_p) {
        this.vernacularName_p = vernacularName_p;
    }

    public String getVideos() {
        return videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }

    public String getVideos_p() {
        return videos_p;
    }

    public void setVideos_p(String videos_p) {
        this.videos_p = videos_p;
    }

    public String getWaterBody() {
        return waterBody;
    }

    public void setWaterBody(String waterBody) {
        this.waterBody = waterBody;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear_p() {
        return year_p;
    }

    public void setYear_p(String year_p) {
        this.year_p = year_p;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    final public static List<String> fieldNames() {
        return Arrays.asList("acceptedNameUsage",
                "acceptedNameUsageID",
                "accessRights",
                "associatedMedia",
                "associatedOccurrences",
                "associatedReferences",
                "associatedSequences",
                "associatedTaxa",
                "basisOfRecord",
                "behavior",
                "bibliographicCitation",
                "catalogNumber",
                "class",
                "collectionCode",
                "collectionID",
                "continent",
                "coordinatePrecision",
                "coordinateUncertaintyInMeters",
                "country",
                "countryCode",
                "county",
                "dataGeneralizations",
                "datasetID",
                "datasetName",
                "dateIdentified",
                "day",
                "decimalLatitude",
                "decimalLongitude",
                "disposition",
                "dynamicProperties",
                "endDayOfYear",
                "establishmentMeans",
                "eventAttributes",
                "eventDate",
                "eventID",
                "eventRemarks",
                "eventTime",
                "family",
                "fieldNotes",
                "fieldNumber",
                "footprintSpatialFit",
                "footprintSRS",
                "footprintWKT",
                "genus",
                "geodeticDatum",
                "georeferencedBy",
                "georeferencedDate",
                "georeferenceProtocol",
                "georeferenceRemarks",
                "georeferenceSources",
                "georeferenceVerificationStatus",
                "habitat",
                "higherClassification",
                "higherGeography",
                "higherGeographyID",
                "identificationID",
                "identificationQualifier",
                "identificationReferences",
                "identificationRemarks",
                "identificationVerificationStatus",
                "identifiedBy",
                "individualCount",
                "individualID",
                "informationWithheld",
                "infraspecificEpithet",
                "institutionCode",
                "institutionID",
                "island",
                "islandGroup",
                "kingdom",
                "language",
                "license",
                "lifeStage",
                "locality",
                "locationAccordingTo",
                "locationAttributes",
                "locationID",
                "locationRemarks",
                "maximumDepthInMeters",
                "maximumDistanceAboveSurfaceInMeters",
                "maximumElevationInMeters",
                "measurementAccuracy",
                "measurementDeterminedBy",
                "measurementDeterminedDate",
                "measurementID",
                "measurementMethod",
                "measurementRemarks",
                "measurementType",
                "measurementUnit",
                "measurementValue",
                "minimumDepthInMeters",
                "minimumDistanceAboveSurfaceInMeters",
                "minimumElevationInMeters",
                "modified",
                "month",
                "municipality",
                "nameAccordingTo",
                "nameAccordingToID",
                "namePublishedIn",
                "namePublishedInID",
                "namePublishedInYear",
                "nomenclaturalCode",
                "nomenclaturalStatus",
                "occurrenceAttributes",
                "occurrenceDetails",
                "occurrenceID",
                "occurrenceRemarks",
                "occurrenceStatus",
                "order",
                "organismQuantity",
                "organismQuantityType",
                "originalNameUsage",
                "originalNameUsageID",
                "otherCatalogNumbers",
                "ownerInstitutionCode",
                "parentNameUsage",
                "parentNameUsageID",
                "phylum",
                "pointRadiusSpatialFit",
                "preparations",
                "previousIdentifications",
                "recordedBy",
                "recordNumber",
                "relatedResourceID",
                "relationshipAccordingTo",
                "relationshipEstablishedDate",
                "relationshipOfResource",
                "relationshipRemarks",
                "reproductiveCondition",
                "resourceID",
                "resourceRelationshipID",
                "rightsholder",
                "samplingEffort",
                "samplingProtocol",
                "scientificName",
                "scientificNameAuthorship",
                "scientificNameID",
                "sex",
                "specificEpithet",
                "startDayOfYear",
                "stateProvince",
                "subgenus",
                "taxonConceptID",
                "taxonID",
                "taxonomicStatus",
                "taxonRank",
                "taxonRemarks",
                "type",
                "typeStatus",
                "verbatimCoordinates",
                "verbatimCoordinateSystem",
                "verbatimDepth",
                "verbatimElevation",
                "verbatimEventDate",
                "verbatimLatitude",
                "verbatimLocality",
                "verbatimLongitude",
                "verbatimSRS",
                "verbatimTaxonRank",
                "vernacularName",
                "waterBody",
                "year");
    }

    public  Map<String, String> mapDwca() {
        Map<String, String> rec = new HashMap<>();
        rec.put("acceptedNameUsage", Objects.toString(this.getAcceptedNameUsage(),""));
        rec.put("acceptedNameUsageID", Objects.toString(this.getAcceptedNameUsageID(),""));
        rec.put("accessRights", Objects.toString(this.getAccessRights(),""));
        rec.put("associatedMedia", Objects.toString(this.getAssociatedMedia(),""));
        rec.put("associatedOccurrences", Objects.toString(this.getAssociatedOccurrences(),""));
        rec.put("associatedReferences", Objects.toString(this.getAssociatedReferences(),""));
        rec.put("associatedSequences", Objects.toString(this.getAssociatedSequences(),""));
        rec.put("associatedTaxa", Objects.toString(this.getAssociatedTaxa(),""));
        rec.put("basisOfRecord", Objects.toString(this.getBasisOfRecord(),""));
        rec.put("behavior", Objects.toString(this.getBehavior(),""));
        rec.put("bibliographicCitation", Objects.toString(this.getBibliographicCitation(),""));
        rec.put("catalogNumber", Objects.toString(this.getCatalogNumber(),""));
        if (this.getClassField().isEmpty()) {
            if (this.getClasss().isEmpty()) {
                rec.put("class", Objects.toString(this.get_class(),""));
            } else {
                rec.put("class", Objects.toString(this.getClasss(),""));
            }
        } else {
            rec.put("class", Objects.toString(this.getClassField(),""));
        }
        rec.put("collectionCode", Objects.toString(this.getCollectionCode(),""));
        rec.put("collectionID", Objects.toString(this.getCollectionID(),""));
        rec.put("continent", Objects.toString(this.getContinent(),""));
        rec.put("coordinatePrecision", Objects.toString(this.getCoordinatePrecision(),""));
        rec.put("coordinateUncertaintyInMeters", Objects.toString(this.getCoordinateUncertaintyInMeters(),""));
        rec.put("country", Objects.toString(this.getCountry(),""));
        rec.put("countryCode", Objects.toString(this.getCountryCode(),""));
        rec.put("county", Objects.toString(this.getCounty(),""));
        rec.put("dataGeneralizations", Objects.toString(this.getDataGeneralizations(),""));
        rec.put("datasetID", Objects.toString(this.getDatasetID(),""));
        rec.put("datasetName", Objects.toString(this.getDatasetName(),""));
        rec.put("dateIdentified", Objects.toString(this.getDateIdentified(),""));
        rec.put("day", Objects.toString(this.getDay(),""));
        rec.put("decimalLatitude", Objects.toString(this.getDecimalLatitude(),""));
        rec.put("decimalLongitude", Objects.toString(this.getDecimalLongitude(),""));
        rec.put("disposition", Objects.toString(this.getDisposition(),""));
        rec.put("dynamicProperties", Objects.toString(this.getDynamicProperties(),""));
        rec.put("endDayOfYear", Objects.toString(this.getEndDayOfYear(),""));
        rec.put("establishmentMeans", Objects.toString(this.getEstablishmentMeans(),""));
        rec.put("eventAttributes", Objects.toString(this.getEventAttributes(),""));
        rec.put("eventDate", Objects.toString(this.getEventDate(),""));
        rec.put("eventID", Objects.toString(this.getEventID(),""));
        rec.put("eventRemarks", Objects.toString(this.getEventRemarks(),""));
        rec.put("eventTime", Objects.toString(this.getEventTime(),""));
        rec.put("family", Objects.toString(this.getFamily(),""));
        rec.put("fieldNotes", Objects.toString(this.getFieldNotes(),""));
        rec.put("fieldNumber", Objects.toString(this.getFieldNumber(),""));
        rec.put("footprintSpatialFit", Objects.toString(this.getFootprintSpatialFit(),""));
        rec.put("footprintSRS", Objects.toString(this.getFootprintSRS(),""));
        rec.put("footprintWKT", Objects.toString(this.getFootprintWKT(),""));
        rec.put("genus", Objects.toString(this.getGenus(),""));
        rec.put("geodeticDatum", Objects.toString(this.getGeodeticDatum(),""));
        rec.put("georeferencedBy", Objects.toString(this.getGeoreferencedBy(),""));
        rec.put("georeferencedDate", Objects.toString(this.getGeoreferencedDate(),""));
        rec.put("georeferenceProtocol", Objects.toString(this.getGeoreferenceProtocol(),""));
        rec.put("georeferenceRemarks", Objects.toString(this.getGeoreferenceRemarks(),""));
        rec.put("georeferenceSources", Objects.toString(this.getGeoreferenceSources(),""));
        rec.put("georeferenceVerificationStatus", Objects.toString(this.getGeoreferenceVerificationStatus(),""));
        rec.put("habitat", Objects.toString(this.getHabitat(),""));
        rec.put("higherClassification", Objects.toString(this.getHigherClassification(),""));
        rec.put("higherGeography", Objects.toString(this.getHigherGeography(),""));
        rec.put("higherGeographyID", Objects.toString(this.getHigherGeographyID(),""));
        rec.put("identificationID", Objects.toString(this.getIdentificationID(),""));
        rec.put("identificationQualifier", Objects.toString(this.getIdentificationQualifier(),""));
        rec.put("identificationReferences", Objects.toString(this.getIdentificationReferences(),""));
        rec.put("identificationRemarks", Objects.toString(this.getIdentificationRemarks(),""));
        rec.put("identificationVerificationStatus", Objects.toString(this.getIdentificationVerificationStatus(),""));
        rec.put("identifiedBy", Objects.toString(this.getIdentifiedBy(),""));
        rec.put("individualCount", Objects.toString(this.getIndividualCount(),""));
        rec.put("individualID", Objects.toString(this.getIndividualID(),""));
        rec.put("informationWithheld", Objects.toString(this.getInformationWithheld(),""));
        rec.put("infraspecificEpithet", Objects.toString(this.getInfraspecificEpithet(),""));
        rec.put("institutionCode", Objects.toString(this.getInstitutionCode(),""));
        rec.put("institutionID", Objects.toString(this.getInstitutionID(),""));
        rec.put("island", Objects.toString(this.getIsland(),""));
        rec.put("islandGroup", Objects.toString(this.getIslandGroup(),""));
        rec.put("kingdom", Objects.toString(this.getKingdom(),""));
        rec.put("language", Objects.toString(this.getLanguage(),""));
        rec.put("license", Objects.toString(this.getLicense(),""));
        rec.put("lifeStage", Objects.toString(this.getLifeStage(),""));
        rec.put("locality", Objects.toString(this.getLocality(),""));
        rec.put("locationAccordingTo", Objects.toString(this.getLocationAccordingTo(),""));
        rec.put("locationAttributes", Objects.toString(this.getLocationAttributes(),""));
        rec.put("locationID", Objects.toString(this.getLocationID(),""));
        rec.put("locationRemarks", Objects.toString(this.getLocationRemarks(),""));
        rec.put("maximumDepthInMeters", Objects.toString(this.getMaximumDepthInMeters(),""));
        rec.put("maximumDistanceAboveSurfaceInMeters", Objects.toString(this.getMaximumDistanceAboveSurfaceInMeters(),""));
        rec.put("maximumElevationInMeters", Objects.toString(this.getMaximumElevationInMeters(),""));
        rec.put("measurementAccuracy", Objects.toString(this.getMeasurementAccuracy(),""));
        rec.put("measurementDeterminedBy", Objects.toString(this.getMeasurementDeterminedBy(),""));
        rec.put("measurementDeterminedDate", Objects.toString(this.getMeasurementDeterminedDate(),""));
        rec.put("measurementID", Objects.toString(this.getMeasurementID(),""));
        rec.put("measurementMethod", Objects.toString(this.getMeasurementMethod(),""));
        rec.put("measurementRemarks", Objects.toString(this.getMeasurementRemarks(),""));
        rec.put("measurementType", Objects.toString(this.getMeasurementType(),""));
        rec.put("measurementUnit", Objects.toString(this.getMeasurementUnit(),""));
        rec.put("measurementValue", Objects.toString(this.getMeasurementValue(),""));
        rec.put("minimumDepthInMeters", Objects.toString(this.getMinimumDepthInMeters(),""));
        rec.put("minimumDistanceAboveSurfaceInMeters", Objects.toString(this.getMinimumDistanceAboveSurfaceInMeters(),""));
        rec.put("minimumElevationInMeters", Objects.toString(this.getMinimumElevationInMeters(),""));
        rec.put("modified", Objects.toString(this.getModified(),""));
        rec.put("month", Objects.toString(this.getMonth(),""));
        rec.put("municipality", Objects.toString(this.getMunicipality(),""));
        rec.put("nameAccordingTo", Objects.toString(this.getNameAccordingTo(),""));
        rec.put("nameAccordingToID", Objects.toString(this.getNameAccordingToID(),""));
        rec.put("namePublishedIn", Objects.toString(this.getNamePublishedIn(),""));
        rec.put("namePublishedInID", Objects.toString(this.getNamePublishedInID(),""));
        rec.put("namePublishedInYear", Objects.toString(this.getNamePublishedInYear(),""));
        rec.put("nomenclaturalCode", Objects.toString(this.getNomenclaturalCode(),""));
        rec.put("nomenclaturalStatus", Objects.toString(this.getNomenclaturalStatus(),""));
        rec.put("occurrenceAttributes", Objects.toString(this.getOccurrenceAttributes(),""));
        rec.put("occurrenceDetails", Objects.toString(this.getOccurrenceDetails(),""));
        rec.put("occurrenceID", Objects.toString(this.getOccurrenceID(),""));
        rec.put("occurrenceRemarks", Objects.toString(this.getOccurrenceRemarks(),""));
        rec.put("occurrenceStatus", Objects.toString(this.getOccurrenceStatus(),""));
        rec.put("order", Objects.toString(this.getOrder(),""));
        rec.put("organismQuantity", Objects.toString(this.getOrganismQuantity(),""));
        rec.put("organismQuantityType", Objects.toString(this.getOrganismQuantityType(),""));
        rec.put("originalNameUsage", Objects.toString(this.getOriginalNameUsage(),""));
        rec.put("originalNameUsageID", Objects.toString(this.getOriginalNameUsageID(),""));
        rec.put("otherCatalogNumbers", Objects.toString(this.getOtherCatalogNumbers(),""));
        rec.put("ownerInstitutionCode", Objects.toString(this.getOwnerInstitutionCode(),""));
        rec.put("parentNameUsage", Objects.toString(this.getParentNameUsage(),""));
        rec.put("parentNameUsageID", Objects.toString(this.getParentNameUsageID(),""));
        rec.put("phylum", Objects.toString(this.getPhylum(),""));
        rec.put("pointRadiusSpatialFit", Objects.toString(this.getPointRadiusSpatialFit(),""));
        rec.put("preparations", Objects.toString(this.getPreparations(),""));
        rec.put("previousIdentifications", Objects.toString(this.getPreviousIdentifications(),""));
        rec.put("recordedBy", Objects.toString(this.getRecordedBy(),""));
        rec.put("recordNumber", Objects.toString(this.getRecordNumber(),""));
        rec.put("relatedResourceID", Objects.toString(this.getRelatedResourceID(),""));
        rec.put("relationshipAccordingTo", Objects.toString(this.getRelationshipAccordingTo(),""));
        rec.put("relationshipEstablishedDate", Objects.toString(this.getRelationshipEstablishedDate(),""));
        rec.put("relationshipOfResource", Objects.toString(this.getRelationshipOfResource(),""));
        rec.put("relationshipRemarks", Objects.toString(this.getRelationshipRemarks(),""));
        rec.put("reproductiveCondition", Objects.toString(this.getReproductiveCondition(),""));
        rec.put("resourceID", Objects.toString(this.getResourceID(),""));
        rec.put("resourceRelationshipID", Objects.toString(this.getResourceRelationshipID(),""));
        rec.put("rightsholder", Objects.toString(this.getRightsholder(),""));
        rec.put("samplingEffort", Objects.toString(this.getSamplingEffort(),""));
        rec.put("samplingProtocol", Objects.toString(this.getSamplingProtocol(),""));
        rec.put("scientificName", Objects.toString(this.getScientificName(),""));
        rec.put("scientificNameAuthorship", Objects.toString(this.getScientificNameAuthorship(),""));
        rec.put("scientificNameID", Objects.toString(this.getScientificNameID(),""));
        rec.put("sex", Objects.toString(this.getSex(),""));
        rec.put("specificEpithet", Objects.toString(this.getSpecificEpithet(),""));
        rec.put("startDayOfYear", Objects.toString(this.getStartDayOfYear(),""));
        rec.put("stateProvince", Objects.toString(this.getStateProvince(),""));
        rec.put("subgenus", Objects.toString(this.getSubgenus(),""));
        rec.put("taxonConceptID", Objects.toString(this.getTaxonConceptID(),""));
        rec.put("taxonID", Objects.toString(this.getTaxonID(),""));
        rec.put("taxonomicStatus", Objects.toString(this.getTaxonomicStatus(),""));
        rec.put("taxonRank", Objects.toString(this.getTaxonRank(),""));
        rec.put("taxonRemarks", Objects.toString(this.getTaxonRemarks(),""));
        rec.put("type", Objects.toString(this.getType(),""));
        rec.put("typeStatus", Objects.toString(this.getTypeStatus(),""));
        rec.put("verbatimCoordinates", Objects.toString(this.getVerbatimCoordinates(),""));
        rec.put("verbatimCoordinateSystem", Objects.toString(this.getVerbatimCoordinateSystem(),""));
        rec.put("verbatimDepth", Objects.toString(this.getVerbatimDepth(),""));
        rec.put("verbatimElevation", Objects.toString(this.getVerbatimElevation(),""));
        rec.put("verbatimEventDate", Objects.toString(this.getVerbatimEventDate(),""));
        rec.put("verbatimLatitude", Objects.toString(this.getVerbatimLatitude(),""));
        rec.put("verbatimLocality", Objects.toString(this.getVerbatimLocality(),""));
        rec.put("verbatimLongitude", Objects.toString(this.getVerbatimLongitude(),""));
        rec.put("verbatimSRS", Objects.toString(this.getVerbatimSRS(),""));
        rec.put("verbatimTaxonRank", Objects.toString(this.getVerbatimTaxonRank(),""));
        rec.put("vernacularName", Objects.toString(this.getVernacularName(),""));
        rec.put("waterBody", Objects.toString(this.getWaterBody(),""));
        rec.put("year", Objects.toString(this.getYear(),""));
        return rec;

    }
    public CassandraOccurrence returnItself(){
        return this;
    }
}
