package com.proteus.core.interactiongraph.edge;

/**
 * This type of access is used when function foo() calls function bar() and bar() returns a value
 * Then foo() reads from bar()
 * @author Saba
 *
 */
public class ReturnFunction extends ReadAccess {
	private boolean isStatic;
	
	public ReturnFunction() {
		this.isStatic = false;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

}
