package com.example.gabrielle.laundryonline.db;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class User {
    private String email;
    private String familyName;
    private String firstName;
    private String joinDate;
    private String password;
    private long remainingSaldo; //kg
    private String telNumber;
    private String username_ID;
    private int verifiedTelNumber; //0=false or 1=true //verifying tel number
    private int verifiedUserEmail; //0 = false or 1 =true //verifying account


    public User(){
        this.email = "";
        this.familyName="";
        this.firstName = "";
        this.joinDate  ="";
        this.password = "";
        this.remainingSaldo=0;
        this.telNumber = "";
        this.username_ID = "";
        this.verifiedTelNumber=0;
        this.verifiedUserEmail = 0;
    }

    public String getUsername_ID(){
        return this.username_ID;
    }
    public String getPassword(){
        return this.password;
    }
    public String getEmail(){
        return this.email;
    }
    public String getFirstName() {return this.firstName;}
    public String getFamilyName(){return this.familyName;}
    public String getTelNumber(){return this.telNumber;}
    public Long getRemainingSaldo(){return this.remainingSaldo;}
    public int getVerifiedUserEmail(){return this.verifiedUserEmail;}
    public int getVerifiedTelNumber(){return this.verifiedTelNumber;}
    public String getJoinDate(){return this.joinDate;}


    public void setUsername_ID(String _id){
        this.username_ID = _id;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setFirstName(String frontName){this.firstName = frontName;}
    public void setFamilyName(String familyName){this.familyName = familyName;}
    public void setTelNumber(String telNumber){this.telNumber = telNumber;}
    public void setRemainingSaldo(int remainingSaldo){this.remainingSaldo = remainingSaldo;}
    public void setVerifiedUserEmail(int verifiedEmailStatus){this.verifiedUserEmail = verifiedEmailStatus;}
    public void setJoinDate(String joinDate){this.joinDate = joinDate;}
    public void setVerifiedTelNumber(int verifiedTelNumber){this.verifiedTelNumber=verifiedTelNumber;}
}
