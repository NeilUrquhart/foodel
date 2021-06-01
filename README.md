# foodel - start here.....

*General*

Foodel is Java library that is designed to optimise (simple) last mile logistics problems. It is aimed at small businesses and voluntary organisations who need to find optimum routes when delivering to houses.

Foodel has been developed to showcase a number of optimisation techniques, such as Evolutionary algorithms.  You are welcome to use Foodel, but in return we would love to hear your feedback.

*Support*

Foodel was developed by Dr Neil Urquhart of Edinburgh Napier University (n.urquhart@napier.ac.uk). Please email Neil for support or advice, suggestions as to how to improve Foodel are also very welcome!

*Running Foodel*

As a Java applicaton it should run on Mac/Linux/Windows etc without any problems. Foodel is designed to be used on one of two ways:

* Single: The Foodel application runs on a local host and you interact with it via your web browswer (default port is 8080 e.g. http://localhost 8080).  Providing you have a firwall set on you machine it should not be accessable to anyone else.

* Shared: The Foodel application is run on a server with the Foodel port open and connections can be made from other hosts via the web.

(Before sharing foodel access, be sure that you have set the appropriate options in server.properties   to keep your data secure.  Look into the /docs/ folder for detailed information.)

*Ths archive*

Foodel is designed to be simple to use and easy to install and configure. Everything required to make Foodel run is contained within this archive.

This archive forms two Eclipse projects, if you clone it, then import the projects (server & optimiser), you should be able to run server and connect to your new Foodel install at http://localhost:8080  .

Foodel uses Open Streetmap data to calculate distances and to draw maps. The data supplied with the download covers the City of Edinburgh and surrounding areas. If you wish to use data for other areas, you will need to download your own OSM/PBF file (free). More information on doing this can be found in the docs directory. Note that this repo. does not include the Open Streetmap OSM/PBF files needed for map data as they are too large to store in github.



