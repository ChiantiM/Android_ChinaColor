package com.chinacolor.chinesetraditionalcolors;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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

    public void removeFoldersName(String name){
        this.foldersName.remove(this.foldersName.indexOf(name));
    }

    /**
     * 构造，从本地初始化foldersName
     * 使用方法：在Activity中读取,将数据存入foldersName
     */
    public Folder(List<String> foldersName) {
        this.foldersName = foldersName;
    }

    /**
     * 新建文件夹dial
     * @param context
     * @return null/folder_name
     */
    private String returnName;
    public String CreatenewFolder(final Context context){
        returnName = null;
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
                        } else if (foldersName.contains(name)) {
                            Toast.makeText(view.getContext(), "名称已被占用", Toast.LENGTH_SHORT).show();
                        } else if (Character.isDigit(name.charAt(0))){
                            Toast.makeText(view.getContext(), "不能以数字开头", Toast.LENGTH_SHORT).show();
                        }else {
                            foldersName.add(name);
                            //改变UserFavor.db


                            SQLiteDatabase db_usr = new UserFavorHelper(view.getContext(),
                                    DATABASEINFO.USRDB_NAME, null, 1).getWritableDatabase();
                            SQLiteDatabase db_folder = new FolderHelper(view.getContext(),
                                    "Folders.db", null, 1).getWritableDatabase();
                            db_usr.beginTransaction();
                            db_folder.beginTransaction();
                            try {
                                //更新color数据库
                                db_usr.execSQL("alter table " + DATABASEINFO.USRTABLE_NAME
                                        + " add " + "column " + name + " INTEGER");
                                Log.d("Folder.java", "增加" + name + "列成功");

                                //更新文件夹数据库。
                                ContentValues cv = new ContentValues();
                                cv.put("name", name);
                                db_folder.insert("Folders", null, cv);
                                Toast.makeText(view.getContext(), "创建\"" + name + "\"成功", Toast
                                        .LENGTH_SHORT).show();
                                db_usr.setTransactionSuccessful();
                                db_folder.setTransactionSuccessful();
                            }catch (Exception e){
                                Toast.makeText(view.getContext(), "创建\"" + name + "\"失败", Toast
                                        .LENGTH_SHORT).show();
                            }finally {
                                db_usr.endTransaction();
                                db_folder.endTransaction();
                                db_usr.close();
                                db_folder.close();
                            }

                            // db_usr.endTransaction();
                        }
                        returnName = name;
                        mydialog.dismiss();
                    }
                });
        //DEBUG
        //returnName = "xxy";
        //foldersName.add("xxy");

        return returnName;
    }

}
