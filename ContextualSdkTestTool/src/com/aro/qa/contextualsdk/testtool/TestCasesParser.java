package com.aro.qa.contextualsdk.testtool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.ProcessingInstruction;

import org.w3c.dom.Element;

public class TestCasesParser extends DefaultHandler  {

	SdkMethodsCaller qaServer = new SdkMethodsCaller();
	
	//XML input test cases
	String 	 mElementValue;//To share data between XML parser methods
	StringBuffer mTestCaseParam;//Gather test case data from the different XML parser methods.
	String mCurrentSdk;
	Set<String> mExpectedResult = new HashSet<String>();
	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	Date date = new Date();
	
	//Tool command line Arguments
	TestToolArguments toolArguments;
	String[] mToolArgs;
	String mCurrentTestSuite;
	String mCurrentTestType;
	String mCurrentTestCaseId;
	String mCurrentParameterName;
	boolean mRunTestCaseId=true;
	boolean mRunTestSuite=true;
	boolean mRunTestType=true;

	//XML report data
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder;
	Document xmlDocument;
	Element rootElement;
	Element testSuite;
	Element testCase;
	Element parameter;
	Element expected;
	Element testResult;
	Element sdkResult;
	Attr attr;
	ProcessingInstruction pi;

	//Create Objects
	StringBuffer mObservation;
	

	public TestCasesParser(String[] args) {
		super();	
		this.mToolArgs=args;
	}	

	
	void parseTestCasesXML() {

		boolean bArgs;
		mTestCaseParam = new StringBuffer();
		SAXParserFactory factory = SAXParserFactory.newInstance();

		//Parse test tool's command line arguments.
		if (mToolArgs.length > 0) {				
			toolArguments = new TestToolArguments();
			
			bArgs = toolArguments.getCommandLineArgs(mToolArgs);
			
			if (bArgs == false) {
				System.out.println("Incorrect tool arguments.");
				return;
			}
		}

		try {		
			//Create XML test report Data
			dBuilder = dbFactory.newDocumentBuilder();
			xmlDocument = dBuilder.newDocument();
			pi = xmlDocument.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"testReport.xsl\"");

			rootElement = xmlDocument.createElement("testReport");
			attr = xmlDocument.createAttribute("date");
			attr.setValue(dateFormat.format(date));
			rootElement.setAttributeNode(attr);

			//Read/Parse testCases.	 
			SAXParser parser = factory.newSAXParser();
			System.out.println(".");
			System.out.println("====================================================");
			System.out.println(".");			
			
			//parser.parse execute all the @Override methods below.
			if (toolArguments.mTcXmlFile != null){			
				parser.parse(toolArguments.mTcXmlFile, this);
			}else{
				parser.parse("testCases.xml", this);
			}

		} catch (ParserConfigurationException e) {
			System.out.println("ParserConfig error " + e);

		} catch (SAXException e) {
			System.out.println("SAXException : xml not well formed "+ e);

		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException :" + e);

		} catch (IOException e) {
			System.out.println("IO error " + e);
		}
	}

	
	@Override
	public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
		
		if (elementName.equalsIgnoreCase("test")){
			System.out.println("Test SDK " +  attributes.getValue("sdkType"));

			//XML report data.
			attr = xmlDocument.createAttribute("sdkType");			
			xmlDocument.appendChild(rootElement);
			xmlDocument.insertBefore(pi, rootElement);
		}

		if (elementName.equalsIgnoreCase("TestSuite")){
			mCurrentTestSuite = attributes.getValue("name");
			
			//Filter test cases run By Test Suite from test tool command line arguments.	
			if (toolArguments.mTestSuiteFlag == true){
				if (toolArguments.mTestSuiteArgs.contains(mCurrentTestSuite.toLowerCase()) == false){	
					System.out.println("Test Suite " +  attributes.getValue("name"));
					mRunTestSuite=false;mRunTestType=false;	mRunTestCaseId=false;
					return;
				}else{
					mRunTestSuite=true;mRunTestType=true;mRunTestCaseId=true;
				}		
			}
			
			//XML report data.
			testSuite = xmlDocument.createElement("testSuite");
			rootElement.appendChild(testSuite);
			attr = xmlDocument.createAttribute("name");
			attr.setValue( attributes.getValue("name"));
			testSuite.setAttributeNode(attr);	
		}		

		if (elementName.equalsIgnoreCase("testCase")) {
				
			mCurrentTestType=attributes.getValue("type");
			mCurrentTestCaseId=attributes.getValue("id");
			mCurrentSdk=attributes.getValue("sdk");
		
			if (mRunTestSuite == false){
				return;
			}
			
			//Filter test cases run By Test Type from test tool command line arguments.
			if (toolArguments.mTestTypeFlag == true){
				if (toolArguments.mTestTypeArgs.contains(mCurrentTestType.toLowerCase()) == false){
					mRunTestCaseId=false;
					return;
				}else{
					mRunTestCaseId=true;
				}
			}
			
			//Filter test cases run By test case number from test tool command line arguments.
			if (toolArguments.mTestCaseFlag == true){				
				if (toolArguments.mTestCaseIdArgs.contains(mCurrentTestCaseId) == false){
					mRunTestCaseId=false;
					return;
				}else{
					mRunTestCaseId=true;
				}				
			}

			System.out.println("Test Case # " +  attributes.getValue("id") +  ". SDK="+ mCurrentSdk + ". Description:"  + attributes.getValue("name"));			
			mTestCaseParam.append(mCurrentSdk+":::");

			//XML report data.
			testCase = xmlDocument.createElement("testCase");
			testSuite.appendChild(testCase);
			attr = xmlDocument.createAttribute("id");
			attr.setValue( attributes.getValue("id"));
			testCase.setAttributeNode(attr);
			attr = xmlDocument.createAttribute("sdk");
			attr.setValue(mCurrentSdk);
			testCase.setAttributeNode(attr);			
			attr = xmlDocument.createAttribute("name");
			attr.setValue( attributes.getValue("name"));
			testCase.setAttributeNode(attr);		
		}

		if (elementName.equalsIgnoreCase("parameter") && mRunTestCaseId) {
			mCurrentParameterName = attributes.getValue("name");
			System.out.println("parameter name=" +  mCurrentParameterName);
			
			//XML report data.
			parameter = xmlDocument.createElement("parameter");
			testCase.appendChild(parameter);
			attr = xmlDocument.createAttribute("name");
			attr.setValue( attributes.getValue("name"));
			parameter.setAttributeNode(attr);			
		}
	}


	@Override
	public void endElement(String s, String s1, String element) throws SAXException {
		String dataFromDevice=null;
		boolean bTestResult;	

		if (element.equals("testCase")  && mRunTestCaseId) {
			
			//SEND TEST CASE TO DEVICE.
			dataFromDevice = qaServer.connectToMobile(mTestCaseParam.toString());

			if (dataFromDevice == null){
				System.out.println("NO DATA RECEIVED FROM DEVICE");
				return;
			}

			//Validate SDK Results.
			bTestResult = checkResultFromDevice(dataFromDevice);
			//dataFromDevice=null;
			//XML report data.
			testResult = xmlDocument.createElement("testResult");
			attr = xmlDocument.createAttribute("result");	
			if (bTestResult == true){
				
				attr.setValue("Pass");
			}else{				
				attr.setValue("Fail");	
				System.out.println("Test case FAILED");
			}
			testResult.setAttributeNode(attr);
			testResult.appendChild(xmlDocument.createTextNode(dataFromDevice));
			mTestCaseParam.delete(0, mTestCaseParam.length());
			testCase.appendChild(testResult);	

			//System.out.println("TEST CASE ENDS");
		}

		if (element.equalsIgnoreCase("parameter")  && mRunTestCaseId ) {
			
		
	/*			//Add the parameter name if it is a Place test case.PlaceBuilder   SdkPlace
				if ( mCurrentSdk.equalsIgnoreCase("Place:placeRequest") || 
						mCurrentSdk.equalsIgnoreCase("Place:Frequent") || 
						mCurrentSdk.equalsIgnoreCase("Place:Favorite") || 						
						mCurrentSdk.equalsIgnoreCase("Place:SavePlace")){
					mTestCaseParam.append(mCurrentParameterName+"#");
				}
	*/
				
			//Add the parameter name if it is a Place test case.PlaceBuilder   SdkPlace
			if ( mCurrentSdk.contains("Place")){
				mTestCaseParam.append(mCurrentParameterName+"#");
			}
			
			
			
				mTestCaseParam.append(mElementValue+":::");
			
			//XML report data.
			parameter.appendChild(xmlDocument.createTextNode(mElementValue));
		}

		if (element.equalsIgnoreCase("expected")  && mRunTestCaseId ) {
			System.out.println("Expected =  " +  mElementValue);
			mExpectedResult.add(mElementValue);

			//XML report data.
			expected = xmlDocument.createElement("expected");
			testCase.appendChild(expected);
			expected.appendChild(xmlDocument.createTextNode(mElementValue));
		}
	}

	/***
	 * Read XML Element value.
	 */
	@Override
	public void characters(char[] ac, int i, int j) throws SAXException {

		mElementValue = new String(ac, i, j);

	}

	@Override
	public void endDocument(){

		//System.out.println("End of XML file");
		toolArguments.mTestCaseFlag =false;

		// write the content into xmlTestReport file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(xmlDocument);
			StreamResult result = new StreamResult(new File("../TestReport.xml"));	

			// Output to console for testing
			//StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);
		}catch (TransformerConfigurationException e) {
			
			System.out.println("Error creating XML test report " +e);
		} catch (TransformerException e) {

			System.out.println("TransformerException Error creating XML test report " + e);
		}		
	}


	boolean checkResultFromDevice(String resultFromDevice){
		boolean tcResult=true;
		String[] results = resultFromDevice.split(":::");
		
		for (String expRes: mExpectedResult){
			
			if (Arrays.asList(results).contains(expRes) == true){
				System.out.println("PASS. Found " + expRes);
			}else{
				
				if (resultFromDevice.toLowerCase().contains(expRes.toLowerCase())) {
					System.out.println("PASS. Found:" + expRes);
				}else{
				System.out.println("FAIL. Expected:" + expRes);
				System.out.println("      Actual:" + resultFromDevice);
				tcResult= false;
				}
			}				
		}		
		mExpectedResult.clear();

		return tcResult;
	}
	

}

