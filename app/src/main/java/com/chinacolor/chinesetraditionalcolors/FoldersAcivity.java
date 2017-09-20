package com.chinacolor.chinesetraditionalcolors;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;
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
 *
 * 文件夹名称同时放入数据库和Folders.db，不使用ContentProvider, 数据直接在自带的data文件夹下
 * 只有一个列为name,要保持和Colors.db数据库属性的一致性
 *
 * listView嵌套无解，推荐用新的Activity
 *
 * TODO:添加新的Activity
 *
 */

public class FoldersAcivity extends AppCompatActivity {
    private static ListView usrfolderslist;
    //用于store user's folder
    public static Folder folder;
    //用于listView的动态更新
    FoldersAdapter foldersAdapter;
    List<String> list_usrfolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_folders);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.folders_toolbar);
        setSupportActionBar(myToolbar);

        //initialize favoriteFolder
        usrfolderslist= (ListView)findViewById(R.id.folder_name_list);
        folder = new Folder(new ArrayList<String>());
        folder.addFoldersName("favorite");

        //initialize Folders
        //Read from Folders.db to class Folder
        FolderHelper folderHelper = new FolderHelper(this, DATABASEINFO.FOLDERDB_NAME, null, 1);
        SQLiteDatabase db_folders = folderHelper.getReadableDatabase();
        Cursor cursor = db_folders.query(DATABASEINFO.FOLDERTABLE_NAME, null, null, null, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    folder.addFoldersName(cursor.getString(cursor.getColumnIndex("name")));
                }while (cursor.moveToNext());
            }else {Log.d("FoldersActivtiy", "来自USEFAVOR查询：查询成功，无数据");}
        }else {
            Log.d("FoldersActivtiy", "来自USERFAVOR查询：查询失败，Cursor为空");
        }
        if (!cursor.isClosed()){cursor.close();}

        list_usrfolder = folder.getFoldersName();
        if (list_usrfolder.isEmpty()){
            Log.d("FoldersActivity", "没有用户文件夹");
        }else{
            //加载文件夹视图
            foldersAdapter = new FoldersAdapter(this, R.layout.folder_title, list_usrfolder);
            usrfolderslist.setAdapter(foldersAdapter);
        }

        // ListView OnItemClickListener
        usrfolderslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Intent
                String folderName = ((TextView)usrfolderslist.getChildAt(i).findViewById(R.id.folder_title)).getText().toString();
                Log.d("FoldersAcivity:","已点击列表项:" + folderName);
                Intent intent = new Intent(FoldersAcivity.this, FolderItemActivity.class);
                intent.putExtra("folderName", folderName);
                startActivity(intent);
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
        Cursor cursor = getContentResolver().query(DATABASEINFO.COLOR_URI, null, folderName + "= ?", new String[]{"1"}, null);
        //Cursor cursor = getContentResolver().query(uri, new String[]{"favorite"}, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                        colorName = new StringBuilder(cursor.getString(cursor.getColumnIndex("name")));
                        colorValue = new StringBuilder(cursor.getString(cursor.getColumnIndex("value")));
                        long value = Long.parseLong(colorValue.toString(), 16);
                        colorItemList.add(new Color(colorName.toString(), new Long(value).intValue()));
                    //}
                }while (cursor.moveToNext());
                Log.d("FoldersAcivity", "成功查询到数据");
            }else {Log.d("FoldersAcivity","查询成功，无数据");
            }
        }else {Log.d("FoldersActivity", "Cursor为空，查询失败");
        }
        if (!cursor.isClosed()){cursor.close();}

        return colorItemList;
    }


    /**
     * Override ToolBar
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

//            SQLiteDatabase db_usr = new UserFavorHelper(this, DATABASEINFO.USRDB_NAME, null, 1).getWritableDatabase();
//            db_usr.beginTransaction();
//            db_usr.execSQL("alter table "+DATABASEINFO.USRTABLE_NAME+ " add column " + name + "2 integer");
//            db_usr.execSQL("alter table "+DATABASEINFO.USRTABLE_NAME+ " add column " + name + "3 integer;");
//            Log.d("FoldersAcivity.java", "增加"+ name + "列成功");
//            db_usr.endTransaction();
//            db_usr.close();

            if (name != null){
                //刷新视图
                list_usrfolder = folder.getFoldersName();
                foldersAdapter.notifyDataSetChanged();
            }
            return true;
        }else {
            return false;
        }
    }

}



