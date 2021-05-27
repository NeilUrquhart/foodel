package edu.napier.foodel.server.handlers;


import java.io.IOException;

import edu.napier.foodel.server.HTMLpage;
import net.freeutils.httpserver.HTTPServer.ContextHandler;


import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class Default implements ContextHandler {

	
	public int serve(Request req, Response resp) throws IOException {
		var defaultpage = new HTMLpage("Foodel Server ");
		defaultpage.addToBody(" <h1>Welcome to Foodel</h1><br>"
				+ "Foodel is a simple application that allows you to sort deliveries into efficient delivery routes."
				+ " <br>"
				+ "Foodel was developed by Dr Neil Urquhart at Edinburgh Napier University. <br>"
				+ "Please feel free to make use of Foodel and let us know of your expieriances with it.<br>"
				+ "<br>"
				+ " <a href=\"/new\">Click here to upload a new problem </a> <br>"
				+ "<a href=\"/status\">Click here to see what problems are being solved on your machine.</a> <br>");
		resp.getHeaders().add("Content-Type", "text/html");
		resp.send(200,   defaultpage.html());
		return 0;
	}
}