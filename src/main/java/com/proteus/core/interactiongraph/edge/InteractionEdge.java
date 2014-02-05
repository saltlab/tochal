package com.proteus.core.interactiongraph.edge;

import com.proteus.core.interactiongraph.node.InteractionNode;

public class InteractionEdge {
	private InteractionNode input;
	private InteractionNode output;
	private boolean visited;
	
	// TODO
	public static int edgeCounter = 0;
	private int counter;
	
	public InteractionEdge() {
		this.visited = false;
		// TODO
		this.counter = InteractionEdge.edgeCounter ++;
	}
	
	// TODO
	public int getCounter() {
		return this.counter;
	}

	public InteractionNode getInput() {
		return input;
	}
	
	public void setInput(InteractionNode input) {
		this.input = input;
	}
	
	public InteractionNode getOutput() {
		return output;
	}
	
	public void setOutput(InteractionNode output) {
		this.output = output;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
