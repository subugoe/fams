# File Access and Metadata Service (FAMS)

Fams provides a simple service to access PDF Metadata if a PDF is existing on a specified location.

If it does not exist, it sends a job to another service to trigger the creation of the pdf.

The URL pattern to get the data is ```/pdf/id``` where id can be any identifier.

## Quickstart

Clone this repository, call ```./gradlew shadowJar```, wait for completion and afterwards run the service with ```java -jar build/libs/fams-1.0-SNAPSHOT-fat.jar```.
A webserver will be spawned and ist reachable at http://localhost:8080.

An example URL for calling the service is http://localhost:8080/pdf/PPN875220959.

## Todo

Currently all urls and paths are hard-coded in the source code. This needs to be configurable.
