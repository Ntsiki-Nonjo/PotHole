package com.project.PotHole.sign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.PotHole.R;
import com.project.PotHole.home.HomeActivity;
import com.project.PotHole.local.LoginDataBaseAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    private EditText email, password;
    private Button signInRegister;
    private RequestQueue requestQueue;
    private static final String URL = "http://lamp.ms.wits.ac.za/~s815108/admin/user_control.php";
    private StringRequest request;
    LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //  create local database
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signInRegister = (Button) findViewById(R.id.signInRegister);

        requestQueue = Volley.newRequestQueue(this);


        signInRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request = new StringRequest(Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.names().get(0).equals("success")) {
                                        Toast.makeText(getApplicationContext(), "Success "
                                                + jsonObject.getString("success"), Toast.LENGTH_LONG).show();

                                        String str = loginDataBaseAdapter.getSingleEntry(email.getText().toString().trim());

                                        if (str.equalsIgnoreCase("NOT EXIST")) {
                                            loginDataBaseAdapter.insertEntry(email.getText().toString().trim(),
                                                    password.getText().toString().trim());
                                        }

                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Error"
                                                + jsonObject.get("error"), Toast.LENGTH_LONG).show();;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("email", email.getText().toString());
                        hashMap.put("password", password.getText().toString());
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginDataBaseAdapter.close();
    }

    public void onRememberPassword(View view) {
        String s = email.getText().toString().trim();

        CheckBox cb = (CheckBox) findViewById(R.id.checkBoxRememberPassword);
        s = loginDataBaseAdapter.getSingleEntry(s);

        if (cb.isChecked()) {
            if (!s.equalsIgnoreCase("NOT EXIST")) {
                password.setText(s);
            }
            else {
                Toast.makeText(getApplicationContext(), "User Doesn't Exist", Toast.LENGTH_LONG).show();
            }
        }
        else {

        }
    }
}
