package com.example.fantasyapp;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CricApiService {
    private static final String API_KEY ="4d14f25c-1065-4354-9ea1-d5b75f9db3cf";
    private static final String BASE_URL = "https://api.cricapi.com/v1/";
    private RequestQueue requestQueue;
//    private int currentApiKeyIndex=0;

    public CricApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
//        API_KEY.add("41d57022-8e39-4201-b433-c98c8d807385");
//        API_KEY.add("16abc403-2526-4dfa-b554-02c2affffbd4");
//        API_KEY.add("590b5a28-b953-48cb-a174-214119f4474c");
//        API_KEY.add("1dfef163-a179-4b97-ac29-b5b501d156e1");

        //alish keys
//        API_KEY.add("4d14f25c-1065-4354-9ea1-d5b75f9db3cf");
//        API_KEY.add("0c312255-0128-406c-b7ad-f3254b1c119e");
//        API_KEY.add("f2cdef41-001f-4694-9f53-0478fe6a909c");
    }

    public void getMatches(int offset,final DataCallback callback) {
        String url = BASE_URL + "currentMatches?apikey=" + API_KEY+"&offset="+offset;
        Log.d("API_REQUEST", "Requesting URL: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE2", "Response: " + response.toString());
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error: " + error.toString());
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

//    public void changeApiKey() {
//        currentApiKeyIndex = (currentApiKeyIndex + 1) % API_KEY.size();
//    }

    public interface DataCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }

    public void getScorecard(String match_id,final DataCallback callback)
    {
        String url=BASE_URL+"match_scorecard?apikey="+API_KEY+"&offset=0"+"&id="+match_id;
        Log.d("API_REQUEST", "Requesting URL: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", "Response: " + response.toString());
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error: " + error.toString());
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void getCricScore(String match_id,final DataCallback callback)
    {
        String url = BASE_URL+"cricScore?apikey="+API_KEY;
        Log.d("API_REQUEST", "getCricScore: "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", "Response: " + response.toString());
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error: " + error.toString());
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void getSquads(String match_id,final DataCallback callback)
    {
        String url = BASE_URL+"match_squad?apikey="+API_KEY+"&offset=0&id="+match_id;
        Log.d("API_REQUEST", "getCricScore: "+url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", "Response: " + response.toString());
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error: " + error.toString());
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
