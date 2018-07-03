package com.adjointtechnologies.mapper.commonadmins;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adjointtechnologies.mapper.R;

import java.util.List;

/**
 * Created by lokeshmutyala on 22-03-2018.
 */

public class AgencyInfoAdapterTotal extends RecyclerView.Adapter<AgencyInfoAdapterTotal.MyViewHolder>{
    List<AgencyInfoDataObjectTotal> infoDataObjectList;
    Context context;

    public AgencyInfoAdapterTotal(List<AgencyInfoDataObjectTotal> infoDataObjectList, Context context) {
        this.infoDataObjectList = infoDataObjectList;
        this.context = context;
    }

    @Override
    public AgencyInfoAdapterTotal.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_agency_info_total,parent,false);
        return new AgencyInfoAdapterTotal.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AgencyInfoAdapterTotal.MyViewHolder holder, final int position) {
        final AgencyInfoDataObjectTotal dataObjectTotal=infoDataObjectList.get(position);
//        Log.i("agencyinfopage",dataObjectTotal.getSupervisorName());
        holder.supervisorName.setText(dataObjectTotal.getSupervisorName());
        holder.supervisorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,CommonSupervisorPage.class);
                intent.putExtra("parent_id",dataObjectTotal.getSupervisorId());
                intent.putExtra("is_agency",true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.noPeopleWorking.setText(""+dataObjectTotal.getPoeopleNo());
        holder.storesNo.setText(""+dataObjectTotal.getStoresNo());
        holder.incount.setText(""+dataObjectTotal.getInsideNo());
        int avg=0;
        if(dataObjectTotal.getPeopleSum()!=0 && dataObjectTotal.daysSum!=0)
        avg=dataObjectTotal.getStoresSum()/(dataObjectTotal.getPeopleSum()*dataObjectTotal.daysSum);
        holder.avgStores.setText(""+avg);
    }

    @Override
    public int getItemCount() {
        return infoDataObjectList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView supervisorName,noPeopleWorking,storesNo,incount,avgStores;
        public MyViewHolder(View itemView) {
            super(itemView);
            supervisorName =(TextView) itemView.findViewById(R.id.supervisor_name_total);
            storesNo=(TextView) itemView.findViewById(R.id.today_stores_total);
            incount=(TextView) itemView.findViewById(R.id.inside_stores_total);
            noPeopleWorking=(TextView) itemView.findViewById(R.id.mappers_working_today_total);
            avgStores=(TextView) itemView.findViewById(R.id.avg_stores_total);
        }
    }
}
