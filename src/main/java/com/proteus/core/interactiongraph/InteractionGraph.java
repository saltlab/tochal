package com.proteus.core.interactiongraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.proteus.core.interactiongraph.edge.InteractionEdge;
import com.proteus.core.interactiongraph.edge.OpenXhr;
import com.proteus.core.interactiongraph.edge.ReadAccess;
import com.proteus.core.interactiongraph.edge.ReceiveXhrResponse;
import com.proteus.core.interactiongraph.edge.SendXhrRead;
import com.proteus.core.interactiongraph.edge.SendXhrWrite;
import com.proteus.core.interactiongraph.edge.WriteAccess;
import com.proteus.core.interactiongraph.node.DomElement;
import com.proteus.core.interactiongraph.node.Function;
import com.proteus.core.interactiongraph.node.InteractionNode;
import com.proteus.core.interactiongraph.node.XmlHttpRequest;

public class InteractionGraph {
	// InteractionGraph is a singleton
	private static final InteractionGraph INSTANCE = new InteractionGraph();
	
	private HashMap<String, Function> functionsByName;
	private HashMap<String, DomElement> domElementsById;
	private HashMap<String, XmlHttpRequest> xhrsById;
	
	//private ArrayList<InteractionEdge> edges; // TODO is this needed? do we also need a list of all nodes?
	

	// Private constructor for singleton
	private InteractionGraph() {
		functionsByName = new HashMap<String, Function>();
		domElementsById = new HashMap<String, DomElement>();
		xhrsById = new HashMap<String, XmlHttpRequest>();
	}

	// Get the instance of singleton InteractionGraph
	public static InteractionGraph getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Creates the relations between DOM elements and JavaScript functions
	 * Extracts the paths // TODO
	 * @param domRelations
	 */
	public void handleDomRelations(String domRelations) {
		extractDomRelations(domRelations);
		
		findImpactPaths(); // TODO 	SHOULD BE CALLED AT STATIC PHASE, NOT HERE
	}
	
	/**
	 * Extracts the relations between DOM elements and JavaScript functions from the received log
	 * @param domRelations
	 */
	protected void extractDomRelations(String domRelations) {
		System.out.println("+++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++");
		
		String domElementId = "", accessTypeList = "", accessFunctionList = "";
		ArrayList<String> accessTypes = new ArrayList<String>();
		ArrayList<String> accessFunctions = new ArrayList<String>();
		DomElement domElement = null;
		
		StringTokenizer tokenizer = new StringTokenizer(domRelations);
		if (tokenizer.hasMoreTokens())
			domElementId = tokenizer.nextToken(".");
		if (tokenizer.hasMoreElements())
			accessTypeList = tokenizer.nextToken(".");
		if (tokenizer.hasMoreTokens())
			accessFunctionList = tokenizer.nextToken(".");
		
		tokenizer = new StringTokenizer(domElementId);
		String str = "";
		for (int i = 0; i < 2; i ++)
			if (tokenizer.hasMoreTokens())
				str = tokenizer.nextToken(":");
		if (!str.isEmpty()) {
			// Check if the element already exists
			if (domElementsById.containsKey(str))
				domElement = domElementsById.get(str);
			else { // If not, create new DomElement object and add it to the map
				domElement = new DomElement(str);
				domElementsById.put(str, domElement);
			}
		}
		else {
			System.err.println("DomAccessHandler::makeAccessRelations -> NO ID");
		}

		// Extract string representations of accessTypes (interactionEdges)
		tokenizer = new StringTokenizer(accessTypeList);
		if (tokenizer.hasMoreTokens())
			tokenizer.nextToken(":");
		while (tokenizer.hasMoreTokens())
			accessTypes.add(tokenizer.nextToken(",:"));
		
		// Extract string representations of functions
		tokenizer = new StringTokenizer(accessFunctionList);
		if (tokenizer.hasMoreTokens())
			tokenizer.nextToken(":");
		while (tokenizer.hasMoreTokens())
			accessFunctions.add(tokenizer.nextToken(",:"));
				
		// Create accessType objects for DOM relations
		ArrayList<InteractionEdge> domAccessTypeObjects = new ArrayList<InteractionEdge>();
		for (int i = 0; i < accessTypes.size(); i ++) {
			String accessType = accessTypes.get(i);
			String accessTypeClassName = "com.proteus.core.interactiongraph.edge." + accessType;
			try {
				InteractionEdge domAccess = (InteractionEdge) Class.forName(accessTypeClassName).newInstance(); ///////
/////////////////				domAccess.setDomElement(domElement);
				domAccessTypeObjects.add(domAccess); //
/*				if (domAccess instanceof ReadAccess) {
					domAccess.setOutput(domElement);
					// TODO ???????????? domElement.getInput.add(domAccess)
					// TODO accessTypeObjects.add(domAccess);
					System.out.println("//////////////////////////////////////////////////////");
				}
				else if (domAccess instanceof WriteAccess) {
					domAccess.setInput(domElement);
					// TODO ???????????? domElement.getOutput.add(domAccess)
					// TODO accessTypeObjects.add(domAccess);
					System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
				}
*/				/*
				domAccess.setDomElement(domElement);
				accessTypeObjects.add(domAccess); //
				*/
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		tokenizer = new StringTokenizer(accessFunctionList);
		if (tokenizer.hasMoreTokens())
			tokenizer.nextToken(":");
		while (tokenizer.hasMoreTokens())
			accessFunctions.add(tokenizer.nextToken(",:"));
		
		// Create accessFunction objects
//		ArrayList<Integer> redundanctFunctions = new ArrayList<Integer>(); // TODO
		ArrayList<Function> accessFunctionObjects = new ArrayList<Function>();
		for (int i = 0; i < accessFunctions.size(); i ++) {
			String accessFunction = accessFunctions.get(i);
			Function function = new Function("null");
			if (accessFunction.equals("null")) {
				// TODO WHAT TO DO?
				// TODO add to map of functions?
			}
			else {
				function.setStrId(accessFunction);
				// TODO add to map of functions?
			}
			accessFunctionObjects.add(function);
///////////////////////			functionsByName.put(function.getName(), function); //////////////////
		}
		
		if (domAccessTypeObjects.size() != accessFunctionObjects.size()) {
			System.err.println("ERROR: InteractionGraph::makeAccessRelations -> number of access types and functions does not match");
			return; //
		}

		ArrayList<InteractionEdge> accessTypeObjectsTrimmed = new ArrayList<InteractionEdge>();
		ArrayList<Function> accessFunctionObjectsTrimmed = new ArrayList<Function>();
		
		for (int i = 0; i < domAccessTypeObjects.size() - 1; i ++) {
			boolean redundantRelation = false;
			for (int j = i + 1; j < domAccessTypeObjects.size(); j ++) {
				if (accessFunctionObjects.get(i).getStrId().equals(accessFunctionObjects.get(j).getStrId()) &&
					domAccessTypeObjects.get(i).getClass().equals(domAccessTypeObjects.get(j).getClass()))
					redundantRelation = true;
			}
			if (!redundantRelation) {
				accessTypeObjectsTrimmed.add(domAccessTypeObjects.get(i));
				accessFunctionObjectsTrimmed.add(accessFunctionObjects.get(i));
			}
		}
		
		for (int i = 0; i < accessTypeObjectsTrimmed.size(); i ++) {
			InteractionEdge da = accessTypeObjectsTrimmed.get(i);
			Function f = accessFunctionObjectsTrimmed.get(i);
			if (functionsByName.containsKey(f.getStrId())) {
				f = functionsByName.get(f.getStrId());
//				Iterator itr = functionsByName.get(f.getName()).iterator();
//				f = (Function) itr.next();
			}
			else {
				functionsByName.put(f.getStrId(), f);
			}
//			da.setFunction(f);
//			f.getDomAccesses().add(da);
			
			if (da instanceof WriteAccess) {
				da.setInput(f);
				da.setOutput(domElement);
				domElement.addInput(da);
				f.addOutput(da);
			}
			else if (da instanceof ReadAccess) {
				da.setInput(domElement);
				da.setOutput(f);
				domElement.addOutput(da);
				f.addInput(da);
			}
		}
	}
	
	/**
	 * Creates the relations between XHR objects and JavaScript functions
	 * Extracts the paths // TODO
	 * @param xhrRelations
	 */
	public void handleXhrRelations(String xhrRelations) {
		extractXhrRelations(xhrRelations);
		// TODO extractXhrJsPaths();
	}

	protected void extractXhrRelations(String xhrRelations) {
		String xhrAccessType = "", xhrObjId = "", xhrAccessFunction = "";
		StringTokenizer tokenizer = new StringTokenizer(xhrRelations);
		
		while (tokenizer.hasMoreTokens()) {
			xhrAccessType = "";
			xhrObjId = "";
			xhrAccessFunction = "";
			
			if (tokenizer.hasMoreTokens())
				xhrAccessType = tokenizer.nextToken("_"); // {"messageType":"XHR_
			if (tokenizer.hasMoreTokens())
				xhrAccessType = tokenizer.nextToken("_\""); // OpenAccess, SendAccess, ResponseAccess
			
			if (tokenizer.hasMoreTokens())
				xhrObjId = tokenizer.nextToken(":"); // ","id":
			if (tokenizer.hasMoreTokens())
				xhrObjId = tokenizer.nextToken(":\""); // XHR662AD7
				
			if (tokenizer.hasMoreTokens())
				xhrAccessFunction = tokenizer.nextToken(":"); // ","accessFunction":
			if (tokenizer.hasMoreTokens())
				xhrAccessFunction = tokenizer.nextToken(":\"}"); // sendUserInfoToServer
			
			System.out.println("xhrAccessType: " + xhrAccessType);
			System.out.println("xhrObjId: " + xhrObjId);
			System.out.println("xhrAccessFunction: " + xhrAccessFunction);
			
			XmlHttpRequest xhr;
			if (xhrsById.containsKey(xhrObjId))
				xhr = xhrsById.get(xhrObjId);
			else {
				xhr = new XmlHttpRequest(xhrObjId);
				xhrsById.put(xhr.getStrId(), xhr);
			}

			Function function = null;
			if (functionsByName.containsKey(xhrAccessFunction))
				function = functionsByName.get(xhrAccessFunction);
			else {
				function = new Function(xhrAccessFunction);
				functionsByName.put(xhrAccessFunction, function);
			}

//			XmlHttpRequestAccess xhrAccess = (XmlHttpRequestAccess) Class.forName("com.tochal.core.interactiongraph.edge." + xhrAccessType).newInstance();
			if (xhrAccessType.equals("OpenAccess")) {
				OpenXhr openXhrAccess = new OpenXhr();
				
				openXhrAccess.setInput(function);
				openXhrAccess.setOutput(xhr);
				xhr.addInput(openXhrAccess);
				function.addOutput(openXhrAccess);
			}
			else if (xhrAccessType.equals("SendAccess")) {
				SendXhrRead sendReadAccess = new SendXhrRead();
				
				sendReadAccess.setInput(xhr);
				sendReadAccess.setOutput(function);
				xhr.addOutput(sendReadAccess);
				function.addInput(sendReadAccess);
				
				SendXhrWrite sendWriteAccess = new SendXhrWrite();
				
				sendWriteAccess.setInput(function);
				sendWriteAccess.setOutput(xhr);
				xhr.addInput(sendWriteAccess);
				function.addOutput(sendWriteAccess);
			}
			else if (xhrAccessType.equals("ResponseAccess")) {
				ReceiveXhrResponse responseAccess = new ReceiveXhrResponse();
				
				responseAccess.setInput(xhr);
				responseAccess.setOutput(function);
				xhr.addOutput(responseAccess);
				function.addInput(responseAccess);
			}
			System.out.println("========");
			System.out.println(xhrsById.toString());
		}

	}
	
	public void findImpactPaths() {
		Collection<Function> functions = functionsByName.values();
		
		for (Function f : functions) {
			dfsFindImpactPath(f, 0);
			// Reset visited flags after traversal for each function
		}
	}
	
	protected void dfsFindImpactPath(InteractionNode node, int levelCounter) {
		if (node == null) // necessary?
			return;
		
		for (InteractionEdge e : node.getOutput()) {
			if (!e.isVisited()) {
				InteractionNode next = e.getOutput();
				if (next != null && !next.isVisited()) {
					e.setVisited(true);
					dfsFindImpactPath(next, levelCounter ++);
				}
			}
		}
		
		node.setVisited(true);
		System.out.println(">>> " + levelCounter + " <<<");
	}

}
