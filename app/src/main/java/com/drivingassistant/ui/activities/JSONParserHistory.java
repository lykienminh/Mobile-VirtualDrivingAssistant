package com.drivingassistant.ui.activities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParserHistory {
    private HashMap<String, String> parseJSONObject(JSONObject object) {
        // Initialize hash map
        HashMap<String, String> dataList = new HashMap<>();

        try {
            String speed = object.getString("speed");
            String latitude = object.getString("latitude");
            String longitude = object.getString("longitude");
            String traffic_sign = object.getString("traffic_sign");
            String created_at = object.getString("created_at");

            // Put all value in hash map
            dataList.put("speed", speed);
            dataList.put("latitude", latitude);
            dataList.put("longitude", longitude);
            dataList.put("traffic_sign", traffic_sign);
            dataList.put("created_at", created_at);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return hash map
        return dataList;
    }

    private List<HashMap<String, String>> parseJSONArray(JSONArray jsonArray){
        // Initialize hash map list
        List<HashMap<String, String>> dataList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++){
            try {
                // Initialize hash map
                HashMap<String, String> data = parseJSONObject((JSONObject) jsonArray.get(i));
                // Add data in hash map list
                dataList.add(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Return hash map list
        return dataList;
    }

    public List<HashMap<String, String>> parseResult(JSONObject jsonObject){
        // Initialize json array
        JSONArray jsonArray = null;

        // Get result array
        try {
            jsonArray = jsonObject.getJSONArray("result");
            Log.wtf("khang", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return array
        return parseJSONArray(jsonArray);
    }
}
