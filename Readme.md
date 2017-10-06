# File Access and Metadata Service (FAMS)

Fams provides a simple service to access PDF Metadata if a PDF is existing on a specified location.

If it does not exist, it sends a job to another service to trigger the creation of the pdf.

The URL pattern to get the data is ```/pdf/context/document_id/structure_id``` where id can be any identifier and context may be the name of a service or client/customer.

## Quickstart

The following environment variables need to be present in the system (also in the docker environment):
* `PORT` that the application runs on (HTTP)
* `ACCESS_KEY` for the s3 storage
* `SECRET_KEY` for the s3 storage

Clone this repository, call ```./gradlew shadowJar```, wait for completion and afterwards run the service with ```java -jar build/libs/app-0.3.0-SNAPSHOT-shadow.jar```.
A webserver will be spawned and ist reachable at http://localhost:8080.

If an environment variable `PORT` is specified, the server will run on the specified port.

An example URL for calling the service is `http://localhost:8080/pdf/gdz/PPN875220959/LOG_00001`.

### Docker

The service can also be run with docker, by calling ```docker run -it --rm --name fams-run fams```

### Development

For local development, the command ```./gradlew run``` starts a daemon and rebuilds the application automatically when source files have been changed.

## Todo

Currently all urls and paths are hard-coded in the source code. This needs to be configurable.
