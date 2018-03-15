package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.My_Interest_items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseInterst_Apart {


    private static final String JSON_ARRAY ="realEstate";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME= "name";


    private String json;

    public ParseInterst_Apart(String json)
    {
        this.json = json;
    }


    public ArrayList<My_Interest_items>  parseProducts (){

        JSONObject jsonObject ;

        ArrayList<My_Interest_items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                My_Interest_items products = new My_Interest_items();

                JSONObject jo = productsJA.getJSONObject(i);

                products.setInterest_ID(jo.getString(KEY_ID));
                products.setInteerest_NAME(jo.getString(KEY_NAME));

                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList",e.getMessage()+"");

        }

        return  productsArrayList;
    }

}