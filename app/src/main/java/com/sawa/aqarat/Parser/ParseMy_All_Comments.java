package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.Comments_all_Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseMy_All_Comments {


    private static final String JSON_ARRAY ="Data";
    private static final String KEY_CONTENT = "comment";
    private static final String KEY_USERNAME= "username";
    private static final String KEY_IMAGE= "user_profile";



    private String json;

    public ParseMy_All_Comments(String json)
    {
        this.json = json;
    }


    public ArrayList<Comments_all_Items>  parseProducts (){

        JSONObject jsonObject ;

        ArrayList<Comments_all_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                Comments_all_Items products = new Comments_all_Items();

                JSONObject jo = productsJA.getJSONObject(i);

                products.setComment_Content(jo.getString(KEY_CONTENT));
                products.setComment_UserName(jo.getString(KEY_USERNAME));
                products.setComment_Image(jo.getString(KEY_IMAGE));

                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList",e.getMessage()+"");

        }

        return  productsArrayList;
    }

}