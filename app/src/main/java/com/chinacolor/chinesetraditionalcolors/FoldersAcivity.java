package com.chinacolor.chinesetraditionalcolors;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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
 *
 */

public class FoldersAcivity extends AppCompatActivity {
    private static ListView usrfolderslist;
    private static ListView remotefolderlist;
    //用于store user's folder
    public static Folder folder;
    public static Folder remotefolders;
    //用于listView的动态更新
    FoldersAdapter foldersAdapter;
    FoldersAdapter remotefoldersAdapter;
    List<String> list_usrfolder;
    List<String> list_remotefolder;


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

        //initialize custom folders
        //Read from Folders.db to class Folder
        final FolderHelper folderHelper = new FolderHelper(this, DATABASEINFO.FOLDERDB_NAME, null, 1);
        final SQLiteDatabase db_folders = folderHelper.getReadableDatabase();
        local_folder_query(db_folders);


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
                intent.putExtra("type", "local");
                startActivity(intent);
            }
        });

        List<String> longPress = new ArrayList<>();
        longPress.add("删除");
        final PopupList popupList = new PopupList(FoldersAcivity.this);
        popupList.bind(usrfolderslist, longPress, new PopupList.PopupListListener(){
            @Override
            public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                return true;
            }

            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                TextView tv_title = (TextView)usrfolderslist.getChildAt(contextPosition).findViewById(R.id.folder_title);
                String name = tv_title.getText().toString();
                Log.d("FolderAcitvity", "当前删除的为"+name);
                local_folder_delete(contextView, contextPosition, position, name, folderHelper, db_folders, popupList);
            }
        });

        /* ============ Remote Folders ==============*/
        remotefolderlist = (ListView)findViewById(R.id.remote_folder_list);
        remotefolders = new Folder(new ArrayList<String>());

        list_remotefolder = remotefolders.getFoldersName();
        if (list_remotefolder.isEmpty()){
            Log.d("FoldersActivity", "没有Remote文件夹");
        }
        remotefoldersAdapter = new FoldersAdapter(this, R.layout.folder_title, list_remotefolder);
        remotefolderlist.setAdapter(remotefoldersAdapter);

        // 远程收藏夹查询
        remote_folder_query();

        remotefolderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String folderName = ((TextView)remotefolderlist.getChildAt(i).findViewById(R.id.folder_title)).getText().toString();
                Log.d("FoldersAcivity:","已点击列表项:" + folderName);
                Intent intent = new Intent(FoldersAcivity.this, FolderItemActivity.class);
                intent.putExtra("folderName", folderName);
                intent.putExtra("type", "remote");
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

    public void local_folder_query(SQLiteDatabase db_folders){
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
        db_folders.close();
    }

    public void local_folder_delete(View contextView, int contextPosition, int position, String name,
                                   FolderHelper folderHelper, SQLiteDatabase db_folders, final PopupList popupList){
        if (contextPosition != 0) {
            switch (position) {
                case 0:
                    //List<String> folders = folder.getFoldersName();
                    //usrfolderslist.getChildAt(contextPosition).setVisibility(View.GONE);

                    //sqlite删除列
                    SQLiteDatabase db_usr = new UserFavorHelper(contextView.getContext(), DATABASEINFO.USRDB_NAME, null, 1)
                            .getWritableDatabase();
                    SQLiteDatabase db = folderHelper.getWritableDatabase();;
                    db_usr.beginTransaction();
                    try {
                        int folder_list_size = list_usrfolder.size();
                        StringBuilder create_java = new StringBuilder("CREATE TABLE copied AS SELECT name, value");
                        if (folder_list_size - 1 > 1) {
                            create_java.append(", ");
                            for (int i = 1; i < folder_list_size;i++) {//不包括favorite
                                if (i != contextPosition) {
                                    create_java.append(list_usrfolder.get(i));
                                    if (i != folder_list_size - 2 && contextPosition==folder_list_size-1) {
                                        create_java.append(", ");
                                    }else if(contextPosition != folder_list_size-1 && i != folder_list_size - 1){
                                        create_java.append(", ");
                                    }
                                }
                            }
                        }
                        create_java.append(" from " + DATABASEINFO.USRTABLE_NAME);
                        String str = create_java.toString();

                        db_usr.execSQL("DROP TABLE IF EXISTS copied");
                        db_usr.execSQL(str);
                        db_usr.execSQL("DROP TABLE IF EXISTS " + DATABASEINFO.USRTABLE_NAME);
                        db_usr.execSQL("ALTER TABLE copied RENAME TO "+ DATABASEINFO.USRTABLE_NAME);

                        //Folders.db删除*/
                        db.beginTransaction();
                        try {
                            db.delete(DATABASEINFO.FOLDERTABLE_NAME, "name = ?", new String[]{name});
                            db.setTransactionSuccessful();
                            db_usr.setTransactionSuccessful();
                            Toast.makeText(contextView.getContext(),"删除" + name + "成功",Toast.LENGTH_SHORT).show();
                            Log.d("FoldersAcitvity", "删除" + name + "成功");
                            popupList.getIndicatorView().setVisibility(View.GONE);
                            folder.removeFoldersName(name);
                        } catch (Exception e) {
                            Toast.makeText(contextView.getContext(), "delete Failed", Toast.LENGTH_SHORT).show();
                            Log.d("FoldersAcitvity", "从Folders删除失败");
                        } finally {
                            db.endTransaction();
                            db.close();
                        }

                    }catch (Exception e){
                        Toast.makeText(contextView.getContext(), "delete Failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        Log.d("FoldersAcitvity", "从UserFavor删除"+name+"失败");
                    }finally {
                        db_usr.endTransaction();
                        db_usr.close();
                    }

                    if (db_folders.isOpen()){
                        db_folders.close();
                    }
                    if (db.isOpen()){
                        db.close();
                    }
                    foldersAdapter.notifyDataSetChanged(); //folder.removeFoldersName(name)时删除了list_folder指向的区域

                    break;
                default:
                    break;
            }
        }else {
            Toast.makeText(contextView.getContext(), "不能删除默认文件夹", Toast.LENGTH_SHORT).show();
        }
    }

    public void remote_folder_query(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL(ServerInfo.Url + "db_selectall_folder.php");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    InputStream in = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    String content = result.toString();

                    JSONObject jsonObject = new JSONObject(content);
                    if (jsonObject.getInt("success") == 1){
                        JSONArray folders = jsonObject.getJSONArray("data");
                        for (int i = 0; i < folders.length(); i++){
                            try{
                                JSONObject folder = folders.getJSONObject(i);
                                String name = folder.getString("name");
                                remotefolders.addFoldersName(name);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }finally {

                            }
                        }
                    }
                    else{
                        // TODO：处理状态
                    }

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e){
                    e.printStackTrace();
                }finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {//关闭连接
                        connection.disconnect();
                    }
                    FoldersAcivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            remotefoldersAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        }).start();
    }


}



