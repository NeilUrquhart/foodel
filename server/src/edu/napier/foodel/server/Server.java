package edu.napier.foodel.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import com.google.gson.Gson;
import edu.napier.foodel.facade.FoodelFacade;
import edu.napier.foodel.geocode.Geocoder;
import edu.napier.foodel.problem.FoodelProblem;
import edu.napier.foodel.server.handlers.Default;
import edu.napier.foodel.server.handlers.GPXHandler;
import edu.napier.foodel.server.handlers.Job;
import edu.napier.foodel.server.handlers.MapHandler;
import edu.napier.foodel.server.handlers.UploadProblem;
import edu.napier.foodel.server.handlers.ServerStatus;
import edu.napier.ghopper.GHopperInterface;
import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.VirtualHost;
import net.freeutils.httpserver.HTTPServer.FileContextHandler;

public class Server {
	private static List<Problem> taskList;
	//private static int port;
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    
	public static void main(String[] args) throws SecurityException, IOException {

		
		LOGGER.addHandler(new FileHandler(ServerProperties.getInstance().get("logdir")+"foodel.log"));
		LOGGER.info("Starting server");
		//Init task list
		taskList =new ArrayList<Problem>();
		taskList = Collections.synchronizedList(taskList);
		
		startServer();
		//Init OSM and pcode data
		setData();
		
		
		
		//main loop
		processLoop();
	}

	private static void processLoop() {
		Problem currentProblem = null;
		while(true) {			
			//read problem
			synchronized(taskList){
				if (taskList.size() >0) {
					for (Problem t : taskList) {
						if (t.getStatus().equals(ProblemStatus.WAITING)) {
							currentProblem = t;
							currentProblem.setStatus(ProblemStatus.RUNNING);
							break;
						}
					}
				}
			}

			if (currentProblem != null){
				currentProblem = executeProblem(currentProblem);
			}
		}
	}

	private static Problem executeProblem(Problem currentTask) {
		FoodelProblem p;
		try {
			LOGGER.info("Starting to solve "+ currentTask.getId());
			p= currentTask.getProblem();
			var f = new FoodelFacade();
			f.setProblem(p);
			f.run();	
			currentTask.setStatus(ProblemStatus.SOLVED);
			LOGGER.info("Solved "+ currentTask.getId());
			
			String save  = ServerProperties.getInstance().get("savesolutions");
			if (save != null)
				if (save.equals("true")) {
					var gson = new Gson();
					// Java objects to String
					String dir = ServerProperties.getInstance().get("solsdir");
					try (var writer = new FileWriter(dir + p.getReference()+".json")) {
						gson.toJson(currentTask, writer);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		currentTask = null;
		return currentTask;
	}

	private static void setData() {
		Geocoder.setDirectory(ServerProperties.getInstance().get("datadir")+ServerProperties.getInstance().get("postcodedir"));
		GHopperInterface.init(ServerProperties.getInstance().get("datadir"),ServerProperties.getInstance().get("osmfile"));
	}

	private static void startServer() {
		//start server

		var port = Integer.parseInt(ServerProperties.getInstance().get("port"));
	
		var server = new HTTPServer(port);
		try {
			server.start();

			var host = new VirtualHost(null);
			host.addAlias("food"); // if it has aliases
			server.addVirtualHost(host);
			host.addContexts(new UploadProblem()); // adds all annotated context handlers relating to problem upload
			UploadProblem.setTaskList(taskList);
			host.addContext("/status", new ServerStatus(taskList));
			host.addContext("/gpx", new GPXHandler(taskList));
			host.addContext("/job", new Job(taskList));
			host.addContext("/map", new MapHandler(taskList));
			host.addContext("/", new Default());
			host.addContext("/static", new FileContextHandler( new File("public_html/")));
			//Allow the public_html/ folder to host static content
			LOGGER.info("Server running");
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}
}
