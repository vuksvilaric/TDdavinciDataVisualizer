package com.example.j.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button clickButton = (Button) findViewById(R.id.getDataButton);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText edit = (EditText)findViewById(R.id.custIDEdit);
                String cust_id_input = edit.getText().toString();
                apiBranchGetCallExample(cust_id_input);
            }
        });

        //apiBranchGetCallExample();

    }

    private void apiBranchGetCallExample(String cust_id) {

        String url;

        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJDQlAiLCJ0ZWFtX2lkIjoiNjE5Yjk2MTUtYjc0Zi0zN2RlLWEyMDUtMWQ0YzI5M2JkOTBlIiwiZXhwIjo5MjIzMzcyMDM2ODU0Nzc1LCJhcHBfaWQiOiI2MTQxMGEwMC02NmI4LTQ3YTMtYjFkYS0yMTUzYjRmZDU2YzYifQ.8gCHO_j4icTZTCQlTU8lc0cjUZj9dRndZAPAKvc4p1E";
        if(cust_id != null) {
            url = "https://api.td-davinci.com/api/customers/" + cust_id +"/transactions";
        } else {
            url = "https://api.td-davinci.com/api/customers/61410a00-66b8-47a3-b1da-2153b4fd56c6_76a3d6e7-cc7f-425c-bc35-6a069fea2e93/transactions";
        }

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
                        Double totalRemainder = 0.0;

                        int curMonth = -1;
                        int curYear = -1;
                        int months_count = 0;
                        ArrayList<Double> monthSpending = new ArrayList<>();
                        for (Transaction t: transactions) {
                            if(t.category.equals("Food and Dining")) {
                                totalFood += t.amount;
                            }
                            double remainder = Math.ceil(t.amount) - t.amount;
                            totalRemainder += remainder;

                            int month = t.dateCal.get(Calendar.MONTH);
                            int year = t.dateCal.get(Calendar.YEAR);
                            if(curMonth != month || curYear !=  year) {
                                curMonth = month;
                                curYear = year;
                                months_count++;
                                monthSpending.add(0.0);
                            }
                            double currSpent = monthSpending.get(months_count-1);
                            currSpent += t.amount;
                            monthSpending.set(months_count-1,currSpent);
                        }
                        DataPoint[] data = new DataPoint[months_count];
                        for(int i = 0; i < months_count; i++) {
                            data[i] = new DataPoint(i,monthSpending.get(i));
                        }


                        GraphView graph = (GraphView) findViewById(R.id.graph);
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(data);
                        graph.addSeries(series);

                        //mTextView.setText("Total Food and Dining: $" + totalFood + "MoneySaved = $" + totalRemainder + " " + months_count);
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
            // get JSONObject from response
            JSONObject obj = new JSONObject(response);
            JSONArray responseArray =  obj.getJSONArray("result");

            ArrayList<Transaction> transactions = new ArrayList<>();
            for(int i = 0; i < responseArray.length(); i++) {
                JSONObject tranJSONObj = responseArray.getJSONObject(i);
                String transactionType = tranJSONObj.getString("type");
                if(transactionType.equals("CreditCardTransaction") && tranJSONObj.getDouble("currencyAmount") >= 0) {
                    Double currencyAmount = tranJSONObj.getDouble("currencyAmount");
                    String dateStr = tranJSONObj.getString("originationDateTime");
                    Date date = Date.from( Instant.parse( dateStr ));
                    Calendar dateCal = Calendar.getInstance();
                    dateCal.setTime(date);
                    String category = tranJSONObj.getJSONArray("categoryTags").getString(0);
                    Transaction transaction = new Transaction(currencyAmount,dateCal,date,category);
                    transactions.add(transaction);
                }

            }
            Collections.sort(transactions);
            //System.out.print("0: " + transactions.get(0).amount + "");
            return transactions;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
