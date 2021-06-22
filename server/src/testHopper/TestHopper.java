package testHopper;

import java.awt.geom.Point2D;

import edu.napier.foodel.geo.GHopperInterface;
import edu.napier.foodel.geo.Journey;
import edu.napier.foodel.geo.NominatimGeocoder;
import edu.napier.foodel.problemTemplate.FoodelVisit;

public class TestHopper {


public static void main(String[] args) {
	/*
	 * A main method used to directly test/debug GraphHopper
	 */
	NominatimGeocoder gc = new NominatimGeocoder();
	
	Point2D loc = gc.find("6 Greenbank Park, Edinburgh");
	FoodelVisit v1 = new FoodelVisit("Visit 1", "GBP", "",loc.getX(), loc.getY(), 0);
	
	loc = gc.find("1 Eskbank Terrace, Dalkeith");
	FoodelVisit v2 = new FoodelVisit("Visit 2", "GBP", "",loc.getX(), loc.getY(), 0);
	GHopperInterface.setFolder("./data/");
	Journey j = GHopperInterface.findJourney(v1, v2, "car");
	
}
}