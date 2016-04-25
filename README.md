## Synopsis

This software is developed as a part of the VAMDC consortium collaboration. 
It consists of a web application for creating an excel output file from the content of the VAMDC species database.  

This software relies on the project [ExcelWriter](https://github.com/cmzwolf/ExcelWriter)

## Using

Run a maven install, then put the generated output war in your preferred servlet-container server. 

Access the service with the url 

_yourServer:listeningPort/SpeciesDBExcelApp/getSpeciesExcel_

This will download into your default download folder a timestamped excel file, containing all the species available into the VAMDC species DB.

## Motivation
This provide a quick way for checking and crossmatching the content of the VAMDC database

## License

This software is published on GitHub with a GPL3 license. Intellectual property belongs to the VAMDC consortium. 