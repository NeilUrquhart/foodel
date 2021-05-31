package edu.napier.foodel.problem.volunteers;

import edu.napier.foodel.problem.cvrp.CVRPIndividual;

public class VolunteerSolver extends edu.napier.foodel.problem.cvrp.CVRPsolver {

	public VolunteerSolver(double end) {
		super(end);
	}
	
	@Override
	protected CVRPIndividual InitialisePopution() {
		/*
		 * Create VolunteerIndividual objects
		 */
		System.out.println("Setting up problem");
		population.clear();

		
		CVRPIndividual init=null;
		//Initialise population with (semi)random solutions
		CVRPIndividual best = null;
		for (int count=0; count < POP_SIZE; count++){
			CVRPIndividual i;
			if(init != null) {
				if(rnd.getRnd().nextBoolean()) {
					i = init.copy();
					int m = rnd.getRnd().nextInt(10);
					for (int x=0; x < m; x++)
						i.mutate();
				}
			}
			i = new VolunteerIndividual(super.theProblem);

			
			if (best == null) 
				best = i;
			if (i.evaluate() < best.evaluate()) 
				best = i;
			
			if((count%10)==0)
				System.out.println("V " + count);// +" "+best.evaluate() + " "+best.getDistance() +" "+ best.getVehicles());

			population.add(i);
			evalsBudget--;
		}
		return best;
	}

}
