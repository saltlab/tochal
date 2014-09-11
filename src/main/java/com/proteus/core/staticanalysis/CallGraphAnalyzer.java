package com.proteus.core.staticanalysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;


/*
import com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil;
import com.ibm.wala.cast.js.rhino.test.HTMLCGBuilder;
import com.ibm.wala.cast.js.rhino.test.HTMLCGBuilder.CGBuilderResult;
import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.CollectionFilter;
import com.ibm.wala.util.collections.Filter;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.GraphSlicer;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;
import com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil;
import com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil.CGBuilderType;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.cast.js.test.*;
*/

public class CallGraphAnalyzer {
	static int i = 0;
	
	public static void main(String []args) {
		String script = "function AAA(){} function BBB(){AAA();} BBB();";
	/*	
		CallGraphAnalyzer analyzer = new CallGraphAnalyzer();
		Graph<CGNode> callGraph = analyzer.getCallGraph(script, "name");
		System.out.println(callGraph.toString());
		*/
	}
	
/*
	public static void main(String []args) {
		CallGraphAnalyzer analyzer = new CallGraphAnalyzer();
		Graph<CGNode> callGraph = analyzer.getCallGraph("function AAA(){} function BBB(){AAA();} BBB();", "name");
		System.out.println(callGraph.toString());
		
		System.out.println(callGraph.getNumberOfNodes());
		Iterator<CGNode> nodeItr = callGraph.iterator();
		while (nodeItr.hasNext()) {
			CGNode node = nodeItr.next();
			System.out.println("id: " + node.getGraphNodeId());
			System.out.println("method: " + node.getMethod().toString());
/*			System.out.println("ir: " + node.getIR().toString());
*/
/*	System.out.println("du: " + node.getDU().toString());
			System.out.println("context: " + node.getContext().toString());
/			System.out.println("call sites");
			Iterator<CallSiteReference> callSiteItr = node.iterateCallSites();
			while (callSiteItr.hasNext())
				System.out.println(callSiteItr.next().toString());
/*			System.out.println("new sites");
			Iterator<NewSiteReference> newSiteItr = node.iterateNewSites();
			while (newSiteItr.hasNext())
				System.out.println(newSiteItr.next().toString());
		System.out.println("getSuccNodeCount: " + callGraph.getSuccNodeCount(node));
			Iterator<CGNode> succItr = callGraph.getSuccNodes(node);
			while (succItr.hasNext()) {
				CGNode next = succItr.next();
				System.out.println(next.toString());
			}
			// Get the IR of a CGNode
			IR ir = node.getIR();
			// Get CFG from IR
			SSACFG cfg = ir.getControlFlowGraph();
			// Iterate over the Basic Blocks of CFG
			Iterator<ISSABasicBlock> cfgIt = cfg.iterator();
			while (cfgIt.hasNext()) {
				ISSABasicBlock ssaBb = cfgIt.next();
				
				// Iterate over SSA Instructions for a Basic Block
				Iterator<SSAInstruction> ssaIt = ssaBb.iterator();
				while (ssaIt.hasNext()) {
					SSAInstruction ssaInstr = ssaIt.next();
					System.out.println(ssaInstr);
				}				
			}
		}
		
	}
*/
	/*
	public Graph<CGNode> getCallGraph(String script, String name) {
		CallGraph callGraph = null;
		Graph<CGNode> prunedGraph = null;
		PrintWriter writer;
				
		try {
//			writer = new PrintWriter(fileName + (CallGraphAnalyzer.i) + ".js", "UTF-8");
			writer = new PrintWriter("hello_world.js", "UTF-8");
			writer.print(script);
			writer.close();

			try {
				JSCallGraphUtil.setTranslatorFactory(new CAstRhinoTranslatorFactory());
				callGraph = JSCallGraphBuilderUtil.makeScriptCG(".", "hello_world.js");
//				callGraph = JSCallGraphBuilderUtil.makeScriptCG(".", fileName + (CallGraphAnalyzer.i ++) + ".js");
//				callGraph = JSCallGraphBuilderUtil.makeScriptCG(".", "temp-js.js");
				
				System.out.println(callGraph.toString());				
				prunedGraph = pruneGraph(callGraph, new ApplicationLoaderFilter());

				CGBuilderResult builderResult = HTMLCGBuilder.buildHTMLCG("hello_world.html", 10000, CGBuilderType.ZERO_ONE_CFA); // TODO ????
				CallGraph htmlCG = builderResult.builder.getCallGraph();
				Graph<CGNode> prunedHTMLCG = pruneGraph(htmlCG, new ApplicationLoaderFilter());
				PointerAnalysis pointerAnalysis = builderResult.builder.getPointerAnalysis();
	
				System.out.println(prunedHTMLCG);

		        // find seed statement
		        Statement statement = findCallTo(findMainMethod(cg), "println");
		        
				// for all nodes and for all statements: find forward slices: find slices' functions. connect original function with the second function
				
				for (Iterator<CGNode> itr = prunedHTMLCG.iterator(); itr.hasNext(); ) {
					CGNode node = itr.next();
					IMethod method = node.getMethod();
					IR ir = node.getIR();
//					System.out.println(node.getMethod());
					
					for (int i = 0; i < ir.getInstructions().length; i ++) {
						SSAInstruction inst = ir.getInstructions()[i];
						Statement s = new com.ibm.wala.ipa.slicer.NormalStatement(node, i);
						
						Collection<Statement> slice = Slicer.computeForwardSlice(s, htmlCG, pointerAnalysis, DataDependenceOptions.NO_BASE_PTRS, ControlDependenceOptions.NONE);
						System.out.println(slice);
}
					
				}
		        
				return prunedGraph;
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

		return prunedGraph;
		
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
				if (n.toString().toLowerCase().contains("prologue"))
					return false;
	/*
				if (!n.toString().contains("hello_world")
						&& !n.toString().contains("alert")
						&& !n.toString().contains("getElementById")
						&& !n.toString().contains("addEventListener")
						&& !n.toString().contains("setAttribute")
						&& !n.toString().contains("querySelectorAll")
						&& !n.toString().contains("getComputedStyle")) {
					return false;
				}
				else {
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
		/*
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
				}
				else {
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
*/
}
