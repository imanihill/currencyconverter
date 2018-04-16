package com.example.imani.currencyconverter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /*declaring variables*/
    private Spinner mainCurr;
    private Button convert;
    private Spinner foreignCurr;
    private TextView foreignCurrValue;
    private static final String url = "http://fx.xdreamz.net/fx.php"; //passing url
    static ArrayList<String> currencies = new ArrayList<>();
    static HashMap<String, Float> exChangeRate = new HashMap<>();
    private Spinner spinner;
    private String foreignCurrency;
    private Float foreignCurrencyValue;
    private ProgressDialog progress;
    private Float amount = 0.0f;
    TextView rateView;
    private EditText mainCurrValue;

    @Override /*displays GUI*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncClass asyncClass = new AsyncClass();
        asyncClass.execute();


        mainCurr = findViewById(R.id.mainCurr); //print list to spinner
        foreignCurr = findViewById(R.id.foreignCurr); //print list to spinner
        convert = findViewById(R.id.convert);
        rateView = findViewById(R.id.Amount);

        mainCurrValue = findViewById(R.id.mainCurrValue);

        foreignCurrValue = findViewById(R.id.foreignCurrValue);

        mainCurr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                amount = exChangeRate.get(currencies.get(i));
                rateView.setText(String.format("%.2f to JMD", amount));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        foreignCurr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                foreignCurrencyValue = exChangeRate.get(foreignCurrency = currencies.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        convert.setOnClickListener(this);

        progress = new ProgressDialog(MainActivity.this);
        progress.setMessage("Loading Rates..."); // show what you want in the progress dialog
        progress.setCancelable(false); //progress dialog is not cancellable here
        progress.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.convert:
                convert();
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    public void convert() {
        Float dollarsToConvert = Float.parseFloat(mainCurrValue.getText().toString().isEmpty() ? "0.0f" : mainCurrValue.getText().toString());
        Float finalConversion = dollarsToConvert * (amount/foreignCurrencyValue);
        foreignCurrValue.setText(String.format("%.2f",finalConversion));
    }

    private class AsyncClass extends AsyncTask<Void, Void, Void> {

        @SuppressWarnings("RedundantStringToString")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient httpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = httpClient.newCall(request).execute();
                String jsonData = response.body() == null ? "" : response.body().string().toString();
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray jsonArray = jsonObject.getJSONArray("exRates");
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject object = jsonArray.getJSONObject(i);
                    currencies.add(object.getString("Currency"));
                    exChangeRate.put(object.getString("Currency"), Float.parseFloat(object.getString("Amount")));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currencies.add("JMD");
            exChangeRate.put("JMD", 1.0f);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progress.isShowing()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, currencies);
                ArrayAdapter<String> foreignAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, currencies);
                mainCurr.setAdapter(adapter);
                foreignCurr.setAdapter(foreignAdapter);
                progress.dismiss();
            }
        }
    }
}



