package com.sawa.aqarat.Activities.Tickets;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseMy_All_tickets {


    private static final String JSON_ARRAY ="Data";
    private static final String KEY_ID = "id";
    private static final String KEY_REASON= "reason";
    private static final String KEY_DESERTION = "desc";
    private static final String KEY_Date= "created_at";


    private String json;

    public ParseMy_All_tickets(String json)
    {
        this.json = json;
    }


    public ArrayList<Ticket_all_Items>  parseProducts (){

        JSONObject jsonObject ;

        ArrayList<Ticket_all_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                Ticket_all_Items products = new Ticket_all_Items();

                JSONObject jo = productsJA.getJSONObject(i);

                products.setTicktId(jo.getString(KEY_ID));
                products.setTicktReason(jo.getString(KEY_REASON));
                products.setTicktDescription(jo.getString(KEY_DESERTION));
                products.setTicktDate(jo.getString(KEY_Date));

                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList",e.getMessage()+"");

        }

        return  productsArrayList;
    }

}