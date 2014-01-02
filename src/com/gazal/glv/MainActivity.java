package com.gazal.glv;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;

import com.gazal.glv.FeedContract.FeedEntry;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.gazal.glv.EXTRA_MESSAGE";
	public static String mainMessage=null;
	public static TextView mTextView;
	public static WebView webView; 
	private Button button;
	final Context context = this;
	public static boolean myStateOnWeb = true;
	public static String myId = "x";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		savedInstanceState = null;
		super.onCreate(null);
		setContentView(R.layout.activity_main);
		
		button = (Button) findViewById(R.id.buttonAlert);
		setupButton(button, "Bye!!!","Sure quit ?", "Yea", "Naa");
		// Initialize member TextView so we can manipulate it later
	    mTextView = (TextView) findViewById(R.id.edit_message);
	    
	    // Make sure we're running on Honeycomb or higher to use ActionBar APIs
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        // For the main activity, make sure the app icon in the action bar
	        // does not behave as a button
	        ActionBar actionBar = getActionBar();
	        actionBar.setHomeButtonEnabled(false);
	    }
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(null);
	}
	
	@SuppressLint("NewApi")
	public void refreshAction(View view){
		System.out.println("TRACK in refresh data");
		JSONObject q=null;
		String uri="";
		try {
			q = new JSONObject("{_id:"+config.mongolabDocumentKey+"}");
			uri = "https://api.mongolab.com/api/1/databases/"+config.mongolabDbName
					+"/collections/"+config.mongolabCollectionName+"?apiKey="+config.mongolabAPIKey
					+"&q="+URLEncoder.encode(q.toString(),"UTF-8");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		new DataDownloadTask().execute(uri);
		
		Intent resultIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, resultIntent, Intent.FILL_IN_ACTION);

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n  = new NotificationCompat.Builder(this)
		        .setContentTitle("New mail from " + "test@gmail.com")
		        .setContentText("Subject")
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentIntent(pendingIntent)
		        .setAutoCancel(true).build();
//		        .addAction(R.drawable.ic_launcher, "Call", pendingIntent)
//		        .addAction(R.drawable.icon, "More", pIntent)
//		        .addAction(R.drawable.icon, "And more", pIntent).build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n); 
	}
	
	public void setupButton(Button button, final String title,
			final String message, final String posText, final String negText) {
		button.setOnClickListener(new OnClickListener () {
			public void onClick(View arg0) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder.setTitle(title);
				alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(posText ,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						finish();
					}
				  })
				.setNegativeButton(negText ,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				 
				// show it
				alertDialog.show();

			}
		});
	}

	@Override
	public void finish(){
		sendToast("Keep Updating!!! :-)");
		super.finish();
	}
	
	@Override
	public void onStart(){
		super.onStart();
		FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = {
				FeedEntry._ID,
				FeedEntry.COLUMN_NAME_CATEGORY,
				FeedEntry.COLUMN_NAME_DETAILS
		};

		Cursor cData = db.query(
				FeedEntry.TABLE_NAME,
				projection,
				null,
				null,
				null,
				null,
				null
				);
		
		
		String category = null;
		String details = null;
		webView = (WebView) findViewById(R.id.myWebView);
		String strShop = "<b>Shopping</b><ul>";
		String strEvent= "<b>Events</b><ul>";
		if (cData !=null && cData.moveToFirst()) {
			do {
				category = cData.getString(cData.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_CATEGORY));
				details = cData.getString(cData.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DETAILS));
				if(category.equals("shop"))
					strShop +="<li>"+details+"</li>";
				else if(category.equals("event"))
					strEvent +="<li>"+details+"</li>";
				System.out.println("TRACK" + "Cat:"+category+"/Det:"+details);
			} while (cData.moveToNext());
		}
		String str = "<html><body>"+strShop+"</ul>"+strEvent+"</ul>"+"</body></html>";
		webView.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);
		db.close();
//		refreshAction(new View(context));
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		String mText = sharedPref.getString("KEY_TEXT", null);
		if(sharedPref.getString("USERNAME", null) == null){
			final Editor myEditor = sharedPref.edit();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Your ID Sir!");

			// Set up the input
			final EditText input = new EditText(this);
			// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(input);

			// Set up the buttons
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
					myEditor.putString("USERNAME", input.getText().toString());
					myEditor.commit();
					myId = input.getText().toString();
			    }
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.cancel();
			    }
			});
			builder.show();
		} else {
			myId = sharedPref.getString("USERNAME", null);
		}
		refreshAction(webView);
	    Switch sView = (Switch) findViewById(R.id.togglebutton);
		if(!myStateOnWeb && sView.isChecked()) sView.toggle();
		if(myStateOnWeb && !sView.isChecked()) sView.toggle();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText et = (EditText) findViewById(R.id.edit_message);
		String msg = et.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, msg);
		String category = "Arbit stuff.";
		intent.putExtra("CATEGORY", category);
		startActivity(intent);
	}

	public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((Switch) view).isChecked();
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		String username = sharedPref.getString("USERNAME", null);

	    if (on) {
	    	MainActivity.webView.loadData("Yeaah...", "text/html; charset=UTF-8", null);;
			new DataUploadTask().execute(username,"1");
	    } else {
	    	MainActivity.webView.loadData("Noooh...", "text/html; charset=UTF-8", null);;
			new DataUploadTask().execute(username,"0");
	        // Disable vibrate
	    }
	    refreshAction(view);
	}
	
	public void saveAction(View view) {
		String category = null;
		RadioButton rView = (RadioButton) findViewById(R.id.radio_shop);
		if(((RadioButton) rView).isChecked())
			category = "shop";
		
		rView = (RadioButton) findViewById(R.id.radio_event);
		if(rView.isChecked()) {
			category = "event";
			// System.out.println("TRACK:" + rview.getText().toString());
		}
		
		if(category==null) { 
			sendToast("Select One.");
			return;
		}
			
		String details = null;
		EditText et = (EditText) findViewById(R.id.edit_message);
		details = et.getText().toString();
		if(details==null || details.equals("")) {
			sendToast("Type Something");
			return;
		}
		
		FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues cValue = new ContentValues();
		cValue.put(FeedEntry.COLUMN_NAME_CATEGORY, category);
		cValue.put(FeedEntry.COLUMN_NAME_DETAILS, details);
		cValue.put(FeedEntry.COLUMN_NAME_ENTRY_ID, 1);
		long newRowId = db.insert(FeedEntry.TABLE_NAME, null, cValue);
		if(newRowId != -1) {
			Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		} else 
			Toast.makeText(this, "Oops! Aliens are coming. Failed to Save!", Toast.LENGTH_SHORT).show();
		db.close();
		onStart();
	}
	
	public void sendToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onStop() {
	    super.onStop();  // Always call the superclass method first
	    SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
	    Editor myEditor = sharedPref.edit();
	    EditText et = (EditText) findViewById(R.id.edit_message);
	    String mText = et.getText().toString();
		myEditor.putString("KEY_TEXT" , mText);
	    myEditor.commit();

	    
//		String id = "1";
//		String category = et.getText().toString();
//		String details = et.getText().toString().toLowerCase();
//
//		FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
//		SQLiteDatabase db = mDbHelper.getWritableDatabase();
//		ContentValues cValues = new ContentValues();
//
//		cValues.put(FeedEntry.COLUMN_NAME_ENTRY_ID, id);
//		cValues.put(FeedEntry.COLUMN_NAME_CATEGORY, category);
//		cValues.put(FeedEntry.COLUMN_NAME_DETAILS, details);
//
//		long newRowId;
//		newRowId = db.insert(
//				FeedEntry.TABLE_NAME,
//				null,
//				cValues);
	}
}
