package com.aro.qa.contextualsdk.testtool;


import org.xml.sax.helpers.DefaultHandler;

public class ContextualSdkTestTool extends DefaultHandler  {
	
	
	public static void main(String[] args){
			
		TestCasesParser qaFileHelper = new TestCasesParser(args);
		
		qaFileHelper.parseTestCasesXML();		
		
	}

}
