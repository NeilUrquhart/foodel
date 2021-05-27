package edu.napier.foodel.ioutils;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.napier.foodel.geocode.Geocoder;
import edu.napier.foodel.problem.FoodelProblem;
import edu.napier.foodel.problem.FoodelVisit;
import edu.napier.foodel.problem.IndexVisit;
import edu.napier.ghopper.GHopperInterface;

/*
 * Nel Urquhart 2019
 * Create VRPPProblem objects from file based instances
 * This Factory assumes that:
 * 

 * 
 */

public class ProblemParser   {
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	//private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	
	static BufferedReader b;
	public static FoodelProblem buildProblem(String fName, FoodelProblem result) throws Exception{
		
		try {
			File f = new File(fName);
			b = new BufferedReader(new FileReader(f));
		}catch(Exception e) {
			throw new Exception("Can't open file:"+fName);
		}
		int capacity =0;
		int c=0;
		boolean foundRef = false;
		int visitCount =0;
		
		String readLine = "";
		
		while ((readLine = b.readLine()) != null) {
			readLine =readLine.trim();
			if (readLine.contains("Order ID,Products,")) {//"Order ID,Products,Shipping Address,Delivery Date")) {//||(readLine.contains("Name  ,Delivery Instructions,Address,Delivery Date,Post code"))) {
				readBagelFormat(result);
			}

			else if (readLine.startsWith("Vehicle Capacity")){
				String[] buffer = readLine.split(",");
				capacity = Integer.parseInt(buffer[1].trim());
				result.setCapacity(capacity);
			}

			else if (readLine.contains("Date")){
				String[] buffer = readLine.split(",");
				result.setDateTime(buffer[1]);
				//result.setStartDate(result.getDateOnlyformatter().parse(buffer[1]).getTime());
				result.setStartTime(sdf.parse(buffer[1]).getTime());
			}

			else if (readLine.startsWith("Speed factor")){
				String[] buffer = readLine.split(",");
				double speedF = Double.parseDouble(buffer[1].trim());
				result.setSpeedfactor(speedF);
			}

			else if (readLine.startsWith("Round time limit (mins)")){
				int timeLimit= Integer.parseInt(readLine.split(",")[1]);
				timeLimit = timeLimit * 60000;
				result.setTimeLimitMS(timeLimit);
			}

			else if (readLine.startsWith("Delivery time per house (mins)")){
				int delTime= Integer.parseInt(readLine.split(",")[1]);
				delTime = delTime * 60000;
				result.setDeliveryTimeMS(delTime);
			}

			else if (readLine.contains("Reference")){
				String ref= readLine.split(",")[1];
				
				Pattern pattern = Pattern.compile("\\p{Alnum}+");
				Matcher matcher = pattern.matcher(ref);
				if (!matcher.matches()) {
				   throw new Exception("The reference may only be letters and numbers, no spaces or punctuation allowed. "+ref);
				}
				result.setReference(ref);
				foundRef = true;
			}	
			else if ((readLine.startsWith("Customer"))||(readLine.startsWith("Visit"))){
				visitCount++;
				String[] buffer = readLine.trim().split(",");
				System.out.println(buffer[0]);//cust
				System.out.println(buffer[1]);//Pcode
				int bags =1;
				if (buffer.length >2) {
				  System.out.println(buffer[2]);//Bags
				  bags = Integer.parseInt(buffer[2]);
				}
				String note = "";
				if (buffer.length>3) {
					note = buffer[3]; 
				
					note = note.replace('<', ' ');
					note = note.replace('>', ' ');
					note = note.replace('(', ' ');
					note = note.replace(')', ' ');
					note = note.replace('&', ' ');
					note = note.replace('+', ' ');
					
				  
					  
				  System.out.println(note);//Note
				}
				
			
				
				
				Point2D p = loadPoint(buffer[1]);
				if (p== null) {
					throw new Exception("Postcode not found " + buffer[1]);

				}else {
					
					FoodelVisit v = new FoodelVisit(buffer[1]  +":" +note,p.getX(),p.getY(),bags);
					v.setPostCode(buffer[1]);
					v.setAddress("");
				
					if (buffer.length >= 6) {
						String winStart = buffer[4];
						String winEnd = buffer[5];
						v.setWinend(sdf.parse(winEnd).getTime());
						v.setWinstart(sdf.parse(winStart).getTime());
						}

						result.addVisit(v);


					c++;
				}
			}

			else if (readLine.startsWith("Start")){
				String[] buffer = readLine.trim().split(",");
				System.out.println(buffer[1]);
				Point2D p =loadPoint(buffer[1]);
				IndexVisit v = new IndexVisit("Depot",p.getX(),p.getY(),0);
				result.setStart(v);
				result.setStartPcode(buffer[1]);
			}
			
			else if (readLine.startsWith("End")){
				String[] buffer = readLine.trim().split(",");
				System.out.println(buffer[1]);
				Point2D p =loadPoint(buffer[1]);
				IndexVisit v = new IndexVisit("End",p.getX(),p.getY(),0);
				result.setEnd(v);
				result.setEndPCode(buffer[1]);
			}
			
			
			
			
		}
		b.close();
		
		if (result.getEnd() == null) {//if no end specified assume end==start
			result.setEnd(result.getStart());
			result.setEndPCode(result.getStartPcode());
		}
		return result;

	}
	

	
	public static Point2D loadPoint(String data) {
		//Assume post code, but if contains : assume lat:lon
		Point2D res;
		if (data.contains(":")) {
			String[] buffer = data.split(":");
			double lat = Double.parseDouble(buffer[0]);
			double lon = Double.parseDouble(buffer[1]);
			res = new Point2D.Double(lat,lon);
		}
		else {
		  res =Geocoder.find(data);
		}
		return res;
	}
	
	private static void readBagelFormat(FoodelProblem res) throws Exception{
		char c;

		int i;
		ArrayList<String> tokens = new ArrayList<String>();
		String current = "";
		boolean inQuotes = false;
		while ((i = b.read()) != -1 ){
			c = (char) i;
			if (c == '"')
				inQuotes = !inQuotes;

			if ((c==',') && (inQuotes==false)){
				tokens.add(current);
				current = "";
			}
			current += c;
			System.out.print(c);
			if ((c=='\r') ||( i==-1)){
				if (tokens.size()>0) {//IE not a blank line
				if (!tokens.get(1).equals(",")) {
					tokens.add(current);
					current = "";
					inQuotes = false;
					System.out.println("\n ADD VISIT");
					String pc="";
					if (tokens.size() > 4) {
					  pc = tokens.get(4).trim();
					  pc = pc.replace(',', ' ').trim();
					}
				//	if (pc.equals(""))
				//		pc = Geocoder.extractFromAddress(tokens.get(2));
					
					

					Point2D p = loadPoint(pc);//GeoCoder.find(pc);
					if (p==null) {
						if (pc.contentEquals("") )
							throw new Exception("Can find a postcode in  "+ tokens.get(2)+"\nHave a look at your CSV file.");
						
						throw new Exception("Can't find the postcode  "+ pc +"\n Have a look at the .CSV file and check the postcode\n"
								+ "does not contain any additional spaces.\nIf the postcode is not recognised you can enter a\n"
								+ "set of lattitude and longitude coordinates.");
					}

					int demand =1;// default
					if (tokens.size()>5) {
						String d = tokens.get(5).replace(',', ' ').trim();
						try {
						   demand = Integer.parseInt(d);
						}catch(Exception e) {
							demand = 1;
						}
						
					}
					
					String orderID = tokens.get(0).replace('\n', ' ');
					orderID = orderID.replace(',', ' ');
					
					orderID = orderID.trim();
					FoodelVisit v = new FoodelVisit(orderID,p.getX(),p.getY(),demand);
					v.setPostCode(pc);
					v.setAddress(tokens.get(2));
					v.setAddress(v.getAddress().replace(',',' '));
					v.setAddress(v.getAddress().replace('\n',' '));

					v.setOrder(tokens.get(1));
					v.setOrder(v.getOrder().replace('\n', ' '));

					v.setOrder(v.getOrder().replace(',', ' '));
					
					v.setShippingDate(tokens.get(3));
					v.setShippingDate(v.getShippingDate().replace(',', ' ').trim());
					res.addVisit(v);
					tokens.clear();
				}else {
					//Assume blank line
					tokens.clear();
				}
				}
			}
		}
		System.out.println("\n DONE");
	

	}
}
