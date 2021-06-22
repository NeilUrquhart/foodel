package edu.napier.foodel.server.handlers;


import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.napier.foodel.facade.FoodelSolver;

import edu.napier.foodel.server.Task;
import edu.napier.foodel.server.TaskStatus;

import net.freeutils.httpserver.HTTPServer.ContextHandler;


import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class CSVHandler implements ContextHandler {

	static List<Task> taskList;

	public CSVHandler(List<Task> taskList) {
		super();
		CSVHandler.taskList = taskList;
	}


	public int serve(Request req, Response resp) throws IOException {
		Map<String, String> params = req.getParams();

		String key = params.get("key");
		String run = params.get("run");

		if ((run == null)||(key==null)) {
			resp.getHeaders().add("Content-Type", "text/html");
			resp.send(200,  "Parameters missing");
			return 0;
		}

		//Find job
		Task current = null;
		synchronized(taskList){ 
			Iterator<Task> myIterator = taskList.iterator(); 
			while(myIterator.hasNext()){ 
				Task t = myIterator.next();
				if ((t.getKey().equals(key))) {
					current = t;
					break;
				}
			} 

			if (current == null) {
				resp.getHeaders().add("Content-Type", "text/html");
				resp.send(200,  "Job not found");
				return 0;
			}
			
			if (!current.getStatus().equals(TaskStatus.SOLVED)) {
				resp.getHeaders().add("Content-Type", "text/html");
				resp.send(200,  "Job not complete");
				return 0;
			}
			var f  = FoodelSolver.getInstance();
			f.setProblem(current.getProblem());
			resp.getHeaders().add("Content-Disposition", "attachment; filename="+current.getId()+"-"+run+".csv");	
			resp.getHeaders().add("Content-Type", "text/csv");
			resp.send(200,  f.getCSV(Integer.parseInt(run)));
			return 0;
		}
	}
}