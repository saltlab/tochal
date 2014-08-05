function printEmpiricalResults() {
	console.log('******** dynamic begin ********');

	console.log('*** dynamicFunctionNames: ', dynamicFunctionNames);
	console.log('*** dynamicFunctionArgs: ', dynamicFunctionArgs);
	console.log('num of dynamic funcs: ' + dynamicFunctionArgs.length);
	var numOfDynamicFuncsWNoArgs = 0;
	var numOfDynamicFuncsWMismatchArgs = 0;
	for (var i = 0; i < dynamicFunctionArgs.length; i ++) {
		var funcWNoArgs = true;
		var funcInstanceWNoArgs = false;
		var funcWMismatchArgs = false;
		var prevArgs;
		if (dynamicFunctionArgs[i].length > 0)
			prevArgs = dynamicFunctionArgs[i][0];
		for (var j = 0; j < dynamicFunctionArgs[i].length; j ++) {
			var currArgs = dynamicFunctionArgs[i][j];
			if (prevArgs != currArgs)
				funcWMismatchArgs = true;
			if (dynamicFunctionArgs[i][j] != 0)
				funcWNoArgs = false;
			else
				funcInstanceWNoArgs = true;
		}
		if (funcInstanceWNoArgs)
			numOfDynamicFuncsWNoArgs ++;
		if (funcWMismatchArgs)
			numOfDynamicFuncsWMismatchArgs ++;

	}
	console.log('numOfDynamicFuncsWNoArgs: ', numOfDynamicFuncsWNoArgs);
//	console.log('*** staticFunctionNames: ', staticFunctionNames);
//	console.log('*** staticFunctionArgs: ', staticFunctionArgs);

	console.log('******** dynamic end ********');


	console.log('******** xhr begin ********');
	printXHRResults();
	console.log('******** xhr end ********');


	console.log('******** dom events ********');
	console.log('numOfDOMEvents: ', numOfDOMEvents);

}

var temp = document.createElement('div');
temp.innerHTML = 'Get DYNAMIC Results';
temp.setAttribute('id', 'tempDIVProteus');
temp.addEventListener('click', printEmpiricalResults, false);
document.body.insertBefore(temp, document.body.children[0]);


function printXHRResults() {
	console.log('numOfXHROpen: ', numOfXHROpen);
	console.log('numOfXHRSend: ', numOfXHRSend);
	console.log('numOfXHRResponse: ', numOfXHRResponse);

}