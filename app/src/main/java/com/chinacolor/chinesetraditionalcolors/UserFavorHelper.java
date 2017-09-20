package com.chinacolor.chinesetraditionalcolors;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lingnanmiao on 9/18/17.
 */

public class UserFavorHelper extends SQLiteOpenHelper{
    public static final String CREATE_COLORINFO = "create table UserFavor("
            + "value text primary key, "
            + "name text)";

    public static final String CREATE_COLORINFO_IFNOTEXISTS = "create table IF NOT EXISTS UserFavor("
            + "value text primary key, "
            + "name text)";

    public UserFavorHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_COLORINFO);
        Log.d("UserFavorrHelper" ,"\n 创建数据库成功！");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL(CREATE_COLORINFO_IFNOTEXISTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists UserFavor");
        onCreate(sqLiteDatabase);
    }
}
