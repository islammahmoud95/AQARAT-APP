package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.All_Interested_Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseAll_Interested {

    private static final String JSON_ARRAY = "Data";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IsChecked = "isChecked";


    private String json;

    public ParseAll_Interested(String json) {
        this.json = json;
    }


    public ArrayList<All_Interested_Items> parseProducts() {

        JSONObject jsonObject;

        ArrayList<All_Interested_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                All_Interested_Items products = new All_Interested_Items();

                JSONObject jo = productsJA.getJSONObject(i);

                products.setInterest_Id(jo.getString(KEY_ID));
                products.setInterest_Name(jo.getString(KEY_NAME));
                products.setInterest_IsChecked(jo.getString(KEY_IsChecked));


                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList", e.getMessage() + "");

        }

        return productsArrayList;
    }

}