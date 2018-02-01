package com.example.visha.businesspocket;

public class Owner {

    private String name,officeNo,businessName,type;

    public  Owner(){

    }
    public Owner(String name,String officeno, String businessname,String type){
        this.name=name;
        this.officeNo=officeno;
        this.businessName=businessname;
        this.type=type;
    }

    public java.lang.String getName() {
        return name;
    }

    public String getOfficeno() {
        return officeNo;
    }

    public String getBusinessname() {
        return businessName;
    }

    public String getType(){return type;}

}
