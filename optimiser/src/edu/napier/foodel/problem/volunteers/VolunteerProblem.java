package edu.napier.foodel.problem.volunteers;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.napier.foodel.geo.GHopperInterface;
import edu.napier.foodel.geo.Geocoder;
import edu.napier.foodel.geo.Journey;
import edu.napier.foodel.ioutils.GPXWriter;
import edu.napier.foodel.ioutils.HTMmapwriter;
import edu.napier.foodel.problem.cvrp.CVRPIndividual;
import edu.napier.foodel.problem.cvrp.CVRPProblem;
import edu.napier.foodel.problemTemplate.FoodelVisit;

public class VolunteerProblem extends CVRPProblem {
	private ArrayList<Volunteer> volunteers = new ArrayList<Volunteer>();

	public void addVolunteer(String name, String address) {
		Point2D loc = Geocoder.find(address);
		volunteers.add(new Volunteer(name, address,"",loc.getX(), loc.getY(),0 ));
	}

	public class Volunteer extends FoodelVisit{
		public Volunteer(String name, String address, String order, double lat, double lon, int demand) {
			super(name, address, order, lat, lon, demand);
		}
	}

	@Override 
	public double getSolutionDistance(ArrayList sol){
		//Get the total distance covered by the solution in <sol>
		ArrayList<ArrayList<FoodelVisit>> solution = (ArrayList<ArrayList<FoodelVisit>>) sol;
		double dist =0;
		int volunteerCount =0;
		Volunteer v = null;
		for (ArrayList<FoodelVisit> route: solution){
			v= null;
			if (volunteerCount < this.volunteers.size())
				v = volunteers.get(volunteerCount);
			double d = getRouteDistance(route,v);
			dist = dist + d;
			volunteerCount ++;
		}
		return dist;
	}


	public double getRouteDistance(ArrayList<FoodelVisit> possibleRoute, Volunteer volunteer){
		//If volunteer == null assume the route is operated start->start

		//Get the distance of the visits within possibleRoute - include the start point as well to complete the circuit
		/* this has to be a weighted distance)*/
		double dist =0;
		if (volunteer !=null) {
			dist = dist + getDistance(volunteer, getStart());
		}
		FoodelVisit previousCity = getStart();
		for (FoodelVisit c : possibleRoute){//go through each city
			FoodelVisit city = (FoodelVisit)c;
			double bDist = getDistance(previousCity, city);
			dist = dist + bDist;
			previousCity = city;
		}
		if (volunteer != null) {
			dist = dist + getDistance(previousCity, volunteer);
		}
		else
			dist = dist + getDistance(previousCity, this.getEnd());
		return dist;
	}

	@Override 
	public double getWeightedDistance(CVRPIndividual i){

		//		//Get the total distance covered by the solution in <sol>
		//		ArrayList solution =  i.getPhenotype();//sol;
		//		
		//		double dist =0;
		//		//int visitCount = super.getSize();
		//		for (Object o: solution){
		//			ArrayList<FoodelVisit> route = (ArrayList<FoodelVisit> ) o;
		//			double d = getWeightedRouteDistance(route);
		//			dist = dist + d;
		//		}
		//		
		//		dist = dist * solution.size();
		//		return dist;

		//Get the total distance covered by the solution in <sol>
		ArrayList<ArrayList<FoodelVisit>> solution = i.getPhenotype();//(ArrayList<ArrayList<FoodelVisit>>) sol;
		double dist =0;
		int volunteerCount =0;

		Volunteer v = null;
		for (ArrayList<FoodelVisit> route: solution){
			v= null;
			if (volunteerCount < this.volunteers.size())
				v = volunteers.get(volunteerCount);
			double d = getWeightedRouteDistance(route,v);
			dist = dist + d;
			volunteerCount ++;
		}
		return dist;
	}

	public double getWeightedRouteDistance(ArrayList<FoodelVisit> possibleRoute,Volunteer volunteer){
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
		if (volunteer != null) {
			dist = dist + getDistance(volunteer, this.getStart());
			dist = dist +Math.pow( getDistance(previousCity,volunteer),2);
		}else
			dist = dist + getDistance(previousCity, this.end);
		return dist;
	}

	@Override
	public String getGPX(int r) {

		long deliveryTime = getDeliveryTimeMS();
		long time = getStartTime();

		ArrayList<ArrayList<FoodelVisit>> solution = getCVRPSolution();
		//		if (getInitialVisit()!= null) {
		//			ArrayList<FoodelVisit> run =solution.get(0);
		//			run.add(0,getInitialVisit());
		//		}

		ArrayList<FoodelVisit> run  = solution.get(r-1);
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

	@Override
	public String getHTMLMap(int route) {
		Volunteer volunteer = null;
		if (route < volunteers.size())
			volunteer = volunteers.get(route-1);
		
		long deliveryTime = getDeliveryTimeMS();
		long time = getStartTime();

		ArrayList<ArrayList<FoodelVisit>> solution = getCVRPSolution();

		ArrayList<FoodelVisit> run  = solution.get(route-1);
		int c=0;
		
		time = getStartTime();
		HTMmapwriter htm = new HTMmapwriter();

		if (volunteer != null) {
			htm.addWayPoint(volunteer, 0, "");
			Journey j = GHopperInterface.getJourney(volunteer, this.getStart(), getMode());
			ArrayList<Point2D.Double> p = j.getPath();
			ArrayList<Double> lat = new ArrayList<Double>();
			ArrayList<Double> lon = new ArrayList<Double>();

			for (Point2D l : p){
				lat.add(l.getX());
				lon.add(l.getY());	
			}
			htm.addPath(lat, lon);
		}
		
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

		FoodelVisit end = this.getStart();
		if (volunteer != null) {
			end = volunteer;
		}
			Journey j = GHopperInterface.getJourney(prev, end, getMode());
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
			htm.addWayPoint(end,c, "");
		
		//Done end
		String title = getReference();
		if (volunteer != null)
			title = title + " - Volunteer : "+ volunteer.getName();
		else
			title =title +" - Not allocated to a volunteer";
		return htm.body(end,getReference(),route);
	}

	@Override
	public String getResultHTML(String key) {
		String html ="";

		int r=0;
		long deliveryTime = getDeliveryTimeMS();
		long time = getStartTime();

		ArrayList<ArrayList<FoodelVisit>> solution = getCVRPSolution();

		for(ArrayList<FoodelVisit> run :solution){
			Volunteer volunteer = null;
			if (r < volunteers.size())
				volunteer = volunteers.get(r);

			r++;
			int c=0;
			time = getStartTime();

			html = html += "<h2> Delivery run "+r+"</h2>";
			if (volunteer != null)
				html = html += "<h2> Volunteer : "+volunteer.getName()+"</h2>";
			else
				html = html += "<h2> No volunteer allocated </h2>";

			html = html += "<a href=\"map?id="+getReference() +"&key="+key+"&run="+r+"\">View Map</a>  <a href=\"gpx?key="+key+"&run="+r+"\">GPX File</a>  <br> \n";

			FoodelVisit prev = getStart();

			for (FoodelVisit v : run){
				c++;
				String description;// = c +" ";
				if (v instanceof FoodelVisit) {
					description= c +" ";
					if (((FoodelVisit)v).getAddress()!= null) {
						
						description += ((FoodelVisit)v).getAddress().replace("&", " and ");
						description += " ";
					}
					if (((FoodelVisit)v).getOrder()!= null) 
						description += ((FoodelVisit)v).getOrder().replace("&", " and ");
				}
				else 
					description = c + " "+ v.getName();

				html = html +  description + " " + getTimeOnlyformatter().format(time) +"<br>" ;

				FoodelVisit curr = v;

				Journey j = GHopperInterface.getJourney(prev, curr, getMode());
				time = time + ( j.getTravelTimeMS()) ;

				//done
				time  = time + deliveryTime;
			}

		}

		return html;
	}
}
