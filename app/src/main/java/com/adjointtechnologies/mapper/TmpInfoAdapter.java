package com.adjointtechnologies.mapper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adjointtechnologies.mapper.infodata.AdapterClassInfo;
import com.adjointtechnologies.mapper.infodata.MapperData;

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

public class TmpInfoAdapter extends RecyclerView.Adapter<TmpInfoAdapter.MyViewHolder> {
    private List<TmpInfoDataObject> tmpInfoDataObjectList;
    Context context;

    public TmpInfoAdapter(List<TmpInfoDataObject> tmpInfoDataObjectList, Context context) {
        this.tmpInfoDataObjectList = tmpInfoDataObjectList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_tmp_info,parent,false);
        return new TmpInfoAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mapperName.setText(tmpInfoDataObjectList.get(position).getAgentName());
        holder.mapperId.setText(tmpInfoDataObjectList.get(position).getAgentId());
        holder.totalStores.setText(""+tmpInfoDataObjectList.get(position).getTotalStores());
        holder.incount.setText(""+tmpInfoDataObjectList.get(position).getInsideStores());
//        holder.lastSync.setText(""+tmpInfoDataObjectList.get(position).getLastSync());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(tmpInfoDataObjectList.get(position).getLastSync());
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
        return tmpInfoDataObjectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mapperName,mapperId,totalStores,lastSync,incount;
        public MyViewHolder(View itemView) {
            super(itemView);
            mapperName =(TextView) itemView.findViewById(R.id.agent_name);
            totalStores=(TextView) itemView.findViewById(R.id.no_of_stores);
            incount=(TextView) itemView.findViewById(R.id.inside_stores);
            lastSync=(TextView) itemView.findViewById(R.id.last_sync);
            mapperId=(TextView) itemView.findViewById(R.id.mapper_id_tmp_info);
        }
    }

}
