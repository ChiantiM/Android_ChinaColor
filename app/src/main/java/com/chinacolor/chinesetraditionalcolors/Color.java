package com.chinacolor.chinesetraditionalcolors;

/**
 * Created by lingnanmiao on 9/12/17.
 */

public class Color {

    private int colorValue;
    private String colorName;

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

    public void setColorValue(int colorValue) {
        this.colorValue= colorValue;
    }
    public void setcolorName(String colorName) {
        this.colorName = colorName;
    }
    @Override
    public String toString() {
        return  colorName + "," + colorValue  ;
    }
}