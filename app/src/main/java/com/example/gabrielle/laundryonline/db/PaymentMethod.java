package com.example.gabrielle.laundryonline.db;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class PaymentMethod {
    private int payment_method_id;
    private String description;

    public PaymentMethod(){
        this.payment_method_id=0;
        this.description="";
    }
    public PaymentMethod(String description){
        this.description = description;
    }

    public void setPayment_method_id(int payment_method_id){
        this.payment_method_id = payment_method_id;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public int getPayment_method_id(){
        return payment_method_id;
    }
    public String getDescription(){
        return description;
    }
}
