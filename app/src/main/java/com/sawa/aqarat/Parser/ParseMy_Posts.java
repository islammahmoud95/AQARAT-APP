package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.My_Posts_Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseMy_Posts {


    private static final String JSON_ARRAY ="Data";
    private static final String KEY_ID = "id";
    private static final String KEY_SENDER_NAME= "username";
    private static final String KEY_Image= "image";
    private static final String KEY_PRICE= "price";
    private static final String KEY_DESC= "desc";
    private static final String KEY_Status= "isClosed";


    private String json;

    public ParseMy_Posts(String json)
    {
        this.json = json;
    }


    public ArrayList<My_Posts_Items>  parseProducts (){

        JSONObject jsonObject ;

        ArrayList<My_Posts_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                My_Posts_Items products = new My_Posts_Items();

                JSONObject jo = productsJA.getJSONObject(i);

                products.setPost_Id(jo.getString(KEY_ID));
                products.setPost_sender(jo.getString(KEY_SENDER_NAME));
                products.setPost_img(jo.getString(KEY_Image));
                products.setPost_price(jo.getString(KEY_PRICE));
                products.setProducts_desc(jo.getString(KEY_DESC));
                products.setPost_Status(jo.getString(KEY_Status));

                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList",e.getMessage()+"");

        }

        return  productsArrayList;
    }

}