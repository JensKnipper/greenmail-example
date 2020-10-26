# greenmail-example

## About

This small example application was planned to be shown in a talk at my employers - [OVSoftware](https://ovsoftware.de/) - internal conference.
Due to the current Corona situation the conference was cancelled.
Still I would like to share the results of my research.

This is an example application in which you can create notes and send them via mail.
Notes can also be created by incoming mails to the address specified in the `application.config` file.
All the mails will remail local due to [GreenMail](https://greenmail-mail-test.github.io/greenmail/) running in the background as a mock SMTP server.
You can use the included [RoundCube](https://roundcube.net/) instance to read and send mails.
Mail sending and receiving is tested with integration tests using GreenMail.

Please do not use the code for receiving mail as an example. 
I had no idea how to do it properly and the application is only intended to be a showcase for GreenMail.  
Currently the test for receiving mail only seems to work on Windows and not on Linux. I will further investigate in this.

## Running the application

### Setup 
* create the application: `mvn clean package`
* start up the docker dependencies: `docker-compose up -d`
* run the application: `java -jar target/greenmail-example-0.0.1-SNAPSHOT.jar`

### 
* call `http://localhost:8080/` to access the application
* call `http://localhost:8000/` to read and send emails locally
  * login with username and password - password is equal to username
  * greenmail automatically creates new users on login or receiving mails
  
## Thanks

I would like to thank my employer [OVSoftware](https://ovsoftware.de/) for allowing me to publish the results of my research.
