package com.squareandcube.locationtracking;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.games.stats.Stats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;


public class MyService extends Service {

    Constants constants;
//    public static final String MyPREFERENCES = "MyPrefs" ;

    ArrayList<HashMap<String, String>> mNewsList;

    JSONObject jsonObj = new JSONObject();
    String deslatitute, deslongitude, desaddres, emplatitute,emplongitude;
    private static Timer timer = new Timer();
    public Context ctx;

    String jSonData;

    String empId;
    String taskId;


    public void onCreate()
    {
        super.onCreate();
            ctx = this;

            mNewsList = new ArrayList<>();
            startService();

    }


    public int onStartCommand (Intent intent, int flags, int startId) {
        empId = intent.getStringExtra("eId");
        taskId = intent.getStringExtra("tId");
        return START_NOT_STICKY;
    }

    private void startService()
    {
            timer.scheduleAtFixedRate(new mainTask(), 0, 1000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
            new GetNews().execute();
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        stopService();

    }

    private void stopService(){

//        timer.cancel();
//        new GetNews().cancel(true);
        System.exit(0);
    }




    @SuppressLint("HandlerLeak")
    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

        }

    };



    private class GetNews extends AsyncTask<Void, Void, Void> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
        }

        @Override
        protected Void doInBackground(Void... voids) {

                constants = new Constants();
                String url = constants.GETPARTICULARTASKDATA + empId + "&" + taskId;


                HttpHandler mHttpHandler = new HttpHandler();

                // Making a request to url and getting response
                String jsonStr = mHttpHandler.makeServiceCall(url);

                Log.e(TAG, "Response from url: " + jsonStr);

                if (jsonStr != null) {
                    try {
                        jsonObj = new JSONObject(jsonStr);
                        jSonData = jsonObj.toString();


                        // Getting JSON Array node
                        JSONArray articles = jsonObj.getJSONArray("OneTask");


                        for (int i = 0; i < articles.length(); i++) {
                            JSONObject mJSONObject = articles.getJSONObject(i);

//                        deslatitute = mJSONObject.getString("deslatitute");
//                        deslongitude = mJSONObject.getString("deslongitude");
//                        desaddres = mJSONObject.getString("desaddres");
                            emplatitute = mJSONObject.getString("emplatitute");
                            emplongitude = mJSONObject.getString("emplongitude");

                        }

//                    SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
//                    editor.putString("deslatitute", deslatitute);
//                    editor.putString("deslongitude", deslongitude);
//                    editor.putString("desaddres", desaddres);
//                    editor.putString("emplatitute", emplatitute);
//                    editor.putString("emplongitude", emplongitude);
//
//                    editor.commit();

                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());


                    }


                } else {
                    Log.e(TAG, "Couldn't get json from server.");


                }
            return null;
        }
        private void saveLoginDetails(String emplatitute, String emplongitude) {
            new LatlongPreferences(getApplicationContext()).saveLatLong(emplatitute,emplongitude);
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            SharedPreferences sharedTaskId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//            sharedTaskId.edit().putString("emplatitute", emplatitute).apply();
//            sharedTaskId.edit().putString("emplongitude", emplongitude).apply();

            new LatlongPreferences(getApplicationContext()).clear();
            saveLoginDetails(emplatitute,emplongitude);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public void stopTimer(){
//        timer.cancel();
//    }
}

