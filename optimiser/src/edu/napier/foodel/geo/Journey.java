package edu.napier.foodel.geo;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.napier.foodel.problemTemplate.FoodelVisit;



/*
 * A basic class to represent a journey that links two locations
 * 
 * Neil Urquhart 2020
 */
public class Journey {
	
	/*
	* The journey takes place between A and B 
	*/
	private FoodelVisit locationA;
	private FoodelVisit locationB;
	
	private double distanceKM=-1; 
	// The distance travelled in KM 
	private long travelTimeMS=-1; 
	// Traveltime in MSecs
	private ArrayList<Point2D.Double> path;
	
	
	/*
	* Constructor
	*/
	public Journey(FoodelVisit a, FoodelVisit b){
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


	public FoodelVisit getPointA() {
		return locationA;
	}

	public FoodelVisit getPointB() {
		return locationB;
	}
	
	
	public ArrayList<Point2D.Double> getPath() {
		return path;
	}

	public void setPath(ArrayList<Point2D.Double> path) {
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
