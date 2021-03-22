package com.example.hustarmobileapp;

// 가게 메뉴 데이터 저장을 위한 클래스
public class UserStoreMenuData {
    private String menuName;
    private String menuPrice;
    private String menuCategory;

    public UserStoreMenuData(String menuName, String menuPrice, String menuCategory) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuCategory = menuCategory;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(String menuPrice) {
        this.menuPrice = menuPrice;
    }

    public String getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory;
    }
}
