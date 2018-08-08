package com.squareandcube.locationtracking.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.squareandcube.locationtracking.Constants;
import com.squareandcube.locationtracking.InputValidation;
import com.squareandcube.locationtracking.PrefManager;
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

public class ManagerRegistrationActivity extends ScreenOrientation implements View.OnClickListener {

    InputValidation mInputValidation;
    Constants constants;

    TextView mAlreadyRegister;
    Button mRegister;

    TextInputEditText mIdInputEditText;
    TextInputEditText mNameInputEditText;
    TextInputEditText mEmailInputEditText;
    TextInputEditText mMobileInputEditText;
    TextInputEditText mPasswordInputEditText;
    TextInputEditText mConfirmPasswordInputEditText;

    ProgressDialog pDailog;
    String emailfromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_registration);

        if(netWorkConnection()){

        }
        initiateObjects();
        initiateViews();
        initiateListeners();
        pickUserAccount();
    }

    private void initiateListeners() {
        mAlreadyRegister.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    private void initiateViews() {
        mIdInputEditText = (TextInputEditText) findViewById(R.id.managerid_textInputEditText);
        mNameInputEditText = (TextInputEditText) findViewById(R.id.name_textInputEditText);
        mEmailInputEditText = (TextInputEditText) findViewById(R.id.email_textInputEditText);
        mMobileInputEditText = (TextInputEditText) findViewById(R.id.mobile_textInputEditText);
        mPasswordInputEditText = (TextInputEditText) findViewById(R.id.password_textInputEditText);
        mConfirmPasswordInputEditText = (TextInputEditText) findViewById(R.id.confirmpassword_textInputEditText);
        mAlreadyRegister = (TextView) findViewById(R.id.textview_already_register);
        mRegister = (Button)findViewById(R.id.button_register);

    }

    public void initiateObjects() {
        mInputValidation = new InputValidation(this);
        constants = new Constants();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_already_register:
                if(netWorkConnection()) {
                    Intent mManagerLoginIntent = new Intent(this, ManagerLoginActivity.class);
                    startActivity(mManagerLoginIntent);
                }
                break;


            case R.id.button_register:
                if(netWorkConnection()) {
                    postValidation();
                }
                break;

        }
    }

    public void pickUserAccount() {
   /*This will list all available accounts on device without any filtering*/
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                null, false, null, null, null, null);
        startActivityForResult(intent, constants.REQUEST_CODE_PICK_ACCOUNT);
    }
    /*After manually selecting every app related account, I got its Account type using the code below*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == constants.REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                emailfromIntent = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mEmailInputEditText.setText(emailfromIntent);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private void postValidation() {
        if (!mInputValidation.isInputEditnewTextFilled(mIdInputEditText, getString(R.string.error_message_id))) {
            return;
        }
        if (!mInputValidation.isInputEditnewTextFilled(mNameInputEditText, getString(R.string.error_message_name))) {
            return;
        }
        if (!mInputValidation.isInputEditTextEmail(mEmailInputEditText, getString(R.string.error_message_email))) {
            return;
        }
        if (!mInputValidation.isMobileNumberCorrect(mMobileInputEditText, getString(R.string.error_mobile_number))) {
            return;
        }
        if (!mInputValidation.isPasswordLength(mPasswordInputEditText, getString(R.string.error_messages_password))) {
            return;
        }
        if (!mInputValidation.isInputEditTextpassFilled(mConfirmPasswordInputEditText, getString(R.string.error_message_confirm_password))) {
            return;
        }
        if (!mInputValidation.isInputEditTextMatches(mPasswordInputEditText, mConfirmPasswordInputEditText, getString(R.string.error_password_match))) {
            return;
        }

        storeDataInServer();
    }

    private void storeDataInServer() {
        new StoreManagerData().execute();
    }

    public class StoreManagerData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDailog = new ProgressDialog(ManagerRegistrationActivity.this);
            pDailog.setMessage("Please Wait......");
            pDailog.show();
            pDailog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(constants.MANAGERREGISTRATIONAPI);

                JSONObject postData = new JSONObject();
                postData.put("id", mIdInputEditText.getText().toString().trim());
                postData.put("name", mNameInputEditText.getText().toString().trim());
                postData.put("email", mEmailInputEditText.getText().toString().trim());
                postData.put("password", mPasswordInputEditText.getText().toString().trim());
                postData.put("mobileno", mMobileInputEditText.getText().toString().trim());

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
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
            String error;
            if (pDailog.isShowing()) {
                pDailog.dismiss();
            }
            try{
                JSONObject jsonObject = new JSONObject(result);
                error = jsonObject.getString("error");
                if(error.equalsIgnoreCase("false")){
                    attemptLogin();

                }else{
                    Toast.makeText(getApplicationContext(), "Manager Id or Mail Already Exists", Toast.LENGTH_LONG).show();
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


    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String email = mEmailInputEditText.getText().toString();
        String password = mPasswordInputEditText.getText().toString();
        String id = mIdInputEditText.getText().toString();
        saveLoginDetails(id,email,password);
        startHomeActivity();
    }

    private void startHomeActivity() {

        Intent mEmployeeListIntent = new Intent(ManagerRegistrationActivity.this,EmployeeListActivity.class);
        mEmployeeListIntent.putExtra("ManagerEmail",mEmailInputEditText.getText().toString().trim());
        mEmployeeListIntent.putExtra("ManagerId",mIdInputEditText.getText().toString().trim());
        startActivity(mEmployeeListIntent);
        finish();
    }

    private void saveLoginDetails(String id,String email, String password) {
        new PrefManager(this).saveLoginDetails(id,email, password);
    }

}
