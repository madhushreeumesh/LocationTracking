package com.squareandcube.locationtracking.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.squareandcube.locationtracking.Constants;
import com.squareandcube.locationtracking.InputValidation;
import com.squareandcube.locationtracking.PrefManager;
import com.squareandcube.locationtracking.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class ManagerLoginActivity extends ScreenOrientation implements View.OnClickListener {

    ProgressDialog pDailog;
    TextInputEditText mManagerId,mManagarPassword;
    Button login;
//    TextView mForgotPassword;
    TextView mManagerNotYetRegister;
    private InputValidation mInputValidation;

    private static final int PERMISSION_REQUEST_CODE = 200;

    private Constants constants;
    private final AppCompatActivity activity = ManagerLoginActivity.this;

    private CheckBox checkBoxRememberMe;

    String id;
    String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);

        if (!checkPermission()) {

            requestPermission();

        }
        if(netWorkConnection()) {

        }

        initiateViews();
        initiateListeners();
        initiateObjects();

        if (!new PrefManager(this).isUserLogedOut()) {
            //user's email and password both are saved in preferences
            startHomeActivity();
        }
    }

    public void initiateObjects() {

        mInputValidation = new InputValidation(activity);
        constants = new Constants();
    }

    private void initiateViews() {
        mManagerId = (TextInputEditText) findViewById(R.id.managerId);
        mManagarPassword = (TextInputEditText) findViewById(R.id.managerPassword);
        login = (Button) findViewById(R.id.button_login);
//        mForgotPassword = (TextView) findViewById(R.id.forgotpassword_textView);
        mManagerNotYetRegister = (TextView) findViewById(R.id.not_yet_registered_textview);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);
    }

    public void initiateListeners() {
        login.setOnClickListener(this);
//        mForgotPassword.setOnClickListener(this);
        mManagerNotYetRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                if(netWorkConnection()) {

                        hideSoftKeyBoard();
                        postDataToSQLiteSignIn();
                }
                break;

//            case R.id.forgotpassword_textView:
////                if(netWorkConnection()) {
////                    Intent mForgotIntent = new Intent(this,ForgotPasswordActivity.class);
////                    startActivity(mForgotIntent);
////                }
////                break;

            case R.id.not_yet_registered_textview:
                if(netWorkConnection()) {
                    Intent mRegisterIntent = new Intent(this,ManagerRegistrationActivity.class);
                    startActivity(mRegisterIntent);
                }
                break;
        }
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void  postDataToSQLiteSignIn() {
        if (!mInputValidation.isInputEditTextFilled(mManagerId, getString(R.string.error_message_managerid))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(mManagarPassword, getString(R.string.error_message_password))) {
            return;
        }

        new Signin().execute();
    }

    public class Signin extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lockScreenOrientation();
            pDailog = new ProgressDialog(activity);
            pDailog.setMessage("Please wait Login......");
            pDailog.show();
            pDailog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(constants.MANAGERLOGINAPI);
                JSONObject postLogin = new JSONObject();
                postLogin.put("identity", mManagerId.getText().toString().trim());
                postLogin.put("password", mManagarPassword.getText().toString().trim());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(1500);
                connection.setConnectTimeout(1500);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postLogin));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            unlockScreenOrientation();
            if (pDailog.isShowing()) {
                pDailog.dismiss();
            }
            try {

                JSONObject js = new JSONObject(result);
                JSONArray data = js.getJSONArray("Manager");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject mJson = data.getJSONObject(i);
                    error = mJson.getString("error");
                    id = mJson.getString("Id");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (error.equalsIgnoreCase("true")) {
                Toast.makeText(getApplicationContext(), "Invalid Email And password", Toast.LENGTH_LONG).show();
            } else {
                attemptLogin();
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String email = mManagerId.getText().toString();
        String password = mManagarPassword.getText().toString();
        String mId = id;
        // save data in local shared preferences
        if (checkBoxRememberMe.isChecked()) {
            saveLoginDetails(mId, email, password);
            startHomeActivity();
        }
        else
        {
            startHomeActivity();
        }
    }

    private void startHomeActivity() {

        Intent mEmployeeListIntent = new Intent(this,EmployeeListActivity.class);
        mEmployeeListIntent.putExtra("ManagerEmail",mManagerId.getText().toString().trim());
        mEmployeeListIntent.putExtra("ManagerId",id);
        startActivity(mEmployeeListIntent);
        finish();
    }

    private void saveLoginDetails(String mId, String email, String password) {
        new PrefManager(this).saveLoginDetails(mId, email, password);
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted ){

                    }
                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ManagerLoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
