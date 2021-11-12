package edu.napier.foodel.server.handlers;

import edu.napier.foodel.server.HTMLpage;
import net.freeutils.httpserver.HTTPServer.ContextHandler;
import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Home implements ContextHandler {
   /*
    * Generate the home page
    */
    @Override
    public int serve(Request req, Response res) throws IOException {

    	HTMLpage page = new HTMLpage("Foodel");

        // Add specific styling for the home page
        page.addToHeader("<link rel='stylesheet' href='/static/home.css'>");

        //Try to find content that matches the uri and add it to the page
        // if it failes (ie an invalid uri -  then default to home)
        if (!page.addFileToBody(req.getURI().toString())) {
        	page.addFileToBody("/home");
        }
        res.getHeaders().add("Content-Type", "text/html");
        res.send(200, page.html());

        return 0;
    }

}
