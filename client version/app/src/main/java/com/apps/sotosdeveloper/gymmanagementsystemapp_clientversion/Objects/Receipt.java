package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Objects;

public class Receipt {

    private String receiptAutoCreatedID; // id that firebase created

    private String receiptID;   // admin give the title!
    private String date;
    private String toUser;
    private String amount;
    private final Boolean paid;

    public Receipt(String firebaseReceiptID, String receiptID, String date, String toUser, String amount, Boolean paid) {
        this.receiptAutoCreatedID   = firebaseReceiptID;
        this.receiptID              = receiptID;
        this.date                   = date;
        this.toUser                 = toUser;
        this.amount                 = amount;
        this.paid                   = paid;
    }

    public String getReceiptID() {
        return receiptID;
    }

    public String getReceiptAutoCreatedID() {
        return receiptAutoCreatedID;
    }

    public void setReceiptAutoCreatedID(String receiptAutoCreatedID) {
        this.receiptAutoCreatedID = receiptAutoCreatedID;
    }

    public void setReceiptID(String receiptID) {
        this.receiptID = receiptID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Boolean isReceiptPaid() {
        return paid;
    }

}
