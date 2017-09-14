package com.chinacolor.chinesetraditionalcolors;

/**
 * Created by lingnanmiao on 9/12/17.
 */

public class Color {

    public int colorValue;
    public String colorName;

    public Color(String name, int value){
        colorValue = value;
        colorName = name;
    }

    public int getColorValue(){
        return colorValue;
    }

    public String getColorName(){
        return colorName;
    }
}
