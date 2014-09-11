package com.proteus.core.staticanalysis;
/***
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;

import org.jfree.chart.needle.PointerNeedle;

import com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil;
import com.ibm.wala.cast.js.rhino.test.HTMLCGBuilder;
import com.ibm.wala.cast.js.rhino.test.HTMLCGBuilder.CGBuilderResult;
import com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil;
import com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil.CGBuilderType;
import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.CollectionFilter;
import com.ibm.wala.util.collections.Filter;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.GraphSlicer;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;
import com.proteus.core.interactiongraph.InteractionGraph;
import com.proteus.core.interactiongraph.node.Function;
***/
public class TestWala {
	public static void main(String[] args) {
		// TestWala walaTest = new TestWala();
		// String script = "function AAA(){} function BBB(){AAA();} BBB();";
		String script = "function AAA(a, b){} \n function BBB(a){} \n function CCC(b, c, d){var a = 3; AAA(a, a + 3); return a;} \n function DDD(){CCC();} \n function EEE(){BBB(); CCC();} EEE();";
		// TestWala.getCallGraph(script);
//		TestWala.getCallGraph_html_test();
//		TestWala.getCallGraphAndSlicer(script);
/***		TestWala.getgraphslicertemp(script);
	***/	
	}
/***
	public static Graph<CGNode> getCallGraph(String script) {
		CallGraph CG = null;
		// use Rhino for parsing; change if you want to use a different parser
		com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil
				.setTranslatorFactory(new CAstRhinoTranslatorFactory());

		PrintWriter writer;
		try {
			writer = new PrintWriter("hello_world0.js", "UTF-8");
			writer.print(script);
			writer.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			CG = com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil.makeScriptCG(
					".", "hello_world0.js");
			System.out.println(CG.getNumberOfNodes());
//			System.out.println(CG.toString());

			Graph<CGNode> g = pruneGraph(CG, new ApplicationLoaderFilter());

			System.out.println("=======================");
//			System.out.println(g.toString());
			System.out.println("=======================");
			System.out.println(g.getNumberOfNodes());

			DotUtil.writeDotFile(g, new JSLabeler(),
					"Simple JS Call Graph Test", "hello_world.dot");
			if (CG.toString().contains("addEventListener")) {
				System.out.println("It contains addEventListener");
			} else {
				System.out.println("It does NOT contain addEventListener");
			}
			// return;
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

		return CG;
	}
	
	public static void getgraphslicertemp(String script) {
		CallGraph callGraph = null;
		Graph<CGNode> prunedGraph = null;
		PrintWriter writer;
		
		try {
			writer = new PrintWriter("hello_world.js", "UTF-8");
			writer.print(script);
			writer.close();

			try {
				JSCallGraphUtil.setTranslatorFactory(new CAstRhinoTranslatorFactory());
				callGraph = JSCallGraphBuilderUtil.makeScriptCG(".", "hello_world.js");
								
				prunedGraph = pruneGraph(callGraph, new ApplicationLoaderFilter());
				
				System.out.println("----------------------------------------------");
				System.out.println("----------------------------------------------");
				System.out.println(prunedGraph.toString());
				System.out.println("----------------------------------------------");
				System.out.println("----------------------------------------------");

				for (Iterator<CGNode> it = prunedGraph.iterator(); it.hasNext(); ) {
					CGNode node = it.next();
					IMethod method = node.getMethod();
					System.out.println("***************" + node.getMethod().getSignature());
					String nodeName = node.getMethod().getSignature().substring(17);
					int nodeEndIndex = nodeName.indexOf(".do()");
					System.out.println(nodeEndIndex);
					if (nodeEndIndex >= 0) {
						nodeName = nodeName.substring(0, nodeEndIndex);
						
					}
					
					// TODO TODO 
					Function caller;
					if (InteractionGraph.getInstance().getFunctions().containsKey(nodeName)) {
						caller = InteractionGraph.getInstance().getFunctions().get(nodeName);
					}
					else
						caller = new Function(nodeName);
					
					
					Iterator<CGNode> succNodesIt = prunedGraph.getSuccNodes(node);
					while (succNodesIt.hasNext()) {
						CGNode next = succNodesIt.next();
						System.out.println("PARAM: " + next.getMethod().getName() + "===" + next.getMethod().getNumberOfParameters());
						System.out.println("RETURN: " + next.getMethod().getReturnType().toString());
						System.out.println("RETURN: " + next.getMethod().getReturnType().getName().toString());
						if (next.getMethod().getNumberOfParameters() > 2) {
							String nextName = next.getMethod().getSignature().substring(17); // TODO TODO TODO TODO
							int endIndex = nextName.indexOf(".do()");
							System.out.println(endIndex);
							if (endIndex >= 0) {
								nextName = nextName.substring(0, endIndex);
							
								System.err.println("CONNECTION FROM " + nodeName + " TO " + nextName);

								// TODO move outside if??? what about other names?
								// TODO TODO
								Function callee;
								if (InteractionGraph.getInstance().getFunctions().containsKey(nextName))
									callee = InteractionGraph.getInstance().getFunctions().get(nextName);
								else
									callee = new Function(nextName);
								
							}
							// node calls next with parameters (relation from node to next)
							System.err.println("param");
						}
//						if (next.getMethod().getReturnType() != null && next.getMethod().getReturnType().toString() != "") // TODO if has a return type
						// OR
						TypeReference returnType = next.getMethod().getReturnType();
						if (returnType != null) {
/*							if (returnType.isArrayType() || returnType.isClassType() || returnType.isPrimitiveType() || returnType.isReferenceType()) {
								// next returns to node with return type (relation from next to node)
								System.err.println("return");
							}
	*/
	/***
							if (returnType.isArrayType() || returnType.isPrimitiveType()) {
								// next returns to node with return type (relation from next to node)
								System.err.println("return");								
							}
							if (returnType.isArrayType()) {
								System.err.println("return 111");
							}
							if (returnType.isClassType()) {
								System.err.println("return 222");
							}
							if (returnType.isPrimitiveType()) {
								System.err.println("return 333");
							}
							if (returnType.isReferenceType()) {
								System.err.println("return 444");
							}
						}
					}
				}
				
				writer = new PrintWriter("hello_world.html", "UTF-8");
				String startScript = "<html><head></head><body><script> \n";
				String endScript = " \n </script></body></html>";
				writer.print(startScript + script + endScript);
				writer.close();

				
				
				CGBuilderResult builderResult = HTMLCGBuilder.buildHTMLCG("hello_world.html", 10000, CGBuilderType.ZERO_ONE_CFA); // TODO ????
				System.out.println("+++++++++++++++++++++++++++++++++++<<");
				CallGraph htmlCG = builderResult.builder.getCallGraph();
				Graph<CGNode> prunedHTMLCG = pruneGraph(htmlCG, new ApplicationLoaderFilter());
				PointerAnalysis pointerAnalysis = builderResult.builder.getPointerAnalysis();
	
//				System.out.println(prunedHTMLCG);
				System.out.println("<>+++++++++++++++++++++++++++++++++++<>");
				
				
				
				for (Iterator<CGNode> itr = prunedHTMLCG.iterator(); itr.hasNext(); ) {
					CGNode node = itr.next();
					IMethod method = node.getMethod();
					IR ir = node.getIR();
//					System.out.println(node.getMethod());
					
					for (Iterator<SSAInstruction> sIt = ir.iterateAllInstructions(); sIt.hasNext();) {
						SSAInstruction s = sIt.next();
						if (s instanceof com.ibm.wala.ssa.SSAAbstractInvokeInstruction) {
							// TODO TODO TODO TODO TODO
							com.ibm.wala.ssa.SSAAbstractInvokeInstruction call = (com.ibm.wala.ssa.SSAAbstractInvokeInstruction) s;
							//if (call.getCallSite().getDeclaredTarget().getName().toString().equals(methodName)) // the name of the method?
								IntSet indices = ir.getCallInstructionIndices(call.getCallSite());
								com.ibm.wala.util.debug.Assertions.productionAssertion(indices.size() == 1, "expected 1 but got " + indices.size());
								Statement normalS = new com.ibm.wala.ipa.slicer.NormalStatement(node, indices.intIterator().next());
								System.out.println(normalS);
	
//								Collection<Statement> slice = Slicer.computeBackwardSlice(normalS, htmlCG, pointerAnalysis, DataDependenceOptions.NO_BASE_PTRS, ControlDependenceOptions.NONE);
/*								Collection<Statement> slice = Slicer.computeForwardSlice(normalS, htmlCG, pointerAnalysis, DataDependenceOptions.NO_BASE_PTRS, ControlDependenceOptions.NONE);
								System.out.println(slice);
								*/
	/***
						}
					}
/******					
					for (int i = 0; i < ir.getInstructions().length; i ++) {
						SSAInstruction inst = ir.getInstructions()[i];
						Statement s = new com.ibm.wala.ipa.slicer.NormalStatement(node, i);
						if (s != null)
							System.out.println(s.toString());
						else
							System.err.println("statement is null");
						
//						Collection<Statement> slice = Slicer.computeForwardSlice(s, htmlCG, pointerAnalysis, DataDependenceOptions.NO_BASE_PTRS, ControlDependenceOptions.NONE);
//						System.out.println(slice);
						
					}
********/					
	/***
					System.out.println("====");
				}
		        
				System.out.println(">>+++++++++++++++++++++++++++++++++++");
				
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

		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public static void getCallGraphAndSlicer(String script) {
		CallGraph CG = null;
		// use Rhino for parsing; change if you want to use a different parser
		com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil
				.setTranslatorFactory(new CAstRhinoTranslatorFactory());

		PrintWriter writer;
		try {
			writer = new PrintWriter("hello_world0.html", "UTF-8");
			String startScript = "<html><head></head><body><script> \n";
			String endScript = " \n </script></body></html>";
			writer.print(startScript + script + endScript);
			writer.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			JSCallGraphUtil
					.setTranslatorFactory(new CAstRhinoTranslatorFactory());

			CGBuilderResult builderResult = HTMLCGBuilder.buildHTMLCG(
					"hello_world0.html", 10000, CGBuilderType.ZERO_ONE_CFA); // TODO
																			// ????
			System.out.println("+++++++++++++++++++++++++++++++++++<<");
			CallGraph htmlCG = builderResult.builder.getCallGraph();
			Graph<CGNode> prunedHTMLCG = pruneGraph(htmlCG,
					new ApplicationLoaderFilter());
			PointerAnalysis pointerAnalysis = builderResult.builder
					.getPointerAnalysis();

			System.out.println(prunedHTMLCG);
			System.out.println("<>+++++++++++++++++++++++++++++++++++<>");

			// for all nodes
			// for all statements
			// find forward slices
			// find slices' functions
			// connect original function with the second function

			for (Iterator<CGNode> itr = prunedHTMLCG.iterator(); itr.hasNext();) {
				CGNode node = itr.next();
				IMethod method = node.getMethod();
				IR ir = node.getIR();
				
				System.out.println("NEXT ----------");
				System.out.println(method.getName());

				for (int i = 0; i < ir.getInstructions().length; i++) {
					SSAInstruction inst = ir.getInstructions()[i];
					Statement s = new com.ibm.wala.ipa.slicer.NormalStatement(node, i);
					
					Collection<Statement> slice = Slicer.computeForwardSlice(s, htmlCG, pointerAnalysis, DataDependenceOptions.NO_BASE_PTRS, ControlDependenceOptions.NONE);
					System.out.println(slice);
				}

			}

			System.out.println(">>+++++++++++++++++++++++++++++++++++");

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
	}


	public static void getCallGraphAndSlicer_html(String fileName) {
		CallGraph callGraph = null;
		Graph<CGNode> prunedGraph = null;
		PrintWriter writer;

		try {
			JSCallGraphUtil
					.setTranslatorFactory(new CAstRhinoTranslatorFactory());

			CGBuilderResult builderResult = HTMLCGBuilder.buildHTMLCG(
					fileName, 10000, CGBuilderType.ZERO_ONE_CFA); // TODO
																			// ????
			System.out.println("+++++++++++++++++++++++++++++++++++<<");
			CallGraph htmlCG = builderResult.builder.getCallGraph();
			Graph<CGNode> prunedHTMLCG = pruneGraph(htmlCG,
					new ApplicationLoaderFilter());
			PointerAnalysis pointerAnalysis = builderResult.builder
					.getPointerAnalysis();

			System.out.println(prunedHTMLCG);
			System.out.println("<>+++++++++++++++++++++++++++++++++++<>");

			// for all nodes
			// for all statements
			// find forward slices
			// find slices' functions
			// connect original function with the second function

			for (Iterator<CGNode> itr = prunedHTMLCG.iterator(); itr
					.hasNext();) {
				CGNode node = itr.next();
				IMethod method = node.getMethod();
				IR ir = node.getIR();

				for (int i = 0; i < ir.getInstructions().length; i++) {
					SSAInstruction inst = ir.getInstructions()[i];
					Statement s = new com.ibm.wala.ipa.slicer.NormalStatement(
							node, i);

					Collection<Statement> slice = Slicer
							.computeForwardSlice(s, htmlCG,
									pointerAnalysis,
									DataDependenceOptions.NO_BASE_PTRS,
									ControlDependenceOptions.NONE);
					System.out.println(slice);
				}

			}

			System.out.println(">>+++++++++++++++++++++++++++++++++++");

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

	}

	public static Graph<CGNode> getCallGraph_html_test() {
		CallGraph CG = null;
		// use Rhino for parsing; change if you want to use a different parser
		com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil
				.setTranslatorFactory(new CAstRhinoTranslatorFactory());

		System.out.println(1);
		try {
			// CG =
			// com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil.makeScriptCG(".",
			// "hello_world0.js");
			// File file = new
			// File("/Users/saba/Documents/CIA/Proteus/src/hello_world1.html");
			File file = new File("hello_world.html");
			System.out.println(2);
			CG = com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil
					.makeHTMLCG(file.toURI().toURL());
			System.out.println(3);
			System.out.println(CG.getNumberOfNodes());
			System.out.println(CG.toString());

			Graph<CGNode> g = pruneGraph(CG, new ApplicationLoaderFilter());

			System.out.println(g.toString());
			System.out.println(g.getNumberOfNodes());

			DotUtil.writeDotFile(g, new JSLabeler(),
					"Simple JS Call Graph Test", "hello_world.dot");
			if (CG.toString().contains("addEventListener")) {
				System.out.println("It contains addEventListener");
			} else {
				System.out.println("It does NOT contain addEventListener");
			}
			// return;
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

		return CG;
	}

	public static <T> Graph<T> pruneGraph(Graph<T> g, Filter<T> f)
			throws WalaException {
		Collection<T> slice = GraphSlicer.slice(g, f);
		return GraphSlicer.prune(g, new CollectionFilter<T>(slice));
	}

	private static class ApplicationLoaderFilter implements Filter<CGNode> {
		@Override
		public boolean accepts(CGNode o) {
			if (o instanceof CGNode) {
				CGNode n = (CGNode) o;
				if (!n.toString().contains("hello_world")
						&& !n.toString().contains("alert")
						&& !n.toString().contains("getElementById")
						&& !n.toString().contains("addEventListener")
						&& !n.toString().contains("setAttribute")
						&& !n.toString().contains("querySelectorAll")
						&& !n.toString().contains("getComputedStyle")) {
					return false;
				} else {
					if (n.toString().startsWith("Node: <ctor for")) {
						// Remove all declarations (is this what "ctor" refers
						// to?)
						return false;
					}
					if (n.toString().contains("make_node")) {
						return false;
					}
					return true;
				}
			} else {
				return false;
			}
		}
	}

	private static class JSLabeler implements NodeDecorator {
		@Override
		public String getLabel(Object o) {
			/*
			 * if (o.toString().contains("foo")) { return "foo"; } else if
			 * (o.toString().contains("bar")) { return "bar"; } else if
			 * (o.toString().contains("__WINDOW_MAIN__/func")) { return "func";
			 * } else if (o.toString().contains("onload")) { return "onload"; }
			 * else if (o.toString().contains("getElementById") &&
			 * o.toString().contains("preamble")) { return "getElementById"; }
			 * return o.toString();
			 */
	/***
			if (o.toString().length() > 200) {
				return o.toString().substring(0, 197) + "...";
			}
			return o.toString();
		}
	}
***/
}
