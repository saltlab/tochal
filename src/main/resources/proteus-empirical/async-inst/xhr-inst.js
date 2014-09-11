/**********************/
/*** XMLHTTPREQUEST ***/
/**********************/

var XMLHttpRequest_original = XMLHttpRequest;

XMLHttpRequest = function() {

	var xhr = new XMLHttpRequest_original();

	// TODO UNIQUE RANDOM ID?
//////////////	var id = generateRandomUniqueXHRId();
//////////////	xhr.id = id;
	/*
	var caller = arguments.callee.caller;
	console.log("caller 1 : " + caller);
	var callerName = "null";
	if (caller != null)
		callerName = arguments.callee.caller.name;
	console.log("++++++++ " + callerName);
	*/
/////////////////	var callerName = getCallerFunctionName(arguments);

	var open_original = xhr.open;
	xhr.open = function(method, url, async) {
		var caller_open = arguments.callee.caller;
		/*
		console.log("caller OPEN : " + caller_open);
*/		var callerName_open = "null";
		if (caller_open != null)
			callerName_open = caller_open.name;
/*		console.log("++++++++ " + callerName_open);
*/	
////////////////	logger.logXHROpen(xhr, method, callerName_open);
	//	alert('logXHROpen');
		__xhrOpen([xhr, method, url, async]);
		return open_original.apply(this, [ method, url, async ]);

	}

	var send_original = xhr.send;
	xhr.send = function(str) {

//////////////////		var caller_send = getCallerFunctionName(arguments);
		/*
		var caller_send = arguments.callee.caller;
		console.log("caller SEND : " + caller_send);
		var callerName_send = "null";
		if (caller_send != null)
			callerName_send = caller_send.name;		
		console.log("++++++++ " + callerName_send);
		*/
/////////////////		logger.logXHRSend(xhr, callerName_send);
	//	alert('logXHRSend');
		__xhrSend([xhr, str]);
		return send_original.apply(this, [ str ]);
	}

	var onreadystatechange = function() {
		if (this.readyState == 4) {
//////////////////			var caller_response = getCallerFunctionName(arguments);
//			xhrCounter--;
			/*
			var caller_response = arguments.callee.caller;
			console.log("caller RESPONSE : " + caller_response);			
			var callerName_response = "null";
			if (caller_response != null)
				callerName_response = caller_response.name;		
			console.log("++++++++ " + callerName_response);
			*/
//////////////////			logger.logXHRResponse(this, callerName_response);
		//	alert('logXHRResponse');
		__xhrResponse(xhr);
		}
	}

	var onload = function() {
//		xhrCounter--;
/////////////////		var caller_response = getCallerFunctionName(arguments);
		/*
		var caller_response = arguments.callee.caller;
		console.log("caller RESPONSE - LOAD : " + caller_response);			
		var callerName_response = "null";
		if (caller_response != null)
			callerName_response = caller_response.name;		
		console.log("++++++++ " + callerName_response);
		*/
////////////////		logger.logXHRResponse(this, callerName_response);
		//alert('logXHRResponse 2');
		__xhrResponse(xhr);
	}

	xhr.addEventListener("readystatechange", onreadystatechange, false);

	return xhr;
}