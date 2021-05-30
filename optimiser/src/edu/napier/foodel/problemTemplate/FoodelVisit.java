package edu.napier.foodel.problemTemplate;

import java.awt.geom.Point2D;


@SuppressWarnings("serial")
public class FoodelVisit extends Point2D.Double {
	private static int counter=0;
	private int index;
	protected int demand;
 	protected String theName;
	private String address;
	private String order;
	//private String postCode;

	public FoodelVisit(String name, double lat, double lon, int demand) {
		super(lat, lon);
		this.demand = demand;
		theName = name;
		counter++;
		index=counter;
	}

	public int getIndex() {
		return index;
	}

	public static int getCounter() {
		return counter;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
//	public String getPostCode() {
//		return postCode;
//	}
//	public void setPostCode(String postCode) {
//		this.postCode = postCode;
//	}

	public void setName(String name) {
		this.theName = name;
	}

	public void setDemand(int demand) {
		this.demand = demand;
	}

		@Override
	  public boolean equals(Object o) {
	     if (!(o instanceof FoodelVisit)){ 
	       return false;
	     }
	     FoodelVisit other = (FoodelVisit) o;
	     return theName.equals(other.theName);
	  }
	
	public double distance(FoodelVisit v) {
 		//Return the distance between 2 points based upon the Euclidean distance
 		//as implemented within 
 		return super.distance(v);
 	}

	public String getName() {
		
		return theName;
	}

	public int getDemand() {
		return demand;
	}
}
