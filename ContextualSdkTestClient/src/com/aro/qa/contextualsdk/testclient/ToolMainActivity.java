package com.aro.qa.contextualsdk.testclient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.aro.qa.contextualsdk.testclient.CommunicationSocket.OnAsyncResult;
import com.aro.qa.contextualsdktesttool.R;


public class ToolMainActivity extends Activity {

	final static String TAG = "QA_test";
	Button btnRunMethod;
	TextView txtLogs;
	CommunicationSocket clientSocket;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tool_main);	
		btnRunMethod = (Button) findViewById(R.id.btnRunMethod);

		//Automatically start listening for desktop on Socket
		CommunicationSocket clientSocket = new CommunicationSocket(this);
		clientSocket.execute("one");	


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tool_main, menu);
		return true;
	}


	/***
	 * Implements commandButton btnRunMethod onClick event	
	 */
	void runMethodButtonListener(){
		btnRunMethod.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//			qaHelper.qaLogs("FROM CLICK BUTTON");
				//			ClientSocket clientSocket = new ClientSocket();
				//			clientSocket.execute("one");	
			}
		});
	}



	OnAsyncResult asynResult = new OnAsyncResult() {

		@Override
		public void onResultSuccess(final int resultCode, final String message) {

			runOnUiThread(new Runnable() {
				public void run() {
					//textView.setText("Code : " + resultCode + "\nMessage : " + message);
				}
			});
		}

		@Override
		public void onResultFail(final int resultCode, final String errorMessage) {
			runOnUiThread(new Runnable() {
				public void run() {
					//textView.setText("Code : " + resultCode + "\nMessage : " + errorMessage);
				}
			});

		}
	};


	void qaLogs(String message, String debugLevel){
		try{
			if (debugLevel == null){
				Log.i(TAG,message);
			}else if (debugLevel.indexOf('e') != -1) {
				Log.e(TAG,message);			
			}else if (debugLevel.indexOf('d') != -1) {
				Log.d(TAG,message);			
			}

			txtLogs=(TextView)findViewById(R.id.txtLogs);
			txtLogs.append("\n\r" + message);			

			if (txtLogs.getLineCount() > 200){
				txtLogs.setText("Logs:");
			}
		}catch(Exception e){
			Log.e(TAG,"qaLogs Exception " + e);
		}

	}	

}
