package com.mywaypointv2;

import java.io.IOException;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpenWeatherApi 
{   JSONObject json;
    HttpClient httpclient = HttpClientBuilder.create().build();
    HttpResponse response;
    String jsonresponse="";
    String key="";
    // Return the API response in JSON format corresponding to given latitude and longitude
    public JSONObject callApi(double latitude,double longitude) throws IOException
    {  
    	HttpGet htptpget=new HttpGet("https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid="+key);
    	response=httpclient.execute(htptpget);
    	if(response.getEntity()==null)
    	{
    		json=null;
    		return json;
    	}
    	else 
    	{
    		HttpEntity entity=response.getEntity();
    		Scanner sc=new Scanner(entity.getContent());
    	    while(sc.hasNext())
    	    {
    	    	jsonresponse=jsonresponse+sc.nextLine();
    	    }
    		sc.close();
    		json=new JSONObject(jsonresponse);
    		return json;
    	}
    }
 // Return the minimum temperature
    public double getMinTemperature(JSONObject json)
    {
    	JSONObject main=(JSONObject)json.get("main");
    	double min=main.optDouble("temp_min");
    	return min;
    }
    // Return the maximum temperature
    public double getMaxTemp(JSONObject json)
    {
    	JSONObject main=(JSONObject)json.get("main");
    	double max=main.optDouble("temp_max");
    	return max;
    }
    // Return the description
    public String getDescription(JSONObject json)
    {
    	JSONArray weather=(JSONArray)json.get("weather");
    	JSONObject wjson=(JSONObject)weather.get(0);
    	String description=wjson.get("description").toString();
    	return description;
    }
}
