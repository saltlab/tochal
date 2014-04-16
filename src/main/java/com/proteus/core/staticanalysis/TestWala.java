package com.proteus.core.staticanalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.CollectionFilter;
import com.ibm.wala.util.collections.Filter;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.GraphSlicer;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;

public class TestWala {
	public static void main(String []args) {
//		TestWala walaTest = new TestWala();
//		String script = "function AAA(){} function BBB(){AAA();} BBB();";
		String script = "function AAA(){} \n function BBB(){} \n function CCC(){AAA();} \n function DDD(){CCC();} \n function EEE(){BBB(); CCC();} EEE();";
//		TestWala.getCallGraph(script);
		TestWala.getCallGraph_html_test();
	}
	
	public static Graph<CGNode> getCallGraph(String script) {
		CallGraph CG = null;
		// use Rhino for parsing; change if you want to use a different parser
		com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil.setTranslatorFactory(new CAstRhinoTranslatorFactory());
		
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
			CG = com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil.makeScriptCG(".", "hello_world0.js");
			System.out.println(CG.getNumberOfNodes());
			System.out.println(CG.toString());
			
			Graph<CGNode> g = pruneGraph(CG, new ApplicationLoaderFilter());
			
			System.out.println("=======================");
			System.out.println(g.toString());
			System.out.println("=======================");
			System.out.println(g.getNumberOfNodes());

			
			DotUtil.writeDotFile(g, new JSLabeler(), "Simple JS Call Graph Test", "hello_world.dot");
			if (CG.toString().contains("addEventListener")) {
				System.out.println("It contains addEventListener");
			}
			else {
				System.out.println("It does NOT contain addEventListener");
			}
//			return;
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

	public static Graph<CGNode> getCallGraph_html_test() {
		CallGraph CG = null;
		// use Rhino for parsing; change if you want to use a different parser
		com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil.setTranslatorFactory(new CAstRhinoTranslatorFactory());
		
		System.out.println(1);
		try {
//			CG = com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil.makeScriptCG(".", "hello_world0.js");
//			File file = new File("/Users/saba/Documents/CIA/Proteus/src/hello_world1.html");
			File file = new File("hello_world.html");
			System.out.println(2);
			CG = com.ibm.wala.cast.js.test.JSCallGraphBuilderUtil.makeHTMLCG(file.toURI().toURL());
			System.out.println(3);
			System.out.println(CG.getNumberOfNodes());
			System.out.println(CG.toString());
			
			Graph<CGNode> g = pruneGraph(CG, new ApplicationLoaderFilter());
			
			System.out.println("=======================");
			System.out.println(g.toString());
			System.out.println("=======================");
			System.out.println(g.getNumberOfNodes());

			
			DotUtil.writeDotFile(g, new JSLabeler(), "Simple JS Call Graph Test", "hello_world.dot");
			if (CG.toString().contains("addEventListener")) {
				System.out.println("It contains addEventListener");
			}
			else {
				System.out.println("It does NOT contain addEventListener");
			}
//			return;
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

	public static <T> Graph<T> pruneGraph(Graph<T> g, Filter<T> f) throws WalaException {
		Collection<T> slice = GraphSlicer.slice(g, f);
		return GraphSlicer.prune(g, new CollectionFilter<T>(slice));
	}
	
	private static class ApplicationLoaderFilter implements Filter<CGNode> {
		@Override
		public boolean accepts(CGNode o) {
			if (o instanceof CGNode) {
				CGNode n = (CGNode) o;
				if (!n.toString().contains("hello_world") && !n.toString().contains("alert") && !n.toString().contains("getElementById") && !n.toString().contains("addEventListener") && !n.toString().contains("setAttribute") && !n.toString().contains("querySelectorAll") && !n.toString().contains("getComputedStyle")) {
					return false;
				}
				else {
					if (n.toString().startsWith("Node: <ctor for")) {
						//Remove all declarations (is this what "ctor" refers to?)
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
			if (o.toString().contains("foo")) {
				return "foo";
			}
			else if (o.toString().contains("bar")) {
				return "bar";
			}
			else if (o.toString().contains("__WINDOW_MAIN__/func")) {
				return "func";
			}
			else if (o.toString().contains("onload")) {
				return "onload";
			}
			else if (o.toString().contains("getElementById") && o.toString().contains("preamble")) {
				return "getElementById";
			}
			return o.toString();
			*/
			if (o.toString().length() > 200) {
				return o.toString().substring(0, 197) + "...";
			}
			return o.toString();
		}
	}

}
