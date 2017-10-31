package com.zm.order.view;

/**
 * Created by lenovo on 2017/10/30.
 */

public class OrderBean {

    private String title;
    private String name;
    private float price;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }
}
