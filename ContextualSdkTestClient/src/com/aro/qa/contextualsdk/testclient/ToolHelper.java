package com.aro.qa.contextualsdk.testclient;


import com.aro.qa.contextualsdktesttool.R;

import android.app.Activity;
import android.content.Context;

import android.util.Log;
import android.widget.TextView;

public class ToolHelper{

	Activity activity;
	private static final String TAG = "QA_test";
	public static boolean mWait=true;
	static TextView txtLogs;	

	public ToolHelper(Context context) {	
		activity = (Activity)context;	
		txtLogs = (TextView)activity.findViewById(R.id.txtLogs);
	}
	
	
	/***
	 * 	
	 * @param waitInSeconds The time to wait for the WebListener.
	 */
	void waitForWebListenerResponse(int waitInSeconds){
		int i=0;
		Log.e(TAG,"mWait="+mWait);
		while (mWait == true){
			Log.e(TAG,".");
			try {
				Thread.sleep(1000);
				if (i++ > waitInSeconds){
					Log.e(TAG,"SDK WebResponse DID NOT came back after" + waitInSeconds + " sec");
					break;

				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mWait=true;
	}

}
