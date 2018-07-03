package com.adjointtechnologies.mapper.commonadmins;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adjointtechnologies.mapper.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by lokeshmutyala on 22-03-2018.
 */

public class SupervisorInfoAdapterTotal extends RecyclerView.Adapter<SupervisorInfoAdapterTotal.MyViewHolder>{
    private List<SupervisorInfoDataObjectTotal> infoDataObjectList;
    Context context;

    public SupervisorInfoAdapterTotal(List<SupervisorInfoDataObjectTotal> infoDataObjectList, Context context) {
        this.infoDataObjectList = infoDataObjectList;
        this.context = context;
    }

    @Override
    public SupervisorInfoAdapterTotal.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_supervisor_info_total,parent,false);
        return new SupervisorInfoAdapterTotal.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SupervisorInfoAdapterTotal.MyViewHolder holder, int position) {
      holder.mapperName.setText(infoDataObjectList.get(position).getMapperName()+"("+infoDataObjectList.get(position).getMapperId()+")");
        holder.totalStores.setText(""+infoDataObjectList.get(position).getTotalStores());
        holder.insideStores.setText(""+infoDataObjectList.get(position).getInsideStores());
        holder.avgWorkHours.setText(infoDataObjectList.get(position).getAvgWorkTime().substring(0,infoDataObjectList.get(position).getAvgWorkTime().indexOf('.')));
    }

    @Override
    public int getItemCount() {
        return infoDataObjectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mapperName,totalStores,insideStores,avgWorkHours;
        public MyViewHolder(View itemView) {
            super(itemView);
            mapperName=(TextView) itemView.findViewById(R.id.mapper_name);
            totalStores=(TextView) itemView.findViewById(R.id.total_stores);
            insideStores=(TextView) itemView.findViewById(R.id.inside_stores);
            avgWorkHours=(TextView) itemView.findViewById(R.id.avg_work_hours);
        }
    }

}
