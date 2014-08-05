//////////////////var __evaluateScript = false;

/*******************/
/** FUNCTION LOGS **/
/*******************/
function _functionEnter(args) {
	var locName = getCallerFunctionName(args);
	// addDynamicFunctionArgs(locName, args);
	logDynamicFunctionName(locName, args);
	console.log('FUNCTION ENTER: ', locName);
/*	var name = args.callee.name;
	if (name == null || typeof name == 'undefined') {

	}
	console.log('1. ENTERed function', args.callee.name);
	if(args.length == 0) {
		console.log('dynamic: with NO args - ', window.numOfFuncsWithNoDynamicArgs ++);
	}
	else {
		console.log('dynamic: with args - ', window.numOfFuncsWithDynamicArgs ++);
	}
	*/
}

function _functionExit(args) {
	console.log('FUNCTION EXIT: ', getCallerFunctionName(args));
	// console.log('1. EXITed function', args.callee.name, ' -- ', args.length);
}

function _functionReturn(args, orig_return) {
	var locName = getCallerFunctionName(args);
	// addDynamicFunctionRets(locName, orig_return);
	// console.log('1. RETURNed function', args.callee.name, ' -- ', orig_return, ' -- ', args.length);
	console.log('FUNCTION RETURN: ', locName);
	return orig_return;
}

/*******************/
/**** XHR LOGS *****/
/*******************/

var numOfXHROpen = 0;
var numOfXHRSend = 0;
var numOfXHRResponse = 0;

function __xhrOpen(args) {
	numOfXHROpen ++;
	console.log('XHR OPEN: ', args);
}

function __xhrSend(args) {
	numOfXHRSend ++;
	console.log('XHR SEND: ', args);
}

function __xhrResponse(args) {
	numOfXHRResponse ++;
	console.log('XHR RESPONSE: ', args);
}
