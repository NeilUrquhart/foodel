package edu.napier.foodel.algorithm;

import java.util.ArrayList;
import java.util.Date;

import edu.napier.foodel.basic.Individual;
import edu.napier.foodel.basic.VRPProblem;
import edu.napier.foodel.basic.VRPVisit;
import edu.napier.foodel.problem.FoodelProblem;
import edu.napier.foodel.problem.FoodelVisit;
import edu.napier.foodel.problem.IndexVisit;
import edu.napier.foodel.utils.RandomSingleton;
import edu.napier.ghopper.GHopperInterface;

public class FoodelIndividual extends Individual {
	private double rawDist=0;
	private double avgVIPtime=0;
	private double waitTime=0;

	public double getavgVIPWait() {
		if (phenotype==null)
			this.evaluate();
		return avgVIPtime;
	}

	public double getWaitTime() {
		return waitTime;
	}

	public FoodelIndividual(VRPProblem prob) {
		super(prob);
	}

	public FoodelIndividual (VRPProblem prob, Individual parent1, Individual parent2){
		super(prob,parent1,parent2);
	}



	public FoodelIndividual(VRPProblem prob, ArrayList<VRPVisit> seed) {
		this(prob);
		this.genotype.clear();
		for(VRPVisit v : seed) {
			genotype.add((IndexVisit)v);
		}

	}

	@Override
	public FoodelIndividual copy() {
		//Create a new individual that is a direct copy of this individual
		FoodelIndividual copy = new FoodelIndividual(this.problem);
		copy.genotype = (ArrayList<VRPVisit>) this.genotype.clone();
		return copy;
	}

	public ArrayList<ArrayList<VRPVisit>> getPhenotype(){
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
		FoodelProblem fProblem = (FoodelProblem)problem;

		//System.out.println(fProblem.getTimeOnlyformatter().format(timeLimit));
		long deliveryTime = ((FoodelProblem)problem).getDeliveryTimeMS();
		int veh= fProblem.getVehicleQty();
		int split = fProblem.getCapacity()/veh;
		int vipCount=0;

		if (phenotype == null) {
			avgVIPtime=0;
			phenotype = new ArrayList<ArrayList<VRPVisit>> ();

			IndexVisit depot = (IndexVisit) problem.getStart();
			IndexVisit finish = (IndexVisit) fProblem.getEnd();
			IndexVisit prev= depot;
			IndexVisit curr = null;
			ArrayList<VRPVisit> newRoute = new ArrayList<VRPVisit>();
			long time = fProblem.getStartTime();//0;
			boolean first = true;
			long twstart;
			long twend;
			int count=0;
			for (VRPVisit v : genotype){
				count++;
				curr = (IndexVisit)v;
				if(((FoodelVisit)curr).getWinstart() > -1){
					long start = fProblem.getStartTime();
					twstart = ((FoodelVisit)curr).getWinstart();
					twstart = twstart - start;
					twend = ((FoodelVisit)curr).getWinend();
					twend = twend - start;

				}else {
					twstart=0;
					twend = Long.MAX_VALUE;
				}

				double newTime = time + GHopperInterface.getJourney(prev,curr, fProblem.getMode()).getTravelTimeMS();

				//check for twstart
				if (time < twstart) 
					time = twstart;
				if ((v.getDemand() + routeDemand(newRoute) > problem.getCapacity())|| (newTime> twend)||((count%split)==0)){


					//If next visit cannot be added  due to capacity constraint OR  being passed the TW then
					//start new route.
					phenotype.add(newRoute);
					newRoute = new ArrayList<VRPVisit>();
					time = fProblem.getStartTime();//0;
					prev = depot;
				}


				time = time + GHopperInterface.getJourney(prev, curr, fProblem.getMode()).getTravelTimeMS();
				if(time < twstart) {
					waitTime = twstart - time;
					time = twstart;
				}

				FoodelVisit fCurr = (FoodelVisit)curr;
				if (fCurr.isVip()) {
					this.avgVIPtime = this.avgVIPtime + time;
					vipCount++;
				}
				time = time + deliveryTime;

				//System.out.println(time + " " + fProblem.getTimeOnlyformatter().format(time));
				newRoute.add(v);

			}
			prev = curr;
			phenotype.add(newRoute);
			if (vipCount >0)
				this.avgVIPtime = (this.avgVIPtime/60000)/vipCount;



			this.rawDist = fProblem.getDistance(phenotype);
		}
		//Get weighted distance


		return ((fProblem.getWeightedDistance(this))); 
	}	
	//	@Override
	//	public double evaluate() {
	//		/*
	//		 * Build a phenotype based upon the genotype
	//		 * Only build the genotyoe if the phenotype has been set to null
	//		 * Return the fitness (distance)
	//		 * 
	//		 * This method has been overriden to allow a custom fitness function
	//		 * 
	//		 * Add in code to ensure that route is not over the allowed time
	//		 * 
	//		 */
	//		FoodelProblem fProblem = (FoodelProblem)problem;
	//		long timeLimit = (((FoodelProblem)problem).getTimeLimitMS()) + fProblem.getStartTime();
	//
	//		//System.out.println(fProblem.getTimeOnlyformatter().format(timeLimit));
	//		long deliveryTime = ((FoodelProblem)problem).getDeliveryTimeMS();
	//		
	//		int vipCount=0;
	//		
	//		if (phenotype == null) {
	//			avgVIPtime=0;
	//			phenotype = new ArrayList<ArrayList<VRPVisit>> ();
	//
	//			IndexVisit depot = (IndexVisit) problem.getStart();
	//			IndexVisit finish = (IndexVisit) fProblem.getEnd();
	//			IndexVisit prev= depot;
	//			IndexVisit curr = null;
	//			ArrayList<VRPVisit> newRoute = new ArrayList<VRPVisit>();
	//			long time = fProblem.getStartTime();//0;
	//			boolean first = true;
	//			long twstart;
	//			long twend;
	//			for (VRPVisit v : genotype){
	//				
	//				curr = (IndexVisit)v;
	//				if(((FoodelVisit)curr).getWinstart() > -1){
	//					long start = fProblem.getStartTime();
	//					twstart = ((FoodelVisit)curr).getWinstart();
	//					twstart = twstart - start;
	//					twend = ((FoodelVisit)curr).getWinend();
	//					twend = twend - start;
	//					
	//				}else {
	//					twstart=0;
	//					twend = Long.MAX_VALUE;
	//				}
	//
	//				double newTime = time + GHopperInterface.getJourney(prev,curr, fProblem.getMode()).getTravelTimeMS();
	//				
	//				//check for twstart
	//				if (time < twstart) 
	//					time = twstart;
	//				if (((v.getDemand() + routeDemand(newRoute) > problem.getCapacity())|| ((newTime)> timeLimit))||
	//						(newTime> twend)){
	//					
	//				
	//					//If next visit cannot be added  due to capacity constraint OR  being passed the TW then
	//					//start new route.
	//					phenotype.add(newRoute);
	//					newRoute = new ArrayList<VRPVisit>();
	//					time = fProblem.getStartTime();//0;
	//					prev = depot;
	//				}
	//				
	//				
	//				time = time + GHopperInterface.getJourney(prev, curr, fProblem.getMode()).getTravelTimeMS();
	//				if(time < twstart) {
	//					waitTime = twstart - time;
	//					time = twstart;
	//				}
	//				
	//				FoodelVisit fCurr = (FoodelVisit)curr;
	//				if (fCurr.isVip()) {
	//					this.avgVIPtime = this.avgVIPtime + time;
	//					vipCount++;
	//				}
	//				time = time + deliveryTime;
	//				
	//				//System.out.println(time + " " + fProblem.getTimeOnlyformatter().format(time));
	//				newRoute.add(v);
	//
	//			}
	//			prev = curr;
	//			phenotype.add(newRoute);
	//			if (vipCount >0)
	//			  this.avgVIPtime = (this.avgVIPtime/60000)/vipCount;
	//			
	//			
	//
	//			this.rawDist = fProblem.getDistance(phenotype);
	//		}
	//		//Get weighted distance
	//		
	//
	//		return (this.phenotype.size()*((fProblem.getWeightedDistance(this)))); 
	//	}	


	//	@Override
	//	public double evaluate() {
	//		/*
	//		 * Build a phenotype based upon the genotype
	//		 * Only build the genotyoe if the phenotype has been set to null
	//		 * Return the fitness (distance)
	//		 * 
	//		 * This method has been overriden to allow a custom fitness function
	//		 * 
	//		 * Add in code to ensure that route is not over the allowed time
	//		 * 
	//		 */
	//		
	//		double timeLimit = ((FoodProblem)problem).getTimeLimitMS();
	//		double deliveryTime = ((FoodProblem)problem).getDeliveryTimeMS();
	//		FoodProblem fProblem = (FoodProblem)problem;
	//		int vipCount=0;
	//		
	//		if (phenotype == null) {
	//			avgVIPtime=0;
	//			phenotype = new ArrayList<ArrayList<VRPVisit>> ();
	//
	//			IVisit depot = (IVisit) problem.getStart();
	//			IVisit finish = (IVisit) fProblem.getEnd();
	//			IVisit prev= depot;
	//			IVisit curr = null;
	//			ArrayList<VRPVisit> newRoute = new ArrayList<VRPVisit>();
	//			double time = 0;
	//			boolean first = true;
	//			for (VRPVisit v : genotype){
	//				curr = (IVisit)v;
	////				if (first ) {
	////
	////					time = (OSMAccessHelper.getJourney(prev, curr, fProblem.getMode()).getTravelTimeMS());
	////					first = false;
	////				}
	//				//double timeToReturn = deliveryTime +  OSMAccessHelper.getJourney(curr,finish, fProblem.getMode()).getTravelTimeMS();
	//				double newTime = time + OSMAccessHelper.getJourney(prev,curr, fProblem.getMode()).getTravelTimeMS();
	//				if ((v.getDemand() + routeDemand(newRoute) > problem.getCapacity())|| ((newTime)> timeLimit)){
	//					//If next visit cannot be added  due to capacity constraint then
	//					//start new route.
	//					phenotype.add(newRoute);
	//					newRoute = new ArrayList<VRPVisit>();
	//					time = 0;
	//					prev = depot;
	//				}
	//				time = time + OSMAccessHelper.getJourney(prev, curr, fProblem.getMode()).getTravelTimeMS();
	//				FoodVisit fCurr = (FoodVisit)curr;
	//				if (fCurr.isVip()) {
	//					this.avgVIPtime = this.avgVIPtime + time;
	//					vipCount++;
	//				}
	//				time = time + deliveryTime;
	//				newRoute.add(v);
	//
	//			}
	//			prev = curr;
	//			phenotype.add(newRoute);
	//			if (vipCount >0)
	//			  this.avgVIPtime = (this.avgVIPtime/60000)/vipCount;
	//			
	//			this.rawDist = fProblem.getDistance(phenotype);
	//		}
	//		//Get weighted distance
	//		
	//		
	//		return fProblem.getWeightedDistance(phenotype);
	//	}	
	@Override
	public void mutate() {
		RandomSingleton rnd =RandomSingleton.getInstance();
		float choice = rnd.getRnd().nextFloat();
		if (choice>=0.8) {
			super.mutate();
		}else if (choice >= 0.6){
			//Move VIP forward
			int rndGene = rnd.getRnd().nextInt(genotype.size()-1);
			FoodelVisit v1 = (FoodelVisit)genotype.get(rndGene);
			int tries=20;
			while(tries >0) {
				tries--;
				v1 = (FoodelVisit)genotype.get(rndGene);
				if (v1.isVip()) tries=0;
			}
			genotype.remove(v1);
			genotype.add(0,v1);
		}
		else {
			//Mutate the genotype, by randomly swapping two adjacent genes
			phenotype = null;
			int rndGene = rnd.getRnd().nextInt(genotype.size()-1);
			VRPVisit v1 = genotype.remove(rndGene);
			genotype.add(rndGene+1,v1);
		}
	}


	public double getDistance() {
		if (phenotype==null)
			this.evaluate();

		return this.rawDist;
	}
}
