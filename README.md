# Living Atlas Pipelines extensions

This project is proof of concept quality code aimed at identifying work required
 to use of pipelines as a replacement to [biocache-store](https://github.com/AtlasOfLivingAustralia/biocache-store)
 for data ingress. 


## Done so far:

1. Extension with ALA taxonomy
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

### ala-namematching-service
So far a simple drop wizard wrapper around ala-namematching library has been prototyped to support integration with pipelines.
 
