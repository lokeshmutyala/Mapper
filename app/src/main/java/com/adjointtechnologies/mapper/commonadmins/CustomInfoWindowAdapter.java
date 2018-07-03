package com.adjointtechnologies.mapper.commonadmins;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adjointtechnologies.mapper.R;
import com.adjointtechnologies.mapper.StoreData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by lokeshmutyala on 08-11-2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
  private Activity context;

    public CustomInfoWindowAdapter(Activity context){
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.info_window, null);
        Button button=(Button)view.findViewById(R.id.action_window);
        Button navigate=(Button)view.findViewById(R.id.navigate_window);
        TextView store_name=(TextView)view.findViewById(R.id.store_name_window);
        TextView distance=(TextView)view.findViewById(R.id.distance_window);
        store_name.setText(marker.getTitle());
        final StoreData data= (StoreData) marker.getTag();
        if(data!=null){
            distance.setText(""+new java.text.DecimalFormat("#").format(data.getDistance()*1000)+"m"); ///1000)+"k.m");
        }
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data==null){
                    return;
                }
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+data.getLatitude()+","+data.getLongitude()+"("+data.getStore_name()+")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(data==null){
//                    return;
//                }
//                    Intent intent = new Intent(context, Page1.class);
//                    intent.putExtra("storeid", data.getStoreid());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//            }
//        });
        return view;
    }
}
