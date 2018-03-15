package com.sawa.aqarat.Parser;

import android.util.Log;

import com.sawa.aqarat.Models.Sub_Cat_Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseMy_Sub_Cat {


    private static final String JSON_ARRAY ="Data";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME= "name";
    private static final String KEY_Image= "img";


    private String json;

    public ParseMy_Sub_Cat(String json)
    {
        this.json = json;
    }


    public ArrayList<Sub_Cat_Items>  parseProducts (){

        JSONObject jsonObject ;

        ArrayList<Sub_Cat_Items> productsArrayList = new ArrayList<>();


        try {
            jsonObject = new JSONObject(json);

            JSONArray productsJA = jsonObject.getJSONArray(JSON_ARRAY);


            for (int i = 0; i < productsJA.length(); i++) {
                Sub_Cat_Items products = new Sub_Cat_Items();

                JSONObject jo = productsJA.getJSONObject(i);

                products.setProductsId(jo.getString(KEY_ID));
                products.setProductsName(jo.getString(KEY_NAME));
                products.setProducts_img(jo.getString(KEY_Image));

                productsArrayList.add(products);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList",e.getMessage()+"");

        }

        return  productsArrayList;
    }

}