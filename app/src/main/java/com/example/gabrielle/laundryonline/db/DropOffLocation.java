package com.example.gabrielle.laundryonline.db;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class DropOffLocation {
    private int dropofflocation_id;
    private String dropoffaddress;
    private String dropoffowner;
    private int employeenumber;
    private double pointlatitude;
    private double pointlongitude;

    public DropOffLocation(){
        this.dropofflocation_id =0;
        this.dropoffaddress = "";
        this.dropoffowner = "";
        this.employeenumber =0;
        this.pointlatitude = 0.0;
        this.pointlongitude = 0.0;
    }
    public DropOffLocation(String dropoffaddress, String dropoffowner, int employeenumber){
        this.dropofflocation_id=0;
        this.dropoffaddress = dropoffaddress;
        this.dropoffowner = dropoffowner;
        this.employeenumber = employeenumber;
        this.pointlatitude = 0.0;
        this.pointlongitude = 0.0;
    }
    public void setDropofflocation_id(int id){
        this.dropofflocation_id = id;
    }
    public void setDropoffaddress(String dropoffaddress){
        this.dropoffaddress = dropoffaddress;
    }
    public void setDropoffowner(String dropoffowner){
        this.dropoffowner = dropoffowner;
    }
    public void setEmployeenumber(int employeenumber){
        this.employeenumber = employeenumber;
    }
    public void setPointlatitude(double lat){this.pointlatitude = lat;}
    public void setPointlongitude(double longt){this.pointlongitude = longt;}
    public int getDropofflocation_id(){
        return dropofflocation_id;
    }
    public String getDropoffaddress(){
        return dropoffaddress;
    }
    public String getDropoffowner(){
        return dropoffowner;
    }
    public int getEmployeenumber(){
        return employeenumber;
    }
    public double getPointlatitude(){return pointlatitude;}
    public double getPointlongitude(){return pointlongitude;}
}