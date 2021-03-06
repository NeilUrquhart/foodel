package edu.napier.foodel.problem.fixedvans;

import java.util.ArrayList;
import java.util.Date;

import edu.napier.foodel.geo.GHopperInterface;
import edu.napier.foodel.problemTemplate.FoodelVisit;
import edu.napier.foodel.problemTemplate.FoodelIndividual;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.utils.RandomSingleton;

public class FixedVansIndividual extends FoodelIndividual {

	

	protected double rawDist=0;

	public FixedVansIndividual(FoodelProblem prob) {
		super(prob);

	}

	
	public FixedVansIndividual (FoodelProblem prob, FoodelIndividual parent1, FoodelIndividual parent2){
		super(prob,parent1,parent2);
	}

	//	public FixedVansIndividual(FoodelProblem prob, ArrayList<FoodelVisit> seed) {
	//		this(prob);
	//		this.genotype.clear();
	//		for(FoodelVisit v : seed) {
	//			genotype.add((FoodelVisit)v);
	//		}
	//
	//	}

	@Override
	public FixedVansIndividual copy() {
		//Create a new individual that is a direct copy of this individual
		FixedVansIndividual copy = new FixedVansIndividual(this.problem);
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
		FixedVansProblem fProblem = (FixedVansProblem)problem;
	
		if (phenotype == null) {
			phenotype = new ArrayList<ArrayList<FoodelVisit>> ();

			//1 Add empty routes
			for (int c=0;  c< fProblem.getVans(); c++) {
				phenotype.add(new ArrayList<FoodelVisit>());
			}
			//2 Add visits to routes
			for(FoodelVisit visit : genotype) {
			// - try prefferred route
				FVGene g = (FVGene) visit;
				ArrayList<FoodelVisit> pref = phenotype.get(g.getVan());
//			//1 Check and correct demand
				if ((getDemand(pref)+g.getDemand())<= this.problem.getVehicleCapacity())
					pref.add(g);
				else {//Find a route
					for (ArrayList<FoodelVisit> r : phenotype) {
						if ((getDemand(r)+g.getDemand())<= this.problem.getVehicleCapacity()) {
							r.add(g);
							break;
						}
					}
				}
			}
//			
//			for (int v=0; v < ((FixedVansProblem)this.problem).getVans(); v++) {
//				int demand =0;
//				for (FoodelVisit visit : this.genotype) {
//					FVGene g = (FVGene) visit;
//					if (g.getVan() == v) {
//						demand = demand + g.getDemand();
//					}
//					if (demand > this.problem.getVehicleCapacity()) {
//						//Correct
//						g.setVan(v+1);//Move to next route
//						demand = demand - g.getDemand();
//					}
//				}
//			}
//
//			//2 Build Genotype
//
//			for (int v=0; v < ((FixedVansProblem)this.problem).getVans(); v++) {
//				ArrayList<FoodelVisit> route = new ArrayList<FoodelVisit>();
//				
//				for (FoodelVisit visit : this.genotype) {
//					FVGene g = (FVGene) visit;
//					if (g.getVan() == v) {
//						route.add(visit);
//					}
//				}
//				phenotype.add(route);
//			}
		this.rawDist = fProblem.getSolutionDistance(phenotype);
		}
	
	//Get weighted distance


	return this.rawDist;
}	

private int getDemand(ArrayList<FoodelVisit> route ) {
	int res=0;
	for (FoodelVisit v : route)
		res = res + v.getDemand();
	return res;
}
//				)

@Override
public void mutate() {


	if (rnd.getRnd().nextBoolean()) {
		super.mutate();
	}


	else {//Mutate van
		int rndGene = rnd.getRnd().nextInt(genotype.size());
		FVGene g= (FVGene) genotype.get(rndGene);
		g.setVan(rnd.getRnd().nextInt(((FixedVansProblem)this.problem).getVans()));
	}

}


protected int routeDemand(ArrayList<FVGene> route){
	//Return the total cumulative demand within <route>
	int demand=0;
	for (FVGene g: route){
		demand += g.getDemand();
	}
	return demand;
}

public double getDistance() {
	if (phenotype==null)
		this.evaluate();

	return this.rawDist;
}
}
