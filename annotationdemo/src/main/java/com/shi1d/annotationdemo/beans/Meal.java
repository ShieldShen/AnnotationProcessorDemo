package com.shi1d.annotationdemo.beans;

/**
 * Created by shenli on 2017/6/29.
 */

public abstract class Meal {
    abstract String getName();

    abstract float getPrice();

    public String toString() {
        return getName() + " : " + getPrice() + "$";
    }
}
