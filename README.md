AndroidContextualSdkTestClientTool
==================================

SDK Test harness is conformed by Client and Server components.

The SdkTestTool project prepares all the test case information and sends this data to the mobile device.
The SdkTestClient project running on mobile device will get the test data and will execute the corresponding SDK method.
The result comming from SDK will be send back to client project to be analized.


1. Run the ContextualSdkTestClient project as an Android project.
2. Run the ContextualSdkTestTool project from desktop command line as: 

      java com.aro.qa.contextualsdk.testtool.ContextualSdkTestTool -xml ../testCases.xml -tt Smoke
      
This tool support the next command line arguments.  The arguments data is comming from tool testCases.xml file.

usage: QA_tool
 -tc <arg>    test case number to run
 -ts <arg>    Test suite to run. Set of methods for a particular SDK Class
              to test
 -tt <arg>    Test type,as BVT,Functional,etc
 -xml <arg>   XML file containing test cases


