package com.example.fantasyapp;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class CricApiService {
    private static final String API_KEY = "";
    private static final String BASE_URL = "https://api.cricapi.com/v1/";
    private RequestQueue requestQueue;

    public CricApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getMatches(final DataCallback callback) {
        String url = BASE_URL + "currentMatches?apikey=" + API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public interface DataCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
}
