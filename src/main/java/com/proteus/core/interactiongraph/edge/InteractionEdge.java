package com.proteus.core.interactiongraph.edge;

import com.proteus.core.interactiongraph.node.InteractionNode;

public class InteractionEdge {
	private InteractionNode input;
	private InteractionNode output;
	
	// TODO constructor
	
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
}
