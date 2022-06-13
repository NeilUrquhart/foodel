package edu.napier.foodel.geo;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.napier.foodel.utils.FoodelProperties;

/*
 * Local Geocoder attempts to find an address in the local postcode csv files
 */
public class LocalGeocoder {
//	private String dataDir;
//
//	
//
//	public void setDirectory(String aDataDir) {
//		dataDir = aDataDir;
//	}

	public  Point2D find(String postCode) throws Exception{
		
		postCode = postCode.trim();
		postCode = postCode.toUpperCase();

		try{
			String data = FoodelProperties.getInstance().get("datadir") +FoodelProperties.getInstance().get("postcodefile");
			InputStream inputStream       = new FileInputStream(data);
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
			reader.close();
			throw new Exception ("Postcode not found "+postCode);
			//return null;
		}catch(Exception e){
			throw new Exception ("Postcode not found "+postCode);
		}
	}

}
