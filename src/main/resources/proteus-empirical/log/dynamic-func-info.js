//var __evaluateScript = false;
///////////////////////

/***************************/
/** BEGIN {KEEPING TRACK OF DYNAMIC FUNCTION ARGUMENTS AND RETURN VALUES} **/
/***************************/

var dynamicFunctionNames = [];
var dynamicFunctionArgs = [];

function logDynamicFunctionName(name, args) {
	if (dynamicFunctionNames[name] == null || typeof dynamicFunctionNames[name] == 'undefined') {
		var newIndex = dynamicFunctionArgs.length;
		dynamicFunctionNames[name] = newIndex;
		dynamicFunctionArgs[newIndex] = [];
		dynamicFunctionArgs[newIndex][0] = args.length;
	}
	else {
		var funcIndex = dynamicFunctionNames[name];
		var lastArgsIndex = dynamicFunctionArgs[funcIndex].length;
		dynamicFunctionArgs[funcIndex][lastArgsIndex] = args.length;
	}
}


// TODO 	RETURN VALUES


/***************************/
/** END {KEEPING TRACK OF DYNAMIC FUNCTION ARGUMENTS AND RETURN VALUES} **/
/***************************/

// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
/******
chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
  	alert('saba');
    console.log(sender.tab ?
                "from a content script:" + sender.tab.url :
                "from the extension");
    if (request.greeting == "hello")
      sendResponse({farewell: "goodbye"});
  });
*****/
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
