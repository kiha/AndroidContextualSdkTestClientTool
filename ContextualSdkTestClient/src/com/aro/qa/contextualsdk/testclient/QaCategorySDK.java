package com.aro.qa.contextualsdk.testclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.aro.android.contextsdk.AroContextualSdk;
import com.aro.android.contextsdk.CategoryRequest;
import com.aro.android.contextsdk.apiobjects.Category;
import com.aro.android.contextsdk.http.WebException;
import com.aro.android.contextsdk.http.WebResultHandler;

public class QaCategorySDK {  


	Context mContext;
	AroContextualSdk mAroContextualSdk;

	ToolHelper qaHelper;
	ToolLogger toolLogger;

	CategoryRequest categoryRequest;

	final static String TAG = "QA_test";
	String mResult="CategoryRequest Result Not Set on Device";

	public QaCategorySDK(Context context, AroContextualSdk contextSdk) {
		super();

		this.mContext = context;
		this.mAroContextualSdk = contextSdk;
		qaHelper = new ToolHelper(mContext);
		toolLogger = new ToolLogger(mContext);
	}	


	String testCategorySDK(String sdkMethod,String[] testCaseData){

		
		if(sdkMethod.equalsIgnoreCase("execute")){

			return categoryRequestExecute_test(testCaseData);	
		}
		
		if(sdkMethod.equalsIgnoreCase("byId")){

			return categoryById_test(testCaseData);	
		}

		if(sdkMethod.equalsIgnoreCase("byIds")){

			return categoryByIds_test(testCaseData);	
		}
		
		if(sdkMethod.equalsIgnoreCase("byName")){

			return categoryByName_test(testCaseData);	
		}		

		if(sdkMethod.equalsIgnoreCase("getCategoryById")){

			return categoryByIds_test(testCaseData);	
		}
		
		if(sdkMethod.equalsIgnoreCase("getCategoriesByIds")){

			return getCategories_test(testCaseData);	
		}	

		toolLogger.qaLogs("Method " + sdkMethod + " NOT FOUND");	
		return "Method " + sdkMethod + " NOT FOUND";
	}

	
	String getCategory_test(String[] testCaseData){

		String id = testCaseData[1];

		try{
			toolLogger.qaLogs("Category id = " + id);
			mAroContextualSdk.getCategory(id, webResultHandlerCategory);
			qaHelper.waitForWebListenerResponse(15);
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG,"Error on activity.getActivity. " + e);
			return e.toString();		
		}

		return mResult;
	}


	String getCategories_test(String[] testCaseData){

		try{
			List<String> ids = new ArrayList<String>();

			//Iterate through test case parameter to set place configuration
			for (int i=1; i < testCaseData.length; i++){
				ids.add(testCaseData[i]);
			}
			toolLogger.qaLogs("ids total = " + ids.size());
			mAroContextualSdk.getCategories(ids, webResultHandlerMap);
			qaHelper.waitForWebListenerResponse(15);
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG,"Error on mAroContextualSdk.getBaseActivities. " + e);
			return e.toString();		
		}

		return mResult;
	}		
	
	
	
	
	String categoryRequestExecute_test(String[] testCaseData){
		try {
			
			CategoryRequest.Builder categoryRequestBuilder = mAroContextualSdk.getCategoryRequestBuilder();
			categoryRequest = categoryRequestBuilder.build();
								
			categoryRequest.execute(webResultHandler);
			qaHelper.waitForWebListenerResponse(10);

		}catch(Exception e){
			toolLogger.qaLogs("categoryRequest.getById Execute : " + e);
		}

		return mResult;
	}	
	
	String categoryByName_test(String[] testCaseData){
		String name = testCaseData[1];
		try {
			
			CategoryRequest.Builder categoryRequestBuilder = mAroContextualSdk.getCategoryRequestBuilder();
			categoryRequestBuilder.setName(name);
			categoryRequest = categoryRequestBuilder.build();
			
			categoryRequest.execute(webResultHandler);
			qaHelper.waitForWebListenerResponse(10);

		}catch(Exception e){
			toolLogger.qaLogs("CategoryRequest.ByName Exception : " + e);
		}

		return mResult;
	}	
	

	String categoryById_test(String[] testCaseData){
		String id = testCaseData[1];
		toolLogger.qaLogs("categoryRequest.getById");
		try {
			
			CategoryRequest.Builder categoryRequestBuilder = mAroContextualSdk.getCategoryRequestBuilder();
			categoryRequest = categoryRequestBuilder.build();
			categoryRequest.getById(id, webResultHandlerCategory);			
			//categoryRequest.execute(webResultHandler);
			qaHelper.waitForWebListenerResponse(10);

		}catch(Exception e){
			toolLogger.qaLogs("categoryRequest.getById Exception : " + e);
		}

		return mResult;
	}



	String categoryByIds_test(String[] testCaseData){
		toolLogger.qaLogs("categoryRequest.getByIdS");
		mResult="No result on categoryRequest.getByIds";
		try {
			CategoryRequest.Builder categoryRequestBuilder = mAroContextualSdk.getCategoryRequestBuilder();
			categoryRequest = categoryRequestBuilder.build();
			Collection<String> ids = new ArrayList<String>();
	

			//Iterate through test case parameter to set place configuration
			for (int i=1; i < testCaseData.length; i++){
				ids.add(testCaseData[i]);
			}
	
			categoryRequest.getByIds(ids, webResultHandlerMap);
			qaHelper.waitForWebListenerResponse(10);

		}catch(Exception e){
			toolLogger.qaLogs("categoryRequest.getByIds Exception : " + e);
		}

		return mResult;
	}


	WebResultHandler<Category> webResultHandlerCategory = new WebResultHandler<Category>() {

		@Override
		protected void onWebSuccess(Category paramResult) {			
			toolLogger.qaLogs("WebResultHandler<Category> onWebSuccess");
			JSONObject jsonCatego;

			//Display Place data on Device logs				
			showCategoryObjectFields(paramResult);

			jsonCatego = paramResult.getJsonObject();
			
			mResult = jsonCatego.toString();
			
			ToolHelper.mWait=false;	
		}
		
		@Override
		protected void onWebFailure(WebException paramWebException) {
			toolLogger.qaLogs("Place WebException = " + paramWebException);
			mResult = "onWebFailure:" + paramWebException;
			ToolHelper.mWait=false;	
		}
		
	};

	
	
	WebResultHandler<Map<String,Category>> webResultHandlerMap = new WebResultHandler<Map<String,Category>>(){
		@Override
		protected void onWebSuccess(Map<String, Category> paramResult) {
			toolLogger.qaLogs("WebResultHandler<Map<String,Category>> onWebSuccess");

			JSONObject jsonPlace;
			StringBuilder placeString = new StringBuilder( "{  \"categories\": [ " );
			
			for (Map.Entry<String, Category> categoMap : paramResult.entrySet()){
				if (categoMap.getValue() == null) continue;
				//Get all places Json to send it back to desktop
				//toolLogger.qaLogs("Key:" + categoMap.getKey());
				showCategoryObjectFields(categoMap.getValue());
				
				jsonPlace = categoMap.getValue().getJsonObject();
				placeString.append(jsonPlace.toString());
				placeString.append(",");
				
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
	
	
	WebResultHandler<List<Category>> webResultHandler = new WebResultHandler<List<Category>>() {

		@Override
		protected void onWebSuccess(List<Category> paramResult) {
			int i=1;
			JSONObject jsonCatego;
			StringBuilder placeString = new StringBuilder( "{  \"categories\": [ " );

			toolLogger.qaLogs("WebResultHandler<List<Category>> onWebSuccess");
			toolLogger.qaLogs("Category total = " + paramResult.size());

			for (Category theCategory :paramResult){

				//Get all places Json to send it back to desktop
				jsonCatego = theCategory.getJsonObject();
				placeString.append(jsonCatego.toString());
				placeString.append(",");

				//Display Place data on Device logs
				toolLogger.qaLogs("===   " + i++  + ". "+ theCategory.getName());
				showCategoryObjectFields(theCategory);
			}
			placeString.deleteCharAt(placeString.length()-1);
			placeString.append(" ] }");
			mResult = placeString.toString();
			ToolHelper.mWait=false;	
		}
		
		@Override
		protected void onWebFailure(WebException paramWebException) {
			toolLogger.qaLogs("Place WebException = " + paramWebException);
			mResult = "onWebFailure:" + paramWebException;
			ToolHelper.mWait=false;	

		}
		
	};


	void showCategoryObjectFields(Category category){

		toolLogger.qaLogs("\r\ngetId =	" + category.getId() + "\r\n" +					
				"getName =	" + category.getName() + "\r\n" +
				"getParentCategoryId =	" + category.getParentCategoryId() + "\r\n" +
				"getParentCategoryName =	" + category.getParentCategoryName() +"\r\n" +
				"getPluralName =	" + category.getPluralName() + "\r\n","d");
	}


}
