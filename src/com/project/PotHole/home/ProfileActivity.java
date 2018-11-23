package com.project.PotHole.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.PotHole.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vmuser on 2016/05/19.
 */
public class ProfileActivity extends Activity {

    RequestQueue requestQueue;
    String insertUrl = "http://lamp.ms.wits.ac.za/~s815108/reg/insert.php";
    EditText fname, lname, username, town;
    ImageButton imageButtonYes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.profile);

        fname = (EditText) findViewById(R.id.editTextFirstName);
        lname = (EditText) findViewById(R.id.editTextLastName);
        username = (EditText) findViewById(R.id.editTextUsername);
        town = (EditText) findViewById(R.id.editTextTown);
        imageButtonYes = (ImageButton) findViewById(R.id.imageButtonYesProfile);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        imageButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> parameters  = new HashMap<String, String>();
                        parameters.put("firstname", fname.getText().toString());
                        parameters.put("lastname",lname.getText().toString());
                        parameters.put("username", username.getText().toString());
                        parameters.put("town", town.getText().toString());

                        return parameters;
                    }
                };
                requestQueue.add(request);
            }

        });
    }
}