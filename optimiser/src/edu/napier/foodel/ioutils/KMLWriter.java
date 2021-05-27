package edu.napier.foodel.ioutils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class KMLWriter {

	private String routes = "";
	private String places = "";
	private static String directory="";
	
	public static void setDataDir(String d) {
		directory = d;
	}
			

	public KMLWriter() {
		//Load in header
		 /*   try
		    {
		        kml = new String ( Files.readAllBytes( Paths.get("./kmlHeader.txt")) );
		    }
		    catch (IOException e)
		    {
		        e.printStackTrace();
		    }*/
	}
	

	 public void addPlacemark(double lat, double lon, String title, String desc, String type) {
		String style = "style3";
		if (type.equals("del")) {
			style = "styleDel";
		}
		if (type.equals("rv")) {
			style = "styleRV";
		}
		
		if (type.equals("delivery")) {
			style = "delivery";
		}
		if (type.equals("cafe")) {
			style = "cafe";
		}
		String place = "<Placemark>" +
	    "<name>"+ title +"</name>" +
	    "<description><![CDATA["+desc+"]]></description>" +
	    "<styleUrl>#"+style+"</styleUrl>" +
//	    "<Polygon>" +
//	      "<outerBoundaryIs>" +
//	        "<LinearRing>" +
//	         // "<tessellate>1</tessellate>" +
//	          "<coordinates>" +
//	            lon +"," + lat + ",0.000000 " +
//	            (lon +0.00009) +"," + (lat+0.00009) + ",0.000000 " +
//	            (lon +0.00009)+"," + (lat-0.00009) + ",0.000000 " +
//	            
//	          "</coordinates>" +
//	        "</LinearRing>" +
//	      "</outerBoundaryIs>" +
//	    "</Polygon>" +
"<Point> " +
" <coordinates> "+lon+ "," + lat+ ",1</coordinates> " +
" </Point> " +
	  "</Placemark>";
		
		 /*
		String place = "<Placemark> "+
		    "<name>Simple placemark</name>"+
		    "<description>Attached to the ground. Intelligently places itself   at the height of the underlying terrain.</description>" +
		    "<Point> " +
		     " <coordinates> "+lon+ "," + lat+ ",1</coordinates> " +
		    " </Point> " +
		  "</Placemark> ";*/
		places = places + place;
	}
	 
	public void addRoute(ArrayList<Double> lat, ArrayList<Double>lon, String name, String desc, String colour) {
		String style = "styleBLine";

		if (colour.equals("blue"))
			style = "styleBLine";
		
		if (colour.equals("green"))
			style = "styleGLine";

		if (colour.equals("red"))
			style = "styleRLine";

		if (colour.equals("yellow"))
			style = "styleYLine";
		
		if (colour.equals("darkgreen"))
			style = "styleDGLine";

	String place = " <Placemark> " +
	    "<name>#"+name+"</name> " +
	    "<description><![CDATA["+desc+"]]></description> " +
	    "<styleUrl>#"+style+"</styleUrl> " +
	    "<LineString> " +
	     // "<tessellate>1</tessellate> " +
	      "<coordinates> ";
	      	for (int x=0; x < lat.size(); x++) {
	      		place = place + lon.get(x)+"," +lat.get(x)+ ",0.000000 ";
	      	}
			
	      place = place +"</coordinates> " +
	    "</LineString> " +
	  "</Placemark>";
	      routes = routes + place;
	}
	
	public void writeFile(String fName) {
		//Add footer
		try {
			String kml = new String ( Files.readAllBytes( Paths.get(directory+"/kmlHeader.txt")) );
			kml = kml + routes;
			kml = kml + places;

			kml = kml + "\n" +new String ( Files.readAllBytes( Paths.get(directory+"/kmlFooter.txt")) );
			fName = fName+".kml";
			Files.write(Paths.get(fName), kml.getBytes());

			kml = "";
		}
		catch (IOException e)
		{
	        e.printStackTrace();
	    }
	}
}
