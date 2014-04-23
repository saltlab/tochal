package com.proteus.core.interactiongraph.edge;

/**
 * This type of access is used when function foo() calls function bar(args) and passes some arguments
 * Then foo() writes to bar()
 * @author Saba
 *
 */
public class CallFunction extends WriteAccess {
	private boolean isStatic;
	
	public CallFunction() {
		this.isStatic = false;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
}
