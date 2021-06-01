package edu.napier.foodel.server.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.napier.foodel.facade.FoodelFacade;


import edu.napier.foodel.server.HTMLpage;
import edu.napier.foodel.server.Problem;
import edu.napier.foodel.server.ProblemStatus;
import edu.napier.foodel.server.ServerProperties;
import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.Context;
import net.freeutils.httpserver.HTTPServer.ContextHandler;
import net.freeutils.httpserver.HTTPServer.MultipartIterator.*;
import net.freeutils.httpserver.HTTPServer.MultipartIterator;

import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class MapHandler implements ContextHandler {

	static List<Problem> taskList;

	public MapHandler(List<Problem> taskList) {
		super();
		MapHandler.taskList = taskList;
	}

	public int serve(Request req, Response resp) throws IOException {
		var page = new HTMLpage("Foodel");

		Map<String, String> params = req.getParams();

		String key = params.get("key");
		String run = params.get("run");
		String id = params.get("id");
		
		

		if (run==null) {
			return mapError(resp, page);
		}

		var nokey = false;//Default is that a key is required
		String tmp = ServerProperties.getInstance().get("nokey");
		if(tmp!=null)
			if (tmp.contains("true"))
				nokey = true;
		
		if ((key == null) && (nokey== false)) {
			return mapError(resp, page);
		}
		
		
				
		//Find job
		Problem current = null;
		synchronized(taskList){
			
			if (nokey == false) {
				Iterator<Problem> myIterator = taskList.iterator(); 
				while(myIterator.hasNext()){ 
					Problem t = myIterator.next();
					if ((t.getKey().equals(key))) {
						current = t;
						break;
					}
				} 
			}else{//nokey = true
				Iterator<Problem> myIterator = taskList.iterator(); 
				while(myIterator.hasNext()){ 
					Problem t = myIterator.next();
					if ((t.getId().equals(id))) {
						current = t;
						break;
					}
				} 
			}
			
			
			
			if (current == null) {
				return mapError(resp, page);
			}

			if(current.getStatus().equals(ProblemStatus.SOLVED)) {
				FoodelFacade f =FoodelFacade.getInstance();
				f.setProblem(current.getProblem());
				page.addToHeader(
				"	<meta charset=\"utf-8\" />\n" + 
         		"	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + 
         		"	<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"docs/images/favicon.ico\" />\n" + 
         		"    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.css\" integrity=\"sha512-xwE/Az9zrjBIphAcBb3F6JVqxf46+CDLwfLMHloNu6KEQCAWi6HcDUbeOfBIptF7tcCzusKFjFw2yuvEpDL9wQ==\" crossorigin=\"\"/>\n" + 
         		"    <script src=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.js\" integrity=\"sha512-gZwIG9x3wUXg2hdXF6+rVkLF/0Vi9U8D2Ntg4Ga5I5BZpVkVxlJWbSQtXPSiUTtC0TjtGOmxa1AJPuV0CPthew==\" crossorigin=\"\"></script>\n");           
				page.addToBody(f.getHTMLMap(Integer.parseInt(run)));
				page.addToBody("<button onclick=\"window.print()\"  class =\"button\" >Print or save this page.</button>");
				
				resp.getHeaders().add("Content-Type", "text/html");
				resp.send(200,  page.html());
				return 0;
			}
		}
		return 0;
	}

	private int mapError(Response resp, HTMLpage page) throws IOException {
		resp.getHeaders().add("Content-Type", "text/html");
		page.addToHeader("<meta http-equiv=\"refresh\" content=\"10; URL=/status \" /> ");
		page.addToBody("<h1>Sorry, I cannot find that map</h1>");
		resp.send(200,  page.html());
		return 0;
	}
}