# File Access and Metadata Service (FAMS)

Fams provides a simple service to access PDF Metadata if a PDF is existing on a specified location.

If it does not exist, it sends a job to another service to trigger the creation of the pdf.

The URL pattern to get the data is ```/pdf/context/id``` where id can be any identifier and context may be the name of a service or client/customer.

## Quickstart

Clone this repository, call ```./gradlew shadowJar```, wait for completion and afterwards run the service with ```java -jar build/libs/fams-0.1.2-SNAPSHOT-fat.jar```.
A webserver will be spawned and ist reachable at http://localhost:8080.

If an environment variable `PORT` is specified, the server will run on the specified port.

An example URL for calling the service is http://localhost:8080/pdf/gdz/PPN875220959.

### Docker

The service can also be run with docker, by calling ```docker run -it --rm --name fams-run fams```

## Todo

Currently all urls and paths are hard-coded in the source code. This needs to be configurable.
