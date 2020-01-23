# Living Atlas Pipelines extensions

This project is **proof of concept quality code** aimed at identifying work required
 to use of pipelines as a replacement to [biocache-store](https://github.com/AtlasOfLivingAustralia/biocache-store)
 for data ingress. 

## Architecture

For details on the GBIF implementation, see the [pipelines github repository](https://github.com/gbif/pipelines).
This project is focussed on extension to that architecture to support use by the living atlases.

![Pipelines](https://docs.google.com/drawings/d/e/2PACX-1vQhQSg5VFo2xRZfDhmvhKuNLUpyTOlW-t-m1fesJ2RElWorVPAEbnsZg_StJKh22mEcS4D28j_nPoTV/pub?w=960&h=720 "Pipelines") 

Above is a representation of the data flow from source data in Darwin core archives supplied by data providers, to the API access to these data via the biocache-service component.

Within the "Interpreted AVRO" box is a list of "transforms" each of which take the source data and produce an isolated output in a AVRO formatted file.

GBIF already supports a number of core transforms for handling biodiversity occurrence data. The intention is to make us of these transforms "as-is" which effectively perform the very similar functionality to what is supported by the biocache-store project (ALA's current ingress library for occurrence data). 

This list of transforms will need to be added to backfill some of the ingress requirements of the ALA. These transforms will make use of existing ALA services:

* ALA Taxonomy transform - will make use of the existing **[ala-name-matching](https://github.com/AtlasOfLivingAustralia/ala-name-matching) library**
* Sensitive data - will make use of existing services in https://lists.ala.org.au to retrieve sensitive species rules.
* Spatial layers - will make use of existing services in https://spatial.ala.org.au/ws/ to retrieve sampled environmental and contextual values for geospatial points
* Species lists - will make use of existing services in https://lists.ala.org.au to retrieve species lists.

## Prototyped so far:

1. Pipeline extension to add the ALA taxonomy to the interpreted data
2. Extension with Sampling information (Environmental & Contextual)
3. Generation of search SOLR index compatible with biocache-service


## To be done:

1. Sensible use of GBIF's key/value store framework (backend storage to be identified)
2. Dealing with sensitive data
3. Integration with Collectory - ALA's current production metadata registry
4. Integration with Lists tool
5. Extensions with separate taxonomies e.g. NZOR
6. Handling of images with ALA's image-service as storage


## Dependent projects

### biocache-service
[experimental/pipelines branch](https://github.com/AtlasOfLivingAustralia/biocache-service/tree/experimental/pipelines) 
The aim for this proof of concept is to make very minimal changes to biocache-service, maintain the existing API and have no impact on existing services and applications.

### ala-namematching-service
So far a simple **drop wizard wrapper around the [ala-name-matching](https://github.com/AtlasOfLivingAustralia/ala-name-matching) library** has been prototyped to support integration with pipelines.
 
### spatial-service
[intersect-cache branch](https://github.com/AtlasOfLivingAustralia/spatial-service/tree/intersect-cache) An additional webservice to allow a point
lookup for all layers. This needs to be supplement with additional work to populate a key value store cache using the spatial-service batch API.