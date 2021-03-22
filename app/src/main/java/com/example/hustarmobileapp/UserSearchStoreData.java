package com.example.hustarmobileapp;

public class UserSearchStoreData {
    private String storeImage;
    private String storeName;
    private String storeEmptyTable;

    public UserSearchStoreData(String storeImage, String storeName, String storeEmptyTable) {
        this.storeImage         = storeImage;
        this.storeName          = storeName;
        this.storeEmptyTable    = storeEmptyTable;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreEmptyTable() {
        return storeEmptyTable;
    }

    public void setStoreEmptyTable(String storeEmptyTable) {
        this.storeEmptyTable = storeEmptyTable;
    }
}
