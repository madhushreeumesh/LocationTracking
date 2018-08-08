package com.squareandcube.locationtracking.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squareandcube.locationtracking.Config;
import com.squareandcube.locationtracking.Constants;
import com.squareandcube.locationtracking.PrefManager;
import com.squareandcube.locationtracking.R;
import com.squareandcube.locationtracking.adapter.EmployeeAdapter;
import com.squareandcube.locationtracking.model.Employee;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmployeeListActivity extends ScreenOrientation implements View.OnClickListener{

    private List<Employee> empList = new ArrayList<>();

    private Constants constants;
    private RecyclerView recyclerView;
    private EmployeeAdapter mAdapter;
//    private Button mAddEmployeeButton;
    private FloatingActionButton mAddEmployeeButton;
    private String managerMailFromIntent;
    private String managerMailFromPreferences;

    SearchView searchView;

    private String managerIdFromIntent;
    private String managerIdFromPreferences;

    private String managerMail,managerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        managerMailFromIntent = getIntent().getStringExtra("ManagerEmail");
        managerIdFromIntent = getIntent().getStringExtra("ManagerId");

        if(netWorkConnection()) {

        }
        initiateViews();
        initiateListeners();
        initiateObjects();

        gettingmail();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(mAdapter);

        if(managerMailFromPreferences.isEmpty()){
            managerMail = managerMailFromIntent;
        }else{
            managerMail = managerMailFromPreferences;
        }

        if(managerIdFromPreferences.isEmpty()){
            managerId = managerIdFromIntent;
        }else{
            managerId = managerIdFromPreferences;
        }

        getData();

        checkPermission();

        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {


            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    searchView.setQuery(matches.get(0),false);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        findViewById(R.id.button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        findViewById(R.id.button).setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        break;
                }
                return false;
            }
        });
        findViewById(R.id.button).setVisibility(View.INVISIBLE);

    }

    private void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }

    }

    private void gettingmail() {
        managerMailFromPreferences = new PrefManager(this).getEmail();
        managerIdFromPreferences = new PrefManager(this).getId();
    }

    private void initiateObjects() {
        constants = new Constants();
    }

    private void initiateListeners() {
        mAddEmployeeButton.setOnClickListener(this);
    }

    private void initiateViews() {
        mAddEmployeeButton = (FloatingActionButton) findViewById(R.id.add_employee_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.employeelis_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem search = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText);
                return false;
            }

        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:

                if(netWorkConnection()){
                    Intent mEditManagerDetailsIntent = new Intent(EmployeeListActivity.this, EditManagerDetailsActivity.class);
                    mEditManagerDetailsIntent.putExtra("ManagerId",managerId);
                    startActivity(mEditManagerDetailsIntent);
                }
                break;
            case R.id.action_logout:
                if(netWorkConnection()){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this,R.style.ProgressDialogTheme);
                    builder.setMessage("Are you sure you want to logout?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // CandidateDetailsActivity.this.finish();
                                    new PrefManager(getApplicationContext()).clear();
                                    Intent mLogoutIntent = new Intent(EmployeeListActivity.this, ManagerLoginActivity.class);
                                    startActivity(mLogoutIntent);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
                }
                break;

            case R.id.action_refresh:

                if(netWorkConnection()){
                    getData();
                }
                break;
            case R.id.action_search:
                break;

            case R.id.action_record:

                findViewById(R.id.button).setVisibility(View.VISIBLE);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.add_employee_button:
                if(netWorkConnection()) {
                    Intent mAddEmployeeIntent = new Intent(EmployeeListActivity.this, AddEmployeeActivity.class);
                    mAddEmployeeIntent.putExtra("ManagerId", managerId);
                    startActivity(mAddEmployeeIntent);
                }
                break;
        }
    }

    private void getData() {
        class GetAllData extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(EmployeeListActivity.this);
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
                    URL url = new URL(constants.GETTINGEMPLOYEEDATA + managerId);
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
            mAdapter = new EmployeeAdapter(EmployeeListActivity.this, getApplicationContext(), Config.id, Config.password, Config.mobile, managerId, managerMail);
            recyclerView.setAdapter(mAdapter);
        }
    }

    private void parseJSON(String json) {
        if(netWorkConnection()) {
            try {
                final JSONObject jsonObject = new JSONObject(json);

                // Getting JSON Array Node
                JSONArray array = jsonObject.getJSONArray("Employees");

                Log.d("Json", "parseJSON: " + array);

                Config config = new Config(array.length());

                // Looping through All articles
                for (int i = 0; i < array.length(); i++) {
                    // Getting the object of given index
                    final JSONObject j = array.getJSONObject(i);
                    Config.id[i] = j.getString("Id");

                    Log.d("TAG", "parseJSON: " + Config.id[i]);

                    String pass = j.getString("Password");

                    byte[] temp = Base64.decode(pass, Base64.DEFAULT);
                    try {
                        Config.password[i] = new String(temp, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Config.mobile[i] = j.getString("MobileNo");
                    Config.managerId[i] = j.getString("ManagerId");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this,R.style.ProgressDialogTheme);
        builder.setMessage("Are you sure you want to Quit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CandidateDetailsActivity.this.finish();
                      finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
