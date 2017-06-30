package com.shi1d.annotationdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.shi1d.annotationdemo.beans.Meal;
import com.shi1d.annotationdemo.beans.MealAAAA;
import com.shi1d.annotationdemo.beans.MealFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Meal meal = MealFactory.create(MealAAAA.class.getSimpleName());
        Log.d("After", meal.toString());
    }
}
