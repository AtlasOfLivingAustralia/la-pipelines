# Living Atlas Pipelines extensions

This project is **proof of concept quality code** aimed at identifying work required
 to use of pipelines as a replacement to [biocache-store](https://github.com/AtlasOfLivingAustralia/biocache-store)
 for data ingress. 

## Architecture

For details on the GBIF implementation, see the [pipelines github repository](https://github.ocom/gbif/pipelines).
This project is focussed on extension to that architecture to support use by the living atlases.

![Pipelines](https://docs.google.com/drawings/d/e/2PACX-1vQhQSg5VFo2xRZfDhmvhKuNLUpyTOlW-t-m1fesJ2RElWorVPAEbnsZg_StJKh22mEcS4D28j_nPoTV/pub?w=960&h=720 "Pipelines") 


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
biocache-service - [experimental/pipelines branch](https://github.com/AtlasOfLivingAustralia/biocache-service/tree/experimental/pipelines) 
The aim for this proof of concept is to make very minimal changes to biocache-service, maintain the existing API and have no impact on existing services and applications.

### ala-namematching-service
So far a simple **drop wizard wrapper around the [ala-name-matching](https://github.com/AtlasOfLivingAustralia/ala-name-matching) library** has been prototyped to support integration with pipelines.
 
