var numOfDOMEvents = 0;

function eventHandlerReplacer(that, event, func, async, elem) {
	// console.log('>>> handler triggered: <', that, '> on <', func, '>');
	// console.log('DOM event handler: ', func.name, ' - time: ', Date.now());
	console.log('DOM event handler: - time: ', Date.now());
	var res = addEventListener1_original.call(that, event, func, async);

	numOfDOMEvents ++;

	return res;
}

var addEventListener1_original = Element.prototype.addEventListener;
Element.prototype.addEventListener = function(event, func, async) {
	// console.log(func);
	// console.log('name: ', func.name);
	// console.log('args: ', func.arguments);

	var res = eventHandlerReplacer(this, event, func, async, this);
	// var res = addEventListener1_original.call(this, event, func, async);
	// console.log("THIS: ", this);
}
