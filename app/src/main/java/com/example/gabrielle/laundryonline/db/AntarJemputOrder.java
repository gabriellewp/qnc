package com.example.gabrielle.laundryonline.db;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class AntarJemputOrder {
    private int antarjemput_id;
    private int laundryorder_id;
    private String takenPlace;
    private String returnPlace;
    private int package_id;
    public AntarJemputOrder(){
        this.antarjemput_id = 0;
        this.laundryorder_id = 0;
        this.takenPlace="";
        this.returnPlace="";
        this.package_id=0;
    }
    public void setAntarjemput_id(int id){
        this.antarjemput_id = id;
    }
    public void setLaundryorder_id(int id){
        this.laundryorder_id = id;
    }
    public void setTakenPlace(String place){
        this.takenPlace = place;
    }
    public void setReturnPlace(String returnPlace){
        this.returnPlace = returnPlace;
    }
    public void setPackage_id(int id){
        this.package_id = id;
    }
    public int getAntarjemput_id(){
        return this.antarjemput_id;
    }
    public int getLaundryorder_id(){
        return this.laundryorder_id;
    }
    public String getTakenPlace(){
        return this.takenPlace;
    }
    public String getReturnPlace(){
        return this.returnPlace;
    }
    public int getPackage_id(){
        return package_id;
    }
}