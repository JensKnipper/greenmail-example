# greenmail-example

## About

This is an example application in which you can create notes and send them via mail.
Notes can also be created by incoming mails to the address specified in the `application.config` file.
All the mails will remain local due to [GreenMail](https://greenmail-mail-test.github.io/greenmail/) running in the background as a mock SMTP server.
You can use the included [RoundCube](https://roundcube.net/) instance to read and send mails.
Mail sending and receiving is tested with integration tests using GreenMail.

## Running the application

### Setup 
* create the application: `mvn package`
* start up the docker dependencies: `docker-compose up -d`
* run the application: `java -jar target/greenmail-example-0.0.1-SNAPSHOT.jar`

### Usage
* call `http://localhost:8080/` to access the application
* call `http://localhost:8000/` to read and send emails locally
  * login with username and password - password is equal to username
  * greenmail automatically creates new users on login or receiving mails
  
## Thanks

I would like to thank my former employer [OVSoftware](https://ovsoftware.de/) and my current employer [OpenValue](https://openvalue.de/) for allowing me to publish the results of my research.
