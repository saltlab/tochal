//console.log('5. FUNCTION INST BEGIN');

var doNotInstrumentLibraries = [];

doNotInstrumentLibraries[0] = 'jquery';
doNotInstrumentLibraries[1] = 'use.typekit.com';

function mustBeInstrumented(src) {
//	console.log('-- ', doNotInstrumentLibraries.length);
	for (var i = 0; i < doNotInstrumentLibraries.length; i ++) {
//		console.log('src: ', src, '  --  lib: ', doNotInstrumentLibraries[i]);
		if (src.toLowerCase().indexOf(doNotInstrumentLibraries[i]) > -1)
			return false;
	}
	return true;
}

/*******************/
/* STATIC FUNCTION INFO */
/*******************/
var staticFunctionNames = [];
var staticFunctionArgs = [];

function logStaticFunctionName(name, args) {
	if (staticFunctionNames[name] == null || typeof staticFunctionNames[name] == 'undefined') {
		var newIndex = staticFunctionArgs.length;
		staticFunctionNames[name] = newIndex;
		staticFunctionArgs[newIndex] = args.length;
	}
	else {
		var funcIndex = staticFunctionNames[name];
		staticFunctionArgs[funcIndex] = args.length;
	}
}

// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
  	analyzeStaticFunctionInfo();
    console.log(sender.tab ?
                "from a content script:" + sender.tab.url :
                "from the extension");
    if (request.greeting == "hello")
      sendResponse({farewell: "goodbye"});
  });
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO

function analyzeStaticFunctionInfo() {
	console.log('******** static begin ********');

	console.log('staticFunctionNames: ', staticFunctionNames);
	console.log('staticFunctionArgs: ', staticFunctionArgs);

	console.log('num of static funcs: ' + staticFunctionArgs.length);
	var numOfStaticFuncsWNoArgs = 0;
	for (var i = 0; i < staticFunctionArgs.length; i ++) {
		var funcWNoArgs = true;
		var funcInstanceWNoArgs = false;
		var prevArgs;
		if (staticFunctionArgs[i].length > 0)
			if (staticFunctionArgs[i][0] == 0)
				numOfStaticFuncsWNoArgs ++;
		
	}
	console.log('numOfStaticFuncsWNoArgs: ', numOfStaticFuncsWNoArgs);

	console.log('******** static end ********');

}
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO

/***************************/
/** BEGIN {KEEPING TRACK OF STATIC FUNCTION ARGUMENTS AND RETURN VALUES} **/
/***************************/

// NEW VERSION SABA
/********** 
var varHolderScript = document.createElement('script');
varHolderScript.setAttribute('instrumented', 'true');
varHolderScript.setAttribute('id', 'staticFunctionArgsVarContainer');
varHolderScript.innerText = '';

var staticFunctionArgs = {};
var staticFunctionRets = {};

function addStaticFunctionArgs(name, args) {
	if (staticFunctionArgs[name] == null || typeof staticFunctionArgs[name] == 'undefined') {
		staticFunctionArgs[name] = {};
		staticFunctionArgs[name][0] = args.length;
	}
	else {
		var index = getArraySize(staticFunctionArgs[name]);
		staticFunctionArgs[name][index] = args.length;
		// console.log(name, ' -- ', staticFunctionArgs[name]);
	}
////////////////	console.log('<><> ', staticFunctionArgs);
}

function addStaticFunctionRets(name, ret) {
	var returnValueLength = 0;
	if (ret != null || typeof ret != 'undefined')
		returnValueLength = 1;
	if (staticFunctionRets[name] == null || typeof staticFunctionRets[name] == 'undefined') {
		staticFunctionRets[name] = {};
		staticFunctionRets[name][0] = returnValueLength;
	}
	else {
		var index = getArraySize(staticFunctionRets[name]);
		staticFunctionRets[name][index] = returnValueLength;
		console.log(name, ' -- ', staticFunctionRets[name]);
	}
}

function getArraySize(theArray) {
	var arraySize = 0;
	while (theArray[arraySize] != null && typeof theArray[arraySize] != 'undefined')
		arraySize ++;
	return arraySize;
}
******/
/***************************/
/** END {KEEPING TRACK OF STATIC FUNCTION ARGUMENTS AND RETURN VALUES} **/
/***************************/


//console.log('NUM OF EXISTING SCRIPT TAGS: ', document.getElementsByTagName('script').length);

/***********************************/
/*** BEGIN { LOGGING FUNCTIONS } ***/
/***********************************/
/**
 * Logging functions must be added to the beginning of the document since they will be used by the document after instrumentation.
 * They are not added to the script contents since their JavaScript content is separate than the content of the page.
 **/
 /*
var funcEnterScript = document.createElement("script");
funcEnterScript.setAttribute('instrumented', 'true');
funcEnterScript.innerHTML = "function _functionEnter(args) {console.log('ENTERed function', args.callee.name);}";

var funcExitScript = document.createElement("script");
funcExitScript.setAttribute('instrumented', 'true');
funcExitScript.innerHTML = "function _functionExit(args) {console.log('EXITed function', args.callee.name);}";

var funcReturnScript = document.createElement("script");
funcReturnScript.setAttribute('instrumented', 'true');
funcReturnScript.innerHTML = "function _functionReturn(args, orig_return) {console.log('RETURNed function', args.callee.name, ' -- ', orig_return);}";

if (document.head.children.length > 0) {
	document.head.insertBefore(funcReturnScript, document.head.children[0]);
	document.head.insertBefore(funcExitScript, document.head.children[0]);
	document.head.insertBefore(funcEnterScript, document.head.children[0]);
}
else {
	document.head.children[0] = funcEnterScript;
	document.head.children[1] = funcEnterScript;
	document.head.children[2] = funcReturnScript;
}
*/
/*
var logScript = document.createElement("script");
logScript.setAttribute('instrumented', 'true');
var url = chrome.extension.getURL('log/log-funcs.js');
logScript.setAttribute('src', url);

if (document.head.children.length > 0) {
	document.head.insertBefore(logScript, document.head.children[0]);
}
else {
	document.head.children[0] = logScript;
}
*/
/*********************************/
/*** END { LOGGING FUNCTIONS } ***/
/*********************************/





/**
 * This function is used for instrumenting the inline JavaScript code in HTML (script tags containing code>
 **/
function replaceInlineScriptNode(newElement) {
		var scriptText = newElement.innerHTML;
		var newScriptText = "";
					
		var instrumentedAst = instrumentAST(scriptText, document.URL); // TODO source page
					
		newScriptText = escodegen.generate(instrumentedAst);
		
// TODO  TODO  TODO  TODO  TODO  TODO  TODO
// TODO  TODO  TODO  TODO  TODO  TODO  TODO
		newScriptText = "if (__evaluateScript) {\n" + newScriptText + "\n}";
// TODO  TODO  TODO  TODO  TODO  TODO  TODO
// TODO  TODO  TODO  TODO  TODO  TODO  TODO


		var newScript = document.createElement("script");
		newScript.setAttribute('instrumented', 'true');
		newScript.innerHTML = newScriptText;

		newElement.parentNode.replaceChild(newScript, newElement);
		// TODO TODO TODO TODO TODO ??????????????????
		/********* duplication
		var replacedNode = newElement.parentNode.replaceChild(newScript, newElement);
		return replacedNode;
		**********/
}

/**
 * This function is used for instrumenting external JavaScript files that are included in the HTML document
 **/
function replaceExternalScriptNode(newElement, srcAttr) {

//	console.log('--------------- ', srcAttr);

/*
	// get the external file through an xhr
	var xhr = new XMLHttpRequest();
	xhr.open('GET', srcAttr, true);
	xhr.send();
	xhr.onreadystatechange = function() {
		if (this.readyState == 4) {
			var instrumentedAst = instrumentAST(xhr.responseText);
							
			var instrumentedScript = escodegen.generate(instrumentedAst);
			newElement.innerHTML = instrumentedScript;
							
			newElement.removeAttribute('src');
			newElement.setAttribute('original_src', srcAttr);
			newElement.setAttribute('instrumented', 'true');
		}
	}
*/	
	//
	// replacing xmlhttpreques with the original xmlhttprequest
	//
	// get the external file through an xhr
	var xhr = new XMLHttpRequest();//XMLHttpRequest_original();
	xhr.open('GET', srcAttr, true);
	xhr.send();
	xhr.onreadystatechange = function() {
		if (this.readyState == 4) {
			var instrumentedAst = instrumentAST(xhr.responseText, srcAttr); // TODO source
							
			var instrumentedScript = escodegen.generate(instrumentedAst);
			
			// TODO  TODO  TODO  TODO  TODO  TODO  TODO
// TODO  TODO  TODO  TODO  TODO  TODO  TODO
		instrumentedScript = "if (__evaluateScript) {\n" + instrumentedScript + "\n}";
// TODO  TODO  TODO  TODO  TODO  TODO  TODO
// TODO  TODO  TODO  TODO  TODO  TODO  TODO


			newElement.innerHTML = instrumentedScript;
							
			newElement.removeAttribute('src');
			newElement.setAttribute('original_src', srcAttr);
			newElement.setAttribute('instrumented', 'true');
		}
	}
}

/**
 * instrument all existing script tags that were loaded before this file
 **/
var existingScriptTags = document.getElementsByTagName('script');
if (existingScriptTags == null || typeof existingScriptTags == 'undefined') {
	console.warn('existingScriptTags UNDEFINED');
}
else {
//	console.log('++++++++ ', existingScriptTags); ///////
for (var i = 0; i < existingScriptTags.length; i ++) {
	var newElement = existingScriptTags[i];

//	console.log('existing script: ' + newElement.getAttribute('src'));

	var instrumented = newElement.getAttribute('instrumented');
	if (instrumented == 'true') {
//		console.log('already instrumented');
		continue; // TODO TODO TODO
	}

	var srcAttr = newElement.getAttribute('src');

	// script code inside script tags in html
	if (srcAttr == null || srcAttr == 'undefined') {
		var replacedNode = replaceInlineScriptNode(newElement, document.URL);  // TODO page source
	}
	else { // external script files
		// TODO TODO TODO
		if (mustBeInstrumented(srcAttr)) {
			replaceExternalScriptNode(newElement, srcAttr);
		}
		// TODO TODO TODO

	}
}
}
/**
 * Define an observer to detect all new script tags that are added to the page
 **/
var observer = new MutationSummary({
	callback: handleScriptChanges,
	queries: [{ element: 'script' }]
});

/**
 * Stage new script tags to be instrumented and replaced
 **/
function handleScriptChanges(summaries) {
	var hTweetSummary = summaries[0];

	if (hTweetSummary == null || typeof hTweetSummary == 'undefined') {
		console.warn('hTweetSummary UNDEFINED');
		return;
	}
	
//	console.log('num of scripts added: ', hTweetSummary.length)

	// Parse and instrument every new added script tag
	hTweetSummary.added.forEach(function(newElement) {
//		console.log(">>> new element: ", newElement);
	// do setup work on new elements with data-h-tweet
//					console.log('================ ', newElement, ' - ', newElement.getAttribute('instrument'));

					//console.log('=== ', newElement);

					// Do not instrument the script tag if it's already instrumented by the algorithm
		var instrumentedBefore = newElement.getAttribute('instrumented');
		if (instrumentedBefore == 'true') {
//			console.log('INSTRUMENTED BEFORE');
			return; // continue; TODO TODO TODO
		}

		var srcAttr = newElement.getAttribute('src');
					
		// Script code inside script tags in html
		if (srcAttr == null || srcAttr == 'undefined') {
			console.log('inline script');

			replaceInlineScriptNode(newElement);
		}
		else { // External script files

			// TODO TODO TODO
			if (!mustBeInstrumented(srcAttr))
				return;
			// TODO TODO TODO
					
			if (srcAttr.toLowerCase().indexOf('clematis') > -1 || srcAttr.toLowerCase().indexOf('jquery') > -1 || srcAttr.toLowerCase().indexOf('toolbar') > -1) {
				console.log('LIBRARY');
				return; // continue; TODO TODO TODO
			}
//			console.log('------- src: ', srcAttr);
						
			replaceExternalScriptNode(newElement, srcAttr);
		}
	});
}

/**
 * Extract and instrument the AST of a JavaScript code segment
 **/
function instrumentAST(scriptText, sourcePage) {
	var ast = esprima.parse(scriptText, {loc: true});
					
	estraverse.traverse(ast, {
		enter: function (node, parent) {
			// console.log('<><><> TYPE: ', node.type);
			
			if (node.type == 'ReturnStatement') {
			/*
				-------------------WORKING
				// TODO TODO TODO
				// Instrument return statements by wrapping the original statement as an argument of a wrapper function call
				var returnStatement = {};
				returnStatement["type"] = "CallExpression";
				returnStatement["callee"] = {};
				returnStatement["callee"]["type"] = "Identifier",
				returnStatement["callee"]["name"] = "_functionReturn";
				returnStatement["arguments"] = [];
				// the return statement is used without any arguments. for example: return;
				if (node.argument == null) {
					returnStatement["arguments"] = [];
				}
				else {
					returnStatement["arguments"][0] = node.argument;
				}
				node.argument = returnStatement;
			*/	
				
								// TODO TODO TODO
				// Instrument return statements by wrapping the original statement as an argument of a wrapper function call
				var returnStatement = {};
				returnStatement["type"] = "CallExpression";
				returnStatement["callee"] = {};
				returnStatement["callee"]["type"] = "Identifier",
				returnStatement["callee"]["name"] = "_functionReturn";
				returnStatement["arguments"] = [];
				// the return statement is used without any arguments. for example: return;
				var originalArguments = {};
				originalArguments["type"] = "Identifier";
				originalArguments["name"] = "arguments";

				if (node.argument == null) {
					returnStatement["arguments"][0] = originalArguments;
				}
				else {
					returnStatement["arguments"][0] = originalArguments;
					returnStatement["arguments"][1] = node.argument;
				}
				node.argument = returnStatement;
				
			}

			if (node.type == 'FunctionDeclaration' || node.type == 'FunctionExpression') {
			
				var name;

				// TODO TODO TODO TODO TODO TODO
				if (node.type == 'FunctionDeclaration') {
					// TODO TODO TODO TODO TODO TODO
					if (node.id.name == '_functionEnter' || node.id.name == '_functionExit')
						return estraverse.VisitorOption.Skip; //////////////// TODO TODO TODO ?????????????
					
					name = node.id.name;
//					console.log('***** DEC: ', name);
				}
							
				var functionBody = node.body.body;

				// TODO  TODO  TODO  TODO  TODO  TODO  TODO ???
				if (functionBody == null || functionBody == 'undefined') {//} || functionBody.length < 1) {
					console.warn('func-inst.js::instrumentAST -> empty function body');
				}
//								console.log(functionBody.length);


				// TODO TODO TODO  TODO TODO TODO ///////////////
				if (node.type == 'FunctionExpression') {
					name = node.id;
					if (name == null || typeof name == 'undefined') {
						// TODO TODO TODO
						name = sourcePage + ':' + node.body.loc.start.line + ':' + node.body.loc.start.column; ///// + '-' + node.body.loc.end.line + ':' + node.body.loc.end.column;
						// TODO TODO TODO
					}
				}
				// TODO TODO TODO  TODO TODO TODO ///////////////

				// TODO TODO ///////////////////////
				if (node.params.length == 0) {
					// saba here
					////////////////////////////console.log('static: with NO args - ', window.numOfFuncsWithNoStaticArgs ++);
				}
				else {
					// saba here
					////////////////////////////console.log('static: with args - ', window.numOfFuncsWithStaticArgs ++);
				}
				// TODO TODO TODO TODO
				logStaticFunctionName(name, node.params);
				// NEW VERSION SABA
				/***********
				addStaticFunctionArgs(name, node.params); // TODO TODO TODO TODO
				***********/
				// TODO TODO TODO TODO
							
				// At the end of the execution of this function, this variable will contain the instrumented function body
				var instrumentedBody = [];
								
				// Instrument the beginning of the body by adding a logger function for entering each function
				var functionEnter = {};
				functionEnter["type"] = "ExpressionStatement";
				var expressionField = {};
				expressionField["type"] = "CallExpression";
				var calleeField = {};
				calleeField["type"] = "Identifier";
				calleeField["name"] = "_functionEnter";
				expressionField["callee"] = calleeField;
				var argumentField = [];
				var originalArguments = {};
				originalArguments["type"] = "Identifier";
				originalArguments["name"] = "arguments";
				argumentField[0] = originalArguments;
				expressionField["arguments"] = argumentField;
				functionEnter["expression"] = expressionField;
							
				// Instrument the end of the body (before a return statement if one exists) to log function exits
				var functionExit = {};
				functionExit["type"] = "ExpressionStatement";
				var expressionField_exit = {};
				expressionField_exit["type"] = "CallExpression";
				var calleeField_exit = {};
				calleeField_exit["type"] = "Identifier";
				calleeField_exit["name"] = "_functionExit";
				expressionField_exit["callee"] = calleeField_exit;
				var argumentField_exit = [];
				var originalArguments_exit = {};
				originalArguments_exit["type"] = "Identifier";
				originalArguments_exit["name"] = "arguments";
				argumentField_exit[0] = originalArguments_exit;
				expressionField_exit["arguments"] = argumentField_exit;
				functionExit["expression"] = expressionField_exit;
					//
//					console.log("copying functionEnter");
				instrumentedBody[0] = functionEnter;

				if (functionBody == null || typeof functionBody == 'undefined') {
					console.warn('functionBody UNDEFINED');
					// TODO TODO TODO
				}
				
				if (functionBody.length == 0) {
					instrumentedBody[1] = functionExit;
				}
				else {
				
					var lastExpression = functionBody[functionBody.length - 1];
//						console.log("length: ", functionBody.length);
//						console.log("lastExpression: ", lastExpression);
					if (lastExpression == null || lastExpression == undefined) {
						console.log(functionBody);
						console.log('>>');
					}
					if (lastExpression.type == 'ReturnStatement') {
					// Do not add _functionExit if the last statement is a return statement since the return statements must be instrumented separately
					// Just shift the statements to make room for _functionEnter
					
//						for (var i = 0; i < functionBody.length - 1; i ++) {
						for (var i = 0; i < functionBody.length; i ++) {
							instrumentedBody[i + 1] = functionBody[i];
						}
					/*	instrumentedBody[instrumentedBody.length] = functionExit;
						instrumentedBody[instrumentedBody.length] = functionBody[functionBody.length - 1];
						*/
					}
					else {
					// Instrument function exit if the function does not end with a return statement
						for (var i = 0; i < functionBody.length; i ++) {
							instrumentedBody[i + 1] = functionBody[i];
						}
						instrumentedBody[instrumentedBody.length] = functionExit;
					}
				}
								
				node.body.body = instrumentedBody; ////////////////////////////// TODO TODO TODO
															
//							console.log('FunctionDeclaration', node.id.name)
//							return estraverse.VisitorOption.Skip;
			}
/*				if (node.type == 'FunctionExpression') {
//								console.log('FunctionExpression', node.id.name)
	//							return estraverse.VisitorOption.Skip;
				}
*/
				// TODO TODO
			if (node.type == 'CallExpression') {
//								console.log('CallExpression', node.callee.name)
			}
		},
		leave: function (node, parent) {
			if (node.type == 'VariableDeclarator') {
//					console.log('VariableDeclarator', node.id.name);
//					node.id.name = "asdf";
			}
		}
	});
					
	return ast;
}


//	console.log('5. FUNCTION INST END');
