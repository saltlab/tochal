package com.proteus.core.interactiongraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
	
	// TODO this method will be accessed from outside to traverse the graph and find info about dynamic paths
	public void handleGraphAfterTermination() {
		// Gather statistical/topological information about the structure of captured dom relations
		HashMap<DomElement, ArrayList<InteractionEdge>> elementSortedAccessMap = getSortedAccessesForElements();//gatherStatInfo();
		int numOfDomElementsWithUniqueWR = findUniqueFunctionAccess(elementSortedAccessMap); // TODO

		
		// If the size of inputs and outputs are greater or equal to one, we can say that there are read and write accesses made to that node
		int numOfDomElementsOnDynamicPath = findDynamicDOMPaths();
//		System.out.println(">>>>>>>>>>>>>numOfDomElementsOnDynamicPath: " + numOfDomElementsOnDynamicPath);
		System.out.println(">>>>>>>>>>>>>numOfDomElementsWithUniqueWR: " + numOfDomElementsWithUniqueWR);
		System.out.println(">>>>>>>>>>>>>numOfDomElementsAccess: " + domElementsById.size());
		
		findImpactPaths(); // TODO 	SHOULD BE CALLED AT STATIC PHASE, NOT HERE

	}
	
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
			
			System.out.println("++++++++++++++++");
			System.out.println("DOM Element: " + el.getStrId());
			System.out.println("numOfWRPairs: " + numOfWRPairs);
			System.out.println("numOfWRPairsDiffFunctions " + numOfWRPairsDiffFunctions);

			for (int i = 0; i < accesses.size(); i ++) {
				for (int j = 0; j < accesses.size(); j ++)
					System.out.print(directedFunctionAccesses[i][j] + " ");
				System.out.println();
			}
			
			for (int i = 0; i < accesses.size(); i ++)
				for (int j = 0; j < accesses.size(); j ++)
					if (directedFunctionAccesses[i][j] == 1 && i != j) {
						findPaths(accesses, directedFunctionAccesses, i, j, accesses.get(i).getStrId(), accesses.get(i).getFunctionNode().getStrId());
					}
		}
		
		return numOfDomElementsWithUniqueWR;
	}
	
	protected void findPaths(ArrayList<InteractionEdge> accesses, int [][]directedFunctionAccesses, int i, int j, String pathAccesses, String pathFunctions) {
		for (int a = 0; a < accesses.size(); a ++)
			if (directedFunctionAccesses[j][a] == 1 && a != j)
				findPaths(accesses, directedFunctionAccesses, j, a, pathAccesses + "," + accesses.get(j).getStrId(), pathFunctions + "," + accesses.get(j).getFunctionNode().getStrId());
		directedFunctionAccesses[i][j] = 2;
		System.out.println("================ pathAccesses:: " + pathAccesses);
		System.out.println("================ pathFunctions:: " + pathFunctions);
	}
	
	/**
	 * Creates the relations between DOM elements and JavaScript functions
	 * Extracts the paths // TODO
	 * @param domRelations
	 */
	public void handleDomRelations(String domRelations) {
		extractDomRelations(domRelations);
		
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
/*				System.out.println("------------------DOM Element id: " + el.getStrId());
				for (int i = 0; i < sortedHybridList.size(); i ++) {
					InteractionEdge e = sortedHybridList.get(i);
					if (e instanceof ReadAccess)
						System.out.println("F: " + sortedHybridList.get(i).getOutput().getStrId() + " + READ: ");//) + sortedHybridList.get(i).getClass().getSimpleName());
					else
						System.out.println("F: " + sortedHybridList.get(i).getInput().getStrId() + " + WRITE: ");// + sortedHybridList.get(i).getClass().getSimpleName());
				}
*/			}
			
/////////////////////			System.out.println("33333333333 " + el.getStrId() + " 33333333333 " + sortedHybridList.toString());
			domElementsWithSortedAcesses.put(el, sortedHybridList); // TODO

		}

		Iterator it = domElementsWithSortedAcesses.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<DomElement, ArrayList<InteractionEdge>> pairs = (Map.Entry<DomElement, ArrayList<InteractionEdge>>)it.next();
			DomElement e = pairs.getKey();
			ArrayList<InteractionEdge> sortedAccesses = pairs.getValue();
			
			System.out.println("========================== DOM Element: " + e.getStrId());
			for (int i = 0; i < sortedAccesses.size(); i ++) {
				InteractionEdge access = sortedAccesses.get(i);
				if (access instanceof WriteAccess)
					System.out.print("<W, " + access.getInput().getStrId() + "> ");
				else
					System.out.print("<R, " + access.getOutput().getStrId() + "> ");
			}
			System.out.println();
			
////////////////////////			it.remove(); // TODO
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
			if (!redundantRelation) {
				accessTypeObjectsTrimmed.add(domAccessTypeObjects.get(i));
				accessFunctionObjectsTrimmed.add(accessFunctionObjects.get(i));
			}
		}
		
/*		System.out.println("============");
		System.out.println("accessTypeObjectsTrimmed.size(): " + accessTypeObjectsTrimmed.size());
		System.out.println("dom relations: " + domRelations);
		System.out.println("trimmed accessTypeObjectsTrimmed: " + accessTypeObjectsTrimmed.toString());
		System.out.println("trimmed accessFunctionObjectsTrimmed: " + accessFunctionObjectsTrimmed.toString());
		System.out.println("============");
*/		
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
			
/*			System.out.println("------------------------------------------------------------");
			System.out.println("domAccess: " + da.getClass());
*/			
			if (da instanceof WriteAccess) {
				da.setInput(f);
				da.setOutput(domElement);
				domElement.addInput(da);
				f.addOutput(da);
				
/*				System.out.println("write access");
				System.out.println("input from function: " + f.getStrId());
				System.out.println("all f outputs: " + f.getOutput().toString());
				System.out.println("output to dom element: " + domElement.getStrId());
				System.out.println("all dom el inputs: " + domElement.getInput().toString());
*/			}
			else if (da instanceof ReadAccess) {
				da.setInput(domElement);
				da.setOutput(f);
				domElement.addOutput(da);
				f.addInput(da);
				
/*				System.out.println("read access");
				System.out.println("output to function: " + f.getStrId());
				System.out.println("all f inputs: " + f.getInput().toString());
				System.out.println("input from dom element: " + domElement.getStrId());
				System.out.println("all dom el outputs: " + domElement.getOutput().toString());
*/			}
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
///////		System.out.println(">>> " + levelCounter + " <<<");
	}

}
