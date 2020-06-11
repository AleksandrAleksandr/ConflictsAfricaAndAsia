package com.example.armedconflicts;

import java.util.ArrayList;

public class DateInfo {

    public static String today;

    public static ArrayList<String> month;

    static public void setTodayAndMonth(String today_, ArrayList<String> month_) {
        today = today_;
        month = month_;
    }
}
