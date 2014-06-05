package com.aro.qa.contextualsdk.testtool;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class TestToolArguments {
		 String mTcXmlFile=null;
		 Set<String> mTestSuiteArgs = new HashSet<String>();
		 Set<String> mTestTypeArgs = new HashSet<String>();
		 Set<String> mTestCaseIdArgs = new HashSet<String>();
	
		//Command line parameters
		boolean mTestCaseFlag=false;
		boolean mTestSuiteFlag=false;
		boolean mTestTypeFlag=false;
		CommandLine cmd;
		Options options = new Options();
		CommandLineParser parser = new BasicParser();
		
		
		public TestToolArguments() {
			super();
		}	
		
		
		boolean getCommandLineArgs(String[] toolArgs){

			String[] commandLineArgumentValues;
			
			options.addOption("xml", true,"XML file containing test cases");
			options.addOption("ts",true,"Test suite to run. Set of methods for a particular SDK Class to test");
			options.addOption("tt",true,"Test type,as BVT,Functional,etc");
			options.addOption("tc",true,"test case number to run");

			//Parse input parameters.
			try{
				cmd = parser.parse(options, toolArgs);
			}catch (ParseException pe){ 			
				usage(options); 
				return false; 
			}	
			
			//Get test cases xml file name
			if(cmd.hasOption("xml")){
				System.out.println("Test file");
				mTcXmlFile = cmd.getOptionValue("xml");
			}
			
			//Get values for Test Suite parameter
			if(cmd.hasOption("ts")){
				System.out.println("Running Test suite");
				mTestSuiteFlag=true;
				commandLineArgumentValues = cmd.getOptionValues("ts");
				
				for (String str: commandLineArgumentValues[0].split(",")){
					mTestSuiteArgs.add(str.toLowerCase());		
				}		
			}
				
			//Get values for Test type parameter
			if(cmd.hasOption("tt")){
				mTestTypeFlag=true;
				commandLineArgumentValues = cmd.getOptionValues("tt");
				
				for (String str: commandLineArgumentValues[0].split(",")){
					mTestTypeArgs.add(str.toLowerCase());		
				}	
			}
					
			//Get values for test cases id parameter
			if(cmd.hasOption("tc")){
				mTestCaseFlag=true;		
				commandLineArgumentValues = cmd.getOptionValues("tc");

				for (String str: commandLineArgumentValues[0].split(",")){
					mTestCaseIdArgs.add(str);		
				}		
			}	 

			return true;
		}

		private static void usage(Options options){

			// Use the inbuilt formatter class
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "QA_tool", options );
		}		

}
