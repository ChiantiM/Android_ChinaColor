package com.chinacolor.chinesetraditionalcolors;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lingnanmiao on 9/14/17.
 */

public class FolderItemAdapter extends ArrayAdapter<Color> {
    public Typeface color_tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/hanyishoujinshujian.ttf");
    private int itemLayoutid;

    public FolderItemAdapter(Context context, int layoutid, List<Color> objs){
        super(context, layoutid, objs);
        itemLayoutid = layoutid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Color color = getItem(position);
        View view;
        ViewHolder viewHolder; //防每次都获取一次id

        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(itemLayoutid, null);
            viewHolder = new ViewHolder();
            viewHolder.color = (ImageView)view.findViewById(R.id.item_color);
            viewHolder.colorvalueHex = (TextView)view.findViewById(R.id.item_rgb);
            viewHolder.colorName = (TextView)view.findViewById(R.id.item_colorname);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.colorName.setText(color.getColorName());
        viewHolder.colorvalueHex.setText("#" + Integer.toHexString(color.getColorValue()));
        GradientDrawable myGrad = (GradientDrawable)viewHolder.color.getBackground();
        myGrad.setColor(color.getColorValue());

        return view;
    }

    class ViewHolder{
        private ImageView color;
        private TextView colorvalueHex;
        private TextView colorName;

    }
}
