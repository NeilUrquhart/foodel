package edu.napier.foodel.ioutils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

import edu.napier.foodel.problemTemplate.FoodelVisit;


public class HTMmapwriter {
	private String segments = "";
	private String waypoints ="";
	private String waypointList = "";

	public  void addPath(ArrayList<Double> lat, ArrayList<Double>lon) {
		String buffer ="	var pointList = [" ;

		for (int c=0 ; c < lat.size(); c++) {
			double lt = lat.get(c);
			double ln = lon.get(c);
			buffer += " new L.LatLng( " +lt+", "+ln+ ") ";
			if (c <lat.size()-1) buffer  +=",";
		}
		buffer += " ];\n" ;
		buffer +="\n" + 
				"	var firstpolyline = new L.Polyline(pointList, {\n" + 
				"	    color: 'red',\n" + 
				"	    weight: 3,\n" + 
				"	    opacity: 0.5,\n" + 
				"	    smoothFactor: 1\n" + 
				"	});\n" + 
				"	firstpolyline.addTo(mymap);";
		segments += buffer;
	}

	 public void addWayPoint(FoodelVisit v,int order, String time ) {
		 String name = v.getName().replace('\n', ' ').trim();
		 waypoints +=
				 "	var marker = new L.marker([" +v.getX()+"," +v.getY()+ "], { opacity: 0.01 }); \n" + 
					  		"	marker.bindTooltip(\" "+order +" : "+name+"   \", {permanent: true, className: \"my-label\", offset: [0, 0] });\n" + 
					  		"marker.addTo(mymap);\n";
		 
		 //if (v instanceof FoodelVisit) {
			 FoodelVisit f = (FoodelVisit)v;
			 waypointList += "<tr><td>" + order + "</td><td>" +time+ "</td><td>"+v.getName() +"</td><td>"+f.getAddress()+ "</td><td>"+f.getOrder() +"</td></tr>\n";
		// }else			 
		// {
		//	 waypointList += "<tr><td>" + order + "</td><td>" +time+ "</td><td>"+v.getName() +"</td></tr>\n";
		// }		 
	 }
	 
//     public  String html(VRPVisit start,String id, int run) {
//     String header = "<!DOCTYPE html>\n" + 
//     		"<html>\n" + 
//     		"<head>\n" + 
//     		"	\n" + 
//     		"	<title>Foodel</title>\n" + 
//     		"\n" + 
//     		"	<meta charset=\"utf-8\" />\n" + 
//     		"	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + 
//     		"	\n" + 
//     		"	<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"docs/images/favicon.ico\" />\n" + 
//     		"\n" + 
//     		"    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.css\" integrity=\"sha512-xwE/Az9zrjBIphAcBb3F6JVqxf46+CDLwfLMHloNu6KEQCAWi6HcDUbeOfBIptF7tcCzusKFjFw2yuvEpDL9wQ==\" crossorigin=\"\"/>\n" + 
//     		"    <script src=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.js\" integrity=\"sha512-gZwIG9x3wUXg2hdXF6+rVkLF/0Vi9U8D2Ntg4Ga5I5BZpVkVxlJWbSQtXPSiUTtC0TjtGOmxa1AJPuV0CPthew==\" crossorigin=\"\"></script>\n" + 
//     		"\n" + 
//     		"\n" + 
//     		"	\n" + 
//     		"</head>\n" + 
//     		"<body>\n" + 
//     		"<H1> foodel </H1><br><H1>delivery ref: " +id +"  </H1>" +
//     		"<H2>  Run :" + run +"</H2>" +
//     		"\n" + 
//     		"<div id=\"mapid\" style=\"width: 1000px; height:600px;\"></div>\n" + 
//     		"<script>\n" + 
//     		"\n" + 
//     		"	var mymap = L.map('mapid').setView([" +start.getX()+","+start.getY()+"], 13);\n" + 
//     		"\n" + 
//     		"	L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {\n" + 
//     		"		maxZoom: 18,\n" + 
//     		"		attribution: 'Map data &copy; <a href=\"https://www.openstreetmap.org/\">OpenStreetMap</a> contributors, ' +\n" + 
//     		"			'<a href=\"https://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>, ' +\n" + 
//     		"			'Imagery © <a href=\"https://www.mapbox.com/\">Mapbox</a>',\n" + 
//     		"		id: 'mapbox/streets-v11',\n" + 
//     		"		tileSize: 512,\n" + 
//     		"		zoomOffset: -1\n" + 
//     		"	}).addTo(mymap);";
//     
//   
//     String footer = "</script>\n" + 
//     		"\n" + 
//     		"<h2>Delivery schedule </h2>\n<table>" + 
//     		waypointList + 
//     		"</table>";
//
//     return header+middle()+footer;
//       
// }
     
     public  String body(FoodelVisit start,String id, int run) {
         return
         		"<H2> "+id+" Route :" + run +"</H2>" +
         		"\n" + 
         		"<div id=\"mapid\" style=\"width: 1000px; height: 600px;\"></div>\n" + 
         		"<script>\n" + 
         		"\n" + 
         		"	var mymap = L.map('mapid').setView([" +start.getX()+","+start.getY()+"], 13);\n" + 
         		"\n" + 
         		"	L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {\n" + 
         		"		maxZoom: 18,\n" + 
         		"		attribution: 'Map data &copy; <a href=\"https://www.openstreetmap.org/\">OpenStreetMap</a> contributors, ' +\n" + 
         		"			'<a href=\"https://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>, ' +\n" + 
         		"			'Imagery © <a href=\"https://www.mapbox.com/\">Mapbox</a>',\n" + 
         		"		id: 'mapbox/streets-v11',\n" + 
         		"		tileSize: 512,\n" + 
         		"		zoomOffset: -1\n" + 
         		"	}).addTo(mymap);"+ 
         		middle() +
         		"</script>\n" + 
         		"\n" + 
         		"<h2>Schedule</h2>\n<table>" + 
         		waypointList + 
         		"</table>";

           
     }
//     public  String header() {
//    	 return
//         		"	<meta charset=\"utf-8\" />\n" + 
//         		"	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + 
//         		"	\n" + 
//         		"	<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"docs/images/favicon.ico\" />\n" + 
//         		"\n" + 
//         		"    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.css\" integrity=\"sha512-xwE/Az9zrjBIphAcBb3F6JVqxf46+CDLwfLMHloNu6KEQCAWi6HcDUbeOfBIptF7tcCzusKFjFw2yuvEpDL9wQ==\" crossorigin=\"\"/>\n" + 
//         		"    <script src=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.js\" integrity=\"sha512-gZwIG9x3wUXg2hdXF6+rVkLF/0Vi9U8D2Ntg4Ga5I5BZpVkVxlJWbSQtXPSiUTtC0TjtGOmxa1AJPuV0CPthew==\" crossorigin=\"\"></script>\n";           
//     }
	 
//	        public  void write(String webRoot, String fileName, VRPVisit start,String id, int run) {
//	        String header = "<!DOCTYPE html>\n" + 
//	        		"<html>\n" + 
//	        		"<head>\n" + 
//	        		"	\n" + 
//	        		"	<title>Foodel</title>\n" + 
//	        		"\n" + 
//	        		"	<meta charset=\"utf-8\" />\n" + 
//	        		"	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + 
//	        		"	\n" + 
//	        		"	<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"docs/images/favicon.ico\" />\n" + 
//	        		"\n" + 
//	        		"    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.css\" integrity=\"sha512-xwE/Az9zrjBIphAcBb3F6JVqxf46+CDLwfLMHloNu6KEQCAWi6HcDUbeOfBIptF7tcCzusKFjFw2yuvEpDL9wQ==\" crossorigin=\"\"/>\n" + 
//	        		"    <script src=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.js\" integrity=\"sha512-gZwIG9x3wUXg2hdXF6+rVkLF/0Vi9U8D2Ntg4Ga5I5BZpVkVxlJWbSQtXPSiUTtC0TjtGOmxa1AJPuV0CPthew==\" crossorigin=\"\"></script>\n" + 
//	        		"\n" + 
//	        		"\n" + 
//	        		"	\n" + 
//	        		"</head>\n" + 
//	        		"<body>\n" + 
//	        		"<H1> foodel </H1><br><H1>delivery ref: " +id +"  </H1>" +
//	        		"<H2>  Run :" + run +"</H2>" +
//	        		"\n" + 
//	        		"<div id=\"mapid\" style=\"width: 1200px; height: 800px;\"></div>\n" + 
//	        		"<script>\n" + 
//	        		"\n" + 
//	        		"	var mymap = L.map('mapid').setView([" +start.getX()+","+start.getY()+"], 13);\n" + 
//	        		"\n" + 
//	        		"	L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {\n" + 
//	        		"		maxZoom: 18,\n" + 
//	        		"		attribution: 'Map data &copy; <a href=\"https://www.openstreetmap.org/\">OpenStreetMap</a> contributors, ' +\n" + 
//	        		"			'<a href=\"https://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>, ' +\n" + 
//	        		"			'Imagery © <a href=\"https://www.mapbox.com/\">Mapbox</a>',\n" + 
//	        		"		id: 'mapbox/streets-v11',\n" + 
//	        		"		tileSize: 512,\n" + 
//	        		"		zoomOffset: -1\n" + 
//	        		"	}).addTo(mymap);";
//	        
//	      
//	        String footer = "</script>\n" + 
//	        		"\n" + 
//	        		"<h2>Delivery schedule </h2>\n<table>" + 
//	        		waypointList + 
//	        		"</table></body>\n" + 
//	        		"</html>";
//
//	        try {
//	        	File file = new File( /*WebServer.getRoot() */ webRoot+"/"+fileName);
//	            FileWriter writer = new FileWriter(file, false);
//	            writer.append(header);
//	           
//	            writer.append(middle());
//	            
//	            writer.append(footer);
//	            writer.flush();
//	            writer.close();
//	            
//	        } catch (IOException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//	    }
	        
	  private String middle() {
		  return waypoints +" \n " + segments;

	  }

}
