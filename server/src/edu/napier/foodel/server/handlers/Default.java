package edu.napier.foodel.server.handlers;


import java.io.IOException;

import edu.napier.foodel.server.HTMLpage;
import net.freeutils.httpserver.HTTPServer.ContextHandler;


import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class Default implements ContextHandler {

	
	public int serve(Request req, Response resp) throws IOException {
		var defaultpage = new HTMLpage("Foodel Server ");
		defaultpage.addToBody(" <h1>Welcome</h1><br>"
				+ "<p>Foodel is a simple application that allows you to sort deliveries into efficient delivery routes."
				
				+ "Foodel was developed by Dr Neil Urquhart at Edinburgh Napier University."
				+ "Please feel free to make use of Foodel and let us know of your expieriences with it.</p>"
				+ "<br>"
				
				+ "<p> <a href=\"/new\" class =\"button\">Upload a new problem </a> </p>"
				+ "<p><a href=\"/status\" class =\"button\" >Check the status of your machine</a> </p>"
				+ "<p>&nbsp;</p>"
				+ "<p><strong>Simple guidance to uploading a new problem in Foodel: (\"<span style=\"color: #339966;\">Upload a new problem</span>\")</strong></p>\n"
				+"<ol>"
				+ "<li>Click on the button \"<span style=\"color: #339966;\"><strong>Upload a new problem</strong><span style=\"color: #000000;\">\"</span></span></li>"
				+"<li><span style=\"color: #339966;\"><span style=\"color: #000000;\">In order to upload your file you can either:</span></span>"
				+ "<ul>" 
				+ "<li><span style=\"color: #339966;\"><span style=\"color: #000000;\"> Click on the box that says \"<strong><span style=\"color: #339966;\">Drop your CSV File here or click to upload</span></strong>\" and choose the file which you would like to use</span></span></li>"
				+ "<li><span style=\"color: #339966;\"><span style=\"color: #000000;\">Drop the Excel File which you would like to use in the box</span></span></li>"
				+ "</ul>"
				+ "</li>"
				+ "<li><span style=\"color: #339966;\"><span style=\"color: #000000;\">If uploading the file is successful you will see that the \"Drop box\" is filled and you can see the name of your file</span></span></li>"
				+ "<li><span style=\"color: #339966;\"><span style=\"color: #000000;\">Click on the button \"<span style=\"color: #339966;\"><strong>Solve my problem</strong></span>\"</span></span></li>"
				+ "<li><span style=\"color: #339966;\"><span style=\"color: #000000;\">Wait a few seconds until you see \"Your file appears to have been understood\" - that means your problem is currently being solved</span></span></li>"
				+ "<li><span style=\"color: #339966;\"><span style=\"color: #000000;\">Click the button \"<strong><span style=\"color: #339966;\">Continue</span></strong>\"</span></span></li>"
				+ "<li><span style=\"color: #339966;\"><span style=\"color: #000000;\">You may see \"<strong>Your problem is being solved at the moment</strong>\" and it may take a while for the problem to be solved&nbsp;</span></span></li>"
				+ "<li><span style=\"color: #339966;\"><span style=\"color: #000000;\">You should be able to see the solution for the provided .csv file and you can click:</span></span>"
				+ "<ul>"
				+ "<li>\"<span style=\"color: #339966;\"><strong>View Map</strong></span>\" - which will show you the exact route&nbsp;"
				+ "<ul>"
				+ "<li>You will be able to \"<strong><span style=\"color: #339966;\">Print or save this page</span></strong>\" which shows the route on the map and the schedule\n"
				+ "<ul style=\"list-style-type: disc;\">"
				+ "<li>If you would like to \"<span style=\"color: #000000;\"><strong>Print</strong></span>\" the page on the dropbox below \"<strong><span style=\"color: #339966;\">Printer</span></strong>\" you would have to choose a printer</li>\n"
				+ "<li>If you would like to save the page on the dropbox below \"Printer\" you would have to choose \"<span style=\"color: #800080;\"><strong>Save as PDF</strong></span>\"</li>"
				+ "</ul>\n"
				+ "</li>\n"
				+ "</ul>\n"
				+ "</li>"
				+ "<li>\"<span style=\"color: #339966;\"><strong>GPX File</strong></span>\" - this button will prompt you to save the file in .gpx type in a location you prefer.</li>\n"
				+ "</ul>\n"
				+ "</li>\n"
				+ "</ol>\n"
				+ "<p>&nbsp;</p>"
				+ "<p><strong>Simple guidance to view uploaded problems in Foodel: (\"<span style=\"color: #339966;\">Check the status of your machine</span>\")<br /></strong></p>\n"
				+ "<ol>\n"
				+ "<li>Click on the button \"<strong><span style=\"color: #339966;\">Check the status of your machine</span></strong>\"</li>\n"
				+ "<li>If you have any uploaded problems currently being solved or solved already they will show up on the<strong> Foodel Status</strong> page\n"
				+ "<ul style=\"list-style-type: circle;\">\n"
				+ "<li>If you have a problem solved (it will have \"<span style=\"color: #3366ff;\"><strong>:solved</strong></span>\") you will be able to <span style=\"color: #000000;\">click on</span> the file name and open it<br />\n"
				+ "<ul style=\"list-style-type: square;\">\n"
				+ "<li><span style=\"color: #339966;\"><span style=\"color: #000000;\">You should be able to see the solution for the provided .csv file and you can click:</span></span>\n"
				+ "<ul style=\"list-style-type: disc;\">\n"
				+ "<li>\"<span style=\"color: #339966;\"><strong>View Map</strong></span>\" - which will show you the exact route&nbsp;\n"
				+ "<ul>\n"
				+ "<li>You will be able to \"<strong><span style=\"color: #339966;\">Print or save this page</span></strong>\" which shows the route on the map and the schedule\n"
				+ "<ul>\n"
				+ "<li>If you would like to \"<span style=\"color: #000000;\"><strong>Print</strong></span>\" the page on the dropbox below \"<strong><span style=\"color: #339966;\">Printer</span></strong>\" you would have to choose a printer</li>\n"
				+ "<li>If you would like to save the page on the dropbox below \"Printer\" you would have to choose \"<span style=\"color: #800080;\"><strong>Save as PDF</strong></span>\"</li>\n"
				+ "</ul>\n"
				+ "</li>\n"
				+ "</ul>\n"
				+ "</li>\n"
				+ "<li>\"<span style=\"color: #339966;\"><strong>GPX File</strong></span>\" - this button will prompt you to save the file in .gpx type in a location you prefer.</li>\n"
				+ "</ul>\n"
				+ "</li>\n"
				+ "</ul>\n"
				+ "</li>\n"
				+ "<li>If you have a problem that is still running (it will have \"<strong><span style=\"color: #3366ff;\">:running</span></strong>\")\n"
				+ "<ul style=\"list-style-type: square;\">\n"
				+ "<li>you would be able to click on it and wait until it is solved&nbsp;</li>\n"
				+ "<li>the page will update to \"<span style=\"color: #3366ff;\"><strong>:solved</strong></span>\" automatically without needing to refresh the page</li>\n"
				+ "</ul>\n"
				+ "</li>\n"
				+ "</ul>\n"
				+ "</li>\n"
				+ "</ol>");
		

		resp.getHeaders().add("Content-Type", "text/html");
		resp.send(200,   defaultpage.html());
		return 0;
	}
}
