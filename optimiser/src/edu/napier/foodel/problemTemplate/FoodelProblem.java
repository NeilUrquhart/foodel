package edu.napier.foodel.problemTemplate;

import java.util.ArrayList;


/*
 * Neil Urquhart 2019
 * This class represents a generic Foodel VRO problem.
 * 
 */
public abstract  class FoodelProblem  {
	private String startPCode;
	private String endPCode;
	protected FoodelVisit end;
	private ArrayList<ArrayList<FoodelVisit>> solution = new ArrayList<ArrayList<FoodelVisit>> ();
	//Represents the colection  of routes that comprise the solution

	private ArrayList<FoodelVisit> visitList = new ArrayList<FoodelVisit>();

	private int vehicleCapacity;//The vehicle capacity

	private FoodelVisit vrpstart;//The starting and finishing point of the TSP
	
	
	public FoodelVisit getStart() {
		return vrpstart;
	}
	public void setStart(FoodelVisit aStart) {
		this.vrpstart = aStart;
	}

	public long getTimeLimitMS() {
		return timeLimitMS;
	}

	public void setTimeLimitMS(long timeLimitMS) {
		this.timeLimitMS = timeLimitMS;
	}

	public long getDeliveryTimeMS() {
		return deliveryTimeMS;
	}

	public void setDeliveryTimeMS(long deliveryTimeMS) {
		this.deliveryTimeMS = deliveryTimeMS;
	}



	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	private String reference;
	private long timeLimitMS;
	private long deliveryTimeMS;
	private double speedfactor;
	private long startTime;

	private FoodelVisit start;

	//    public void setStart(FoodelVisit s) {
	//    	this.start = s;
	//    }
	//    
	//    public FoodelVisit getStart() {
	//    	return this.start;
	//    }
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public double getSpeedfactor() {
		return speedfactor;
	}

	public void setSpeedfactor(double speedfactor) {
		this.speedfactor = speedfactor;
	}


	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}


	private String dateTime;

	public int getNoVisits(){
		//Return the number of visits
		return visitList.size();
	}

	public ArrayList<FoodelVisit> getVisitList(){
		return visitList;
	}

	public void setSolution(ArrayList<ArrayList<FoodelVisit>> aVRPSolution){
		solution = aVRPSolution;
	}

	public ArrayList<ArrayList<FoodelVisit>> getCVRPSolution(){
		return this.solution;
	}
	public int getVehicleCapacity(){
		return vehicleCapacity;
	}

	public void setVehicleCapacity(int capacity){
		this.vehicleCapacity = capacity;
	}

	public void solve(FoodelSolver mySolver){
		//Solve the problem using the supplied solver
		mySolver.setProblem(this);
		mySolver.solve(); 
	}

	public void addVisit(FoodelVisit aCity){
		visitList.add(aCity);
	}

	public int getNoVehiclesRequired(){
		return solution.size();
	}


	public FoodelVisit getEnd() {
		return end;
	}

	public void setEnd(FoodelVisit end) {
		this.end = end;
	}

	public void setEndPCode(String pCode) {
		endPCode = pCode;
	}

	public String getEndPCode() {
		return endPCode;
	}


	public void setStartPcode(String pc) {
		this.startPCode = pc;
	}

	public String getStartPcode() {
		return this.startPCode;
	}

	public double getSolutionDistance(){
		//Return the total distance travelled by all of the vehicles
		return getSolutionDistance(solution);
	}

	public double getSolutionDistance(ArrayList sol){
		//Get the total distance covered by the solution in <sol>
		ArrayList<ArrayList<FoodelVisit>> solution = (ArrayList<ArrayList<FoodelVisit>>) sol;
		double dist =0;
		for (ArrayList<FoodelVisit> route: solution){
			ArrayList<FoodelVisit> r = new ArrayList<FoodelVisit>(route);
			dist = dist + getRouteDistance(r);
		}
		return dist;
	}

	private double getRouteDistance(ArrayList<FoodelVisit> possibleRoute){
		//Get the distance of the visits within possibleRoute - include the start point as well to complete the circuit
		double dist =0;
		FoodelVisit previousCity = vrpstart;
		for (FoodelVisit city : possibleRoute){//go through each city
			dist = dist + getDistance(previousCity, city);
			previousCity = city;
		}
		dist = dist + getDistance(previousCity, vrpstart);
		return dist;
	}

	public double getDistance(FoodelVisit x, FoodelVisit y){
		//Get the distance between two visits
		if ((x == null)||(y==null))
			return 0;
		else			
			return x.distance(y);
	}

	public ArrayList getVisits(){
		return  visitList;
	}

	public String isValid() {
		//Check to see if the problem contains enough info to solve. If it does return "" else
		//return an error msg
		String result  = "";
		if (reference == null)
			result += "No problem reference <br>";
		if (timeLimitMS == 0)
			result += "No round time limit entered <br>";
		if (dateTime.equals(""))
			result += "No start date entered\n";
		if (this.getNoVisits() < 2)
			result += "You need at least 2 deliveries <br>";
		if (this.getVehicleCapacity()==0)
			result += "No delivery capacity\n";
		if (this.getStart() == null)
			result += "You need to enter a start location <br>";
		return result;
	}

	public abstract String getGPX(int route);
	public abstract String getHTMLMap(int route);
	public abstract String getResultHTML(String key);

	public String toString(){
		//Return a string representation of the solution
		String buffer="";
		for (ArrayList<FoodelVisit> route: solution){
			buffer = buffer +"Route: ";
			for (FoodelVisit v : route){
				buffer = buffer +(" : "+v);
			}
			buffer = buffer +"\n";
		}
		return buffer;
	}
}
