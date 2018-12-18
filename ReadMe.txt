MyWayPoint 
MyWayPoint is a web application developed to demonstrate the functionality of client server architecture. This application provides the functionality to find the directions between two cities along with the weather information of cities falling in the direction of input cities.
Flow 
•     User input the cities in a source and destination textbox.
•     Application looks for directions and weather data of input cities in database.
•     If no data found in database ,applications hit the APIs to get data and insert it into database.
•    The direction between cities and weather information is displayed on maps on client side.
Technologies Used
•     Google Maps API:-Server side google maps api is used to process the request of user.
•     Open Weather API:-Open Weather API (REST) is used to extract the weather information.
•     JAVA:-Java is used as server side language to interact with the APIs and user respectively.
•     MongoDB:-MongoDB is used as database to store the response from APIs.
Getting Started 
To run the MyWayPoint on your local machine server ,Please follow the steps:-
•    Download  Tomcat 8 version as per your system requirement https://tomcat.apache.org/download-80.cgi.
•    Install the Tomcat server on port 8080.
•    Install the MongoDB database https://docs.mongodb.com/manual/installation/.
•    Sign Up on Google developers https://developers.google.com/ and generate API key for following APIs 
         o	MAPS Javascript API
         o	Directions API
•    Sign Up on Open Weather Map https://openweathermap.org/ and generate API key.
•    Install the IDE which supports the Java web application development.
•    Import the project code in a IDE.
•    Integrate the IDE with Tomcat server.
•    Open the source folder of project and enter the generated API key in APIKey variable in following files:-   
         o	Home.html :- API key of MAPS Javascript API
         o	showRoute.jsp:- API key of Directions API
         o	GoogleDirectionMap.java:- API key o directions API
         o	OpenWeatherApi.java:- API of OpenWeatherAPI
•   Run the project on Tomcat server on port 8080 and open the mongodb instance of database on local machine.
Running the MyWayPoint
After running the application on Tomcat server. Please follow the steps below to start using the application 
•    Go to http://localhost:8080/MyWayPointV2/
•    Select the source and origin cities and click on submit.
•    Route between the input cities will be displayed on next page along with cities/towns between routes. 
•    Hover the mouse over the waypoints to know about weather information.
 

