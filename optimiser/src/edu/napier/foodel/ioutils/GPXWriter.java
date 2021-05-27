package edu.napier.foodel.ioutils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class GPXWriter {
	private  String segments = "";
	private String waypoints ="";


	public  void addPath(ArrayList<Double> lat, ArrayList<Double>lon) {

		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		for (int c=0; c< lat.size();c++) {
			segments += "<trkpt lat=\"" + lat.get(c) + "\" lon=\"" + lon.get(c) + "\">"/*<time>" + df.format(new Date(l.getTime())) + "</time>*/+"</trkpt>\n";
		}
	}

	public void addWayPoint(double lat, double lon, String name) {
		waypoints +=
				"<wpt lat=\""+lat+" \" lon=\" "+lon+"\">"/*<ele>0</ele>*/+" <name>"+name+ "</name></wpt>";
	}

	public  void write(String fileName, String n) {
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n";
		String name = "<name>" + n + "</name>\n";

		String footer = "</trkseg></trk></gpx>";
		try {
			File file = new File( fileName);
			FileWriter writer = new FileWriter(file, false);
			writer.append(header);
			writer.append(name);

			writer.append(waypoints);
			writer.append("<trk><trkseg>");
			writer.append(segments);
			writer.append(footer);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public  String getText(String desc) {
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n";
		String name = "<name>" + desc + "</name>\n";

		String footer = "</trkseg></trk></gpx>";
		return header +
			name +
			waypoints+
			"<trk><trkseg>"+
			segments+
			footer;
	}
}
