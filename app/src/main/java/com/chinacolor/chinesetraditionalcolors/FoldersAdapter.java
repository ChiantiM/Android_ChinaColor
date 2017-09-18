package com.chinacolor.chinesetraditionalcolors;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Created by lingnanmiao on 9/15/17.
 */

public class FoldersAdapter extends ArrayAdapter<String> {
    private int itemLayoutid;
    private ViewHolder vh;


    public FoldersAdapter(Context context, int layoutid, List<String> objs) {
        super(context, layoutid, objs);
        itemLayoutid = layoutid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String folderName = getItem(position);
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(itemLayoutid, null);
            vh = new ViewHolder();
            vh.tv = (TextView) view.findViewById(R.id.folder_title);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder) view.getTag();
        }
        vh.tv.setText(folderName);
        return view;
    }

    class ViewHolder {
        TextView tv;
    }
}