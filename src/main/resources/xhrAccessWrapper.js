var logger = {};

/**
 * Prints the information related to creation of a XMLHTTPRequest object to the
 * console
 */
logger.logXHROpen = function(xhr, method, callerName) {
	console.log("------------------------------------");
	console.log("XMLHTTPREQUEST: OPEN");

	console.log(" + XHR ID: ", xhr.id);
	console.log(" + Method: ", method);
//	console.log(" + URL: ", url);
//	console.log(" + Async: ", async);

//    send(JSON.stringify({messageType: "XHR_OPEN", timeStamp: date, id: xhr.id, methodType: method, url: url, async: async, counter: traceCounter++}));
  send_dom(JSON.stringify({messageType: "XHR_OpenAccess", id: xhr.id, accessFunction: callerName}));
};

/**
 * Prints the information related to sending a XHR object to server on the
 * console
 */
logger.logXHRSend = function(xhr, callerName) {
	console.log("------------------------------------");
	console.log("XMLHTTPREQUEST: SEND");
	console.log(" + XHR ID: ", xhr.id);
//	console.log(" + Message (POST):", str);

//    send(JSON.stringify({messageType: "XHR_SEND", timeStamp: date, id: xhr.id, message: str, counter: traceCounter++}));
	  send_dom(JSON.stringify({messageType: "XHR_SendAccess", id: xhr.id, accessFunction: callerName}));
};

/**
 * Prints the information related to getting the response of a XHR object and
 * executing the callback function on the console
 */
logger.logXHRResponse = function(xhr, callerName) {
	console.log("------------------------------------");
	console.log("XMLHTTPREQUEST: RESPONSE");

	console.log(" + XHR ID: ", xhr.id);
	console.log(" + XHR callback function: ", xhr.onreadystatechange);
//	console.log(" + XHR response headers: ", xhr.getAllResponseHeaders());
//	console.log(" + XHR response: ", xhr.response);

	send_dom(JSON.stringify({messageType: "XHR_ResponseAccess", id: xhr.id, accessFunction: callerName}));

/*    if (xhr.onreadystatechange != null) {
    	send(JSON.stringify({messageType: "XHR_RESPONSE", id: xhr.id, callbackFunction: xhr.onreadystatechange.name, accessFunction: callerName}));
    } else if (xhr.onload != null) {
    	send(JSON.stringify({messageType: "XHR_RESPONSE", id: xhr.id, callbackFunction: xhr.onload.name, accessFunction: callerName}));
    } else {
    	send(JSON.stringify({messageType: "XHR_RESPONSE", id: xhr.id, callbackFunction: "", accessFunction: callerName}));
	}
	*/
};


/**********************/
/*** XMLHTTPREQUEST ***/
/**********************/

var XMLHttpRequest_original = XMLHttpRequest;

XMLHttpRequest = function() {

	var xhr = new XMLHttpRequest_original();

	// TODO UNIQUE RANDOM ID?
	var id = generateRandomUniqueXHRId();
	xhr.id = id;
	/*
	var caller = arguments.callee.caller;
	console.log("caller 1 : " + caller);
	var callerName = "null";
	if (caller != null)
		callerName = arguments.callee.caller.name;
	console.log("++++++++ " + callerName);
	*/
	var callerName = getCallerFunctionName(arguments);
	
	var open_original = xhr.open;
	xhr.open = function(method, url, async) {
		var caller_open = arguments.callee.caller;
		console.log("caller OPEN : " + caller_open);
		var callerName_open = "null";
		if (caller_open != null)
			callerName_open = caller_open.name;
		console.log("++++++++ " + callerName_open);
		logger.logXHROpen(xhr, method, callerName_open);
		return open_original.apply(this, [ method, url, async ]);

	}

	var send_original = xhr.send;
	xhr.send = function(str) {
		
		var caller_send = getCallerFunctionName(arguments);
		/*
		var caller_send = arguments.callee.caller;
		console.log("caller SEND : " + caller_send);
		var callerName_send = "null";
		if (caller_send != null)
			callerName_send = caller_send.name;		
		console.log("++++++++ " + callerName_send);
		*/
		logger.logXHRSend(xhr, callerName_send);
		return send_original.apply(this, [ str ]);
	}
	
	var onreadystatechange = function() {
		if (this.readyState == 4) {
			var caller_response = getCallerFunctionName(arguments);
//			xhrCounter--;
			/*
			var caller_response = arguments.callee.caller;
			console.log("caller RESPONSE : " + caller_response);			
			var callerName_response = "null";
			if (caller_response != null)
				callerName_response = caller_response.name;		
			console.log("++++++++ " + callerName_response);
			*/
			logger.logXHRResponse(this, callerName_response);
		}
	}
	
	var onload = function() {
//		xhrCounter--;
		var caller_response = getCallerFunctionName(arguments);
		/*
		var caller_response = arguments.callee.caller;
		console.log("caller RESPONSE - LOAD : " + caller_response);			
		var callerName_response = "null";
		if (caller_response != null)
			callerName_response = caller_response.name;		
		console.log("++++++++ " + callerName_response);
		*/
		logger.logXHRResponse(this, callerName_response);
	}

	xhr.addEventListener("readystatechange", onreadystatechange, false);

	return xhr;
}
