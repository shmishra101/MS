package com.mywaypointv2;

import java.net.UnknownHostException;

import org.bson.Document;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoConnect 
{
   DB db;
   MongoClient client;
   DBCollection collection;
   // Connect to MongoDB database and return db of given name if present 
   public  DB getConnection(String dbname) throws UnknownHostException
    {  
       
       
    	  client=new MongoClient(new ServerAddress("localhost",27017));
    	   if(client!=null)
    	   {   
    		   db=(DB)client.getDatabase(dbname);
    	   }
        
        return db;
    } 
   // Return the collection from given db
    public DBCollection getCollection(DB db,String colname)
    {   
    	return db.getCollection(colname);
    }
    // Close the database connection
    public  void closeConnection()
    {
    	client.close();
    }

}
