package com.aro.qa.contextualsdk.testtool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SdkMethodsCaller {

	private static int PORT = 4444;
	private static String HOST = "127.0.0.1";
	private static Socket socket;

	
/***
 * Open a client socket to connect to mobile device via socket (Device is Server, Computer is Client).
 * Method returns the data sent from mobile device.
 * 
 * 
 * @param dataToDevice  An string separated by colons containing the data to be sent to mobile device.
 * @return
 */
	String connectToMobile(String dataToDevice){
		String dataFromDevice;
		
        try
        {         
            InetAddress address = InetAddress.getByName(HOST);
            socket = new Socket(address, PORT);
            
            //Send the message to mobile device (the server socket).
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
 
            System.out.println("Data To Device:" + dataToDevice);
            String sendMessage = dataToDevice + "\n";
            bw.write(sendMessage);
            bw.flush();
 
            //Get returned message from Mobile device(the server socket).
            dataFromDevice=null;
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            dataFromDevice = br.readLine();
            
            if (dataFromDevice.length() > 350){
               System.out.println("Data From Device:" + dataFromDevice.substring(0,349) + " ... check testReport.xml to see full response.");
            }else{
            	   System.out.println("Data From Device:" + dataFromDevice);
            }
            return dataFromDevice;
            
        }
        catch (Exception exception) 
        {
        	System.out.println("connectToServer Exception " + exception + ". Try adb forward tcp:4444 tcp:4444");
        	return null;

        }
        finally
        {
            //Closing socket
            try
            {
                socket.close();
            }
            catch(Exception e)
            {
            	System.out.println("Can't close socket. Exception " + e);
            }
        }
    }			

}
