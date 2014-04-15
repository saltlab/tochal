var ACCESS_TYPE_LABEL = "access_type";
var ACCESS_FUNCTION_LABEL = "access_function";
var ACCESS_TYPE_ENUM = {
	GETELEMENTBYID		:	"GetElementById",
	GETELEMENTBYNAME	:	"GetElementByName",
	GETELEMENTBYTAGNAME	:	"GetElementByTagName",
	GETELEMENTBYCLASSNAME:	"GetElementByClassName",
	CREATEELEMENT		:	"CreateElement",
	GETATTRIBUTE		:	"GetAttribute",
	SETATTRIBUTE		:	"SetAttribute",
	REMOVEATTRIBUTE		:	"RemoveAttribute",
	GETELEMENTBYNAME	:	"GetElementByName",
	QUERYSELECTOR		:	"QuerySelector",
	QUERYSELECTORALL	:	"QuerySelectorAll",
	CREATETEXTNODE		:	"CreateTextNode",
	APPENDCHILD			:	"AppendChild",
	REMOVECHILD			:	"RemoveChild"
};

// Moved getCallerName to a separate file



/*********************
**					**
**  QUERY SELECTOR	**
**					**
*********************/

var querySelector_original = Document.prototype.querySelector;

Document.prototype.querySelector = function(selector) {
	var result = querySelector_original.call(this, selector);
/*	
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "") // TODO
			callerName = arguments.callee.caller.name;
*/	
	var callerName = getCallerFunctionName(arguments);
	
	var accessType = "";
	var accessFunction = "";

	console.log("12");
	for (var i = 0; i < result.length; i ++) {
		var element = result[i];
		accessType = getAccessType(element); // ???
		accessFunction = getAccessFunction(element); // ???
		
		accessType += ACCESS_TYPE_ENUM.QUERYSELECTOR;
		accessFunction += callerName;
		
		setAttribute_original.call(element, ACCESS_TYPE_LABEL, accessType); // ???
		setAttribute_original.call(element, ACCESS_FUNCTION_LABEL, accessFunction); // ???

	}
	
	return result;
}

var querySelectorAll_original = Document.prototype.querySelectorAll;

Document.prototype.querySelectorAll = function(selector) {
	var result = querySelectorAll_original.call(this, selector);
	/*
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "") // TODO
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);
	
	
	var accessType = "";
	var accessFunction = "";

	console.log("13");
	for (var i = 0; i < result.length; i ++) {
		var element = result[i];
		accessType = getAccessType(element); // ???
		accessFunction = getAccessFunction(element); // ???
		
		accessType += ACCESS_TYPE_ENUM.QUERYSELECTORALL;
		accessFunction += callerName;
		
		setAttribute_original.call(element, ACCESS_TYPE_LABEL, accessType); // ???
		setAttribute_original.call(element, ACCESS_FUNCTION_LABEL, accessFunction); // ???

	}
	
	return result;
}

/***************
** ATTRIBUTES **
***************/

////////// ACCESS ATTRIBUTES //////////

var getAttribute_original = Element.prototype.getAttribute;

Element.prototype.getAttribute = function (attrName) {
	// this == element
	var attr = getAttribute_original.call(this, attrName);
	
	// TODO
	if (this instanceof Element)
		console.warn("Element");
	else {
		console.warn("NOT Element");
		return;
	}
	/*
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "") // TODO
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);

		
	console.log("1");
	var accessType = getAccessType(this); // ???
	var accessFunction = getAccessFunction(this); // ???
	
	accessType += ACCESS_TYPE_ENUM.GETATTRIBUTE;
	accessFunction += callerName;
	
	setAttribute_original.call(this, ACCESS_TYPE_LABEL, accessType); // ???
	setAttribute_original.call(this, ACCESS_FUNCTION_LABEL, accessFunction); // ???

	return attr;
}

var setAttribute_original = Element.prototype.setAttribute;

Element.prototype.setAttribute = function (attr, value) {
	// this == element
	
	setAttribute_original.call(this, attr, value);
//	console.log("setAttribute, " + attr + " -> " + element);
	
	// TODO
	if (this instanceof Element)
		console.warn("Element");
	else {
		console.warn("NOT Element");
		return;
	}
/*
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);

		
	console.log("2");
	var accessType = getAccessType(this); // ???
	var accessFunction = getAccessFunction(this); // ???
	
	accessType += ACCESS_TYPE_ENUM.SETATTRIBUTE;
	accessFunction += callerName;
	
	setAttribute_original.call(this, ACCESS_TYPE_LABEL, accessType); // ???
	setAttribute_original.call(this, ACCESS_FUNCTION_LABEL, accessFunction); // ???
}

var removeAttribute_original = Element.prototype.removeAttribute;

Element.prototype.removeAttribute = function (attr) {
	removeAttribute_original.call(this, attr);
/*
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);

	
	console.log("3");
	var accessType = getAccessType(this); // ???
	var accessFunction = getAccessFunction(this); // ???
	
	accessType += ACCESS_TYPE_ENUM.REMOVEATTRIBUTE;
	accessFunction += callerName;
	
	setAttribute_original.call(this, ACCESS_TYPE_LABEL, accessType); // ???
	setAttribute_original.call(this, ACCESS_FUNCTION_LABEL, accessFunction); // ???
}

/*************
** ELEMENTS **
*************/

////////// GET ELEMENTS //////////

var getElementById_original = Document.prototype.getElementById;

Document.prototype.getElementById = function (id) {
	var element = getElementById_original.call(this, id);
	
	if (element == null)
		return element;
/*	
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);

		
	console.log("4");
	var accessType = getAccessType(element);
	var accessFunction = getAccessFunction(element);
	
	accessType += ACCESS_TYPE_ENUM.GETELEMENTBYID;
	accessFunction += callerName;
	
/*
	element.setAttribute(ACCESS_TYPE_LABEL, accessType);
	element.setAttribute(ACCESS_FUNCTION_LABEL, accessFunction);
*/
	setAttribute_original.call(element, ACCESS_TYPE_LABEL, accessType);
	setAttribute_original.call(element, ACCESS_FUNCTION_LABEL, accessFunction);
	
///////////////////	console.log("access: " + accessType + ", function: ", accessFunction);
///////////////////	console.log("element: " + id + " -> " + element);

	return element;
}

var getElementsByName_original = Document.prototype.getElementsByName;

Document.prototype.getElementsByName = function (name) {
	var elements = getElementsByName_original.call(this, name);
/*
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);

		
	console.log("5");
	var accessType = "";
	var accessFunction = "";

	for (var i = 0; i < elements.length; i ++) {
		accessType = getAccessType(elements[i]);
		accessFunction = getAccessFunction(elements[i]);
		
		accessType += ACCESS_TYPE_ENUM.GETELEMENTBYNAME;
		accessFunction += callerName;

		setAttribute_original.call(elements[i], ACCESS_TYPE_LABEL, accessType);
		setAttribute_original.call(elements[i], ACCESS_FUNCTION_LABEL, accessFunction);
	}

	return elements;
}

var getElementsByTagName_original = Document.prototype.getElementsByTagName;

Document.prototype.getElementsByTagName = function (tagName) {
	var elements = getElementsByTagName_original.call(this, tagName);
/*
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);

		
	console.log("6");
	var accessType = "";
	var accessFunction = "";

	for (var i = 0; i < elements.length; i ++) {
		accessType = getAccessType(elements[i]);
		accessFunction = getAccessFunction(elements[i]);
		
		accessType += ACCESS_TYPE_ENUM.GETELEMENTBYTAGNAME;
		accessFunction += callerName;

		setAttribute_original.call(elements[i], ACCESS_TYPE_LABEL, accessType);
		setAttribute_original.call(elements[i], ACCESS_FUNCTION_LABEL, accessFunction);
	}

	return elements;
}

var getElementsByClassName_original = Document.prototype.getElementsByClassName;

Document.prototype.getElementsByClassName = function (className) {
	var elements = getElementsByClassName_original.call(this, className);
/*
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);

		
	console.log("7");
	var accessType = "";
	var accessFunction = "";

	for (var i = 0; i < elements.length; i ++) {
		accessType = getAccessType(elements[i]);
		accessFunction = getAccessFunction(elements[i]);
		
		accessType += ACCESS_TYPE_ENUM.GETELEMENTBYCLASSNAME;
		accessFunction += callerName;

		setAttribute_original.call(elements[i], ACCESS_TYPE_LABEL, accessType);
		setAttribute_original.call(elements[i], ACCESS_FUNCTION_LABEL, accessFunction);
	}

	return elements;
}

////////// CREATE ELEMENTS //////////

var createElement_original = Document.prototype.createElement;

Document.prototype.createElement = function (tagName) {
	var element = createElement_original.call(this, tagName);
/*
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);

		
	console.log("8");
	var accessType = getAccessType(element);
	var accessFunction = getAccessFunction(element);
	
	accessType += ACCESS_TYPE_ENUM.CREATEELEMENT;
	accessFunction += callerName;

	setAttribute_original.call(element, ACCESS_TYPE_LABEL, accessType);
	setAttribute_original.call(element, ACCESS_FUNCTION_LABEL, accessFunction);

	return element;
}

/**************
**   NODES   **
**************/

////////// ADD / REMOVE CHILD //////////

var appendChild_original = Node.prototype.appendChild;

Node.prototype.appendChild = function (child) {
	var element = appendChild_original.call(this, child);

	if (element == null)
		return element;
	
	if (!(element instanceof Element)) // TODO if text is getting appended or any non-element value, ignore it
		return element;
/*	
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);


	console.log("9");
	var accessType = getAccessType(element);
	var accessFunction = getAccessFunction(element);
	
	accessType += ACCESS_TYPE_ENUM.APPENDCHILD;
	accessFunction += callerName;
	
	console.log("xxxxxxxxx appendChild::setAttribute on <", element, ">");
	setAttribute_original.call(element, ACCESS_TYPE_LABEL, accessType);
	setAttribute_original.call(element, ACCESS_FUNCTION_LABEL, accessFunction);

	return element;
}

var removeChild_original = Node.prototype.removeChild;

Node.prototype.removeChild = function (child) {
	var element = removeChild_original.call(this, child);
	if (element == null)
		return element;
	
	if (!(element instanceof Element)) // TODO if text is getting removed (or any non-element value), ignore it
		return element;
/*	
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
			*/
	var callerName = getCallerFunctionName(arguments);


	console.log("10");
	var accessType = getAccessType(element);
	var accessFunction = getAccessFunction(element);
	
	accessType += ACCESS_TYPE_ENUM.REMOVECHILD;
	accessFunction += callerName;
	
	console.log("xxxxxxxxx removeChild::setAttribute on <", element, ">");
	setAttribute_original.call(element, ACCESS_TYPE_LABEL, accessType);
	setAttribute_original.call(element, ACCESS_FUNCTION_LABEL, accessFunction);

	return element;
}

//////// Text Nodes ///////////
/*
var createTextNode_original = Document.prototype.createTextNode;

Document.prototype.createTextNode = function(text) {
	var node = createTextNode_original.call(this, text);
	
	if (node == null)
		return node;
	
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;

	console.log("11");
	var accessType = getAccessType(node);
	var accessFunction = getAccessFunction(node);
	
	accessType += ACCESS_TYPE_ENUM.CREATETEXTNODE;
	accessFunction += callerName;
	
	console.log("xxxxxxxxx createTextNode::setAttribute on <", node, ">");
	setAttribute_original.call(node, ACCESS_TYPE_LABEL, accessType);
	setAttribute_original.call(node, ACCESS_FUNCTION_LABEL, accessFunction);

	return node;
}
*/
/**************
***   CSS   ***
**************/

// Reading CSS properties
/*
var style = window.getComputedStyle;

window.getComputedStyle = function(node) {
	
	console.log("-----------------", node);
	var result = style.call(this, node);
	
	console.log("caller: ", arguments.callee.caller.name);

	if (arguments == null)
		console.log("null");
	else if (arguments == "undefined")
		console.log("undefined");
	else if (arguments == "")
		console.log("empty");
	else {
		console.log("has ", arguments.length, " args");
		console.log("args: ", arguments);
	}
	
	return result;

}
*/
/********************

********************/

function getAccessType (element) {
//	var accessType = element.getAttribute(ACCESS_TYPE_LABEL);
	console.log("getAccessType->element: " + element + " - caller: ", arguments.callee.caller.name);
	var accessType = getAttribute_original.call(element, ACCESS_TYPE_LABEL);
	if (accessType != null)
		accessType += ",";
	else
		accessType = "";

	return accessType;
}

function getAccessFunction (element) {
//	var accessFunction = element.getAttribute(ACCESS_FUNCTION_LABEL);
	var accessFunction = getAttribute_original.call(element, ACCESS_FUNCTION_LABEL);
	if (accessFunction != null)
		accessFunction += ",";
	else
		accessFunction = "";

	return accessFunction;
}

function checkAndSetElementId(element) {
	if (element == null)
		return; //
//	var id = elemenet.getAttribute("id");
	var id = getAttribute_original.call(element, "id");
	if (id == null) {
		id = generateRandomUniqueId();
		setAttribute_original.call(elemenet, "id", id);
	}

	return id;
}

function generateRandomUniqueId() {
	return PseudoGuid.GetNew();
}

var PseudoGuid = new (function() {
//    this.empty = "00000000-0000-0000-0000-000000000000";
    this.empty = "000000000000";
    this.GetNew = function() {
    	var fourChars = function() {
    		return (((1 + Math.random()) * 0x10000)|0).toString(16).substring(1).toUpperCase();
    	}
    	return (fourChars() + fourChars() + fourChars());
//    	return (fourChars() + fourChars() + "-" + fourChars() + "-" + fourChars() + "-" + fourChars() + "-" + fourChars() + fourChars() + fourChars());
    };
})();

function generateRandomUniqueXHRId() {
	return PseudoGuXHRid.GetNew();
}

var PseudoGuXHRid = new (function() {
//    this.empty = "00000000-0000-0000-0000-000000000000";
    this.empty = "XHR00000000";
    this.GetNew = function() {
    	var fourChars = function() {
    		return (((1 + Math.random()) * 0x10000)|0).toString(16).substring(1).toUpperCase();
    	}
    	return ("XHR" + fourChars() + fourChars());
//    	return (fourChars() + fourChars() + "-" + fourChars() + "-" + fourChars() + "-" + fourChars() + "-" + fourChars() + fourChars() + fourChars());
    };
})();

