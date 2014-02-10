/*
 * Automatic JavaScript Invariants is a plugin for Crawljax that can be used to derive JavaScript
 * invariants automatically and use them for regressions testing. Copyright (C) 2010 crawljax.com
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should
 * have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.proteus.jsmodify;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crawljax.util.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proteus.core.interactiongraph.InteractionGraph;

/**
 * Reads an instrumentation array from the webbrowser and saves the contents in a JSON trace file.
 * 
 * @author Frank Groeneveld
 * @version $Id: JSExecutionTracer.java 6162 2009-12-16 13:56:21Z frank $
 */
public class JSExecutionTracer {

	private static final int ONE_SEC = 1000;

	private static String outputFolder;
	private static String traceFilename;

	private static JSONArray points = new JSONArray();

	private static final Logger LOGGER = Logger
	        .getLogger(JSExecutionTracer.class.getName());

	public static final String FUNCTIONTRACEDIRECTORY = "functiontrace/";

	private static PrintStream output;

	static String theTime;


	/**
	 * @param filename
	 */
	public JSExecutionTracer(String filename) {
		traceFilename = filename;
	}

	/**
	 * Initialize the plugin and create folders if needed.
	 * 
	 * @param browser
	 *            The browser.
	 */
	public static void preCrawling() {
		try {
			points = new JSONArray();

			Helper.directoryCheck(getOutputFolder());
			output = new PrintStream(getOutputFolder() + getFilename());

			// Add opening bracket around whole trace
			PrintStream oldOut = System.out;
			System.setOut(output);
			System.out.println("{");
			System.setOut(oldOut);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the JavaScript instrumentation array from the webbrowser and writes its contents in
	 * Daikon format to a file.
	 * 
	 * @param session
	 *            The crawling session.
	 * @param candidateElements
	 *            The candidate clickable elements.
	 */

	public void preStateCrawling() {

		String filename = getOutputFolder() + FUNCTIONTRACEDIRECTORY
		        + "jstrace-";

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		filename += dateFormat.format(date) + ".dtrace";

		try {

			LOGGER.info("Reading execution trace");

			LOGGER.info("Parsing JavaScript execution trace");

			// session.getBrowser().executeJavaScript("sendReally();");
			Thread.sleep(ONE_SEC);

			LOGGER.info("Saved execution trace as " + filename);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * Get a list with all trace files in the executiontracedirectory.
	 * 
	 * @return The list.
	 */
	public List<String> allTraceFiles() {
		ArrayList<String> result = new ArrayList<String>();

		/* find all trace files in the trace directory */
		File dir = new File(getOutputFolder() + FUNCTIONTRACEDIRECTORY);

		String[] files = dir.list();
		if (files == null) {
			return result;
		}
		for (String file : files) {
			if (file.endsWith(".dtrace")) {
				result.add(getOutputFolder() + FUNCTIONTRACEDIRECTORY + file);
			}
		}
		return result;
	}

	public static void postCrawling() {
		
		// TODO 
		
		InteractionGraph.getInstance().handleGraphAfterTermination();


		/*
		try {
			// Add closing bracket
			PrintStream oldOut = System.out;
			System.setOut(output);
			System.out.println(" ");
			System.out.println("}");
			System.setOut(oldOut);

			// close the output file
			output.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

	/**
	 * @return Name of the file.
	 */
	public static String getFilename() {
		return traceFilename;
	}

	public static String getOutputFolder() {
		return Helper.addFolderSlashIfNeeded(outputFolder);
	}

	public void setOutputFolder(String absolutePath) {
		outputFolder = absolutePath;
	}

}
