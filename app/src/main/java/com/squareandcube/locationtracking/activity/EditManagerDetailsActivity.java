package com.squareandcube.locationtracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squareandcube.locationtracking.Constants;
import com.squareandcube.locationtracking.HttpHandler;
import com.squareandcube.locationtracking.InputValidation;
import com.squareandcube.locationtracking.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class EditManagerDetailsActivity extends ScreenOrientation implements View.OnClickListener {

    private InputValidation mInputValidation;
    private Constants constants;

    private  Button mEdit,mUpdate;

    private TextInputEditText mIdInputEditText;
    private TextInputEditText mNameInputEditText;
    private TextInputEditText mEmailInputEditText;
    private TextInputEditText mMobileInputEditText;
    private TextInputEditText mPasswordInputEditText;

    ProgressDialog pDailog;
    String managerid;

    String id,name,email,password,mobileno,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_manager_details);

        if (netWorkConnection()) {
        }
        managerid = getIntent().getStringExtra("ManagerId");

        initiateObjects();
        initiateViews();
        initiateListeners();
        getDataFromServer();
    }

    private void getDataFromServer() {
        new GetManagerData().execute();
    }

    private void initiateListeners() {
        mEdit.setOnClickListener(this);
        mUpdate.setOnClickListener(this);
    }

    private void initiateViews() {
        mIdInputEditText = (TextInputEditText) findViewById(R.id.managerid_textInputEditText);
        mNameInputEditText = (TextInputEditText) findViewById(R.id.name_textInputEditText);
        mEmailInputEditText = (TextInputEditText) findViewById(R.id.email_textInputEditText);
        mMobileInputEditText = (TextInputEditText) findViewById(R.id.mobile_textInputEditText);
        mPasswordInputEditText = (TextInputEditText) findViewById(R.id.password_textInputEditText);
        mEdit = (Button)findViewById(R.id.edit_button);
        mUpdate = (Button)findViewById(R.id.update_button);

    }

    public void initiateObjects() {
        mInputValidation = new InputValidation(this);
        constants = new Constants();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_button:
                if(netWorkConnection()) {
                    editableTrue();
                    mUpdate.setVisibility(View.VISIBLE);
                    mEdit.setVisibility(View.GONE);
                }
                break;
            case R.id.update_button:
                if(netWorkConnection()) {
                    if (postValidation() == false) {
                        mEdit.setVisibility(View.VISIBLE);
                        mUpdate.setVisibility(View.GONE);
                        editableFalse();
                    }
                }
                break;

        }
    }

    private void editableFalse() {
        mNameInputEditText.setEnabled(false);
        mPasswordInputEditText.setEnabled(false);
        mMobileInputEditText.setEnabled(false);
    }

    private void editableTrue() {
        mNameInputEditText.setEnabled(true);
        mPasswordInputEditText.setEnabled(true);
        mMobileInputEditText.setEnabled(true);
    }


    private boolean postValidation() {
        if (!mInputValidation.isInputEditnewTextFilled(mIdInputEditText, getString(R.string.error_message_id))) {
            return true;
        }
        if (!mInputValidation.isInputEditnewTextFilled(mNameInputEditText, getString(R.string.error_message_name))) {
            return true;
        }
        if (!mInputValidation.isInputEditTextEmail(mEmailInputEditText, getString(R.string.error_message_email))) {
            return true;
        }
        if (!mInputValidation.isMobileNumberCorrect(mMobileInputEditText, getString(R.string.error_mobile_number))) {
            return true;
        }
        if (!mInputValidation.isPasswordLength(mPasswordInputEditText, getString(R.string.error_messages_password))) {
            return true;
        }
        new UpdateData().execute();
        return false;
    }

    public class GetManagerData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            lockScreenOrientation();
            pDailog = new ProgressDialog(EditManagerDetailsActivity.this/*, R.style.ProgressDialogTheme*/);
            pDailog.setMessage("Please Wait......");
            pDailog.show();
            pDailog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String url =  constants.GETMANAGERDATA + managerid;
            HttpHandler mHttpHandler = new HttpHandler();
            String jsonStr = mHttpHandler.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    final JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray jsonArray = jsonObject.getJSONArray("Manager");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject mJson = jsonArray.getJSONObject(i);
                        id = mJson.getString("Id");
                        name = mJson.getString("Name");
                        email = mJson.getString("Email");
                        pass = mJson.getString("Password");
                        mobileno = mJson.getString("MobileNo");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            unlockScreenOrientation();
            if (pDailog.isShowing()) {
                pDailog.dismiss();
            }
            mIdInputEditText.setText(id);
            mNameInputEditText.setText(name);
            mEmailInputEditText.setText(email);
            //Decode the password from server and store in edit text
            byte[] temp = Base64.decode(pass, Base64.DEFAULT);
            try {
                password = new String(temp, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mPasswordInputEditText.setText(password);
            mMobileInputEditText.setText(mobileno);
        }
    }

    public class UpdateData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            lockScreenOrientation();
            pDailog = new ProgressDialog(EditManagerDetailsActivity.this/*, R.style.ProgressDialogTheme*/);
            pDailog.setMessage("Please Wait......");
            pDailog.show();
            pDailog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL( constants.UPDATEMANAGERDATA + managerid);
                JSONObject postData = new JSONObject();
                postData.put("name", mNameInputEditText.getText().toString().trim());
                postData.put("password", mPasswordInputEditText.getText().toString().trim());
                postData.put("mobileno", mMobileInputEditText.getText().toString().trim());

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
//            unlockScreenOrientation();
            String error;
            if (pDailog.isShowing()) {
                pDailog.dismiss();
            }
            try {
                JSONObject js = new JSONObject(result);
                error = js.getString("error");
                if (error.equalsIgnoreCase("false")) {
                    Toast.makeText(getApplicationContext(), R.string.manager_data_updated_text, Toast.LENGTH_SHORT).show();

                    Intent mEmpListIntent = new Intent(EditManagerDetailsActivity.this,EmployeeListActivity.class);
                    mEmpListIntent.putExtra("ManagerEmail",mEmailInputEditText.getText().toString().trim());
                    mEmpListIntent.putExtra("ManagerId",managerid);
                    startActivity(mEmpListIntent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.manager_data_not_updated_text, Toast.LENGTH_SHORT).show();
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
