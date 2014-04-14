package com.proteus.core.interactiongraph.node;

import java.util.ArrayList;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableList;
import com.proteus.core.interactiongraph.edge.InteractionEdge;



public class InteractionNode {
	private ArrayList<InteractionEdge> input;
	private ArrayList<InteractionEdge> output;
	private String strId;
	private boolean visited;
		
	// TODO more constructors?
	public InteractionNode(String strId) {
		input = new ArrayList<InteractionEdge>();
		output = new ArrayList<InteractionEdge>();
		this.strId = strId;
		this.visited = false;
	}

	public ArrayList<InteractionEdge> getInput() {
		return new ArrayList<InteractionEdge>(Collections.unmodifiableList(this.input));
		
///////////////		return (ArrayList<InteractionEdge>) Collections.unmodifiableList(this.input);
	}

	public void setInput(ArrayList<InteractionEdge> input) {
		this.input = input;
	}

	public ArrayList<InteractionEdge> getOutput() {
		return new ArrayList<InteractionEdge>(Collections.unmodifiableList(this.output));
///////////////		return new ArrayList<InteractionEdge>(Collections.unmodifiableList(this.output));
		
//		return this.output; // TODO
		
//		ImmutableList<InteractionEdge> list = ImmutableList.copyOf(this.output);
//		return (ArrayList<InteractionEdge>) Collections.unmodifiableList(this.output);
//		return list;
	}

	public void setOutput(ArrayList<InteractionEdge> output) {
		this.output = output;
	}
	
	public void addInput(InteractionEdge newInput) {
		input.add(newInput);
	}

	public void addOutput(InteractionEdge newOutput) {
		output.add(newOutput);
	}
	
	public String getStrId() {
		return this.strId;
	}
	
	public void setStrId(String strId) {
		this.strId = strId;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
