package com.example.gabrielle.laundryonline.db;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class DropOffOrder {
    private int dropofforder_id;
    private int laundryorder_id;
    private int dropofflocation_id;

    public DropOffOrder(){
        this.dropofforder_id = 0;
        this.laundryorder_id = 0;
        this.dropofflocation_id = 0;
    }
    public void setDropofforder_id(int id){
        this.dropofforder_id =id;
    }
    public void setLaundryorder_id(int id){
        this.laundryorder_id = id;
    }
    public void setDropofflocation_id(int id){
        this.dropofflocation_id  = id;
    }
    public int getDropofforder_id(){
        return this.dropofforder_id;
    }
    public int getLaundryorder_id(){
        return this.laundryorder_id;
    }
    public int getDropofflocation_id(){
        return this.dropofflocation_id;
    }
}
