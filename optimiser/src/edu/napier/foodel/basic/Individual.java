package edu.napier.foodel.basic;

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
public class Individual   {
	//Use the RandomSingleton object to get access to a seeded random number generator
	private RandomSingleton rnd =RandomSingleton.getInstance();

	//The genotype is a "grand tour" list of visits
	protected ArrayList<VRPVisit> genotype;

	//The phenotype is a set of routes created from the genotype
	protected ArrayList<ArrayList<VRPVisit>> phenotype;

	//THe problem being solved
	protected VRPProblem problem;

	public Individual( VRPProblem prob) {
		/*
		 * Constructor to create a new random genotype
		 */
		problem = prob;
		genotype = new ArrayList<VRPVisit>();
		for (VRPVisit v : prob.getSolution()){
			genotype.add((VRPVisit)v);
		}
		genotype = randomize(genotype);
		phenotype = null;
	}

	public Individual (VRPProblem prob, Individual parent1, Individual parent2){
		/*
		 * Create a new Individual based on the recombination of genes from <parent1> and <parent2>
		 */
		problem = prob;
		genotype = new ArrayList<VRPVisit>();
		int xPoint = rnd.getRnd().nextInt(parent1.genotype.size());
		
		//copy all of p1 to the xover point
		for (int count =0; count < xPoint; count++ ){
			genotype.add(parent1.genotype.get(count));
		}
		
		//Now add missing genes from p2
		for (int count =0; count < parent2.genotype.size(); count++){
			VRPVisit v = parent2.genotype.get(count);
			if (!genotype.contains(v)){
				genotype.add(v);
			}

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
		phenotype = null;
		int rndGene = rnd.getRnd().nextInt(genotype.size());
		VRPVisit v = genotype.remove(rndGene);
		int addPoint = rnd.getRnd().nextInt(genotype.size());
		genotype.add(addPoint,v);
	}

	public double evaluate() {
		/*
		 * Build a phenotype based upon the genotype
		 * Only build the genotyoe if the phenotype has been set to null
		 * Return the fitness (distance)
		 */
		if (phenotype == null) {
			phenotype = new ArrayList<ArrayList<VRPVisit>> ();
			ArrayList<VRPVisit> newRoute = new ArrayList<VRPVisit>();
			for (VRPVisit v : genotype){
				if (v.getDemand() + routeDemand(newRoute) > problem.getCapacity()){
					//If next visit cannot be added  due to capacity constraint then
					//start new route.
					phenotype.add(newRoute);
					newRoute = new ArrayList<VRPVisit>();
				}
				newRoute.add(v);
			}
			phenotype.add(newRoute);
		}
		return problem.getDistance(phenotype);
	}

	public ArrayList<ArrayList<VRPVisit>> getPhenotype(){
		return phenotype;
	}

	public double getDistance(){
		if (phenotype == null)
			//If the genotype has been changed then evaluate
			evaluate();
		return problem.getDistance(phenotype);
	}

	public int getVehicles() {
		if (phenotype == null)
			//If the genotype has been changed then evaluate
			evaluate();
		return phenotype.size();
	}

	protected int routeDemand(ArrayList<VRPVisit> route){
		//Return the total cumulative demand within <route>
		int demand=0;
		for (VRPVisit visit: route){
			demand += visit.getDemand();
		}
		return demand;
	}

	public Individual copy() {
		//Create a new individual that is a direct copy of this individual
		Individual copy = new Individual(this.problem);
		copy.genotype = (ArrayList<VRPVisit>) this.genotype.clone();
		return copy;
	}

	public void check() {
		/*
		 * Use this method when testing new crossover or mutation operators
		 * 
		 */
		int targetCusts = this.problem.getVisits().size();

		if (targetCusts != this.genotype.size()) {
			System.out.println("Genotype size error");
		}

		int phenocount=0;
		if (phenotype != null) {
			for (ArrayList route: phenotype) {
				phenocount = phenocount + route.size();
			}
			if (targetCusts != phenocount) {
				System.out.println("Phenotype size error");
			}
		}

	}
	
	public String toString() {
		String res="";
		for (VRPVisit v : genotype) {
			res = res + v.toString()+":";
		}
		res = res +"\n";
		
		if (phenotype != null) {
			res = res+"Phenotype\n";
			for (ArrayList<VRPVisit> route: phenotype) {
				for (VRPVisit v : route) {
					res = res + v.toString()+":";
				}
				res = res +"\n";
				
			}
		}
			
		return res;
	}
}
