package com.squareandcube.locationtracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
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

public class EditDeleteEmployeeActivity extends ScreenOrientation implements View.OnClickListener{

    ProgressDialog pDailog;
    private TextInputEditText mEmpid,mEmpPassword,mEmpMobile;
    Button mEditEmp,mEmpUpdate;
    private String idstring,passwordstring,mobilestring,managerId;
    private InputValidation mInputValidation;
    private Constants constants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_employee);

        if(netWorkConnection()){

        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        idstring = sharedPreferences.getString("Id","");
        passwordstring = sharedPreferences.getString("Password","");
        mobilestring = sharedPreferences.getString("Mobile","");
        managerId = sharedPreferences.getString("ManagerId","");

        initview();
        initListeners();
        initObjects();
        storeData();
    }

    private void storeData() {
        mEmpid.setText(idstring);
        mEmpPassword.setText(passwordstring);
        mEmpMobile.setText(mobilestring);
    }

    private void initListeners() {
        mEditEmp.setOnClickListener(this);
        mEmpUpdate.setOnClickListener(this);
    }

    private void initview() {
        mEmpid = (TextInputEditText)findViewById(R.id.empid_textInputEditText);
        mEmpPassword = (TextInputEditText)findViewById(R.id.emp_password_textInputEditText);
        mEmpMobile = (TextInputEditText)findViewById(R.id.empmobile_textInputEditText);
        mEditEmp = (Button)findViewById(R.id.empedit_button);
        mEmpUpdate = (Button)findViewById(R.id.empupdate_button);
    }

    private void initObjects() {
        mInputValidation = new InputValidation(this);
        constants = new Constants();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empedit_button:
                if(netWorkConnection()) {
                    editableTrue();
                    mEmpUpdate.setVisibility(View.VISIBLE);
                    mEditEmp.setVisibility(View.GONE);
                }
                break;
            case R.id.empupdate_button:
                if(netWorkConnection()) {
                    if (postValidation() == false) {
                        mEditEmp.setVisibility(View.VISIBLE);
                        mEmpUpdate.setVisibility(View.GONE);
                        editableFalse();
                    }
                }
                break;
        }
    }

    private void editableFalse() {
        mEmpPassword.setEnabled(false);
        mEmpMobile.setEnabled(false);
    }

    private void editableTrue() {
        mEmpPassword.setEnabled(true);
        mEmpMobile.setEnabled(true);
    }

    private boolean postValidation() {
        if (!mInputValidation.isPasswordLength(mEmpPassword, getString(R.string.error_messages_password))) {
            return true;
        }
        if (!mInputValidation.isMobileNumberCorrect(mEmpMobile, getString(R.string.error_mobile_number))) {
            return true;
        }
        new UpdateEmployeeData().execute();
        return false;
    }


    public class UpdateEmployeeData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lockScreenOrientation();
            pDailog = new ProgressDialog(EditDeleteEmployeeActivity.this/*, R.style.ProgressDialogTheme*/);
            pDailog.setMessage("Please Wait......");
            pDailog.show();
            pDailog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL( constants.UPDATEEMPLOYEEDATAAPI + idstring);
                JSONObject postData = new JSONObject();
                postData.put("password", mEmpPassword.getText().toString().trim());
                postData.put("mobileno", mEmpMobile.getText().toString().trim());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(1500);
                connection.setConnectTimeout(1500);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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
            try {
                JSONObject js = new JSONObject(result);
                error = js.getString("error");
                if (error.equalsIgnoreCase("false")) {
                    Toast.makeText(getApplicationContext(), R.string.employee_data_update_text, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.not_update_text, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent employeeListIntent= new Intent(EditDeleteEmployeeActivity.this,EmployeeListActivity.class);
        employeeListIntent.putExtra("ManagerId",managerId);
        startActivity(employeeListIntent);
        finish();
    }
}
