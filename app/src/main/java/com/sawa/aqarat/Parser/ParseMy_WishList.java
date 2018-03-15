package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.My_WishList_Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseMy_WishList {


    private static final String JSON_ARRAY = "Data";
    private static final String KEY_ID = "id";
    private static final String KEY_SENDER_NAME = "username";
    private static final String KEY_Image = "image";
    private static final String KEY_PRICE = "price";
    private static final String KEY_DESC = "desc";


    private String json;

    public ParseMy_WishList(String json) {
        this.json = json;
    }

    public ArrayList<My_WishList_Items> parseProducts() {

        JSONObject jsonObject;

        ArrayList<My_WishList_Items> productsArrayList = new ArrayList<>();

        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                My_WishList_Items products = new My_WishList_Items();

                JSONObject jo = productsJA.getJSONObject(i);

                products.setPost_Id(jo.getString(KEY_ID));
                products.setPost_sender(jo.getString(KEY_SENDER_NAME));
                products.setPost_img(jo.getString(KEY_Image));
                products.setPost_price(jo.getString(KEY_PRICE));
                products.setProducts_desc(jo.getString(KEY_DESC));

                productsArrayList.add(products);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList", e.getMessage() + "");

        }

        return productsArrayList;
    }

}