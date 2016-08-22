package com.example.gabrielle.laundryonline.db;

/**
 * Created by gabrielle on 6/11/2016.
 */
public class UserAddressDetails {
    private String username_id;
    private double addressLatitude;
    private double addressLongitude;
    private String completeAddress;
    private String detailAddress;
    private String labelAddress;
    public UserAddressDetails(){
        username_id="";
        addressLatitude=0.0;
        addressLongitude=0.0;
        completeAddress = "";
        detailAddress = "";
        labelAddress="";
    }
    public void setUsername_id(String id){this.username_id=id;}
    public void setAddressLatitude (double lat){
        this.addressLatitude = lat;
    }
    public void setAddressLongitude(double longitude){
        this.addressLongitude = longitude;
    }
    public void setCompleteAddress(String address){
        this.completeAddress = address;
    }
    public void setDetailAddress(String detailAddress){
        this.detailAddress = detailAddress;
    }
    public void setLabelAddress(String labelAddress){
        this.labelAddress = labelAddress;
    }

    public String getUsername_id(){return this.username_id;}
    public double getAddressLatitude (){
        return this.addressLatitude;
    }
    public double getAddressLongitude(){
        return this.addressLongitude;
    }
    public String getCompleteAddress(){
        return this.completeAddress;
    }
    public String getDetailAddress(){
        return this.detailAddress;
    }
    public String getLabelAddress(){
        return this.labelAddress;
    }

}
