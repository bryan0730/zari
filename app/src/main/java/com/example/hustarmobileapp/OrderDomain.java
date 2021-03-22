package com.example.hustarmobileapp;

class OrderDomain {

    private int tableSeq;
    private int orderSeq;
    private String menuName;
    private String menuPrice;
    private int menuSeq;
    private int orderPerson;

    public OrderDomain(int tableSeq, int orderSeq, String menuName, String menuPrice){
        this.tableSeq = tableSeq;
        this.orderSeq = orderSeq;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
    }

    public OrderDomain(){

    }

    public int getOrderPerson() {
        return orderPerson;
    }

    public void setOrderPerson(int orderPerson) {
        this.orderPerson = orderPerson;
    }

    public int getMenuSeq() {
        return menuSeq;
    }

    public void setMenuSeq(int menuSeq) {
        this.menuSeq = menuSeq;
    }

    public int getTableSeq() {
        return tableSeq;
    }

    public void setTableSeq(int tableSeq) {
        this.tableSeq = tableSeq;
    }

    public int getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(int orderSeq) {
        this.orderSeq = orderSeq;
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
