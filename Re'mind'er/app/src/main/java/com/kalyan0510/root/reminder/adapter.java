package com.kalyan0510.root.reminder;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kalyan0510.root.reminder.BatteryBroadcast.person;
import com.kalyan0510.root.reminder.BatteryBroadcast.res;


public class adapter extends BaseAdapter {
    private ArrayList<res> listData;
    private LayoutInflater layoutInflater;
    Context context;
    public adapter(Context aContext, ArrayList<res> listData) {
        Toast.makeText(aContext, "Hello", Toast.LENGTH_SHORT).show();
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        context=aContext;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        System.out.print("####################################################################" +
                "#############################################################################" +
                "#############################################################################" +
                "####################################" +
                "####################################" +
                "#######################");

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView)convertView.findViewById(R.id.img);
            holder.name  = (TextView) convertView.findViewById(R.id.name);
            holder.det  = (TextView) convertView.findViewById(R.id.det);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(position<0)
            return convertView;
        final res li = listData.get(position);
        holder.name.setText(li.name);
        holder.det.setText(li.H+" hours "+li.m+" minutes");
        holder.img.setImageResource(R.mipmap.kalyan);

       /* if(li.getCol()==1){
            holder.from.setBackgroundColor(Color.rgb(74,68,86));
            holder.to.setBackgroundColor(Color.rgb(74,68,86));
            holder.time.setBackgroundColor(Color.rgb(74,68,86));
            holder.bus.setBackgroundColor(Color.rgb(74,68,86));
        }
        else {

        }*/

        //convertView.notifyAll();
      /*  ((LinearLayout)convertView.findViewById(R.id.busitem)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(li.getUsers())
                        .setTitle("Users")
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });*/
        return convertView;
    }

    static class ViewHolder {
        ImageView img;
        TextView name;
        TextView det;


    }
}