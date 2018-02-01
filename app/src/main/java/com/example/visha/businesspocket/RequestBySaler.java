package com.example.visha.businesspocket;

/**
 * Created by visha on 01 Feb 2018.
 */

public class RequestBySaler {

    private String name,quantity,retailername;

    public RequestBySaler(){}

    public RequestBySaler(String name,String quantity,String retailername){

        this.name=name;
        this.quantity=quantity;
        this.retailername=retailername;

    }

    public String getName(){return name;}

    public String getQuantity(){return quantity;}

    public String getRetailername(){return retailername;}

}
