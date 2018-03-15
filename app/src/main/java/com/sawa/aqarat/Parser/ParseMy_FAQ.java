package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.MyFAQ_Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseMy_FAQ {


    private static final String JSON_ARRAY = "Data";
    private static final String KEY_ID = "id";
    private static final String KEY_QES = "question";
    private static final String KEY_ANSWER = "answer";


    private String json;

    public ParseMy_FAQ(String json) {
        this.json = json;
    }


    public ArrayList<MyFAQ_Items> parseProducts() {

        JSONObject jsonObject;

        ArrayList<MyFAQ_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                MyFAQ_Items products = new MyFAQ_Items();

                JSONObject jo = productsJA.getJSONObject(i);


                products.setFaqId(jo.getString(KEY_ID));
                products.setFaqQes(jo.getString(KEY_QES));
                products.setFaqAnswer(jo.getString(KEY_ANSWER));

                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList", e.getMessage() + "");

        }

        return productsArrayList;
    }

}