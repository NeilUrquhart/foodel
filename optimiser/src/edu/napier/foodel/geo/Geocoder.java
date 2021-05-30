package edu.napier.foodel.geo;

import java.awt.geom.Point2D;
import java.io.BufferedReader;

import java.io.FileInputStream;

import java.io.InputStream;
import java.io.InputStreamReader;




public class Geocoder {

	private static LocalGeocoder postcodes = new LocalGeocoder();
	private static NominatimGeocoder nominatim = new NominatimGeocoder();

	

	public static void setDirectory(String aDataDir) {
		postcodes.setDirectory(aDataDir);
	}

	public static Point2D find(String addr){
		Point2D result  = null;
		if (addr.length()<10)
			result = postcodes.find(addr);

		if (result == null) {
			result = nominatim.find(addr);
		}

		return result;

	}

}
