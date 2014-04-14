package com.proteus.core.trace;

public class FunctionCall extends FunctionTrace {
	private String TargetFunction;

	public String getTargetFunction() {
		return TargetFunction;
	}

	public void setTargetFunction(String targetFunction) {
		TargetFunction = targetFunction;
	}	
}
