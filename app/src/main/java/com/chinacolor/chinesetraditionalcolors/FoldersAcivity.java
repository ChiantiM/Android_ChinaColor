package com.chinacolor.chinesetraditionalcolors;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * 通过文件夹管理数据库
 * 一个文件夹名称对应一个标题
 * Created by lingnanmiao on 9/14/17.
 *
 * 注意int是有符号数，parseInt会有坑，0xffffffff颜色不能直接parse为int
 */

public class FoldersAcivity extends AppCompatActivity {
    private TextView folderName;
    private ListView listView;
    //用于query
    public final String color_dir = "content://com.chinacolor.chinesetraditionalcolors.provider/color";
    public Uri uri = Uri.parse(color_dir);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_folders);

        //initialize
        folderName = (TextView)findViewById(R.id.favorite_folder);
        folderName.setText("favorite");
        listView = (ListView)findViewById(R.id.favorite_list);
        listView.setVisibility(ListView.GONE);

        folderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listView.getVisibility() == ListView.GONE){
                    List<Color> colorList = getColorItemList(folderName.getText().toString());
                    if (colorList.isEmpty()){
                        Log.d("FoldersAcivity", "此收藏夹下颜色列表为空");
                    }else{
                        FolderItemAdapter folderItemAdapter = new FolderItemAdapter(FoldersAcivity.this, R.layout.item_layout, colorList);
                        listView.setAdapter(folderItemAdapter);
                    }
                    listView.setVisibility(ListView.VISIBLE);
                }else {
                    listView.setVisibility(ListView.GONE);
                }
            }
        });

    }

    /**
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
                    //int isfavoer = cursor.getInt(cursor.getColumnIndex("favorite"));
                    //if (isfavoer == 1) {
                        //get name and colorvalue
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
}



