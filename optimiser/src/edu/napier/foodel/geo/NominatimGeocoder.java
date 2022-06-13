package edu.napier.foodel.geo;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;

import edu.napier.foodel.utils.FoodelProperties;

public class NominatimGeocoder {

	//private String nominatimBaseURL = "https://nominatim.openstreetmap.org";//"http://localhost:9090";//
	private  String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

	public Point2D find(String addr) throws Exception {
		String nominatimBaseURL = FoodelProperties.getInstance().getInstance().get("nominatimurl");
		try {
			Thread.sleep(2000);//Must ensure that Nominatim requests are spaced out
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		

		Point2D res = null;
		addr = encodeValue(addr);
		String request = nominatimBaseURL+ "/search?q="+addr+"&format=xml";
		
		//Force UTF-8
		
		
		ByteBuffer bufferutf8 = StandardCharsets.UTF_8.encode(request); 
		request = StandardCharsets.UTF_8.decode(bufferutf8).toString();
//		
		try {

			URL url = new URL(request);
			URLConnection connection;
			if ((nominatimBaseURL.contains("https"))||((nominatimBaseURL.contains("HTTPS")))) {
				
			     connection = (HttpsURLConnection)url.openConnection();
				 connection.setRequestProperty("User-Agent", "foodel");
				 
			}
			else
			{
				connection = (HttpURLConnection)url.openConnection();
			}
//			HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
			
			 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));

			String response="";
			String buffer;
			while ((buffer =  in.readLine()) != null) {
				response += buffer;
			}
			res = extractLocation(response);
			in.close();
		}catch(Exception e) {
			return null;
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


