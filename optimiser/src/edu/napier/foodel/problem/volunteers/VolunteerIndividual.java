package edu.napier.foodel.problem.volunteers;

import java.util.ArrayList;

import edu.napier.foodel.geo.GHopperInterface;
import edu.napier.foodel.problem.cvrp.CVRPIndividual;
import edu.napier.foodel.problem.cvrp.CVRPProblem;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.problemTemplate.FoodelVisit;

public class VolunteerIndividual extends CVRPIndividual {

	public VolunteerIndividual(FoodelProblem prob) {
		super(prob);
	}

	
	@Override
	public double evaluate() {
		/*
		 * Build a phenotype based upon the genotype
		 * Only build the genotyoe if the phenotype has been set to null
		 * Return the fitness (distance)
		 * 
		 * This method has been overriden to allow a custom fitness function
		 * 
		 * Add in code to ensure that route is not over the allowed time
		 * 
		 */
		CVRPProblem fProblem = (CVRPProblem)problem;
		long timeLimit = fProblem.getTimeLimitMS();
		//System.out.println(fProblem.getTimeOnlyformatter().format(timeLimit));
		long deliveryTime = fProblem.getDeliveryTimeMS();
		int veh= fProblem.getVehicleQty();
		//int split = fProblem.getVehicleCapacity()/veh;
		
		if (phenotype == null) {
			phenotype = new ArrayList<ArrayList<FoodelVisit>> ();

			FoodelVisit depot = (FoodelVisit) problem.getStart();
			FoodelVisit finish = (FoodelVisit) fProblem.getEnd();
			FoodelVisit prev= depot;
			FoodelVisit curr = null;
			ArrayList<FoodelVisit> newRoute = new ArrayList<FoodelVisit>();
			long time = 0;//fProblem.getStartTime();//0;
			boolean first = true;
			int count=0;
			for (FoodelVisit v : genotype){
				count++;
				curr = (FoodelVisit)v;
				

				double newTime = time + GHopperInterface.getJourney(prev,curr, fProblem.getMode()).getTravelTimeMS();

				//if ((v.getDemand() + routeDemand(newRoute) > problem.getVehicleCapacity())||((count%split)==0)){
					if ((v.getDemand() + routeDemand(newRoute) > problem.getVehicleCapacity())||(newTime >timeLimit )){


					//If next visit cannot be added  due to capacity constraint 
					//start new route.
					phenotype.add(newRoute);
					newRoute = new ArrayList<FoodelVisit>();
					time = 0;
					prev = depot;
				}


				time = time + GHopperInterface.getJourney(prev, curr, fProblem.getMode()).getTravelTimeMS();
				
				FoodelVisit fCurr = (FoodelVisit)curr;
				time = time + deliveryTime;

				newRoute.add(v);

			}
			prev = curr;
			phenotype.add(newRoute);
		


			this.rawDist = fProblem.getSolutionDistance(phenotype);
		}
		//Get weighted distance


		return ((fProblem.getWeightedDistance(this))); 
	}	

}
