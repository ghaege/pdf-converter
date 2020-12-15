# pdf-converter


## Description

* With **pdf-converter** you can convert office documents and any text documents to PDF documents through a REST API.
* It's usage is most rewarding for automated document conversion within a docker infrastructure.
* The project is based on Spring Boot, JODConverter and LibreOffice, which means it can convert any document type LibreOffice can.
* File extensions don't matter, as long as LibreOffice is able to open and convert them.

## The Application

### Dependencies

* Spring Boot 
* JODConverter 
* LibreOffice

### Build/Run the Application

    # local java build 
    ./gradlew clean build
    
    # local java build with intergrationTest's (LibreOffice needs to be installed)
    ./gradlew clean build intergrationTest

    # run (LibreOffice needs to be installed)
    ./gradlew bootRun
    # or
    java -jar build/libs/pdf-converter-$version.jar

## The Docker Image

### Shortcut via docker hub

If you only want to use it, without the need to build your own, you can pull the image from docker hub with

    docker pull ghaege/pdf-converter

### Build/Run/Stop the Docker Image

    # build
    docker build --target pdf-converter . -t ghaege/pdf-converter

    # run
    docker run --name pdf-converter -m 512m --rm -p 8100:8100 ghaege/pdf-converter
    # run as daemon
    docker run --name pdf-converter -m 512m --rm -p 8100:8100 ghaege/pdf-converter -d

    # stop
    docker stop pdf-converter

### Build info

- Debian SID (because it comes with LibreOffice 7+ which performs about 15% better than 6+)
- LibreOffice 7+
- OpenJDK 11 Java
- PDF Converter as REST API

### Configuration

The default Configuration comes with 1 LibreOffice instance see [application.yml](src/main/resources/application.yml), which is fine for most usages.

But if you feel the need to change it, you can configure spring boot app by mounting `/etc/app/application.properties` and put whatever you like into them.

For example if you like to have 2 LibreOffice instances, you would put into the file

```properties
# amount of libreOffice instances to start - one for each given port. So this means 2
jodconverter.local.port-numbers: 2002, 2003
# change the tmp folder
jodconverter.local.working-dir: /tmp
# change upload sizes
spring.servlet.multipart.max-file-size: 5MB
spring.servlet.multipart.max-request-size: 5MB
# change the server port (where the REST app is listenting
server.port=8090
```

## REST endpoints

Run the Application or Image and access [http://localhost:8100/swagger-ui.html](http://localhost:8100/swagger-ui.html) to browse, inspect and try the REST endpoints.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) for details

## Acknowledgments

All the credits go to the following for their great work:
* [JODconverter](https://github.com/sbraconnier/jodconverter)
* [eugenmayer/converter](https://github.com/EugenMayer/converter)
* [LibreOffice](https://de.libreoffice.org/)

Thanx.