package edu.napier.foodel.problem;

import edu.napier.foodel.basic.VRPVisit;

public class IndexVisit extends VRPVisit{
	/*
	 * An extension of Visit - each visit has a unique index property, which is set when the object is created
	 */
	private static int counter=0;
	
	private int index;
	
	public IndexVisit(String name, double lat, double lon, int demand) {
		super(name, lat, lon, demand);
		counter++;
		index=counter;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static int getCounter() {
		return counter;
		
	}

}
