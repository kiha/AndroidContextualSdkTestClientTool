package com.aro.qa.contextualsdk.testclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

import com.aro.android.contextsdk.AroContextualSdk;
import com.aro.android.contextsdk.CategoryRequest;
import com.aro.android.contextsdk.PersonRequest;
import com.aro.android.contextsdk.TraitRequest;
import com.aro.android.contextsdk.apiobjects.Category;
import com.aro.android.contextsdk.apiobjects.Person;
import com.aro.android.contextsdk.apiobjects.Trait;
import com.aro.android.contextsdk.http.WebException;
import com.aro.android.contextsdk.http.WebResultHandler;
import com.aro.android.contextsdk.p.api.ApiHelper;

public class QaTraitSDK {
	
	Context mContext;
	AroContextualSdk mAroContextualSdk;

	ToolHelper qaHelper;
	ToolLogger toolLogger;

	TraitRequest traitRequest;

	final static String TAG = "QA_test";
	String mResult="Result Not Set on Device";
	String serverURL = "https://lifestream-dev0.aro.com";

	public QaTraitSDK(Context context, AroContextualSdk aroContextualSdk) {
		super();

		this.mContext = context;
		this.mAroContextualSdk = aroContextualSdk;
		qaHelper = new ToolHelper(mContext);
		toolLogger = new ToolLogger(mContext);
	}		
	
	
	String testTraitSDK(String sdkMethod,String[] testCaseData){

			if(sdkMethod.equalsIgnoreCase("getTrait")){

				return getTrait_test(testCaseData);	
			}

			if(sdkMethod.equalsIgnoreCase("getTraits")){

				return getTraits_test(testCaseData);	
			}
			
			if(sdkMethod.equalsIgnoreCase("getUserTraits")){
				toolLogger.qaLogs("getUserTraits");
				return getPersonTraits_test(testCaseData);	
			}			
			
			toolLogger.qaLogs("Traits. Method " + sdkMethod + " NOT FOUND");	
			return "Method " + sdkMethod + " NOT FOUND";

	}
	

	String getTrait_test(String[] testCaseData){
		
		//TraitRequest.execute(webResultHandlerTraitList);
	
		String traitId = testCaseData[1];
		
		try{
			mAroContextualSdk.getTrait(traitId, webResultHandlerTrait);
			qaHelper.waitForWebListenerResponse(10);
			
		}catch(Exception e){
			toolLogger.qaLogs("Person.getMe Exception " + e);
		}
		
		
		return mResult;
	}	
	
	
	
	String getTraits_test(String[] testCaseData){
		
		try{

			List<String> ids = new ArrayList<String>();
			
			//Iterate through test case parameter to set place configuration
			for (int i=1; i < testCaseData.length; i++){
				ids.add(testCaseData[i]);
			}
			
			mAroContextualSdk.getTraits(ids, webResultHandlerTraits);
			qaHelper.waitForWebListenerResponse(10);
			
		}catch(Exception e){
			e.printStackTrace();
			toolLogger.qaLogs("Person.getMe Exception " + e);
		}
		
		return mResult;
	}	
	
	
	String getPersonTraits_test(String[] testCaseData){
		
		try{
			QaPersonSDK qaPersonSDK = new QaPersonSDK(mContext, mAroContextualSdk);

			JSONObject personObject = new JSONObject(qaPersonSDK.getMe());	
			Person person = new Person(personObject);	
			toolLogger.qaLogs("Person Id = " + person.getId());
			mAroContextualSdk.getTraits(person, webResultHandlerTraits);
			qaHelper.waitForWebListenerResponse(10);
			
		}catch(Exception e){
			toolLogger.qaLogs("Person.getMe Exception " + e);
		}
		
		return mResult;
	}	
	
	
	
	void showTraitFields(Trait theTrait){
		
		
		toolLogger.qaLogs("\r\ngetId=	" + theTrait.getId() + "\r\n" +					
				"getDescription =	" + theTrait.getDescription() + "\r\n" +
				"getExplanation =	" +theTrait.getExplanation() + "\r\n" +
				"getName =	" +theTrait.getName() + "\r\n","d");
		
		
		
	}
	
	WebResultHandler<Person> webResultHandler = new WebResultHandler<Person>() {

		@Override
		protected void onWebSuccess(Person person) {
			toolLogger.qaLogs("WebResultHandler onWebSuccess");
			//showPersonFields(person);		
			
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
	
	
	WebResultHandler<Trait> webResultHandlerTrait = new WebResultHandler<Trait>() {
		@Override
		protected void onWebSuccess(Trait arg0) {
			toolLogger.qaLogs("webResultHandlerTrait onWebSuccess" + arg0);
			mResult = arg0.getJsonObject().toString();
			ToolHelper.mWait=false;					
		}
		
		
		@Override
		protected void onWebFailure(WebException arg0) {
			toolLogger.qaLogs("onWebFailure " + arg0);
			
			mResult =arg0.toString();
			ToolHelper.mWait=false;	
			
		}
	};
	
	
	WebResultHandler<List<Trait>> webResultHandlerTraitList = new WebResultHandler <List<Trait>>() {
		
		@Override
		protected void onWebSuccess(List<Trait> arg0) {
			toolLogger.qaLogs("onWebSuccess List<Trait>");
			StringBuilder placeString = new StringBuilder( "{  \"traits\": [ " );
			
			for (Trait trait : arg0){
				
				//Get all places Json to send it back to desktop
				placeString.append(trait.getJsonObject().toString());
				placeString.append(",");
								
				showTraitFields(trait);	
			}
			placeString.deleteCharAt(placeString.length()-1);
			placeString.append(" ] }");
			mResult = placeString.toString();
			ToolHelper.mWait=false;							
		}
		
		@Override
		protected void onWebFailure(WebException arg0) {
			toolLogger.qaLogs("onWebFailure " + arg0);
			
			mResult =arg0.toString();
			ToolHelper.mWait=false;	
			
		}
		
	};	
	
	
	WebResultHandler<Map<String,Trait>> webResultHandlerTraits = new WebResultHandler<Map<String,Trait>>(){
		@Override
		protected void onWebSuccess(Map<String, Trait> paramResult) {
			toolLogger.qaLogs("onWebSuccess <Map<String,Trait>>. " + paramResult.size());
			int i=1;
			JSONObject jsonPlace;
			StringBuilder placeString = new StringBuilder( "{  \"traits\": [ " );
			
			for (Map.Entry<String, Trait> categoMap : paramResult.entrySet()){
				if (categoMap.getValue() == null) continue;
				//Get all places Json to send it back to desktop
				jsonPlace = categoMap.getValue().getJsonObject();
				placeString.append(jsonPlace.toString());
				placeString.append(",");
								
				toolLogger.qaLogs("Key:" + categoMap.getKey());
				showTraitFields(categoMap.getValue());
				
			}
			
			placeString.deleteCharAt(placeString.length()-1);
			placeString.append(" ] }");
			mResult = placeString.toString();
			ToolHelper.mWait=false;	

		}
		
		@Override
		protected void onWebFailure(WebException paramWebException) {
			
			
			
			ToolHelper.mWait=false;	
		}

		
	};
		
	
}
