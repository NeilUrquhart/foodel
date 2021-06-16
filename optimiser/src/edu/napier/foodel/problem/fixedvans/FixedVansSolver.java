package edu.napier.foodel.problem.fixedvans;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.napier.foodel.problem.cvrp.CVRPsolver;
import edu.napier.foodel.problemTemplate.FoodelIndividual;
import edu.napier.foodel.problemTemplate.FoodelSolver;
import edu.napier.foodel.utils.RandomSingleton;


public class FixedVansSolver  extends CVRPsolver{
//	protected RandomSingleton rnd = RandomSingleton.getInstance();
//	//Note that we use the RandomSingleton object to generate random numbers
//	protected ArrayList <FoodelIndividual> population = new ArrayList<FoodelIndividual>();
//	//population stores our pool of potential solutions
//
//	private static int evalsChange = 1000000;//default
//	private static DecimalFormat df2 = new DecimalFormat("#.##");
//	private double end;
//
//
//	//EA Parameters
//	protected int POP_SIZE = 500;
//	protected int TOUR_SIZE = 2;
//	protected double XO_RATE = 0.2;
//	protected int evalsBudget = 1000000;
//
//	protected int runs = 10;


//	public void solve() {
//		FoodelIndividual best = null;
//		for (int run = 0; run < runs; run ++) {
//			FoodelIndividual curr = this.runEA(run);
//			if (best != null) {
//				if (curr.evaluate() <  best.evaluate())
//					best = curr;
//			}else
//				best = curr;
//		}
//		super.theProblem.setSolution(best.getPhenotype());
//	}




	public FixedVansSolver( double end) {
		super(end);
		//this.end = end;
	}

//	protected FoodelIndividual tournamentSelection(int poolSize){
//		//Return the best individual from a randomly selected pool of individuals
//		FoodelIndividual bestI = null;
//		double bestFit = Double.MAX_VALUE;
//		for (int tries=0; tries < poolSize; tries++){
//			FoodelIndividual i = population.get(rnd.getRnd().nextInt(population.size()));
//			if (i.getDistance() < bestFit){
//				bestFit = i.getDistance();
//				bestI = i;
//			}
//		}
//		return bestI;
//	}

//	protected FoodelIndividual tournamentSelectWorst(int poolSize){
//		//Return the worst individual from a ransomly selected pool of individuals
//		FoodelIndividual bestI = null;
//		double bestFit = 0;
//		for (int tries=0; tries < poolSize; tries++){
//			FoodelIndividual i = population.get(rnd.getRnd().nextInt(population.size()));
//			if (i.getDistance() > bestFit){
//				bestFit = i.getDistance();
//				bestI = i;
//			}
//		}
//		return bestI;
//	}

//	protected FoodelIndividual InitialisePopution() {
//		System.out.println("Setting up problem");
//		population.clear();
//
//
//		//Initialise population with (semi)random solutions
//		FixedVansIndividual best = null;
//		for (int count=0; count < POP_SIZE; count++){
//			FixedVansIndividual i;
//
//			i = new FixedVansIndividual(super.theProblem);
//			if (count==0)
//				i.nn();
//
//			if (best == null) 
//				best = i;
//			if (i.evaluate() < best.evaluate()) 
//				best = i;
//
//			if((count%10)==0)
//				System.out.println(count);
//			population.add(i);
//			evalsBudget--;
//		}
//		return best;
//	}


//	public FoodelIndividual runEA(int run) {
//		if (super.theProblem.getNoVisits() <=2 ) {
//			FixedVansIndividual i = new FixedVansIndividual(super.theProblem);
//			i.evaluate();
//			System.out.println("Finished! \nThanks for waiting.");
//			return i;
//		}
//		int factor =(int) (super.theProblem.getNoVisits());
//		evalsChange = 1000000;
//		System.out.println("Time out = "+ evalsChange);
//		int timeOut = evalsChange;
//
//		//Reference to the best individual in the population
//		FixedVansIndividual bestSoFar = InitialisePopution();
//		System.out.println("Initialised");
//		while(timeOut >0) {	
//			//Create child
//			FixedVansIndividual child = null;
//			if (rnd.getRnd().nextDouble() < XO_RATE){
//				//Create a new Individual using recombination, randomly selecting the parents
//				child = new FixedVansIndividual(super.theProblem, tournamentSelection(TOUR_SIZE),tournamentSelection(TOUR_SIZE));				
//			}
//			else{
//				//Create a child by copying a single parent
//				child = (FixedVansIndividual)tournamentSelection(TOUR_SIZE).copy();
//			}
//
//			child.mutate();
//			child.evaluate();
//
//			timeOut --;
//
//			//Select an Individual with a poor fitness to be replaced
//			FoodelIndividual poor = tournamentSelectWorst(TOUR_SIZE);
//			if (poor.evaluate() > child.evaluate()){
//				//Only replace if the child is an improvement
//
//				if (child.evaluate() < bestSoFar.evaluate()){
//					bestSoFar = child;
//					timeOut =evalsChange;
//				}
//				//child.check();//Check child contains a valid solution
//				population.remove(poor);
//				population.add(child);
//			}
//
//
//			if ((timeOut %50000)==0) {
//				System.out.print(run +"\tTo run "+(timeOut)+"\t ");
//				System.out.println("\tVehicles "+bestSoFar.getVehicles() +"\tDist"+ df2.format(bestSoFar.getDistance())+"\tFitness "+ df2.format(bestSoFar.evaluate()));
//			}
//			if (System.currentTimeMillis() > this.end) {//time out
//				timeOut=0;
//				System.out.println("Timer expired.");
//			}
//		}
//		return bestSoFar;
//	}
}
