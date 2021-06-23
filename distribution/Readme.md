##Distribution

This folder contains a working distribution of Foodel.

To start it, run the foode.jar archive  
`java -jar foodel.jar`

Then point your browser to 

`http:\\localhost:8080`


Windows - 

launch4j can be used to turn the .jar into an exe file.
See http://launch4j.sourceforge.net/
A copy of OpenJDK to bundle into the EXE is in the utils directory, along with a config file for launch 4j.

## Building the jar

Foodel uses Maven projects in order to keep life simple.  To create the jar...

1. Compile, package and install the optimser project (it is a dependency of server).
2. Compile and package the server project
3. Done. Go and have a tea/coffee/beer/juice etc
