package com.adjointtechnologies.mapper.commonadmins;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adjointtechnologies.mapper.R;
import com.adjointtechnologies.mapper.TmpInfoDataObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by lokeshmutyala on 18-03-2018.
 */

public class SupervisorInfoAdapter extends RecyclerView.Adapter<SupervisorInfoAdapter.MyViewHolder> {
    private List<SupervisorInfoDataObject> infoDataObjectList;
    Context context;

    public SupervisorInfoAdapter(List<SupervisorInfoDataObject> infoDataObjectList, Context context) {
        this.infoDataObjectList = infoDataObjectList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_supervisor_info,parent,false);
        return new SupervisorInfoAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mapperName.setText(infoDataObjectList.get(position).getMapperName()+"("+infoDataObjectList.get(position).getMapperId()+")");
        holder.workingTime.setText(infoDataObjectList.get(position).getWorkingTime());
        holder.totalStores.setText(""+infoDataObjectList.get(position).getTotalStores());
        holder.incount.setText(""+infoDataObjectList.get(position).getInsideStores());
//        holder.lastSync.setText(""+tmpInfoDataObjectList.get(position).getLastSync());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(infoDataObjectList.get(position).getLastSyncTime());
            calendar.setTime(date);
            calendar.add(Calendar.HOUR,5);
            calendar.add(Calendar.MINUTE,30);
            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
//            sdf1.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String result=sdf1.format(calendar.getTime());
            holder.lastSync.setText(result);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("Adapterclass","exception ="+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return infoDataObjectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mapperName,totalStores,lastSync,incount,workingTime;
        public MyViewHolder(View itemView) {
            super(itemView);
            mapperName =(TextView) itemView.findViewById(R.id.agent_name_supervisor);
            totalStores=(TextView) itemView.findViewById(R.id.no_of_stores_supervisor);
            incount=(TextView) itemView.findViewById(R.id.inside_stores_supervisor);
            lastSync=(TextView) itemView.findViewById(R.id.last_sync_supervisor);
            workingTime=(TextView) itemView.findViewById(R.id.working_time_supervisor);
        }
    }

}
