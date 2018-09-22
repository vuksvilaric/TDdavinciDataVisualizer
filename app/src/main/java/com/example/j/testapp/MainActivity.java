package com.example.j.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
        String url = "https://api.td-davinci.com/api/customers/61410a00-66b8-47a3-b1da-2153b4fd56c6_76a3d6e7-cc7f-425c-bc35-6a069fea2e93/transactions";

        // Basic TextView layout object to route the API response to.
        final TextView mTextView = (TextView) findViewById(R.id.textView);

        // Instantiate the RequestQueue (normally you should create your own RequestQueue).
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided API endpoint.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // get all the money spent on Food and Dining as a test
                        ArrayList<Transaction> transactions = parseResponse(response);
                        Double totalFood = 0.0;
                        for (Transaction t: transactions) {
                            if(t.category.equals("Food and Dining")) {
                                totalFood += t.amount;
                            }
                        }
                        mTextView.setText("Total Food and Dining: $" + totalFood);
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

    private ArrayList<Transaction> parseResponse(String response) {
        try {
            // get JSONObject from JSON file
            JSONObject obj = new JSONObject(response);
            JSONArray responseArray =  obj.getJSONArray("result");

            ArrayList<Transaction> transactions = new ArrayList<>();
            for(int i = 0; i < responseArray.length(); i++) {
                JSONObject tranJSONObj = responseArray.getJSONObject(i);
                String transactionType = tranJSONObj.getString("type");
                if(transactionType.equals("CreditCardTransaction")) {
                    Double currencyAmount = tranJSONObj.getDouble("currencyAmount");
                    String dateStr = tranJSONObj.getString("originationDateTime");
                    String category = tranJSONObj.getJSONArray("categoryTags").getString(0);
                    Transaction transaction = new Transaction(currencyAmount,dateStr,category);
                    transactions.add(transaction);
                }

            }
            return transactions;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
