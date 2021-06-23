package edu.napier.foodel.problem.fixedvans;

import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.napier.foodel.geo.Geocoder;

import edu.napier.foodel.problem.volunteers.VolunteerProblem;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.problemTemplate.FoodelProblemFactory;
import edu.napier.foodel.problemTemplate.FoodelVisit;

public class FixedVansProblemFactory extends FoodelProblemFactory {
	
	@Override
	public FoodelProblem parseData(HashMap<String, String[]> csvData, FoodelProblem result) throws Exception {
			super.parseData(csvData, result);
			FixedVansProblem resultV = (FixedVansProblem) result;
			for(String keyword : csvData.keySet()) {
				if (keyword.startsWith("Vehicles Available")){
					String[] buffer = csvData.get(keyword);
					resultV.setVans(Integer.parseInt(buffer[1]));
				}
			}
			return resultV;
		}
	
	@Override
	protected void addVisit(String keyword, HashMap<String, String[]> csvData, FoodelProblem result) throws Exception {
		String[] buffer = csvData.get(keyword);
		System.out.println(buffer[0]);//cust
		System.out.println(buffer[1]);//name
		System.out.println(buffer[2]);//address
		int bags =1;
		String note = "";
		if (buffer.length>4) {
			note = buffer[5]; 
			note = note.replace('<', ' ');
			note = note.replace('>', ' ');
			note = note.replace('(', ' ');
			note = note.replace(')', ' ');
			note = note.replace('&', ' ');
			note = note.replace('+', ' ');
			System.out.println(note);//Note
		}
		Point2D p = Geocoder.find(buffer[2] +" "+buffer[3]);//Address + Postcode
		if (p== null) {
			throw new Exception("Address not found " + buffer[2]+" "+buffer[3]);
		}else {
			FVGene v = new FVGene(buffer[1],buffer[2]+" "+buffer[3],note,p.getX(),p.getY(),bags,0);
			result.addVisit(v);
		}
	}
}