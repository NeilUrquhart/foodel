package edu.napier.foodel.problem.volunteers;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.napier.foodel.geo.Geocoder;
import edu.napier.foodel.problem.cvrp.CVRPProblem;

public class VolunteerProblem extends CVRPProblem {
	private ArrayList<Volunteer> volunteers = new ArrayList<Volunteer>();
	
	public void addVolunteer(String name, String address) {
		volunteers.add(new Volunteer(name, address));
	}
	
	public class Volunteer{
		private String name;
		private String address;
		private Point2D location;
		
		public Volunteer(String name, String address) {
			this.name = name;
			this.address = address;
			this.location = Geocoder.find(address);
		}
		
		public String getName() {
			return name;
		}
		
		public String getAddress() {
			return address;
		}
		
		public Point2D getLocation() {
			return location;
		}
	}
}
