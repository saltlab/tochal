var evaluateVarHolder = document.createElement('script');
evaluateVarHolder.innerHTML = 'var __evaluateScript = true;';
evaluateVarHolder.setAttribute('type', 'text/javascript');
evaluateVarHolder.setAttribute('instrumented', 'true');


//var temp = document.createElement('div'); temp.innerText = 'hello'; temp.setAttribute('id', 'tempDIVProteus');

var empResultScript = document.createElement("script");
empResultScript.setAttribute('type', 'text/javascript');
empResultScript.setAttribute('instrumented', 'true');
empResultScript.setAttribute('src', chrome.extension.getURL('empirical/injected-result-div.js'));
//tempScript.innerHTML = "var __evaluateScript = false; var temp = document.createElement('div'); temp.innerHTML = 'hello'; temp.setAttribute('id', 'tempDIVProteus'); temp.addEventListener('click', function() {alert('event listener');}, false); document.body.insertBefore(temp, document.body.children[0]);";



var logDynamicScriptText = "var dynamicFunctionNames = [];" +
"var dynamicFunctionArgs = [];" +
"function logDynamicFunctionName(name, args) {" +
	"if (dynamicFunctionNames[name] == null || typeof dynamicFunctionNames[name] == 'undefined') {" +
		"var newIndex = dynamicFunctionArgs.length;" +
		"dynamicFunctionNames[name] = newIndex;" +
		"dynamicFunctionArgs[newIndex] = [];" +
		"dynamicFunctionArgs[newIndex][0] = args.length;" +
	"}" +
	"else {" +
		"var funcIndex = dynamicFunctionNames[name];" +
		"var lastArgsIndex = dynamicFunctionArgs[funcIndex].length;" +
		"dynamicFunctionArgs[funcIndex][lastArgsIndex] = args.length;" +
	"}" +
"}";

var logDynamicScript = document.createElement("script");
logDynamicScript.setAttribute('type', 'text/javascript');
logDynamicScript.setAttribute('src', chrome.extension.getURL('log/dynamic-func-info.js'));
//logDynamicScript.innerHTML = logDynamicScriptText; //setAttribute('src', chrome.extension.getURL('log/dynamic-func-info.js'));
logDynamicScript.setAttribute('instrumented', 'true');



var funcNamingScriptText = "function getErrorObject() {" +
"   try { throw Error('') } catch(err) { return err; }" +
"}" +
"function getStackTrace(e) {" +
"return e.stack.replace(/(?:\\n@:0)?\\s+$/m, '')" +
               ".replace(/^(?:\\((\\S*)\\))?@/gm, '{anonymous}($1)@')" +
               ".split('\\n');" +
"}" +
"function getCallerFunctionName(args) {" +
	"var caller = args.callee.caller;" +
	"var callerName = 'null';" +
	"if (caller != null)" +
		"if (caller.name != '')" +
			"callerName = args.callee.caller.name;" +
	"if (callerName == 'null') {" +
		"var err = getErrorObject();" +
		"var stackTrace = getStackTrace(err);" +
		"if (stackTrace.length > 0) {" +
			"var latestFunction = stackTrace[stackTrace.length - 1];" +
			"console.log('++++++++++++++ ', latestFunction);" +
			"var scope = latestFunction;" +
			"callerName = scope;" +
		"}" +
	"}" +
	"return callerName;" +
"}";

var funcNamingScript = document.createElement("script");
funcNamingScript.setAttribute('type', 'text/javascript');
funcNamingScript.setAttribute('src', chrome.extension.getURL('empirical/function-naming.js'));
// funcNamingScript.innerHTML = funcNamingScriptText; //setAttribute('src', chrome.extension.getURL('empirical/function-naming.js'));
funcNamingScript.setAttribute('instrumented', 'true');



var logScriptText = "function _functionEnter(args) {" +
	"alert('_functionEnter');" +
	"var locName = getCallerFunctionName(args);" +
	"logDynamicFunctionName(locName, args);" +
	"console.log('ENTER function: ', locName);" +
"}" +
"function _functionExit(args) {" +
	"console.log('EXIT function: ', getCallerFunctionName(args));" +
"}" +
"function _functionReturn(args, orig_return) {" +
	"var locName = getCallerFunctionName(args);" +
	"console.log('RETURN function: ', locName);" +
	"return orig_return;" +
"}";

var logScript = document.createElement("script");
logScript.setAttribute('type', 'text/javascript');
logScript.setAttribute('src', chrome.extension.getURL('log/log-funcs.js'));
// logScript.innerHTML = logScriptText; //setAttribute('src', chrome.extension.getURL('log/log-funcs.js'));
logScript.setAttribute('instrumented', 'true');

////////////////// XHRS


var xhrScript = document.createElement("script");
xhrScript.setAttribute('type', 'text/javascript');
xhrScript.setAttribute('src', chrome.extension.getURL('async-inst/xhr-inst.js'));
xhrScript.setAttribute('instrumented', 'true');


var domScript = document.createElement("script");
domScript.setAttribute('type', 'text/javascript');
domScript.setAttribute('src', chrome.extension.getURL('dom-inst/dom-inst.js'));
domScript.setAttribute('instrumented', 'true');


//document.body.insertBefore(tempScript, document.body.children[0]);

if (document.head.children.length > 0) {
	document.head.insertBefore(domScript, document.head.children[0]);
	document.head.insertBefore(xhrScript, document.head.children[0]);
	document.head.insertBefore(empResultScript, document.head.children[0]);
	document.head.insertBefore(logScript, document.head.children[0]);
	document.head.insertBefore(funcNamingScript, document.head.children[0]);
	document.head.insertBefore(logDynamicScript, document.head.children[0]);
	document.head.insertBefore(evaluateVarHolder, document.head.children[0]);

///	document.head.insertBefore(tempScript, document.head.children[0]);
//	document.head.insertBefore(temp, document.head.children[0]);
}
else {
//	document.head.children[0] = temp;
//	document.head.children[0] = tempScript;
	document.head.children[0] = evaluateVarHolder;
	document.head.children[1] = logDynamicScript;
	document.head.children[2] = funcNamingScript;
	document.head.children[3] = logScript;
	document.head.children[4] = empResultScript;
	document.head.children[5] = xhrScript;
	document.head.children[6] = domScript;
	
}
