package edu.napier.foodel.geo;


import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.PointList;

import edu.napier.foodel.problemTemplate.FoodelVisit;




public class GHopperInterface {
	private static GraphHopperOSM hopper=null;
	private static Journey[][] cache;
	private static String fileName;
	private static String folder;
	private static double  timeFactor = 1.6;
	
	
	public static void setCacheSize() {
		int size = FoodelVisit.getCounter()+1;
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
	
	public static Journey getJourney(FoodelVisit vlast, FoodelVisit vnext, String type){
		Journey res;
		FoodelVisit last = (FoodelVisit) vlast;
		FoodelVisit next= (FoodelVisit) vnext;
		try {
			res = cache[last.getIndex()][next.getIndex()];
			if (res != null)
				return res;

			res = findJourney(last, next, type);
		}catch(Exception e) {
			//if hopper fails, then calculate journey based on Euclidean dist
			res = new Journey(last, next);
			res.setDistanceKM(Haversine.haversine(last,next));
			ArrayList<Point2D.Double>path = new ArrayList<Point2D.Double>();
			path.add(last);
			path.add(next);
			res.setPath(path);
			

		}
		cache[last.getIndex()][next.getIndex()] =res;
		return res;
	}

	public static Journey findJourney(FoodelVisit start, FoodelVisit end, String type) {
	
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
	    ArrayList<Point2D.Double> path = new ArrayList<Point2D.Double>();
	    PointList pl = pw.getPoints();
	     for (int c=0; c < pl.size();c++){
	    		path.add(new Point2D.Double(pl.getLatitude(c),pl.getLongitude(c)));
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
	
	
}
