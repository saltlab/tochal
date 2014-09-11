// Can use
// chrome.devtools.*
// chrome.extension.*

// Create a tab in the devtools area
chrome.devtools.panels.create("DemoPanel", "toast.png", "panel.html", function(panel) {});

function createSourcesSidebar(sidebar) {
	sidebar.setPage('sources-sidebar.html');
}

function createElementsSidebar(sidebar) {
	sidebar.setPage('elements-sidebar.html');
}

chrome.devtools.panels.sources.createSidebarPane('Proteus', createSourcesSidebar);
chrome.devtools.panels.elements.createSidebarPane('Proteus', createElementsSidebar);