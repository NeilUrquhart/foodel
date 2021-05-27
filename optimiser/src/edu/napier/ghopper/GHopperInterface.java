package edu.napier.ghopper;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;

import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;

import edu.napier.foodel.basic.Journey;
import edu.napier.foodel.basic.VRPVisit;
import edu.napier.foodel.problem.IndexVisit;


public class GHopperInterface {
	private static GraphHopperOSM hopper=null;
	private static Journey[][] cache;
	private static String fileName;
	private static String folder;
	private static double  timeFactor = 1.6;
	
	
	public static void setCacheSize() {
		int size = IndexVisit.getCounter()+1;
		cache = new Journey[size][];
		for (int c=0; c < size;c++)
			cache[c] = new Journey[size];
	}
	
	public static void setFileName(String fName) {
		fileName = fName;
	}
	
	public static void setFolder(String path) {
		folder = path;
	}
	
	public static Journey getJourney(VRPVisit vlast, VRPVisit vnext, String type){
		Journey res;
		IndexVisit last = (IndexVisit) vlast;
		IndexVisit next= (IndexVisit) vnext;
		try {

	
			res = cache[last.getIndex()][next.getIndex()];
			if (res != null)
				return res;

			res = findJourney(last, next, type);
		}catch(Exception e) {
			//if hopper fails, then calculate journey based on Euclidean dist
			res = new Journey(last, next);
			res.setDistanceKM(haversine(last,next));
			ArrayList<VRPVisit>path = new ArrayList<VRPVisit>();
			path.add(last);
			path.add(next);
			res.setPath(path);
			

		}
		cache[last.getIndex()][next.getIndex()] =res;
		return res;
	}

	private static Journey findJourney(VRPVisit start, VRPVisit end, String type) {
	
		if (hopper==null)
			init();
		
		GHRequest request = new GHRequest(start.getX(),start.getY(),end.getX(),end.getY()).setVehicle(type);
	    GHResponse response = hopper.route(request);
	    if (response.hasErrors()) {
	        throw new IllegalStateException("S= " + start + "e= " + end +". GraphHopper gave " + response.getErrors().size()
	                + " errors. First error chained.",
	                response.getErrors().get(0)
	        );
	    }
	    
	    PathWrapper pw = response.getBest();
	    
	    Journey res = new Journey(start,end);
	    res.setDistanceKM(pw.getDistance()/1000);//Check
	    res.setTravelTimeMS( (long) (pw.getTime()*timeFactor));//Check
	    ArrayList<VRPVisit> path = new ArrayList<VRPVisit>();
	    PointList pl = pw.getPoints();
	     for (int c=0; c < pl.size();c++){
	    		path.add(new VRPVisit("",pl.getLatitude(c),pl.getLongitude(c)));
	    }
	    res.setPath(path);
	    
	    return res;
	}

	public static GraphHopperOSM init(String folder,String fName) {
		setFileName(fName);
		setFolder(folder);
		return init();
	}
	
	private static GraphHopperOSM init() {
		hopper = new GraphHopperOSM();
		hopper.setOSMFile(folder+"/"+fileName);
		hopper.setCHEnabled(false); // CH does not work with shortest weighting (at the moment)
		
		// where to store GH files?
		String store = folder +"/osm/";
		File dir = new File(store);
		if (!dir.exists()){
			try{
			Path path = Paths.get(store);
			Files.createDirectories(path);
			//dir.mkdir();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		hopper.setGraphHopperLocation(store );
		hopper.setEncodingManager(new EncodingManager("car,bike,foot"));

		// this may take a few minutes
		hopper.importOrLoad();
		return hopper;
	}

	private static double haversine(VRPVisit start, VRPVisit finish) {
		/* from https://rosettacode.org/wiki/Haversine_formula */
		final double R = 6372.8; // In kilometers
	    
        double dLat = Math.toRadians(finish.getX()- start.getX());
        double dLon = Math.toRadians(finish.getY() - start.getY());
        double lat1 = Math.toRadians(start.getX());
        double lat2 = Math.toRadians(finish.getX());
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
