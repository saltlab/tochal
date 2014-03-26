package com.proteus.core.interactiongraph.node;

public class Function extends InteractionNode {
//	private String name;
	private String fileName, scopeName;
	
	public Function(String functionName) {
		super(functionName);
		fileName = "";
		scopeName = "";
//		this.name = name;
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
	
	
/*
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	*/
}
