package edu.napier.foodel.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.napier.foodel.facade.FoodelSolver;
import edu.napier.foodel.problem.cvrp.CVRPProblem;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.problemTemplate.FoodelProblemFactory;
import edu.napier.foodel.server.HTMLpage;
import edu.napier.foodel.server.ServerProperties;
import edu.napier.foodel.server.Task;
import edu.napier.foodel.server.TaskStatus;
import net.freeutils.httpserver.HTTPServer.Context;
import net.freeutils.httpserver.HTTPServer.MultipartIterator.*;
import net.freeutils.httpserver.HTTPServer.MultipartIterator;

import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class UploadProblem {

	static List<Task> taskList;

	public static void setTaskList(List<Task> taskList) {
		UploadProblem.taskList = taskList;
	}

	@Context("/new")
	public int serveForm(Request req, Response resp) throws IOException {
		var page = new HTMLpage("Submit Task");
		page.addToHeader("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		
		
		page.addToBody(  "<div class=\"container\">\n "+
		"<H1>Submit a new task </H1>");
		
		String dragDrop = "<form action=\"\\upload\" method=\"post\" enctype=\"multipart/form-data\"> \n"
				+ "<!-- <input type=\"file\" name=\"fileToUpload\" id=\"fileToUpload\"><br> -->\n"
				+ "<div class=\"drop-zone\">\n"
				+ "<span class=\"drop-zone__prompt\">Drop your CSV file here or click to upload</span>\n"
				+ "<input type=\"file\" name=\"fileToUpload\" id=\"fileToUpload\" class=\"drop-zone__input\">\n"
				+ "</div>\n"
				+ "<BR><BR>"
				+ "<input type=\"submit\" value=\"Solve my task\" name=\"submit\"  class =\"button\">\n"
				+ "</form>\n"
				+ "<script src=\"/static/dragdrop.js\"></script> ";
		
				
		page.addToBody(dragDrop);
		page.addFileToBody("taskformats");
		
		page.addToBody("</div>");
		resp.getHeaders().add("Content-Type", "text/html");
		resp.send(200, page.html());
		return 0;
	}

	@Context(value="/upload", methods={"GET","POST"})
	public int serveUpload(Request request, Response resp) throws IOException {
		
		var page = new HTMLpage("New Foodel problem:");
		page.addToBody("<div class=\"container\">");
		String filename;
		
		var newTask = new Task();
		Iterator<Part> it=null;
		try {
			it = new MultipartIterator(request);
		}catch(Exception e) {//If the MultipartIterator call fails then we can assume that the URL has been
			resp.getHeaders().add("Content-Type", "text/html");
			page.addToHeader("<meta http-equiv=\"refresh\" content=\"0; URL=/new \" />");
			page.addToBody("</div>");
			resp.send(200, page.html());
		}
		while (it.hasNext()) {
			var part = it.next();
			filename = part.getFilename();
			if (filename !=null) {
				try {
					if (!filename.endsWith(".csv")) {
						throw new Exception("Your task should be saved as a .CSV file.");
					}
					newTask.setInputFile(filename);
					//Check file can be parsed + extract id
					
					if (idInUse(filename)) {
						int c=1;
						while(idInUse(filename + "-"+c))
								c++;
						
						filename = filename +"-"+c;
					}
					newTask.setId(filename);
					newTask.setRawData(readStream(part.getBody()));
					newTask.setStatus(TaskStatus.WAITING);
				
				} catch(Exception e) {
					e.printStackTrace();
					resp.getHeaders().add("Content-Type", "text/html");
					page.addToBody("<div class=\"container\">\n "
							+ "<h3>Foodel appears to have found a problem in your task file.</h3>");
					
					page.addToBody("        <section>"
							+ "<div class=\"card\">\n"
							+ "<div class=\"card-body\">\n"
							+ "<div class=\"card-title\"> The message received from Foodel is: </div>\n"
							+ "<p>"+e.getMessage() +"</p>\n</div> </div> </section>\n");
					
					page.addToBody("<h3> Would you like to:  <br> <ul>"
							+ "<li><a href=\"new \"> submit another task?</a> </li>"
							+ "<li><a href=\"mailto:n.urquhart@napier.ac.uk\">email for help?</a> </li>"
							+ "</ul></h3></div>");	
					page.addToBody("</div>");
					resp.send(200, page.html());
					return 0;
				}
			}
		}
		synchronized(taskList){ 
			taskList.add(newTask);
		}
		resp.getHeaders().add("Content-Type", "text/html");
		page.addToBody("<h3>Your task file has been understood and added to the list of problems to be solved.</h3> ");
		page.addToBody("<h3> Your task reference is  :<br></h3> "+newTask.getId());			

		String nokey = ServerProperties.getInstance().get("nokey");
		if (nokey != null)
			if (!nokey.contains("true")) {
				page.addToBody("<h3> Your task has been allocated the unique key:</h3>"+ newTask.getKey() 
				+" <button title=\"Copy key\"  alt = \"Copy key \" onclick=\"toClipboard()\" >\n"
				+"<img src=\" \\static\\copyico.svg \" height =\"15\"  /> \n"
				+ "  </button> </h3>");
				
				page.addToBody("<p>Please copy and paste this key somewhere safe, you will need it to access your results. "
						+ "Do not share this key with anyone else, unless you wish them to be able access your"
						+ "results.</p>");
			}
		page.addToBody("<H3><a href= /job?id="+newTask.getId()+"&key="+newTask.getKey() +" class =\"button\" > Continue </a></h3> ");
		
		page.addToBody("\n <script>\n"
				+ "function toClipboard() {\n"
				+ "  navigator.clipboard.writeText(\""+newTask.getKey()+ "\");\n"
				+ "}\n"
				+ "</script>");
		page.addToBody("</div>");
		resp.send(200,page.html());
		return 0;
	}

	private HashMap<String, String[]> readStream(InputStream problemStream) throws Exception {
		HashMap<String,String[]> res = new HashMap<String,String[]>();
		
		BufferedReader b;
		try {
			InputStreamReader isr = new InputStreamReader(problemStream,StandardCharsets.UTF_8);
			b = new BufferedReader(isr); 
		}catch(Exception e) {
			throw new Exception("Can't open file:");
		}

		String readLine = "";
	
		while ((readLine = b.readLine()) != null) {
			readLine =readLine.trim();
			readLine =readLine.toLowerCase();
			String[] data = split(readLine);//readLine.split(",");
			if (data.length >= 2) {
				String key = data[0];
				int count = 0;
				while (res.containsKey(key+" "+count)) {
					count++;
				}
				key = key +" " + count;
				res.put(key, data);
			}
		}
		b.close();
		
		return res;
	}
	
	private String[] split(String csv) {
		//Split line by , - ignore any within quotes
		ArrayList<String> parsed = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();
		boolean inQuotes =false;
		
		for(char c : csv.toCharArray()) {
			if (c=='"')
				inQuotes = !inQuotes;
			
			if (c==',') {
				if (!inQuotes) {
					parsed.add(buffer.toString());
					buffer = new StringBuilder();
					c=' ';
				}
			}
			buffer.append(c);
		
		}
		parsed.add(buffer.toString());
		
		String[] res = new String[parsed.size()];
		int c=0;
		for (String item : parsed) {
			res[c] = item.strip().toLowerCase();
			c++;
		}
		return res;
	}
	
	private boolean idInUse(String id) {
		for (Task t : taskList) {
			if (t.getId().equals(id))
				return true;
		}
		return false;
	}

}