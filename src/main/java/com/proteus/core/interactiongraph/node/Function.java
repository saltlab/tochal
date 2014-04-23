package com.proteus.core.interactiongraph.node;

import java.util.ArrayList;

public class Function extends InteractionNode {
//	private String name;
	private String fileName, scopeName;
	private ArrayList<String> argsOverTime;
	
	public Function(String functionName) {
		super(functionName);
		fileName = "";
		scopeName = "";
//		this.name = name;
		argsOverTime = new ArrayList<String>();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
	
	public void addArgs(String args) {
		this.argsOverTime.add(args);
	}
	
	public ArrayList<String> getArgsOverTime() {
		return this.argsOverTime;
	}
	
/*
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	*/
}
