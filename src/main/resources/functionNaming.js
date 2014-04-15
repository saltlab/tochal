/**********************
GET THE FILE NAME
AND THE LINE NUMBER
OF THE CALLER FUNCTION
**********************/
//BEGIN
function getErrorObject() {
   try { throw Error('') } catch(err) { return err; }
}

/**********
FOR FIREFOX
**********/
function getStackTrace(e) {
return e.stack.replace(/(?:\n@:0)?\s+$/m, '')
               .replace(/^(?:\((\S*)\))?@/gm, '{anonymous}($1)@')
               .split('\n');
}

function getCallerFunctionName(argumens) {
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "") // TODO
			callerName = arguments.callee.caller.name;
	
	if (callerName == "null") {
		var err = getErrorObject();
//		console.log("1) err: ", err);
		// get the last url
		var stackTrace = getStackTrace(err);
//		for (var i = 0; i < stackTrace.length; i ++)
//			console.log(i + "))) " + stackTrace[i]);
		if (stackTrace.length > 0) {
			var latestFunction = stackTrace[stackTrace.length - 1];
			// tokenize the string
			var arrayOfStrings = latestFunction.split("/");
//			console.log("||||||||||| ", arrayOfStrings[arrayOfStrings.length - 1]);
/////////////////////			callerName = callerName + "+" + arrayOfStrings[arrayOfStrings.length - 1];
			var scope = arrayOfStrings[arrayOfStrings.length - 1];
			/*
			if ((scope.toLowerCase().indexOf("jquery") != -1) && (arrayOfStrings.length > 1)) // TODO TODO 
				scope = arrayOfStrings[arrayOfStrings.length - 3];
			*/
			callerName = callerName + "+" + scope; // TODO
			
			if (callerName.length > 1)
				callerName = callerName.substring(0, callerName.length - 1);
		}
	}
	
	return callerName;
}
//END
