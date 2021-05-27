package edu.napier.foodel.basic;

import java.util.ArrayList;



/*
 * Neil Urquhart 2019
 * This class represents a CVRP problem.
 * It extends the basic TSPProblem class.
 * 
 */
public class VRPProblem  {
	
	private ArrayList<ArrayList<VRPVisit>> currentVRPSolution = new ArrayList<ArrayList<VRPVisit>> ();
	private ArrayList<VRPVisit> currentSolution = new ArrayList<VRPVisit>();
	//Represents the colection  of routes that comprise the solution
	private int capacity;
	//The vehicle capacity
	
	private VRPVisit vrpstart;//The starting and finishing point of the TSP
	public VRPVisit getStart() {
		return vrpstart;
	}
	public void setStart(VRPVisit aStart) {
		this.vrpstart = aStart;
	}
	public int getSize(){
		//Return the number of cities within the TSP (not including the start)
		return currentSolution.size();
	}

	public ArrayList<VRPVisit> getSolution(){
		return currentSolution;
	}
	
	public void setSolution(ArrayList<ArrayList<VRPVisit>> aVRPSolution){
		currentVRPSolution = aVRPSolution;
	}
	
	public ArrayList<ArrayList<VRPVisit>> getCVRPSolution(){
		return this.currentVRPSolution;
	}
	public int getCapacity(){
		return capacity;
	}
	
	public void setCapacity(int capacity){
		this.capacity = capacity;
	}
	
	public void solve(VRPSolver mySolver){
		//Solve the problem using the supplied solver
		mySolver.setProblem(this);
		mySolver.solve(); 
	}
	
	public void addVisit(VRPVisit aCity){
		currentSolution.add(aCity);
	}

	public int getVehicles(){
		return currentVRPSolution.size();
	}
	
	public double getDistance(){
		//Return the total distance travelled by all of the vehicles
		return getDistance(currentVRPSolution);
	}
	
	
	public double getDistance(ArrayList sol){
		//Get the total distance covered by the solution in <sol>
		ArrayList<ArrayList<VRPVisit>> solution = (ArrayList<ArrayList<VRPVisit>>) sol;
		double dist =0;
		for (ArrayList<VRPVisit> route: solution){
		    ArrayList<VRPVisit> r = new ArrayList<VRPVisit>(route);
			dist = dist + getDistanceRoute(r);
		}
		return dist;
	}
	
	public double getDistanceRoute(ArrayList<VRPVisit> possibleRoute){
		//Get the distance of the visits within possibleRoute - include the start point as well to complete the circuit
		double dist =0;
		VRPVisit previousCity = vrpstart;
		for (VRPVisit city : possibleRoute){//go through each city
			dist = dist + getDistance(previousCity, city);
			previousCity = city;
		}
		dist = dist + getDistance(previousCity, vrpstart);
		return dist;
	}
	public double getDistance(VRPVisit x, VRPVisit y){
		//Get the distance between two visits
		if ((x == null)||(y==null))
			return 0;
		else			
			return x.distance(y);
	}
	
	public ArrayList getVisits(){
		return  currentSolution;
		
	}
	
	public String toString(){
		//Return a string representation of the solution
		String buffer="";
		for (ArrayList<VRPVisit> route: currentVRPSolution){
			buffer = buffer +"Route: ";
			for (VRPVisit v : route){
				buffer = buffer +(" : "+v);
			}
			buffer = buffer +"\n";
		}
		return buffer;
	}
}
