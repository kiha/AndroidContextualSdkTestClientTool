package com.aro.qa.contextualsdk.testclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;

import com.aro.android.contextsdk.AroContextualSdk;
import com.aro.android.contextsdk.ObservationRequest;
import com.aro.android.contextsdk.apiobjects.UserAccount;
import com.aro.android.contextsdk.http.WebException;
import com.aro.android.contextsdk.http.WebResultHandler;
import com.aro.android.contextsdk.p.SecurePreferences;
import com.aro.android.contextsdk.p.collector.sensors.SensorAsyncCollector;
import com.aro.android.contextsdk.p.db.AROObservationUploader.OnObservationUploadingFinished;
import com.aro.android.contextsdk.p.db.ObservationsDb;
import com.aro.android.contextsdk.p.observation.Observation;
import com.aro.android.contextsdk.p.observation.OnNewObservationListener;


public class QaObservationSDK {

	Context mContext;
	AroContextualSdk mAroContextualSdk;
	ToolHelper qaHelper;
	ToolLogger toolLogger;

	final static String TAG = "QA_test";
	String mResult = "Result No Set";

	public QaObservationSDK(Context context, AroContextualSdk aroContextualSdk) {
		super();

		this.mContext =context;
		mAroContextualSdk = aroContextualSdk;
		qaHelper = new ToolHelper(mContext);
		toolLogger = new ToolLogger(mContext);

	}	


	String testObservationSDK(String sdkMethod,String[] testCaseData){


		if(sdkMethod.equalsIgnoreCase("addObservation")){
			toolLogger.qaLogs("observation.uploadobservations");
			return uploadObservation_test(testCaseData);

		}

		if (sdkMethod.equalsIgnoreCase("createSet")){
			return createObservationSet(testCaseData);

		}

		if (sdkMethod.equalsIgnoreCase("buildUserHistory")){
			return createUserHisotory(testCaseData);

		}		
		
		toolLogger.qaLogs("Observations. Method " + sdkMethod + "NOT FOUND");	
		return "Observations. Method " + sdkMethod + " NOT FOUND";
	}	




	String uploadObservation_test(String[] testCaseData){

		JSONArray observationJason;

		SecurePreferences securePreferences = new SecurePreferences(mContext);
		if (UserAccount.isSaved(securePreferences)) {
			UserAccount userAccount = UserAccount.getUserAccount(securePreferences);            
			//  BaseApi.setUserToken(userAccount.getAccessToken());
			Log.d(TAG, "userAccount.getAccessToken() =  " + userAccount.getAccessToken());            
		}

		try {
			Log.d(TAG,"Test data : " + testCaseData[1]);


			observationJason = new JSONArray(testCaseData[1]);
			//observation = new LocationObservation(observationJason);			

			Log.d(TAG,"Local Db Size (Before) : " + ObservationsDb.sizeOfDb(mContext));

			//observation = ObservationFactory.makeObservationFromJsonString(testCaseData[1]);

			mAroContextualSdk.uploadObservations(observationJason, webResultHandler);

			//ObservationsDb.addObservation(mContext, observation);

		} catch (JSONException e) {
			Log.d(TAG,"uploadObservation_test JSONException" + e);
		} catch (Exception ex) {
			Log.d(TAG, "uploadObservation_test Exception " + ex);
		}

		Log.d(TAG,"Local Db Size (After) : " + ObservationsDb.sizeOfDb(mContext));

		return mResult;
	}


	String createObservationSet(String[] testCaseData){



		String latitude = testCaseData[1];
		String longitude = testCaseData[2];
		String timeObserved = testCaseData[3];
		int intervalMinutes = Integer.valueOf(testCaseData[4]);
		int numberOfObservations = Integer.valueOf(testCaseData[5]);

		createObservation(latitude,longitude, timeObserved, intervalMinutes, numberOfObservations);

		return mResult;

	}




	String createUserHisotory(String[] testCaseData){

		int interval = Integer.valueOf(testCaseData[1]);
		int numObsrv = Integer.valueOf(testCaseData[2]);
		int hrOnDay = Integer.valueOf(testCaseData[3]);
		int days = Integer.valueOf(testCaseData[4]);;
		int daysBack = days + 1;
		String observartionTime;
		long longObservartionTime;
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss",Locale.ENGLISH);
		Calendar calendar = Calendar.getInstance();
		
		for (int i=0; i<days;i++){
			
			calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, - daysBack);
			calendar.set(Calendar.HOUR_OF_DAY, hrOnDay);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			
			daysBack--;

			//CenturyLink Field
			observartionTime = sdf.format(calendar.getTime());
			longObservartionTime = createObservation("47.59561","-122.33091", observartionTime, interval, numObsrv);
			
			//Safeco Field
			observartionTime =  sdf.format(longObservartionTime + 1800000);
			longObservartionTime =createObservation("47.59101","-122.33357", observartionTime, interval, numObsrv);
			
			//Pyramid Restaurant
			observartionTime = sdf.format(longObservartionTime + 1800000);
			longObservartionTime =createObservation("47.59225","-122.33471", observartionTime, interval, numObsrv);
			
			//SBUX HQ
			observartionTime = sdf.format(longObservartionTime + 1800000);
			longObservartionTime =createObservation("47.58085","-122.33586", observartionTime, interval, numObsrv);
			
			//SEA-TAC
			observartionTime = sdf.format(longObservartionTime + 3600000);
			longObservartionTime = createObservation("47.44431","-122.30276", observartionTime, interval, numObsrv);
		
			observartionTime = sdf.format(longObservartionTime + 7200000);
		}

		return "OK";
	}

		

	Long createObservation(String latitude , String longitude, String timeObserved, int intervalMinutes, int numberOfObservations){

		String geo;
		String theObservation;
		Random random = new Random();
		int latlongChange = 0;
		String strLat;
		String strLon;
		StringBuffer sbLat = new StringBuffer();
		StringBuffer sbLon = new StringBuffer();

		//		Observation observation;
		JSONObject observationJson;
		JSONArray observationArray = new JSONArray();
		toolLogger.qaLogs("Obsrv Date: " + timeObserved);
		Long timeObservedMS = dateStringToMilliseconds(timeObserved);

		for (int i = 0; i < numberOfObservations; i++){	
			observationArray = new JSONArray();
			//add random number to location.
			latlongChange = random.nextInt(9999 - 0 + 1) + 0;
			

			try {

				//add random number at the end of Latitude								
				sbLat.append(latitude);
				sbLat.replace(latitude.indexOf(".")+5, latitude.length()-1,String.valueOf(latlongChange));
				strLat = sbLat.toString();
				//add random number at the end of Long
				sbLon.append(longitude);
				sbLon.replace(longitude.indexOf(".")+5, longitude.length()-1,String.valueOf(latlongChange));				
				strLon = sbLon.toString();

				geo = "geo:" + strLat + "," +strLon + ",0.0;u=10.0;tz=America%2FLos_Angeles;source=network;test=jngeee";
				//observationArray.put("[{\"type\":\"Location\",\"location\":\"geo:47.59507995257869,-122.33173370361328,0.0;u=10.0;tz=America%2FLos_Angeles;source=network;test=jngeee\",\"accuracyMeters\":10,\"bearingDegrees\":89.2,\"speedMetersPerSecond\":0.1,\"timeObserved\":\"2014-05-05T13:33:27.997Z\",\"timeZone\":\"America/Los_Angeles\"},{\"type\":\"WiFi\",\"scanResults\":[{\"BSSID\":\"f4e1111126a651181a042470773c8ba7b88f95c7\",\"SSID\":\"ecbf034d05660c25ca38a328d10a55c8ae916e0c\",\"capabilities\":\"[WPA-PSK-TKIP][ESS]\",\"frequency\":2412,\"level\":-69}],\"timeObserved\":\"2014-05-05T13:33:27.997Z\",\"timeZone\":\"America/Los_Angeles\"}]");

				theObservation = "{\"type\":\"Location\",\"accuracyMeters\":10,\"bearingDegrees\":89.3,\"speedMetersPerSecond\":0.1,\"timeZone\":\"America/Los_Angeles\",\"provider\":\"gps\"}";						

				observationJson = new JSONObject(theObservation);
				observationJson.put("location",geo);
				observationJson.put("timeObserved", timeObservedMS);	
				toolLogger.qaLogs("Obsrv Json \r\n" + observationJson.toString());

				//observation = ObservationFactory.makeObservationFromJsonString(observationJson.toString());
				//ObservationsDb.addObservation(mContext, observation);

				observationArray.put(observationJson);
				mAroContextualSdk.uploadObservations(observationArray, webResultHandler);
				qaHelper.waitForWebListenerResponse(10);

				//Add x time to next observation.
				timeObservedMS = timeObservedMS + ( intervalMinutes * DateUtils.MINUTE_IN_MILLIS);
				sbLat.setLength(0);
				sbLon.setLength(0);

			} catch (Exception j) {
				j.printStackTrace();
				toolLogger.qaLogs("createObservation Exception" +j.toString());
				return -1L;
			}	

		}

		
		return  timeObservedMS;
		
	}

	private Long dateStringToMilliseconds(String timeObserved){

		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss",Locale.ENGLISH);
		Date date;

		try {
			date = sdf.parse(timeObserved);
			return date.getTime();

		} catch (ParseException e1) {
			e1.printStackTrace();
			toolLogger.qaLogs("latlongChange = " +e1);
		}

		return 0L;
	}
	
	
	
	WebResultHandler<Void> webResultHandler = new WebResultHandler<Void>(Looper.getMainLooper()) {

		@Override
		protected void onWebFailure(WebException arg0) {
			mResult="webResultHandler WebException " + arg0;
			Log.e(TAG, "WebFailure. WebException = " + arg0.getMessage());
			ToolHelper.mWait=false;		
		}


		@Override
		protected void onWebSuccess(Void v) {
			Log.i(TAG,"ObseravationUpload WebResultHandler onWebSuccess.");
			mResult="WebSuccess";
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

	OnNewObservationListener onNewObservationListener = new OnNewObservationListener() {

		@Override
		public void onObservationUnavailable(SensorAsyncCollector arg0, boolean arg1) {
			Log.e(TAG, "onObservationUnavailable. " + arg0 + "boolean = " + arg1);
			ToolHelper.mWait=false;	
		}

		@Override
		public void onObservationFinished(SensorAsyncCollector arg0) {
			Log.e(TAG, "onObservationFinished. " + arg0);
			ToolHelper.mWait=false;	
		}

		@Override
		public void onNewObservation(SensorAsyncCollector arg0, Observation arg1) {
			Log.e(TAG, "onNewObservation. " + arg0 + "Observation = " + arg1);
			ToolHelper.mWait=false;	
		}
	};

	OnObservationUploadingFinished uploadListener = new OnObservationUploadingFinished() {

		@Override
		public void onUploadingFinished(boolean arg0) {
			Log.i(TAG,"onUploadingFinished = " + arg0);
			ToolHelper.mWait=false;	
		}
	};

}
