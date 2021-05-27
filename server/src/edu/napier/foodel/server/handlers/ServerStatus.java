package edu.napier.foodel.server.handlers;


import java.io.IOException;

import java.util.Iterator;
import java.util.List;



import edu.napier.foodel.server.HTMLpage;
import edu.napier.foodel.server.Problem;

import net.freeutils.httpserver.HTTPServer.ContextHandler;


import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class ServerStatus implements ContextHandler {

	static List<Problem> taskList;

	public ServerStatus(List<Problem> taskList) {
		super();
		ServerStatus.taskList = taskList;
	}

	public int serve(Request req, Response resp) throws IOException {
		var page = new HTMLpage("Foodel - server status");
		page.addToHeader("<meta http-equiv=\"refresh\" content=\"10\">\n");
		page.addToBody("<H1>Jobs currently on the server : </H1>");
		synchronized(taskList){ 
			Iterator<Problem> myIterator = taskList.iterator(); 
			while(myIterator.hasNext()){ 
				Problem t = myIterator.next();
				
				page.addToBody("<a href=\"/job?id="+t.getId()+" \">" +t.getId() + ":"+ t.getStatus()  +"</a><br>");
			} 
		}
		resp.getHeaders().add("Content-Type", "text/html");
		resp.send(200,   page.html());
		return 0;
	}
}