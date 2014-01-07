package com.gazal.glv;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class DataUploadTask extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
	    // Create a new HttpClient and Post Header
		String str = "";
		JSONObject q=null;
		try {
			q = new JSONObject("{_id:"+config.mongolabDocumentKey+"}");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		HttpClient httpclient = new DefaultHttpClient();
		String hostStr =  "";
		try {
			hostStr = "https://api.mongolab.com/api/1/databases/"+config.mongolabDbName
					+"/collections/"+config.mongolabCollectionName+"?apiKey="+config.mongolabAPIKey 
					+ "&q="+ URLEncoder.encode(q.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	    HttpPut httppost=new HttpPut(hostStr);

//	    JSONObject jsonObj=null;
//		try {
//			jsonObj = new JSONObject( "{ a : 3 }" );
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	    System.out.println("TRACK jsonObjTostr" + jsonObj.toString());
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        nameValuePairs.add(new BasicNameValuePair("type", "PUT"));
//	        nameValuePairs.add(new BasicNameValuePair("Accept", "application/json"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        
	        StringEntity entity = new StringEntity("{\"$set\":{\""+params[0]+"\":"+params[1]+"}}", HTTP.UTF_8);
	        entity.setContentType("application/json");
	        httppost.setEntity(entity);

	        // Execute HTTP Post Request
		    System.out.println("Track Request " +httppost);
	        
	        HttpResponse response = httpclient.execute(httppost);
		    System.out.println("Track Response " +response);

		    HttpEntity entity1 = httppost.getEntity();
		    String responseString = EntityUtils.toString(entity1, "UTF-8");
		    System.out.println("TRACK Actual Request" + responseString);
		    
		    entity1 = response.getEntity();
		    responseString = EntityUtils.toString(entity1, "UTF-8");
		    System.out.println("TRACK Actual Response" + responseString);
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    }
	    return str;
	}

}
