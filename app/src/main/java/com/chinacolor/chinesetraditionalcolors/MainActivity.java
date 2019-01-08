package com.chinacolor.chinesetraditionalcolors;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 已实现：
 * 变换背景色功能
 * Text淡入淡出
 * rgb解析
 *
 * 注意：
 * 在插入，删除，移动时，保持gview与colorlist的顺序一致
 * 保持colorValue和colorname数目一致
 *
 * ??? 为什么catch块不起作用，都是用if处理的。
 *
 */
public class MainActivity extends AppCompatActivity {
    private View bg;
    private GridView gView;
    private TextView title;
    private Typeface tf;
    private TextView color_r;
    private TextView color_g;
    private TextView color_b;
    private ImageView favor;
    private View ll_title;


    public int currentColorpos = 0;
    public static final int[] colorValue = {0xff973444, 0xffcc3536, 0xffc43739,0xffdd3b44,0xffdc143c,
    0xffeacdd1, 0xffbb1c33, 0xff89303f, 0xffa54358, 0xff674950,
    0xff643441, 0xff79485a, 0xff793d56, 0xff9c6680, 0xff3e3c3d,
    0xffa71368, 0xffeea5d1, 0xffa22076, 0xffc3a6cb, 0xffab96c5,
    0xff4e1892, 0xff857e95, 0xffc4c3cb, 0xff1f3696, 0xff1b54f2,
    0xff25386b, 0xff43454a, 0xff0041a5, 0xff3c5e91, 0xff546b83,
    0xff455667, 0xff17507d, 0xff2578b5, 0xff31678d, 0xff276893,
    0xff2b5e7d, 0xff6493af, 0xff37444b, 0xff4f5355, 0xff93a2a9,
    0xff507883, 0xff2ec3e7, 0xff5d828a, 0xff7ba1a8, 0xff748a8d};
    public String[] colorname = {"玫瑰红", "艳红", "猩红", "银朱","洋红"
    ,"浅血牙","月季红", "枣红", "紫粉","茄皮紫",
    "深烟红", "雪紫", "玫瑰灰", "洋葱紫", "元青",
    "品红", "紫薇花", "牵牛紫", "紫水晶", "浅石英紫",
    "柏坊灰蓝", "紫藤灰", "浅藤紫", "宝蓝", "靛蓝",
    "藏蓝", "粗晶皂", "孔雀蓝", "浅海昌蓝", "花青",
    "鹊灰", "海蓝", "深竹月", "绒蓝", "北京毛蓝",
    "沙青", "钻蓝", "铁灰", "红皂", "正灰",
    "玉石蓝", "天青", "灰蓝", "春蓝", "织锦灰"};
    public List<Color> colorList;

    public final String color_dir = "content://com.chinacolor.chinesetraditionalcolors.provider/color";
    public Uri uri = Uri.parse(color_dir);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //initialize
        bg = findViewById(R.id.background);
        gView = (GridView)findViewById(R.id.gview);
        favor = (ImageView)findViewById(R.id.favor);
        colorList = new ArrayList<Color>();
        colorList_init();
        title = (TextView)findViewById(R.id.color_title);
        title.setText(colorname[currentColorpos]);
        ll_title  = (LinearLayout)findViewById(R.id.mainactivity_title);

        //ActionBars
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        //设置rgb文字
        color_r = (TextView)findViewById(R.id.color_R);
        color_g = (TextView) findViewById(R.id.color_G);
        color_b = (TextView) findViewById(R.id.color_B);
        int[] argb = parseRGB(colorValue[currentColorpos]);
        color_r.setText(Integer.toString(argb[1]));
        color_g.setText(Integer.toString(argb[2]));
        color_b.setText(Integer.toString(argb[3]));
        bg.setBackgroundColor(colorValue[currentColorpos]);

        //设置typeface
        tf = Typeface.createFromAsset(getAssets(),"fonts/hanyishoujinshujian.ttf");
        title.setTypeface(tf);

        //初始化收藏图标
        Cursor cursor = getContentResolver().query(uri, new String[]{"favorite"}, "value = ?", new String[]{Integer.toHexString(colorValue[currentColorpos])}, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(cursor.getColumnIndex("favorite")) == 1) {
                favor.setBackgroundResource(R.drawable.icon_1);
                Log.d("MainAcitivity", "检测到收藏");
            }
        }

        //设置gView适配器
        ColorAdapter adapter = new ColorAdapter(MainActivity.this, R.layout.color_item, colorList);
        gView.setAdapter(adapter);

        //为每节一个item设置监听事件
        gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                crossfade(colorValue[currentColorpos], colorValue[i], colorname[i]);
                //isFavor
                Cursor cursor = getContentResolver().query(uri, new String[]{"favorite"}, "value = ?", new String[]{Integer.toHexString(colorValue[i])}, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        if (cursor.getInt(cursor.getColumnIndex("favorite")) == 1) {
                            favor.setBackgroundResource(R.drawable.icon_1);
                            Log.d("MainAcitivity", "检测到收藏");
                        }else {
                            favor.setBackgroundResource(R.drawable.icon_0);
                            Log.d("MainAcitivity", "检测到未收藏");
                        }
                    } else {
                        favor.setBackgroundResource(R.drawable.icon_0);
                        Log.d("MainAcitivity", "查询成功，无数据（无法移动到First）");
                    }
                }else{Log.d("MainAcitivity", "Cursor为空，查询失败");}

                currentColorpos = i;
            }
        });

        // favoraite button监听
        favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = getContentResolver().query(uri, null, "value = ?", new String[]{Integer.toHexString(colorValue[currentColorpos])}, null);
                if (cursor.moveToFirst()){
                    //if (cursor.moveToFirst()) {
                        int isFavor = cursor.getInt(cursor.getColumnIndex("favorite"));
                        String value = cursor.getString(cursor.getColumnIndex("value"));
                        if (isFavor == 1){
                            favor.setBackgroundResource(R.drawable.icon_0);
                            //update新的数据
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("favorite", 0);
                            int rows = getContentResolver().update(uri, contentValues, "value = ?", new String[]{value});
                            Log.d("MainActivity", "取消收藏成功: \n rows = " + rows +"\n value = " + value);

                        }else{
                            favor.setBackgroundResource(R.drawable.icon_1);
                            //update
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("favorite", 1);
                            int rows = getContentResolver().update(uri, contentValues, "value = ?", new String[]{value});
                            Log.d("MainActivity", "添加收藏成功: \n rows = " + rows +"\n value = " + value);
                        }
                    //}
                }
                else {
                    favor.setBackgroundResource(R.drawable.icon_1);
                    //insert
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("value", Integer.toHexString(colorValue[currentColorpos]));
                    contentValues.put("name", colorname[currentColorpos]);
                    contentValues.put("favorite", 1);
                    Uri newuri = getContentResolver().insert(uri, contentValues);
                    Log.d("MainActivity", "新增到收藏: \n newuri = " + newuri
                            + "\n value = " + Integer.toHexString(colorValue[currentColorpos])
                            + "\n name = " + colorname[currentColorpos]);
                }

            }
        });

        ll_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = Integer.toHexString(colorValue[currentColorpos]).substring(2);
                copy(content, MainActivity.this);
            }
        });

    }

    /*
    背景色渐变和文字渐变
     */
    public void crossfade(int startColor, int endColor, String newColorname){
        ValueAnimator colorAnim = ObjectAnimator.ofInt(bg,"backgroundColor", startColor, endColor);
        colorAnim.setDuration(500);
        colorAnim.setEvaluator(new ArgbEvaluator());

        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(title, "alpha", 1f, 0f);
        fadeAnim.setDuration(500);

        AnimatorSet crossFador = new AnimatorSet();
        crossFador.play(colorAnim).with(fadeAnim);
        crossFador.start();

        title.setText(newColorname);
        //十进制变换rgb
        int[] argb = parseRGB(endColor);
        color_r.setText(Integer.toString(argb[1]));
        color_g.setText(Integer.toString(argb[2]));
        color_b.setText(Integer.toString(argb[3]));


        ValueAnimator fadeinAnim = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f);
        fadeinAnim.setDuration(500);
        fadeinAnim.start();

    }

    /**
     * 初始化颜色数据
     */
    public void colorList_init(){
        Color color;
        for (int i = 0; i<colorValue.length; i++){
            color = new Color(colorname[i], colorValue[i]);
            colorList.add(color);
        }
    }

    /**
     * 解析int类型RGB
     * @param colorValue
     * @return int[](a, r, g, b)
     */
    public int[] parseRGB(int colorValue){
        int[] rgb = {0,0,0,0};
        rgb[0] = (colorValue >> 24) & 0xff; //a
        rgb[1] = (colorValue >> 16) & 0xff; //r
        rgb[2] = (colorValue >> 8) & 0xff;//g
        rgb[3] = (colorValue) & 0xff; //b
        return rgb;
    }

    /**
     * Actionbar布局填充
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Overflow的选项监听
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            Intent toFoldersActivity = new Intent(MainActivity.this, FoldersAcivity.class);
            startActivity(toFoldersActivity);
            return true;
        }
        if(id==R.id.action_color){
            Intent intent = new Intent(MainActivity.this, MyColorActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_addTo){
            //query local folders
            final List<String> names = new ArrayList<>();
            local_folder_query(names);
            final int localFolders_length = names.size();


            //dialog
            final AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.show();
            dialog.getWindow().setContentView(R.layout.dialog_addto);
            ListView lv = (ListView)dialog.getWindow().findViewById(R.id.dialog_addto_lv);
            final FoldersAdapter adapter  = new FoldersAdapter(this, R.layout.folder_title, names);
            lv.setAdapter(adapter);

            // 远程收藏夹查询
            remote_folder_query(names, adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i<localFolders_length){
                        local_add_to_folder(names, i);
                    }else{
                        // 把当前颜色存到当前选中的远程的收藏夹里，也就是表里的收藏标记置1.
                        final int folderindex = i;
                        remote_add_to_folder(names,folderindex,adapter);
                    }


                    dialog.dismiss();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    public static void copy(String content, Context context){
        ClipboardManager cbm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipdata = ClipData.newPlainText("color_rgb", content);
        cbm.setPrimaryClip(mClipdata);
        Toast.makeText(context, "已复制"+ content+"到剪贴板", Toast.LENGTH_SHORT).show();
    }

    private void local_folder_query(final List<String> names){
        SQLiteDatabase db = new UserFavorHelper(this, DATABASEINFO.FOLDERDB_NAME, null, 1).getReadableDatabase();
        final Cursor cursor= db.query(DATABASEINFO.FOLDERTABLE_NAME, null, null, null, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    names.add(name);
                }while (cursor.moveToNext());
                Log.d("Main activity", "查询成功");
            }else {Log.d("Main activity", "查询成功，无数据");}
        }else {Log.d("Main activity", "Cursor为空");}
        cursor.close();
    }

    private void remote_folder_query(final List<String> names, final FoldersAdapter adapter){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL(ServerInfo.Url+"db_selectall_folder.php");
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
                                names.add(name);
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
                    // TODO：需要选择一个Context，dialog是不是一个thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        }).start();
    }

    private void remote_add_to_folder(final List<String> names, final int folderindex, final FoldersAdapter adapter){
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
                try{
                    URL url = new URL(ServerInfo.Url+"db_add_color.php?name="
                            +colorname[currentColorpos]+"&value="+Integer.toHexString(colorValue[currentColorpos])
                            +"&folderName="+names.get(folderindex));
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
                    String message = jsonObject.getString("message");
                    if (success == 1){
                        Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if (message.matches("(.*)Duplicate(.*)")){
                            Toast.makeText(MainActivity.this, "已存在", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (MalformedURLException e){
                    // TODO: 异常处理
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

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                Looper.loop();
            }
        }).start();

    }

    private void local_add_to_folder(final List<String> names, int i){
        SQLiteDatabase db = new UserFavorHelper(MainActivity.this, DATABASEINFO.USRDB_NAME, null, 1)
                .getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", colorname[currentColorpos]);
        String color_rgb = Integer.toHexString(colorValue[currentColorpos]);
        contentValues.put("value", color_rgb);
        String foldername = names.get(i);
        contentValues.put(foldername, 1); // List Name

        try{
            Cursor cursor1 = db.query(DATABASEINFO.USRTABLE_NAME, new String[]{"value", foldername}, "value = ?", new String[]{color_rgb}, null, null, null);
            if (!cursor1.moveToFirst()){
                long issuccess = db.insert(DATABASEINFO.USRTABLE_NAME, null, contentValues);
                if (issuccess != -1){
                    Toast.makeText(MainActivity.this, "已添加到"+foldername, Toast.LENGTH_SHORT).show();
                }
            } else { // 已经存在更新
                contentValues = new ContentValues();
                contentValues.put(foldername, 1);
                int row = db.update(DATABASEINFO.USRTABLE_NAME, contentValues, "value = ?", new String[]{color_rgb});
                if (row == 1) {
                    if (cursor1.getInt(cursor1.getColumnIndex(foldername)) == 1){
                        Toast.makeText(MainActivity.this, "已存在"+foldername+"中", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        Log.d("MainAcitvity", "再次收藏成功");
                    }
                }else {
                    Log.d("MainActivtiy","添加失败，返回了" + row + "行");
                }
            }
            cursor1.close();
        }catch (android.database.sqlite.SQLiteConstraintException e){
            // 这里不知道为什么执行不到
            e.printStackTrace();
            Log.d("MainActivtity", "插入失败！!" + e.getMessage());
        }finally {
            db.close();
        }
    }
}


