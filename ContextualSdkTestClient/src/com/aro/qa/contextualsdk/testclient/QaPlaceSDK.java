package com.aro.qa.contextualsdk.testclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.media.MediaPlayer.TrackInfo;
import android.os.Looper;
import android.os.Trace;

import com.aro.android.contextsdk.AroContextualSdk;
import com.aro.android.contextsdk.PlaceRequest;
import com.aro.android.contextsdk.apiobjects.Address;
import com.aro.android.contextsdk.apiobjects.Category;
import com.aro.android.contextsdk.apiobjects.GeoUri;
import com.aro.android.contextsdk.apiobjects.Person;
import com.aro.android.contextsdk.apiobjects.Place;

import com.aro.android.contextsdk.http.WebException;
import com.aro.android.contextsdk.http.WebResultHandler;


public class QaPlaceSDK {

	final String LATITUDE = "latitude";
	final String LONGITUDE = "longitude";
	final String RADIUS = "radius";
	final String LIMIT = "limit";
	final String FREQUENT = "frequent";
	final String ALL_FREQUENT = "All_frequent";
	final String FAVORITE = "favorite";
	final String ALL_FAVORITE = "All_favorite";
	final String RECENT = "recent";
	final static String TAG = "QA_test";
	String mResult="Result Not Set on Device";	

	Context mContext;
	AroContextualSdk mAroContextualSdk;
	ToolHelper qaHelper;
	ToolLogger toolLogger;
	
	List<Place> mListOfPlaces;

	

	public QaPlaceSDK(Context context, AroContextualSdk contextSdk) {
		super();

		this.mContext = context;
		this.mAroContextualSdk = contextSdk;
		qaHelper = new ToolHelper(mContext);
		toolLogger = new ToolLogger(mContext);
	}		



	String testPlaceSDK(String sdkMethod,String[] testCaseData){
		
		if(sdkMethod.equalsIgnoreCase("placeRequest") ||
				sdkMethod.equalsIgnoreCase(FREQUENT) 	 ||
				sdkMethod.equalsIgnoreCase(RECENT) 	 ||
				sdkMethod.equalsIgnoreCase(FAVORITE)) {

			return placeRequest_test(testCaseData);	
		}

		if(sdkMethod.equalsIgnoreCase("getByIds")){

			return getByIds_test(testCaseData);	
		}
		
		if(sdkMethod.equalsIgnoreCase("savePlace")){

			return savePlace(testCaseData);	
		}
		
		if(sdkMethod.equalsIgnoreCase("getPlace")){

			return getPlace_test(testCaseData);	
		}
		
		if(sdkMethod.equalsIgnoreCase("getPlaces")){

			return getPlaces_test(testCaseData);	
		}	
		
		
		toolLogger.qaLogs("Place. Method " + sdkMethod + " NOT FOUND");	
		return "Method " + sdkMethod + " NOT FOUND";
	}

	
	String getPlace_test(String[] testCaseData){
		String id;
		try {
	
			//if auto call places and get id's from current user places.
			if (testCaseData[1].equalsIgnoreCase("id#auto")){
				
				String[] param = {"latitude#47.5956569","longitude#-122.3309575","limit#5"};
				placeRequest_test(param);
				
				id = mListOfPlaces.get(0).getId();
			}else{
				
				id = testCaseData[1];
			}
				
			toolLogger.qaLogs("Place id = " + id);
			mAroContextualSdk.getPlace(id, webResultHandlerPlace);			
			qaHelper.waitForWebListenerResponse(10);
			
		}catch(Exception e){
			e.printStackTrace();
			toolLogger.qaLogs("mAroContextualSdk.getPlace Exception " + e);
			return e.toString();
		}
		
		return mResult;
	}
	
	
	String getPlaces_test(String[] testCaseData){
		
		List<String> ids = new ArrayList<String>();
		
		
		//if auto call places and get id's from current user places.
		if (testCaseData[1].equalsIgnoreCase("id#auto")){
			
			String[] param = {"latitude#47.5956569","longitude#-122.3309575","limit#5"};
			placeRequest_test(param);
			
			for (Place place : mListOfPlaces){
				
				ids.add(place.getId());
			}
			
			toolLogger.qaLogs("# places found (auto)= " + mListOfPlaces.size());
		}else{
			//Iterate through test case parameter to set place configuration
			for (int i=1; i < testCaseData.length; i++){
				ids.add(testCaseData[i]);
			}
		}
		mAroContextualSdk.getPlaces(ids, webResultHandlerMap);
		qaHelper.waitForWebListenerResponse(10);		
		return mResult;
	}
	
	String getByIds_test(String[] testCaseData){

		Collection<String> ids = new ArrayList<String>();
		try {
			//if auto call places and get id's from current user places.
			if (testCaseData[1].equalsIgnoreCase("id#auto")){

				String[] param = {"latitude#47.5956569","longitude#-122.3309575","limit#5"};
				placeRequest_test(param);

				for (Place place : mListOfPlaces){

					ids.add(place.getId());
				}

				toolLogger.qaLogs("# places found (auto)= " + mListOfPlaces.size());
			}else{
				//Iterate through test case parameter to set place configuration
				for (int i=1; i < testCaseData.length; i++){
					ids.add(testCaseData[i]);
				}
			}		

			PlaceRequest.Builder placeRequestBuilder = mAroContextualSdk.getPlaceRequestBuilder();
			PlaceRequest placeRequest = placeRequestBuilder.build();
			toolLogger.qaLogs("ids =" + ids.size());
			placeRequest.getByIds(ids, webResultHandlerMap);

			qaHelper.waitForWebListenerResponse(15);

		}catch (Exception e){

			toolLogger.qaLogs("placegetByIds_test Exception." + e);
			return e.toString();
		}

		return mResult;
	}	

	
	String placeRequest_test(String[] testCaseData){

		String parameter="";
		String value="";
		double latitude=0;
		double longitude=0;
		int radius;
		int limit;
		PlaceRequest.Builder placeRequestBuilder = null;

		try {
			placeRequestBuilder = mAroContextualSdk.getPlaceRequestBuilder();
			toolLogger.qaLogs("testCaseData length= " + testCaseData.length);
			//Iterate through test case parameter to set place configuration
			for (String theParameter : testCaseData){
				toolLogger.qaLogs("theParameter = " + theParameter);
				if (theParameter.contains("#")){

					parameter = theParameter.substring(0,theParameter.indexOf("#"));
					value = theParameter.substring(theParameter.indexOf("#")+1,theParameter.length());

					toolLogger.qaLogs("Parameter = " + parameter + " / Value = " + value);

				}			

				if (parameter.equalsIgnoreCase(LATITUDE)){
					latitude = Double.valueOf(value);	
					toolLogger.qaLogs("latitude = " + latitude);
				}

				if (parameter.equalsIgnoreCase(LONGITUDE)){

					longitude = Double.valueOf(value);	
					toolLogger.qaLogs("longitude = " + longitude);
					placeRequestBuilder.setLocation(latitude,longitude);
				}		

				if (parameter.equalsIgnoreCase(RADIUS)){	
					radius = Integer.valueOf(value);				
					placeRequestBuilder.setRadius(radius);
					toolLogger.qaLogs("radius = " + radius);
				}

				if (parameter.equalsIgnoreCase(LIMIT)){	
					limit = Integer.valueOf(value);				
					placeRequestBuilder.setLimit(limit);
					toolLogger.qaLogs("limit = " + limit);
				}	

				if (parameter.equalsIgnoreCase(ALL_FREQUENT)){	

					if (value.equalsIgnoreCase("true")){
						toolLogger.qaLogs("placeRequestBuilder.setAllFrequent()");
						placeRequestBuilder.setAllFrequent();
					}
				}				

				if (parameter.equalsIgnoreCase(FREQUENT)){	

					if (value.equalsIgnoreCase("true")){
						toolLogger.qaLogs("placeRequestBuilder.setFrequent()");
						placeRequestBuilder.setFrequent();
					}
				}	

				if (parameter.equalsIgnoreCase(ALL_FAVORITE)){	

					if (value.equalsIgnoreCase("true")){
						toolLogger.qaLogs("placeRequestBuilder.setAllFavorites()");
						placeRequestBuilder.setAllFavorites();
					}
				}
				
				if (parameter.equalsIgnoreCase(FAVORITE)){	
					if (value.equalsIgnoreCase("true")){
						toolLogger.qaLogs("placeRequestBuilder.setFavorite()");
						placeRequestBuilder.setFavorite();
					}
				}	
				
				if (parameter.equalsIgnoreCase(RECENT)){	
					if (value.equalsIgnoreCase("true")){
						toolLogger.qaLogs("placeRequestBuilder.setRecent()");
						placeRequestBuilder.setRecent();
					}
				}	

			}

			PlaceRequest placeRequest = placeRequestBuilder.build();

			placeRequest.execute(webResultHanderPlaceList);

			qaHelper.waitForWebListenerResponse(15);

		}catch (Exception e){
			toolLogger.qaLogs("placeRequest_test Exception." + e);
			return e.toString();
		}

		return mResult;
	}


	
	String savePlace(String[] testCaseData){
		String parameter="";
		String value="";
		boolean favorite=false;
		Place thePlace=null;
		JSONObject placeJson;


		try{
		
			//If new user, first crate place
			//GeoUri location = new GeoUri(47.518181, -122.318181, GeoUri.ACCURACY_UNKNOWN);
			//Place myPlace = new Place.Builder("QA Place", location)
			//                         .build();
		
			for (String theParameter : testCaseData){
				toolLogger.qaLogs("theParameter" + theParameter);
				if (theParameter.contains("#")){

					parameter = theParameter.substring(0,theParameter.indexOf("#"));
					value = theParameter.substring(theParameter.indexOf("#")+1,theParameter.length());

					toolLogger.qaLogs("Parameter = " + parameter + " / Value = " + value);
					
				}			

				if (parameter.equalsIgnoreCase("place")){
					placeJson = new JSONObject(value);
					thePlace = new Place(placeJson);
				}
				
				if (parameter.equalsIgnoreCase("Favorite")){
					toolLogger.qaLogs("Favorite = " + thePlace.isFavorite());
					favorite = Boolean.valueOf(value);	
					thePlace.setAsFavorite(favorite);	
				}

			}
			

		if (thePlace == null){
			GeoUri location = new GeoUri(47.51818, -122.31818, GeoUri.ACCURACY_UNKNOWN);
			thePlace = new Place.Builder("Auto Generated QA Place", location)
			                         .build();
			
		}

		toolLogger.qaLogs("Place Id = " + thePlace.getId() + "\r\n Favorite = " + thePlace.isFavorite());	
		mAroContextualSdk.savePlace(thePlace, webResultHandlerPlace);
		qaHelper.waitForWebListenerResponse(10);
		
		
		} catch (JSONException e) {
			e.printStackTrace();
			toolLogger.qaLogs("savePlace Exception : " + e);

		} catch (Exception ex) {
			ex.printStackTrace();
			toolLogger.qaLogs("savePlace Exception : " + ex);
		}
		
		
		return mResult;
		
	}
	
	
	
	 WebResultHandler<Void> webResultHandler = new WebResultHandler<Void>() {

		@Override
		protected void onWebSuccess(Void paramResult) {
			toolLogger.qaLogs("Place onWebSuccess");
			mResult="Place onWebSuccess";
			ToolHelper.mWait=false;					
		}		 		 
		 
		@Override
		protected void onWebFailure(WebException paramWebException) {
			toolLogger.qaLogs("Place onWebFailure<Place>");
			mResult=paramWebException.toString();
			ToolHelper.mWait=false;	
			
		}
	};
	
	 WebResultHandler<Place> webResultHandlerPlace = new WebResultHandler<Place>() {

			@Override
			protected void onWebSuccess(Place place) {
				toolLogger.qaLogs("Place onWebSuccess <Place>");	
				
				showPlaceObjectFields(place);
				mResult = place.getJsonObject().toString();
				ToolHelper.mWait=false;					
			}		 		 
		 
		@Override
		protected void onWebFailure(WebException paramWebException) {
			toolLogger.qaLogs("Place onWebFailure <Place>" + paramWebException.toString());
			mResult=paramWebException.toString();
			ToolHelper.mWait=false;	
			
		}
	};
	
	WebResultHandler<Map<String,Place>> webResultHandlerMap = new WebResultHandler<Map<String,Place>>(Looper.getMainLooper()){
		@Override
		protected void onWebSuccess(Map<String, Place> paramResult) {
			toolLogger.qaLogs("WebResultHandler<Map<String,Place>> onWebSuccess");

			JSONObject jsonPlace;
			StringBuilder placeString = new StringBuilder( "{  \"places\": [ " );
			
			for (Map.Entry<String, Place> categoMap : paramResult.entrySet()){
				if (categoMap.getValue() == null) continue;
				//Get all places Json to send it back to desktop
				jsonPlace = categoMap.getValue().getJsonObject();
				placeString.append(jsonPlace.toString());
				placeString.append(",");
								
				toolLogger.qaLogs("Key:" + categoMap.getKey());
				showPlaceObjectFields(categoMap.getValue());
				
			}
			
			placeString.deleteCharAt(placeString.length()-1);
			placeString.append(" ] }");
			mResult = placeString.toString();
			ToolHelper.mWait=false;	

		}
		
		@Override
		protected void onWebFailure(WebException paramWebException) {
			toolLogger.qaLogs("Place WebException <Map<String,Place>> = " + paramWebException);
			mResult=paramWebException.toString();
			ToolHelper.mWait=false;	
		}

		
	};	
	
	WebResultHandler<List<Place>> webResultHanderPlaceList = new WebResultHandler<List<Place>>() {

		@Override
		protected void onWebFailure(WebException paramWebException) {
			toolLogger.qaLogs("Place WebException<List<Place>> = " + paramWebException);
			mResult = "onWebFailure:" + paramWebException;
			ToolHelper.mWait=false;	

		}

		@Override
		protected void onWebSuccess(List<Place> paramResult) {
			toolLogger.qaLogs("onWebSuccess List<Place>>");
			mListOfPlaces=null;
			mListOfPlaces = (List<Place>)paramResult;
			int i=1;
			JSONObject jsonPlace;
			StringBuilder placeString = new StringBuilder( "{  \"places\": [ " );

			toolLogger.qaLogs("Place total = " + paramResult.size());

			for (Place thePlace :paramResult){

				//Get all places Json to send it back to desktop
				jsonPlace = thePlace.getJsonObject();
				placeString.append(jsonPlace.toString());
				placeString.append(",");

				//Display Place data on Device logs
				toolLogger.qaLogs("===   " + i++  + ". "+ thePlace.getName());
				showPlaceObjectFields(thePlace);
			}
			placeString.deleteCharAt(placeString.length()-1);
			placeString.append(" ] }");
			mResult = placeString.toString();
	
			ToolHelper.mWait=false;	

		}
	};



	/***
	 * 
	 * Method to display Place object fields on logs AND To store all sdk results to be compared.
	 * 
	 * @param place
	 */

	void showPlaceObjectFields(Place place){

		try{	
			if (place.getRoles() != null){
				for (String role : place.getRoles()){

					toolLogger.qaLogs("\r\nRole = " + role);

				}
			}

			toolLogger.qaLogs("\r\ngetCreatedTime =	" + place.getCreatedTime() + "\r\n" +
					"getId =	" + place.getId() + "\r\n" +
					"getName =	" + place.getName() + "\r\n" +
					"getAlias =	" + place.getAlias() + "\r\n" +
					"getLocation =	" + place.getLocation() + "\r\n" +
					"getLastVisitedTime=" + place.getLastVisitedTime() + "\r\n" +				
					"getPhoneNumber =	" + place.getPhoneNumber() + "\r\n" +
					"getRadiusInMeters=" + place.getRadiusInMeters() + "\r\n" +
					"getRating =	" + place.getRating() + "\r\n" +
					"getRecentMinutes =	" + place.getRecentMinutes() + "\r\n" +
					"getRecentStays =	" + place.getRecentStays() + "\r\n" +
					"getTotalMinutes =	" + place.getTotalMinutes() + "\r\n" +
					"getTotalStays =	" + place.getTotalStays() + "\r\n" +
					"isFavorite =	" + place.isFavorite() + "\r\n" +
					"isFrequent =	" + place.isFrequent() + "\r\n" +
					"isPublic =	" + place.isPublic() + "\r\n" +		
					"getBusinessHours=" + place.getBusinessHours() + "\r\n" ,"d");


			if (place.getAddress() != null){

				Address placeAddress = place.getAddress();
				toolLogger.qaLogs("ADDRESS:" + "\r\n\t"+
						"Street    :"+ placeAddress.getStreet() + "\r\n\t"+
						"City      :"+placeAddress.getCity() + "\r\n\t"+
						"StateProvince:"+placeAddress.getStateProvince() + "\r\n\t" +
						"CityState :"+ placeAddress.getCityState() + "\r\n\t"+
						"PostalCode:"+ placeAddress.getPostalCode() + "\r\n\t"+
						"Country   :"+ placeAddress.getCountry());
			}else{
				toolLogger.qaLogs("Place ADDRESS object = NULL" ,"e");
			}
		}catch(Exception e){
			toolLogger.qaLogs("showPlaceObjectFields Exception."+ e);
			mResult = "showPlaceObjectFields Exception";
		}
	}	
}
