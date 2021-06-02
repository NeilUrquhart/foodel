package edu.napier.foodel.server;

import java.security.SecureRandom;

import edu.napier.foodel.problem.cvrp.CVRPProblem;
import edu.napier.foodel.problemTemplate.FoodelProblem;



public class Task {
	private FoodelProblem p;// = new FoodelProblem();
	private String inputFile;
	private TaskStatus status;
	private String key = "";
	//private String id = "";
	
	
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
		return p.getReference();
	}

}
