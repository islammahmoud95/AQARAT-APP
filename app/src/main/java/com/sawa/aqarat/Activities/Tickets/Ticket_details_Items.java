package com.sawa.aqarat.Activities.Tickets;


import java.util.ArrayList;

public class Ticket_details_Items {

    private String tickt_Id;
    private String tickt_Description;
    private String tickt_sender;
    private String tickt_Date;

    private ArrayList<String> tickt_Images;


    public String getTicktId() {
        return tickt_Id;
    }

    public void setTicktId(String tickt_Id) {
        this.tickt_Id = tickt_Id;
    }


    public String getTicktDescription() {
        return tickt_Description;
    }

    public void setTicktDescription(String tickt_Description) {
        this.tickt_Description = tickt_Description;
    }


    public String getTicktSender() {
        return tickt_sender;
    }

    public void setTicktSender(String tickt_sender) {
        this.tickt_sender = tickt_sender;
    }


    public String getTicktDate() {
        return tickt_Date;
    }

    public void setTicktDate(String tickt_Date) {
        this.tickt_Date = tickt_Date;
    }





    public ArrayList<String> getTicktImages() {
        return tickt_Images;
    }

    public void setTicktImages(ArrayList<String> tickt_Images) {
        this.tickt_Images = tickt_Images;
    }



}



