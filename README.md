tdt4215 Project for NTNU
=======

## A project for NTNU

Compile:
`mvn install`

Clean:
`mvn clean`

Make a jar with dependency:
`mvn clean compile assembly:single`

Use the fully stuffed jar:
`java -jar target/NAMEOFJAR.jar`

## Usage
You will need a folder called `indexes` that will store all the indexes used by the search engine.
All the data to index should be in the folder `documents`. The folders should be `NLH/{G,L,T}` for the NLH chapters and `icd10no.owl` for the icd10 ontology
