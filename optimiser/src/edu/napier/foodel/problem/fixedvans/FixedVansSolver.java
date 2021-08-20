package edu.napier.foodel.problem.fixedvans;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.napier.foodel.problem.cvrp.CVRPsolver;
import edu.napier.foodel.problemTemplate.FoodelIndividual;
import edu.napier.foodel.problemTemplate.FoodelSolver;
import edu.napier.foodel.utils.RandomSingleton;


public class FixedVansSolver  extends CVRPsolver{





	public FixedVansSolver( double end) {
		super(end);
		//this.end = end;
	}



	@Override
	protected FoodelIndividual InitialisePopution() {
		System.out.println("Setting up problem");
		population.clear();


		//Initialise population with (semi)random solutions
		FixedVansIndividual best = null;
		for (int count=0; count < POP_SIZE; count++){
			FixedVansIndividual i;

			i = new FixedVansIndividual(super.theProblem);
			if (count==0)
				i.nn();

			if (best == null) 
				best = i;
			if (i.evaluate() < best.evaluate()) 
				best = i;

			if((count%10)==0)
				System.out.println(count);
			population.add(i);
			evalsBudget--;
		}
		return best;
	}


	public FoodelIndividual runEA(int run) {
		if (super.theProblem.getNoVisits() <=2 ) {
			FixedVansIndividual i = new FixedVansIndividual(super.theProblem);
			i.evaluate();
			System.out.println("Finished! \nThanks for waiting.");
			return i;
		}
		int factor =(int) (super.theProblem.getNoVisits());
		evalsChange = 1000000;
		System.out.println("Time out = "+ evalsChange);
		int timeOut = evalsChange;

		//Reference to the best individual in the population
		FoodelIndividual bestSoFar = InitialisePopution();
		System.out.println("Initialised");
		while(timeOut >0) {	
			//Create child
			FixedVansIndividual child = null;
			if (rnd.getRnd().nextDouble() < XO_RATE){
				//Create a new Individual using recombination, randomly selecting the parents
				child = new FixedVansIndividual(super.theProblem, tournamentSelection(TOUR_SIZE),tournamentSelection(TOUR_SIZE));				
			}
			else{
				//Create a child by copying a single parent
				child = (FixedVansIndividual)tournamentSelection(TOUR_SIZE).copy();
			}

			child.mutate();
			child.evaluate();

			timeOut --;

			//Select an Individual with a poor fitness to be replaced
			FoodelIndividual poor = tournamentSelectWorst(TOUR_SIZE);
			if (poor.evaluate() > child.evaluate()){
				//Only replace if the child is an improvement

				if (child.evaluate() < bestSoFar.evaluate()){
					bestSoFar = child;
					timeOut =evalsChange;
				}
				//child.check();//Check child contains a valid solution
				population.remove(poor);
				population.add(child);
			}


			if ((timeOut %50000)==0) {
				System.out.print(run +"\tTo run "+(timeOut)+"\t ");
				System.out.println("\tVehicles "+bestSoFar.getVehicles() +"\tDist"+ df2.format(bestSoFar.getDistance())+"\tFitness "+ df2.format(bestSoFar.evaluate()));
			}
			if (System.currentTimeMillis() > this.end) {//time out
				timeOut=0;
				System.out.println("Timer expired.");
			}
		}

		return bestSoFar;
	}
}
