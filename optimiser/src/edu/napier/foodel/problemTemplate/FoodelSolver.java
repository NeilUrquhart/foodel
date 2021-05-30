package edu.napier.foodel.problemTemplate;

/*
 * Neil Urquhart 2019
 * This class represents the requirements for a Foodel Solver. Concrete instanciations 
 * of this class will need to implement the solve() method.
 * 
 */

public abstract class FoodelSolver {
	protected FoodelProblem theProblem;
	
	public void setProblem(FoodelProblem aProblem) {//aProblem represents the problem that is to be solved.
		theProblem = aProblem;
	}
	
	public abstract void solve();
	//This method should solve the problem contained in theProblem
}
