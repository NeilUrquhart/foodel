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
		var page = new HTMLpage("Foodel -  status");
		page.addToHeader("<meta http-equiv=\"refresh\" content=\"10\">\n");
		page.addToBody("<H1>Foodel Status </H1>");
		page.addToBody("<p>Foodel is running on your computer.</p>");

		if (taskList.size() == 0) {
			page.addToBody("<p>You have not uploaded any problems so far in this session.</p>");

		}else {
			page.addToBody("<p> Here are the problems that you have loaded so far in this session: </p>");

		synchronized(taskList){ 
			Iterator<Problem> myIterator = taskList.iterator(); 
			while(myIterator.hasNext()){ 
				Problem t = myIterator.next();

				page.addToBody("<a href=\"/job?id="+t.getId()+" \">" +t.getId() + ":"+ t.getStatus()  +"</a><br>");
			} 
		}

		page.addToBody("<p>Click on a problem to look at the solution.</p>");
		}

		resp.getHeaders().add("Content-Type", "text/html");
		resp.send(200,   page.html());
		return 0;
	}
}