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
	//private  BufferedReader b;

	//	public static FoodelProblem parseStream(InputStream problemStream, FoodelProblem result) throws Exception{
	//		BufferedReader b;
	//		try {
	//			InputStreamReader isr = new InputStreamReader(problemStream,StandardCharsets.UTF_8);
	//			b = new BufferedReader(isr); 
	//		}catch(Exception e) {
	//			throw new Exception("Can't open file:");
	//		}
	//		//int capacity =0;
	//		
	//		String readLine = "";
	//
	//		while ((readLine = b.readLine()) != null) {
	//			readLine =readLine.trim();
	//			processLine(result, readLine);
	//		}
	//		b.close();
	//
	//		if (result.getEnd() == null) {//if no end specified assume end==start
	//			result.setEnd(result.getStart());
	//			result.setEndPCode(result.getStartPcode());
	//		}
	//		
	//		String check = result.isValid();
	//		if (check.length()>0)
	//			throw (new Exception("Your problem file is missing some information :<br>"+check));
	//			
	//		return result;
	//
	//	}
	//	
	//	private static void processLine(FoodelProblem result, String readLine) throws ParseException, Exception {
	//		if (readLine.startsWith("Vehicle Capacity")){
	//			String[] buffer = readLine.split(",");
	//			int capacity = Integer.parseInt(buffer[1].trim());
	//			result.setVehicleCapacity(capacity);
	//		}
	//
	//		else if (readLine.contains("Date")){
	//			String[] buffer = readLine.split(",");
	//			result.setDateTime(buffer[1]);
	//			result.setStartTime(sdf.parse(buffer[1]).getTime());
	//		}
	//
	//		else if (readLine.startsWith("Speed factor")){
	//			String[] buffer = readLine.split(",");
	//			double speedF = Double.parseDouble(buffer[1].trim());
	//			result.setSpeedfactor(speedF);
	//		}
	//
	//		else if (readLine.startsWith("Round time limit (mins)")){
	//			int timeLimit= Integer.parseInt(readLine.split(",")[1]);
	//			timeLimit = timeLimit * 60000;
	//			result.setTimeLimitMS(timeLimit);
	//		}
	//
	//		else if (readLine.startsWith("Delivery time per house (mins)")){
	//			int delTime= Integer.parseInt(readLine.split(",")[1]);
	//			delTime = delTime * 60000;
	//			result.setDeliveryTimeMS(delTime);
	//		}
	//
	//		else if ((readLine.startsWith("Customer"))||(readLine.startsWith("Visit"))){
	//			String[] buffer = readLine.trim().split(",");
	//			System.out.println(buffer[0]);//cust
	//			System.out.println(buffer[1]);//Address
	//			int bags =1;
	//			if (buffer.length >2) {
	//				System.out.println(buffer[2]);//Bags
	//				bags = Integer.parseInt(buffer[2]);
	//			}
	//			String note = "";
	//			if (buffer.length>3) {
	//				note = buffer[3]; 
	//				note = note.replace('<', ' ');
	//				note = note.replace('>', ' ');
	//				note = note.replace('(', ' ');
	//				note = note.replace(')', ' ');
	//				note = note.replace('&', ' ');
	//				note = note.replace('+', ' ');
	//				System.out.println(note);//Note
	//			}
	//			Point2D p = Geocoder.find(buffer[1]);
	//			if (p== null) {
	//				throw new Exception("Address not found " + buffer[1]);
	//			}else {
	//				FoodelVisit v = new FoodelVisit(buffer[1],p.getX(),p.getY(),bags);
	//				v.setOrder(note);
	//				//v.setPostCode(buffer[1]);
	//				v.setAddress(buffer[1]);
	//				result.addVisit(v);
	//			}
	//		}
	//
	//		else if (readLine.startsWith("Start")){
	//			String[] buffer = readLine.trim().split(",");
	//			System.out.println(buffer[1]);
	//			Point2D p = Geocoder.find(buffer[1]);
	//			FoodelVisit v = new FoodelVisit("Depot",p.getX(),p.getY(),0);
	//			result.setStart(v);
	//			result.setStartPcode(buffer[1]);
	//		}
	//
	//		else if (readLine.startsWith("End")){
	//			String[] buffer = readLine.trim().split(",");
	//			System.out.println(buffer[1]);
	//			Point2D p =Geocoder.find(buffer[1]);
	//			FoodelVisit v = new FoodelVisit("End",p.getX(),p.getY(),0);
	//			result.setEnd(v);
	//			result.setEndPCode(buffer[1]);
	//		}
	//	}

	public FoodelProblem parseData(HashMap<String, String[]> csvData, FoodelProblem result) throws Exception {
		

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

		if (keyword.startsWith("Vehicle Capacity")){
			String[] buffer = csvData.get(keyword);
			int capacity = Integer.parseInt(buffer[1].trim());
			result.setVehicleCapacity(capacity);
		}

		else if (keyword.contains("Date")){
			String[] buffer = csvData.get(keyword);
			result.setDateTime(buffer[1]);
			result.setStartTime(sdf.parse(buffer[1]).getTime());
		}

		else if (keyword.startsWith("Speed factor")){
			String[] buffer = csvData.get(keyword);
			double speedF = Double.parseDouble(buffer[1].trim());
			result.setSpeedfactor(speedF);
		}

		else if (keyword.startsWith("Round time limit (mins)")){
			String[] buffer = csvData.get(keyword);
			int timeLimit= Integer.parseInt(buffer[1]);
			timeLimit = timeLimit * 60000;
			result.setTimeLimitMS(timeLimit);
		}

		else if (keyword.startsWith("Delivery time per house (mins)")){
			String[] buffer = csvData.get(keyword);
			int delTime= Integer.parseInt(buffer[1]);
			delTime = delTime * 60000;
			result.setDeliveryTimeMS(delTime);
		}

		else if ((keyword.startsWith("Customer"))||(keyword.startsWith("Visit"))){
			String[] buffer = csvData.get(keyword);
			System.out.println(buffer[0]);//cust
			System.out.println(buffer[1]);//Address
			int bags =1;
//			if (buffer.length >2) {
//				System.out.println(buffer[2]);//Bags
//				bags = Integer.parseInt(buffer[2]);
//			}
			String note = "";
			if (buffer.length>4) {
				note = buffer[4]; 
				note = note.replace('<', ' ');
				note = note.replace('>', ' ');
				note = note.replace('(', ' ');
				note = note.replace(')', ' ');
				note = note.replace('&', ' ');
				note = note.replace('+', ' ');
				System.out.println(note);//Note
			}
			Point2D p = Geocoder.find(buffer[2]);
			if (p== null) {
				throw new Exception("Address not found " + buffer[2]);
			}else {
				FoodelVisit v = new FoodelVisit(buffer[1],buffer[2],note,p.getX(),p.getY(),bags);
				//v.setOrder(note);
				//v.setPostCode(buffer[1]);
				//v.setAddress(buffer[1]);
				result.addVisit(v);
			}
		}

		else if (keyword.startsWith("Start")){
			String[] buffer = csvData.get(keyword);
			System.out.println(buffer[1]);
			Point2D p = Geocoder.find(buffer[1]);
			FoodelVisit v = new FoodelVisit("Depot","","",p.getX(),p.getY(),0);
			result.setStart(v);
			result.setStartPcode(buffer[1]);
		}

		else if (keyword.startsWith("End")){
			String[] buffer = csvData.get(keyword);
			System.out.println(buffer[1]);
			Point2D p =Geocoder.find(buffer[1]);
			FoodelVisit v = new FoodelVisit("End","","",p.getX(),p.getY(),0);
			result.setEnd(v);
			result.setEndPCode(buffer[1]);
		}
	}
}

