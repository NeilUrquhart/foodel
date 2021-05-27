package edu.napier.foodel.server;

import java.security.SecureRandom;

import edu.napier.foodel.problem.FoodelProblem;

public class Problem {
	private FoodelProblem p = new FoodelProblem();
	private String inputFile;
	private ProblemStatus status;
	private String key = "";
	//private String id = "";
	
	
	public Problem() {
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
	
	public ProblemStatus getStatus() {
		return status;
	}

	public void setStatus(ProblemStatus status) {
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

//	public void setId(String id) {
//		this.id = id;
//	}
}
