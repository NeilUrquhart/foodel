package edu.napier.foodel.facade;
/*
 * Neil Urquhart 2019
 * This programme tests a set of CVRP problem instances, using a range of solvers to produce solutions.

 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;


import edu.napier.foodel.algorithm.FoodelEA;
import edu.napier.foodel.basic.Journey;
import edu.napier.foodel.basic.VRPVisit;
import edu.napier.foodel.ioutils.GPXWriter;
import edu.napier.foodel.ioutils.HTMmapwriter;
import edu.napier.foodel.ioutils.KMLWriter;
import edu.napier.foodel.problem.FoodelProblem;
import edu.napier.foodel.problem.FoodelVisit;
import edu.napier.foodel.utils.RandomSingleton;
import edu.napier.ghopper.GHopperInterface;

public class FoodelFacade implements Runnable {
	private  FoodelProblem myVRP;

	public  void setProblem(FoodelProblem aProb) {
		myVRP = aProb;
	}

	public  void run(){
		RandomSingleton.getInstance().setSeed(86);
		double runTime = 60000*15; //15 mins
		double end = System.currentTimeMillis() + runTime;
		GHopperInterface.setCacheSize();
		FoodelEA eaSolve = new FoodelEA(end);
		myVRP.solve(eaSolve);
		System.out.print('~');//Used to signal that we're done
	}

//	public String writeOut(String outFolder) throws Exception{
//		writeOutGPX(outFolder);	
//		return writeOutCSV(outFolder);
//	}

//	private String writeOutCSV(String outFolder) throws Exception {
//
//		String buffer="";
//		int r=1;
//		long deliveryTime = myVRP.getDeliveryTimeMS();
//		long time = myVRP.getStartTime();
//
//		ArrayList<ArrayList<VRPVisit>> solution = myVRP.getCVRPSolution();
//		if (myVRP.getInitialVisit()!= null) {
//			ArrayList<VRPVisit> run =solution.get(0);
//			run.add(0,myVRP.getInitialVisit());
//		}
//
//		for(ArrayList<VRPVisit> run :solution){
//			int c=0;
//			if (this.myVRP.isConcurrent())
//				time = myVRP.getStartTime();
//
//
//			File csvF = new File(outFolder+"/Run"+r+".csv");
//			PrintWriter csv = new PrintWriter(csvF);
//
//
//			buffer +=("Run " + r+ "\n");
//			csv.println("Run," + r+ "\n");
//			csv.println("Route number,Order ID,Products,Shipping Address,Postcode,Estim Dlivery Time");
//			Visit prev = myVRP.getStart();
//			buffer +=("Start" + myVRP.getTimeOnlyformatter().format(time)+"\n");
//			csv.println("0,Start,,,,,"+myVRP.getStart().getName()+"," + myVRP.getTimeOnlyformatter().format(time));
//
//			for (VRPVisit v : run){
//				c++;
//				long winStart=0;
//				if (v instanceof FoodelVisit) {
//					winStart = ((FoodelVisit)v).getWinstart();
//				}
//
//
//				Visit curr = v;
//
//				Journey j = GHopperInterface.getJourney(prev, curr, myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//				//window
//				if (time < winStart ) {
//					time = winStart;
//				}
//				//done
//				buffer +=(c + " " +v.getName() + " " + myVRP.getTimeOnlyformatter().format(time)+"\n");
//				if (v instanceof FoodelVisit){
//					FoodelVisit f = (FoodelVisit)v;
//					csv.println(c + "," +f.getName()+ ","+f.getOrder()+","+f.getAddress()+","+f.getPostCode()+"," + myVRP.getTimeOnlyformatter().format(time));
//				}
//				else {
//					csv.println(c + "," +v.getName() + ",,,," + myVRP.getTimeOnlyformatter().format(time));
//				}
//				time  = time + deliveryTime;
//
//				prev = curr;
//			}
//
//			/*
//			 * Add journey to end...
//			 */
//
//			if (myVRP.getEnd() != null) {
//				Journey j = GHopperInterface.getJourney(prev, myVRP.getEnd() /*new Visit( "",myVRP.getEnd().getX(),myVRP.getEnd().getY())*/, myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//				buffer +=("End"  + " " + myVRP.getTimeOnlyformatter().format(time)+"\n");
//				csv.println( (c+1)+"," +"End"+ ",,,"+myVRP.getEndPCode()+"," + myVRP.getTimeOnlyformatter().format(time));	
//				time  = time + deliveryTime;
//
//			}
//			r++;
//			csv.close();
//
//		}
//
//		File summaryFile  = new File(outFolder+"/summary.csv");
//		PrintWriter summary = new PrintWriter(summaryFile);
//		summary.println(buffer);
//		summary.close();
//		return buffer;
//	}

//	private void writeOutGPX(String outFolder) throws Exception {
//
//		int r=1;
//		long deliveryTime = myVRP.getDeliveryTimeMS();
//		long time = myVRP.getStartTime();
//
//		ArrayList<ArrayList<VRPVisit>> solution = myVRP.getCVRPSolution();
//		if (myVRP.getInitialVisit()!= null) {
//			ArrayList<VRPVisit> run =solution.get(0);
//			run.add(0,myVRP.getInitialVisit());
//		}
//
//		for(ArrayList<VRPVisit> run :solution){
//			int c=0;
//			if (this.myVRP.isConcurrent())
//				time = myVRP.getStartTime();
//
//			GPXWriter gpx = new GPXWriter();
//			gpx.addWayPoint(myVRP.getStart().getX(), myVRP.getStart().getY(), "Start");
//	
//			Visit prev = myVRP.getStart();
//			for (VRPVisit v : run){
//				c++;
//				String description = c +" ";
//				long winStart=0;
//				if (v instanceof FoodelVisit) {
//					winStart = ((FoodelVisit)v).getWinstart();
//					if (((FoodelVisit)v).getAddress()!= null) {
//						description += ((FoodelVisit)v).getAddress().replace("&", " and ");
//						description += " ";
//					}
//					if (((FoodelVisit)v).getOrder()!= null) 
//						description += ((FoodelVisit)v).getOrder().replace("&", " and ");
//				}else 
//					description += v.getName();
//
//
//				gpx.addWayPoint(v.getX(), v.getY(), description);
//		
//				Visit curr = v;
//				Journey j = GHopperInterface.getJourney(prev, curr, myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//				//window
//				if (time < winStart ) {
//					time = winStart;
//				}
//				//done
//				
//				time  = time + deliveryTime;
//
//
//				ArrayList<Visit> p = j.getPath();
//				ArrayList<Double> lat = new ArrayList<Double>();
//				ArrayList<Double> lon = new ArrayList<Double>();
//
//				for (Visit l : p){
//					lat.add(l.getX());
//					lon.add(l.getY());	
//				}
//				gpx.addPath(lat, lon);
//				prev = curr;
//			}
//					/*
//			 * Add journey to end...
//			 */
//
//			if (myVRP.getEnd() != null) {
//				Journey j = GHopperInterface.getJourney(prev, myVRP.getEnd(), myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//		
//	
//				time  = time + deliveryTime;
//
//
//				ArrayList<Visit> p = j.getPath();
//				ArrayList<Double> lat = new ArrayList<Double>();
//				ArrayList<Double> lon = new ArrayList<Double>();
//
//				for (Visit l : p){
//					lat.add(l.getX());
//					lon.add(l.getY());	
//				}
//				gpx.addPath(lat, lon);	
//				gpx.addWayPoint(myVRP.getEnd().getX(), myVRP.getEnd().getY(), "End");
//			}
//			//Done end
//			gpx.write(outFolder+"/"+r+".gpx", "gpxTest");
//
//			r++;
//	
//		}	
//	}

	
	public String getGPX(int r) {

		long deliveryTime = myVRP.getDeliveryTimeMS();
		long time = myVRP.getStartTime();

		ArrayList<ArrayList<VRPVisit>> solution = myVRP.getCVRPSolution();
		if (myVRP.getInitialVisit()!= null) {
			ArrayList<VRPVisit> run =solution.get(0);
			run.add(0,myVRP.getInitialVisit());
		}

		//for(ArrayList<VRPVisit> run :solution){
		ArrayList<VRPVisit> run  = solution.get(r-1);
			int c=0;
			if (this.myVRP.isConcurrent())
				time = myVRP.getStartTime();

			GPXWriter gpx = new GPXWriter();
			gpx.addWayPoint(myVRP.getStart().getX(), myVRP.getStart().getY(), "Start");
	
			VRPVisit prev = myVRP.getStart();
			for (VRPVisit v : run){
				c++;
				String description = c +" ";
				long winStart=0;
				if (v instanceof FoodelVisit) {
					winStart = ((FoodelVisit)v).getWinstart();
					if (((FoodelVisit)v).getAddress()!= null) {
						description += ((FoodelVisit)v).getAddress().replace("&", " and ");
						description += " ";
					}
					if (((FoodelVisit)v).getOrder()!= null) 
						description += ((FoodelVisit)v).getOrder().replace("&", " and ");
				}else 
					description += v.getName();


				gpx.addWayPoint(v.getX(), v.getY(), description);
		
				VRPVisit curr = v;
				Journey j = GHopperInterface.getJourney(prev, curr, myVRP.getMode());
				time = time + ( j.getTravelTimeMS()) ;
				//window
				if (time < winStart ) {
					time = winStart;
				}
				//done
				
				time  = time + deliveryTime;


				ArrayList<VRPVisit> p = j.getPath();
				ArrayList<Double> lat = new ArrayList<Double>();
				ArrayList<Double> lon = new ArrayList<Double>();

				for (VRPVisit l : p){
					lat.add(l.getX());
					lon.add(l.getY());	
				}
				gpx.addPath(lat, lon);
				prev = curr;
			}
					/*
			 * Add journey to end...
			 */

			if (myVRP.getEnd() != null) {
				Journey j = GHopperInterface.getJourney(prev, myVRP.getEnd(), myVRP.getMode());
				time = time + ( j.getTravelTimeMS()) ;
		
	
				time  = time + deliveryTime;


				ArrayList<VRPVisit> p = j.getPath();
				ArrayList<Double> lat = new ArrayList<Double>();
				ArrayList<Double> lon = new ArrayList<Double>();

				for (VRPVisit l : p){
					lat.add(l.getX());
					lon.add(l.getY());	
				}
				gpx.addPath(lat, lon);	
				gpx.addWayPoint(myVRP.getEnd().getX(), myVRP.getEnd().getY(), "End");
			}
			//Done end
			//gpx.write(outFolder+"/"+r+".gpx", "gpxTest");
			return gpx.getText(myVRP.getReference() + " "+r);
			//r++;
	
		//}	
	}
//	private  void writeOutKML(String outFolder) throws Exception {
//
//		int r=1;
//		long deliveryTime = myVRP.getDeliveryTimeMS();
//		long time = myVRP.getStartTime();
//
//		ArrayList<ArrayList<VRPVisit>> solution = myVRP.getCVRPSolution();
//		if (myVRP.getInitialVisit()!= null) {
//			ArrayList<VRPVisit> run =solution.get(0);
//			run.add(0,myVRP.getInitialVisit());
//		}
//
//		
//		for(ArrayList<VRPVisit> run :solution){
//			int c=0;
//			if (this.myVRP.isConcurrent())
//				time = myVRP.getStartTime();
//
//			KMLWriter kml = new KMLWriter();
//			
//		
//			kml.addPlacemark(myVRP.getStart().getX(), myVRP.getStart().getY(), "Start", "", "cafe");
//			
//			Visit prev = myVRP.getStart();//new Visit("",myVRP.getStart().getX(),myVRP.getStart().getY());
//		
//			for (VRPVisit v : run){
//				c++;
//				String description = c +" ";
//				long winStart=0;
//				//long winEnd=Long.MAX_VALUE;
//				if (v instanceof FoodelVisit) {
//					winStart = ((FoodelVisit)v).getWinstart();
//					//winEnd = ((FoodVisit)v).getWinend();
//					if (((FoodelVisit)v).getAddress()!= null) {
//						description += ((FoodelVisit)v).getAddress().replace("&", " and ");
//						description += " ";
//					}
//					if (((FoodelVisit)v).getOrder()!= null) 
//						description += ((FoodelVisit)v).getOrder().replace("&", " and ");
//				}else 
//					description += v.getName();
//
//
//				kml.addPlacemark(v.getX(), v.getY(),description + " " + myVRP.getTimeOnlyformatter().format(time), "", "delivery");
//		
//				Visit curr = v;//new Visit("",v.getX(),v.getY());
//
//				Journey j = GHopperInterface.getJourney(prev, curr, myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//				//window
//				if (time < winStart ) {
//					time = winStart;
//				}
//				//done
//				if (v instanceof FoodelVisit){
//					FoodelVisit f = (FoodelVisit)v;
//				}
//				else {
//				}
//				time  = time + deliveryTime;
//
//
//				ArrayList<Visit> p = j.getPath();
//				ArrayList<Double> lat = new ArrayList<Double>();
//				ArrayList<Double> lon = new ArrayList<Double>();
//
//				for (Visit l : p){
//					lat.add(l.getX());
//					lon.add(l.getY());	
//				}
//				kml.addRoute(lat, lon, "", "", "red");
//				prev = curr;
//			}
//			//time = time + OSMAccessHelper.getJourney(prev, myVRP.getEnd(), myVRP.getMode()).getTravelTimeMS();
//
//			/*
//			 * Add journey to end...
//			 */
//
//			if (myVRP.getEnd() != null) {
//				Journey j = GHopperInterface.getJourney(prev, myVRP.getEnd() /*new Visit( "",myVRP.getEnd().getX(),myVRP.getEnd().getY())*/, myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//				time  = time + deliveryTime;
//
//
//				ArrayList<Visit> p = j.getPath();
//				ArrayList<Double> lat = new ArrayList<Double>();
//				ArrayList<Double> lon = new ArrayList<Double>();
//
//				for (Visit l : p){
//					lat.add(l.getX());
//					lon.add(l.getY());	
//				}
//				kml.addRoute(lat, lon, "", "", "red");
//		
//				kml.addPlacemark(myVRP.getEnd().getX(), myVRP.getEnd().getY(), "End", "", "cafe");
//				}
//			//Done end
//			kml.writeFile(outFolder+"/"+r);
//			r++;
//		}
//	
//	}

	public String getResultHTML(String key) {
		String html ="";
		
		int r=0;
		long deliveryTime = myVRP.getDeliveryTimeMS();
		long time = myVRP.getStartTime();

		ArrayList<ArrayList<VRPVisit>> solution = myVRP.getCVRPSolution();
		if (myVRP.getInitialVisit()!= null) {
			ArrayList<VRPVisit> run =solution.get(0);
			run.add(0,myVRP.getInitialVisit());
		}

		
		for(ArrayList<VRPVisit> run :solution){
			r++;
			int c=0;
			if (this.myVRP.isConcurrent())
				time = myVRP.getStartTime();

			html = html += "<h2> Delivery run "+r+"</h2>";
			html = html += "<a href=\"map?id="+myVRP.getReference() +"&key="+key+"&run="+r+"\">View Map</a>  <a href=\"gpx?key="+key+"&run="+r+"\">GPX File</a>  <br> \n";
			VRPVisit prev = myVRP.getStart();//new Visit("",myVRP.getStart().getX(),myVRP.getStart().getY());
			
			for (VRPVisit v : run){
				c++;
				String description = c +" ";
				long winStart=0;
				//long winEnd=Long.MAX_VALUE;
				if (v instanceof FoodelVisit) {
					winStart = ((FoodelVisit)v).getWinstart();

					if (((FoodelVisit)v).getAddress()!= null) {
						description += ((FoodelVisit)v).getAddress().replace("&", " and ");
						description += " ";
					}
					if (((FoodelVisit)v).getOrder()!= null) 
						description += ((FoodelVisit)v).getOrder().replace("&", " and ");
				}else 
					description += v.getName();

				html = html +  description + " " + myVRP.getTimeOnlyformatter().format(time) +"<br>" ;

				VRPVisit curr = v;

				Journey j = GHopperInterface.getJourney(prev, curr, myVRP.getMode());
				time = time + ( j.getTravelTimeMS()) ;
				//window
				if (time < winStart ) {
					time = winStart;
				}
				//done
				time  = time + deliveryTime;
				}
				
			}
			//time = time + OSMAccessHelper.getJourney(prev, myVRP.getEnd(), myVRP.getMode()).getTravelTimeMS();

			/*
			 * Add journey to end...
			 */

			
		return html;
		
	}
	
	public String getHTMLMap(int route) {
		//int r=1;
		long deliveryTime = myVRP.getDeliveryTimeMS();
		long time = myVRP.getStartTime();

		ArrayList<ArrayList<VRPVisit>> solution = myVRP.getCVRPSolution();
		if (myVRP.getInitialVisit()!= null) {
			ArrayList<VRPVisit> run =solution.get(0);
			run.add(0,myVRP.getInitialVisit());
		}

		ArrayList<VRPVisit> run  = solution.get(route-1);
			int c=0;
			if (this.myVRP.isConcurrent())
				time = myVRP.getStartTime();
			HTMmapwriter htm = new HTMmapwriter();

			htm.addWayPoint(myVRP.getStart(), 0, myVRP.getTimeOnlyformatter().format(time));
	
			VRPVisit prev = myVRP.getStart();//new Visit("",myVRP.getStart().getX(),myVRP.getStart().getY());
		
			for (VRPVisit v : run){
				c++;
				long winStart=0;
				if (v instanceof FoodelVisit) {
					winStart = ((FoodelVisit)v).getWinstart();
				}


				htm.addWayPoint(v, c, myVRP.getTimeOnlyformatter().format(time));

				VRPVisit curr = v;

				Journey j = GHopperInterface.getJourney(prev, curr, myVRP.getMode());
				time = time + ( j.getTravelTimeMS()) ;
				//window
				if (time < winStart ) {
					time = winStart;
				}
				//done
				time  = time + deliveryTime;


				ArrayList<VRPVisit> p = j.getPath();
				ArrayList<Double> lat = new ArrayList<Double>();
				ArrayList<Double> lon = new ArrayList<Double>();

				for (VRPVisit l : p){
					lat.add(l.getX());
					lon.add(l.getY());	
				}
				htm.addPath(lat, lon);
				prev = curr;
			}
	
			/*
			 * Add journey to end...
			 */

			if (myVRP.getEnd() != null) {
				Journey j = GHopperInterface.getJourney(prev, myVRP.getEnd() /*new Visit( "",myVRP.getEnd().getX(),myVRP.getEnd().getY())*/, myVRP.getMode());
				time = time + ( j.getTravelTimeMS()) ;
		
				time  = time + deliveryTime;


				ArrayList<VRPVisit> p = j.getPath();
				ArrayList<Double> lat = new ArrayList<Double>();
				ArrayList<Double> lon = new ArrayList<Double>();

				for (VRPVisit l : p){
					lat.add(l.getX());
					lon.add(l.getY());	
				}
				htm.addPath(lat, lon);

				htm.addWayPoint(myVRP.getEnd(),c, myVRP.getTimeOnlyformatter().format(time));
			}
			//Done end
	//		r++;

		return htm.body(myVRP.getStart(),myVRP.getReference(),route);
		
		
	}
	
//	public String getHTMLMap(int route) {
//		int r=1;
//		long deliveryTime = myVRP.getDeliveryTimeMS();
//		long time = myVRP.getStartTime();
//
//		ArrayList<ArrayList<VRPVisit>> solution = myVRP.getCVRPSolution();
//		if (myVRP.getInitialVisit()!= null) {
//			ArrayList<VRPVisit> run =solution.get(0);
//			run.add(0,myVRP.getInitialVisit());
//		}
//
//		//HTMLIndexBuilder indexHTM = new HTMLIndexBuilder(myVRP.getReference());
//
//		//for(ArrayList<VRPVisit> run :solution){
//		ArrayList<VRPVisit> run  = solution.get(route-1);
//			int c=0;
//			if (this.myVRP.isConcurrent())
//				time = myVRP.getStartTime();
//
//			//indexHTM.newRun(r);
//			HTMWriter htm = new HTMWriter();
//
//			htm.addWayPoint(myVRP.getStart(), 0, myVRP.getTimeOnlyformatter().format(time));
//			//indexHTM.addDel(0, "", "", "", "", myVRP.getTimeOnlyformatter().format(time));
//
//			VRPVisit prev = myVRP.getStart();//new Visit("",myVRP.getStart().getX(),myVRP.getStart().getY());
//		
//			for (VRPVisit v : run){
//				c++;
//				String description = c +" ";
//				long winStart=0;
//				//long winEnd=Long.MAX_VALUE;
//				if (v instanceof FoodelVisit) {
//					winStart = ((FoodelVisit)v).getWinstart();
//					//winEnd = ((FoodVisit)v).getWinend();
//					if (((FoodelVisit)v).getAddress()!= null) {
//						description += ((FoodelVisit)v).getAddress().replace("&", " and ");
//						description += " ";
//					}
//					if (((FoodelVisit)v).getOrder()!= null) 
//						description += ((FoodelVisit)v).getOrder().replace("&", " and ");
//				}else 
//					description += v.getName();
//
//
//				htm.addWayPoint(v, c, myVRP.getTimeOnlyformatter().format(time));
//
//				VRPVisit curr = v;//new Visit("",v.getX(),v.getY());
//
//				Journey j = GHopperInterface.getJourney(prev, curr, myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//				//window
//				if (time < winStart ) {
//					time = winStart;
//				}
//				//done
////				if (v instanceof FoodelVisit){
////					FoodelVisit f = (FoodelVisit)v;
////					indexHTM.addDel(c, f.getName(), f.getOrder(), f.getAddress(), f.getPostCode(), myVRP.getTimeOnlyformatter().format(time));
////				}
////				else {
////					indexHTM.addDel(c, v.getName(), "", "", "", myVRP.getTimeOnlyformatter().format(time));
////				}
//				time  = time + deliveryTime;
//
//
//				ArrayList<VRPVisit> p = j.getPath();
//				ArrayList<Double> lat = new ArrayList<Double>();
//				ArrayList<Double> lon = new ArrayList<Double>();
//
//				for (VRPVisit l : p){
//					lat.add(l.getX());
//					lon.add(l.getY());	
//				}
//				htm.addPath(lat, lon);
//				prev = curr;
//			}
//			//time = time + OSMAccessHelper.getJourney(prev, myVRP.getEnd(), myVRP.getMode()).getTravelTimeMS();
//
//			/*
//			 * Add journey to end...
//			 */
//
//			if (myVRP.getEnd() != null) {
//				Journey j = GHopperInterface.getJourney(prev, myVRP.getEnd() /*new Visit( "",myVRP.getEnd().getX(),myVRP.getEnd().getY())*/, myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//			//			indexHTM.addDel(c+1, "", "", "", "",  myVRP.getTimeOnlyformatter().format(time));
//
//				time  = time + deliveryTime;
//
//
//				ArrayList<VRPVisit> p = j.getPath();
//				ArrayList<Double> lat = new ArrayList<Double>();
//				ArrayList<Double> lon = new ArrayList<Double>();
//
//				for (VRPVisit l : p){
//					lat.add(l.getX());
//					lon.add(l.getY());	
//				}
//				htm.addPath(lat, lon);
//
//				htm.addWayPoint(myVRP.getEnd(),c, myVRP.getTimeOnlyformatter().format(time));
//			}
//			//Done end
//			//String pageName = id +"-"+r+".htm";
//			//htm.write(webRoot, pageName,myVRP.getStart(),myVRP.getReference(),r);
//			r++;
//		
//		//}
////		String fName = id;//myVRP.getReference();
////		indexHTM.write(webRoot, fName);
////		String pageName = fName +".htm";
////		try {
////			if (openBrowser) {
////				WebServer.openPage(pageName);
////				updateIndexHtm(webRoot,myVRP.getReference(),pageName);
////			}
////		}catch(Exception e) {}
//		return htm.html(myVRP.getStart(),myVRP.getReference(),route);
//		
//		
//	}

	
//	public void writeHTML(String webRoot, String id, String outFolder, boolean openBrowser) throws Exception {
//
//		int r=1;
//		long deliveryTime = myVRP.getDeliveryTimeMS();
//		long time = myVRP.getStartTime();
//
//		ArrayList<ArrayList<VRPVisit>> solution = myVRP.getCVRPSolution();
//		if (myVRP.getInitialVisit()!= null) {
//			ArrayList<VRPVisit> run =solution.get(0);
//			run.add(0,myVRP.getInitialVisit());
//		}
//
//		HTMLIndexBuilder indexHTM = new HTMLIndexBuilder(myVRP.getReference());
//
//		for(ArrayList<VRPVisit> run :solution){
//			int c=0;
//			if (this.myVRP.isConcurrent())
//				time = myVRP.getStartTime();
//
//			indexHTM.newRun(r);
//			HTMWriter htm = new HTMWriter();
//
//					htm.addWayPoint(myVRP.getStart(), 0, myVRP.getTimeOnlyformatter().format(time));
//			indexHTM.addDel(0, "", "", "", "", myVRP.getTimeOnlyformatter().format(time));
//
//			Visit prev = myVRP.getStart();//new Visit("",myVRP.getStart().getX(),myVRP.getStart().getY());
//		
//			for (VRPVisit v : run){
//				c++;
//				String description = c +" ";
//				long winStart=0;
//				//long winEnd=Long.MAX_VALUE;
//				if (v instanceof FoodelVisit) {
//					winStart = ((FoodelVisit)v).getWinstart();
//					//winEnd = ((FoodVisit)v).getWinend();
//					if (((FoodelVisit)v).getAddress()!= null) {
//						description += ((FoodelVisit)v).getAddress().replace("&", " and ");
//						description += " ";
//					}
//					if (((FoodelVisit)v).getOrder()!= null) 
//						description += ((FoodelVisit)v).getOrder().replace("&", " and ");
//				}else 
//					description += v.getName();
//
//
//				htm.addWayPoint(v, c, myVRP.getTimeOnlyformatter().format(time));
//
//				Visit curr = v;//new Visit("",v.getX(),v.getY());
//
//				Journey j = GHopperInterface.getJourney(prev, curr, myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//				//window
//				if (time < winStart ) {
//					time = winStart;
//				}
//				//done
//				if (v instanceof FoodelVisit){
//					FoodelVisit f = (FoodelVisit)v;
//					indexHTM.addDel(c, f.getName(), f.getOrder(), f.getAddress(), f.getPostCode(), myVRP.getTimeOnlyformatter().format(time));
//				}
//				else {
//					indexHTM.addDel(c, v.getName(), "", "", "", myVRP.getTimeOnlyformatter().format(time));
//				}
//				time  = time + deliveryTime;
//
//
//				ArrayList<Visit> p = j.getPath();
//				ArrayList<Double> lat = new ArrayList<Double>();
//				ArrayList<Double> lon = new ArrayList<Double>();
//
//				for (Visit l : p){
//					lat.add(l.getX());
//					lon.add(l.getY());	
//				}
//				htm.addPath(lat, lon);
//				prev = curr;
//			}
//			//time = time + OSMAccessHelper.getJourney(prev, myVRP.getEnd(), myVRP.getMode()).getTravelTimeMS();
//
//			/*
//			 * Add journey to end...
//			 */
//
//			if (myVRP.getEnd() != null) {
//				Journey j = GHopperInterface.getJourney(prev, myVRP.getEnd() /*new Visit( "",myVRP.getEnd().getX(),myVRP.getEnd().getY())*/, myVRP.getMode());
//				time = time + ( j.getTravelTimeMS()) ;
//						indexHTM.addDel(c+1, "", "", "", "",  myVRP.getTimeOnlyformatter().format(time));
//
//				time  = time + deliveryTime;
//
//
//				ArrayList<Visit> p = j.getPath();
//				ArrayList<Double> lat = new ArrayList<Double>();
//				ArrayList<Double> lon = new ArrayList<Double>();
//
//				for (Visit l : p){
//					lat.add(l.getX());
//					lon.add(l.getY());	
//				}
//				htm.addPath(lat, lon);
//
//				htm.addWayPoint(myVRP.getEnd(),c, myVRP.getTimeOnlyformatter().format(time));
//			}
//			//Done end
//			String pageName = id +"-"+r+".htm";
//			htm.write(webRoot, pageName,myVRP.getStart(),myVRP.getReference(),r);
//			r++;
//		
//		}
//		String fName = id;//myVRP.getReference();
//		indexHTM.write(webRoot, fName);
//		String pageName = fName +".htm";
//		try {
//			if (openBrowser) {
//				WebServer.openPage(pageName);
//				updateIndexHtm(webRoot,myVRP.getReference(),pageName);
//			}
//		}catch(Exception e) {}
//		
//		
//		
//	}

//	private void updateIndexHtm(String root, String ref, String file) {
//
//		try {
//			String buffer = "";
//			File f = new File(root+"/index.htm");
//
//			BufferedReader b = new BufferedReader(new FileReader(f));
//
//			String readLine = "";
//
//
//			while ((readLine = b.readLine()) != null) {
//				buffer += readLine;
//
//				if (readLine.contains("Add data here")) {
//					buffer += "<tr>\n" + 
//
//	                		"<td> <a href=\" "+file+" \">"  +ref+"</a> </td>\n" + 
//	                		"</tr>\n";
//				}
//			}
//			b.close();
//
//			//Write new file
//			Files.write(Paths.get(WebServer.getRoot()+"/index.htm"), buffer.getBytes());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
