package com.proteus.core.staticanalysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil;
import com.ibm.wala.cast.js.rhino.test.HTMLCGBuilder;
import com.ibm.wala.cast.js.rhino.test.HTMLCGBuilder.CGBuilderResult;
import com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil;
import com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil.CGBuilderType;
import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.CollectionFilter;
import com.ibm.wala.util.collections.Filter;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.GraphSlicer;
import com.proteus.core.interactiongraph.InteractionGraph;
import com.proteus.core.interactiongraph.edge.CallFunction;
import com.proteus.core.interactiongraph.edge.InteractionEdge;
import com.proteus.core.interactiongraph.node.Function;



public class StaticAnalyzer {
	public static void main(String[] args) {
		StaticAnalyzer staticAnalyzer = new StaticAnalyzer();
		String path = "src/main/webapp/temp.html";
		staticAnalyzer.getCallGraph("js.js", "src/main/webapp/tempStaticAnalysis/607");

		System.out.println(InteractionGraph.getInstance().getFunctions().size());
		System.out.println(InteractionGraph.getInstance().getFunctions().values().toString());
	}

	public Graph<CGNode> getCallGraph(String fileName, String path) {
		CallGraph callGraph = null;
		Graph<CGNode> prunedGraph = null;
		PrintWriter writer;

		JSCallGraphUtil.setTranslatorFactory(new CAstRhinoTranslatorFactory());
		try {
			if (fileName.endsWith(".js")) {
				System.out.println(".js file");
				callGraph = JSCallGraphBuilderUtil.makeScriptCG(path,
					fileName);
			}
			else if (fileName.endsWith(".html")) {
				System.out.println(".html file: " + fileName);
				CGBuilderResult builderResult = HTMLCGBuilder.buildHTMLCG(fileName, 10000, CGBuilderType.ZERO_ONE_CFA); // TODO ????
				callGraph = builderResult.builder.getCallGraph();
			}
			
			System.out.println(callGraph.toString());
			prunedGraph = pruneGraph(callGraph, new ApplicationLoaderFilter());
			System.out.println("Num of nodes in pruned static call graph: " + prunedGraph.getNumberOfNodes());
			System.out.println(prunedGraph.toString());
	

			for (Iterator<CGNode> it = prunedGraph.iterator(); it.hasNext();) {
				CGNode node = it.next();
				
				String nodeName = node.getMethod().getSignature().substring(17);
				int nodeEndIndex = nodeName.indexOf(".do()");
				if (nodeEndIndex >= 0) {
					nodeName = nodeName.substring(0, nodeEndIndex);
				}
				System.out.println("name: " + nodeName);
				
				Function caller;
				if (InteractionGraph.getInstance().getFunctions().containsKey(nodeName)) {
					caller = InteractionGraph.getInstance().getFunctions().get(nodeName);
				}
				else {
					caller = new Function(nodeName);
					InteractionGraph.getInstance().getFunctions().put(nodeName, caller);
				}
				

				Iterator<CGNode> succNodesIt = prunedGraph.getSuccNodes(node);
				while (succNodesIt.hasNext()) {
					CGNode next = succNodesIt.next();
					System.out.println("PARAM: " + next.getMethod().getName()
							+ "===" + next.getMethod().getNumberOfParameters());
					System.out.println("RETURN: "
							+ next.getMethod().getReturnType().toString());
					System.out.println("RETURN: "
							+ next.getMethod().getReturnType().getName()
									.toString());
					

					if (next.getMethod().getNumberOfParameters() > 2) {
						String nextName = next.getMethod().getSignature()
								.substring(17);
						int endIndex = nextName.indexOf(".do()");
						System.out.println(endIndex);
						if (endIndex >= 0) {
							nextName = nextName.substring(0, endIndex);

							System.err.println("CONNECTION FROM " + nodeName
									+ " TO " + nextName);

							Function callee;
							if (InteractionGraph.getInstance().getFunctions().containsKey(nextName))
								callee = InteractionGraph.getInstance().getFunctions().get(nextName);
							else {
								callee = new Function(nextName);
								InteractionGraph.getInstance().getFunctions().put(nextName, callee);
							}
							
							boolean accessExists = false;
							for (InteractionEdge e : caller.getOutput()) {
								if (e instanceof CallFunction) {
									if (e.getOutput().getStrId().equals(callee.getStrId()))
											accessExists = true;
								}
							}

							if (!accessExists) {
								CallFunction callAccess = new CallFunction();
								callAccess.setInput(caller);
								callAccess.setOutput(callee);
								caller.addOutput(callAccess);
								callee.addInput(callAccess);
							}
						}
						System.err.println("param");
					}
					TypeReference returnType = next.getMethod().getReturnType();
					if (returnType != null) {
						if (returnType.isArrayType()
								|| returnType.isPrimitiveType()) {
							// next returns to node with return type (relation
							// from next to node)
							System.out.println("return");
						}
						if (returnType.isArrayType()) {
							System.out.println("return 111");
						}
						if (returnType.isClassType()) {
							System.out.println("return 222");
						}
						if (returnType.isPrimitiveType()) {
							System.out.println("return 333");
						}
						if (returnType.isReferenceType()) {
							System.out.println("return 444");
						}
					}
				}

			}

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CancelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WalaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static <T> Graph<T> pruneGraph(Graph<T> g, Filter<T> f)
			throws WalaException {
		Collection<T> slice = GraphSlicer.slice(g, f);
		return GraphSlicer.prune(g, new CollectionFilter<T>(slice));
	}

	private static class ApplicationLoaderFilter implements Filter<CGNode> {
//		@Override
		public boolean accepts(CGNode o) {
			if (o instanceof CGNode) {
				CGNode n = (CGNode) o;

				if (n.toString().toLowerCase().contains("prologue")) {
//					System.out.println("FILTER PROLOGUE: " + n.toString());
					return false;
				} else if (n.toString().toLowerCase().contains("preamble")) {
//					System.out.println("FILTER PREAMBLE: " + n.toString());
					return false;
				}

				if (n.toString().toLowerCase().contains("make_node")) {
//					System.out.println("FILTER MAKE_NODE: " + n.toString());
					return false;
				} 
				/*
				else if (!n.toString().toLowerCase()
						.contains("__window_main__/")) {
//					System.out.println("FILTER NO FUNCTION: " + n.toString());
					return false;
				}*/
				if (n.toString().toLowerCase().contains("fakerootmethod"))
					return false;

				return true;

			} else {
				return false;

			}
		}
	}

}
