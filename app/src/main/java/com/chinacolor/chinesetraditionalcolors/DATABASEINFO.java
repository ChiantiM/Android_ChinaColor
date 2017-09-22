package com.chinacolor.chinesetraditionalcolors;

import android.net.Uri;

/**
 * Created by lingnanmiao on 9/18/17.
 */

public class DATABASEINFO {
    public static final String COLORDB_NAME = "Colors.db";
    public static final String FOLDERDB_NAME = "Folders.db";
    public static final String USRDB_NAME = "UserFavor.db";

    public static final String COLORDTABLE_NAME = "Colors";
    public static final String FOLDERTABLE_NAME = "Folders";
    public static final String USRTABLE_NAME = "UserFavor";
    public static final String COLORTABLE_MYCOLOR = "Mycolor";

    public static final String COLOR_DIR = "content://com.chinacolor.chinesetraditionalcolors.provider/color";
    public static final Uri COLOR_URI = Uri.parse(COLOR_DIR);
}
