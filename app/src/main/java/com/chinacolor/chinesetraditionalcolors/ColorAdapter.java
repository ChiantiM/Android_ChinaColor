package com.chinacolor.chinesetraditionalcolors;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lingnanmiao on 9/12/17.
 */

public class ColorAdapter extends ArrayAdapter<Color> {

    public Typeface color_tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/hanyishoujinshujian.ttf");
    private int itemLayoutid;

    public ColorAdapter(Context context, int layoutid, List<Color> objs){
        super(context, layoutid, objs);
        itemLayoutid = layoutid;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Color color = getItem(position);
        View view;
        ViewHolder viewHolder; //防每次都获取一次id


        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(itemLayoutid, null);
            viewHolder = new ViewHolder();
            viewHolder.colorValue = (ImageView) view.findViewById(R.id.color_image);
            viewHolder.colorName = (TextView)view.findViewById(R.id.color_text);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        GradientDrawable myGrad = (GradientDrawable)viewHolder.colorValue.getBackground();
        myGrad.setColor(color.getColorValue());
        viewHolder.colorName.setText(color.getColorName());
        viewHolder.colorName.setTypeface(color_tf);

        return view;

    }


    class ViewHolder{
        ImageView colorValue;
        TextView colorName;
    }
}
