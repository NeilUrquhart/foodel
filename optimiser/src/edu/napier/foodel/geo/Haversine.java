package edu.napier.foodel.geo;

import java.awt.geom.Point2D;

public class Haversine {
	public static double haversine(Point2D p1, Point2D p2) {
		/* from https://rosettacode.org/wiki/Haversine_formula */
		final double R = 6372.8; // In kilometers
	    
        double dLat = Math.toRadians(p2.getX() -  p1.getX());
        double dLon = Math.toRadians(p2.getY() - p1.getY());
        double lat1 = Math.toRadians(p1.getX());
        double lat2 = Math.toRadians(p2.getX());
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
