package com.chinacolor.chinesetraditionalcolors;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lingnanmiao on 9/18/17.
 */

public class FolderHelper extends SQLiteOpenHelper {
     public static final String CREATE_FOLDERINFO = "create table Folders ("
            + "name text primary key)";
    public static final String CREATE_FOLDERINFO_IFNOTEXISTS = "create table IF NOT EXISTS Folders ("
            + "name text primary key)";

    public FolderHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int
            version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_FOLDERINFO);
        Log.d("FolderHelper" ,"\n 创建数据库成功！");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL(CREATE_FOLDERINFO_IFNOTEXISTS);
        Log.d("FolderHelper" ,"\n 创建表成功！");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        switch (i1){
            case 2:
                sqLiteDatabase.execSQL("drop table if exists Folders");
        }
    }
}


