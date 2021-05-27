package edu.napier.foodel.basic;

import java.awt.geom.Point2D;

/*
 * VRPVisit - extends Visit adds demand.
 * 
 */


public class VRPVisit extends Point2D.Double {

	protected int demand;
 	protected String theName;
 	
	public VRPVisit(String name, double lat, double lon, int demand) {
		super(lat, lon);
		this.demand = demand;
		theName = name;
	}
	
	public VRPVisit(String name, double lat, double lon) {
		this(name, lat, lon,0);

	}
	
	public int getDemand(){
		return demand;
	}
	
	public String toString(){
		return  theName + " " +demand+" (" + super.x +":" + super.y +")";
	}
	

	 	
	 	public String getName() {
	 		return theName;
	 	}

	@Override
	  public boolean equals(Object o) {
	     if (!(o instanceof VRPVisit)){ 
	       return false;
	     }
	     VRPVisit other = (VRPVisit) o;
	     return theName.equals(other.theName);
	  }
	
	public double distance(VRPVisit v) {
 		//Return the distance between 2 points based upon the Euclidean distance
 		//as implemented within 
 		return super.distance(v);
 	}
}
