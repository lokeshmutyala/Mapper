package com.adjointtechnologies.mapper.commonadmins;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adjointtechnologies.mapper.R;

import java.util.List;

/**
 * Created by lokeshmutyala on 21-03-2018.
 */

public class AgencyInfoAdapter extends RecyclerView.Adapter<AgencyInfoAdapter.MyViewHolder>{
    List<AgencyInfoDataObject> infoDataObjectList;
    Context context;

    public AgencyInfoAdapter(List<AgencyInfoDataObject> infoDataObjectList, Context context) {
        this.infoDataObjectList = infoDataObjectList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_agency_info,parent,false);
        return new AgencyInfoAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.supervisorName.setText(infoDataObjectList.get(position).getSupervisorName());
        holder.supervisorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,CommonSupervisorPage.class);
                intent.putExtra("parent_id",infoDataObjectList.get(position).getSupervisorId());
                intent.putExtra("is_agency",true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.noPeopleWorking.setText(""+infoDataObjectList.get(position).getMapperWorkingNo());
        holder.storesNo.setText(""+infoDataObjectList.get(position).getStoresNo());
        holder.incount.setText(""+infoDataObjectList.get(position).getInsideNo());
        holder.avgStores.setText(""+infoDataObjectList.get(position).getAvgNo());
    }

    @Override
    public int getItemCount() {
        return infoDataObjectList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView supervisorName,noPeopleWorking,storesNo,incount,avgStores;
        public MyViewHolder(View itemView) {
            super(itemView);
            supervisorName =(TextView) itemView.findViewById(R.id.supervisor_name);
            storesNo=(TextView) itemView.findViewById(R.id.today_stores);
            incount=(TextView) itemView.findViewById(R.id.inside_stores);
            noPeopleWorking=(TextView) itemView.findViewById(R.id.mappers_working_today);
            avgStores=(TextView) itemView.findViewById(R.id.avg_stores);
        }
    }
}
