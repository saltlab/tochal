//////////////////var __evaluateScript = false;
function send(value) {

    // Only record when intended
    if (!recordingInProgress) return; 

	window.buffer.push(value);
}

function sendReally() {
    if (window.buffer.length > 0) { 
    	window.xhr.open('POST', document.location.href + '?thisisafunctiontracingcall', false);
    	window.xhr.send('['+(window.buffer).toString()+']');
    	window.buffer = new Array();
    }
}
setInterval(sendReally, 8000);

function addVariable(name, value) {
	var pattern=/[.]attr[(]/;
	var getAttrPattern=/[.]getAttribute[(]/;
	if(typeof(value) == 'object') {
		if(value instanceof Array) {
				if(value[0] instanceof Array){
					
					if(value[0].length > 0) 
						//return new Array(name, typeof (value[0][0]) + '_array', value);
						return JSON.stringify({name: name, type: typeof (value[0][0]) + '_array', value: value});
				
					else
//						return new Array(name, 'object_array', value);
						return JSON.stringify({name: name, type: 'object_array', value: value});
				}
				else
					if(value.length > 0)
						//return new Array(name, typeof (value[0]) + '_array', value);
						return JSON.stringify({name: name, type: typeof (value[0]) + '_array', value: value});
					else 
						//return new Array(name, 'object_array', value);
						return JSON.stringify({name: name, type: 'object_array', value: value});
		}
	
	} else if(typeof(value) != 'undefined' && typeof(value) != 'function') {
	//	return new Array(name, typeof(value), value);
		return JSON.stringify({name: name, type: typeof(value), value: value});
	}
		else if (pattern.test(name) || getAttrPattern.test(name)){
		//	return new Array(name, 'string', value);//'java.lang.String');
		return JSON.stringify({name: name, type: 'string', value: value});
		}
	else if (name.match(pattern)==".attr("){
		//return new Array(name, 'string', 'java.lang.String');
		return JSON.stringify({name: name, type: 'string', value: 'java.lang.String'});
	}
	//return new Array(name, typeof(value), 'undefined');
	return JSON.stringify({name: name, type: typeof(value), value: 'undefined'});
}


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

