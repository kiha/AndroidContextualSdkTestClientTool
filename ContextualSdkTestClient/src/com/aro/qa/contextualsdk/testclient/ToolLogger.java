package com.aro.qa.contextualsdk.testclient;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.aro.android.contextsdk.AroContextualSdk;
import com.aro.android.contextsdk.AroContextualSdk.CurrentLocationListener;
import com.aro.android.contextsdk.apiobjects.GeoUri;
import com.aro.qa.contextualsdktesttool.R;

	
	public class ToolLogger extends AsyncTask<String, Void, Void> {
		String applicationId = "fee8baf337283e5ba320a836c16dd80a";
		//String applicationId = "@#$%^&";
		String serverURL = "https://lifestream-dev0.aro.com";
		
		
		Activity activity;
		ToolHelper qaHelper;
		private static final String TAG = "QA_test";
		public static boolean mWait=true;
		static TextView txtLogs;	
		Context mContext;
		ToolLogger toolLogger;
		static String result="result on ToolLogger";

		public ToolLogger(Context context) {	
			mContext=context;
			activity = (Activity)context;	
			txtLogs = (TextView)activity.findViewById(R.id.txtLogs);
			qaHelper = new ToolHelper(context);
			
		}


		@Override
		protected Void doInBackground(final String... params) {

			activity.runOnUiThread(new Runnable() {

				String message = params[0];
				@Override
				public void run() {
					Log.d(TAG,"FROM runOnUI");
					txtLogs.append("___>>>   Testing");
				
					if (message.equalsIgnoreCase("getCurrentLocation")){
						getCurrentLocation();
					}else{
					
						qaLogs(message);
					}
				}
			});		
			return null;
		}


		void qaLogs(String message){		
			qaLogs(message, null);		
		}


		void qaLogs(String message, String debugLevel){
			try{
				if (debugLevel == null){
					Log.i(TAG,message);
				}else if (debugLevel.indexOf('e') != -1) {
					Log.e(TAG,message);			
				}else if (debugLevel.indexOf('d') != -1) {
					Log.d(TAG,message);			
				}


				txtLogs.append("\n\r" + message);			

				if (txtLogs.getLineCount() > 200){
					txtLogs.setText("Logs:");
				}
				
			}catch(Exception e){
				//Log.e(TAG,"qaLogs Exception " + e);
			}

		}

		void qaLogsClean(){

			TextView txtView = (TextView)activity.findViewById(R.id.txtLogs);
			txtView.setText("Logs");

		}

	
		void getCurrentLocation(){
			qaLogs("getCurrentLocation");
			AroContextualSdk aroContextualSdk = new AroContextualSdk(mContext, applicationId, serverURL);
			try {
				aroContextualSdk.getCurrentLocation(currentLocationListener);

				qaHelper.waitForWebListenerResponse(15);
				
			}catch(Exception exp){
				qaLogs("contextSdk.getCurrentLocation_test Error "+exp);
				
			}
		}
	
	
		CurrentLocationListener currentLocationListener = new CurrentLocationListener() {
			
			@Override
			public void onCurrentLocation(GeoUri arg0) {
				qaLogs("onMovement Listener");
				qaLogs(arg0.toStringComplete(mContext));
				result=arg0 + ":::" + arg0.toStringComplete(mContext);
				
				ToolHelper.mWait=false;			
			}
		};
	
}
