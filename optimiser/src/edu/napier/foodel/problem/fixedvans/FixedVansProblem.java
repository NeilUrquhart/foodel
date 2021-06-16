package edu.napier.foodel.problem.fixedvans;

import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.napier.foodel.geo.GHopperInterface;
import edu.napier.foodel.geo.Journey;
import edu.napier.foodel.ioutils.GPXWriter;
import edu.napier.foodel.ioutils.HTMmapwriter;
import edu.napier.foodel.problem.cvrp.CVRPProblem;
import edu.napier.foodel.problemTemplate.FoodelVisit;
import edu.napier.foodel.problemTemplate.FoodelProblem;

/*
 * Neil Urquhart 2021
 * 
 * This problem represents a CVRP with a FIXED no of vehicles

 * 
 */
public class FixedVansProblem extends CVRPProblem {
	private int vans; //No of vans 
	
   public void setVans(int v) {
	   vans =v;
   }
   
   public int getVans() {
	   return vans;
   }
	
}
