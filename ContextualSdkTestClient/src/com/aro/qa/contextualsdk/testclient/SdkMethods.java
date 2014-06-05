package com.aro.qa.contextualsdk.testclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.aro.android.contextsdk.AroContextualSdk;

public class SdkMethods {
	
	String applicationId = "fee8baf337283e5ba320a836c16dd80a";
	//String applicationId = "@#$%^&*()";
	String serverURL = "https://lifestream-dev0.aro.com";
	private static String TAG="QA_test";
	private static AroContextualSdk mAroContextualSdk;
	
	
	Handler mHandler;
	Context mContext;
	ToolLogger toolLogger;
	ToolHelper qaHelper;

	QaContextSDK qaContextSDK;
	QaObservationSDK qaObservation;
	QaActivitySDK qaActivity;	
	QaPlaceSDK qaPlace;
	QaCategorySDK qaCategory;
	QaPersonSDK qaPerson;
	QaTraitSDK qaTrait;

	public SdkMethods(Context mContext) {
		super();
		this.mContext = mContext;
		qaHelper = new ToolHelper(mContext);
		mAroContextualSdk = new AroContextualSdk(mContext, applicationId, serverURL);
		Log.d(TAG, "AroContextualSdk.  applicationId = " + applicationId + ". ServerURL = " + serverURL); 
		
		qaContextSDK = new QaContextSDK(mContext, mAroContextualSdk);
		qaObservation = new QaObservationSDK(mContext, mAroContextualSdk);
		qaActivity = new QaActivitySDK(mContext, mAroContextualSdk);
		qaPlace = new QaPlaceSDK(mContext, mAroContextualSdk);
		qaCategory = new QaCategorySDK(mContext, mAroContextualSdk);
		qaPerson = new QaPersonSDK(mContext, mAroContextualSdk);
		qaTrait = new QaTraitSDK(mContext, mAroContextualSdk);
		
		mHandler = new Handler(Looper.getMainLooper());
	}	
	
	@SuppressLint("DefaultLocale")
	String callMethodTest(String dataFromServer){
		String[] sdkClass;
		String result="Error on Client. SdkMethods";
		String[] testCaseData = dataFromServer.split(":::");
		toolLogger = new ToolLogger(mContext);
		
		String methodToTest = testCaseData[0];

	/*
		 SecurePreferences securePreferences = new SecurePreferences(mContext);
         UserAccount userAccount = UserAccount.getUserAccount(securePreferences);
         Log.d(TAG, "1 userAccount.getAccessToken() =  " + userAccount.getAccessToken());
     */		
		
		try	{


			sdkClass = methodToTest.split(":");
			if (sdkClass.length < 2) {
				toolLogger.qaLogs("Data From Server parsed lenght = " + sdkClass.length); 
				toolLogger.qaLogs("Is Sdk attribute separated by ':'?" + ". methodToTest = "+methodToTest);
				
				for (String tmp : testCaseData){
					toolLogger.qaLogs(">" + tmp);
				}
				return "Empty method";
			}
			toolLogger.qaLogs("=====   " + sdkClass[0] + "." + sdkClass[1] + "   =====");
			
			if (sdkClass[0].toLowerCase().contains("contextsdk")){
				
				return qaContextSDK.testContextSdk(sdkClass[1], testCaseData);
				
			}
				
			if (sdkClass[0].equalsIgnoreCase("observation")){
				
				return qaObservation.testObservationSDK(sdkClass[1], testCaseData);
				
			}			
			if (sdkClass[0].equalsIgnoreCase("ActivityBuilder") || sdkClass[0].equalsIgnoreCase("Activity")){
				
				return qaActivity.testActivitySDK(sdkClass[1], testCaseData);
				
			}	
			
			if (sdkClass[0].equalsIgnoreCase("PlaceBuilder") || sdkClass[0].equalsIgnoreCase("SdkPlace")){
				
				return qaPlace.testPlaceSDK(sdkClass[1], testCaseData);
				
			}
			
			if (sdkClass[0].equalsIgnoreCase("category")){
				
				return qaCategory.testCategorySDK(sdkClass[1], testCaseData);
				
			}
			
			if (sdkClass[0].equalsIgnoreCase("person")){
				
				return qaPerson.testPersonSDK(sdkClass[1], testCaseData);
				
			}	
			
			if (sdkClass[0].equalsIgnoreCase("trait")){

				return qaTrait.testTraitSDK(sdkClass[1], testCaseData);
				
			}				
			
			toolLogger.qaLogs("SDK '" + sdkClass[0] + "' NOT FOUND");
		}
		catch(Exception e){	
			e.printStackTrace();
			Log.d(TAG,"Error running Method." + e);
			
			for (String tmp : testCaseData){
				toolLogger.qaLogs(">>" + tmp);
			}
		}
		return result;
	}	
	
	

	
}
