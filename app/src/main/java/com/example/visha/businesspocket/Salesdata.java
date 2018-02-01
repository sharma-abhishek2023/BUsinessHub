package com.example.visha.businesspocket;
public class Salesdata {
    String productName;
    String quantity;
    public Salesdata(){}

    public Salesdata(String item, String ql){
        this.productName=item;
        this.quantity=ql;
    }

    public String getquantity() {
        return quantity;
    }


    public String getproductName() {
        return productName;
    }
}
