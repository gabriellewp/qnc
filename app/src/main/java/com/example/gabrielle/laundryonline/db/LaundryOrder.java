package com.example.gabrielle.laundryonline.db;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class LaundryOrder {
    //private int order_id;
    private String addressLabel;
    private String note;
    private String orderID;
    private int orderStatus; //1 = Completed, 0=Not Completed
    private int package_id;
    private int paymentStatus; //1 = PAID, 0 = NOT_PAID
    private long price;
    private String returnDate;
    private int returnTime;
    private String takenDate;
    private int takenTime;
    private String username_id;
    private int weight; //kg
    private int rating;

    public LaundryOrder(){
        this.addressLabel = "";
        this.note = "";
        this.orderID = "";
        this.orderStatus = 0;
        this.package_id=0;
        this.paymentStatus = 0;
        this.price = 0;
        this.returnDate = "";
        this.returnTime=0;
        this.takenDate = "";
        this.takenTime=0;
        this.username_id = "";
        this.weight = 0;
        this.rating = 0;
    }
    public void setAddressLabel(String label){this.addressLabel=label;}
    public void setNote(String note){this.note=note;}
    public void setOrderID(String orderID){this.orderID = orderID;}
    public void setOrderStatus(int orderStatus){this.orderStatus= orderStatus;}
    public void setPackage_id (int package_id){this.package_id= package_id;}
    public void setPaymentStatus(int paymentStatus) {this.paymentStatus=paymentStatus;}
    public void setPrice(long price){this.price = price;}
    public void setReturnDate(String returnDate){this.returnDate = returnDate;}
    public void setReturnTime(int time){this.returnTime=time;}
    public void setTakenDate(String takenDate){this.takenDate = takenDate;}
    public void setTakenTime(int time){this.takenTime=time;}
    public void setUsername_id(String username_id){this.username_id = username_id;}
    public void setWeight(int weight){this.weight = weight;}
    public void setRating(int rating) {this.rating = rating;}

    public String getAddressLabel(){return this.addressLabel;}
    public String getNote(){return this.note;}
    public String getOrderID(){return this.orderID;}
    public int getOrderStatus(){return this.orderStatus;}
    public int getPackage_id(){return this.package_id;}
    public int getPaymentStatus(){return this.paymentStatus;}
    public long getPrice(){return this.price;}
    public String getReturnDate(){return returnDate;}
    public int getReturnTime(){return this.returnTime;}
    public String getTakenDate(){return takenDate;}
    public int getTakenTime(){return this.takenTime;}
    public String getUsername_id(){return this.username_id;}
    public int getWeight(){return this.weight;}
    public int getRating(){return this.rating;}

}
