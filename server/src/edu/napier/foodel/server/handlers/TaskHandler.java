package edu.napier.foodel.server.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import edu.napier.foodel.facade.FoodelSolver;

import edu.napier.foodel.server.HTMLpage;
import edu.napier.foodel.server.Task;
import edu.napier.foodel.server.TaskStatus;
import edu.napier.foodel.server.ServerProperties;
import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.Context;
import net.freeutils.httpserver.HTTPServer.ContextHandler;
import net.freeutils.httpserver.HTTPServer.MultipartIterator.*;
import net.freeutils.httpserver.HTTPServer.MultipartIterator;

import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class TaskHandler implements ContextHandler {



	static List<Task> taskList;

	public TaskHandler(List<Task> taskList) {
		super();
		TaskHandler.taskList = taskList;
	}


	public int serve(Request req, Response resp) throws IOException {
		var page = new HTMLpage("Foodel");
		page.addToBody("<div class=\"container\">");
		Map<String, String> params = req.getParams();

		String key = params.get("key");
		String id = params.get("id");
		var nokey = false;//Default is that a key is required
		String tmp = ServerProperties.getInstance().get("nokey");
		if(tmp!=null)
			if (tmp.contains("true"))
				nokey = true;


		if ((key==null)&&(!nokey)) {
			return noKeyError(resp, page);
		}
		synchronized(taskList){ 
			Task current = null;
			if (nokey) {//find job by id
				Iterator<Task> myIterator = taskList.iterator(); 
				while(myIterator.hasNext()){ 
					Task t = myIterator.next();
					if ((t.getId().equals(id))) {
						current = t;
						break;
					}
				} 

				if (current == null) {
					return noIdError(resp, page);
				}
			}else {
				//Find job by key
				

				Iterator<Task> myIterator = taskList.iterator(); 
				while(myIterator.hasNext()){ 
					Task t = myIterator.next();
					if ((t.getKey().equals(key))) {
						current = t;
						break;
					}
				} 

				if (current == null) {
					return noKeyError(resp, page);
				}

			}
			if (!current.getStatus().equals(TaskStatus.SOLVED)) {
				page.addToHeader("  <meta http-equiv=\"refresh\" content=\"3\">\n");

			}

			page.addToBody("<body> <h3>Problem: "+current.getId()+"</h3>");
			if (current.getStatus().equals(TaskStatus.WAITING))	{
				page.addToBody("<p>Your problem is currently in a queue waiting to be solved.</p>");
			}
			
			if (current.getStatus().equals(TaskStatus.RUNNING))	{
				page.addToBody("<p> Your problem is being solved at the moment. Please be patient, your result will be shown here in a few minutes.</p>");
			}
			
			if (current.getStatus().equals(TaskStatus.BROKEN))	{
				page.addToBody("<p> Something has gone wrong. The message below may help explain the issue </p>"
						+ "<br>" + current.getErrMsg() + "<br>");
			}
			
			if (!current.getStatus().equals(TaskStatus.SOLVED))	{
				resp.getHeaders().add("Content-Type", "text/html");
				resp.send(200,  page.html());
				return 0;
			}

			if(current.getStatus().equals(TaskStatus.SOLVED)) {
				var f  =FoodelSolver.getInstance();
				f.setProblem(current.getProblem());
				page.addToBody(f.getResultHTML(key));
				resp.getHeaders().add("Content-Type", "text/html");
				page.addToBody("</div>");
				resp.send(200,page.html());
				return 0;
			}

		}

		return 0;
	}


	private int noKeyError(Response resp, HTMLpage page) throws IOException {
		resp.getHeaders().add("Content-Type", "text/html");	
		page.addToBody("<h3>Please enter the key to view your task</h3>\n"
				+ "Task Key: <input type=\"text\" id=\"key\" value=\"xxx\">\n"
				+ "<button onclick=\"myFunction()\">Find task </button>\n"
				+ "\n"
				+ "<script>\n"
				+ "function myFunction() {\n"
				+ "var key;"
				+ "  key = document.getElementById(\"key\").value;\n"
				+ " \n"
				+ "  window.location.href = '/job?&key=' + key ;\n"
				+ " \n"
				+ "}\n"
				+ "</script>");
		resp.send(200,  page.html());
		return 0;
	}
	
	private int noIdError(Response resp, HTMLpage page) throws IOException {
		resp.getHeaders().add("Content-Type", "text/html");	
		page.addToBody("<h3>Sorry, I cannot find that task.</h3>");
		resp.send(200,  page.html());
		return 0;
	}
}