package com.chinacolor.chinesetraditionalcolors;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;

/**
 * Created by lingnanmiao on 9/15/17.
 *
 */

public class Folder {

    //注意与数据库里的列属性保持数目一致
    private List<String> foldersName;

    public List<String> getFoldersName() {
        return foldersName;
    }

    public void addFoldersName(String name) {
        this.foldersName.add(name);
    }

    /**
     * 构造，从本地初始化foldersName
     * 使用方法：在Activity中读取SP中的String Set,将数据存入foldersName
     */
    public Folder(List<String> foldersName) {
        this.foldersName = foldersName;
    }

    /**
     * 新建文件夹dial
     * @param context
     * @return null/folder_name
     */
    public String CreatenewFolder(Context context){
        final String returnName = null;

        final AlertDialog mydialog = new AlertDialog.Builder(context).create();
        mydialog.show();
        mydialog.getWindow().setContentView(R.layout.newfolder_dialog);
        mydialog.setView(new EditText(context));
        //加入下面两句以后即可弹出输入法
        mydialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        final EditText newfloder_name = (EditText) mydialog.getWindow().findViewById(R.id.new_folder_name);

        mydialog.getWindow().findViewById(R.id.new_folder_cancel)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydialog.dismiss();
                return;
            }
        });

        mydialog.getWindow().findViewById(R.id.new_folder_OK)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = newfloder_name.getText().toString();
                        if(name.isEmpty()) {
                            Toast.makeText(view.getContext(), "名称不能为空", Toast.LENGTH_SHORT).show();
                        }else {
                            //加入SP
                            foldersName.add(name);
                            //更新数据库
                            SQLiteDatabase db = new ColorHelper(view.getContext(), "Colors.db", null, 1)
                                    .getWritableDatabase();
                            db.execSQL("alter table Colors add column" + name +"text");
                            Toast.makeText(view.getContext(), "创建成功", Toast.LENGTH_SHORT).show();
                        }
                        name = returnName;
                        mydialog.dismiss();
                    }
                });
        return returnName;
    }

}
