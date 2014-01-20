window.xhr = new XMLHttpRequest();


window.buffer = new Array();

function send(value) {
	window.buffer.push(value);
}
/*
function sendReally() {
    if (window.buffer.length > 0) { 
    	window.xhr.open('POST', document.location.href + '?DOMACCESSLOG', false);
//    	window.xhr.send('['+(window.buffer).toString()+']');
    	window.xhr.send(window.buffer).toString());
    	window.buffer = new Array();
    }
}
setInterval(sendReally, 8000); // ?
*/

function sendDomElementAccesses() {
///////////////////////////////	alert("sendDomElementAccesses");
//////////////////	console.log(document);
	/*
	console.log("---1");
	console.log(document.documentElement == document.getElementsByTagName('html')[0]); //false
	console.log("---2");
	console.log(document.documentElement instanceof HTMLElement); // false
	console.log("---3");
	console.log(document.getElementsByTagName('html')[0] instanceof HTMLElement); //true
	console.log("---4");
	console.log(Object.prototype.toString.call(document.documentElement)); // [object HTMLHtmlElement]
	console.log("---5");
	console.log(Object.prototype.toString.call(document.getElementsByTagName('html')[0])); //[object Object]
	console.log("---6");
	console.log(document.documentElement.contains(document.querySelector('#ID'))); //TypeError: Value does not implement interface Node.
	console.log("---7");
	console.log(document.getElementsByTagName('html')[0].contains(document.querySelector('#ID'))); //true
	console.log("---8");
	*/
	
//	var allElements = document.getElementsByTagName("*");
	console.log("++");
	console.log(window.document);
	var allElements = getElementsByTagName_original.call(window.document, "*"); /////??????????????????????????????
	console.log(allElements.length);

	for (var i = 0; i < allElements.length; i++) {
//		console.log(i);
		var element = allElements[i];
		var id = getAttribute_original.call(element, "id");
		var accessType = getAttribute_original.call(element, ACCESS_TYPE_LABEL);
		var accessFunction = getAttribute_original.call(element, ACCESS_FUNCTION_LABEL);
		
		if (accessType == null) {
			continue;
		}

		var msg = "ID:" + id + ". ACCESST_TYPES:" + accessType + ". ACCESS_FUNCTIONS:" + accessFunction;
		
    	window.xhr.open('POST', document.location.href + '?DOMACCESSLOG', false);
    	window.xhr.send(msg.toString());
    	
    	console.log(msg);

    	removeAttribute_original.call(element, ACCESS_TYPE_LABEL);
    	removeAttribute_original.call(element, ACCESS_FUNCTION_LABEL);
    	
	}
	console.log("============== " + window.buffer.length);
	sendXhrLog();
}

function sendXhrLog() {
    if (window.buffer.length > 0) { 
    	window.xhr.open('POST', document.location.href + '?XHRACCESSLOG', false);
//    	window.xhr.send('['+(window.buffer).toString()+']');
    	window.xhr.send((window.buffer).toString());
    	window.buffer = new Array();
    }

}

setInterval(sendDomElementAccesses, 5000);



