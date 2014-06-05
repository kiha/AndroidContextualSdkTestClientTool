package com.aro.qa.contextualsdk.testclient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.aro.android.contextsdk.AroContextualSdk;
import com.aro.android.contextsdk.BaseActivityRequest;
import com.aro.android.contextsdk.BaseActivityRequest.Builder;
import com.aro.android.contextsdk.apiobjects.BaseActivity;
import com.aro.android.contextsdk.apiobjects.Trait;
import com.aro.android.contextsdk.http.WebException;
import com.aro.android.contextsdk.http.WebResultHandler;

public class QaActivitySDK {

	Context mContext;
	AroContextualSdk mAroContextualSdk;
	BaseActivityRequest baseActivityRequest;
	Builder baseActivityBuilder;
	ToolHelper qaHelper;
	ToolLogger toolLogger;
	List<BaseActivity> mBaseActivityList;


	final static String TAG = "QA_test";
	String mResult = "Result No Set";

	public QaActivitySDK(Context mContext,AroContextualSdk contextSdk) {
		super();
		this.mContext = mContext;
		this.mAroContextualSdk = contextSdk;
		qaHelper = new ToolHelper(mContext);
		toolLogger = new ToolLogger(mContext);
	}	


	String testActivitySDK(String sdkMethod,String[] testCaseData){

		if(sdkMethod.equalsIgnoreCase("getActivity")){
			return getActivity(testCaseData);	
		}

		if(sdkMethod.equalsIgnoreCase("getById")){
			return getById(testCaseData);	
		}		

		if(sdkMethod.equalsIgnoreCase("getByIds")){
			return getByIds(testCaseData);	
		}	

		if(sdkMethod.equalsIgnoreCase("getbaseActivities")){
			return baseActivities_test(testCaseData);	
		}			

		if(sdkMethod.equalsIgnoreCase("getbaseActivity")){
			return baseActivity_test(testCaseData);	
		}	

		toolLogger.qaLogs("Activity. Method " + sdkMethod + " NOT FOUND");	
		return "Method " + sdkMethod + " NOT FOUND";
	}


/***
 * Executes mAroContextualSdk.getBaseActivity
 * 	
 * @param testCaseData
 * @return
 */
	
	
	String baseActivity_test(String[] testCaseData){

		String id;

		try{

			if (testCaseData[1].equalsIgnoreCase("auto")){
			
				//First get one just created activity
				toolLogger.qaLogs("Searching for an Activity id");
				baseActivityBuilder = mAroContextualSdk.getBaseActivityRequestBuilder();
				baseActivityBuilder.setStartTime(1398042000000L);
				baseActivityBuilder.setEndTime(System.currentTimeMillis());
				baseActivityBuilder.setLimit(Integer.valueOf(2));
				baseActivityRequest = baseActivityBuilder.build();
				//	baseActivityRequest.getById("a76829a0-73b2-48bb-810e-f33481113f0b", webResultHandlerActivity);
				baseActivityRequest.execute(webResultReturnActivity);
				qaHelper.waitForWebListenerResponse(20);
				id = mBaseActivityList.get(0).getId();	
			}else{
				id= testCaseData[1];
			}
					
			//Now, search for activity Id.	
			toolLogger.qaLogs("Activity id = " + id);
			mAroContextualSdk.getBaseActivity(id, webResultHandlerActivity);
			qaHelper.waitForWebListenerResponse(15);
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG,"Error on activity.getActivity. " + e);
			return e.toString();		
		}

		return mResult;
	}


/***
 * Executes mAroContextualSdk.getBaseActivities(ids, webResultHandlerActivityMap);
 * 	
 * @param testCaseData
 * @return
 */
	
	
	String baseActivities_test(String[] testCaseData){

	List<String> ids = new ArrayList<String>();
		
	
	try{
		if (testCaseData[1].equalsIgnoreCase("auto")){
			//First get  just created activity
			toolLogger.qaLogs("Searching for an Activity id");
			baseActivityBuilder = mAroContextualSdk.getBaseActivityRequestBuilder();
			baseActivityBuilder.setStartTime(1398042000000L);
			baseActivityBuilder.setEndTime(System.currentTimeMillis());
			baseActivityBuilder.setLimit(Integer.valueOf(2));
			baseActivityRequest = baseActivityBuilder.build();
			//	baseActivityRequest.getById("a76829a0-73b2-48bb-810e-f33481113f0b", webResultHandlerActivity);
			baseActivityRequest.execute(webResultReturnActivity);
			qaHelper.waitForWebListenerResponse(20);		
			
			for (BaseActivity ba : mBaseActivityList){
				ids.add(ba.getId());
			}
			
		}else{
			//Iterate through test case parameter to set place configuration
			for (int i=1; i < testCaseData.length; i++){
				ids.add(testCaseData[i]);
			}
		}
			toolLogger.qaLogs("ids total = " + ids.size());
			mAroContextualSdk.getBaseActivities(ids, webResultHandlerActivityMap);
			qaHelper.waitForWebListenerResponse(15);
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG,"Error on mAroContextualSdk.getBaseActivities. " + e);
			return e.toString();		
		}

		return mResult;
	}	


	String getActivity(String[] testCaseData){	
		toolLogger.qaLogs("StartDate= " + testCaseData[1] + " EndDate " + testCaseData[2] + " Limit = " + testCaseData[3]);
		try{
			baseActivityBuilder = mAroContextualSdk.getBaseActivityRequestBuilder();
			baseActivityBuilder.setStartTime(Long.valueOf(testCaseData[1]));

			if (testCaseData[2].equalsIgnoreCase("today")){
				baseActivityBuilder.setEndTime(System.currentTimeMillis());
			}else{
				baseActivityBuilder.setEndTime(Long.valueOf(testCaseData[2]));
			}

			baseActivityBuilder.setLimit(Integer.valueOf(testCaseData[3]));
			baseActivityRequest = baseActivityBuilder.build();


			//	baseActivityRequest.getById("a76829a0-73b2-48bb-810e-f33481113f0b", webResultHandlerActivity);
			baseActivityRequest.execute(webResultAct);
			qaHelper.waitForWebListenerResponse(15);

		}catch (Exception e){
			Log.e(TAG,"Error on activity.getActivity. " + e);
			return e.toString();
		}

		return mResult;

	}


	String getById(String[] testCaseData){

		String id = testCaseData[1];

		try{

			if (id.equalsIgnoreCase("auto")){
			
				//First get one just created activity
				toolLogger.qaLogs("Searching for an Activity id");
				baseActivityBuilder = mAroContextualSdk.getBaseActivityRequestBuilder();
				baseActivityBuilder.setStartTime(1398042000000L);
				baseActivityBuilder.setEndTime(System.currentTimeMillis());
				baseActivityBuilder.setLimit(Integer.valueOf(2));
				baseActivityRequest = baseActivityBuilder.build();
				//	baseActivityRequest.getById("a76829a0-73b2-48bb-810e-f33481113f0b", webResultHandlerActivity);
				baseActivityRequest.execute(webResultReturnActivity);
				qaHelper.waitForWebListenerResponse(20);
				id = mBaseActivityList.get(0).getId();	
			}
			
			//Now, search for activity Id.	
			baseActivityBuilder = mAroContextualSdk.getBaseActivityRequestBuilder();
			baseActivityRequest = baseActivityBuilder.build();
			toolLogger.qaLogs("Activity Id = " + id);
			baseActivityRequest.getById(id, webResultHandlerActivity);
			qaHelper.waitForWebListenerResponse(15);
		}catch (Exception e){
			Log.e(TAG,"Error on activity.getActivity. " + e);
			e.printStackTrace();
			return "Error on activity.getActivity. " + e.toString();
		}
		return mResult;
	}


	String getByIds(String[] testCaseData){
		
		List<String> ids = new ArrayList<String>();
		
		if (testCaseData[1].equalsIgnoreCase("auto")){
			//First get  just created activity
			toolLogger.qaLogs("Searching for an Activity id");
			baseActivityBuilder = mAroContextualSdk.getBaseActivityRequestBuilder();
			baseActivityBuilder.setStartTime(1398042000000L);
			baseActivityBuilder.setEndTime(System.currentTimeMillis());
			baseActivityBuilder.setLimit(Integer.valueOf(2));
			baseActivityRequest = baseActivityBuilder.build();
			//	baseActivityRequest.getById("a76829a0-73b2-48bb-810e-f33481113f0b", webResultHandlerActivity);
			baseActivityRequest.execute(webResultReturnActivity);
			qaHelper.waitForWebListenerResponse(20);		
			
			for (BaseActivity ba : mBaseActivityList){
				ids.add(ba.getId());
			}
			
		}else{
			//Iterate through test case parameter to set place configuration
			for (int i=1; i < testCaseData.length; i++){
				ids.add(testCaseData[i]);
			}
		}

		try{
			baseActivityBuilder = mAroContextualSdk.getBaseActivityRequestBuilder();	
			baseActivityRequest = baseActivityBuilder.build();
			baseActivityRequest.getByIds(ids, webResultHandlerActivityMap);
			qaHelper.waitForWebListenerResponse(15);
		}catch (Exception e){
			Log.e(TAG,"Error on activity.getActivity. " + e);
			return e.toString();
		}
		return mResult;
	}


	void displayActivityFields(BaseActivity baseActivity){

		if (baseActivity == null) return;
		
		try {
			toolLogger.qaLogs( "getId:"+baseActivity.getId() + "\r\n" +
					"getName    :"+ baseActivity.getName() + "\r\n"+
					"getDescription:"+baseActivity.getDescription() + "\r\n"+
					"Type:"+baseActivity.getJsonObject().getString("type") + "\r\n"+
					"getSource   :"+ baseActivity.getSource());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	WebResultHandler<BaseActivity> webResultHandlerActivity = new WebResultHandler<BaseActivity>(Looper.getMainLooper()) {

		@Override
		protected void onWebFailure(WebException arg0) {
			toolLogger.qaLogs("Activity onWebFailure : " + arg0);
			mResult="onWebFailure. " + arg0 ;
			ToolHelper.mWait=false;	
		}


		@Override
		protected void onWebSuccess(BaseActivity baseActivity) {
			toolLogger.qaLogs("Activity OnWebSuccess");

			displayActivityFields(baseActivity);
			mResult = baseActivity.getJsonObject().toString();
			ToolHelper.mWait=false;	

		}


	};

	WebResultHandler<List<BaseActivity>> webResultAct = new WebResultHandler<List<BaseActivity>>(Looper.getMainLooper()) {

		@Override
		protected void onWebFailure(WebException arg0) {
			toolLogger.qaLogs("onWebFailure List<BaseActivity>>");
			mResult="onWebFailure. " + arg0;
			ToolHelper.mWait=false;	
		}

		@Override
		protected void onWebSuccess(List<BaseActivity> arg0) {
			int i=0;
			List<BaseActivity> BaseActivityList = (ArrayList<BaseActivity>) arg0;		
			toolLogger.qaLogs("Total Activities = " + BaseActivityList.size());				
			StringBuilder placeString = new StringBuilder( "{  \"activities\": [ " );

			for (BaseActivity baseActivity : BaseActivityList){
				toolLogger.qaLogs("==========     " + ++i + "     =========");
				displayActivityFields(baseActivity);

				placeString.append(baseActivity.getJsonObject().toString());
				placeString.append(",");
			}
			placeString.deleteCharAt(placeString.length()-1);
			placeString.append(" ] }");
			mResult = placeString.toString();
			ToolHelper.mWait=false;	
		}
	};

	
	WebResultHandler<List<BaseActivity>> webResultReturnActivity = new WebResultHandler<List<BaseActivity>>(Looper.getMainLooper()) {

		@Override
		protected void onWebFailure(WebException arg0) {
			toolLogger.qaLogs("onWebFailure List<BaseActivity>>");
			mResult="onWebFailure. " + arg0;
			ToolHelper.mWait=false;	
		}

		@Override
		protected void onWebSuccess(List<BaseActivity> arg0) {
			mBaseActivityList = (List<BaseActivity>)arg0;
			ToolHelper.mWait=false;	
		}
	};	 
	
	WebResultHandler<Map<String,BaseActivity>> webResultHandlerActivityMap = new WebResultHandler<Map<String,BaseActivity>>() {

		@Override
		protected void onWebSuccess(Map<String, BaseActivity> paramResult) {
			toolLogger.qaLogs("onWebSuccess <Map<String,BaseActivity>>");
			int i=1;
			JSONObject jsonPlace;
			StringBuilder placeString = new StringBuilder( "{  \"activities\": [ " );

			for (Map.Entry<String, BaseActivity> baseActivity : paramResult.entrySet()){
				if (baseActivity.getValue() == null) continue;
				//Get all places Json to send it back to desktop
				jsonPlace = baseActivity.getValue().getJsonObject();
				placeString.append(jsonPlace.toString());
				placeString.append(",");

				toolLogger.qaLogs("Key:" + baseActivity.getKey());
				displayActivityFields(baseActivity.getValue());

			}

			placeString.deleteCharAt(placeString.length()-1);
			placeString.append(" ] }");
			mResult = placeString.toString();
			ToolHelper.mWait=false;	

		}

		@Override
		protected void onWebFailure(WebException paramWebException) {
			mResult = "onWebFailure. " + paramWebException;
			ToolHelper.mWait=false;	
		}

	};

}