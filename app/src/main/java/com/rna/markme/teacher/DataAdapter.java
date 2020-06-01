package com.rna.markme.teacher;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rna.markme.R;

import java.util.ArrayList;

public class DataAdapter extends ArrayAdapter<DataModel> {

    Context context;
    ArrayList<DataModel> dataList;

    DataAdapter(Context context, ArrayList<DataModel> dataList,int textviewid){
        super(context,textviewid,dataList);
        this.context=context;
        this.dataList=dataList;
    }
    private class ViewHolder{
        TextView regno;
        TextView rss;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder=null;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.listview_item,null);

            holder=new ViewHolder();
            holder.regno=(TextView)convertView.findViewById(R.id.regno);
            holder.rss=(TextView)convertView.findViewById(R.id.rss);
            convertView.setTag(holder);
        }
        else {
            holder=(ViewHolder)convertView.getTag();
        }

        final  DataModel model=dataList.get(position);
        holder.regno.setText(model.getRegno());
        holder.rss.setText(model.getRss().toString());

        if(model.getDoubt()){
            holder.regno.setTextColor(Color.WHITE);
            holder.rss.setTextColor(context.getResources().getColor(R.color.colorYellow));
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }
        else {
            holder.regno.setTextColor(Color.BLACK);
            holder.rss.setTextColor(Color.BLACK);
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }
}
