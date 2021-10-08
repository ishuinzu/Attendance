package com.ishuinzu.aitattendance.app;

import java.util.Calendar;

public class Utils {
    public static String getDateID() {
        return getValueInDoubleFigure(Calendar.getInstance().get(Calendar.MONTH) + 1) + getValueInDoubleFigure(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String getValueInDoubleFigure(int value) {
        if (value <= 9) {
            return "0" + value;
        } else {
            return "" + value;
        }
    }
}