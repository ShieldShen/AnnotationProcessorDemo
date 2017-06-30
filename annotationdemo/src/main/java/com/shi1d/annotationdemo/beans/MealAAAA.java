package com.shi1d.annotationdemo.beans;

/**
 * Created by shenli on 2017/6/29.
 */

import com.shie1d.annotations.Factory;

@Factory(id = "MealAAAA", type = Meal.class)
public class MealAAAA extends Meal {

    @Override
    String getName() {
        return "AAAA";
    }

    @Override
    float getPrice() {
        return 2.2f;
    }
}
