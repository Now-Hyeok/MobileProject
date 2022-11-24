package com.example.projectmobile;

import java.util.HashMap;
import java.util.Map;

public class data {
    public String name;
    public String type;
    public String manufacturer;
    public String date;
    public String purchase_date;

    public data(){}

    public data(String name,String purchase_date, String manufacturer, String type, String date){
        this.name = name;
        this.type = type;
        this.manufacturer = manufacturer;
        this.date = date;
        this.purchase_date = purchase_date;

    }

    public Map<String, Object> toData(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name",name);
        result.put("purchase_date",purchase_date);
        result.put("manufacturer",manufacturer);
        result.put("type",type);
        result.put("date",date);

        return result;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getDate() {
        return date;
    }

    public String getPurchase_date() {
        return purchase_date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }
}
