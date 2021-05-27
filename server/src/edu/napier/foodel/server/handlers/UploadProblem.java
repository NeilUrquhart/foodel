package edu.napier.foodel.server.handlers;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import edu.napier.foodel.ioutils.ProblemStreamParser;
import edu.napier.foodel.problem.FoodelProblem;
import edu.napier.foodel.server.HTMLpage;
import edu.napier.foodel.server.Problem;
import edu.napier.foodel.server.ProblemStatus;
import edu.napier.foodel.server.ServerProperties;
import net.freeutils.httpserver.HTTPServer.Context;
import net.freeutils.httpserver.HTTPServer.MultipartIterator.*;
import net.freeutils.httpserver.HTTPServer.MultipartIterator;

import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class UploadProblem {

	static List<Problem> taskList;

	public static void setTaskList(List<Problem> taskList) {
		UploadProblem.taskList = taskList;
	}

	@Context("/new")
	public int serveForm(Request req, Response resp) throws IOException {
		var page = new HTMLpage("Upload Problem");

		page.addToBody("<H1>Upload a new problem for Foodel to solve:</H1>");
		page.addToBody("<H3>Your problem should be saved as a .CSV file</H3>");
		page.addToBody("<form action=\"\\upload\" method=\"post\" enctype=\"multipart/form-data\"> \n"
				+ "<!-- <input type=\"file\" name=\"fileToUpload\" id=\"fileToUpload\"><br> -->\n"
				+ "<div class=\"drop-zone\">\n"
				+ "<span class=\"drop-zone__prompt\">Drop file here or click to upload</span>\n"
				+ "<input type=\"file\" name=\"fileToUpload\" id=\"fileToUpload\" class=\"drop-zone__input\">\n"
				+ "</div>\n"
				+ "<BR><BR>"
				+ "<input type=\"submit\" value=\"Upload Problem\" name=\"submit\">\n"
				+ "</form>\n"
				+ "<script src=\"/static/dragdrop.js\"></script>");
		
		page.addToBody("<H3>Most spreadhseets (such as MS Excel) will allow you to edit CSV files.</H3>");
		resp.getHeaders().add("Content-Type", "text/html");
		resp.send(200, page.html());
		return 0;
	}

	@Context(value="/upload", methods={"GET", "POST"})
	public int serveUpload(Request request, Response resp) throws IOException {
		var page = new HTMLpage("New Foodel problem:");
		
		String filename;
		var newTask = new Problem();
		Iterator<Part> it = new MultipartIterator(request);
		while (it.hasNext()) {
			var part = it.next();
			filename = part.getFilename();
			if (filename !=null) {
				try {
					if (!filename.endsWith(".csv")) {
						throw new Exception("Your problem file must be saved as a .CSV");
					}
					
					//Check file can be parsed + extract id
					var p = new FoodelProblem();
					p.setReference(filename);
					p= ProblemStreamParser.buildProblem(part.getBody(), p);	
					newTask.setProblem(p);
					//set new task
					newTask.setInputFile(filename);
					newTask.setStatus(ProblemStatus.WAITING);
					//newTask.setId(p.getReference());

				} catch(Exception e) {
					e.printStackTrace();
					resp.getHeaders().add("Content-Type", "text/html");
					page.addToBody("<h2>It looks like something has gone wrong when your problem file was being  read...</h2>");
					page.addToBody("<br> The following error was generated : <br>"+e.getMessage() );
					page.addToBody("<br> You can ... <a href=\"new \"> try and upload again </a> or  <a href=\"mailto:n.urquhart@napier.ac.uk\">email for help</a> <br>");				
					resp.send(200, page.html());
					return 0;
				}
			}
		}
		synchronized(taskList){ 
			taskList.add(newTask);
		}
		resp.getHeaders().add("Content-Type", "text/html");
		page.addToBody("<h2>Your CSV file appears to have been read correctly.</h2> ");
		page.addToBody("<h3>Your problem reference is  : "+newTask.getId()+ "<br></h3>");			

		String nokey = ServerProperties.getInstance().get("nokey");
		if (nokey != null)
			if (!nokey.contains("true")) {
				page.addToBody("<h3> Key: "+ newTask.getKey() +" <br>You might wish to make a note of the key as you may need it later to access your results.</h3>");
			}
		page.addToBody("<H3><a href= /job?id="+newTask.getId()+"&key="+newTask.getKey() +"> Click to continue </a></h3> ");
		resp.send(200,page.html());
		return 0;
	}

	//	
	//
	//	private String getIndex(String key,String id) throws Exception{
	//		String fName = "./"+key+"/summary.csv";
	//		List<String>  tmp= Files.readAllLines(Paths.get(fName));
	//		String html =  "<!DOCTYPE html>\n" + 
	//				"<html>\n" + 
	//				"<body>\n" + 
	//				"\n"  ;
	//		for (String line : tmp)
	//		{
	//			if (line.startsWith("Run")) {
	//				int run = Integer.parseInt(line.split(" ")[1]);
	//				line = "<h1><a href=\"http://"+Application.host()+"/result?id="+id+"&key="+key+"&run="+run+"\">"+line+"</a></h1>";
	//				//
	//			}
	//			html += line +"<br>";
	//			System.out.println(line);
	//		}
	//
	//		html +=  "</body>\n" + 
	//				"</html>";
	//		return html;
	//	}
}