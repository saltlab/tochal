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
import com.proteus.core.trace.DOMElement;
import com.proteus.core.trace.DOMEventTrace;
import com.proteus.core.trace.DOMMutationTrace;
import com.proteus.core.trace.FunctionCall;
import com.proteus.core.trace.FunctionEnter;
import com.proteus.core.trace.FunctionExit;
import com.proteus.core.trace.FunctionReturnStatement;
import com.proteus.core.trace.TraceObject;
import com.proteus.instrument.FunctionTrace;
import com.proteus.stats.Statistics;

public class InteractionGraph {
	// InteractionGraph is a singleton
	private static final InteractionGraph INSTANCE = new InteractionGraph();

	private HashMap<String, Function> functionsByName;
	private HashMap<String, DomElement> domElementsById;
	private HashMap<String, XmlHttpRequest> xhrsById;
	
	private HashMap<String, Boolean> staticFunctions;
	private HashMap<String, Boolean> dynamicFunctions;

	private ArrayList<InteractionEdge> edges;

	// Private constructor for singleton
	private InteractionGraph() {
		functionsByName = new HashMap<String, Function>();
		domElementsById = new HashMap<String, DomElement>();
		xhrsById = new HashMap<String, XmlHttpRequest>();

		edges = new ArrayList<InteractionEdge>();
		
		staticFunctions = new HashMap<String, Boolean>();
		dynamicFunctions = new HashMap<String, Boolean>();
	}
	
	public HashMap<String, Boolean> getStaticFunctions() {
		return this.staticFunctions;
	}
	
	public HashMap<String, Boolean> getDynamicFunctions() {
		return this.dynamicFunctions;
	}

	// Get the instance of singleton InteractionGraph
	public static InteractionGraph getInstance() {
		return INSTANCE;
	}

	public HashMap<String, Function> getFunctions() {
		return this.functionsByName;
	}

	// This method will be accessed from outside to traverse the graph and
	// Finding info about dynamic paths
	public void handleGraphAfterTermination() {

		System.out.println("1) " + domElementsById.size());
		System.out.println("2) " + functionsByName.size());

		// Gather statistical/topological information about the structure of
		// captured dom relations
		HashMap<DomElement, ArrayList<InteractionEdge>> elementSortedAccessMap = getSortedAccessesForElements();// gatherStatInfo();
		int numOfDomElementsWithUniqueWR = findUniqueFunctionAccess(elementSortedAccessMap); // TODO

		// If the size of inputs and outputs are greater or equal to one, we can say that there are read and write accesses made to that node
		int numOfDomElementsOnDynamicPath = findDynamicDOMPaths();
		// System.out.println("numOfDomElementsOnDynamicPath: " + numOfDomElementsOnDynamicPath);
		System.out.println("numOfDomElementsWithUniqueWR: " + numOfDomElementsWithUniqueWR);
		System.out.println("numOfDomElementsAccess: " + domElementsById.size());

		// findImpactPaths();
		findPathsBetweenFunctionPairs();
		findTopologicalGraphCharacteristics();
		findFunctionDynamicCallInformation();
		findXHRInformation();
	}

	protected void findFunctionDynamicCallInformation() {
		matchStaticDynamicFunctionArities();
		matchDynamicFunctionReturns();
	}

	private void matchStaticDynamicFunctionArities() {
		int numOfFunctions = functionsByName.size();
		int numOfFunctionsWOArgs = 0;
		int numOfFunctionsWithDiffArity = 0;
		int numOfFunctionsWithDiffStaticArity = 0;
		int numOfStaticallyUndetectedFunctions = 0;
		// all functions and all their arguments during execution
		for (Iterator<Function> itr = functionsByName.values().iterator(); itr
				.hasNext();) {
			Function f = itr.next();

			boolean staticDynamicArityMismatch = false;

			if (FunctionTrace.functionParamNum.get(f.getStrId()) != null) {
				int staticParamCount = FunctionTrace.functionParamNum.get(f
						.getStrId());
				if (f.getArgsOverTime().isEmpty()) {
					numOfFunctionsWOArgs++;
				} else {
					boolean allArgsEmpty = true;
					int prevNumOfArgs = 0, currNumOfArgs = 0;
					boolean allArgsArityMatch = true;
					if (f.getArgsOverTime().size() > 0) {
						prevNumOfArgs = getNumOfArgs(f.getArgsOverTime().get(0));
						currNumOfArgs = prevNumOfArgs;
					}
					for (String args : f.getArgsOverTime()) {
						if (!args.isEmpty() && !args.equals("[]")) {
							allArgsEmpty = false;
							currNumOfArgs = getNumOfArgs(args);
							if (currNumOfArgs != prevNumOfArgs) {
								allArgsArityMatch = false;
								if (currNumOfArgs != staticParamCount)
									staticDynamicArityMismatch = true;
							}
							prevNumOfArgs = currNumOfArgs;
						}
					}
					if (allArgsEmpty) {
						numOfFunctionsWOArgs++;
					} else { // Function has arguments
						if (!allArgsArityMatch)
							numOfFunctionsWithDiffArity++;

						if (staticDynamicArityMismatch)
							numOfFunctionsWithDiffStaticArity++;
					}

				}
			}
			else {
				System.out.println("Function not detected statically");
				numOfStaticallyUndetectedFunctions ++;
			}
		}

		System.out.println("numOfFunctions: " + numOfFunctions);
		System.out.println("numOfFunctionsWOArgs: " + numOfFunctionsWOArgs);
		System.out.println("numOfFunctionsWithDiffArity: "
				+ numOfFunctionsWithDiffArity);
		System.out.println("numOfFunctionsWithDiffStaticArity: "
				+ numOfFunctionsWithDiffStaticArity);
		System.out.println("numOfStaticallyUndetectedFunctions: " + numOfStaticallyUndetectedFunctions);
	}

	private void matchDynamicFunctionReturns() {
		int numOfRetValMismatches = 0;

		// all functions and all their return values during execution
		for (Iterator<Function> itr = functionsByName.values().iterator(); itr.hasNext();) {
			Function f = itr.next();
			// System.out.println(f.getReturnValues());
			if (!f.getReturnValues().isEmpty() && f.isCalledWOReturnValue()) {
				System.out.println("RETURN VALUE MISMATCH");
				numOfRetValMismatches++;
			}
		}
		System.out.println("numOfRetValMismatches: " + numOfRetValMismatches);
	}

	private int getNumOfArgs(String args) {
		int numOfArgs = 0;
		StringTokenizer tokenizer = new StringTokenizer(args, "{");
		while (tokenizer.hasMoreTokens()) {
			tokenizer.nextToken();
			numOfArgs++;
		}
		return numOfArgs;
	}

	protected void findXHRInformation() {
		int numOfXhrsWDiffInput = 0, numOfXhrsWDiffOutput = 0, numOfXhrsWDiffInOut = 0;
		for (Iterator<XmlHttpRequest> itr = xhrsById.values().iterator(); itr
				.hasNext();) {
			XmlHttpRequest xhr = itr.next();

			ArrayList<InteractionEdge> inputs = xhr.getInput();
			ArrayList<InteractionEdge> outputs = xhr.getOutput();
			
			if (inputs.size() > 0) {
				String prevInput = inputs.get(0).getInput().getStrId();
				String currInput = prevInput;
				for (InteractionEdge e : inputs) {
					currInput = e.getInput().getStrId();
					if (!currInput.equals(prevInput)) {
						numOfXhrsWDiffInOut ++;
						break;
					}
					prevInput = currInput;
				}
			}

			if (outputs.size() > 0) {
				String prevOutput = outputs.get(0).getOutput().getStrId();
				String currOutput = prevOutput;
				for (InteractionEdge e : outputs) {
					currOutput = e.getOutput().getStrId();
					if (!currOutput.equals(prevOutput)) {
						numOfXhrsWDiffOutput ++;
						break;
					}
					prevOutput = currOutput;
				}
			}
			
			ArrayList<InteractionEdge> allEdges = new ArrayList<InteractionEdge>(inputs);
			allEdges.addAll(outputs);
			if (allEdges.size() > 0) {
				String prev = "", curr = "";
				if (allEdges.get(0) instanceof ReadAccess) {
					prev = allEdges.get(0).getOutput().getStrId();
				}
				else {
					prev = allEdges.get(0).getInput().getStrId();
				}
				curr = prev;
				for (InteractionEdge e : allEdges) {
					if (e instanceof ReadAccess) {
						curr = e.getOutput().getStrId();
					}
					else {
						curr = e.getInput().getStrId();
					}
					if (!curr.equals(prev)) {
						numOfXhrsWDiffInOut ++;
						break;
					}
				}
			}
		}
		
		System.out.println("numOfXhrsWDiffInput: " + numOfXhrsWDiffInput);
		System.out.println("numOfXhrsWDiffOutput: " + numOfXhrsWDiffOutput);
		System.out.println("numOfXhrsWDiffInOut: " + numOfXhrsWDiffInOut);
	}

	protected void findTopologicalGraphCharacteristics() {
		System.out.println("DOM element input sizes");
		double[] domInputSizes = new double[domElementsById.values().size()];
		int counter = 0;
		for (DomElement el : domElementsById.values()) {
			System.out.print(el.getInput().size() + ", ");
			domInputSizes[counter++] = el.getInput().size();
		}
		System.out.println();
		System.out.println("SIZE: " + domElementsById.size());
		System.out.println("AVG: " + Statistics.getMean(domInputSizes));
		System.out.println("MED: " + Statistics.getMedian(domInputSizes));
		System.out.println("VAR: " + Statistics.getVariance(domInputSizes));
		System.out.println("STD-DEV: " + Statistics.getStdDev(domInputSizes));

		System.out.println("DOM element output sizes");
		counter = 0;
		double[] domOutputSizes = new double[domElementsById.values().size()];
		for (DomElement el : domElementsById.values()) {
			System.out.print(el.getOutput().size() + ", ");
			domOutputSizes[counter++] = el.getOutput().size();
		}
		System.out.println();
		System.out.println("SIZE: " + domElementsById.size());
		System.out.println("AVG: " + Statistics.getMean(domOutputSizes));
		System.out.println("MED: " + Statistics.getMedian(domOutputSizes));
		System.out.println("VAR: " + Statistics.getVariance(domOutputSizes));
		System.out.println("STD-DEV: " + Statistics.getStdDev(domOutputSizes));

		System.out.println("Function input sizes");
		counter = 0;
		double[] functionInputSizes = new double[functionsByName.values()
				.size()];
		for (Function f : functionsByName.values()) {
			System.out.print(f.getInput().size() + ", ");
			functionInputSizes[counter++] = f.getInput().size();
		}
		System.out.println();
		System.out.println("SIZE: " + functionsByName.size());
		System.out.println("AVG: " + Statistics.getMean(functionInputSizes));
		System.out.println("MED: " + Statistics.getMedian(functionInputSizes));
		System.out
				.println("VAR: " + Statistics.getVariance(functionInputSizes));
		System.out.println("STD-DEV: "
				+ Statistics.getStdDev(functionInputSizes));

		System.out.println("Function output sizes");
		counter = 0;
		double[] functionOutputSizes = new double[functionsByName.values()
				.size()];
		for (Function f : functionsByName.values()) {
			System.out.print(f.getOutput().size() + ", ");
			functionOutputSizes[counter++] = f.getOutput().size();
		}
		System.out.println();
		System.out.println("SIZE: " + functionsByName.size());
		System.out.println("AVG: " + Statistics.getMean(functionOutputSizes));
		System.out.println("MED: " + Statistics.getMedian(functionOutputSizes));
		System.out.println("VAR: "
				+ Statistics.getVariance(functionOutputSizes));
		System.out.println("STD-DEV: "
				+ Statistics.getStdDev(functionOutputSizes));

	}

	protected int findUniqueFunctionAccess(
			HashMap<DomElement, ArrayList<InteractionEdge>> elementSortedAccessMap) {
		int numOfDomElementsWithUniqueWR = 0;

		for (DomElement el : elementSortedAccessMap.keySet()) {
			ArrayList<InteractionEdge> accesses = elementSortedAccessMap
					.get(el);
			ArrayList<Integer> readerFunctions = new ArrayList<Integer>(); // index of the function based on sorted accesses
			ArrayList<Integer> writerFunctions = new ArrayList<Integer>(); // index of the function based on sorted accesses
			int[][] directedFunctionAccesses = new int[accesses.size()][accesses.size()];

			for (int i = 0; i < accesses.size(); i++) {
				InteractionEdge access = accesses.get(i);
				if (access instanceof ReadAccess)
					readerFunctions.add(i);
				else
					writerFunctions.add(i);
			}

			for (int i = 0; i < writerFunctions.size(); i++)
				for (int j = 0; j < readerFunctions.size(); j++)
					directedFunctionAccesses[writerFunctions.get(i)][readerFunctions
							.get(j)] = 1;
			// directedFunctionAccesses[i][j] = 1;

			int numOfWRPairs = 0;
			int numOfWRPairsDiffFunctions = 0;

			for (int i = 0; i < accesses.size(); i++)
				for (int j = 0; j < accesses.size(); j++)
					if (directedFunctionAccesses[i][j] == 1) {
						numOfWRPairs++;
						if (i != j)
							numOfWRPairsDiffFunctions++;
					}

			if (numOfWRPairsDiffFunctions > 0)
				numOfDomElementsWithUniqueWR++;

			for (int i = 0; i < accesses.size(); i++)
				for (int j = 0; j < accesses.size(); j++)
					if (directedFunctionAccesses[i][j] == 1 && i != j) {
						findPaths(accesses, directedFunctionAccesses, i, j,
								accesses.get(i).getStrId(), accesses.get(i)
										.getFunctionNode().getStrId(), 1);
					}
		}

		return numOfDomElementsWithUniqueWR;
	}

	protected void findPaths(ArrayList<InteractionEdge> accesses,
			int[][] directedFunctionAccesses, int i, int j,
			String pathAccesses, String pathFunctions, int numOfAccesses) {
		for (int a = 0; a < accesses.size(); a++)
			if (directedFunctionAccesses[j][a] == 1 && a != j)
				findPaths(accesses, directedFunctionAccesses, j, a,
						pathAccesses + "," + accesses.get(j).getStrId(),
						pathFunctions + ","
								+ accesses.get(j).getFunctionNode().getStrId(),
						numOfAccesses + 1);
		directedFunctionAccesses[i][j] = 2;
	}

	/**
	 * Creates the relations between DOM elements and JavaScript functions
	 * Extracts the paths // TODO
	 * 
	 * @param domRelations
	 */
	public void handleDomRelations(String domRelations) {
		extractDomRelations(domRelations);
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
				numOfReadOnlyDomElements++;
			else if (el.getOutput().size() == 0)
				numOfWriteOnlyDomElements++;
			else {
				// Create a list of all inputs and outputs sorted by the counter
				numOfReadWriteElements++;
				ArrayList<InteractionEdge> input = el.getInput();
				ArrayList<InteractionEdge> output = el.getOutput();
				// sort interaction edges based on time

				int inputIndex = 0;
				int outputIndex = 0;

				while (true) {
					if (input.get(inputIndex).getCounter() <= output.get(
							outputIndex).getCounter()) {
						sortedHybridList.add(input.get(inputIndex));
						inputIndex++;
					} else {
						sortedHybridList.add(output.get(outputIndex));
						outputIndex++;
					}
					if (inputIndex >= (input.size() - 1)) {
						for (int i = outputIndex; i < output.size(); i++)
							sortedHybridList.add(output.get(i));
						break;
					} else if (outputIndex >= (output.size() - 1)) {
						for (int i = inputIndex; i < input.size(); i++)
							sortedHybridList.add(input.get(i));
						break;
					}
				}
			}

			domElementsWithSortedAcesses.put(el, sortedHybridList); // TODO

		}

		Iterator it = domElementsWithSortedAcesses.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<DomElement, ArrayList<InteractionEdge>> pairs = (Map.Entry<DomElement, ArrayList<InteractionEdge>>) it.next();
			DomElement e = pairs.getKey();
			ArrayList<InteractionEdge> sortedAccesses = pairs.getValue();
		}

		System.out.println("numOfReadOnlyDomElements: " + numOfReadOnlyDomElements);
		System.out.println("numOfWriteOnlyDomElements: " + numOfWriteOnlyDomElements);
		System.out.println("numOfReadWriteElements: " + numOfReadWriteElements);

		return domElementsWithSortedAcesses;
	}

	protected int findDynamicDOMPaths() {
		int numOfDomElementsOnDynamicPath = 0;

		for (Map.Entry<String, DomElement> entry : domElementsById.entrySet()) {
			DomElement el = entry.getValue();
			if (el.getInput().size() >= 1 && el.getOutput().size() >= 1) {
				numOfDomElementsOnDynamicPath++;
			}
		}

		return numOfDomElementsOnDynamicPath;
	}

	/**
	 * Extracts the relations between DOM elements and JavaScript functions from
	 * the received log
	 * 
	 * @param domRelations
	 */
	protected void extractDomRelations(String domRelations) {
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
		for (int i = 0; i < 2; i++)
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
		} else {
			System.err.println("DomAccessHandler::makeAccessRelations -> NO ID");
		}

		// Extract string representations of accessTypes (interactionEdges)
		tokenizer = new StringTokenizer(accessTypeList);
		if (tokenizer.hasMoreTokens())
			tokenizer.nextToken("@");
		while (tokenizer.hasMoreTokens())
			accessTypes.add(tokenizer.nextToken(",@"));

		// Extract string representations of functions
		tokenizer = new StringTokenizer(accessFunctionList);
		if (tokenizer.hasMoreTokens())
			tokenizer.nextToken("@");
		while (tokenizer.hasMoreTokens())
			accessFunctions.add(tokenizer.nextToken(",@"));

		// Create accessType objects for DOM relations
		ArrayList<InteractionEdge> domAccessTypeObjects = new ArrayList<InteractionEdge>();
		for (int i = 0; i < accessTypes.size(); i++) {
			String accessType = accessTypes.get(i);
			String accessTypeClassName = "com.proteus.core.interactiongraph.edge."
					+ accessType;
			try {
				InteractionEdge domAccess = (InteractionEdge) Class.forName(
						accessTypeClassName).newInstance(); // /////
				domAccessTypeObjects.add(domAccess); //
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		// Create accessFunction objects
		ArrayList<Function> accessFunctionObjects = new ArrayList<Function>();
		for (int i = 0; i < accessFunctions.size(); i++) {
			String accessFunction = accessFunctions.get(i);
			Function function = new Function("null");
			if (accessFunction.equals("null")) {
			} else {
				function.setStrId(accessFunction);
			}
			accessFunctionObjects.add(function);
		}

		if (domAccessTypeObjects.size() != accessFunctionObjects.size()) {
			System.err.println("ERROR: InteractionGraph::makeAccessRelations -> number of access types and functions does not match");
			return; 
		}

		ArrayList<InteractionEdge> accessTypeObjectsTrimmed = new ArrayList<InteractionEdge>();
		ArrayList<Function> accessFunctionObjectsTrimmed = new ArrayList<Function>();

		for (int i = 0; i < domAccessTypeObjects.size(); i++) {
			boolean redundantRelation = false;
			for (int j = 0; j < accessTypeObjectsTrimmed.size(); j++) {
				if (domAccessTypeObjects.get(i).getClass()
						.equals(accessTypeObjectsTrimmed.get(j).getClass())
						&& accessFunctionObjects
								.get(i)
								.getStrId()
								.equals(accessFunctionObjectsTrimmed.get(j)
										.getStrId())) {
					redundantRelation = true;
				}
			}
			// TODO TODO TODO
			for (int k = 0; k < domElement.getInput().size(); k++) {
				if (domAccessTypeObjects.get(i).getClass()
						.equals(domElement.getInput().get(k).getClass())
						&& accessFunctionObjects
								.get(i)
								.getStrId()
								.equals(domElement.getInput().get(k).getInput()
										.getStrId())) {
					redundantRelation = true;
				}
			}
			// check redundancy in element's outputs
			for (int l = 0; l < domElement.getOutput().size(); l++) {
				if (domAccessTypeObjects.get(i).getClass()
						.equals(domElement.getOutput().get(l).getClass())
						&& accessFunctionObjects
								.get(i)
								.getStrId()
								.equals(domElement.getOutput().get(l)
										.getOutput().getStrId())) {
					redundantRelation = true;
				}
			}

			if (!redundantRelation) {
				accessTypeObjectsTrimmed.add(domAccessTypeObjects.get(i));
				accessFunctionObjectsTrimmed.add(accessFunctionObjects.get(i));
			}
		}

		for (int i = 0; i < accessTypeObjectsTrimmed.size(); i++) {
			InteractionEdge da = accessTypeObjectsTrimmed.get(i);
			Function f = accessFunctionObjectsTrimmed.get(i);
			if (functionsByName.containsKey(f.getStrId())) {
				f = functionsByName.get(f.getStrId());
			} else {
				functionsByName.put(f.getStrId(), f);
			}

			if (da instanceof WriteAccess) {
				da.setInput(f);
				da.setOutput(domElement);
				domElement.addInput(da);
				f.addOutput(da);
			} else if (da instanceof ReadAccess) {
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
	 * 
	 * @param xhrRelations
	 */
	public void handleXhrRelations(String xhrRelations) {
		extractXhrRelations(xhrRelations);
		// extractXhrJsPaths();
	}

	protected void extractXhrRelations(String xhrRelations) {
		String xhrAccessType = "", xhrObjId = "", xhrAccessFunction = "";
		StringTokenizer tokenizer = new StringTokenizer(xhrRelations);

//		System.out.println("=>=>=>==== " + xhrRelations);

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

			if (xhrAccessType.equals("OpenAccess")) {
				OpenXhr openXhrAccess = new OpenXhr();

				openXhrAccess.setInput(function);
				openXhrAccess.setOutput(xhr);
				xhr.addInput(openXhrAccess);
				function.addOutput(openXhrAccess);
			} else if (xhrAccessType.equals("SendAccess")) {
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
			} else if (xhrAccessType.equals("ResponseAccess")) {
				ReceiveXhrResponse responseAccess = new ReceiveXhrResponse();

				responseAccess.setInput(xhr);
				responseAccess.setOutput(function);
				xhr.addOutput(responseAccess);
				function.addInput(responseAccess);
			}
		}

	}

	public void findImpactPaths() {
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

		System.out.println(allPaths.size());
		StringTokenizer tokenizer;
		int functionCounter = 0;
		for (String s : allPaths) {
			System.out.println("function #: " + functionCounter++);
			int pathCounter = 0;
			tokenizer = new StringTokenizer(s, "/");
			while (tokenizer.hasMoreTokens()) {
				System.out.println("path #: " + pathCounter++);
				String path = tokenizer.nextToken();
				StringTokenizer tkn = new StringTokenizer(path, ",");
				int nodeCounter = 0;
				while (tkn.hasMoreTokens()) {
					nodeCounter++;
					tkn.nextToken();
				}
				System.out.println("has " + nodeCounter + " nodes");
			}
			System.out.println(s);
			break;
		}

	}

	protected String dfsFindImpactPath(InteractionNode node, String paths) {
		if (node == null || node.isVisited()) // necessary?
			return "";// paths;

		String newPaths = paths;

		for (InteractionEdge e : node.getOutput()) {
			if (!e.isVisited()) {
				InteractionNode next = e.getOutput();
				if (next != null && !next.isVisited()) {
					e.setVisited(true);
					newPaths = newPaths
							+ "/"
							+ dfsFindImpactPath(next,
									paths + "," + next.getStrId());
				}
			}
		}

		node.setVisited(true);
		return newPaths;
	}

	protected void resetVisitedFlags() {
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
				// System.out.println(path);
				StringTokenizer tkn = new StringTokenizer(path, ",");
				int nodeCounter = 0;
				while (tkn.hasMoreTokens()) {
					nodeCounter++;
					tkn.nextToken();
				}
				if (nodeCounter > maxPathLength) {
					maxPathLength = nodeCounter;
					maxPath = path;
				}
				// System.out.println("has " + nodeCounter + " nodes");
			}
			System.out.println(maxPathLength + ") max path: " + maxPath);
			resetVisitedFlags();
		}
	}

	protected void findAllPaths(InteractionNode n1, String str,
			ArrayList<String> paths) {
		if (n1.isVisited()) {
			paths.add(str);
			return;
		}
		n1.setVisited(true);
		for (InteractionEdge e : n1.getOutput()) {
			InteractionNode next = e.getOutput();
			// if (!next.isVisited()) {
			findAllPaths(next, str + "," + next.getStrId(), paths);
			// }
			// else
			// System.out.println("* " + str);
		}
		// n1.setVisited(true);
	}

	protected void findMaxPath(InteractionNode n1, InteractionNode n2,
			int counter, String path, ArrayList<Integer> allCounters,
			ArrayList<String> allPaths) {
	}	

	public void handleDomEvents(Collection<TraceObject> domEventTraces) {
		int numOfPropagations = 0;
		System.out.println("# of DOM events: " + domEventTraces.size());

		long prevTimestamp = -50;
		boolean propagationOn = false;
		for (TraceObject o : domEventTraces) {
			if (o instanceof DOMMutationTrace)
				continue;
			DOMEventTrace domEvent = (DOMEventTrace)o;
//			int currCounter = domEvent.getCounter();
			long currTimestamp = domEvent.getTimeStamp();
			if (currTimestamp - prevTimestamp < 20) {
//			if (currCounter - prevCounter == 1) {
//				if (propagationOn) {
					System.out.println("PROPAGATION! " + currTimestamp);
					numOfPropagations ++;
//				}
//				propagationOn = true;
			}
			prevTimestamp = currTimestamp;
		}
		System.out.println("Num of propagations: " + numOfPropagations);
	}

	public void handleDynamicCallGraph(Collection<TraceObject> functionTraces) {
		System.out.println("functionTraces.size(): " + functionTraces.size());
		Stack<Function> functions = new Stack<Function>();
		Stack<TraceObject> functionTraceStack = new Stack<TraceObject>();

		ArrayList<String> callGraphPairs = new ArrayList<String>();

		Iterator<TraceObject> itr = functionTraces.iterator();
		while (itr.hasNext()) {
			TraceObject functionTrace = itr.next();

			// FunctionCall is ignored on purpose since it refers to system
			if (functionTrace instanceof FunctionEnter) {
				FunctionEnter functionEnter = (FunctionEnter) functionTrace;
				String name = functionEnter.getTargetFunction();
				String scope = functionEnter.getScopeName();
				String args = functionEnter.getArgs(); // TODO

				Function f;

				// make the naming style the same - for anonymous functions
				if (functionsByName.containsKey(name)) {
					f = functionsByName.get(name);
				} else {
					f = new Function(name);
					f.setScopeName(scope);

					functionsByName.put(name, f);
				}

				f.addArgs(args);

				functions.push(f);
				functionTraceStack.push(functionEnter);
				
				InteractionGraph.getInstance().getDynamicFunctions().put(name, true);
			}
			// Same treatment for FunctionExit and FunctionReturnStatement
			else if (functionTrace instanceof FunctionExit) {
				if (functions.empty()) {
					System.err
							.println("ERROR, INTERACTIONGRAPH::HANDLEDYNAMICCALLGRAPH, FUNCTION STACK EMPTP");
					continue;
				}

				Function terminatedFunction = functions.pop();
				FunctionEnter terminatedTrace = (FunctionEnter) functionTraceStack
						.pop();

				terminatedFunction.setCalledWOReturnValue(true);

				if (!functions.empty()) {
					Function headFunction = functions.peek();
					FunctionEnter headTrace = (FunctionEnter) functionTraceStack
							.peek();

					if (terminatedTrace.getArgs() != null) {
						// Check if the access does not already exist
						if (!functionCallAccessExists(headFunction,
								terminatedFunction)) {
							CallFunction callAccess = new CallFunction();
							callAccess.setInput(headFunction);
							callAccess.setOutput(terminatedFunction);
							headFunction.addOutput(callAccess);
							terminatedFunction.addInput(callAccess);

							callGraphPairs.add(headFunction.getStrId() + " -> " + terminatedFunction.getStrId());

						}
					}
				}
			} else if (functionTrace instanceof FunctionReturnStatement) {
				if (functions.empty()) {
					System.err.println("ERROR, INTERACTIONGRAPH::HANDLEDYNAMICCALLGRAPH, FUNCTION STACK EMPTY");
					continue;
				}

				FunctionReturnStatement returnStatement = (FunctionReturnStatement) functionTrace; // TODO

				Function terminatedFunction = functions.pop();
				FunctionEnter terminatedTrace = (FunctionEnter) functionTraceStack
						.pop();

				terminatedFunction.addReturnValue(returnStatement.getValue());

				if (!functions.empty()) {
					Function headFunction = functions.peek();
					FunctionEnter headTrace = (FunctionEnter) functionTraceStack.peek();

					if (terminatedTrace.getArgs() != null) {
						// Check if the access does not already exist
						if (!functionCallAccessExists(headFunction,
								terminatedFunction)) {
							CallFunction callAccess = new CallFunction();
							callAccess.setInput(headFunction);
							callAccess.setOutput(terminatedFunction);
							headFunction.addOutput(callAccess);
							terminatedFunction.addInput(callAccess);

							callGraphPairs.add(headFunction.getStrId() + " -> " + terminatedFunction.getStrId());

						}
					}

					// Check if the access does not already exist
					if (!returnFunctionAccessExists(terminatedFunction,
							headFunction)) {
						ReturnFunction returnAccess = new ReturnFunction();
						returnAccess.setInput(terminatedFunction);
						returnAccess.setOutput(headFunction);
						headFunction.addInput(returnAccess);
						terminatedFunction.addOutput(returnAccess);

						callGraphPairs.add(terminatedFunction.getStrId() + " -> " + headFunction.getStrId());

					}
				}
			}
		}

		for (String s : callGraphPairs)
			System.out.print(s + " -- ");
		System.out.println();
	}
	
	private void resetRanks() {
		Iterator it = domElementsById.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, DomElement> pairs = (Map.Entry<String, DomElement>)it.next();
			pairs.getValue().setRank(0);
		}
		
		it = functionsByName.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, DomElement> pairs = (Map.Entry<String, DomElement>)it.next();
			pairs.getValue().setRank(0);
		}

		it = xhrsById.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, DomElement> pairs = (Map.Entry<String, DomElement>)it.next();
			pairs.getValue().setRank(0);
		}
	}
	
	protected void determineRanks(InteractionNode node) {
		resetRanks();
		
		rankNode(node, 0, 0);
	}
	
	protected int rankNode(InteractionNode node, int distance, int len) {
		for (InteractionEdge e : node.getOutput()) {
			InteractionNode next = e.getOutput();
			int l = rankNode(next, distance ++, len ++);
			double f = 0.5 * node.getInput().size();
			if (node instanceof Function)
				f += (0.5 * node.getOutput().size());
			node.setRank(next.getRank() * l * f / distance);
		}
		return len;
	}

	private boolean functionCallAccessExists(Function f1, Function f2) {
		boolean accessExists = false;
		for (InteractionEdge edge : f1.getOutput()) {
			if (edge instanceof CallFunction
					&& edge.getOutput().getStrId().equals(f2.getStrId()))
				accessExists = true;
		}
		return accessExists;
	}

	private boolean returnFunctionAccessExists(Function f1, Function f2) {
		boolean accessExists = false;
		for (InteractionEdge e : f1.getOutput()) {
			if (e instanceof ReturnFunction
					&& e.getOutput().getStrId().equals(f2.getStrId()))
				accessExists = true;
		}
		return accessExists;
	}

}
