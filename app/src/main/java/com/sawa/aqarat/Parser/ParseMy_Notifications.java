package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.Notifications_Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseMy_Notifications {


    private static final String JSON_ARRAY ="Data";
    private static final String KEY_ID = "id";
    private static final String KEY_Title= "title";
    private static final String KEY_DESERTION = "msg";
    private static final String KEY_IS_SEEN= "isSeen";
    private static final String KEY_Redirect_type = "notifyType";
    private static final String KEY_Redirect_id = "redirect_id";

    private String json;

    public ParseMy_Notifications(String json)
    {
        this.json = json;
    }


    public ArrayList<Notifications_Items>  parseProducts (){

        JSONObject jsonObject ;

        ArrayList<Notifications_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                Notifications_Items products = new Notifications_Items();

                JSONObject jo = productsJA.getJSONObject(i);

                products.setNotify_Id(jo.getString(KEY_ID));
                products.setNotify_Title(jo.getString(KEY_Title));
                products.setNotify_Description(jo.getString(KEY_DESERTION));
                products.setNotify_is_Seen(jo.getString(KEY_IS_SEEN));
                products.setNotify_Redirect_Type(jo.getString(KEY_Redirect_type));
                products.setNotify_Redirect_ID(jo.getString(KEY_Redirect_id));

                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList",e.getMessage()+"");

        }

        return  productsArrayList;
    }

}