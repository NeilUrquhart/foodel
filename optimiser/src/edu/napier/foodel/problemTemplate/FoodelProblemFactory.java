package edu.napier.foodel.problemTemplate;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import edu.napier.foodel.geo.Geocoder;
import edu.napier.foodel.problem.cvrp.CVRPProblem;

/*
 * Nel Urquhart 2019
 * Create Foodel Problem objects from a file stream based instances

 */

public abstract class  FoodelProblemFactory   {
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	protected HashMap<String,Integer> header;

	public FoodelProblem parseData(HashMap<String, String[]> csvData, FoodelProblem result) throws Exception {

		//Process headers first
		header = new HashMap<String,Integer>();//Setup column headers
		String[] buffer = csvData.get("header 0");
		int idx=1;
		for (String heading : buffer) {
			header.put(heading, idx);
			idx++;
		}

		for(String keyword : csvData.keySet()) {
			processLine(keyword,csvData,result);
		}

		if (result.getEnd() == null) {//if no end specified assume end==start
			result.setEnd(result.getStart());
			result.setEndPCode(result.getStartPcode());
		}
		String check = result.isValid();
		if (check.length()>0)
			throw (new Exception("Your problem file is missing some information :<br>"+check));
		return result;
	}

	private  void processLine(String keyword, HashMap<String, String[]> csvData, FoodelProblem result) throws Exception {

		if (keyword.startsWith("vehicle capacity")){
			String[] buffer = csvData.get(keyword);
			int capacity = Integer.parseInt(buffer[1].trim());
			result.setVehicleCapacity(capacity);
		}

		else if (keyword.contains("date")){
			String[] buffer = csvData.get(keyword);
			result.setDateTime(buffer[1]);
			result.setStartTime(sdf.parse(buffer[1]).getTime());
		}

		else if (keyword.startsWith("speed factor")){
			String[] buffer = csvData.get(keyword);
			double speedF = Double.parseDouble(buffer[1].trim());
			result.setSpeedfactor(speedF);
		}

		else if (keyword.startsWith("round time limit")){
			String[] buffer = csvData.get(keyword);
			int timeLimit= Integer.parseInt(buffer[1]);
			timeLimit = timeLimit * 60000;
			result.setTimeLimitMS(timeLimit);
		}

		else if (keyword.startsWith("delivery time per house ")){
			String[] buffer = csvData.get(keyword);
			int delTime= Integer.parseInt(buffer[1]);
			delTime = delTime * 60000;
			result.setDeliveryTimeMS(delTime);
		}

		else if ((keyword.startsWith("customer"))||(keyword.startsWith("visit"))){
			addVisit(keyword, csvData, result);
		}



		else if (keyword.startsWith("start")){
			String[] buffer = csvData.get(keyword);
			Point2D p = Geocoder.find(buffer[1]);
			FoodelVisit v = new FoodelVisit("Base","","","",p.getX(),p.getY(),0);
			result.setStart(v);
			result.setStartPcode(buffer[1]);
		}

		else if (keyword.startsWith("end")){
			String[] buffer = csvData.get(keyword);
			Point2D p =Geocoder.find(buffer[1]);
			FoodelVisit v = new FoodelVisit("End","","","",p.getX(),p.getY(),0);
			result.setEnd(v);
			result.setEndPCode(buffer[1]);
		}
	}

	protected void addVisit(String keyword, HashMap<String, String[]> csvData, FoodelProblem result) throws Exception {
		String[] buffer = csvData.get(keyword);
		if (header==null) {
			return;
		}
		System.out.println("Adding visit");

		String name  = findAttribute(buffer,"name");
		String address = findAttribute(buffer,"address");
		String postcode  = findAttribute(buffer,"postcode");
		String note  = findAttribute(buffer,"note");
		
		String strdmd = findAttribute(buffer,"bags");
		int demand =0;//default
		if (strdmd!= null)
		  demand = Integer.parseInt(strdmd);
			
		String strlat = findAttribute(buffer,"lat");
		String strlon =  findAttribute(buffer,"lon");

		Point2D loc=null;
		if ((strlat != null)&&(strlon != null))
			loc = new Point2D.Double(Double.parseDouble(strlat),Double.parseDouble(strlon));
		
		if (loc == null)
			loc = Geocoder.find(address + ","+ postcode);
		
		if (loc == null)
			loc = Geocoder.find(postcode);
		
		if (loc == null)
			loc = Geocoder.find(address);
		
		
	
	if (loc== null) {
			throw new Exception("Address not found " + address +" , " + postcode);
		}else {
			FoodelVisit v = new FoodelVisit(name,address,postcode,note,loc.getX(),loc.getY(),demand);
			result.addVisit(v);
		}
	}
	protected String findAttribute(String[] data, String heading) {
		Integer indx = header.get(heading);
		if (indx == null)
			return null;
		
		if (indx<= data.length)
			return data[indx-1];
		
		return null;
	}
}

