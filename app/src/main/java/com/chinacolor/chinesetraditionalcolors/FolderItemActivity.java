package com.chinacolor.chinesetraditionalcolors;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
 * Created by lingnanmiao on 9/18/17.
 */

public class FolderItemActivity extends AppCompatActivity {
    public TextView tv_title;
    private ListView lv_colorlist;
    static String folderName;
    static String folderType;
    //用于query
    public final String color_dir = "content://com.chinacolor.chinesetraditionalcolors.provider/color";
    public Uri uri = Uri.parse(color_dir);
    List<Color> list_color;
    FolderItemAdapter folderItemAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_folder_item);

        tv_title = (TextView)findViewById(R.id.acivity_folder_item_title);
        lv_colorlist = (ListView) findViewById(R.id.acivity_folder_item_colorlist);
        folderName = getIntent().getStringExtra("folderName");
        folderType = getIntent().getStringExtra("type");
        tv_title.setText(folderName);

        if (folderType.equals("local")){
            if (folderName.equals("favorite")) {
                list_color = getColorItemList(folderName);
                if(!list_color.isEmpty()){
                    folderItemAdapter = new FolderItemAdapter(this, R.layout.item_layout, list_color);
                    lv_colorlist.setAdapter(folderItemAdapter);
                }
            }else {
                list_color = usr_getColorItemList(folderName);
                if(!list_color.isEmpty()){
                    folderItemAdapter = new FolderItemAdapter(this, R.layout.item_layout, list_color);
                    lv_colorlist.setAdapter(folderItemAdapter);
                }
            }
        }else{//folderType.equals("remote")
            list_color = new ArrayList<Color>();
            // 存入list_color，注意list_color的每个元素都是一个Color对象
            folderItemAdapter = new FolderItemAdapter(this, R.layout.item_layout, list_color);
            lv_colorlist.setAdapter(folderItemAdapter);
            remote_color_query();


        }

        lv_colorlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String color_rgb = Integer.toHexString(list_color.get(i).getColorValue()).substring(2);
                copy(color_rgb, FolderItemActivity.this);
            }
        });

        // 删除
        final PopupList popupList = new PopupList(FolderItemActivity.this);
        List<String> longPress = new ArrayList<>();
        longPress.add("删除");
        popupList.bind(lv_colorlist, longPress, new PopupList.PopupListListener() {
            @Override
            public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                return true;
            }

            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                TextView tv_rgb = lv_colorlist.getChildAt(contextPosition).findViewById(R.id.item_rgb);
                final String color_argb = tv_rgb.getText().toString().substring(1);

                if (folderType.equals("local")){
                    // 删除颜色，就是修改数据库
                    int row = 0 ;
                    if(folderName.equals("favorite")){
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("favorite", 0);
                        row = getContentResolver().update(uri, contentValues, "value = ?", new String[]{color_argb});
                        Log.d("MainActivity", "取消收藏中: \n rows = " + row +"\n value = " + color_argb);

                    }else{
                        SQLiteDatabase db = new UserFavorHelper(FolderItemActivity.this, DATABASEINFO.USRDB_NAME, null, 1).getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(folderName, 0);
                        row = db.update(DATABASEINFO.USRTABLE_NAME, contentValues, "value = ?", new String[]{color_argb});
                        Log.d("FolderItemActivity", "颜色删除影响了" + row + "行");
                    }
                    // 检查删除成功没
                    if(row == 1){
                        list_color.remove(contextPosition);
                        Toast.makeText(FolderItemActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(FolderItemActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    // 如果删除成功，执行一下list_color.remove(contextPosition);
                    final int i = contextPosition;
                    remote_color_delete(color_argb, i);
                }

                folderItemAdapter.notifyDataSetChanged();


            }
        });


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

    public static void copy(String content, Context context){
        ClipboardManager cbm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipdata = ClipData.newPlainText("color_rgb", content);
        cbm.setPrimaryClip(mClipdata);
        Toast.makeText(context, "已复制"+ content+"到剪贴板", Toast.LENGTH_SHORT).show();
    }

    public void remote_color_query(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL(ServerInfo.Url+"db_selectall_color.php?name="+folderName);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

//                        Debug.waitForDebugger();
                    InputStream in = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    String content = result.toString();

                    JSONObject jsonObject = new JSONObject(content);
                    if (jsonObject.getInt("success")==1){
                        JSONArray colors = jsonObject.getJSONArray("data");
                        for (int i = 0; i<colors.length(); i++){
                            JSONObject colorobj = colors.getJSONObject(i);
                            long value = Long.parseLong(colorobj.getString("value"), 16);
                            String colorName = colorobj.getString("name");
                            list_color.add(new Color(colorName, new Long(value).intValue()));
                        }
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
                    FolderItemActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            folderItemAdapter.notifyDataSetChanged(); //Update UI
                        }
                    });

                }
            }
        }).start();
    }

    public void remote_color_delete(final String color_argb, final int i){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(Looper.myLooper() == null)
                {
                    Looper.prepare();
                }
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                int success = 0;
                String message = "";
                try{
                    URL url = new URL(ServerInfo.Url+"db_delete_color.php?value="
                            +color_argb+"&folderName="+folderName);
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
                    content = content.replace("\uFEFF", "");
                    JSONObject jsonObject = new JSONObject(content);
                    success = jsonObject.getInt("success");
                    message = jsonObject.getString("message");

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
                    if (success == 1){
                        Toast.makeText(FolderItemActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        FolderItemActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                folderItemAdapter.notifyDataSetChanged();
                                list_color.remove(i);
                            }
                        });

                    }else{
                        Toast.makeText(FolderItemActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        Log.d("FolderItemActivity","远程删除颜色失败"+message);
                    }

                }
                Looper.loop();
            }
        }).start();
    }
}
