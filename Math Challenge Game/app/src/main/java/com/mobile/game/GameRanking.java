package com.mobile.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.mobile.game.databinding.ActivityGameRankingBinding;

import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GameRanking extends AppCompatActivity {
    private ActivityGameRankingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameRankingBinding.inflate(getLayoutInflater());// Set up the back button click listener
        setContentView(binding.getRoot());


        initData();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        OkHttpClient client = new OkHttpClient();

        //Calling Json,HTTP Request
        Request request = new Request.Builder()// Create a request to fetch JSON data
                .url("https://ranking-mobileasignment-wlicpnigvf.cn-hongkong.fcapp.run")//GET request from the  URL.
                .build();


        client.newCall(request).enqueue(new Callback() {// request to be executed asynchronously to execution prevents blocking the main thread
            @Override
            public void onFailure(Call call, IOException e) {//Handles cases where the network request fails
                e.printStackTrace();// Print the stack trace if the request fails
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {// Handle unsuccessful response
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                // Handle the server response
                final String responseData = response.body().string();// Get the response body as a string

                // Update the UI on the main thread
                runOnUiThread(new Runnable() {


                    @Override
                    //Converts the JSON response into a list of RankBean objects.
                    public void run() {
                        Gson gson = new Gson();// Create a new Gson instance
                        Type personListType = new TypeToken<List<RankBean>>() {
                        }.getType();
                        // Deserialize the JSON string into a list of RankBean objects
                        List<RankBean> rankList = gson.fromJson(responseData, personListType);


                        //inds the deserialized data to the ListView
                        RankAdapter rankAdapter = new RankAdapter(GameRanking.this, rankList);
                        binding.listview.setAdapter(rankAdapter);
                        Log.d("jwll", "responseData:" + responseData);
                    }
                });
            }
        });
    }
}