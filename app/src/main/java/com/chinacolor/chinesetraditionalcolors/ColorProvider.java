package com.chinacolor.chinesetraditionalcolors;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.URI;

/**
 * Created by lingnanmiao on 9/13/17.
 *
 * BUG:id不存在，应改成表中的value。
 */

public class ColorProvider extends ContentProvider {

    public static String AUTH = "com.chinacolor.chinesetraditionalcolors.provider";

    private static UriMatcher uriMatcher;

    ColorHelper colorHelper;

    public static final int COLOR_DIR = 0;
    public static final int COLOR_ITEM = 1;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTH, "color", COLOR_DIR);
        uriMatcher.addURI(AUTH, "color/#", COLOR_ITEM);
    }

    @Override
    public boolean onCreate() {
        colorHelper = new ColorHelper(getContext(), "Colors.db", null, 1);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = colorHelper.getWritableDatabase();
        Uri returnUri = null;
        switch (uriMatcher.match(uri)){
            case COLOR_DIR:
            case COLOR_ITEM:
                long newUri = db.insert("Colors", null, contentValues);
                returnUri = Uri.parse("content://" + AUTH + "/color/" + newUri);
                break;
            default:
        }
        return returnUri;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,@Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder){
        Cursor cursor = null;
        SQLiteDatabase db = colorHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)){
            case COLOR_DIR:
                cursor = db.query("Colors", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case COLOR_ITEM:
                String colorid = uri.getPathSegments().get(1);//不是colorValue属性
                cursor = db.query("Colors", projection, "id = ?", new String[]{colorid}, null, null, sortOrder);
                break;
            default:
                break;
        }


        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String
            s, @Nullable String[] strings) {
        int updateRows = 0;
        SQLiteDatabase db = colorHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case COLOR_DIR:
                updateRows = db.update("Colors", contentValues, s, strings);
                break;
            case COLOR_ITEM:
                String id = uri.getPathSegments().get(1);
                updateRows = db.update("Colors", contentValues, "id = ?", new String[]{ id });
                break;
            default:
        }

        return updateRows;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int deletedRows = 0;
        SQLiteDatabase db = colorHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case COLOR_DIR:
                deletedRows = db.delete("Colors", s, strings);
                break;
            case COLOR_ITEM:
                String id = uri.getPathSegments().get(1);
                deletedRows = db.delete("Colors", "id = ?", new String[]{id});
                break;
            default:
        }
        return  deletedRows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case COLOR_DIR:
                return "vnd.android.cursor.dir/vnd.com.chinacolor.chinesetraditionalcolors.provider.color";
            case COLOR_ITEM:
                return "vnd.android.cursor.item/vnd.com.chinacolor.chinesetraditionalcolors.provider.color";
        }
        return null;
    }


}
