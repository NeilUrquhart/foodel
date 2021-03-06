#Optimser

*Introduction*

The Optimiser is an Eclipse project that that contains the algorithms required to create solutions to Foodel problems.

*Problem Types*

Currently the Optimiser recognises two types of problem:

* CVRP - Capacitated vehicle routing problem, the solution must find a set of routes that visit each of the customers once. Routes have two constraints, vehicle capacity and time.
	* Vehicle capacity - each customer has a demand 	value (default is 1, but can be higher) the total of the demand values for a route may not exceed the vehicle capacity.
	* Time - This refers to the maximum time (minutes) that a delivery may be in transit for. EG if this was set to 60 minutes, then the longest time allowed between a route starting and the last delivery would be 60 mins.

	The solution will comprise as many routes as are required to make the deliveries based on the two constraints. Routes are optimised to minimise distance weighted by the load carried by the vehicle  over each part of the route (default) or can be set to optimse purely on distance travelled..  

* VolRP  - Volunteer Routing Problem - an adaptation of CVRP to suit those working with volunteers. Within the problem CSV file a list of volunteer names and addresses may be added. The routes are constructed as for CVRP, but with the following changes:
	* Routes are allocated to volunteers (e.g. first route to the first volunteer, second route to the second volunteer and so on).
	* When a route is allocated to a volunteer, the distance from their home address to the start and from the last delivery to their home address is taken into account - the aim being to try to ensure that volunteers are allocated routes that finish close to their homes
	* If there are more routes than volunteers, the additional routes will start/end at the depot and have no volunteers allocated
	* Volunteers are allocated in the order they appear in the problem, you may wish to place those volunteers whom you wish to allocate work to first at the to of the list.

The format of the input files can be found in /problems/ 

Depending on the input data the Optimiser will determine the type of problem (e.g. an input file that contains the Volunteer keyword will be solved as a VolRP).

Further problems will be added to the solver in due course.

*Solver Architecture*

The solver built a around a Facade pattern. The class edu.napier.foodel.facade.FoodelSolver publishes a set of methods that can be used to solve problems.  FoodelSolver is based on the Singleton pattern and so cannot be instanciated, but should be accessed using 

`FoodelSolver.getInstance()`

The FoodelSolver class only handles one problem instance at a time. The queing of problems as well as any loading/saving is dealt with the the Server project.

The methods available are:

* `public FoodelProblem newProblem(HashMap<String, String[]> csvData,String ref) throws Exception` Create a new problem based on the problem contauned in csvData. Our CSV files are arranged so that column A contains a keyword which conveys the meaning of the remaining columns (see data file examples). Each <key,value> pair represents the keyword (col A) paired with a String array that represents the values in the columns on that row (including A). Ref is a String used as the problem reference.

The method returns a FoodelProblem object (it will actually be a subclass such as CVRPProblem or VolunteerProblem).

* `public  void setProblem(FoodelProblem aProb)`
Used to pass a FoodelProblem object to the solver, which becomes the current problem.

* `public  void solve()`
Optimises the current problem and produces a solution, which may be accessed through the methods below. Note that this method may take some time to execute depening on the size and type of the current problem.

* `public String getResultHTML(String key)` 
Returns a string that contains an HTML representation of the answer (note not a complete page). The key parameter is required in order to correctly form the links to individual maps. The key parameter is generated within the Server project. Note that this method only returns a valid result the solve() method as been called.

* `public String getGPX(int r)`

Returns a string representing a GPX representation of route <r> of the current problem. Note that this method only returns a valid result the solve() method as been called.


* `public String getHTMLMap(int route)`

Returns a String containing an HTML representation (including a Leaflet OSM map) of route <route> of the current problem. Note that this method only returns a valid result the solve() method as been called.


