			function elementAnalyzeButtonPressed() {
				var el = document.getElementById('elementId').value;


				if (typeof el == 'undefined' || el == null || el == 'undefined') {
					alert('Please select an element first');
					return;
				}
				console.log('SELECTED ELEMENT: ', el);

				var url = 'http://localhost:8888/phormer331-new/sets/task1.json';

				if (el.indexOf('ss_smaller_link') > -1)
					url = 'http://localhost:8888/phormer331-new/sets/task1.json';
				else if (el.indexOf('ss_playpause') > -1)
					url = 'http://localhost:8888/phormer331-new/sets/task2.json';
				else if (el.indexOf('cmntTable') > -1)
					url = 'http://localhost:8888/phormer331-new/sets/task3.json';
				else if (el.indexOf('emailInput') > -1 || el.indexOf('ComEmailTR') > -1)
					url = 'http://localhost:8888/phormer331-new/task4.json';
				else
					url = 'http://localhost:8888/phormer331-new/sets/task5.json';
			
				var xhr = new XMLHttpRequest();
				xhr.open('GET', url, true);
				xhr.send();
				xhr.onreadystatechange = function() {
					if (xhr.readyState == 4 && xhr.status == 200) {
//						alert('-- el response --');
						var impactSetJson = xhr.responseText;

						var parsedResponse = JSON.parse(impactSetJson);
						
						console.clear();
						console.log('SELECTED ELEMENT: ', el);
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
				}


				// alert('elements --- analyze');
//				sendObjectToInspectedPage({action: "code", content: "document.body.innerHTML='<button>Send message to DevTools</button>'"});
/*				sendObjectToInspectedPage({action: "code", content: ""});
			    sendObjectToInspectedPage({action: "script", content: "messageback-script.js"});

			    chrome.devtools.inspectedWindow.eval("send($0)", { useContentScriptContext: true });
*/
			}
			document.getElementById('elementAnalyzeButton').addEventListener('click', elementAnalyzeButtonPressed, false);
			