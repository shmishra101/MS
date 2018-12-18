package com.mywaypointv2;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;

public class GoogleDirectionMap 
{  
	// This method is used to connect with directions api and retrieve the directions response 
	String key="";
	public  DirectionsResult getApiresponse(String origin,String destination) throws ApiException, InterruptedException, IOException
	{
		GeoApiContext context=new GeoApiContext.Builder().apiKey(key).build();
	    DirectionsApiRequest directionapi=DirectionsApi.getDirections(context, origin, destination);
	    DirectionsResult result=directionapi.await();
	    return result;
	}
	// This method is used to convert the response from directions api in JSON format
	public JSONObject getResponse(DirectionsResult result)
	{   JSONObject json;
		Gson gson =new Gson();
		json=new JSONObject(gson.toJson(result));
		return json;
    }
	// This method is used to get the location of each steps of the route
  public JSONObject getLocation(DirectionsResult result) 
     {   
	DirectionsRoute route[]=result.routes;
    DirectionsLeg leg[]=route[0].legs;
    LatLng startlo=leg[0].startLocation;
    LatLng endlo=leg[0].endLocation;
    DirectionsStep step[]=leg[0].steps;
    JSONObject json=new JSONObject();
    JSONArray lon=new JSONArray();
    JSONArray lat=new JSONArray();
    lat.put(startlo.lat);
    lon.put(startlo.lng);
    lat.put(endlo.lat);
    lon.put(endlo.lng);
    for(int i=0; i<step.length;i++)
    { long meters=step[i].distance.inMeters;
      double miles=meters/1609.344;
       if(miles>50)
       {
    	   LatLng loc=step[i].startLocation;
           lat.put(loc.lat);
           lon.put(loc.lng);
       }
     }
    json.put("latitude",lat);
    json.put("longitude",lon);
    return json;
   }
}
