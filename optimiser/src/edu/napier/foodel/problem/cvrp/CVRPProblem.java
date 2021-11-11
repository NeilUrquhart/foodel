package edu.napier.foodel.problem.cvrp;

import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.napier.foodel.geo.GHopperInterface;
import edu.napier.foodel.geo.Haversine;
import edu.napier.foodel.geo.Journey;
import edu.napier.foodel.ioutils.GPXWriter;
import edu.napier.foodel.ioutils.HTMmapwriter;
import edu.napier.foodel.problem.fixedvans.FixedVansProblem;
import edu.napier.foodel.problemTemplate.FoodelVisit;
import edu.napier.foodel.problemTemplate.FoodelProblem;

/*
 * Neil Urquhart 2019
 * This class represents a FoodCVRP problem.
 * It extends the basic CVRPProblem class.
 * 
 */
public class CVRPProblem extends FoodelProblem {

	private String mode;

	//private FoodelVisit initialVisit;

	private int vehicles=2;
	private transient SimpleDateFormat timeOnlyformatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");


	private boolean concurrentRoutes = true;

	public void setConcurrentRoutes(boolean val) {
		concurrentRoutes = val;
	}

	public boolean isConcurrent() {
		return concurrentRoutes;
	}

	@Override
	public String isValid() {
		String result  = super.isValid();
		if ((getTimeLimitMS() == 0 )&& !(this instanceof FixedVansProblem))
			result += "No round time limit entered <br>";
		return result;
	}


	public void setMode(String aMode) {
		if (aMode.contains("Car"))
			mode = "car";

		if (aMode.contains("cycle"))
			mode = "bike";

		if (aMode.contains("Walk"))
			mode = "foot";
	}

	public String getMode() {
		return mode;
	}



	public SimpleDateFormat getTimeOnlyformatter() {
		return timeOnlyformatter;
	}




	//	
	//	public double getDistance(){
	//		//Return the total distance travelled by all of the vehicles
	//		return getDistance(currentVRPSolution);
	//	}
	//	



	//	public ArrayList<String>  getPostalAreas(){
	//		ArrayList<String> result = new ArrayList<String>();
	//		
	//		for (Object ov : this.getVisits()) {
	//			if (ov instanceof FoodelVisit) {
	//				FoodelVisit v = (FoodelVisit) ov;
	//				String pCodeArea = v.getPostCode().split(" ")[0];
	//				if (!result.contains(pCodeArea)) {
	//					result.add(pCodeArea);
	//				}
	//			}
	//		}
	//			
	//		
	//		return result;
	//	}
	//	
	//	public void filterPCodeArea(String[] pCodes) {
	//			int counter =0;
	//		while (counter < this.getVisits().size()) {
	//		   Object ov = this.getVisits().get(counter);
	//			if (ov instanceof FoodelVisit) {
	//				FoodelVisit v = (FoodelVisit) ov;
	//				boolean found = false;
	//				for(String pCode: pCodes)
	//				  if (v.getPostCode().contains(pCode+' ')) {
	//					  found = true;
	//					  break;
	//				  }
	//					  
	//				 if (!found)
	//					this.getVisits().remove(v);
	//				else
	//					counter ++;
	//			}else {
	//				counter++;
	//			}
	//		}
	//	}
	//	






	//	public ArrayList<String>  getDeliveryNotes(){
	//		ArrayList<String> result = new ArrayList<String>();
	//		
	//		for (Object ov : this.getVisits()) {
	//			if (ov instanceof FoodelVisit) {
	//				FoodelVisit v = (FoodelVisit) ov;
	//				String comment = v.getdeliveryNote();
	//				if (!result.contains(comment)) {
	//					result.add(comment);
	//				}
	//			}
	//		}	
	//		return result;
	//	}

	//	public void filterDeliveryNotes(String[] comments) {
	//		int counter =0;
	//		while (counter < this.getVisits().size()) {
	//		   Object ov = this.getVisits().get(counter);
	//			if (ov instanceof FoodelVisit) {
	//				FoodelVisit v = (FoodelVisit) ov;
	//				boolean found = false;
	//				for(String comment: comments)
	//				  if (v.getdeliveryNote().equals(comment)) {
	//					  found = true;
	//					  break;
	//				  }
	//					  
	//				 if (!found)
	//					this.getVisits().remove(v);
	//				else
	//					counter ++;
	//			}else {
	//				counter++;
	//			}
	//		}
	//	}
	//	






	public double getSolutionDistance(ArrayList sol){
		//Get the total distance covered by the solution in <sol>
		ArrayList<ArrayList<FoodelVisit>> solution = (ArrayList<ArrayList<FoodelVisit>>) sol;
		double dist =0;
		//int visitCount = super.getSize();
		for (ArrayList<FoodelVisit> route: solution){
			double d = getRouteDistance(route);

			dist = dist + d;

		}

		return dist;
	}



	public double getRouteDistance(ArrayList<FoodelVisit> possibleRoute){
		//Get the distance of the visits within possibleRoute - include the start point as well to complete the circuit
		/* this has to be a weighted distance)*/
		double dist =0;
		FoodelVisit previousCity = getStart();
		for (FoodelVisit c : possibleRoute){//go through each city
			FoodelVisit city = (FoodelVisit)c;
			double bDist = getDistance(previousCity, city);
			dist = dist + bDist;
			previousCity = city;
		}
		if (end != null)
			dist = dist + getDistance(previousCity, this.end);
		return dist;
	}

	public double getWeightedDistance(CVRPIndividual i){

		//Get the total distance covered by the solution in <sol>
		//ArrayList<ArrayList<FoodVisit>> solution = (ArrayList<ArrayList<FoodVisit>>) i.getPhenotype();//sol;
		ArrayList solution =  i.getPhenotype();//sol;

		double dist =0;
		//int visitCount = super.getSize();
		for (Object o: solution){
			ArrayList<FoodelVisit> route = (ArrayList<FoodelVisit> ) o;
			double d = getWeightedRouteDistance(route);
			dist = dist + d;
		}

		dist = dist * solution.size();
		return dist;
	}


	public double getWeightedRouteDistance(ArrayList<FoodelVisit> possibleRoute/*,int visitcount*/){
		//Get the distance of the visits within possibleRoute - include the start point as well to complete the circuit
		/* this has to be a weighted distance)*/
		int visitCount = possibleRoute.size();
		double dist =0;
		FoodelVisit previousCity = getStart();
		for (FoodelVisit c : possibleRoute){//go through each city
			FoodelVisit city = (FoodelVisit)c;
			double bDist = getDistance(previousCity, city);
			double wDist = (bDist /* *bDist*/)*(visitCount);//Add weighting
			dist = dist + wDist;
			visitCount --;
			previousCity = city;
		}
		if (this.end != null)
			dist = dist + getDistance(previousCity, this.end);//super.getStart());
		return dist;
	}
	public double getDistance(FoodelVisit x, FoodelVisit y){
		//Get the distance between two visits
		if ((x == null)||(y==null))
			return 0;
		else			{
			//	Location lx = new Location(x.getX(),x.getY());
			//	Location ly = new Location(y.getX(),y.getY());

			return GHopperInterface.getJourney(x, y, mode).getDistanceKM();
		}
	}

	//	public void addVisit(FoodelVisit newVis) {
	//		//Check for duplicate postcodes
	//		//String pc = newVis.getAddress();
	//		
	////		for (Object ov: super.getVisits()) {
	////			try {
	////				FoodelVisit v = (FoodelVisit)ov;
	////				if (v.getPostCode().equals(newVis.getPostCode())) {
	////					//Combine visits
	////					v.setOrder(v.getName() +" : " +v.getOrder() +" AND "+ newVis.getName() + " : "+newVis.getOrder());
	////					v.setName(v.getName() +"+"+newVis.getName());
	////					v.setDemand(v.getDemand() + newVis.getDemand());
	////					return;
	////				}
	////			}catch(Exception e) {}//Ignore non FoodVisits
	////		}
	//		super.addVisit(newVis);
	//		
	//	}


	public String toString() {
		String buffer = "";

		for (Object visit : this.getVisits()) {
			buffer = buffer + visit.toString() +"\n";
		}

		return buffer;
	}

	public void setVehicleQty(int v) {
		this.vehicles = v;

	}

	public int getVehicleQty() {
		return this.vehicles;

	}

	public String getGPX(int r) {

		long deliveryTime = getDeliveryTimeMS();
		long time = getStartTime();

		ArrayList<ArrayList<FoodelVisit>> solution = getCVRPSolution();

		ArrayList<FoodelVisit> run  = solution.get(r);
		int c=0;
		if (isConcurrent())
			time =getStartTime();

		GPXWriter gpx = new GPXWriter();
		gpx.addWayPoint(getStart().getX(), getStart().getY(), "Start");

		FoodelVisit prev = getStart();
		for (FoodelVisit v : run){
			c++;
			String description = c +" ";
			if (v instanceof FoodelVisit) {
				if (((FoodelVisit)v).getAddress()!= null) {
					description += ((FoodelVisit)v).getAddress().replace("&", " and ");
					description += " ";
				}
				if (((FoodelVisit)v).getOrder()!= null) 
					description += ((FoodelVisit)v).getOrder().replace("&", " and ");
			}else 
				description += v.getName();


			gpx.addWayPoint(v.getX(), v.getY(), description);

			FoodelVisit curr = v;
			Journey j = GHopperInterface.getJourney(prev, curr, getMode());
			time = time + ( j.getTravelTimeMS()) ;
			//done

			time  = time + deliveryTime;


			ArrayList<Point2D.Double> p = j.getPath();
			ArrayList<Double> lat = new ArrayList<Double>();
			ArrayList<Double> lon = new ArrayList<Double>();

			for (Point2D l : p){
				lat.add(l.getX());
				lon.add(l.getY());	
			}
			gpx.addPath(lat, lon);
			prev = curr;
		}
		/*
		 * Add journey to end...
		 */

		if (getEnd() != null) {
			Journey j = GHopperInterface.getJourney(prev, getEnd(), getMode());
			time = time + ( j.getTravelTimeMS()) ;


			time  = time + deliveryTime;


			ArrayList<Point2D.Double> p = j.getPath();
			ArrayList<Double> lat = new ArrayList<Double>();
			ArrayList<Double> lon = new ArrayList<Double>();

			for (Point2D l : p){
				lat.add(l.getX());
				lon.add(l.getY());	
			}
			gpx.addPath(lat, lon);	
			gpx.addWayPoint(getEnd().getX(), getEnd().getY(), "End");
		}
		//Done end
		return gpx.getText(getReference() + " "+r);

	}

	public String getHTMLMap(int route) {
		long deliveryTime = getDeliveryTimeMS();
		long time = getStartTime();

		ArrayList<ArrayList<FoodelVisit>> solution = getCVRPSolution();

		ArrayList<FoodelVisit> run  = solution.get(route);
		int c=0;
		if (isConcurrent())
			time = getStartTime();
		HTMmapwriter htm = new HTMmapwriter();

		htm.addWayPoint(getStart(), 0, getTimeOnlyformatter().format(time));

		FoodelVisit prev = getStart();

		for (FoodelVisit v : run){
			c++;
			htm.addWayPoint(v, c, getTimeOnlyformatter().format(time));
			FoodelVisit curr = v;
			Journey j = GHopperInterface.getJourney(prev, curr, getMode());
			time = time + ( j.getTravelTimeMS()) ;

			//done
			time  = time + deliveryTime;
			ArrayList<Point2D.Double> p = j.getPath();
			ArrayList<Double> lat = new ArrayList<Double>();
			ArrayList<Double> lon = new ArrayList<Double>();

			for (Point2D l : p){
				lat.add(l.getX());
				lon.add(l.getY());	
			}
			htm.addPath(lat, lon);
			prev = curr;
		}

		/*
		 * Add journey to end...
		 */

		if (getEnd() != null) {
			Journey j = GHopperInterface.getJourney(prev, getEnd() /*new Visit( "",myVRP.getEnd().getX(),myVRP.getEnd().getY())*/, getMode());
			time = time + ( j.getTravelTimeMS()) ;
			time  = time + deliveryTime;
			ArrayList<Point2D.Double> p = j.getPath();
			ArrayList<Double> lat = new ArrayList<Double>();
			ArrayList<Double> lon = new ArrayList<Double>();
			for (Point2D l : p){
				lat.add(l.getX());
				lon.add(l.getY());	
			}
			htm.addPath(lat, lon);
			htm.addWayPoint(getEnd(),c, getTimeOnlyformatter().format(time));
		}
		//Done end
		return htm.body(getStart(),getReference(),route);
	}

	public String getResultHTML(String key) {
		String html ="<div class=\"container\">";

		int r=0;
		long deliveryTime = getDeliveryTimeMS();
		long time = getStartTime();

		ArrayList<ArrayList<FoodelVisit>> solution = getCVRPSolution();

		for(ArrayList<FoodelVisit> run :solution){

			int c=0;
			if (this.isConcurrent())
				time = getStartTime();
			html = html +" <section> "+
					"<div class=\"card\"> "+
					"<div class=\"card-body\">"+
					"<div class=\"card-title\">"+
					"Route " +r+" "+
					"</div>";

			html = html + "<a href=\"map?id="+getReference() +"&key="+key+"&run="+r+"\" class =\"button\"  >View Map</a>  "
					+ "<a href=\"gpx?id="+this.getReference()+"&key="+key+"&run="+r+"\" class =\"button\" >GPX File</a> "
					+ "<a href=\"csv?id="+this.getReference()+"&key="+key+"&run="+r+"\" class =\"button\" >CSV File</a> "
					+ "</h2>\n";
			
			FoodelVisit prev = getStart();
			html = html + "<ol>";
			for (FoodelVisit v : run){
				c++;
				String description = "<li>";
				if (v instanceof FoodelVisit) {

					if (((FoodelVisit)v).getAddress()!= null) {
						description += ((FoodelVisit)v).getAddress().replace("&", " and ");
						description += " ";
					}
					if (((FoodelVisit)v).getOrder()!= null) 
						description += ((FoodelVisit)v).getOrder().replace("&", " and ");
				}else 
					description += v.getName();

				html = html +  description + " " + getTimeOnlyformatter().format(time) +"</li>" ;

				FoodelVisit curr = v;

				Journey j = GHopperInterface.getJourney(prev, curr, getMode());
				time = time + ( j.getTravelTimeMS()) ;

				//done
				time  = time + deliveryTime;
			}
			html = html + "</ol>";
			html = html+ "</div></div></section>";
			r++;
		}
		html = html +"</div>";
		return html;
	}

	@Override
	public String getCSV(int r) {
		StringBuilder csv = new StringBuilder();

		long deliveryTime = getDeliveryTimeMS();
		long time = getStartTime();

		ArrayList<ArrayList<FoodelVisit>> solution = getCVRPSolution();

		ArrayList<FoodelVisit> run = solution.get(r);
		r++;
		int c=0;
		if (this.isConcurrent())
			time = getStartTime();

		csv.append(",Time,Name,Address,Postcode,Instructions\n");
		csv.append("," + getTimeOnlyformatter().format(time) + ",Start"+ this.getStart().getAddress()+"\n");
		FoodelVisit prev = getStart();


		Journey j = GHopperInterface.getJourney(prev, run.get(0), getMode());
		time = time + ( j.getTravelTimeMS()) ;

		for (FoodelVisit v : run){
			c++;
			String description = c +" ";
			//if (v instanceof FoodelVisit) {

			//					if (((FoodelVisit)v).getAddress()!= null) {
			//						description += ((FoodelVisit)v).getAddress().replace("&", " and ");
			//						description += " ";
			//					}
			//					if (((FoodelVisit)v).getOrder()!= null) 
			//						description += ((FoodelVisit)v).getOrder().replace("&", " and ");
			//				}else 
			//					description += v.getName();

			csv.append(c +","  + getTimeOnlyformatter().format(time) +"," + v.getName() + "," + v.getAddress()+","+v.getPostcode() +","+v.getOrder()+"\n") ;

			FoodelVisit curr = v;

			j = GHopperInterface.getJourney(prev, curr, getMode());
			time = time + ( j.getTravelTimeMS()) ;

			//done
			time  = time + deliveryTime;
		}



		return csv.toString();
	}





}
