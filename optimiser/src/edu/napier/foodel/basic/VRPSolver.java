package edu.napier.foodel.basic;

/*
 * Neil Urquhart 2019
 * This class represents the requirements for a TSP Solver. Concrete instanciations 
 * of this class will need to implement the solve() method.
 * 
 */

public abstract class VRPSolver {
	protected VRPProblem theProblem;
	
	public void setProblem(VRPProblem aProblem) {//aProblem represents the problem that is to be solved.
		theProblem = aProblem;
	}
	
	public abstract void solve();
	//This method should solve the problem contained in theProblem
}