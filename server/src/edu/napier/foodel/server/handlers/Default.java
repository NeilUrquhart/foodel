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
				+ "<p><a href=\"/status\" class =\"button\" >Check the status of your machine</a> </p>");
		resp.getHeaders().add("Content-Type", "text/html");
		resp.send(200,   defaultpage.html());
		return 0;
	}
}