package com.example.visha.businesspocket;

/**
 * Created by visha on 01 Feb 2018.
 */

public class Employee {

    private String name,type,businessname,phoneNo;
    public  Employee(){

    }
    public  Employee(String name,String category, String businessname,String phoneNo){


        this.name=name;
        this.type=category;
        this.businessname=businessname;
        this.phoneNo=phoneNo;

    }

    public String getType() {
        return type;
    }

    public String getBusinessname() {
        return businessname;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {return phoneNo;}


}
