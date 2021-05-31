package edu.napier.foodel.problem.volunteers;

import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.napier.foodel.geo.Geocoder;
import edu.napier.foodel.problem.cvrp.CVRPProblemFactory;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.problemTemplate.FoodelProblemFactory;
import edu.napier.foodel.problemTemplate.FoodelVisit;

public class VolunteerProblemFactory extends FoodelProblemFactory {
	
@Override
public FoodelProblem parseData(HashMap<String, String[]> csvData, FoodelProblem result) throws Exception {
		super.parseData(csvData, result);
		VolunteerProblem resultV = (VolunteerProblem) result;
		for(String keyword : csvData.keySet()) {
			if (keyword.startsWith("Volunteer")){
				String[] buffer = csvData.get(keyword);
				resultV.addVolunteer(buffer[1], buffer[2]);
			}
		}
		return resultV;
	}
}
