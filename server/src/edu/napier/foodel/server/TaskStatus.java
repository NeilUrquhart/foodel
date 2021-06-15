package edu.napier.foodel.server;

public enum TaskStatus {
	LOADING("loading"),
	WAITING("waiting"),
	RUNNING("running"),
	SOLVED("solved"),
	BROKEN("broken");//Used if there's any form of error -see Task.errMsg

	private String status;

	private TaskStatus(String s) {
		this.status = s;
	}

	@Override
	public String toString(){
		return status;
	}
}
