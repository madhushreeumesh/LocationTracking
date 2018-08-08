package com.squareandcube.locationtracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class ForgotPasswordActivity extends ScreenOrientation implements View.OnClickListener {

    ProgressDialog pDailog;
    TextInputEditText mEmail;
    Button mSendMail;
    InputValidation mInputValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);
        initviews();
        initiateListeners();
        initObjects();
    }

    private void initObjects() {
        mInputValidation = new InputValidation(this);
    }

    private void initiateListeners() {
        mSendMail.setOnClickListener(this);
    }

    private void initviews() {
        mEmail = (TextInputEditText) findViewById(R.id.mail_textInputEditText);
        mSendMail = (Button) findViewById(R.id.send_Button);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.send_Button:
                sendForgotMail();
        }
    }

    private void sendForgotMail() {
        if (!mInputValidation.isInputEditTextFilled(mEmail, getString(R.string.error_message_managerid))) {
            return;
        }

//        new SendForgotMail().execute();
    }

//
//    public class SendForgotMail extends AsyncTask<String,Void,String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDailog = new ProgressDialog(ForgotPasswordActivity.this);
//            pDailog.setMessage("Please wait Login......");
//            pDailog.show();
//            pDailog.setCancelable(false);
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                URL url = new URL("https://squareandcube.000webhostapp.com/CodeIgniter/index.php/forgot-password");
//                JSONObject postLogin = new JSONObject();
//                postLogin.put("identity", mEmail.getText().toString().trim());
//
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setReadTimeout(1500);
//                connection.setConnectTimeout(1500);
//                connection.setRequestMethod("POST");
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                OutputStream os = connection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                writer.write(getPostDataString(postLogin));
//
//                writer.flush();
//                writer.close();
//                os.close();
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpsURLConnection.HTTP_OK) {
//                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    StringBuffer sb = new StringBuffer("");
//                    String line = "";
//                    while ((line = in.readLine()) != null) {
//                        sb.append(line);
//                        break;
//                    }
//                    in.close();
//                    return sb.toString();
//                } else {
//                    return new String("false : " + responseCode);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            String status;
//            if (pDailog.isShowing()) {
//                pDailog.dismiss();
//            }
//            try {
//                JSONObject js = new JSONObject(result);
//                status = js.getString("status");
//                if (status.equalsIgnoreCase("true")) {
//                    Toast.makeText(getApplicationContext(),"Reset password link sent to your mail",Toast.LENGTH_SHORT).show();
//                    Intent mLoginIntent = new Intent(ForgotPasswordActivity.this,ManagerLoginActivity.class);
//                    startActivity(mLoginIntent);
//                    finish();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Mail is not register with us", Toast.LENGTH_LONG).show();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public String getPostDataString(JSONObject params) throws Exception {
//
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//
//        Iterator<String> itr = params.keys();
//
//        while (itr.hasNext()) {
//
//            String key = itr.next();
//            Object value = params.get(key);
//
//            if (first)
//                first = false;
//            else
//                result.append("&");
//
//            result.append(URLEncoder.encode(key, "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
//
//        }
//        return result.toString();
//    }
}
