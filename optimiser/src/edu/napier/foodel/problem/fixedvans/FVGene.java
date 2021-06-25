package edu.napier.foodel.problem.fixedvans;

import edu.napier.foodel.problemTemplate.FoodelVisit;

public class FVGene extends FoodelVisit{
	
	public FVGene(String name, String address, String postcode, String order, double lat, double lon, int demand, int van) {
		super(name, address,postcode, order, lat, lon, demand);
		this.van = van;
	}

	private int van;

	public int getVan() {
		return van;
	}

	public void setVan(int van) {
		this.van = van;
	}


}
