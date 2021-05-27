package edu.napier.foodel.ioutils;

import java.io.File;
import java.io.PrintWriter;

import edu.napier.foodel.basic.VRPVisit;
import edu.napier.foodel.problem.FoodelProblem;

public class ProblemWriter {
	public static void write(FoodelProblem prob, String fileName)throws Exception{
		File csv = new File(fileName);
	    PrintWriter pw = new PrintWriter(csv);
	    
	 
	    pw.println("Reference,"+prob.getReference());		
	    pw.println("Date/time (dd/MM/yyyy hh:mm),"+prob.getDateTime());
	    pw.println("Start,"+prob.getStartPcode());
	    pw.println("End,"+prob.getEndPCode());
	    pw.println("Vehicle Capacity,"+prob.getCapacity());
	    pw.println("Round time limit (mins),"+(int)(prob.getTimeLimitMS()/60000));//back to mins
	    pw.println("Delivery time per house (mins),"+(int)(prob.getDeliveryTimeMS()/60000));//back to mins
	   
	    pw.println("Postcode,Bags,Notes");
	    for (Object o : prob.getVisits()) {
	    	VRPVisit v = (VRPVisit)o;
	    	String[] splits = v.getName().split(":");
	    	pw.println("Customer,"+splits[0]+","+v.getDemand()+","+splits[1]);
	    }
	    pw.close();
	    
		
		
	}
}
