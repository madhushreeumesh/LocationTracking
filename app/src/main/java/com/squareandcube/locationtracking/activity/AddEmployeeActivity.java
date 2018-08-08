package com.squareandcube.locationtracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squareandcube.locationtracking.Constants;
import com.squareandcube.locationtracking.InputValidation;
import com.squareandcube.locationtracking.R;

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

public class AddEmployeeActivity extends ScreenOrientation implements View.OnClickListener{

    private Button mUpdate;
    private TextInputEditText mEmployeeId;
    private TextInputEditText mEmployeeMobileNumber;
    private TextInputEditText mEmployeePassword;
    private TextInputEditText mManagerId;

    private InputValidation mInputValidation;

    private Constants constants;
    ProgressDialog pDailog;
    String managerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        if(netWorkConnection()){

        }
        managerId = getIntent().getStringExtra("ManagerId");

        initiateObjects();
        initiateViews();
        initiateListeners();
    }

    private void initiateListeners() {
        mUpdate.setOnClickListener(this);
    }

    private void initiateViews() {

        mUpdate = (Button) findViewById(R.id.button_update_employee);
        mEmployeeId = (TextInputEditText)findViewById(R.id.employeeid_textInputEditText);
        mEmployeeMobileNumber = (TextInputEditText)findViewById(R.id.employee_mobile_textInputEditText);
        mEmployeePassword = (TextInputEditText)findViewById(R.id.employee_password_textInputEditText);
        mManagerId = (TextInputEditText) findViewById(R.id.manager_id_textInputEditText);

        mManagerId.setText(managerId);
    }

    public void initiateObjects() {
        mInputValidation = new InputValidation(this);
        constants = new Constants();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_update_employee:
                if(netWorkConnection()) {
                    postValidation();
                    if (postValidation() == false) {
                        new AddEmployee().execute();
                    }
                }
                break;
        }
    }

    private boolean postValidation() {

        if (!mInputValidation.isInputEditnewTextFilled(mEmployeeId, getString(R.string.error_message_id))) {
            return true;
        }
        if (!mInputValidation.isMobileNumberCorrect(mEmployeeMobileNumber, getString(R.string.error_mobile_number))) {
            return true;
        }

        if (!mInputValidation.isPasswordLength(mEmployeePassword, getString(R.string.error_messages_password))) {
            return true;
        }

        if (!mInputValidation.isInputEditnewTextFilled(mManagerId, getString(R.string.error_messages_managerId))) {
            return true;
        }
        return false;
    }


    public class AddEmployee extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lockScreenOrientation();
            pDailog = new ProgressDialog(AddEmployeeActivity.this);
            pDailog.setMessage("Please Wait......");
            pDailog.show();
            pDailog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(constants.ADDEMPLOYEEAPI);

                JSONObject postData = new JSONObject();
                postData.put("emp_id", mEmployeeId.getText().toString().trim());
                postData.put("password", mEmployeePassword.getText().toString().trim());
                postData.put("mobileno", mEmployeeMobileNumber.getText().toString().trim());
                postData.put("manager_id", mManagerId.getText().toString().trim());

                Log.d("StoreData.......", "doInBackground: " + postData.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(1500);
                connection.setConnectTimeout(1500);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postData));

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
            String error;
            if (pDailog.isShowing()) {
                pDailog.dismiss();
            }
            try{
                JSONObject jsonObject = new JSONObject(result);
                error = jsonObject.getString("error");
                if(error.equalsIgnoreCase("false")){
                    Intent mEmployeeListIntent = new Intent(AddEmployeeActivity.this,EmployeeListActivity.class);
                    mEmployeeListIntent.putExtra("ManagerId",managerId);
                    startActivity(mEmployeeListIntent);
                    Toast.makeText(getApplicationContext(), "Employee Added Successful", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Employee id Already Exists", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
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
}
