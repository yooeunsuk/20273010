package com.example.a20273010;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;
    DatePickerDialog.OnDateSetListener callbackMethon;

    static RequestQueue requestQueue;
    RecyclerView recyclerView;
    MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.InitalLizeListenner();

        editText = findViewById(R.id.xeditText);
        textView = findViewById(R.id.textView);

        Button backbutton = findViewById(R.id.backButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("message", "result message is OK!");

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


        Button xbutton = findViewById(R.id.xbutton);
        xbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRequest();
            }
        });

        Button dateButton = findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateProcess(v);
            }
        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        );
        recyclerView.setLayoutManager(LayoutManager);

        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter);

    }
    public void InitalLizeListenner()
    {
        callbackMethon = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMounth) {
                String year1 = String.valueOf(year);
                String month1 = String.valueOf(month);
                String dayOfMonth1 = String.valueOf(dayOfMounth);
                String date = year1 + month1 + dayOfMonth1;
                editText.setText("https://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=11b9e04b31f03612433ddcdedd400936&targetDt=" +  date);

            }
        };
    }
    public void dateProcess(View v){
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethon, 2021, 11, 10);
        dialog.show();
    }
    public void makeRequest(){
        String url = editText.getText().toString();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        println("응답 ->" + response);
                        processResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println("에러 ->" + error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        println("요청 보냄.");
    }
    public void println(String data) {
        Log.d("MainActivity", data);
    }
    public void processResponse(String respond) {
        Gson gson = new Gson();
        MovieList movieList = gson.fromJson(respond, MovieList.class);
        println("영화 정보의 수: " + movieList.boxOfficeResult.dailyBoxOfficeList.size());

        for(int i=0; i< movieList.boxOfficeResult.dailyBoxOfficeList.size();i++){
            Movie movie = movieList.boxOfficeResult.dailyBoxOfficeList.get(i);
            adapter.addItem(movie);
        }
        adapter.notifyDataSetChanged();
    }

}