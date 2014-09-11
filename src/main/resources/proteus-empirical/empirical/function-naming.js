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

function getCallerFunctionName(args) {
	var caller = args.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = args.callee.caller.name;

	if (callerName == "null") {
		var err = getErrorObject();
		// get the last url
		var stackTrace = getStackTrace(err);

		if (stackTrace.length > 0) {
			var latestFunction = stackTrace[stackTrace.length - 1];
			// console.log('++++++++++++++ ', latestFunction);
			// tokenize the string
	////////////////////////////////		var arrayOfStrings = latestFunction.split("/");

	////////////////////////////////		var scope = arrayOfStrings[arrayOfStrings.length - 1];
	
			var scope = latestFunction; // TODO TODO TODO
			callerName = scope; // TODO TODO TODO
	
	/**********
			console.log('(): ', callerName);
			console.log('()(): ', scope);
			callerName = callerName + "+" + scope; // TODO

			if (callerName.length > 1)
				callerName = callerName.substring(0, callerName.length - 1);
**********/
		}
	}

	return callerName;
}
//END
