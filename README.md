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

## Further improvements

* update dependencies
* unit test emails
  * include templating engine?
* edit notes by sending mails
* random ports for tests
* include glyphicons to improve styling
* test for changing imap/pop3 via integration test
* test mail read/unread feature via integration test
* assert recipient and sender in GreenMail tests
* scheduler delay in properties and in e2e test
* make e2e tests runnable on all OS variants - testcontainers?

## Thanks

I would like to thank my former employer [OVSoftware](https://ovsoftware.de/) and my current employer [OpenValue](https://openvalue.de/) for allowing me to publish the results of my research.
