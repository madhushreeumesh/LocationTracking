package com.squareandcube.locationtracking.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareandcube.locationtracking.Config;
import com.squareandcube.locationtracking.Constants;
import com.squareandcube.locationtracking.LatlongPreferences;
import com.squareandcube.locationtracking.PrefManager;
import com.squareandcube.locationtracking.R;
import com.squareandcube.locationtracking.TrackConfig;
import com.squareandcube.locationtracking.adapter.TrackDetailsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TrackEmployeeActivity extends ScreenOrientation {

    String empid;
    String managerId;
    String managerEmail;

    private final AppCompatActivity activity = TrackEmployeeActivity.this;
    private Constants constants;
    private RecyclerView recyclerView;
    private TrackDetailsAdapter mTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_employee);

        new LatlongPreferences(getApplicationContext()).clear();

        if(netWorkConnection()) {

        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        empid = sharedPreferences.getString("empId","");
        managerId = sharedPreferences.getString("managerId","");
        managerEmail = sharedPreferences.getString("managerMail","");

        initiateObjects();

        recyclerView = (RecyclerView) findViewById(R.id.track_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(mTrack);

        getTrackData();
    }

    private void initiateObjects() {
        constants = new Constants();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_refresh_button:

                if(netWorkConnection()){
                    getTrackData();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getTrackData() {
        class GetAllData extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(TrackEmployeeActivity.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();

            }

            @Override
            protected String doInBackground(Void... params) {
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(constants.GETTHETOTALTASKDETAILS + empid);
                    Log.d("Data", "doInBackground: "+url);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    // Read the response & convert stream to string
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                parseJSON(s);
                showData();
            }
        }
        final GetAllData gd = new GetAllData();
        gd.execute();
    }

    public void showData() {
        if (netWorkConnection()) {
            Log.d("tag", "showData: " + Config.id + " " + Config.managerId + " " + Config.mobile);
            mTrack = new TrackDetailsAdapter(activity, getApplicationContext(), TrackConfig.taskNo, TrackConfig.deslat, TrackConfig.desLong, TrackConfig.desAddress, TrackConfig.curLat, TrackConfig.curLong, TrackConfig.curAddress, managerId, managerEmail, empid);
            recyclerView.setAdapter(mTrack);
        }
    }

    private void parseJSON(String json) {
        if(netWorkConnection()) {
            try {
                final JSONObject jsonObject = new JSONObject(json);

                // Getting JSON Array Node
                JSONArray array = jsonObject.getJSONArray("Task");

                Log.d("Json", "parseJSON: " + array);

                TrackConfig config = new TrackConfig(array.length());

                // Looping through All articles
                for (int i = 0; i < array.length(); i++) {
                    // Getting the object of given index
                    final JSONObject j = array.getJSONObject(i);
                    TrackConfig.taskNo[i] = j.getString("TaskNo");
                    TrackConfig.empId[i] = j.getString("employeeid");
                    TrackConfig.deslat[i] = j.getString("deslatitute");
                    TrackConfig.desLong[i] = j.getString("deslongitude");
                    TrackConfig.desAddress[i] = j.getString("desaddres");
                    TrackConfig.curLat[i] = j.getString("emplatitute");
                    TrackConfig.curLong[i] = j.getString("emplongitude");
                    TrackConfig.curAddress[i] = j.getString("empaddres");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mGoToEmployeeList = new Intent(activity,EmployeeListActivity.class);
        mGoToEmployeeList.putExtra("ManagerId",managerId);
        mGoToEmployeeList.putExtra("ManagerEmail",managerEmail);
        startActivity(mGoToEmployeeList);
    }
}
