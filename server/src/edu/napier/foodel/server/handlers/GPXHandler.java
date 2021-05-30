package edu.napier.foodel.server.handlers;


import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.napier.foodel.facade.FoodelFacade;

import edu.napier.foodel.server.Problem;
import edu.napier.foodel.server.ProblemStatus;

import net.freeutils.httpserver.HTTPServer.ContextHandler;


import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

public class GPXHandler implements ContextHandler {

	static List<Problem> taskList;

	public GPXHandler(List<Problem> taskList) {
		super();
		GPXHandler.taskList = taskList;
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
		Problem current = null;
		synchronized(taskList){ 
			Iterator<Problem> myIterator = taskList.iterator(); 
			while(myIterator.hasNext()){ 
				Problem t = myIterator.next();
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
			
			if (!current.getStatus().equals(ProblemStatus.SOLVED)) {
				resp.getHeaders().add("Content-Type", "text/html");
				resp.send(200,  "Job not complete");
				return 0;
			}
			var f  = FoodelFacade.getInstance();
			f.setProblem(current.getProblem());
			resp.getHeaders().add("Content-Disposition", "attachment; filename="+current.getId()+"-"+run+".gpx");	
			resp.getHeaders().add("Content-Type", "application/octet-stream");
			resp.send(200,  f.getGPX(Integer.parseInt(run)));
			return 0;
		}
	}
}