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
import edu.napier.foodel.problem.cvrp.CVRPsolver;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.problemTemplate.ProblemStreamParser;
import edu.napier.foodel.utils.RandomSingleton;

public class FoodelFacade {
	//Implements Singleton
	private static FoodelFacade instance = null;
	private FoodelFacade() {}
	
	public static FoodelFacade getInstance() {
		if (instance == null)
			instance = new FoodelFacade();
		return instance;
	}
	//Done Singleton
	
	private  FoodelProblem myVRP;
	
	
	public  void setProblem(FoodelProblem aProb) {
		myVRP = aProb;
	}
	
//	public  FoodelProblem buildProblem(InputStream problemStream, String ref) throws Exception{
//		myVRP = new CVRPProblem();
//		myVRP.setReference(ref);
//		myVRP = (CVRPProblem) ProblemStreamParser.parseStream(problemStream, myVRP);
//		return myVRP;
//	}

	public  void solve(){
		RandomSingleton.getInstance().setSeed(86);
		double runTime = 60000*15; //15 mins
		double end = System.currentTimeMillis() + runTime;
		GHopperInterface.setCacheSize();
		CVRPsolver eaSolve = new CVRPsolver(end);
		myVRP.solve(eaSolve);
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
		myVRP = new CVRPProblem();
		myVRP.setReference(ref);
		myVRP = (CVRPProblem) ProblemStreamParser.parseData(csvData, myVRP);
		return myVRP;
	}	
}
