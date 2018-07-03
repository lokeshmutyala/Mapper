package com.adjointtechnologies.mapper.adapterclasses;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.adjointtechnologies.mapper.HydAuditPage;
import com.adjointtechnologies.mapper.ImagesActivity;
import com.adjointtechnologies.mapper.ProductApplication;
import com.adjointtechnologies.mapper.R;
import com.adjointtechnologies.mapper.StoreData;
import com.adjointtechnologies.mapper.commonadmins.CommonAuditPage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by lokeshmutyala on 31-10-2017.
 */

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewHolder>{

    private final String TAG="adapterclass";
    private List<StoreData> storeDataList;
    private ReactiveEntityStore<Persistable> data;
    private PopupWindow popupWindow;
    Application application;
    int selecteditem=-1;
    Context context;
    public AdapterClass(List<StoreData> storeDataList , Context context,Application application) {
        this.storeDataList = storeDataList;
        this.context=context;
        this.application=application;
        data= ((ProductApplication)application).getData();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        switch (storeDataList.get(position).getSurveyStatus()){
            case -1:
                holder.itemView.setBackgroundColor(Color.BLUE);
//                mMap.addMarker(new MarkerOptions().position(storeList.get(i).getLatLng()).title(storeList.get(i).getStore_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).setTag(storeList.get(i));
                break;
            case 4:
                holder.itemView.setBackgroundColor(Color.YELLOW);
//                mMap.addMarker(new MarkerOptions().position(storeList.get(i).getLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title(storeList.get(i).getStore_name())).setTag(storeList.get(i));
                break;
            case 5:
                holder.itemView.setBackgroundColor(Color.GREEN);
//                mMap.addMarker(new MarkerOptions().position(storeList.get(i).getLatLng()).title(storeList.get(i).getStore_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(storeList.get(i));
                break;
            default:
                if(storeDataList.get(position).isComplete()){
                    holder.itemView.setBackgroundColor(Color.YELLOW);
//                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(storeList.get(i).getLatitude(), storeList.get(i).getLongitude())).title(storeList.get(i).getStore_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
//                    marker.setTag(storeList.get(i));
                }else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
//                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(storeList.get(i).getLatitude(), storeList.get(i).getLongitude())).title(storeList.get(i).getStore_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                    marker.setTag(storeList.get(i));
                }
                break;
        }
        if(selecteditem==position ){
                holder.navigate.setVisibility(View.VISIBLE);
            holder.landmark.setVisibility(View.VISIBLE);
            holder.landmark.setText("Landmark : "+storeDataList.get(position).getLandmark());
            holder.mobile.setVisibility(View.VISIBLE);
            holder.mobile.setText("Mobile : "+storeDataList.get(position).getMobile());
            holder.category.setVisibility(View.VISIBLE);
            holder.category.setText("Category : "+storeDataList.get(position).getCategory());
            holder.showimage.setVisibility(View.VISIBLE);
            holder.action.setVisibility(View.VISIBLE);
            holder.showimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent=new Intent(context,ImagesActivity.class);
//                    intent.putExtra("storeid",storeDataList.get(position).getStoreid());
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
                    showImage(storeDataList.get(position));
                }
            });
                holder.navigate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(context, "button clicked", Toast.LENGTH_SHORT).show();
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+storeDataList.get(position).getLatitude()+","+storeDataList.get(position).getLongitude()+"("+storeDataList.get(position).getStore_name()+")");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(mapIntent);
                        }
                    }
                });
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    view.setEnabled(false);

//                    if(storeDataList.get(position).getDistance()<100 && storeDataList.get(position).getDistance()!=0) {
//                            Intent intent = new Intent(context, AeEditPage.class);
//                            intent.putExtra("storeid", storeDataList.get(position).getStoreid());
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent);
                        ((CommonAuditPage)context).performAction(storeDataList.get(position));
//                    }else {
//                        Toast.makeText(context,"You Are 100m Away From Store, Move Around To Refresh Distance",Toast.LENGTH_SHORT).show();
//                    }
                }
            });
        }else {
            holder.action.setEnabled(true);
            holder.navigate.setVisibility(View.GONE);
            holder.showimage.setVisibility(View.GONE);
            holder.action.setVisibility(View.GONE);
            holder.landmark.setVisibility(View.GONE);
            holder.mobile.setVisibility(View.GONE);
            holder.category.setVisibility(View.GONE);
        }
        holder.storeName.setText(storeDataList.get(position).getStore_name());
        holder.distance.setText(""+new DecimalFormat("#").format(storeDataList.get(position).getDistance()*1000));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecteditem=position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView storeName,distance,landmark,mobile,category;
        Button navigate,showimage,action;
        LinearLayout row;
        public MyViewHolder(View itemView) {
            super(itemView);
            storeName=(TextView)itemView.findViewById(R.id.store_name);
            distance=(TextView)itemView.findViewById(R.id.distance);
            navigate=(Button)itemView.findViewById(R.id.navigate);
            showimage=(Button)itemView.findViewById(R.id.show_pic);
            action=(Button)itemView.findViewById(R.id.action);
            row=(LinearLayout)itemView.findViewById(R.id.row);
            landmark=(TextView)itemView.findViewById(R.id.list_landmark);
            mobile=(TextView)itemView.findViewById(R.id.mobile_list);
            category=(TextView)itemView.findViewById(R.id.list_category);
        }
    }

    private void showImage(StoreData tag){
        if(tag.getSurveyStatus()==-1 || tag.getSurveyStatus()==4 || tag.getSurveyStatus()==5){
            Toast.makeText(context,"No Image For Auto Store",Toast.LENGTH_SHORT).show();
            return;
        }
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView=inflater.inflate(R.layout.image_popup,null);
        popupWindow=new PopupWindow(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(Build.VERSION.SDK_INT>=21){
            popupWindow.setElevation(5.0f);
        }
        final ProgressBar popUpProgress=(ProgressBar) popUpView.findViewById(R.id.popup_progress);
        final ImageView popUpImage=(ImageView) popUpView.findViewById(R.id.popup_image);
        final String url="https://firebasestorage.googleapis.com/v0/b/storechecker-9a5cb.appspot.com/o/images%2F"+tag.getStoreid()+"-outer.jpeg?alt=media&token=a630e4ae-f7f2-4a25-af9f-e98685b0bd03";
        final String url2="https://firebasestorage.googleapis.com/v0/b/storechecker-9a5cb.appspot.com/o/images%2F"+tag.getStoreid()+".jpeg?alt=media&token=a630e4ae-f7f2-4a25-af9f-e98685b0bd03";
        Glide.with(context).load(url).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                popUpProgress.setVisibility(View.GONE);
                Log.i(TAG,"image exception="+e.toString()+" url="+url);
                Picasso.with(context).load(url2).error(R.drawable.error).into(popUpImage);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                popUpProgress.setVisibility(View.GONE);
                return false;
            }
        }).into(popUpImage);
//                        Picasso.with(getApplicationContext()).load(url).error(R.drawable.error).into(popUpImage);
        Button closeButton = (Button) popUpView.findViewById(R.id.close_popup);
        TextView storeName=(TextView) popUpView.findViewById(R.id.popup_store_name);
        TextView landmark=(TextView) popUpView.findViewById(R.id.landmark_popup);
        TextView mapper=(TextView) popUpView.findViewById(R.id.mapper_popup);
        storeName.setText("Store Name:"+tag.getStore_name());
        landmark.setText("Landmark:"+tag.getLandmark());
        mapper.setText("Mapper Id:"+tag.getMapperId());
        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER,0,0);
    }
}
