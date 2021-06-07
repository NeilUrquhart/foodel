package edu.napier.foodel.problem.cvrp;

import java.util.ArrayList;
import java.util.Date;

import edu.napier.foodel.geo.GHopperInterface;
import edu.napier.foodel.problemTemplate.FoodelVisit;
import edu.napier.foodel.problemTemplate.FoodelIndividual;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.utils.RandomSingleton;

public class CVRPIndividual extends FoodelIndividual {
	protected double rawDist=0;


	public CVRPIndividual(FoodelProblem prob) {
		super(prob);
	}

	public CVRPIndividual (FoodelProblem prob, FoodelIndividual parent1, FoodelIndividual parent2){
		super(prob,parent1,parent2);
	}



	public CVRPIndividual(FoodelProblem prob, ArrayList<FoodelVisit> seed) {
		this(prob);
		this.genotype.clear();
		for(FoodelVisit v : seed) {
			genotype.add((FoodelVisit)v);
		}

	}

	@Override
	public CVRPIndividual copy() {
		//Create a new individual that is a direct copy of this individual
		CVRPIndividual copy = new CVRPIndividual(this.problem);
		copy.genotype = (ArrayList<FoodelVisit>) this.genotype.clone();
		return copy;
	}

	public ArrayList<ArrayList<FoodelVisit>> getPhenotype(){
		return phenotype;
	}

	public void resetPheno() {
		phenotype =null;
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
		int split = fProblem.getNoVisits()/veh;
		
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
				
				double jt = GHopperInterface.getJourney(prev,curr, fProblem.getMode()).getTravelTimeMS();
				double newTime = time + jt;

				//if ((v.getDemand() + routeDemand(newRoute) > problem.getVehicleCapacity())){//||((count%split)==0)){
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


		return this.rawDist;//((fProblem.getWeightedDistance(this))); 
	}	



	@Override
	public void mutate() {
		//RandomSingleton rnd =RandomSingleton.getInstance();
		//float choice = rnd.getRnd().nextFloat();
		//if (choice>=0.8) {
			super.mutate();
		//}
		//else {
		//	//Mutate the genotype, by randomly swapping two adjacent genes
		//	phenotype = null;
		//	int rndGene = rnd.getRnd().nextInt(genotype.size()-1);
		//	FoodelVisit v1 = genotype.remove(rndGene);
		//	genotype.add(rndGene+1,v1);
		//}
	}

	
	protected int routeDemand(ArrayList<FoodelVisit> route){
	//Return the total cumulative demand within <route>
	int demand=0;
	for (FoodelVisit visit: route){
		demand += visit.getDemand();
	}
	return demand;
}

	public double getDistance() {
		if (phenotype==null)
			this.evaluate();

		return this.rawDist;
	}
}
