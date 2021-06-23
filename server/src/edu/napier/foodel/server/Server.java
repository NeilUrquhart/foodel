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

import com.google.gson.Gson;
import edu.napier.foodel.facade.FoodelSolver;
import edu.napier.foodel.geo.GHopperInterface;
import edu.napier.foodel.geo.Geocoder;
import edu.napier.foodel.problem.cvrp.CVRPProblem;
import edu.napier.foodel.problemTemplate.FoodelProblem;
import edu.napier.foodel.server.handlers.CSVHandler;
import edu.napier.foodel.server.handlers.Default;
import edu.napier.foodel.server.handlers.GPXHandler;
import edu.napier.foodel.server.handlers.TaskHandler;
import edu.napier.foodel.server.handlers.MapHandler;
import edu.napier.foodel.server.handlers.UploadProblem;
import edu.napier.foodel.server.handlers.ServerStatus;
import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.VirtualHost;
import net.freeutils.httpserver.HTTPServer.FileContextHandler;

public class Server {
	private static List<Task> taskList;
	//private static int port;
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

	public static void main(String[] args) throws SecurityException, IOException {


		LOGGER.addHandler(new FileHandler(ServerProperties.getInstance().get("logdir")+"foodel.log"));
		LOGGER.info("Starting server");
		//Init task list
		taskList =new ArrayList<Task>();
		taskList = Collections.synchronizedList(taskList);

		startServer();
		//Init OSM
		setData();
		//main loop
		processLoop();
	}

	private static void processLoop() {
		Task currentProblem = null;
		while(true) {			
			//read problem
			synchronized(taskList){
				if (taskList.size() >0) {
					for (Task t : taskList) {
						if (t.getStatus().equals(TaskStatus.WAITING)) {
							currentProblem = t;
							currentProblem.setStatus(TaskStatus.RUNNING);
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

	private static Task executeProblem(Task currentTask) {
		FoodelProblem p;
		try {
			LOGGER.info("Parsing raw data "+ currentTask.getInputFile());
			try {
				p= FoodelSolver.getInstance().newProblem(currentTask.getRawData(),currentTask.getInputFile());
				currentTask.setProblem(p);
			}catch(Exception e) {
				currentTask.setStatus(TaskStatus.BROKEN);
				currentTask.setErrMsg(e.getMessage());
				return null;
			}
			LOGGER.info("Starting to solve "+ currentTask.getId());
			p= currentTask.getProblem();
			var f = FoodelSolver.getInstance();
			f.setProblem(p);
			f.solve();	
			currentTask.setStatus(TaskStatus.SOLVED);
			LOGGER.info("Solved "+ currentTask.getId());

			String save  = ServerProperties.getInstance().get("savesolutions");
			if (save != null)
				if (save.equals("true")) {
					var gson = new Gson();
					// Java objects to String
					String dir = ServerProperties.getInstance().get("solsdir");
					try (var writer = new FileWriter(dir + currentTask.getKey()+".json")) {
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
		if (ServerProperties.getInstance().get("customrouting") != null)
			if (Boolean.parseBoolean(ServerProperties.getInstance().get("customrouting"))==true) {
				GHopperInterface.useCustomProile();
				int c=1;
				String rd  = ServerProperties.getInstance().get("roadclass"+c);
				while (rd != null) {
					double weight = Double.parseDouble(ServerProperties.getInstance().get("roadweight"+c));
					GHopperInterface.addRoadWeightToModel(rd, weight);
					c++;
					rd  = ServerProperties.getInstance().get("roadclass"+c);
				}
			}
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
			host.addContext("/csv", new CSVHandler(taskList));
			host.addContext("/job", new TaskHandler(taskList));
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
