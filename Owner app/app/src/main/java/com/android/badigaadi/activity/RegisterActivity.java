/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.android.badigaadi.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.android.badigaadi.R;
import com.android.badigaadi.app.AppConfig;
import com.android.badigaadi.app.AppController;
import com.android.badigaadi.helper.SQLiteHandler;
import com.android.badigaadi.helper.SessionManager;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity implements TextWatcher {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private TextView btnLinkToLogin;
    private FormEditText user_company_name, user_address, user_contact_no;
    private FormEditText inputPassword, confirm_password;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    FormEditText user_name;

    Spinner userChoice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        user_name = (FormEditText) findViewById(R.id.name);
        user_company_name = (FormEditText) findViewById(R.id.company_name);
        user_address = (FormEditText) findViewById(R.id.address);
        user_contact_no = (FormEditText) findViewById(R.id.contact_number);
        inputPassword = (FormEditText) findViewById(R.id.user_sign_up_password);
        confirm_password = (FormEditText) findViewById(R.id.confirm_password);
        user_company_name.addTextChangedListener(this);
        user_address.addTextChangedListener(this);
        user_contact_no.addTextChangedListener(this);
        inputPassword.addTextChangedListener(this);
        confirm_password.addTextChangedListener(this);
        user_name.addTextChangedListener(this);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (TextView) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = user_name.getText().toString().trim();
                String companyname = user_company_name.getText().toString().trim();
                String companyaddress = user_address.getText().toString().trim();
                String companycontact = user_contact_no.getText().toString().trim();

                String password = inputPassword.getText().toString().trim();
                String confirmPassword = confirm_password.getText().toString().trim();
                if (confirmPassword.equals(password)) {

                    if (!name.isEmpty() && !companycontact.isEmpty() && !companyaddress.isEmpty() &&
                            !companyname.isEmpty() && !password.isEmpty()) {

                        FormEditText[] allFields = {user_name,user_company_name, user_address, user_contact_no,
                        inputPassword, confirm_password};

                        boolean allValid = true;
                        for (FormEditText field: allFields) {
                            allValid = field.testValidity() && allValid;
                        }
                        if(allValid) {
                            registerUser(name, companyname, companycontact, companyaddress, password);
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Wrong Details", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter your details!", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    inputPassword.setText("");
                    confirm_password.setText("");
                    Toast.makeText(RegisterActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String name, final String companyName, final String companyContact,
                              final String companyAddress,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();


                try {
                    JSONObject jObj = new JSONObject(response);

                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite


                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String company_name = user.getString("company_name");
                        String company_address = user.getString("address");
                        String company_contact = user.getString("contact_no");
                        String created_at = user
                                .getString("created_at");

                        String updated_at = user
                                .getString("updated_at");
                        Toast.makeText(RegisterActivity.this, name, Toast.LENGTH_SHORT).show();
                        // Inserting row in users table
                        db.addUser(name, company_name, company_contact, company_address, created_at, updated_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("company_name", companyName);
                params.put("contact_no", companyContact);
                params.put("address", companyAddress);
                params.put("password", password);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public static boolean validate(String txt) {

        String regx = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);
        return matcher.find();

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
       /* boolean allValid;
        allValid = user_name.testValidity();
        if (allValid) {
            Toast.makeText(RegisterActivity.this, "Yes", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegisterActivity.this, "Not", Toast.LENGTH_SHORT).show();
            String str = user_name.getText().toString();
            str = str.substring(0, str.length() - 1);
            user_name.setText(str);
            user_name.setSelection(user_name.getText().length());
        }
    }*/
}}
