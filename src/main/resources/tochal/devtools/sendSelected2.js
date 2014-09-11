function sendSelectedElement2(el) {
	alert('send dom el');
/*
				if (typeof el == 'undefined' || el == null || el == 'undefined') {
					alert('Please select an element first');
					return;
				}
				console.log('SELECTED ELEMENT: ', el);
*/			
/*
				var xhr = new XMLHttpRequest();
				xhr.open('GET', 'http://localhost:8080/same-game/temp.json', true);
				xhr.send();
				xhr.onreadystatechange = function() {
					if (xhr.readyState == 4 && xhr.status == 200) {
						alert('-- el response --');
						var impactSetJson = xhr.responseText;
						parseAndPrintImpactSet2(impactSetJson);
//						chrome.devtools.inspectedWindow.eval('parseAndPrintImpactSet('impactSetJson')', {useContentScriptContext: true});
					}
				}
*/

	// TODO GET THE IMPACT SET AND PRINT IT HERE
}

					///////////////////////////////
					// parse-and-print-impact-set
					// response is a JSON set
					function parseAndPrintImpactSet2(response) {
						var parsedResponse = JSON.parse(response);
						
						console.clear();
//						chrome.devtools.inspectedWindow.eval('console.clear()');
						console.log('Impact Set Summary');
//						chrome.devtools.inspectedWindow.eval('console.log("Impact Set Summary")');
//						chrome.devtools.inspectedWindow.eval('console.log(' + parsedResponse + ')');
						console.table(parsedResponse);
						console.log("===========================================");
						console.log('Impact Set in Details');
						for (var i = 0; i < parsedResponse.length; i ++) {
							var currRow = parsedResponse[i];
							if (currRow.type == 'element', currRow.id)
								console.log('DOM Element: ');
							else if (currRow.type == 'function')
								console.log('Function: ', currRow.id);
							console.log(currRow.body);
							console.table([{type: currRow.type, id: currRow.id, rank: currRow.rank, distance: currRow.distance}]);
						}
					}

