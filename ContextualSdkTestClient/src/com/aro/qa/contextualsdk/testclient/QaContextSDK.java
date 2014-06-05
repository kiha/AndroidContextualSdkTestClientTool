package com.aro.qa.contextualsdk.testclient;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.aro.android.contextsdk.AroContextualSdk;
import com.aro.android.contextsdk.AroContextualSdk.CurrentLocationListener;
import com.aro.android.contextsdk.AroContextualSdk.MovementListener;
import com.aro.android.contextsdk.AroContextualSdk.OnUserCreatedListener;

import com.aro.android.contextsdk.apiobjects.GeoUri;
import com.aro.android.contextsdk.http.WebException;
import com.aro.android.contextsdk.http.WebResultHandler;
import com.aro.android.contextsdk.service.CollectorPolicyType;


public class QaContextSDK {
	
	Context mContext;
	AroContextualSdk mAroContextualSdk;
	ToolHelper qaHelper;
	ToolLogger toolLogger;
	Handler mHandler;

	final static String TAG = "QA_test";
	String mResult="Result Not Set on Device";

	public QaContextSDK(Context context, AroContextualSdk contextSdk) {
		
		super();
	
		this.mContext = context;
		qaHelper = new ToolHelper(context);
		toolLogger = new ToolLogger(context);
		this.mAroContextualSdk = contextSdk;
	}

	/***
	 * 
	 * Execute the corresponding SDK method testing.
	 * 	
	 * @param sdkMethodToTest
	 * @param testCaseData
	 * @return
	 */

	String testContextSdk(String sdkMethodToTest,String[] testCaseData){


		if(sdkMethodToTest.equalsIgnoreCase("contextSdk")){
			return createContextSDK(testCaseData);
		}

		if (sdkMethodToTest.equalsIgnoreCase("createUser")){
			return createUser_test(testCaseData);
		}		
/*		
		if (sdkMethodToTest.equalsIgnoreCase("sendCompressed")){
			return sendCompressed_test(testCaseData);
		}
*/		
		if (sdkMethodToTest.equalsIgnoreCase("getCurrentLocation")){
			return getCurrentLocation_test(testCaseData);
		}		
		
		if (sdkMethodToTest.equalsIgnoreCase("getCurrentMovement")){
			return getCurrentMovement_test(testCaseData);
		}		
/*		
		if (sdkMethodToTest.equalsIgnoreCase("getDeviceId")){
			return getDeviceId_test(testCaseData);
		}				
*/
		if (sdkMethodToTest.equalsIgnoreCase("isUserCreated")){
			return isUserCreated_test(testCaseData);
		}		
		
		if (sdkMethodToTest.equalsIgnoreCase("startCollector")){
			return startCollector_test(testCaseData);
		}		

		if (sdkMethodToTest.equalsIgnoreCase("stopCollector")){
			return stopCollector_test(testCaseData);
		}	
		
		toolLogger.qaLogs("Method " + sdkMethodToTest + "NOT FOUND");	
		return "Method " + sdkMethodToTest + "NOT FOUND";


	}	

	
	String getCurrentLocation_test(String[] testCaseData){
	/*	
		try {
			mContextSdk.getCurrentLocation(currentLocationListener);

			qaHelper.waitForWebListenerResponse(15);
			return mResult;
		}catch(Exception exp){
			toolLogger.qaLogs("contextSdk.getCurrentLocation_test Error "+exp);
			mResult= exp.toString();
		}
		
		*/
		 toolLogger.doInBackground("getCurrentLocation");
		 
		 return ToolLogger.result;
	}

	String getCurrentMovement_test(String[] testCaseData){

		try {
			boolean fast = Boolean.getBoolean(testCaseData[1]);

			mAroContextualSdk.getCurrentMovement(movementListener,fast);

			qaHelper.waitForWebListenerResponse(15);
			return mResult;
		}catch(Exception exp){
			toolLogger.qaLogs("contextSdk.getCurrentMovement_test Error "+exp);
			mResult= exp.toString();
		}
		return mResult;
	}


	String createUser_test(String[] testCaseData){

	try{
		
		mAroContextualSdk.createUser(onUserCreatedListener);

		qaHelper.waitForWebListenerResponse(10);
	}catch(Exception e){
		toolLogger.qaLogs("mContextSdk.createUser Exception: "+ e);
		return e.toString();
	}
		
		return mResult;
	}


	String isUserCreated_test(String[] testCaseData){
		boolean iuc;

		iuc = mAroContextualSdk.isUserCreated();

		toolLogger.qaLogs("contextSdk.isUserCreated() = "+ String.valueOf(iuc));
		return String.valueOf(iuc); 
	}	

/*
	String getDeviceId_test(String[] testCaseData){
		toolLogger.qaLogs("Calling getDeviceId...");
		mContextSdk.getDeviceId(webResultString);

		qaHelper.waitForWebListenerResponse(10);
		return mResult;
	}
*/

	String createContextSDK(String[] testCaseData){

		String applicationId=testCaseData[1];
		String serverURL    =testCaseData[2];

		try{

			toolLogger.qaLogs("CreateContext parameters:" + applicationId + "," + serverURL);

			AroContextualSdk myContextSdk = new AroContextualSdk(mContext, applicationId, serverURL);
			myContextSdk.isUserCreated();

			mResult = "SDK Context Created";
		}catch (Exception exp){
			toolLogger.qaLogs("Error on createContextSDK. " + exp,"e");
			return exp.toString();
		}

		return mResult;
	}

/*
	String sendCompressed_test(String[] testCaseData){
		
		String ObservJsonString = testCaseData[1]; 
		
		JSONArray observationsJSONArray;
		try {
			Log.e(TAG, "Parameter:" + ObservJsonString);
			observationsJSONArray = new JSONArray(ObservJsonString);

	//		mContextSdk.sendCompressed(observationsJSONArray);
		
			qaHelper.waitForWebListenerResponse(10);
		} catch (JSONException e) {
			Log.e(TAG, "sendCompressed_test JSONException. " + e);
		}catch (Exception e) {
			Log.e(TAG, "sendCompressed_test Error. " + e);
		}				
		return "Send Compress Depracated method";
	}
	*/
	
	String startCollector_test(String[] testCaseData){

		try{

			mAroContextualSdk.startCollector(CollectorPolicyType.BASIC);
			mResult = "startCollector";
		}catch (Exception exp){
			toolLogger.qaLogs("Error on startCollector. " + exp,"e");
			return exp.toString();
		}

		return mResult;
	}
	
	String stopCollector_test(String[] testCaseData){

		try{

			mAroContextualSdk.stopCollector();
			mResult = "stopCollector";
		}catch (Exception exp){
			toolLogger.qaLogs("Error on stopCollector. " + exp,"e");
			return exp.toString();
		}

		return mResult;
	}
		
	
	
	OnUserCreatedListener onUserCreatedListener = new OnUserCreatedListener() {

		@Override
		public void onUserCreatedFailure(WebException arg0) {
			Log.i(TAG,"==> onUserCreatedFailure. " + arg0);
			ToolHelper.mWait=false;
			
		}

		@Override
		public void onUserCreatedSuccess(String arg0) {
			mResult="onUserCreatedSuccess:"+arg0;
			Log.i(TAG,"==> onUserCreated. " + arg0);
			ToolHelper.mWait=false;
			
		}
	};


	WebResultHandler<String> webResultString = new WebResultHandler<String>(Looper.getMainLooper()) {
		
		@Override
		protected void onWebFailure(WebException arg0) {
			mResult="ContextSdk WebException " + arg0;
			Log.e(TAG, "WebFailure. WebException = " + arg0.getMessage());
			ToolHelper.mWait=false;	
		}

		@Override
		protected void onWebSuccess(String arg0) {
			Log.i(TAG,"==> WebSuccess. " + arg0);
			mResult=arg0;
			ToolHelper.mWait=false;
		}
	};


	MovementListener movementListener = new MovementListener() {

		@Override
		public void onMovement(
				com.aro.android.contextsdk.AroContextualSdk.MotionState arg0,
				double arg1, double[] arg2) {
			
			toolLogger.qaLogs("onMovement Listener");

			mResult=arg0 + ":::" + arg1 + ":::" + arg2;

			for (double dbl : arg2){				
				mResult = mResult + ":::" + Double.toString(dbl);
			}
			ToolHelper.mWait=false;
			
		}
	};
	
	
	CurrentLocationListener currentLocationListener = new CurrentLocationListener() {
		
		@Override
		public void onCurrentLocation(GeoUri arg0) {
			toolLogger.qaLogs("onMovement Listener");
			toolLogger.qaLogs(arg0.toStringComplete(mContext));
			mResult=arg0 + ":::" + arg0.toStringComplete(mContext);
			
			ToolHelper.mWait=false;			
		}
	};
}

