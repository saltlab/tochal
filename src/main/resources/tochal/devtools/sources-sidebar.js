
			function analyzeButtonPresset() {
				if (typeof selectedText == 'undefined' || selectedText == null || selectedText == 'undefined') {
					alert('Please select a function first');
					return;
				}
				console.log('SELECTED FUNCTION: ', selectedText);
/*				alert('sources analyze');
			    sendObjectToInspectedPage({action: "code", content: "console.log('Inline script executed')"});
			    alert(selectedText);
*/				
/*
				var taskUrl = 'http://localhost:8888/phormer331-new/sets/task1.json';
				
				if (selectedText.indexOf('ss_smaller_link') > -1 || selectedText.indexOf('ss_toggleSmaller') > -1)
					taskUrl = 'http://localhost:8888/phormer331-new/sets/task1.json';
				else if (selectedText.indexOf('ss_playpause') > -1)
					taskUrl = 'http://localhost:8888/phormer331-new/sets/task2.json';
				else if (selectedText.indexOf('cmntTable') > -1 || selectedText.indexOf('handleTable') > -1 || selectedText.indexOf('checkWV') > -1)
					taskUrl = 'http://localhost:8888/phormer331-new/sets/task3.json';
				else
					taskUrl = 'http://localhost:8888/phormer331-new/sets/task5.json';


				var xhr = new XMLHttpRequest();
				xhr.open('GET', taskUrl, true);
				xhr.send();
				xhr.onreadystatechange = function() {
					if (xhr.readyState == 4 && xhr.status == 200) {
//						alert('-- response --');
						var impactSetJson = xhr.responseText;
						parseAndPrintImpactSet(impactSetJson);
//						chrome.devtools.inspectedWindow.eval('parseAndPrintImpactSet('impactSetJson')', {useContentScriptContext: true});
					}
				}
*/
				var impactSetJson = [
					{"type": "element","id": "ss_smaller_link","body": "<a id='ss_smaller_link' class='q'>Smaller Size</a>","file": "index.php","rank": "5","distance": "0"},
					{"type": "function","id": "ss_toggleSmaller","body": "function ss_toggleSmaller() {\nss_smaller = !ss_smaller;\ndg('ss_smaller_link').innerHTML = (ss_smaller)?'Larger Size':'Smaller Size';\ndg('ss_photo').src = ss_smaller?src_smaller(dg('ss_photo').src):ss_src[ss_cur];\n}","file": "phorm.js","rank": "4","distance": "1"},
					{"type": "element","id": "ss_photo","body": "<img id='ss_photo' onload='javscript:ss_loaddone();' />","file": "index.php","rank": "3","distance": "2"},
					{"type": "function","id": "src_smaller","body": "function src_smaller(x) {\nif (x.charAt(x.length-1) == '')')\nx = x.substr(3, x.length-4);\nvar a = x.substr(0, x.length-5)+'4.jpg';\nreturn a;\n}","file": "phorm.js","rank": "2","distance": "2"},
					{"type": "function","id": "mp_granny","body": "function mp_granny() {\nvar myDiv = document.getElementById('myDiv');\nmyDiv.innerHTML = 'count';\n}","file": "phorm.js","rank": "2","distance": "1"},
					{"type": "function","id": "mainAppArea","body": "function mainAppArea() {\ndocument.getElementById('myDiv').innerHTML = 'main';\n}","file": "phorm.js","rank": "2","distance": "1"},
					{"type": "function","id": "leavingReply","body": "function leavingReply() {\ndocument.getElementById('myDiv').innerHTML = 'reply';\n}","file": "phorm.js","rank": "2","distance": "1"},
					{"type": "function","id": "ss_update","body": "function ss_update() {\nss_cur = Math.max(ss_cur, 0);\nif (ss_cur >= ss_date.length) {\nhideElem('ss_link2');\nshowElem('ss_theend');\nss_cur = ss_date.length;\nvar a = dg('ss_n');\na.innerHTML = 'Final';\nif (ss_play)\nss_playpause();\n}\nelse {\nhideElem('ss_theend');\ninlineElem('ss_link2');\nss_loaded = (dg('ss_photo').src == ss_src[ss_cur]);\nlink = '.?p='+ss_pid[ss_cur];\nsrc = ss_src [ss_cur];\nsrc = ss_smaller?src_smaller(src):src;\ndg('ss_photo').src = src;\ndg('ss_date').innerHTML = ss_date[ss_cur];\ndg('ss_title').innerHTML = ss_ttl[ss_cur];\ndg('ss_desc').innerHTML = ss_desc[ss_cur];\ndg('ss_n').innerHTML = 1+ss_cur;\ndg('ss_link1').setAttribute('href', link);\ndg('ss_link2').setAttribute('href', link);\nif (ss_cur < ss_date.length) {\npreimg = new Image;\npreimg.src = ss_src [ss_cur+1];\n}\n}\n}","file": "phorm.js","rank": "1","distance": "3"},
					{"type": "element","id": "ss_link2","body": "<a id='ss_link2' style='display:inline;'>","file": "index.php","rank": "0.5","distance": "4"},
					{"type": "element","id": "ss_theend","body": "function showElem(x) {\ntry {\ndg(x).style.display = 'block';\n} catch(e) {}\n}","file": "phorm.js","rank": "0.5","distance": "4"}
				];

parseAndPrintImpactSet(impactSetJson);

			}
			document.getElementById('analyzeButton').addEventListener('click', analyzeButtonPresset, false);
			

var url = 'undefined';
	var startLine = -1;
	var endLine = -1;
	var startColumn = -1;
	var endColumn = -1;
	
	var selectedText = 'undefined';
	
chrome.devtools.panels.sources.onSelectionChanged.addListener(function(selectionInfo){
	url = selectionInfo.url;
	startLine = selectionInfo.startLine;
	endLine = selectionInfo.endLine;
	startColumn = selectionInfo.startColumn;
	endColumn = selectionInfo.endColumn;
	
	function allResources(resources) {
		
		for (var i = 0; i < resources.length; i ++) {
			if (resources[i].url == url) {
				
				resources[i].getContent(function(content, encoding){
					
					// calculate start and end index based on start and end line and column number. get a substring based on indexes
					var startIndex = 0;
					var endIndex = 0;
					
					var contentLines = content.split('\n');
					
//					chrome.devtools.inspectedWindow.eval('console.log("--' + contentLines.length + '");');
					
					var lineCounter = 0;
					while (lineCounter < startLine) {
						startIndex += (contentLines[lineCounter].length + 1);
						endIndex += (contentLines[lineCounter].length + 1);
						lineCounter ++;
					}
					startIndex += startColumn;
					
					while (lineCounter < endLine) {
						endIndex += (contentLines[lineCounter].length + 1);
						lineCounter ++;
					}
					endIndex += endColumn;
					
					selectedText = content.substring(startIndex, endIndex);
					
				});
			}
		}
	}
	
	chrome.devtools.inspectedWindow.getResources(allResources);

});

					///////////////////////////////
					// parse-and-print-impact-set
					// response is a JSON set
					function parseAndPrintImpactSet(response) {
						var parsedResponse = response;//JSON.parse(response);
						
						console.clear();
										console.log('SELECTED FUNCTION: ', selectedText);

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
