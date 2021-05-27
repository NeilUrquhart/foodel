package edu.napier.foodel.server;

public enum ProblemStatus {
	LOADING("loading"),
	WAITING("waiting"),
	RUNNING("running"),
	SOLVED("solved");

	private String status;

	private ProblemStatus(String s) {
		this.status = s;
	}

	@Override
	public String toString(){
		return status;
	}
}
