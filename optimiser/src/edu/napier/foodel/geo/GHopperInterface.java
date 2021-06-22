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
import com.graphhopper.ResponsePath;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.LMProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.parsers.OSMSurfaceParser;
import com.graphhopper.routing.weighting.custom.CustomProfile;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;

import java.util.Arrays;
import java.util.Locale;

import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.LIMIT;
import static com.graphhopper.json.Statement.Op.MULTIPLY;

import edu.napier.foodel.problemTemplate.FoodelVisit;




public class GHopperInterface {
	private static Journey[][] cache;
	private static String fileName;
	private static String folder;
	private static double  timeFactor = 1.6;
	private static GraphHopper hopper;
	
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

	static GraphHopper createGraphHopperInstance() {
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(folder + fileName);
        // specify where to store graphhopper files
        hopper.setGraphHopperLocation(folder+"osm/");

        // see docs/core/profiles.md to learn more about profiles
        hopper.setProfiles(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));

        // this enables speed mode for the profile we called car
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));

        // now this can take minutes if it imports or a few seconds for loading of course this is dependent on the area you import
        hopper.importOrLoad();
        return hopper;
    }

	
	public static Journey findJourney(FoodelVisit start, FoodelVisit end, String type) {
		if (hopper==null)
			 hopper = createGraphHopperInstance();

//		  // simple configuration of the request object
        GHRequest req = new GHRequest(start.getX(), start.getY(), end.getX(), end.getY()).
                // note that we have to specify which profile we are using even when there is only one like here
                        setProfile("car").
                // define the language for the turn instructions
                        setLocale(Locale.UK);
        GHResponse rsp = hopper.route(req);

        // handle errors
        if (rsp.hasErrors())
            throw new RuntimeException(rsp.getErrors().toString());

        // use the best path, see the GHResponse class for more possibilities.
        ResponsePath path = rsp.getBest();

        // points, distance in meters and time in millis of the full path
        PointList pointList = path.getPoints();
        double distance = path.getDistance();
        long timeInMs = path.getTime();
//		GHRequest request = new GHRequest(start.getX(),start.getY(),end.getX(),end.getY());//.setVehicle(type);
//	 
//		GHResponse response = hopper.route(request);
//	    if (response.hasErrors()) {
//	        throw new IllegalStateException("S= " + start + "e= " + end +". GraphHopper gave " + response.getErrors().size()
//	                + " errors. First error chained.",
//	                response.getErrors().get(0)
//	        );
//	    }
//
//	    PathWrapper pw = response.getBest();
	    Journey res = new Journey(start,end);
        res.setDistanceKM(path.getDistance()/1000);//Check
	    res.setTravelTimeMS( (long) (path.getTime()*timeFactor));//Check
	    ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
	    PointList pl = path.getPoints();
	     for (int c=0; c < pl.size();c++){
	    		points.add(new Point2D.Double(pl.getLat(c),pl.getLon(c)));
	    }
	    res.setPath(points);
	    return res;
	}

	public static void init(String folder,String fName) {
		setFileName(fName);
		setFolder(folder);
	}
	
//	private static GraphHopperOSM init() {

//		hopper = new GraphHopperOSM();
//		
//		hopper.setOSMFile(folder+"/"+fileName);
////		hopper.setCHEnabled(false); // CH does not work with shortest weighting (at the moment)
//		
//		// where to store GH files?
//		String store = folder +"/osm/";
//		File dir = new File(store);
//		if (!dir.exists()){
//			try{
//			Path path = Paths.get(store);
//			Files.createDirectories(path);
//			//dir.mkdir();
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		
//		hopper.setGraphHopperLocation(store );
//	//	hopper.setEncodingManager(new EncodingManager("car,bike,foot"));
//
//		// this may take a few minutes
//		hopper.importOrLoad();
//		return hopper;
//	}
	
	
}
