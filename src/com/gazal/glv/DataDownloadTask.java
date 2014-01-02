package com.gazal.glv;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.webkit.WebView;

public class DataDownloadTask extends AsyncTask<String, Void, String> {

	
	@Override
	protected String doInBackground(String... uri) {
		System.out.println("TRACK in doInBackground");
		// TODO Auto-generated method stub		
		HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
	}
	

    @Override
    protected void onPostExecute(String result) {
		System.out.println("TRACK in postExecute");
        super.onPostExecute(result);
        MainActivity.mainMessage = result;
//        JSONArray jsonAry=null;
        JSONObject jsonObj=null;
		String strStatus="";
		try {
			jsonObj = new JSONArray(result).getJSONObject(0);
//			System.out.println("TRACK SPARTA  jsonAry : " + jsonAry);
//			jsonObj = jsonAry.getJSONObject(0);
			String val = jsonObj.getString("a");
			if(val.equals("0"))
				strStatus += "<font color=red><i>Anurag </i>&#10008";
			else 
				strStatus += "<font color=green><i>Anurag </i>&#10004";

			val = jsonObj.getString("s");
			if(val.equals("0"))
				strStatus += "<br><font color=red><i>Salil </i>&#10008";
			else 
				strStatus += "<br><font color=green><i>Salil </i>&#10004";

			val = jsonObj.getString("g");
			if(val.equals("0"))
				strStatus += "<br><font color=red><i>Gazal </i>&#10008";
			else 
				strStatus += "<br><font color=green><i>Gazal </i>&#10004";
			//			System.out.println("TRACK SPARTA  jsongetString : " + result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("TRACK SPARTA ERROR");
		}
		String str = "<html><body align=center>"+strStatus+""+"</body></html>";
        MainActivity.webView.loadData(str, "text/html; charset=UTF-8", null);
		System.out.println("TRACK result="+str);
        //Do anything with response..
    }

}
