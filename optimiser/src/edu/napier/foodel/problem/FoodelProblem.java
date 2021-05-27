package edu.napier.foodel.problem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.napier.foodel.algorithm.FoodelIndividual;
import edu.napier.foodel.basic.VRPProblem;
import edu.napier.foodel.basic.VRPVisit;
import edu.napier.ghopper.GHopperInterface;

/*
 * Neil Urquhart 2019
 * This class represents a FoodCVRP problem.
 * It extends the basic CVRPProblem class.
 * 
 */
public class FoodelProblem extends VRPProblem {
	private String startPCode;
    private String endPCode;
    private IndexVisit end;
    private String mode;
    private IndexVisit start;
    private IndexVisit initialVisit;
  	private  int VIP_WEIGHT = 10;
  	private int vehicles=1;
  	private transient SimpleDateFormat timeOnlyformatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public int getVIP_WEIGHT() {
		return VIP_WEIGHT;
	}

	public void setVIP_WEIGHT(int vIP_WEIGHT) {
		VIP_WEIGHT = vIP_WEIGHT;
	}

	private boolean concurrentRoutes = false;
	
	public void setConcurrentRoutes(boolean val) {
		concurrentRoutes = val;
	}
    
	public boolean isConcurrent() {
		return concurrentRoutes;
	}
	
    public void setStart(IndexVisit s) {
    	this.start = s;
    }
    
    public IndexVisit getStart() {
    	return this.start;
    }
    
    public IndexVisit getInitialVisit() {
		return initialVisit;
	}

	public void setInitialVisit(FoodelVisit initialVisit) {
		this.initialVisit = initialVisit;
	}

	public void setMode(String aMode) {
    	if (aMode.contains("Car"))
    		mode = "car";
    	
    	if (aMode.contains("cycle"))
    		mode = "bike";
    	
    	if (aMode.contains("Walk"))
    		mode = "foot";
    	
    	
    	//mode = aMode;
    }
    
    public String getMode() {
    	return mode;
    }

    public IndexVisit getEnd() {
		return end;
	}

	public void setEnd(IndexVisit end) {
		this.end = end;
	}

	public void setEndPCode(String pCode) {
    	endPCode = pCode;
    }
    
    public String getEndPCode() {
    	return endPCode;
    }
    
    
    public void setStartPcode(String pc) {
    	this.startPCode = pc;
    }
    
    public String getStartPcode() {
    	return this.startPCode;
    }
    
	//public SimpleDateFormat getDateOnlyformatter() {
	//	return dateOnlyformatter;
	//}


	public SimpleDateFormat getTimeOnlyformatter() {
		return timeOnlyformatter;
	}

		

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public long getTimeLimitMS() {
		return timeLimitMS;
	}

	public void setTimeLimitMS(long timeLimitMS) {
		this.timeLimitMS = timeLimitMS;
	}

	public long getDeliveryTimeMS() {
		return deliveryTimeMS;
	}

	public void setDeliveryTimeMS(long deliveryTimeMS) {
		this.deliveryTimeMS = deliveryTimeMS;
	}

	

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	private String reference;
	private long timeLimitMS;
	private long deliveryTimeMS;
	private double speedfactor;
	private long startTime;
	private String dateTime;
	
//	
//	public double getDistance(){
//		//Return the total distance travelled by all of the vehicles
//		return getDistance(currentVRPSolution);
//	}
//	
	
	public String isValid() {
		//Check to see if the problem contains enough info to solve. If it does return "" else
		//return an error msg
		String result  = "";
		if (reference == null)
			result += "No problem reference <br>";
		if (timeLimitMS == 0)
			result += "No round time limit entered <br>";
		if (this.dateTime.equals(""))
			result += "No start date entered\n";
		if (this.getSize() < 2)
			result += "You need at least 2 deliveries <br>";
		if (this.getCapacity()==0)
			result += "No delivery capacity\n";
		if (this.getStart() == null)
			result += "You need to enter a start location <br>";
		return result;
	}
	
	public ArrayList<String>  getPostalAreas(){
		ArrayList<String> result = new ArrayList<String>();
		
		for (Object ov : this.getVisits()) {
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				String pCodeArea = v.getPostCode().split(" ")[0];
				if (!result.contains(pCodeArea)) {
					result.add(pCodeArea);
				}
			}
		}
			
		
		return result;
	}
	
	public void filterPCodeArea(String[] pCodes) {
			int counter =0;
		while (counter < this.getVisits().size()) {
		   Object ov = this.getVisits().get(counter);
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				boolean found = false;
				for(String pCode: pCodes)
				  if (v.getPostCode().contains(pCode+' ')) {
					  found = true;
					  break;
				  }
					  
				 if (!found)
					this.getVisits().remove(v);
				else
					counter ++;
			}else {
				counter++;
			}
		}
	}
	
	public ArrayList<String>  getShippingDates(){
		ArrayList<String> result = new ArrayList<String>();
		
		for (Object ov : this.getVisits()) {
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				String shipDate = v.getShippingDate();
				if (!result.contains(shipDate)) {
					result.add(shipDate);
				}
			}
		}
			
		
		return result;
	}
	
	public ArrayList<String>  getNames(){
		ArrayList<String> result = new ArrayList<String>();
		
		for (Object ov : this.getVisits()) {
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				String name = v.getName();
			
					result.add(name);
				
			}
		}
			
		
		return result;
	}
	
	public void filterShipDates(String[] dates) {
		int counter =0;
		while (counter < this.getVisits().size()) {
		   Object ov = this.getVisits().get(counter);
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				boolean found = false;
				for(String date: dates)
				  if (v.getShippingDate().equals(date)) {
					  found = true;
					  break;
				  }
					  
				 if (!found)
					this.getVisits().remove(v);
				else
					counter ++;
			}else {
				counter++;
			}
		}
	}
	
	public ArrayList<String>  getDeliveryNotes(){
		ArrayList<String> result = new ArrayList<String>();
		
		for (Object ov : this.getVisits()) {
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				String comment = v.getdeliveryNote();
				if (!result.contains(comment)) {
					result.add(comment);
				}
			}
		}	
		return result;
	}
	
	public void filterDeliveryNotes(String[] comments) {
		int counter =0;
		while (counter < this.getVisits().size()) {
		   Object ov = this.getVisits().get(counter);
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				boolean found = false;
				for(String comment: comments)
				  if (v.getdeliveryNote().equals(comment)) {
					  found = true;
					  break;
				  }
					  
				 if (!found)
					this.getVisits().remove(v);
				else
					counter ++;
			}else {
				counter++;
			}
		}
	}
	
	public ArrayList<String>  getLocales(){
		ArrayList<String> result = new ArrayList<String>();
		
		for (Object ov : this.getVisits()) {
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				String locale = v.getLocality();
				if (!result.contains(locale)) {
					result.add(locale);
				}
			}
		}	
		return result;
	}
	
	public void filterLocales(String[] locales) {
		int counter =0;
		while (counter < this.getVisits().size()) {
		   Object ov = this.getVisits().get(counter);
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				boolean found = false;
				for(String locale: locales)
				  if (v.getLocality().equals(locale)) {
					  found = true;
					  break;
				  }
					  
				 if (!found)
					this.getVisits().remove(v);
				else
					counter ++;
			}else {
				counter++;
			}
		}
	}
	
	public ArrayList<String>  getAllergies(){
		ArrayList<String> result = new ArrayList<String>();
		
		for (Object ov : this.getVisits()) {
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				String allergy = v.getAllergyDetails();
				if (!result.contains(allergy)) {
					result.add(allergy);
				}
			}
		}	
		return result;
	}
	
	public void filterAllergies(String[] allergies) {
		int counter =0;
		while (counter < this.getVisits().size()) {
		   Object ov = this.getVisits().get(counter);
			if (ov instanceof FoodelVisit) {
				FoodelVisit v = (FoodelVisit) ov;
				boolean found = false;
				for(String allergy: allergies)
				  if (v.getAllergyDetails().equals(allergy)) {
					  found = true;
					  break;
				  }
					  
				 if (!found)
					this.getVisits().remove(v);
				else
					counter ++;
			}else {
				counter++;
			}
		}
	}
	
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public double getSpeedfactor() {
		return speedfactor;
	}

	public void setSpeedfactor(double speedfactor) {
		this.speedfactor = speedfactor;
	}
	
	
	
	public double getDistance(ArrayList sol){
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
		IndexVisit previousCity = getStart();
		for (VRPVisit c : possibleRoute){//go through each city
			IndexVisit city = (IndexVisit)c;
			double bDist = getDistance(previousCity, city);
				dist = dist + bDist;
			previousCity = city;
		}
		if (this.end != null)
  		  dist = dist + getDistance(previousCity, this.end);
		return dist;
	}

	public double getWeightedDistance(FoodelIndividual i){
		
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
  
	public int countVIPs(ArrayList<FoodelVisit> lst) {
		int res=0;
		for(VRPVisit v: lst) {
			FoodelVisit f = (FoodelVisit)v;
			if (f.isVip())
				res++;
		}
		return res;
	}

	public double getWeightedRouteDistance(ArrayList<FoodelVisit> possibleRoute/*,int visitcount*/){
		//Get the distance of the visits within possibleRoute - include the start point as well to complete the circuit
		/* this has to be a weighted distance)*/
		int visitCount = (countVIPs(possibleRoute)*VIP_WEIGHT) + possibleRoute.size();
		double dist =0;
		IndexVisit previousCity = getStart();
		for (VRPVisit c : possibleRoute){//go through each city
			IndexVisit city = (IndexVisit)c;
			double bDist = getDistance(previousCity, city);
			double wDist = (bDist /* *bDist*/)*(visitCount);//Add weighting
			dist = dist + wDist;
			if (((FoodelVisit) city).isVip())
				visitCount = visitCount - VIP_WEIGHT;
			visitCount --;
			previousCity = city;
		}
		if (this.end != null)
  		dist = dist + getDistance(previousCity, this.end);//super.getStart());
		return dist;
	}
	public double getDistance(IndexVisit x, IndexVisit y){
		//Get the distance between two visits
		if ((x == null)||(y==null))
			return 0;
		else			{
		//	Location lx = new Location(x.getX(),x.getY());
		//	Location ly = new Location(y.getX(),y.getY());
			
			return GHopperInterface.getJourney(x, y, mode).getDistanceKM();
		}
	}

	public void addVisit(FoodelVisit newVis) {
		//Check for duplicate postcodes
		String pc = newVis.getPostCode();
		
		for (Object ov: super.getVisits()) {
			try {
				FoodelVisit v = (FoodelVisit)ov;
				if (v.getPostCode().equals(newVis.getPostCode())) {
					//Combine visits
					v.setOrder(v.getName() +" : " +v.getOrder() +" AND "+ newVis.getName() + " : "+newVis.getOrder());
					v.setName(v.getName() +"+"+newVis.getName());
					v.setDemand(v.getDemand() + newVis.getDemand());
					return;
				}
			}catch(Exception e) {}//Ignore non FoodVisits
		}
		super.addVisit(newVis);
		
	}
		
	
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
	

}
