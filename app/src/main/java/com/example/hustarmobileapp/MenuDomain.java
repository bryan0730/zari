package com.example.hustarmobileapp;

public class MenuDomain {

    private int menuSeq;
    private String menuName;
    private String menuPrice;

    public MenuDomain(int menuSeq, String menuName, String menuPrice) {
        this.menuSeq = menuSeq;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
    }

    public int getMenuSeq() {
        return menuSeq;
    }

    public void setMenuSeq(int menuSeq) {
        this.menuSeq = menuSeq;
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
}
