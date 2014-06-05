package com.aro.qa.contextualsdk.testclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;

import com.aro.android.contextsdk.AroContextualSdk;
import com.aro.android.contextsdk.PersonRequest;
import com.aro.android.contextsdk.apiobjects.Person;
import com.aro.android.contextsdk.apiobjects.Place;
import com.aro.android.contextsdk.http.WebException;
import com.aro.android.contextsdk.http.WebResultHandler;

public class QaPersonSDK {

	
	Context mContext;
	AroContextualSdk mContextSdk;

	ToolHelper qaHelper;
	ToolLogger toolLogger;

	PersonRequest PersonRequest;

	final static String TAG = "QA_test";
	String mResult="Person Result Not Set on Device";

	public QaPersonSDK(Context context, AroContextualSdk aroContextualSdk) {
	
		super();
		this.mContext = context;
		this.mContextSdk = aroContextualSdk;
		qaHelper = new ToolHelper(mContext);
		toolLogger = new ToolLogger(mContext);
	}		
	
	
	String testPersonSDK(String sdkMethod,String[] testCaseData){
		
		if(sdkMethod.equalsIgnoreCase("getMe")){

			return getMe_test(testCaseData);	
		}		
			
		if(sdkMethod.equalsIgnoreCase("getPerson")){

			return getPerson_test(testCaseData);	
		}
		
		if(sdkMethod.equalsIgnoreCase("getPeople")){

			return getPeople_test(testCaseData);	
		}
		
		if(sdkMethod.equalsIgnoreCase("createPerson")){

			return createPerson_test(testCaseData);	
		}		

		toolLogger.qaLogs("Person. Method " + sdkMethod + " NOT FOUND");	
		return "Method " + sdkMethod + " NOT FOUND";
	}
	

	/**
	 * This method is used by QaTraidSDK class.
	 * 
	 * 
	 * @return
	 */
	String getMe(){
		
		String[] testCase=null;

		getMe_test(testCase);
		
		//qaHelper.waitForWebListenerResponse(15);
		
		return mResult;
	}
	

	
	/**
	 * Gets the current user id.
	 * 
	 * 
	 * @return
	 */
	String getMeId(){
		
		String[] testCase=null;

		getMe_test(testCase);
		
		qaHelper.waitForWebListenerResponse(15);
		
		try {
			JSONObject personJson = new JSONObject(mResult);
			mResult = (String)personJson.get("id");
			toolLogger.qaLogs("getMe id = " + mResult);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return mResult;
	}
	
/***
 * Executes mContextSdk.getMe 
 * 	
 * @param testCase
 * @return
 */
	
	String getMe_test(String[] testCase){
		
		try{
			mContextSdk.getMe(webResultHandler);
			qaHelper.waitForWebListenerResponse(10);
			
		}catch(Exception e){
			toolLogger.qaLogs("Person.getMe Exception " + e);
		}
		
		return mResult;
	}
	
	
	String getPerson_test(String[] testCase){
		
		String id;
		
		try{
		
			if (testCase.length > 1) {
				id = testCase[1];
			} else {
			
				id = getMeId();
			}
			//	PersonRequest.execute(webResultHandlerPerson);
			toolLogger.qaLogs("getPerson id = "+ id);
			mContextSdk.getPerson(id, webResultHandler);
			qaHelper.waitForWebListenerResponse(10);
			
		}catch(Exception e){
			toolLogger.qaLogs("Person.getMe Exception " + e);
		}
		
		
		return mResult;
	}
	
	
	
	String getPeople_test(String[] testCase){
		
		List<String> ids = new ArrayList<String>();
		
		try{
		
			if (testCase.length > 1) {
				ids.add(testCase[1]);
			} else {
				
				ids.add(getMeId());
			}
			toolLogger.qaLogs("getPerson getPeople = "+ ids.get(0));	
			mContextSdk.getPeople(ids,webResultHandlerMap);
			qaHelper.waitForWebListenerResponse(10);
			
		}catch(Exception e){
			toolLogger.qaLogs("Person.getMe Exception " + e);
		}
	
		return mResult;
	}	
	
	
	String createPerson_test(String[] testCase){
		
		try {
			JSONObject personJson = new JSONObject("{\"person\":{ }}");
			personJson.put("id", testCase[1]);
			personJson.put("firstName", "QaFirstName");
			personJson.put("lastName", "QaLastName");
			personJson.put("name", "QA_Name");
			
			Person person = new Person(personJson);
			
			toolLogger.qaLogs("Person = "+ person.getJsonObject().toString());
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			toolLogger.qaLogs("Create Person JSONException= "+ e.toString());
		} catch (Exception ex){
			ex.printStackTrace();
			toolLogger.qaLogs("Create Person Exception= "+ ex.toString());
		}
		
		
		
		return mResult;
	}
	
	
	
	WebResultHandler<Map<String,Person>> webResultHandlerMap=new WebResultHandler<Map<String,Person>>(Looper.getMainLooper()) {

		@Override
		protected void onWebFailure(WebException arg0) {
			toolLogger.qaLogs("webResultHandlerPerson onWebFailure " + arg0);
			
			mResult =arg0.toString();
			ToolHelper.mWait=false;	
			
		}

		@Override
		protected void onWebSuccess(Map<String, Person> arg0) {
			toolLogger.qaLogs("WebResultHandler<Map<String,Person>> onWebSuccess");

			JSONObject jsonPlace;
			StringBuilder placeString = new StringBuilder( "{ \"people\": [ " );
			
			for (Map.Entry<String, Person> categoMap : arg0.entrySet()){
				
				if (categoMap.getValue()==null) continue;
				//Get all places Json to send it back to desktop
				jsonPlace = categoMap.getValue().getJsonObject();
				placeString.append(jsonPlace.toString());
				placeString.append(",");
								
				toolLogger.qaLogs("Key:" + categoMap.getKey());
				showPersonFields(categoMap.getValue());
				
			}
			
			placeString.deleteCharAt(placeString.length()-1);
			placeString.append(" ] }");
			mResult = placeString.toString();
			ToolHelper.mWait=false;	
			
		}
	};
	
	
	
	WebResultHandler<List<Person>> webResultHandlerPerson = new WebResultHandler<List<Person>>(Looper.getMainLooper()) {

		@Override
		protected void onWebSuccess(List<Person> paramResult) {
			toolLogger.qaLogs("webResultHandlerPerson onWebSuccess");
			
			mResult = "TO DO get Person info";
			ToolHelper.mWait=false;	
		}
		
		@Override
		protected void onWebFailure(WebException e) {
			toolLogger.qaLogs("webResultHandlerPerson onWebFailure " + e);
			
			mResult =e.toString();
			ToolHelper.mWait=false;	

			
		}
	};
	
	WebResultHandler<Person> webResultHandler = new WebResultHandler<Person>(Looper.getMainLooper()) {

		@Override
		protected void onWebSuccess(Person person) {
			toolLogger.qaLogs("onWebSuccess <Person>");
			showPersonFields(person);		
			
			mResult= person.getJsonObject().toString();
			ToolHelper.mWait=false;	
			
		}
		
		@Override
		protected void onWebFailure(WebException paramWebException) {
			toolLogger.qaLogs("WebResultHandler onWebFailure");
			
			mResult =paramWebException.toString();
			ToolHelper.mWait=false;	
		}

	};	
	

	
	void showPersonFields(Person person){
		
		toolLogger.qaLogs("\r\ngetAccountCreated =	" + person.getAccountCreated() + "\r\n" +					
				"getName =	" + person.getEmail() + "\r\n" +
				"getFirstName =	" +person.getFirstName() + "\r\n" +
				"getId =	" + person.getId() +"\r\n" +
				"getLastName =	" + person.getLastName() + "\r\n" +
				"getName =	" +person.getName() + "\r\n","d");
	}
	
}
