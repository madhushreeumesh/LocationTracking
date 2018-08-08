package com.squareandcube.locationtracking;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class InputValidation {

    private Context context;

    /**
     * constructor
     *
     * @param context
     */
    public InputValidation(Context context) {
        this.context = context;
    }

    /**
     * method to check InputEditText filled .
     *
     * @param textInputEditText //     * @param textInputLayout
     * @param message
     * @return
     */
    public boolean isInputEditTextFilled(TextInputEditText textInputEditText, /*TextInputLayout textInputLayout,*/ String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputEditText.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
//            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean isInputEditTextpassFilled(EditText textInputEditText, /*TextInputLayout textInputLayout,*/ String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputEditText.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
//            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean isPasswordLength(EditText textInputEditText, String message) {
        String password = textInputEditText.getText().toString().trim();
        if(password.length()<8) {
            textInputEditText.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }else{

        }

        return true;
    }

    public boolean isMobileNumberCorrect(EditText textInputEditText, String message) {
        String mobileNo = textInputEditText.getText().toString().trim();
        if (mobileNo.isEmpty()||(mobileNo.length()!=10)) {
            textInputEditText.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
//            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * method to check InputEditText has valid email .
     *
     * @param textInputEditText //     * @param textInputLayout
     * @param message
     * @return
     */
    public boolean isInputEditTextEmail(EditText textInputEditText, /*TextInputLayout textInputLayout,*/ String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            textInputEditText.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
//            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextMatches(EditText textInputEditText1, EditText textInputEditText2, /*TextInputLayout textInputLayout,*/ String message) {
        String value1 = textInputEditText1.getText().toString().trim();
        String value2 = textInputEditText2.getText().toString().trim();
        if (!value1.contentEquals(value2)) {
            textInputEditText2.setError(message);
            hideKeyboardFrom(textInputEditText2);
            return false;
        } else {
//            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }



    /**
     * method to Hide keyboard
     *
     * @param view
     */
    private void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public boolean isRadioButtongenderChecked(RadioGroup gender, Context context) {
        if (gender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(context, "Please select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {

        }
        return true;
    }

    public boolean isRadioButtonjobChecked(RadioGroup job, Context context) {
        if (job.getCheckedRadioButtonId() == -1) {
            Toast.makeText(context, "Please select Occupation", Toast.LENGTH_SHORT).show();
            return false;
        } else {

        }
        return true;
    }

    public boolean isRadioButtonOnlineclass(RadioGroup job, Context context) {
        if (job.getCheckedRadioButtonId() == -1) {
            Toast.makeText(context, "Please select Online class", Toast.LENGTH_SHORT).show();
            return false;
        } else {

        }
        return true;
    }


    public boolean isRadioButtonGapChecked(RadioGroup mEducationGapsRadioGroup, Context applicationContext) {
        if (mEducationGapsRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(context, "Please select Year Gap", Toast.LENGTH_SHORT).show();
            return false;
        } else {

        }
        return true;
    }

    public boolean isRadioButtonYes(RadioGroup gender, Context context) {
        if (gender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(context, "Please select Yes or No", Toast.LENGTH_SHORT).show();
            return false;
        } else {

        }
        return true;
    }

    public boolean isAutoCompleteTextViewFilled(AutoCompleteTextView text, String mesage) {
        if (text.getText().toString().trim().isEmpty() || text.getText().toString().isEmpty()) {
            Toast.makeText(context, "State and city values cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else {

        }
        return true;

    }


    public boolean isInputEditnewTextFilled(EditText textInputEditText, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputEditText.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
//            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean isPercentageSpinnerFilled(Spinner text, String mesage) {
        if (text.getSelectedItem().toString().equals("Select Your Percentage")) {
            Toast.makeText(context, "Select Percentage", Toast.LENGTH_SHORT).show();
            return false;
        } else {

        }
        return true;

    }

    public boolean isExperienceSpinnerFilled(Spinner text, String mesage) {
        if (text.getSelectedItem().toString().equals("Select Your Experience")) {
            Toast.makeText(context, "Select Experience", Toast.LENGTH_SHORT).show();
            return false;
        } else {

        }
        return true;

    }
}
