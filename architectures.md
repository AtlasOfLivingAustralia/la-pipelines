# Comparison of architectures (work in progress)

This is page is intended to document changes in the architecture of biocache to a pipelines based deployment.

## Current architecture
![Existing architecture](https://docs.google.com/drawings/d/e/2PACX-1vQPYIjmbt1e-PNU0ZK6FpYpqxw4xQj4A3tXHjEZIQ3ZGPnwj0cBCdFgEs_SNnccR0rGcnpOFWgqYlLS/pub?w=960&h=720)

### Notes on the diagram


## New architecture
![New architecture](https://docs.google.com/drawings/d/e/2PACX-1vSGixRyj2cCyZjGFdcMZflYLXU_VuiA4Y6CIkNiF4DHLFOa03613iolLFOaDX_-AqnhMu6qdJn-veVA/pub?w=960&h=720)

### Notes on the diagram
1. The use of cassandra is still something under review. There needs to be storage of the UUID mapping. It maybe be possible to do this without using cassandra and using a serialisation of UUID mappings stored on a dataset by dataset basis on the filesystem. Having no connection from pipelines to cassandra is attractive as it reduces barriers to run ALA pipelines in other environments (e.g. GBIF).


