package edu.napier.foodel.server.handlers;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



import edu.napier.foodel.server.HTMLpage;
import edu.napier.foodel.server.Task;
import edu.napier.foodel.server.TaskStatus;
import net.freeutils.httpserver.HTTPServer.ContextHandler;


import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class ServerStatus implements ContextHandler {

	static List<Task> taskList;

	public ServerStatus(List<Task> taskList) {
		super();
		ServerStatus.taskList = taskList;
	}

	public int serve(Request req, Response resp) throws IOException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss  dd MMMM YYYY");
		
		var page = new HTMLpage("Foodel -  status");
		page.addToHeader("<meta http-equiv=\"refresh\" content=\"10\">\n");
		
		if (taskList.size() == 0) {
			page.addToBody("<p>You have not uploaded any task so far in this session.</p>");

		}else {
			
			
			synchronized(taskList){ 
				page.addToBody("<h3>Solved:</h3>");
	
				Iterator<Task> myIterator = taskList.iterator(); 
				page.addToBody("<ul>");
				String removal = "";
				while(myIterator.hasNext()){ 
					Task t = myIterator.next();
					removal = simpleDateFormat.format(new Date(t.getRemovalTime()));
					if (t.getStatus() == TaskStatus.SOLVED) {
						page.addToBody("<li><b>" +t.getId() +"</b>"+
						"<br> <a class=\"button\" href=\"/job?id="+t.getId()+" \"> View result </a>" +
								" (To be removed at:"+removal+")</li><br>");
								//" To be removed at "+removal+ 
						//<a href="/job?id="+t.getId()
					}
				} 
				page.addToBody("</ul>");
				
				page.addToBody("<h3>Currently being solved:</h3>");
				myIterator = taskList.iterator(); 
				page.addToBody("<ul>");
				while(myIterator.hasNext()){ 
					 Task t = myIterator.next();
					removal = simpleDateFormat.format(new Date(t.getRemovalTime()));
					 if (t.getStatus() == TaskStatus.RUNNING) {
						page.addToBody("<li><b>" +t.getId() +  "</b></li>");
					}
					
				} 
				page.addToBody("</ul>");
				
				page.addToBody("<h3>Waiting to be solved:</h3>");
				myIterator = taskList.iterator(); 
				page.addToBody("<ul>");
				while(myIterator.hasNext()){ 
					Task t = myIterator.next();
					 
					if (t.getStatus() == TaskStatus.WAITING) {
						page.addToBody("<li>" +t.getId() + " </li>");
					}
				} 
				page.addToBody("</ul>");
			}

		}

		resp.getHeaders().add("Content-Type", "text/html");
		resp.send(200,   page.html());
		return 0;
	}
}