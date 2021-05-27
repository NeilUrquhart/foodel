package edu.napier.foodel.basic;


import java.util.ArrayList;
import java.util.HashMap;


/*
 * A basic class to represent a journey that links two locations
 * 
 * Neil Urquhart 2020
 */
public class Journey {
	
	/*
	* The journey takes place between A and B 
	*/
	private VRPVisit locationA;
	private VRPVisit locationB;
	
	private double distanceKM=-1; 
	// The distance travelled in KM 
	private long travelTimeMS=-1; 
	// Traveltime in MSecs
	private ArrayList<VRPVisit> path;
	
	
	/*
	* Constructor
	*/
	public Journey(VRPVisit a, VRPVisit b){
		locationA = a;
		locationB = b;
	}

	/*
	 * Accessor Methods
	 * 
	 */
	public double getDistanceKM() {
		return distanceKM;
	}

	public void setDistanceKM(double distanceKM) {
		this.distanceKM = distanceKM;
	}

	public long getTravelTimeMS() {
		return travelTimeMS;
	}

	public void setTravelTimeMS(long travelTimeMS) {
		this.travelTimeMS = travelTimeMS;
	}


	public VRPVisit getPointA() {
		return locationA;
	}

	public VRPVisit getPointB() {
		return locationB;
	}
	
	
	public ArrayList<VRPVisit> getPath() {
		return path;
	}

	public void setPath(ArrayList<VRPVisit> path) {
		this.path = path;
	}

	/*
	 * ToString
	 * 
	 */
	public String toString(){
		String buffer ="";
		
		buffer = locationA + " : " + locationB;
		
		
		return buffer;
	}
}
