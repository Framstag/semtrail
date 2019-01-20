# About "SemTrail"

SemTrail was created because I had the need to document and structure a
medium number interconnected requirements. Customer requirements and
facts lead over a number of indirections to a concrete architecture.

It uses a lisp/clojure-like DSL for describing requirements, facts,
conclusions and so on using typed nodes. Interconnections are defined
via edge that also have a type. This builds a semantic graph.

From the description file an internal model is build.

It uses thymeleaf for generating the resulting web pages base don the
passed internal model. 

# Current status

The current status is "it starts to work for me but I'll likely add
some further small enhancements".

# Be invited!

If you are interested, participate.

# The name...

The project visualizes a trail from customer requirement to conceptual
solutions and finally to concrete technological decisions. In principle
it is a semantic graph. The similarity between "SemTrail" and
"Chemtrail" was realized and found to be funny. Also some architecture
claims to have its origin in a path of concrete, well thought of
decisions - but nobody never have seen them, neither are they documented. 

# How to use

Simply call it using

```
SemTrail <description file>.semtrail <template directory> <target directory>
```

There are currently some quirks in the command line handling 
and resulting processing.