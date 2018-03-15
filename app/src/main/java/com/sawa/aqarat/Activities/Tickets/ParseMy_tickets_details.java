package com.sawa.aqarat.Activities.Tickets;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseMy_tickets_details {


    private static final String JSON_ARRAY ="replies";
    private static final String KEY_ID = "id";
    private static final String KEY_DESERTION = "desc";
    private static final String KEY_Date= "created_at";
    private static final String KEY_SENDER= "sender";

    private static final String KEY_IMAGES= "attachments";



    private String json;

    ParseMy_tickets_details(String json)
    {
        this.json = json;
    }


    public ArrayList<Ticket_details_Items>  parseProducts (){

        JSONObject jsonObject ;

        ArrayList<Ticket_details_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                Ticket_details_Items products = new Ticket_details_Items();

                JSONObject jo = productsJA.getJSONObject(i);

                products.setTicktId(jo.getString(KEY_ID));
                products.setTicktDescription(jo.getString(KEY_DESERTION));
                products.setTicktDate(jo.getString(KEY_Date));
                products.setTicktSender(jo.getString(KEY_SENDER));



                ArrayList<String> ticket_images = new ArrayList<>();

                JSONArray jsonArray = jo.getJSONArray(KEY_IMAGES);

                for(int j = 0; j<jsonArray.length(); j++){
                    //  powers.add(((String) jsonArray.get(j))+"\n");
                    ticket_images.add(((String) jsonArray.get(j)));
                    Log.d("sfsfs", String.valueOf(ticket_images));
                }

                products.setTicktImages(ticket_images);



                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList",e.getMessage()+"");

        }

        return  productsArrayList;
    }

}