package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.All_Banks_Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseMy_All_Banks {


    private static final String JSON_ARRAY ="Data";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME= "name";
    private static final String KEY_Account_NAME= "account_name";
    private static final String KEY_Account_NUMBER= "account_number";
  //  private static final String KEY_IBAN_number= "Iban_number";
    private static final String KEY_Image= "img";



    private String json;

    public ParseMy_All_Banks(String json)
    {
        this.json = json;
    }


    public ArrayList<All_Banks_Items>  parseProducts (){

        JSONObject jsonObject ;

        ArrayList<All_Banks_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                All_Banks_Items banks_items = new All_Banks_Items();

                JSONObject jo = productsJA.getJSONObject(i);


                banks_items.setBankID(jo.getString(KEY_ID));
                banks_items.setBankName(jo.getString(KEY_NAME));
                banks_items.setBank_Account_Name(jo.getString(KEY_Account_NAME));
                banks_items.setBank_Account_Number(jo.getString(KEY_Account_NUMBER));
//                banks_items.setBank_Iban_Number(jo.getString(KEY_IBAN_number));
                banks_items.setBank_img(jo.getString(KEY_Image));

                productsArrayList.add(banks_items);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList",e.getMessage()+"");

        }

        return  productsArrayList;
    }

}