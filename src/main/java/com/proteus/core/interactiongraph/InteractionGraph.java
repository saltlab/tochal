package com.proteus.core.interactiongraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import com.proteus.core.interactiongraph.edge.CallFunction;
import com.proteus.core.interactiongraph.edge.InteractionEdge;
import com.proteus.core.interactiongraph.edge.OpenXhr;
import com.proteus.core.interactiongraph.edge.ReadAccess;
import com.proteus.core.interactiongraph.edge.ReceiveXhrResponse;
import com.proteus.core.interactiongraph.edge.ReturnFunction;
import com.proteus.core.interactiongraph.edge.SendXhrRead;
import com.proteus.core.interactiongraph.edge.SendXhrWrite;
import com.proteus.core.interactiongraph.edge.WriteAccess;
import com.proteus.core.interactiongraph.node.DomElement;
import com.proteus.core.interactiongraph.node.Function;
import com.proteus.core.interactiongraph.node.InteractionNode;
import com.proteus.core.interactiongraph.node.XmlHttpRequest;
import com.proteus.core.trace.FunctionCall;
import com.proteus.core.trace.FunctionEnter;
import com.proteus.core.trace.FunctionExit;
import com.proteus.core.trace.FunctionReturnStatement;
import com.proteus.core.trace.TraceObject;
import com.proteus.stats.Statistics;

public class InteractionGraph {
	// InteractionGraph is a singleton
	private static final InteractionGraph INSTANCE = new InteractionGraph();
	
	private HashMap<String, Function> functionsByName;
	private HashMap<String, DomElement> domElementsById;
	private HashMap<String, XmlHttpRequest> xhrsById;
	
	private ArrayList<InteractionEdge> edges; // TODO
	
	// Private constructor for singleton
	private InteractionGraph() {
		functionsByName = new HashMap<String, Function>();
		domElementsById = new HashMap<String, DomElement>();
		xhrsById = new HashMap<String, XmlHttpRequest>();
		
		edges = new ArrayList<InteractionEdge>();
	}

	// Get the instance of singleton InteractionGraph
	public static InteractionGraph getInstance() {
		return INSTANCE;
	}
	
	// TODO this method will be accessed from outside to traverse the graph and find info about dynamic paths
	public void handleGraphAfterTermination() {
		
		System.out.println("1) " + domElementsById.size());
		System.out.println("2) " + functionsByName.size());
		
		// Gather statistical/topological information about the structure of captured dom relations
		HashMap<DomElement, ArrayList<InteractionEdge>> elementSortedAccessMap = getSortedAccessesForElements();//gatherStatInfo();
		int numOfDomElementsWithUniqueWR = findUniqueFunctionAccess(elementSortedAccessMap); // TODO

		
		// If the size of inputs and outputs are greater or equal to one, we can say that there are read and write accesses made to that node
		int numOfDomElementsOnDynamicPath = findDynamicDOMPaths();
//		System.out.println(">>>>>>>>>>>>>numOfDomElementsOnDynamicPath: " + numOfDomElementsOnDynamicPath);
		System.out.println(">>>>>>>>>>>>>numOfDomElementsWithUniqueWR: " + numOfDomElementsWithUniqueWR);
		System.out.println(">>>>>>>>>>>>>numOfDomElementsAccess: " + domElementsById.size());
		
/////////		findImpactPaths(); // TODO 	SHOULD BE CALLED AT STATIC PHASE, NOT HERE

		findPathsBetweenFunctionPairs();
		
		findTopologicalGraphCharacteristics();
	}
	
	// TODO TODO TODO TODO TODO
	protected void findTopologicalGraphCharacteristics() {
		System.out.println("DOM element input sizes");
		double []domInputSizes = new double[domElementsById.values().size()];
		int counter = 0;
		for (DomElement el : domElementsById.values()) {
			System.out.print(el.getInput().size() + ", ");
			domInputSizes[counter ++] = el.getInput().size();
		}
		System.out.println();
		System.out.println("SIZE: " + domElementsById.size());
		System.out.println("AVG: " + Statistics.getMean(domInputSizes));
		System.out.println("MED: " + Statistics.getMedian(domInputSizes));
		System.out.println("VAR: " + Statistics.getVariance(domInputSizes));
		System.out.println("STD-DEV: " + Statistics.getStdDev(domInputSizes));
		
		System.out.println("DOM element output sizes");
		counter = 0;
		double []domOutputSizes = new double[domElementsById.values().size()];
		for (DomElement el : domElementsById.values()) {
			System.out.print(el.getOutput().size() + ", ");
			domOutputSizes[counter ++] = el.getOutput().size();
		}
		System.out.println();
		System.out.println("SIZE: " + domElementsById.size());
		System.out.println("AVG: " + Statistics.getMean(domOutputSizes));
		System.out.println("MED: " + Statistics.getMedian(domOutputSizes));
		System.out.println("VAR: " + Statistics.getVariance(domOutputSizes));
		System.out.println("STD-DEV: " + Statistics.getStdDev(domOutputSizes));

		System.out.println("Function input sizes");
		counter = 0;
		double []functionInputSizes = new double[functionsByName.values().size()];
		for (Function f : functionsByName.values()) {
			System.out.print(f.getInput().size() + ", ");
			functionInputSizes[counter ++] = f.getInput().size();
		}
		System.out.println();
		System.out.println("SIZE: " + functionsByName.size());
		System.out.println("AVG: " + Statistics.getMean(functionInputSizes));
		System.out.println("MED: " + Statistics.getMedian(functionInputSizes));
		System.out.println("VAR: " + Statistics.getVariance(functionInputSizes));
		System.out.println("STD-DEV: " + Statistics.getStdDev(functionInputSizes));
		
		System.out.println("Function output sizes");
		counter = 0;
		double []functionOutputSizes = new double[functionsByName.values().size()];
		for (Function f : functionsByName.values()) {
			System.out.print(f.getOutput().size() + ", ");
			functionOutputSizes[counter ++] = f.getOutput().size();
		}
		System.out.println();
		System.out.println("SIZE: " + functionsByName.size());
		System.out.println("AVG: " + Statistics.getMean(functionOutputSizes));
		System.out.println("MED: " + Statistics.getMedian(functionOutputSizes));
		System.out.println("VAR: " + Statistics.getVariance(functionOutputSizes));
		System.out.println("STD-DEV: " + Statistics.getStdDev(functionOutputSizes));

	}
	
	// TODO TODO TODO
	protected int findUniqueFunctionAccess(HashMap<DomElement, ArrayList<InteractionEdge>> elementSortedAccessMap) {
		int numOfDomElementsWithUniqueWR = 0;
		
		for (DomElement el : elementSortedAccessMap.keySet()) {
			ArrayList<InteractionEdge> accesses = elementSortedAccessMap.get(el);
			ArrayList<Integer> readerFunctions = new ArrayList<Integer>(); // TDOO index of the function based on sorted accesses
			ArrayList<Integer> writerFunctions = new ArrayList<Integer>(); // TDOO index of the function based on sorted accesses
			int [][]directedFunctionAccesses = new int[accesses.size()][accesses.size()];
			
			for (int i = 0; i < accesses.size(); i ++) {
				InteractionEdge access = accesses.get(i);
				if (access instanceof ReadAccess)
					readerFunctions.add(i);
				else
					writerFunctions.add(i);
			}
			
			for (int i = 0; i < writerFunctions.size(); i ++)
				for (int j = 0; j < readerFunctions.size(); j ++)
					directedFunctionAccesses[writerFunctions.get(i)][readerFunctions.get(j)] = 1;
				//					directedFunctionAccesses[i][j] = 1;
			
			int numOfWRPairs = 0;
			int numOfWRPairsDiffFunctions = 0;
			
			for (int i = 0; i < accesses.size(); i ++)
				for (int j = 0; j < accesses.size(); j ++)
					if (directedFunctionAccesses[i][j] == 1) {
						numOfWRPairs ++;
						if (i != j)
							numOfWRPairsDiffFunctions ++;
					}
			
			if (numOfWRPairsDiffFunctions > 0)
				numOfDomElementsWithUniqueWR ++;
/*			
			System.out.println("++++++++++++++++");
			System.out.println("DOM Element: " + el.getStrId());
			System.out.println("numOfWRPairs: " + numOfWRPairs);
			System.out.println("numOfWRPairsDiffFunctions " + numOfWRPairsDiffFunctions);
*/
			for (int i = 0; i < accesses.size(); i ++)
				for (int j = 0; j < accesses.size(); j ++)
					if (directedFunctionAccesses[i][j] == 1 && i != j) {
						findPaths(accesses, directedFunctionAccesses, i, j, accesses.get(i).getStrId(), accesses.get(i).getFunctionNode().getStrId(), 1);
					}
		}
		
		return numOfDomElementsWithUniqueWR;
	}
	
	protected void findPaths(ArrayList<InteractionEdge> accesses, int [][]directedFunctionAccesses, int i, int j, String pathAccesses, String pathFunctions, int numOfAccesses) {
		for (int a = 0; a < accesses.size(); a ++)
			if (directedFunctionAccesses[j][a] == 1 && a != j)
				findPaths(accesses, directedFunctionAccesses, j, a, pathAccesses + "," + accesses.get(j).getStrId(), pathFunctions + "," + accesses.get(j).getFunctionNode().getStrId(), numOfAccesses + 1);
		directedFunctionAccesses[i][j] = 2;
	}
	
	/**
	 * Creates the relations between DOM elements and JavaScript functions
	 * Extracts the paths // TODO
	 * @param domRelations
	 */
	public void handleDomRelations(String domRelations) {
		extractDomRelations(domRelations);
		
		/***
		for (InteractionEdge e : edges)
			System.out.println(e.getStrId());
		***/
		
		// Answering the motivating challenge, we want to see if this is a real problem.
		// We want to see if in fact, there are nodes that are written to, and read from
		
		/**************************
		 THIS PART IS MOVED TO THE FOLLOWING FUNCTION handleGraphAfterTermination
		
		// Gather statistical/topological information about the structure of captured dom relations
		gatherStatInfo();
		
		// If the size of inputs and outputs are greater or equal to one, we can say that there are read and write accesses made to that node
		int numOfDomElementsOnDynamicPath = findDynamicDOMPaths();
		System.out.println(">>>>>>>>>>>>>numOfDomElementsOnDynamicPath: " + numOfDomElementsOnDynamicPath);
		System.out.println(">>>>>>>>>>>>>numOfDomElementsAccess: " + domElementsById.size());
		
		findImpactPaths(); // TODO 	SHOULD BE CALLED AT STATIC PHASE, NOT HERE
		
		**************************/
	}
	
	protected HashMap<DomElement, ArrayList<InteractionEdge>> getSortedAccessesForElements() {
		HashMap<DomElement, ArrayList<InteractionEdge>> domElementsWithSortedAcesses = new HashMap<DomElement, ArrayList<InteractionEdge>>();
		ArrayList<InteractionEdge> sortedHybridList;// = new ArrayList<InteractionEdge>();

		int numOfReadOnlyDomElements = 0;	
		int numOfWriteOnlyDomElements = 0;
		int numOfReadWriteElements = 0;

		for (Map.Entry<String, DomElement> entry : domElementsById.entrySet()) {
			sortedHybridList = new ArrayList<InteractionEdge>();
			
			DomElement el = entry.getValue();
			if (el.getInput().size() == 0)
				numOfReadOnlyDomElements ++;
			else if (el.getOutput().size() == 0)
				numOfWriteOnlyDomElements ++;
			else {
				// Create a list of all inputs and outputs sorted by the counter
				numOfReadWriteElements ++;
				ArrayList<InteractionEdge> input = el.getInput();
				ArrayList<InteractionEdge> output = el.getOutput();
				// sort interaction edges based on time
//				ArrayList<InteractionEdge> sortedHybridList = new ArrayList<InteractionEdge>();
				
				int inputIndex = 0;
				int outputIndex = 0;
				
				while (true) {
					if (input.get(inputIndex).getCounter() <= output.get(outputIndex).getCounter()) {
						sortedHybridList.add(input.get(inputIndex));
						inputIndex ++;
					}
					else {
						sortedHybridList.add(output.get(outputIndex));
						outputIndex ++;
					}
					if (inputIndex >= (input.size() - 1)) {
						for (int i = outputIndex; i < output.size(); i ++)
							sortedHybridList.add(output.get(i));
						break;
					}
					else if (outputIndex >= (output.size() - 1)) {
						for (int i = inputIndex; i < input.size(); i ++)
							sortedHybridList.add(input.get(i));
						break;
					}
				}
			}
			
			domElementsWithSortedAcesses.put(el, sortedHybridList); // TODO

		}

		Iterator it = domElementsWithSortedAcesses.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<DomElement, ArrayList<InteractionEdge>> pairs = (Map.Entry<DomElement, ArrayList<InteractionEdge>>)it.next();
			DomElement e = pairs.getKey();
			ArrayList<InteractionEdge> sortedAccesses = pairs.getValue();
			/****
			System.out.println("========================== DOM Element: " + e.getStrId());
			for (int i = 0; i < sortedAccesses.size(); i ++) {
				InteractionEdge access = sortedAccesses.get(i);
				if (access instanceof WriteAccess)
					System.out.print("<W, " + access.getInput().getStrId() + "> ");
				else
					System.out.print("<R, " + access.getOutput().getStrId() + "> ");
			}
			System.out.println();
			****/
//			it.remove();
		}


		System.out.println("+*+*+*+ numOfReadOnlyDomElements: " + numOfReadOnlyDomElements);
		System.out.println("+*+*+*+ numOfWriteOnlyDomElements: " + numOfWriteOnlyDomElements);
		System.out.println("+*+*+*+ numOfReadWriteElements: " + numOfReadWriteElements);
		
		return domElementsWithSortedAcesses;
	}
	
	protected int findDynamicDOMPaths() {
		int numOfDomElementsOnDynamicPath = 0;
		
		for (Map.Entry<String, DomElement> entry : domElementsById.entrySet()) {
			DomElement el = entry.getValue();
/*			ArrayList<InteractionEdge> inputs = el.getInput();
			ArrayList<InteractionEdge> outputs = el.getOutput();
*/
/*			System.out.println("^^^^^^^^^^^ " + "ID: " + entry.getKey() + ", INPUTS: " + el.getInput().toString() + ", OUTPUTS: " + el.getOutput().toString() + "^^^^^^^^^^^");
*/
			if (el.getInput().size() >= 1 && el.getOutput().size() >= 1) {
				numOfDomElementsOnDynamicPath ++;
/*				System.out.println("^^^^^^^^^^^ " + "DYNAMIC DOM PATH" + "^^^^^^^^^^^");
				System.out.println("^^^^^^^^^^^ " + "ID: " + entry.getKey() + ", INPUTS: " + el.getInput().toString() + ", OUTPUTS: " + el.getOutput().toString() + "^^^^^^^^^^^");
				System.out.println("^^^^^^^^^^^ " + "" + "^^^^^^^^^^^");
*/			}
		}
		
/*		Iterator it = domElementsById.keySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			
		}
		*/
		
		return numOfDomElementsOnDynamicPath;
	}
	
	/**
	 * Extracts the relations between DOM elements and JavaScript functions from the received log
	 * @param domRelations
	 */
	protected void extractDomRelations(String domRelations) {
		System.out.println("+++++++++++++++++++++");
		
		String domElementId = "", accessTypeList = "", accessFunctionList = "";
		ArrayList<String> accessTypes = new ArrayList<String>();
		ArrayList<String> accessFunctions = new ArrayList<String>();
		DomElement domElement = null;
		
		StringTokenizer tokenizer = new StringTokenizer(domRelations);
		if (tokenizer.hasMoreTokens())
			domElementId = tokenizer.nextToken("*");
		if (tokenizer.hasMoreElements())
			accessTypeList = tokenizer.nextToken("*");
		if (tokenizer.hasMoreTokens())
			accessFunctionList = tokenizer.nextToken("*");
		
		tokenizer = new StringTokenizer(domElementId);
		String str = "";
		for (int i = 0; i < 2; i ++)
			if (tokenizer.hasMoreTokens())
				str = tokenizer.nextToken("@");
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
		System.out.println("** " + accessTypeList);
		if (tokenizer.hasMoreTokens())
			tokenizer.nextToken("@");
		while (tokenizer.hasMoreTokens())
			accessTypes.add(tokenizer.nextToken(",@"));
		
		// Extract string representations of functions
		tokenizer = new StringTokenizer(accessFunctionList);
		System.out.println("** " + accessFunctionList);
		if (tokenizer.hasMoreTokens())
			tokenizer.nextToken("@");
		while (tokenizer.hasMoreTokens())
			accessFunctions.add(tokenizer.nextToken(",@"));
				
		// Create accessType objects for DOM relations
		ArrayList<InteractionEdge> domAccessTypeObjects = new ArrayList<InteractionEdge>();
		for (int i = 0; i < accessTypes.size(); i ++) {
			String accessType = accessTypes.get(i);
			String accessTypeClassName = "com.proteus.core.interactiongraph.edge." + accessType;
			try {
				InteractionEdge domAccess = (InteractionEdge) Class.forName(accessTypeClassName).newInstance(); ///////
////////////*************+++				edges.add(domAccess); // TODO TODO TODO
				
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
		
/*		System.out.println("==========SIZES: " + domAccessTypeObjects.size() + " " + accessFunctionObjects.size());
*/
		ArrayList<InteractionEdge> accessTypeObjectsTrimmed = new ArrayList<InteractionEdge>();
		ArrayList<Function> accessFunctionObjectsTrimmed = new ArrayList<Function>();

		for (int i = 0; i < domAccessTypeObjects.size(); i ++) {
			boolean redundantRelation = false;
			for (int j = 0; j < accessTypeObjectsTrimmed.size(); j ++) {
				if (domAccessTypeObjects.get(i).getClass().equals(accessTypeObjectsTrimmed.get(j).getClass()) &&
						accessFunctionObjects.get(i).getStrId().equals(accessFunctionObjectsTrimmed.get(j).getStrId())) {
					redundantRelation = true;
				}
			}
			// TODO TODO TODO
			for (int k = 0; k < domElement.getInput().size(); k ++) {
				if (domAccessTypeObjects.get(i).getClass().equals(domElement.getInput().get(k).getClass()) &&
						accessFunctionObjects.get(i).getStrId().equals(domElement.getInput().get(k).getInput().getStrId())) {
					redundantRelation = true;
				}
			}
			// check redundancy in element's outputs
			for (int l = 0; l < domElement.getOutput().size(); l ++) {
				if (domAccessTypeObjects.get(i).getClass().equals(domElement.getOutput().get(l).getClass()) &&
						accessFunctionObjects.get(i).getStrId().equals(domElement.getOutput().get(l).getOutput().getStrId())) {
					redundantRelation = true;
				}
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
			}
			else {
				functionsByName.put(f.getStrId(), f);
			}

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

		edges.addAll(accessTypeObjectsTrimmed); // TODO
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
/********************
		System.out.println("# of elements: " + domElementsById.values().size());
		System.out.println("# of functions: " + functionsByName.values().size());
		System.out.println("# of accesses: " + edges.size());
		for (InteractionEdge e : edges) {
			System.out.println("<" + e.getStrId() + ">" + " from " + "<" + e.getInput().getStrId() + "> to <" + e.getOutput().getStrId() + ">");
		}
**********************/		
		Collection<Function> functions = functionsByName.values();
		
		ArrayList<String> allPaths = new ArrayList<String>();
		
		for (Function f : functions) {
			allPaths.add(dfsFindImpactPath(f, f.getStrId()));
			// Reset visited flags after traversal for each function
			resetVisitedFlags();
		}
		
		System.out.println("numOfEdges: " + edges.size());
		System.out.println("numOfElements: " + domElementsById.size());
		System.out.println("numOfFunctions: " + functionsByName.size());
/*
		for (InteractionEdge e : edges)
			System.out.println(e.getStrId() + " from " + e.getInput().getStrId() + " to " + e.getOutput().getStrId());
*/		
		
		System.out.println("((((((((((((()))))))))))))");
		System.out.println(allPaths.size());
		StringTokenizer tokenizer;
		int functionCounter = 0;
		for (String s : allPaths) {
			System.out.println("function #: " + functionCounter ++);
			int pathCounter = 0;
			tokenizer = new StringTokenizer(s, "/");
			while (tokenizer.hasMoreTokens()) {
				System.out.println("path #: " + pathCounter ++);
				String path = tokenizer.nextToken();
				StringTokenizer tkn = new StringTokenizer(path, ",");
				int nodeCounter = 0;
				while (tkn.hasMoreTokens()) {
					nodeCounter ++;
					tkn.nextToken();
				}
				System.out.println("has " + nodeCounter + " nodes");
			}
			System.out.println(s);
			System.out.println("--------------------");
			break; // TODO
		}
		
		/*
		for (String s : allPaths) {
			System.out.println(allPaths);
		}
		*/
//		System.out.println("((((((((((((()))))))))))))");
	}
	
	protected String dfsFindImpactPath(InteractionNode node, String paths) {
		if (node == null || node.isVisited()) // necessary?
			return "";//paths;
		
		String newPaths = paths;
		
		for (InteractionEdge e : node.getOutput()) {
			if (!e.isVisited()) {
				InteractionNode next = e.getOutput();
				if (next != null && !next.isVisited()) {
					e.setVisited(true);
					newPaths = newPaths + "/" + dfsFindImpactPath(next, paths + "," + next.getStrId());
				}
			}
		}
		
		node.setVisited(true);
		return newPaths;
///////		System.out.println(">>> " + levelCounter + " <<<");
	}
	
	protected void resetVisitedFlags() {
		// TODO
		for (InteractionEdge e : edges)
			e.setVisited(false);
		for (Function f : functionsByName.values())
			f.setVisited(false);
		for (DomElement el : domElementsById.values())
			el.setVisited(false);
	}
	
	protected void findPathsBetweenFunctionPairs() {
		for (Function f : functionsByName.values()) {
			ArrayList<String> paths = new ArrayList<String>();
			findAllPaths(f, f.getStrId(), paths);
			
			int maxPathLength = 0;
			String maxPath = "";
			for (String path : paths) {
//				System.out.println(path);
				StringTokenizer tkn = new StringTokenizer(path, ",");
				int nodeCounter = 0;
				while (tkn.hasMoreTokens()) {
					nodeCounter ++;
					tkn.nextToken();
				}
				if (nodeCounter > maxPathLength) {
					maxPathLength = nodeCounter;
					maxPath = path;
				}
//				System.out.println("has " + nodeCounter + " nodes");
			}
			System.out.println(maxPathLength + ") max path: " + maxPath);
			System.out.println("-----------------");
			resetVisitedFlags();
		}
		/*
		for (Function n1 : functionsByName.values()) {
			for (Function n2 : functionsByName.values()) {
				if (!n1.getStrId().equals(n2.getStrId())) {
				System.out.println(n1.getStrId() + " ------------ " + n2.getStrId());
				ArrayList<Integer> counters = new ArrayList<Integer>();
				ArrayList<String> paths = new ArrayList<String>();
				findMaxPath(n1, n2, 0, "", counters, paths);
				System.out.println("--=========-------------========--");
				String maxPath = "";
				int maxCounter = 0;
				for (int i = 0; i < counters.size(); i ++) {
					if (counters.get(i) > maxCounter) {
						maxCounter = counters.get(i);
						maxPath = paths.get(i);
					}
				}
				System.out.println("max path between <" + n1.getStrId() + "> and <" + n2.getStrId() + "> is: " + maxPath);
				}
			}
		}
		*/
		
	}
	
	protected void findAllPaths(InteractionNode n1, String str, ArrayList<String> paths) {
		if (n1.isVisited()) {
//			System.out.println("* " + str);
			paths.add(str);
			return;
		}
		n1.setVisited(true);
		for (InteractionEdge e : n1.getOutput()) {
			InteractionNode next = e.getOutput();
//			if (!next.isVisited()) {
				findAllPaths(next, str + "," + next.getStrId(), paths);
//			}
//			else
//				System.out.println("* " + str);
		}
//		n1.setVisited(true);
		
/*		if (n1 == null || n1.isVisited()) {
			System.out.println("+++ " + str);
			return;
		}
		for (InteractionEdge e : n1.getOutput()) {
			InteractionNode next = e.getOutput();
			foo(next, str + next.getStrId());
		}
		n1.setVisited(true);
		*/
	}
	
	protected void findMaxPath(InteractionNode n1, InteractionNode n2, int counter, String path, ArrayList<Integer> allCounters, ArrayList<String> allPaths) {
		/*
		if (n1 == null || n1.isVisited())
			return; // TODO
		System.out.println("1 " + n1.getStrId());
		if (n1.getStrId().equals(n2.getStrId())) {
			allCounters.add(counter);
			allPaths.add(path);
			return;
		}
		for (InteractionEdge e : n1.getOutput()) {
			InteractionNode next = e.getOutput();
			System.out.println("2 " + next.getStrId());
			if (next.getStrId().equals(n2.getStrId())) {
				System.out.println("3 " + n2.getStrId());
				allCounters.add(++ counter);
				allPaths.add(path + next.getStrId());
//				return;
			}
			else {
				if (!next.isVisited()) {
					System.out.println("4 " + next.getStrId());
					findMaxPath(next, n2, ++ counter, path + next.getStrId(), allCounters, allPaths);
					next.setVisited(true);
				}
			}
		}
		*/
	}
	
	public void handleDynamicCallGraph(Collection<TraceObject> functionTraces) {
		Stack<Function> functions = new Stack<Function>();
		Stack<TraceObject> functionTraceStack = new Stack<TraceObject>(); // TODO
		
		ArrayList<Function> functionsInDynamicCallGraph = new ArrayList<Function>(); // temp, only to print the results
		
		Iterator<TraceObject> itr = functionTraces.iterator();
		while (itr.hasNext()) {
			TraceObject functionTrace = itr.next();

			// TODO FunctionCall is ignored on purpose since it refers to system functions ??????
			if (functionTrace instanceof FunctionEnter) {
				FunctionEnter functionEnter = (FunctionEnter) functionTrace;
				String name = functionEnter.getTargetFunction();
				String scope = functionEnter.getScopeName();
				
				Function f;
				
				// TODO TODO TODO TODO
				// TODO TODO TODO TODO
				// MAKE THE NAMING STYLES THE SAME - FOR UNDEFINED FUNCTION NAMES IN FUNCTION TRACE ****************
				// TODO TODO TODO TODO
				// TODO TODO TODO TODO
				if (functionsByName.containsKey(name)) {
					f = functionsByName.get(name);
				}
				else {
					f = new Function(name);
					f.setScopeName(scope);
					
					// TODO TODO TODO
					// TODO TODO TODO
					functionsByName.put(name, f);
					// TODO TODO TODO
					// TODO TODO TODO
				}
				
				functionsInDynamicCallGraph.add(f);
				
				functions.push(f);
				functionTraceStack.push(functionEnter);
				// TODO TODO TODO TODO
				// TODO TODO TODO TODO
			}
//			else if (functionTrace instanceof FunctionExit) {
//				String name = ((FunctionExit) functionTrace).getTargetFunction();
//				String scope = ((FunctionExit) functionTrace).getScopeName();			
//			}
			// same treatment for FunctionExit and FunctionReturnStatement
			else if (functionTrace instanceof FunctionExit) {
				if (functions.empty()) {
					System.err.println("ERROR, INTERACTIONGRAPH::HANDLEDYNAMICCALLGRAPH, FUNCTION STACK EMPTP");
					continue; // TODO
				}
				
				Function terminatedFunction = functions.pop();
				FunctionEnter terminatedTrace = (FunctionEnter)functionTraceStack.pop();
				if (!functions.empty()) {
					Function headFunction = functions.peek();
					FunctionEnter headTrace = (FunctionEnter)functionTraceStack.peek();
					
					if (terminatedTrace.getArgs() != null) {
						// TODO TODO CHECK IF THE ACCESS DOES NOT ALREADY EXIST

						CallFunction callAccess = new CallFunction();
						callAccess.setInput(headFunction);
						callAccess.setOutput(terminatedFunction);
						headFunction.addOutput(callAccess);
						terminatedFunction.addInput(callAccess);
					}
				}
				// TODO
			}
			else if (functionTrace instanceof FunctionReturnStatement) {
				if (functions.empty()) {
					System.err.println("ERROR, INTERACTIONGRAPH::HANDLEDYNAMICCALLGRAPH, FUNCTION STACK EMPTP");
					continue; // TODO
				}

				Function terminatedFunction = functions.pop();
				FunctionEnter terminatedTrace = (FunctionEnter)functionTraceStack.pop();
				if (!functions.empty()) {
					Function headFunction = functions.peek();
					FunctionEnter headTrace = (FunctionEnter)functionTraceStack.peek();
					
					if (terminatedTrace.getArgs() != null) {
						// TODO TODO CHECK IF THE ACCESS DOES NOT ALREADY EXIST
						
						CallFunction callAccess = new CallFunction();
						callAccess.setInput(headFunction);
						callAccess.setOutput(terminatedFunction);
						headFunction.addOutput(callAccess);
						terminatedFunction.addInput(callAccess);
					}
					
					// TODO TODO CHECK IF THE ACCESS DOES NOT ALREADY EXIST

					ReturnFunction returnAccess = new ReturnFunction();
					returnAccess.setInput(terminatedFunction);
					returnAccess.setOutput(headFunction);
					headFunction.addInput(returnAccess);
					terminatedFunction.addOutput(returnAccess);
				}
				// TODO
			}
		}
		
		System.out.println("&&&&&&&&&&&&&&&&&&&");
		for (Function f : functionsInDynamicCallGraph)
			System.out.print(f.getStrId() + " -- ");
		System.out.println();
	}

}

