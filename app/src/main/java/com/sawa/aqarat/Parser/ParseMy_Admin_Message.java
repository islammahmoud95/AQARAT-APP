package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.Admin_messag_Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseMy_Admin_Message {


    private static final String JSON_ARRAY ="Data";
    private static final String KEY_ID = "id";
    private static final String KEY_SENDER= "sender";
    private static final String KEY_MESSAGE= "message";
    private static final String KEY_IMAGE= "senderImage";


    private String json;

    public ParseMy_Admin_Message(String json)
    {
        this.json = json;
    }


    public ArrayList<Admin_messag_Items>  parseProducts (){

        JSONObject jsonObject ;

        ArrayList<Admin_messag_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                Admin_messag_Items products = new Admin_messag_Items();

                JSONObject jo = productsJA.getJSONObject(i);


                products.setAD_Msg_Id(jo.getString(KEY_ID));
                products.setAD_Msg_Sender(jo.getString(KEY_SENDER));
                products.setAD_Msg_Message(jo.getString(KEY_MESSAGE));
                products.setAD_Msg_Sender_Img(jo.getString(KEY_IMAGE));

                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList",e.getMessage()+"");

        }

        return  productsArrayList;
    }

}