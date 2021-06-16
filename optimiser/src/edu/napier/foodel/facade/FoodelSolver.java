package edu.napier.foodel.facade;
/*
 * Neil Urquhart 2019
 * This programme tests a set of CVRP problem instances, using a range of solvers to produce solutions.

 * 
 */



import java.io.InputStream;
import java.util.HashMap;

import edu.napier.foodel.geo.GHopperInterface;
import edu.napier.foodel.problem.cvrp.CVRPProblem;
import edu.napier.foodel.problem.cvrp.CVRPProblemFactory;
import edu.napier.foodel.problem.cvrp.CVRPsolver;
import edu.napier.foodel.problem.fixedvans.FixedVansProblem;
import edu.napier.foodel.problem.fixedvans.FixedVansProblemFactory;
import edu.napier.foodel.problem.fixedvans.FixedVansSolver;
import edu.napier.foodel.problem.volunteers.VolunteerProblem;
import edu.napier.foodel.problem.volunteers.VolunteerProblemFactory;
import edu.napier.foodel.problem.volunteers.VolunteerSolver;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.problemTemplate.FoodelProblemFactory;
import edu.napier.foodel.utils.RandomSingleton;

public class FoodelSolver {
	//Implements Singleton
	private static FoodelSolver instance = null;
	private FoodelSolver() {}
	
	public static FoodelSolver getInstance() {
		if (instance == null)
			instance = new FoodelSolver();
		return instance;
	}
	//Done Singleton
	
	private  FoodelProblem myVRP;
	private enum ProblemType {CVRP, Volunteer, FixedVans}
	private ProblemType problem;
	
	public  void setProblem(FoodelProblem aProb) {
		myVRP = aProb;
	}
	
	public  void solve(){
		RandomSingleton.getInstance().setSeed(86);
		double runTime = 60000*15; //15 mins
		double end = System.currentTimeMillis() + runTime;
		GHopperInterface.setCacheSize();
		FoodelSolver eaSolve=null;
		
		if (problem == ProblemType.CVRP)
			myVRP.solve(new CVRPsolver(end));

		if (problem == ProblemType.Volunteer)
			myVRP.solve(new VolunteerSolver(end));
		
		if (problem == ProblemType.FixedVans)
			myVRP.solve(new FixedVansSolver(end));

		}

	public String getGPX(int r) {
		return myVRP.getGPX(r);
	}
	
	public String getHTMLMap(int route) {
		return myVRP.getHTMLMap(route);
	}

	public String getResultHTML(String key) {
		return myVRP.getResultHTML(key);
	}

	public FoodelProblem newProblem(HashMap<String, String[]> csvData,String ref) throws Exception {
		//Establish Problem Type...
		
		if (findKey(csvData,"Volunteer")) {
			problem = ProblemType.Volunteer;
			myVRP = new VolunteerProblem();
			FoodelProblemFactory factory = new VolunteerProblemFactory();
			myVRP.setReference(ref);
			myVRP = (CVRPProblem) factory.parseData(csvData,myVRP);
			return myVRP;
		}
		
		else if (findKey(csvData,"Vehicle Capacity")) {
			problem = ProblemType.FixedVans;
			myVRP = new FixedVansProblem();
			FoodelProblemFactory factory = new FixedVansProblemFactory();
			myVRP.setReference(ref);
			myVRP = (FixedVansProblem) factory.parseData(csvData,myVRP);
			return myVRP;
			
		}
		else {
			//Default is CVRP problem
			problem = ProblemType.CVRP;
			FoodelProblemFactory factory = new CVRPProblemFactory();
			myVRP = new CVRPProblem();
			myVRP.setReference(ref);
			myVRP = (CVRPProblem) factory.parseData(csvData,myVRP);
			return myVRP;
		}
	}	
	
	private boolean findKey(HashMap<String, String[]> data, String key) {
		for (String s: data.keySet() ) {
			if (s.startsWith(key))
				return true;
		}
		return false;
	}
}
