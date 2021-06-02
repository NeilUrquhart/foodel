package edu.napier.foodel.server;

public enum TaskStatus {
	LOADING("loading"),
	WAITING("waiting"),
	RUNNING("running"),
	SOLVED("solved");

	private String status;

	private TaskStatus(String s) {
		this.status = s;
	}

	@Override
	public String toString(){
		return status;
	}
}
