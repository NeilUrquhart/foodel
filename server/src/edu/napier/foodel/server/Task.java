package edu.napier.foodel.server;

import java.security.SecureRandom;
import java.util.HashMap;

import edu.napier.foodel.problem.cvrp.CVRPProblem;
import edu.napier.foodel.problemTemplate.FoodelProblem;



public class Task {
	private FoodelProblem p;// = new FoodelProblem();
	private String inputFile;
	private TaskStatus status;
	private String key = "";
	private HashMap<String,String[]> rawData;
	private String id = "";
	private String errMsg;
	private long removalTime;
	private int lifeTime= 12;  //Hours to live for on the server - default is 12
	
	
	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Task() {
		//Generate random key
		SecureRandom random = new SecureRandom();
		byte[] values = new byte[20];
		random.nextBytes(values);
		
		StringBuilder sb = new StringBuilder();
	    for (byte b : values) {
	        sb.append(String.format("%02X", b));
	    }
		key = sb.toString();
		
		String time =ServerProperties.getInstance().get("removaltime");
		if (time!=null) {
			try {
				lifeTime = Integer.parseInt(time);
			}catch(Exception e) {
				
				lifeTime=12;//default
			}
		}
		removalTime = System.currentTimeMillis() + (3600000*lifeTime);
	}
	
	public long getRemovalTime() {
		return removalTime;
	}
	public void setRawData(HashMap<String,String[]> rawData) {//The basic data strucure read in from the CSV file
		this.rawData = rawData;
	}
	
	public HashMap<String,String[]>  getRawData() {//The basic data strucure read in from the CSV file
		return this.rawData ;
	}
	
	public void setProblem(FoodelProblem p) {
		this.p = p;
	}
	
	public FoodelProblem getProblem() {
		return p;
	}
	
	public String getKey() {
		return key;
	}
	
	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
