package com.nowfolats_restaurent_task.Model;

import org.json.JSONObject;

public class RestaurentData
{
    public  String name;
    private String address;
    private String myImg;



    public RestaurentData(String name, String address, String myImg)
    {
        this.name = name;
        this.address = address;
        this.myImg = myImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMyImg() {
        return myImg;
    }

    public void setImg(String myImg) {
        this.myImg = myImg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
