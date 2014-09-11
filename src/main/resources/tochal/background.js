chrome.browserAction.onClicked.addListener(function(tab) {
  // No tabs or host permissions needed!
  /*
  console.log('Turning ' + tab.url + ' red!');
  chrome.tabs.executeScript({
    code: 'document.body.style.backgroundColor="red"'
  });
  */

//  alert('hi');

//  console.log(window.dynamicFunctionNames);
  
  chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
  	chrome.tabs.sendMessage(tabs[0].id, {greeting: "hello"}, function(response) {
    	console.log(response.farewell);
  	});
  });

});
