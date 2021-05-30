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

public class Job implements ContextHandler {

	//	/*
	//	 * /*
	//		 * 
	//		 * Test
	//		 */
	//		Gson gson = new Gson();
	//
	//        try (Reader reader = new FileReader("./gson/test.json")) {
	//
	//            // Convert JSON File to Java Object
	//            Problem p  = gson.fromJson(reader, Problem.class);
	//            
	//            taskList.add(p);
	//
	//        } catch (IOException e) {
	//            e.printStackTrace();
	//        }
	//		/*
	//		 * 
	//		 * Done test
	//		 */


	static List<Problem> taskList;

	public Job(List<Problem> taskList) {
		super();
		Job.taskList = taskList;
	}


	public int serve(Request req, Response resp) throws IOException {
		var page = new HTMLpage("Foodel");
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
			Problem current = null;
			if (nokey) {//find job by id
				Iterator<Problem> myIterator = taskList.iterator(); 
				while(myIterator.hasNext()){ 
					Problem t = myIterator.next();
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
				

				Iterator<Problem> myIterator = taskList.iterator(); 
				while(myIterator.hasNext()){ 
					Problem t = myIterator.next();
					if ((t.getKey().equals(key))) {
						current = t;
						break;
					}
				} 

				if (current == null) {
					return noKeyError(resp, page);
				}

			}
			if (!current.getStatus().equals(ProblemStatus.SOLVED)) {
				page.addToHeader("  <meta http-equiv=\"refresh\" content=\"5\">\n");

			}

			page.addToBody("<body> <h1>Problem task: "+current.getId()+"</h1>");
			if (current.getStatus().equals(ProblemStatus.WAITING))	{
				page.addToBody("<h2>Your problem is currently in a queue waiting to be solved.</h2>");
			}
			
			if (current.getStatus().equals(ProblemStatus.RUNNING))	{
				page.addToBody("<h2>Your problem is being solved right now! Please wait...</h2>");
			}
			
			if (!current.getStatus().equals(ProblemStatus.SOLVED))	{
				resp.getHeaders().add("Content-Type", "text/html");
				resp.send(200,  page.html());
				return 0;
			}

			if(current.getStatus().equals(ProblemStatus.SOLVED)) {
				var f  =FoodelFacade.getInstance();
				f.setProblem(current.getProblem());
				page.addToBody(f.getResultHTML(key));
				resp.getHeaders().add("Content-Type", "text/html");
				resp.send(200,page.html());
				return 0;
			}

		}

		return 0;
	}


	private int noKeyError(Response resp, HTMLpage page) throws IOException {
		resp.getHeaders().add("Content-Type", "text/html");	
		page.addToBody("<h1>Please enter the problem key to view your problem</h1>\n"
				+ "Problem Key: <input type=\"text\" id=\"key\" value=\"xxx\">\n"
				+ "<button onclick=\"myFunction()\">Try it</button>\n"
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
		page.addToBody("<h1>Sorry, I cannot find that job.</h1>");
		resp.send(200,  page.html());
		return 0;
	}
}