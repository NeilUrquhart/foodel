package edu.napier.foodel.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.napier.foodel.facade.FoodelSolver;
import edu.napier.foodel.problem.cvrp.CVRPProblem;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.problemTemplate.FoodelProblemFactory;
import edu.napier.foodel.server.HTMLpage;
import edu.napier.foodel.server.Task;
import edu.napier.foodel.server.TaskStatus;
import edu.napier.foodel.server.ServerProperties;
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
		var page = new HTMLpage("Upload Problem");

		page.addToBody("<H1>Solve a new problem.</H1>");
		page.addToBody("<form action=\"\\upload\" method=\"post\" enctype=\"multipart/form-data\"> \n"
				+ "<!-- <input type=\"file\" name=\"fileToUpload\" id=\"fileToUpload\"><br> -->\n"
				+ "<div class=\"drop-zone\">\n"
				+ "<span class=\"drop-zone__prompt\">Drop your CSV file here or click to upload</span>\n"
				+ "<input type=\"file\" name=\"fileToUpload\" id=\"fileToUpload\" class=\"drop-zone__input\">\n"
				+ "</div>\n"
				+ "<BR><BR>"
				+ "<input type=\"submit\" value=\"Solve my problem\" name=\"submit\"  class =\"button\">\n"
				+ "</form>\n"
				+ "<script src=\"/static/dragdrop.js\"></script>");
		
		page.addToBody("<p>Your problem should be saved as a .CSV file. ");
		page.addToBody("Most spreadhseets (such as MS Excel) will allow you to edit CSV files.</p>");
		resp.getHeaders().add("Content-Type", "text/html");
		resp.send(200, page.html());
		return 0;
	}

	@Context(value="/upload", methods={"GET", "POST"})
	public int serveUpload(Request request, Response resp) throws IOException {
		var page = new HTMLpage("New Foodel problem:");
		
		String filename;
		var newTask = new Task();
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
					
					if (idInUse(filename)) {
						int c=1;
						while(idInUse(filename + "-"+c))
								c++;
						
						filename = filename +"-"+c;
					}
					HashMap<String,String[]> csvData = readStream(part.getBody());
					
					FoodelProblem p= FoodelSolver.getInstance().newProblem(csvData,filename);//.buildProblem(part.getBody(), filename);	
					newTask.setProblem(p);
					//set new task
					newTask.setInputFile(filename);
					newTask.setStatus(TaskStatus.WAITING);
					//newTask.setId(p.getReference());

				} catch(Exception e) {
					e.printStackTrace();
					resp.getHeaders().add("Content-Type", "text/html");
					page.addToBody("<h2>It looks like something has gone wrong when your problem file was being  read...</h2>");
					page.addToBody("<br> <p>The following error was generated : <br>"+e.getMessage() +"</p>");
					page.addToBody("<h2> Would you like to  ... <br><a href=\"new \"> try and upload again?</a> <br>or<br>  <a href=\"mailto:n.urquhart@napier.ac.uk\">email for help?</a> </h2>");				
					resp.send(200, page.html());
					return 0;
				}
			}
		}
		synchronized(taskList){ 
			taskList.add(newTask);
		}
		resp.getHeaders().add("Content-Type", "text/html");
		page.addToBody("<h2>Your file appears to have been understood .</h2> ");
		page.addToBody("<h3>Your problem reference is  : "+newTask.getId()+ "<br></h3>");			

		String nokey = ServerProperties.getInstance().get("nokey");
		if (nokey != null)
			if (!nokey.contains("true")) {
				page.addToBody("<h3> Key: "+ newTask.getKey() +" <br>You might wish to make a note of the key as you may need it later to access your results.</h3>");
			}
		page.addToBody("<H3><a href= /job?id="+newTask.getId()+"&key="+newTask.getKey() +" class =\"button\" > Continue </a></h3> ");
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
		//int capacity =0;
		
		String readLine = "";
	
		while ((readLine = b.readLine()) != null) {
			System.out.println(readLine);
			readLine =readLine.trim();
			String[] data = readLine.split(",");
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
	
	private boolean idInUse(String id) {
		for (Task t : taskList) {
			if (t.getId().equals(id))
				return true;
		}
		return false;
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