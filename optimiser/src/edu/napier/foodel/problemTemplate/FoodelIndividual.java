package edu.napier.foodel.problemTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.napier.foodel.utils.RandomSingleton;



/*
 * Neil Urquhart 2019
 * This class represents a single CVRP solution to be used within an Evolutionary Algorithm.
 * 
 * The basic solution (a grand tour) is stored in the genotype. Once the solution has been
 * evaluated then the solution is stored in the genotype.
 * 
 */
public abstract class  FoodelIndividual   {
	//Use the RandomSingleton object to get access to a seeded random number generator
	private RandomSingleton rnd =RandomSingleton.getInstance();

	//The genotype is a "grand tour" list of visits
	protected ArrayList<FoodelVisit> genotype;

	//The phenotype is a set of routes created from the genotype
	protected ArrayList<ArrayList<FoodelVisit>> phenotype;

	//THe problem being solved
	protected FoodelProblem problem;

	public FoodelIndividual( FoodelProblem prob) {
		/*
		 * Constructor to create a new random genotype
		 */
		problem = prob;
		genotype = new ArrayList<>();
		for (FoodelVisit v : prob.getVisitList()){
			genotype.add(v);
		}
		genotype = randomize(genotype);
		phenotype = null;
	}

	public FoodelIndividual (FoodelProblem prob, FoodelIndividual parent1, FoodelIndividual parent2){
		/*
		 * Create a new Individual based on the recombination of genes from <parent1> and <parent2>
		 */
		problem = prob;
		genotype = new ArrayList<>();
		int xPoint = rnd.getRnd().nextInt(parent1.genotype.size());

		//copy all of p1 to the xover point
		for (int count =0; count < xPoint; count++ ){
			genotype.add(parent1.genotype.get(count));
		}

		//Now add missing genes from p2
		for (int count =0; count < parent2.genotype.size(); count++){
			FoodelVisit v = parent2.genotype.get(count);
			if (!genotype.contains(v)){
				genotype.add(v);
			}

		}
	}

	public void nn() {
		//Initialise using NN heuristic
		FoodelVisit currentPos = problem.getStart();
		ArrayList<FoodelVisit> temp = new ArrayList<FoodelVisit>(genotype);
		genotype.clear();
		
		while(temp.size()>0) {
			
			//Find nearest n
			int bestPos=0;
			double bestDist = Double.MAX_VALUE;
			for (int count =0; count < temp.size(); count ++) {
				FoodelVisit v = temp.get(count);
				double d = problem.getDistance(v,currentPos);
				if (d < bestDist) {
					bestDist = d;
					bestPos = count;
				}
			}
			currentPos = temp.remove(bestPos);
			genotype.add(currentPos);

			
		}
	}

	private ArrayList randomize(ArrayList list) {
		// Randomly shuffle the contents of <list>
		Random  r= rnd.getInstance().getRnd();

		for (int c=0; c < list.size();c++) {
			Object o = list.remove(r.nextInt(list.size()));
			list.add(r.nextInt(list.size()),o);
		}
		return list;
	}

	public void mutate() {
		//Mutate the genotype, by randomly moving a gene.
		if (rnd.getRnd().nextDouble() <0.4) {
			phenotype = null;
			int rndGene = rnd.getRnd().nextInt(genotype.size());
			FoodelVisit v = genotype.remove(rndGene);
			int addPoint = rnd.getRnd().nextInt(genotype.size());
			genotype.add(addPoint,v);
		}else {
			nnmutate();
		}
	}

	private void nnmutate() {
		//Mutate the genotype, by randomly moving a gene to its nearest neighbour
		phenotype = null;
		int sp = rnd.getRnd().nextInt(genotype.size()-1);
		FoodelVisit gene = genotype.get(sp);

		//Find nearest n
		int bestPos=0;
		double bestDist = Double.MAX_VALUE;
		for (int count =0; count < genotype.size(); count ++) {
			FoodelVisit v = genotype.get(count);
			if (v != gene) {
				double d = problem.getDistance(v,gene);
				if (d < bestDist) {
					bestDist = d;
					bestPos = count;
				}
			}
		}
		gene = genotype.remove(bestPos);
		sp++;
		genotype.add(sp,gene);
	}


	public abstract double evaluate();// {
	//		/*
	//		 * Build a phenotype based upon the genotype
	//		 * Only build the genotyoe if the phenotype has been set to null
	//		 * Return the fitness (distance)
	//		 */
	//		if (phenotype == null) {
	//			phenotype = new ArrayList<ArrayList<FoodelVisit>> ();
	//			ArrayList<FoodelVisit> newRoute = new ArrayList<>();
	//			for (FoodelVisit v : genotype){
	//				if (v.getDemand() + routeDemand(newRoute) > problem.getVehicleCapacity()){
	//					//If next visit cannot be added  due to capacity constraint then
	//					//start new route.
	//					phenotype.add(newRoute);
	//					newRoute = new ArrayList<>();
	//				}
	//				newRoute.add(v);
	//			}
	//			phenotype.add(newRoute);
	//		}
	//		return problem.getSolutionDistance(phenotype);
	//	}

	public ArrayList<ArrayList<FoodelVisit>> getPhenotype(){
		return phenotype;
	}

	public double getDistance(){
		if (phenotype == null)
			//If the genotype has been changed then evaluate
			evaluate();
		return problem.getSolutionDistance(phenotype);
	}

	public int getVehicles() {
		if (phenotype == null)
			//If the genotype has been changed then evaluate
			evaluate();
		return phenotype.size();
	}



	public abstract FoodelIndividual copy();// {
	//		//Create a new individual that is a direct copy of this individual
	//		FoodelIndividual copy = new FoodelIndividual(this.problem);
	//		copy.genotype = (ArrayList<FoodelVisit>) this.genotype.clone();
	//		return copy;
	//	}


	public String toString() {
		StringBuilder res= new StringBuilder();
		for (FoodelVisit v : genotype) {
			res.append(v.toString()+":");
		}
		res.append("\n");

		if (phenotype != null) {
			res.append("Phenotype\n");
			for (ArrayList<FoodelVisit> route: phenotype) {
				for (FoodelVisit v : route) {
					res.append( v.toString()+":");
				}
				res.append("\n");

			}
		}
		return res.toString();
	}
}
