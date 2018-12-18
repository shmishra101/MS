package com.mywaypointv2;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoQuery 
{
	DBObject dbobject;
	BasicDBObject basicdb;
	DBCursor dbcursor;
	JSONObject jsonresponse;
	// This step return the direction response on basic of origin and destination from database.
	public JSONObject getResponse(String origin,String destination,DBCollection dbcollection)
	{
		try 
		{
			basicdb=new BasicDBObject();
		    List<BasicDBObject> finddirection = new ArrayList<BasicDBObject>();
		    finddirection.add(new BasicDBObject("origin", origin));
		    finddirection.add(new BasicDBObject("destination", destination));
		    basicdb.put("$and", finddirection);
			dbobject=dbcollection.findOne(basicdb);
			if(dbobject!=null)
			  {  String response=dbobject.get("response").toString();
			     jsonresponse=new JSONObject(response);
			  }
		} 
		catch (Exception e) 
		{
			
			e.printStackTrace();
		}
		return jsonresponse;
	}
	// This steps returns the weather information of given location from database
	public JSONObject getWeather(Double latitude,Double longitude,DBCollection collection)
	{       basicdb=new BasicDBObject();
            List<BasicDBObject> findweather = new ArrayList<BasicDBObject>();
            findweather.add(new BasicDBObject("latitude",latitude));
            findweather.add(new BasicDBObject("longitude", longitude));
            basicdb.put("$and", findweather);
	        dbobject=collection.findOne(basicdb);
			JSONObject jsonweather=null;
			if(dbobject!=null)
			{
				jsonweather=new JSONObject(dbobject.get("weather").toString());
			}
		    return jsonweather;
	}
	// This method returns the list of latitude of steps in route
	public JSONArray getLatitude(JSONObject jsonobject)
	{   JSONArray latitude=new JSONArray();
		JSONArray routes=jsonobject.getJSONArray("routes");
		JSONObject route=(JSONObject)routes.get(0);
		JSONArray legs=(JSONArray)route.get("legs");
		JSONObject leg=(JSONObject)legs.get(0);
		JSONObject startlo=(JSONObject)leg.get("startLocation");
		JSONObject endlo=(JSONObject)leg.get("endLocation");
		latitude.put(startlo.get("lat"));
		latitude.put(endlo.get("lat"));
		JSONArray steps=leg.getJSONArray("steps");
		for(int i=0; i<steps.length();i++)
	    { JSONObject step=(JSONObject)steps.get(i);
	      JSONObject distance=(JSONObject)step.get("distance");
	      long meters=distance.getLong("inMeters");
	      double miles=meters/1609.44;
	      if(miles>50)
	      { JSONObject location=(JSONObject)step.get("startLocation");
	        latitude.put(location.get("lat"));
	      }
	    }
		return latitude;
	}
	// This method returns the list of longitude coordinate of steps in a route
	public JSONArray getLongitude(JSONObject jsonobject)
	{   JSONArray longitude=new JSONArray();
		JSONArray routes=jsonobject.getJSONArray("routes");
		JSONObject route=(JSONObject)routes.get(0);
		JSONArray legs=(JSONArray)route.get("legs");
		JSONObject leg=(JSONObject)legs.get(0);
		JSONObject startlo=(JSONObject)leg.get("startLocation");
		JSONObject endlo=(JSONObject)leg.get("endLocation");
		longitude.put(startlo.get("lng"));
		longitude.put(endlo.get("lng"));
	    JSONArray steps=leg.getJSONArray("steps");
		for(int i=0; i<steps.length();i++)
	    { JSONObject step=(JSONObject)steps.get(i);
	      JSONObject distance=(JSONObject)step.get("distance");
	      long meters=distance.getLong("inMeters");
	      double miles=meters/1609.44;
	      if(miles>50)
	      { JSONObject location=(JSONObject)step.get("startLocation");
	        longitude.put(location.get("lng"));
	      }
	    }
		return longitude;
	}
	// This method inserts the directions response in a database
	public void insertResponse(String origin,String destination,JSONObject jsonresponse,DBCollection collection)
	{
		basicdb=new BasicDBObject();
		basicdb.put("origin",origin);
		basicdb.put("destination", destination);
		basicdb.put("response",jsonresponse.toString());
		collection.insert(basicdb);
	}
	// This method inserts the weather information in a database 
	public void insertWeather(Double latitude,Double longitude,JSONObject weather,DBCollection collection)
	{  basicdb=new BasicDBObject();
	   basicdb.put("latitude",latitude);
	   basicdb.put("longitude", longitude);
	   basicdb.put("weather", weather.toString());
	   collection.insert(basicdb);
	}
}
