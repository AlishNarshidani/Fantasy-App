package com.example.fantasyapp;
import android.content.Context;
import android.content.SharedPreferences;
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
    //    private static final String API_KEY ="9168995a-06a6-45d8-817b-9b599cc16d2e";
    private static final String PREFS_NAME = "CricApiPrefs";
    private static final String KEY_API_INDEX = "apiKeyIndex";
//    private static final String API_KEY ="beb673a6-f4e7-4847-bcd9-b36af7d775ad";
    private static final List<String> API_KEYS = new ArrayList<>();
    private static final String BASE_URL = "https://api.cricapi.com/v1/";
    private RequestQueue requestQueue;
    private int currentApiKeyIndex=0;
    private SharedPreferences sharedPreferences;

    public CricApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentApiKeyIndex = sharedPreferences.getInt(KEY_API_INDEX, 0);
        API_KEYS.add("41d57022-8e39-4201-b433-c98c8d807385");
        API_KEYS.add("16abc403-2526-4dfa-b554-02c2affffbd4");
        API_KEYS.add("590b5a28-b953-48cb-a174-214119f4474c");
        API_KEYS.add("1dfef163-a179-4b97-ac29-b5b501d156e1");
        API_KEYS.add("9168995a-06a6-45d8-817b-9b599cc16d2e");
        API_KEYS.add("beb673a6-f4e7-4847-bcd9-b36af7d775ad");
        API_KEYS.add("2f576a1e-bd56-4ec8-b634-7988f9bd3564");
        API_KEYS.add("9f01dcf0-ed57-4499-9987-70da5e7a06ee");
        API_KEYS.add("fae457ef-5ee3-481a-a79d-8c8485638743");
        API_KEYS.add("c8abad4a-038e-49fd-8656-e2fac1397728");
        API_KEYS.add("19a30b4e-48ad-4f9a-b161-1db01722b60b");
        API_KEYS.add("19a30b4e-48ad-4f9a-b161-1db01722b60b");
        API_KEYS.add("b9e2bfd1-c019-446f-9f7c-55f12425c1a9");

        //alish keys
        API_KEYS.add("4d14f25c-1065-4354-9ea1-d5b75f9db3cf");
        API_KEYS.add("0c312255-0128-406c-b7ad-f3254b1c119e");
        API_KEYS.add("f2cdef41-001f-4694-9f53-0478fe6a909c");
    }

    private String getCurrentApiKey() {
        return API_KEYS.get(currentApiKeyIndex);
    }

    private void changeApiKey() {
        currentApiKeyIndex = (currentApiKeyIndex + 1) % API_KEYS.size();
        sharedPreferences.edit().putInt(KEY_API_INDEX, currentApiKeyIndex).apply();
        Log.d("API_KEY_CHANGE", "API key changed to: " + getCurrentApiKey());
    }

    public void getMatches(int offset,final DataCallback callback) {
        String url = BASE_URL + "currentMatches?apikey=" + getCurrentApiKey()+"&offset="+offset;
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

    public void getMatchesNew(final DataCallback callback)
    {
        String url = BASE_URL+"cricScore?apikey="+getCurrentApiKey();
        Log.d("API_REQUEST", "getMatchesNew: "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", "Response: " + response.toString());
                        try {
                            if (response.has("info")) {
                                JSONObject info = response.getJSONObject("info");
                                int hitsToday = info.getInt("hitsToday");
                                Log.d("API_HITS_TODAY", "Hits today: " + hitsToday+" "+getCurrentApiKey());
                                if (hitsToday > 85) {
                                    changeApiKey();
                                    getMatchesNew(callback); // Retry with new API key
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("API_ERROR", "Error processing response", e);
                        }
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
        String url=BASE_URL+"match_scorecard?apikey="+getCurrentApiKey()+"&offset=0"+"&id="+match_id;
        Log.d("API_REQUEST", "Requesting URL: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", "Response: " + response.toString());
                        try {
                            if (response.has("info")) {
                                JSONObject info = response.getJSONObject("info");
                                int hitsToday = info.getInt("hitsToday");
                                Log.d("API_HITS_TODAY", "Hits today: " + hitsToday+" "+getCurrentApiKey());
                                if (hitsToday > 85) {
                                    changeApiKey();
                                    getMatchesNew(callback); // Retry with new API key
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("API_ERROR", "Error processing response", e);
                        }
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
        String url = BASE_URL+"cricScore?apikey="+getCurrentApiKey();
        Log.d("API_REQUEST", "getCricScore: "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", "Response: " + response.toString());
                        try {
                            if (response.has("info")) {
                                JSONObject info = response.getJSONObject("info");
                                int hitsToday = info.getInt("hitsToday");
                                Log.d("API_HITS_TODAY", "Hits today: " + hitsToday+" "+getCurrentApiKey());
                                if (hitsToday > 85) {
                                    changeApiKey();
                                    getMatchesNew(callback); // Retry with new API key
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("API_ERROR", "Error processing response", e);
                        }
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
        String url = BASE_URL+"match_squad?apikey="+getCurrentApiKey()+"&offset=0&id="+match_id;
        Log.d("API_REQUEST", "getCricScore: "+url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", "Response: " + response.toString());
                        try {
                            if (response.has("info")) {
                                JSONObject info = response.getJSONObject("info");
                                int hitsToday = info.getInt("hitsToday");
                                Log.d("API_HITS_TODAY", "Hits today: " + hitsToday+" "+getCurrentApiKey());
                                if (hitsToday > 85) {
                                    changeApiKey();
                                    getMatchesNew(callback); // Retry with new API key
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("API_ERROR", "Error processing response", e);
                        }
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

    public void getFantasyPoints(String match_id, final DataCallback callback)
    {
        String url = BASE_URL+"match_points?apikey="+getCurrentApiKey()+"&id="+match_id+"&ruleset=0";
        Log.d("API_REQUEST", "getCricScore: "+url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", "Response: " + response.toString());
                        try {
                            if (response.has("info")) {
                                JSONObject info = response.getJSONObject("info");
                                int hitsToday = info.getInt("hitsToday");
                                Log.d("API_HITS_TODAY", "Hits today: " + hitsToday+" "+getCurrentApiKey());
                                if (hitsToday > 85) {
                                    changeApiKey();
                                    getMatchesNew(callback); // Retry with new API key
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("API_ERROR", "Error processing response", e);
                        }
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
