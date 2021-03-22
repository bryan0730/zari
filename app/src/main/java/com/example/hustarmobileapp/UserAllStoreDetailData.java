package com.example.hustarmobileapp;

public class UserAllStoreDetailData {
    private String storeSeq;
    private String storeName;
    private String storeAddress;
    private String storeIntroduce;
    private String storePhoneNumber;
    private String storeImage;

    public UserAllStoreDetailData(String storeSeq, String storeName, String storeAddress, String storeIntroduce, String storePhoneNumber, String storeImage) {
        this.storeSeq = storeSeq;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeIntroduce = storeIntroduce;
        this.storePhoneNumber = storePhoneNumber;
        this.storeImage = storeImage;
    }

    public String getStoreSeq() {
        return storeSeq;
    }

    public void setStoreSeq(String storeSeq) {
        this.storeSeq = storeSeq;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreIntroduce() {
        return storeIntroduce;
    }

    public void setStoreIntroduce(String storeIntroduce) {
        this.storeIntroduce = storeIntroduce;
    }

    public String getStorePhoneNumber() {
        return storePhoneNumber;
    }

    public void setStorePhoneNumber(String storePhoneNumber) {
        this.storePhoneNumber = storePhoneNumber;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }
}
