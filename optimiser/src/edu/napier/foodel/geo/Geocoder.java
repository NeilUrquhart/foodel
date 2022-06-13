package edu.napier.foodel.geo;

import java.awt.geom.Point2D;
import java.io.BufferedReader;

import java.io.FileInputStream;

import java.io.InputStream;
import java.io.InputStreamReader;

import edu.napier.foodel.utils.FoodelProperties;




public class Geocoder {

	private static LocalGeocoder postcodes = new LocalGeocoder();
	private static NominatimGeocoder nominatim = new NominatimGeocoder();

//	public enum Type{REMOTE,LOCAL};

//	public static void setDirectory( String aDataDir) {
//		postcodes.setDirectory(aDataDir);
//	}

	public static Point2D find(String addr, String postcode)throws Exception
	{
		Point2D result  = null;
		String url = FoodelProperties.getInstance().get("nominatimurl");
		if  (url!=null)
			if (url.contains("http")) //It's likely to be a url of some sort
				result = nominatim.find(addr);
		if (result == null)
			if (postcode!=null)
				result = postcodes.find(postcode);
		
		if (result == null) {
			throw new Exception("I cannot process the address " + addr +"\n"+
		"Check you have either a connection to a Nominatim server or are using postcodes.");
		}
		return result;

	}

}
