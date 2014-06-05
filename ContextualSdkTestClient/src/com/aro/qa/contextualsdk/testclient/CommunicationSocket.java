package com.aro.qa.contextualsdk.testclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class CommunicationSocket extends AsyncTask<String, Void,Void> {

	Context mContext;
	ToolHelper qaHelper;
	ToolLogger toolLogger;
	SdkMethods sdkMethods;
	String mDataToServer;
	private static String TAG="QA_test";
	private static int PORT=4444;
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static InputStreamReader inputStreamReader;
	private static BufferedReader bufferedReader;
	private String mDataFromServer;
	OnAsyncResult onAsyncResult;
	
	

	

	public CommunicationSocket(Context context) {
		super();
		mContext = context;
		qaHelper = new ToolHelper(mContext);
		toolLogger = new ToolLogger(mContext);
		sdkMethods = new SdkMethods(mContext);
		
	}
	

	@Override
	protected void onPostExecute(Void result) {
		toolLogger.qaLogs("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

	}


	@Override
	protected Void doInBackground(String... params) {

		try {

			serverSocket = new ServerSocket(PORT);  //Server socket

		} catch (IOException e) {
			Log.d(TAG,"COULD NOT LISTEN TO PORT:" + PORT + "   <==============" + e);
			Log.d(TAG,"Try	lsof -i :4444 then  kill -9 PID #    or   adb forward tcp:4444 tcp:4444");	
		}
		toolLogger.qaLogs("Server started on port:" + PORT);		

		connectToDesktop();

		if (mDataFromServer != null) {
			onAsyncResult.onResultSuccess(0, mDataFromServer);
		} else {
			onAsyncResult.onResultFail(1, "No Data From Server");
		}
		//		}
		return null;
	}	


	public void setOnResultListener(OnAsyncResult onAsyncResult) {
		if (onAsyncResult != null) {
			this.onAsyncResult = onAsyncResult;
		}
	}	


	/***
	 * Open a Socket to Send/Receive data from server.	
	 */

	void connectToDesktop(){
		String sdkResult;
		
		while (true) {
			try {

				//WAIT for client socket (desktop) to request connection.
				clientSocket = serverSocket.accept();   				          
				
				//Need to create a new Helper here for new socket thread.
				toolLogger = new ToolLogger(mContext);
				//get data from desktop
				inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
				bufferedReader = new BufferedReader(inputStreamReader); 
				mDataFromServer = bufferedReader.readLine();
				toolLogger.qaLogs("Data From Server:" + mDataFromServer,"d");
				// CALL the Method to Be Tested.
				sdkResult = sdkMethods.callMethodTest(mDataFromServer);

				OutputStream os = clientSocket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				toolLogger.qaLogs("Data to Server:" + sdkResult);

				bw.write(sdkResult);
				bw.flush();     	

				inputStreamReader.close();
				clientSocket.close();

			} catch (IOException ex) {
				Log.e(TAG,"Error on socket communication. " + ex);
			}
		}

	}	

	public interface OnAsyncResult {

		public abstract void onResultSuccess(int resultCode, String message);

		public abstract void onResultFail(int resultCode, String errorMessage);
	}
}	


/***
 * Open a Socket to Send/Receive data from server.	
 */
/*	
	void connectToServer(){

        try
        {
            InetAddress address = InetAddress.getByName(HOST);
            socket = new Socket(address, PORT);

            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            String toServer = "FROM CLIENT";
            Log.d(TAG,"toServer");
            String sendMessage = toServer + "\n";
            bw.write(sendMessage);
            bw.flush();

            //Get the return message from the server
            mDataFromServer=null;
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            mDataFromServer = br.readLine();
            Log.d(TAG,"Data from the server : " + mDataFromServer);

        }
        catch (Exception exception) 
        {
        	Log.d(TAG,"Exception " + exception);

        }
        finally
        {
            //Closing the socket
            try
            {
                socket.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }		

	public interface OnAsyncResult {

		public abstract void onResultSuccess(int resultCode, String message);

		public abstract void onResultFail(int resultCode, String errorMessage);
	}


}
 */