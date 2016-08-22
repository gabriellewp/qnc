package com.example.gabrielle.laundryonline.db;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class PackageType {

    private int id_package;
    private String description;
    private int weight;

    public PackageType(){
        this.id_package = 0;
        this.description = "";
        this.weight =0;
    }

    public PackageType(String description, int weigt){
        this.description = description;
        this.weight = weight;
    }
    public void setId_package(int id){
        id_package = id;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setWeight(int weight){
        this.weight = weight;
    }
    public int getId_package(){
        return id_package;
    }
    public String getDescription(){
        return description;
    }
    public int getWeight(){
        return getWeight();
    }

}