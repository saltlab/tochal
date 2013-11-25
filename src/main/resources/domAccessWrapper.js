var ACCESS_TYPE_LABEL = "access_type";
var ACCESS_FUNCTION_LABEL = "access_function";
var ACCESS_TYPE_ENUM = {
	GETELEMENTBYID		:	"getElementById",
	GETELEMENTBYNAME	:	"getElementByName"
};

	
/***************
** ATTRIBUTES **
***************/

////////// ACCESS ATTRIBUTES //////////

var getAttribute_original = Element.prototype.getAttribute;

Element.prototype.getAttribute = function (attrName) {
	var attr = getAttribute_original.call(this, attrName);
	console.log("getAttribute, " + attrName + " -> " + attr);
	var caller = arguments.callee.caller;
//	console.log("caller: " + caller);
	return attr;
}

var setAttribute_original = Element.prototype.setAttribute;

Element.prototype.setAttribute = function (attr, value) {
	var element = setAttribute_original.call(this, attr, value);
	console.log("setAttribute, " + attr + " -> " + element);
//	console.log(element);
	var caller = arguments.callee.caller;
//	console.log("caller: " + caller);
	return element;
}

var removeAttribute_original = Element.prototype.removeAttribute;

Element.prototype.removeAttribute = function (attr) {
	var element = removeAttribute_original.call(this, attr);
	console.log("removeAttribute");
//	console.log(element);
	var caller = arguments.callee.caller;
//	console.log("caller: " + caller);
	return element;
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
	
	console.log("access: " + accessType + ", function: ", accessFunction);
	console.log("element: " + id + " -> " + element);

	return element;
}

var getElementsByName_original = Document.prototype.getElementsByName;

Document.prototype.getElementsByName = function (name) {
	var element = getElementsByName_original.call(this, name);
	console.log("getElementsByName, " + name + " -> " + element);
	var caller = arguments.callee.caller;
//	console.log("caller: " + caller);
	return element;
}
/*
var getElementsByTagName_original = Document.prototype.getElementsByTagName;

Document.prototype.getElementsByTagName = function (tagName) {
	var element = getElementsByTagName_original.call(this, tagName);
	console.log("getElementsByTagName, " + tagName + " -> " + element);
	var caller = arguments.callee.caller;
//	console.log("caller: " + caller);
	return element;
}
*/
var getElementsByClassName_original = Document.prototype.getElementsByClassName;

Document.prototype.getElementsByClassName = function (className) {
	var element = getElementsByClassName_original.call(this, className);
	console.log("getElementsByClassName, " + className + " -> " + element);
	var caller = arguments.callee.caller;
//	console.log("caller: " + caller);
	return element;
}

////////// CREATE ELEMENTS //////////

var createElement_original = Document.prototype.createElement;

Document.prototype.createElement = function (tagName) {
	var element = createElement_original.call(this, tagName);
	console.log("createElement, " + tagName + " -> " + element);
	var caller = arguments.callee.caller;
//	console.log("caller: " + caller);
	return element;
}

/**************
**   NODES   **
**************/
////////// NODES //////////

////////// ADD / REMOVE CHILD //////////

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

/********************

********************/

function getAccessType (element) {
	var accessType = getAttribute_original(element, ACCESS_TYPE_LABEL);
	if (accessType != null)
		accessType += ",";
	else
		accessType = "";

	return accessType;
}

function getAccessFunction (element) {
	var accessFunction = getAttribute_original(element, ACCESS_FUNCTION_LABEL);
	if (accessFunction != null)
		accessFunction += ",";
	else
		accessFunction = "";

	return accessFunction;
}

function checkAndSetElementId(element) {
	if (element == null)
		return; //
	var id = getAttribute_original(elemenet, "id");
	if (id == null) {
		id = generateRandomUniqueId();
		setAttribute_original(elemenet, "id", id);
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