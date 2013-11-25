package com.proteus.core.interactiongraph.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.proteus.core.interactiongraph.edge.InteractionEdge;

public class InteractionNode {
	private ArrayList<InteractionEdge> input;
	private ArrayList<InteractionEdge> output;
	private String strId;
		
	// TODO more constructors?
	public InteractionNode(String strId) {
		input = new ArrayList<InteractionEdge>();
		output = new ArrayList<InteractionEdge>();
		this.strId = strId;
	}

	public ArrayList<InteractionEdge> getInput() {
		return (ArrayList<InteractionEdge>) Collections.unmodifiableList(this.input);
	}

	public void setInput(ArrayList<InteractionEdge> input) {
		this.input = input;
	}

	public ArrayList<InteractionEdge> getOutput() {
		return (ArrayList<InteractionEdge>) Collections.unmodifiableList(this.output);
	}

	public void setOutput(ArrayList<InteractionEdge> output) {
		this.output = output;
	}
	
	public void addInput(InteractionEdge newInput) {
		input.add(newInput);
	}

	public void addOutput(InteractionEdge newOutput) {
		input.add(newOutput);
	}
	
	public String getStrId() {
		return this.strId;
	}
	
	public void setStrId(String strId) {
		this.strId = strId;
	}

}
