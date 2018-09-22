package com.example.j.testapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiBranchGetCallExample();

    }

    private void apiBranchGetCallExample() {

        // You can retrieve this info from the My App tab on the TD Da Vinci Platform.
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJDQlAiLCJ0ZWFtX2lkIjoiNjE5Yjk2MTUtYjc0Zi0zN2RlLWEyMDUtMWQ0YzI5M2JkOTBlIiwiZXhwIjo5MjIzMzcyMDM2ODU0Nzc1LCJhcHBfaWQiOiI2MTQxMGEwMC02NmI4LTQ3YTMtYjFkYS0yMTUzYjRmZDU2YzYifQ.8gCHO_j4icTZTCQlTU8lc0cjUZj9dRndZAPAKvc4p1E";
        String url = "https://api.td-davinci.com/api/branches";

        // Basic TextView layout object to route the API response to.
        final TextView mTextView = (TextView) findViewById(R.id.textView);

        // Instantiate the RequestQueue (normally you should create your own RequestQueue).
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided API endpoint.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string as a test.
                        mTextView.setText("Response is: " + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("Error with the API call: " + error.getMessage());
            }
        }) {
            // Add the API Key to the Authorization header of the API request call.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", authToken);

                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
