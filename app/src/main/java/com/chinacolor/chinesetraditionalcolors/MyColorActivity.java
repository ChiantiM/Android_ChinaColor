package com.chinacolor.chinesetraditionalcolors;

/**
 * Created by ASUS on 2017/9/21.
 */


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.name;
import static android.R.attr.value;
import static java.lang.Integer.parseInt;


public class MyColorActivity extends AppCompatActivity {
    public List<Color> colormine;
    private GridView mygView;
    private ColorAdapter adapter;
    private View bg;
    private List list;
    private TextView color_r;
    private TextView color_g;
    private TextView color_b;
    private TextView title;
    private Typeface tf;
    public int currentpos = 0;
    // private List<Color> colordel;

    //@BindView(R.id.id_grid_main)
    // private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.color_mine);

        bg = findViewById(R.id.mybackground);
        mygView = (GridView) findViewById(R.id.mygview);
        colormine = new ArrayList<Color>();
        title = (TextView)findViewById(R.id.color_mine_title);

        //colormine_init();

        //ActionBars
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.mine_toolbar);
        this.setSupportActionBar(toolbar);

        //设置rgb文字
        color_r = (TextView) findViewById(R.id.color_RR);
        color_g = (TextView) findViewById(R.id.color_GG);
        color_b = (TextView) findViewById(R.id.color_BB);


        //设置typeface
        tf = Typeface.createFromAsset(getAssets(), "fonts/hanyishoujinshujian.ttf");
        title.setTypeface(tf);


/**
 * 数据库显示
 */
        //从数据库里面把数据取出来
        ColorHelper oh = new ColorHelper(this, "Colors.db", null, 1);
        SQLiteDatabase db = oh.getWritableDatabase();
        Cursor cursor2 = db.query(DATABASEINFO.COLORTABLE_MYCOLOR, null, null, null, null, null, null);
        if (cursor2 != null) {
            if (cursor2.moveToFirst()) {
                do {
                    String colorName = cursor2.getString(cursor2.getColumnIndex("name"));
                    String colorValue = cursor2.getString(cursor2.getColumnIndex("value"));
                    long mm=Long.parseLong(colorValue,16);
                    Color p = new Color(colorName, new Long(mm).intValue());
                    colormine.add(p);
                } while (cursor2.moveToNext());
            } else {
                Log.d("MyColorA", "查询成功，无数据");
            }
        }
        if (!cursor2.isClosed()) {
            cursor2.close();
        }
        //把数据显示到屏幕

        adapter = new ColorAdapter(MyColorActivity.this, R.layout.color_item, colormine);
        mygView.setAdapter(adapter);
        if(!colormine.isEmpty()) {
            int[] argb = parseRGB(colormine.get(currentpos).getColorValue());
            color_r.setText(Integer.toString(argb[1]));
            color_g.setText(Integer.toString(argb[2]));
            color_b.setText(Integer.toString(argb[3]));
            bg.setBackgroundColor(colormine.get(currentpos).getColorValue());
        }else
        {
            Log.d("MyColorActivity","空");
        }
        if(!colormine.isEmpty()) {
            title.setText(colormine.get(currentpos).getColorName());
        }else
        {
            title.setText("墨绿");
        }
        /*
        删除
         */
        List<String> longPress = new ArrayList<>();
        longPress.add("删除");
        final PopupList popupList = new PopupList(MyColorActivity.this);
        popupList.bind(mygView, longPress, new PopupList.PopupListListener() {
            @Override
            public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                return true;
            }

            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                TextView tv_title = (TextView) mygView.getChildAt(contextPosition).findViewById(R.id.color_text);
                String name = tv_title.getText().toString();
                Log.d("MyColorAcitvity", "当前删除的为" + name);
                switch (position) {
                    case 0:
                        ColorHelper oh = new ColorHelper(MyColorActivity.this, "Colors.db", null, 1);
                        SQLiteDatabase db = oh.getWritableDatabase();
                        db.beginTransaction();
                        try {
                            db.delete(DATABASEINFO.COLORTABLE_MYCOLOR, "name=?", new String[]{name});

                            if (currentpos == contextPosition) {
                                if (currentpos>0){
                                    crossfade(colormine.get(currentpos).getColorValue(), colormine.get(currentpos-1).getColorValue(), colormine.get(currentpos-1).getColorName());
                                    currentpos -= 1;
                                }else {
                                    long x = Long.parseLong("ff537376", 16);
                                    crossfade(colormine.get(currentpos).getColorValue(),new Long(x).intValue(), "墨绿");
                                }
                            }
                            colormine.remove(contextPosition);
                            adapter.notifyDataSetChanged();
                            db.setTransactionSuccessful();
                        }catch (Exception e){
                            Toast.makeText(contextView.getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                        }finally {
                            db.endTransaction();
                            db.close();
                        }
                        break;
                    default:
                        break;
                }
            }
        });


        //为每节一个item设置监听事件
        mygView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                crossfade(colormine.get(currentpos).getColorValue(), colormine.get(i).getColorValue(), colormine.get(i).getColorName());
                currentpos = i;
            }
        });
    }

    /*
  背景色渐变和文字渐变
   */
    public void crossfade(int startColor, int endColor, String newColorname) {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(bg, "backgroundColor", startColor, endColor);
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
     * 解析int类型RGB
     *
     * @param colorValue
     * @return int[](a, r, g, b)
     */
    public int[] parseRGB(int colorValue) {
        int[] rgb = {0, 0, 0, 0};
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
        getMenuInflater().inflate(R.menu.menu_mine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add:
                LayoutInflater factory = LayoutInflater.from(MyColorActivity.this);
                final View textEntryView = factory.inflate(R.layout.my_alert, null);
                final EditText colorname = (EditText) textEntryView.findViewById(R.id.my_alert_color_text);
                final EditText colorvalue = (EditText) textEntryView.findViewById(R.id.my_alert_value_text);
                AlertDialog.Builder ad1 = new AlertDialog.Builder(MyColorActivity.this);

                ad1.setTitle("添加颜色:");
                //ad1.setIcon(android.R.drawable.ic_dialog_info);
                ad1.setView(textEntryView);
                ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        ColorHelper oh = new ColorHelper(MyColorActivity.this, "Colors.db", null, 1);
                        SQLiteDatabase db = oh.getWritableDatabase();
                        
                        String s = colorvalue.getText().toString();
                        if (s.matches("^[0-9a-fA-F]*$")) {
                            if (s.length() >= 6 && s.length() < 9) {
                                do {
                                    s = "f" + s;
                                } while (s.length() < 8);
                                long vm=Long.parseLong(s,16);
                                int color_value = new Long(vm).intValue();
                                Color x = new Color(colorname.getText().toString(), color_value);
                                //用于检测重复
                                List<Integer> colorvalues = new ArrayList<Integer>();
                                for (Color c : colormine){
                                    colorvalues.add(c.getColorValue());
                                }

                                db.beginTransaction();
                                try {
                                    if (!colorvalues.contains(color_value)) {
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("name", colorname.getText().toString());
                                        contentValues.put("value", s);
                                        db.insert("Mycolor", null, contentValues);

                                        colormine.add(x);
                                        adapter.notifyDataSetChanged();
                                        db.setTransactionSuccessful();
                                    } else {
                                        Toast.makeText(MyColorActivity.this, "已有此颜色", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (Exception e){
                                    Toast.makeText(MyColorActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                                }finally {
                                    db.endTransaction();
                                    db.close();
                                }
                            } else {
                                Toast.makeText(MyColorActivity.this, "请输入十六进制6位RGB值", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(MyColorActivity.this, "RGB值只包含0-9和a-f", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                    }
                });
                ad1.show();// 显示对话框

                break;
            default:
        }
        return true;

        /** int id = item.getItemId();

         *noinspection SimplifiableIfStatement
         *if (id == R.id.action_settings) {
         *   return true;
         * }
         return super.onOptionsItemSelected(item);*/
    }
}

