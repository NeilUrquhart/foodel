package edu.napier.foodel.algorithm;

import java.text.DecimalFormat;

import edu.napier.foodel.basic.Individual;
import edu.napier.foodel.basic.VRPea;



public class FoodelEA extends VRPea {
	private static int evalsChange = 1000000;//default
	 private static DecimalFormat df2 = new DecimalFormat("#.##");
	  private double end;
	 private int  POP_SIZE=1000;
	 protected int runs = 10;
		
		@Override
		public void solve() {
			Individual best = null;
			for (int run = 0; run < runs; run ++) {
				Individual curr = this.runEA(run);
				if (best != null) {
					if (curr.evaluate() <  best.evaluate())
						best = curr;
				}else
					best = curr;
			}
		//	FoodelIndividual fBest = (FoodelIndividual)best;
		//			fBest.resetPheno();
					
		//	System.out.println("*****");
	//		System.out.println(fBest.DEBUGevaluate());
			super.theProblem.setSolution(best.getPhenotype());
		}

		
		
		
    public FoodelEA( double end) {
    	this.end = end;
    }
	@Override
	protected FoodelIndividual InitialisePopution() {
		System.out.println("Setting up problem");
		population.clear();

		
		FoodelIndividual init=null;
		//Initialise population with (semi)random solutions
		FoodelIndividual best = null;
		for (int count=0; count < POP_SIZE; count++){
			FoodelIndividual i;
			if(init != null) {
				if(rnd.getRnd().nextBoolean()) {
					i = init.copy();
					int m = rnd.getRnd().nextInt(10);
					for (int x=0; x < m; x++)
						i.mutate();
				}
			}
			i = new FoodelIndividual(super.theProblem);

			
			if (best == null) 
				best = i;
			if (i.evaluate() < best.evaluate()) 
				best = i;
			
			if((count%10)==0)
				System.out.println(count);// +" "+best.evaluate() + " "+best.getDistance() +" "+ best.getVehicles());

			population.add(i);
			evalsBudget--;
		}
		return best;
	}
	

	public Individual runEA(int run) {
		if (super.theProblem.getSize() <=2 ) {
			FoodelIndividual i = new FoodelIndividual(super.theProblem);
			i.evaluate();
			//super.theProblem.setSolution(i.getPhenotype());
			System.out.println("Finished! \nThanks for waiting.");
			return i;
		}
		int factor =(int) (super.theProblem.getSize());
		evalsChange = 1000000;//10000 +(factor*10000);
		System.out.println("Time out = "+ evalsChange);
		int timeOut = evalsChange;
		
		//Reference to the best individual in the population
		FoodelIndividual bestSoFar = InitialisePopution();
		while(timeOut >0) {	
			//Create child
			FoodelIndividual child = null;
			if (rnd.getRnd().nextDouble() < XO_RATE){
				//Create a new Individual using recombination, randomly selecting the parents
				child = new FoodelIndividual(super.theProblem, tournamentSelection(TOUR_SIZE),tournamentSelection(TOUR_SIZE));				
			}
			else{
				//Create a child by copying a single parent
				child = (FoodelIndividual)tournamentSelection(TOUR_SIZE).copy();
			}
			
			child.mutate();
			child.evaluate();
			
			timeOut --;
			
			//Select an Individual with a poor fitness to be replaced
			Individual poor = tournamentSelectWorst(TOUR_SIZE);
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
			  System.out.println("\tVehicles "+bestSoFar.getVehicles() +"\tDist"+ df2.format(bestSoFar.getDistance())+"\tAvg VIP wait "+df2.format(bestSoFar.getavgVIPWait())+"\tFitness "+ df2.format(bestSoFar.evaluate()));
			}
			if (System.currentTimeMillis() > this.end) {//time out
				timeOut=0;
				System.out.println("Timer expired.");
			}
		}

		return bestSoFar;
		
	}
}
