# About "SemTrail"

## About

SemTrail was created because I had the need to document and structure a medium number interconnected requirements. Customer requirements and facts lead over a number of indirections to a concrete architecture.

It uses a LISP/Clojure-like DSL for describing requirements, facts, conclusions and similar using typed nodes. Interconnections are defined via edge that also have a type. This builds a semantic graph.

From the description file an internal model is build.

It uses Thymeleaf for generating the resulting web pages base don the passed internal model.

## Current status

The current status is "it starts to work for me but I'll likely add
some further small enhancements".

## Be invited!

If you are interested, participate.

## The name...

The project visualizes a trail from customer requirements to conceptual solutions and finally to concrete technological decisions. In principle, it is a semantic graph. Where each node is a requirement, a concept or a technical solution and each edge represents a deduction, a decision, a possible alternative or similar.

The similarity between "SemTrail" and "Chemtrail" was realized and found to be funny. Also, some architecture claims to have its origin in a path of concrete, well thought of decisions - but nobody never has seen them, neither are they documented.

## How to use

Simply call it using

```sh
SemTrail <description file>.semtrail <template directory> <target directory>
```

There are currently some quirks in the command line handling
and resulting processing. So expect errors if you do not follow the expected syntax.

## Reports

The current version using the default templates generates the following navigable pages:

|Page     |Template|Content|
|---------|--------|-------|
|All      |`index.html`|Tabular representation of all defined nodes|
|All (Map)|`image.html`|Network-lke visualisation of all nodes and their connecting edges|
|\<Node type\>|`index.html`|For each node type a tabular representation of all node of this type|
|Starter |`index.html`|A list of all nodes that "start" a directed graph|
|Leaves  |`index.html`|A list of all nodes that have only incoming edges|
|By #causes|`index.html`|A list of all nodes sorted by the number of incoming edges|
|By #consequences|`index.html`|A list of all nodes sorted by the number of outgoing edges|
|By #connected|`index.html`|A list of all nodes sorted by the sum of incoming and outgoing edges|
|Orphans|`index.html`|A list of nodes that have neither incoming nor outgoing edges|
|Missing Documentation|`index.html`|A list of nodes that have no documentation|

Besides above pages a page for each node is generated using the `node.html` template.

The following additional template files are used for reusing and sharing content over all generated pages and should be referenced from above pages:

|File          |Content|
|--------------|-------|
|`footer.html` |Common footer of each page|
|`header.html` |Common header of each page|
|`navigation.html`|Common navigation component|
|`standard.css`|Common style sheet|

## Data passed to the templates

The template gets passed the following objects, depending on its meaning:

|Object|Type|Passed to|Description|
|------|----|---------|-----------|
|model |Model|Always|The data parsed from the semtrail file|
|node  |Node|Detail page of a node|The current node|
|nodes|List\<Node\>|List of nodes|The (possibly filtered) list of nodes to display|

### The Model type

The Model class has the following attributes:

|Attribute|Type|Description|
|---------|----|-----------|
|nodeTypeSet|Set\<String\>|Set of node type names|
|nodeTypeNames|Map\<String,String\>|Mapping from node type name to label|
|nodeTypeColors|Map\<String,String\>|Mapping from node type name to color|
|edgeTypeSet|Set\<String\>|Set of node type names|
|edgeTypeNames|Map\<String,String\>|Mapping from edge type name to label|
|edgeTypeColors|Map\<String,String\>|Mapping from edge type name to color|
|nodeMap|Map\<String,Node\>|Mapping from node name to `Node` type instance|

### The Node type

|Attribute|Type  |Description|
|---------|------|-----------|
|name     |String|The name of the node|
|type     |String|The name of the node type|
|doc      |String|A documentation|
|fromNodes|Map\<String,Edge\>|Map from edge name to `Edge` type instance for all incoming edges|
|toNodes  |Map\<String,Edge\>|Map from edge name to `Edge` type instance for all outgoing edges|

### The Edge type

|Attribute|Type  |Description|
|---------|------|-----------|
|type     |String|The type name|
|from     |Node  |The from `Node`|
|to       |Node  |The to `Node`|
|doc      |String|A documentation|

## Syntax of *.semtrail files

TODO

## Simple Docker Image for hosting the generation result

The repository contains a simple Dockerfile as boiler plate example for
hosting the generated static web pages using nginx. This is not a production
ready example, only a simple show cases for tests. Make sure that you
host static content with the base images you trust.

Build the docker container:

```sh
./build_docker.sh
```

This copies the relevant files in to a maven-based base Image (maven:latest from docker.io), starts a maven build and generate the static pages for Semtrail.semtrail.

Afterwards it creates a nginx based image (`framstag/semtrail:latest`) with the above generated static content to serve.

Running the image:

```sh
./run_docker.sh
```
This runs the nginx in the resulting Docker image under localhost:8080
