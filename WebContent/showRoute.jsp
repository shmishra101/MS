<%@page import="com.mongodb.client.MongoCollection"%>
<%@page import="com.mongodb.client.MongoDatabase"%>
<%@page import="com.mywaypointv2.MongoConnect"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.mywaypointv2.OpenWeatherApi"%>
<%@page import="com.mywaypointv2.MongoQuery"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.google.gson.JsonObject"%>
<%@page import="com.mywaypointv2.GoogleDirectionMap"%>
<%@page import="com.google.gson.JsonArray"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" 
%>
<%@ page import="org.apache.http.HttpEntity" %>
<%@ page import="org.bson.Document" %>
<%@ page import="org.apache.http.HttpResponse" %>
<%@ page import="org.apache.http.client.HttpClient" %>
<%@page import="org.json.JSONObject"%>
<%@ page import="java.io.IOException"
         import="java.io.InputStream"
         import="java.util.Scanner"
         import="com.mongodb.DB"
         import="com.mongodb.DBCollection"%>
<% %>

<html>
  <head>
    <title>Simple Map</title>
    <meta name="viewport" content="initial-scale=1.0">
    <meta charset="utf-8">
    <style>
      #map {
        height: 100%;
      }
    </style>
  </head>
  <%
    String origin=request.getParameter("origin");
    String destination=request.getParameter("destination");
    String contentString="";
    String weathercollection="weather";
    String directioncollection="direction";
    DBCollection collectionweather=null;
    DBCollection collectiondirection=null;
    JSONObject jsonresponse=null;
    JSONObject jsonlocation=null;
    JSONArray jsonlong=null;
    JSONArray jsonlat=null;
    ArrayList<Double> mintemp=new ArrayList<Double>();
    ArrayList<Double> maxtemp=new ArrayList<Double>();
    ArrayList<String> description=new ArrayList<String>();
    ObjectMapper objectMapper=new ObjectMapper();
    DB db;
    
    String dbname="mywaypoint";
    MongoConnect mongoconnect=new MongoConnect();
    //long start=System.currentTimeMillis();
    if(mongoconnect.getConnection(dbname)!=null)
    {   db=mongoconnect.getConnection(dbname);
       if(mongoconnect.getCollection(db,directioncollection)!=null&&mongoconnect.getCollection(db, weathercollection)!=null)
    	{   // This condition will be true if request is sotred in database
    		collectiondirection=mongoconnect.getCollection(db,directioncollection);
            collectionweather=mongoconnect.getCollection(db, weathercollection);
    	    MongoQuery mongoquery=new MongoQuery();
            jsonresponse=mongoquery.getResponse(origin,destination,collectiondirection);
            if(jsonresponse!=null)
            { 
    	        jsonlat=mongoquery.getLatitude(jsonresponse);
    	        jsonlong=mongoquery.getLongitude(jsonresponse);
    	        for(int i=0; i<jsonlat.length();i++)
    	        {
    		       Double lat=(Double)jsonlat.get(i);
    		       Double lon=(Double)jsonlong.get(i);
    		       JSONObject jsonweather=mongoquery.getWeather(lat, lon,collectionweather);
    		       mintemp.add(jsonweather.getDouble("mintemp"));
    		       maxtemp.add(jsonweather.getDouble("maxtemp"));
    		       description.add(jsonweather.getString("description"));
    	        }
    	      /* long time=System.currentTimeMillis()-start;
    	       out.println("Time take by Database"+time);*/
            }
            // This condition will be true when no data stored in database and server will call APIs
    	    else
           { 
    	       GoogleDirectionMap gdirect=new GoogleDirectionMap();
               jsonresponse=gdirect.getResponse(gdirect.getApiresponse(origin, destination));
               if(jsonresponse!=null)
               { jsonlocation=gdirect.getLocation(gdirect.getApiresponse(origin, destination));
               jsonlong=jsonlocation.getJSONArray("longitude");
               jsonlat=jsonlocation.getJSONArray("latitude");
        	   JSONObject weatherdata=new JSONObject();
               ArrayList<String> city=new ArrayList<String>();
               for(int i=0; i<jsonlong.length();i++)
               {  
    	         double latitude=(Double)jsonlat.get(i);
    	         double longitude=(Double)jsonlong.get(i);
		    	 OpenWeatherApi openweather=new OpenWeatherApi();
		    	 JSONObject weatherresponse=openweather.callApi(latitude,longitude);  
		    	 JSONObject cityweather=new JSONObject();
		    	 mintemp.add(openweather.getMinTemperature(weatherresponse));
		    	 cityweather.put("mintemp", openweather.getMinTemperature(weatherresponse));
		    	 maxtemp.add(openweather.getMaxTemp(weatherresponse));
		    	 cityweather.put("maxtemp", openweather.getMaxTemp(weatherresponse));
		    	 description.add(""+openweather.getDescription(weatherresponse).toString());
		    	 cityweather.put("description",openweather.getDescription(weatherresponse).toString());
		    	 mongoquery.insertWeather(latitude, longitude, cityweather, collectionweather);
               }
              mongoquery.insertResponse(origin, destination, jsonresponse, collectiondirection);
               }
               else 
               {   
            	 response.sendRedirect("Home.html");
               }
              /* long time=System.currentTimeMillis()-start;
    	       out.println("Time take by API"+time);*/
          }
    	}
    }
    mongoconnect.closeConnection();
    
  %>
  <body>
    <div id="map"></div>
   <script>
   
  function initMap() 
   {
	   map = new google.maps.Map(document.getElementById('map'), {
	     zoom: 6,
	     center: {lat: -28, lng: 137}
	   });
	   direct();
   }
  function showWayPoints()
  {   var lat=<%=jsonlat%>;
      var lon=<%=jsonlong%>;
      var mintemp=<%=mintemp%>;
      var maxtemp=<%=maxtemp%>;
      var description=[];
      description=<%=objectMapper.writeValueAsString(description)%>;
      var latlng=[];
      var mark=[]
	   for(i=0; i<lat.length; i++)
		  {
		    latlng.push(new google.maps.LatLng(lat[i],lon[i]));
		    console.log(latlng[i].lat);
		    var marker = new google.maps.Marker({
	        position: latlng[i],
	        map: map
	        
	    });
		    mark.push(marker);
		var desc="Minimum Temperature: "+mintemp[i]+"\n"+"Maximum Temperature: "+maxtemp[i]+"\n"+"Condition:"+description[i];
	  var infowindow = new google.maps.InfoWindow({
		    content: desc
		  });
	  google.maps.event.addListener(marker,'mouseover', (function(marker,infowindow){ 
	        return function() {
	           infowindow.open(map,marker);
	        };
	    })(marker,infowindow));
      google.maps.event.addListener(marker,'mouseout', (function(marker,infowindow){ 
	        return function() {
	           infowindow.close();
	        };
	    })(marker,infowindow));
      }
		  
  }
   function direct()
   {   map=new google.maps.Map(document.getElementById('map'),{});
	   var directionsService = new google.maps.DirectionsService();
	   var renderer = new google.maps.DirectionsRenderer();
       var response=<%=jsonresponse%>;
       var buffalo = new google.maps.LatLng(42.8864, -78.8784);
 	   var newyork = new google.maps.LatLng(40.7128, -74.0060);
 	  response.request={origin:buffalo,destination:newyork,travelMode:'DRIVING'};
 	   typecastRoutes(response.routes);
 	   renderer.setMap(map);
 	   renderer.setOptions({suppressMarkers:true});
 	   renderer.setDirections(response);
 	  showWayPoints();
 }
   // Converting the data of response into Maps Javascript API readable form .
   //Referenced piazza
   function typecastRoutes(routes){
 	    routes.forEach(function(route){
 	        route.bounds = asBounds(route.bounds);
 	        route.overviewpath = asPath(route.overviewPolyline);
 	        route.legs.forEach(function(leg){
 	            leg.start_location = asLatLng(leg.startLocation);
 	            leg.end_location   = asLatLng(leg.endLocation);
 	            leg.steps.forEach(function(step){
 	                step.startlocation = asLatLng(step.startLocation);
 	                step.endlocation   = asLatLng(step.endLocation);
 	                step.path = asPath(step.polyline);
 	            });

 	        });
 	    });
 	}
     function asBounds(boundsObject){
 	    return new google.maps.LatLngBounds(asLatLng(boundsObject.southwest),
 	                                    asLatLng(boundsObject.northeast));
 	}

 	function asLatLng(latLngObject){
 	    return new google.maps.LatLng(latLngObject.lat, latLngObject.lng);
 	}

 	function asPath(encodedPolyObject){
 	    return google.maps.geometry.encoding.decodePath(encodedPolyObject.points );
 	}
 	
    </script>
 <script
   type="text/javascript" src="https://maps.googleapis.com/maps/api/js?libraries=geometry&key=&callback&callback=initMap">
</script>
    </body>
    </html>
    