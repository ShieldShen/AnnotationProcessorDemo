package com.shi1d.annotationdemo.beans;

/**
 * Created by shenli on 2017/6/29.
 */

import com.shie1d.annotations.Factory;

@Factory(id = "MealBBBB", type = Meal.class)
public class MealBBBB extends Meal {

    @Override
    String getName() {
        return "BBBB";
    }

    @Override
    float getPrice() {
        return 4.2f;
    }
}
