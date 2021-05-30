package edu.napier.foodel.geo;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NominatimGeocoder {

	private String nominatimBaseURL = "https://nominatim.openstreetmap.org";

	public Point2D find(String addr) {
		Point2D res = null;
		String request = nominatimBaseURL+ "/search?q="+addr+"&format=xml";
		try {

			URL url = new URL(request);
			HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));

			String response="";
			String buffer;
			while ((buffer =  in.readLine()) != null) {
				response += buffer;
			}
			res = extractLocation(response);
			in.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private Point2D extractLocation(String xml){
		/*
		 * A function that very crudely extracts the lat and lon from 
		 * the supplied xml and returns them as a Point2D object.
		 * 
		 * If anything goes wrong, null is returned.
		 */
		try {
			double lat=Double.MIN_VALUE,lon=Double.MIN_VALUE;
			String[] data = xml.split(" ");
			for (String section: data) {
				if (section.contains("lat='")) {
					//extract lat
					if (lat == Double.MIN_VALUE)
						lat = Double.parseDouble(section.split("'")[1]);
				}
				if (section.contains("lon='")) {
					//extract lat
					if (lon == Double.MIN_VALUE)
						lon = Double.parseDouble(section.split("'")[1]);
				}
			}
			return new Point2D.Double(lat,lon);

		}catch(Exception e) {
			return null;
		}	
	}
}


