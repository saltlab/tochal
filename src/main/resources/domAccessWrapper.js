var ACCESS_TYPE_LABEL = "access_type";
var ACCESS_FUNCTION_LABEL = "access_function";
var ACCESS_TYPE_ENUM = {
	GETELEMENTBYID		:	"GetElementById",
	GETELEMENTBYNAME	:	"GetElementByName",
	GETELEMENTBYTAGNAME	:	"GetElementByTagName",
	GETELEMENTBYCLASSNAME:	"GetElementByClassName",
	GETATTRIBUTE		:	"GetAttribute",
	SETATTRIBUTE		:	"GetAttribute",
	REMOVEATTRIBUTE		:	"RemoveAttribute",
	GETELEMENTBYNAME	:	"GetElementByName"
};

	
/***************
** ATTRIBUTES **
***************/

////////// ACCESS ATTRIBUTES //////////

var getAttribute_original = Element.prototype.getAttribute;

Element.prototype.getAttribute = function (attrName) {
	// this == element
	var attr = getAttribute_original.call(this, attrName);

	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "") // TODO
			callerName = arguments.callee.caller.name;
		
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

	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
		
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

	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
	
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
	
	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
		
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
	var element = getElementsByName_original.call(this, name);

	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
		
	var accessType = getAccessType(element);
	var accessFunction = getAccessFunction(element);
	
	accessType += ACCESS_TYPE_ENUM.GETELEMENTBYNAME;
	accessFunction += callerName;

	setAttribute_original.call(element, ACCESS_TYPE_LABEL, accessType);
	setAttribute_original.call(element, ACCESS_FUNCTION_LABEL, accessFunction);

	return element;
}

var getElementsByTagName_original = Document.prototype.getElementsByTagName;

Document.prototype.getElementsByTagName = function (tagName) {
	var element = getElementsByTagName_original.call(this, tagName);

	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
		
	var accessType = getAccessType(element);
	var accessFunction = getAccessFunction(element);
	
	accessType += ACCESS_TYPE_ENUM.GETELEMENTBYTAGNAME;
	accessFunction += callerName;

	setAttribute_original.call(element, ACCESS_TYPE_LABEL, accessType);
	setAttribute_original.call(element, ACCESS_FUNCTION_LABEL, accessFunction);

	return element;
}

var getElementsByClassName_original = Document.prototype.getElementsByClassName;

Document.prototype.getElementsByClassName = function (className) {
	var element = getElementsByClassName_original.call(this, className);

	var caller = arguments.callee.caller;
	var callerName = "null";
	if (caller != null)
		if (caller.name != "")
			callerName = arguments.callee.caller.name;
		
	var accessType = getAccessType(element);
	var accessFunction = getAccessFunction(element);
	
	accessType += ACCESS_TYPE_ENUM.GETELEMENTBYCLASSNAME;
	accessFunction += callerName;

	setAttribute_original.call(element, ACCESS_TYPE_LABEL, accessType);
	setAttribute_original.call(element, ACCESS_FUNCTION_LABEL, accessFunction);

	return element;
}
/*
////////// CREATE ELEMENTS //////////

var createElement_original = Document.prototype.createElement;

Document.prototype.createElement = function (tagName) {
	var element = createElement_original.call(this, tagName);
	console.log("createElement, " + tagName + " -> " + element);
	var caller = arguments.callee.caller;
//	console.log("caller: " + caller);
	return element;
}
*/
/**************
**   NODES   **
**************/
////////// NODES //////////

////////// ADD / REMOVE CHILD //////////
/*
var appendChild_original = Document.prototype.appendChild;

Element.prototype.appendChild = function (child) {
	var element = appendChild_original.call(this, child);
	console.log("appendChild, " + child + " -> " + element);
	var caller = arguments.callee.caller;
	return element;
}

var removeChild_original = Document.prototype.removeChild;

Element.prototype.removeChild = function (child) {
	var element = removeChild_original.call(this, child);
	console.log("removeChild, " + child + " -> " + element);
	var caller = arguments.callee.caller;
	return element;
}
*/
/********************

********************/

function getAccessType (element) {
//	var accessType = element.getAttribute(ACCESS_TYPE_LABEL);
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