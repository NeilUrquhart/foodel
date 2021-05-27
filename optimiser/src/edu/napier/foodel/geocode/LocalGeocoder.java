package edu.napier.foodel.geocode;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * Local Geocoder attempts to find an address in the local postcode csv files
 */
public class LocalGeocoder {
	private String dataDir;

	

	public void setDirectory(String aDataDir) {
		dataDir = aDataDir;
	}

	public  Point2D find(String postCode){
		postCode = postCode.trim();
		postCode = postCode.toUpperCase();
		String area = "";
		for (int x=0; x < postCode.length();x++){
			if (Character.isLetter(postCode.charAt(x)))
				area = area + postCode.charAt(x);
			else
				x= postCode.length();
		}
		try{
			InputStream inputStream       = new FileInputStream(dataDir+"/"+area+" postcodes.csv");
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(isr);

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains(postCode)){
					String[] items = line.split(",");

					Point2D res = new Point2D.Double(Double.parseDouble(items[2]),Double.parseDouble(items[3]));
					reader.close();
					return res;
				}
			}
			System.out.println("Postcode not found "+postCode);
			reader.close();
			return null;
		}catch(Exception e){
			return null;
		}
	}

}
