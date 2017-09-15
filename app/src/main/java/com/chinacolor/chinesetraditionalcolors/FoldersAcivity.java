package com.chinacolor.chinesetraditionalcolors;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 通过文件夹管理数据库
 * 一个文件夹名称对应一个标题
 * Created by lingnanmiao on 9/14/17.
 *
 * 注意int是有符号数，parseInt会有坑，0xffffffff颜色不能直接parse为int
 *
 * 文件夹名称同时放入数据库和SharedPreference
 * 存储列名的SP文件名为user_folders,用Set<String> folders存储，与数据库列要保持一致
 *
 */

public class FoldersAcivity extends AppCompatActivity {
    private TextView folderName;
    private ListView listView;
    //用于store user's folder
    public Folder folder;
    public String SPName = "user_folders";
    public String SPItem = "folders";
    //用于query
    public final String color_dir = "content://com.chinacolor.chinesetraditionalcolors.provider/color";
    public Uri uri = Uri.parse(color_dir);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_folders);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.folders_toolbar);
        setSupportActionBar(myToolbar);

        //initialize favoriteFolder
        folderName = (TextView)findViewById(R.id.favorite_folder);
        folderName.setText("favorite");
        listView = (ListView)findViewById(R.id.favorite_list);
        listView.setVisibility(ListView.GONE);

        //initialize userFolder
        //Read from SP
        List<String> folder_list = new ArrayList<>();
        Set<String> folders_name = getSharedPreferences(SPName, MODE_PRIVATE)
                .getStringSet(SPItem, new HashSet<String>());
        if (!folders_name.isEmpty()) {
            for (String name : folders_name) {
                folder_list.add(name);
            }
        }
        folder = new Folder(folder_list);
        List<String> userFolder = folder.getFoldersName();
        if (userFolder.isEmpty()){
            Log.d("FoldersActivity", "没有用户文件夹");
        }else{
            //动态加载用户文件夹视图
            Log.d("FoldersActivity", "正在动态加载视图...");

        }

        // onClickListener
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
     * Get folder's item
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


    /**
     * Overrider ToolBar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_folder, menu);
        return true;
    }

    /**
     * Create new Folder
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_folder_button) {
            String name = folder.CreatenewFolder(FoldersAcivity.this);
            //刷新视图
            if (name != null){
                //保存到本地SP
                SharedPreferences sp = getSharedPreferences(SPName, MODE_PRIVATE);
                Set<String> folders_name = sp.getStringSet(SPItem, new HashSet<String>());
                folders_name.add(name);
                SharedPreferences.Editor editor= sp.edit();
                editor.putStringSet(SPItem, folders_name);
                Log.d("FoldersActivity", "新文件夹名称为"+ name + "已保存");
                //刷新视图
                //
                //
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



