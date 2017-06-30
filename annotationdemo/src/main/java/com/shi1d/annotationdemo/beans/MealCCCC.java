package com.shi1d.annotationdemo.beans;

/**
 * Created by shenli on 2017/6/29.
 */

import com.shie1d.annotations.Factory;

@Factory(id = "MealCCCC", type = Meal.class)
public class MealCCCC extends Meal {

    @Override
    String getName() {
        return "CCCC";
    }

    @Override
    float getPrice() {
        return 1.2f;
    }
}
