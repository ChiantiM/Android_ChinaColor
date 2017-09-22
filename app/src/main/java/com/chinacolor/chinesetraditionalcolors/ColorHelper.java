package com.chinacolor.chinesetraditionalcolors;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lingnanmiao on 9/13/17.
 */

public class ColorHelper extends SQLiteOpenHelper {
    public static final String CREATE_COLORINFO = "create table Colors("
            + "value text primary key, "
            + "name text, "
            + "favorite integer)";
    public static final String CREATE_MYCOLOR = "create table Mycolor ("
            + "value text primary key, "
            + "name text, "
            + "favorite integer)";
    public static final String CREATE_MYCOLOR_IFNOTEXISTS = "create table IF NOT EXISTS Mycolor ("
            + "value text primary key, "
            + "name text, "
            + "favorite integer)";
    public static final String CREATE_COLORINFO_IFNOTEEXISTS = "create table IF NOT EXISTS Colors("
            + "value text primary key, "
            + "name text, "
            + "favorite integer)";

    public ColorHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_COLORINFO);
        sqLiteDatabase.execSQL(CREATE_MYCOLOR);
        Log.d("ColorHelper" ,"\n 创建数据库成功！");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL(CREATE_COLORINFO_IFNOTEEXISTS);
        db.execSQL(CREATE_MYCOLOR_IFNOTEXISTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Colors");
        sqLiteDatabase.execSQL("drop table if exists Mycolor");
        onCreate(sqLiteDatabase);
    }
}
