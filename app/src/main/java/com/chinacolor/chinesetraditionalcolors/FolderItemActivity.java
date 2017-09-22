package com.chinacolor.chinesetraditionalcolors;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingnanmiao on 9/18/17.
 */

public class FolderItemActivity extends AppCompatActivity {
    public TextView tv_title;
    private ListView lv_colorlist;
    static String folderName;
    //用于query
    public final String color_dir = "content://com.chinacolor.chinesetraditionalcolors.provider/color";
    public Uri uri = Uri.parse(color_dir);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_folder_item);

        tv_title = (TextView)findViewById(R.id.acivity_folder_item_title);
        lv_colorlist = (ListView) findViewById(R.id.acivity_folder_item_colorlist);
        folderName = getIntent().getStringExtra("folderName");
        tv_title.setText(folderName);

        if (folderName.equals("favorite")) {
            List<Color> list_color = getColorItemList(folderName);
            if(list_color != null){
                FolderItemAdapter folderItemAdapter = new FolderItemAdapter(this, R.layout.item_layout, list_color);
                lv_colorlist.setAdapter(folderItemAdapter);
            }
        }else {
            List<Color> list_color = usr_getColorItemList(folderName);
            if(list_color != null){
                FolderItemAdapter folderItemAdapter = new FolderItemAdapter(this, R.layout.item_layout, list_color);
                lv_colorlist.setAdapter(folderItemAdapter);
            }
        }

    }


    /**
     * Get folder's item from Colors.db
     * @param folderName
     * @return
     */
    public List<Color> getColorItemList(String folderName){
        List<Color> colorItemList = new ArrayList<Color>();
        StringBuilder colorName, colorValue;

        // Query from Color.db
        // Foldername must be Color.db's attribute
        Cursor cursor = getContentResolver().query(uri, null, folderName + "= ?", new String[]{"1"}, null);
        //Cursor cursor = getContentResolver().query(uri, new String[]{"favorite"}, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    colorName = new StringBuilder(cursor.getString(cursor.getColumnIndex("name")));
                    colorValue = new StringBuilder(cursor.getString(cursor.getColumnIndex
                            ("value")));
                    long value = Long.parseLong(colorValue.toString(), 16);
                    colorItemList.add(new Color(colorName.toString(), new Long(value).intValue()));
                    //}
                }while (cursor.moveToNext());
                Log.d("FoldersAcivity", "成功查询到数据");
            }else {Log.d("FoldersAcivity","查询成功，无数据");
            }
        }else {Log.d("FoldersActivity", "Cursor为空，查询失败");
        }

        return colorItemList;
    }

    /**
     * Get folder's item from UserFavor.db
     * @param folderName
     * @return
     */
    public List<Color> usr_getColorItemList(String folderName){
        List<Color> colorItemList = new ArrayList<Color>();
        String colorName;
        StringBuilder colorValue;
        //Query From usrFolder
        SQLiteDatabase db = new UserFavorHelper(FolderItemActivity.this, DATABASEINFO.USRDB_NAME, null, 1)
                .getReadableDatabase();
        Cursor cursor = db.query(DATABASEINFO.USRTABLE_NAME, null, folderName + " = ?", new String[]{"1"}, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    colorName = cursor.getString(cursor.getColumnIndex("name"));
                    colorValue = new StringBuilder(cursor.getString(cursor.getColumnIndex
                            ("value")));
                    long value = Long.parseLong(colorValue.toString(), 16);
                    colorItemList.add(new Color(colorName, new Long(value).intValue()));
                }while (cursor.moveToNext());
            }else {Log.d("FolderItemActivity", "查詢成功，无数据");}
        }else {Log.d("FolderItemActivity", "Cursor为空");}


        return colorItemList;
    }
}
