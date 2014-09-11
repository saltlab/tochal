Proteus
=======

Tool for Hybrid Change Impact Analysis in JavaScript Applications

Performing change impact analysis on JavaScript applications is challenging due to features such as dynamic event-driven function calls, DOM-based operations, and asynchronous client/server communication. We propose a hybrid change impact analysis technique , named Tochal, that uses a combination of static and dynamic analysis to form a model of the system entities and their relations. The proposed approach incorporates a novel ranking algorithm for indicating the importance of each entity in the impact set.

### Installation

#### Server side.
Requirements: Eclipse, Apache Maven

After cloning the repository, you can use the Maven plugin of Eclipse to import the project into Eclipse.

#### Client side.
Requirement: Google Chrome

Navigate to chrome://extensions on Chrome. Activate the developer mode, select the "load unpacked extension" option, and choose the "Tochal" folder from tochal/src/main/resources.

### Execution
First, you need to start Jetty from Tochal home directory. Then you can navigate to the URL of the application you can investigate. Opening the Chrome DevTools gives you access to the interface of Tochal. Two additional sidebars are added to the Elements and Sources panels of DevTools. In the Sources panel, you can observe the impact set of a function by selecting the function name in the text and clicking the "Analyze" button in Tochal's sidebar. In the Elements panel, you can enter the ID of the element in Tochal's sidebar and press "Analyze".
