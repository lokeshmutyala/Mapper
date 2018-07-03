package com.adjointtechnologies.mapper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adjointtechnologies.mapper.adapterclasses.AdapterClass;
import com.adjointtechnologies.mapper.adapterclasses.AdapterClassHyd;
import com.adjointtechnologies.mapper.commonadmins.CommonAuditPage;
import com.adjointtechnologies.mapper.commonadmins.CustomInfoWindowAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HydAuditPage extends AppCompatActivity implements OnMapReadyCallback{

    private final String TAG="hydauditpage";
    LocationTracker tracker;
    Location lastLocation = null;
    private GoogleMap mMap;
    private boolean isMapReady=false;
    private ReactiveEntityStore<Persistable> data;
//    private List<LatLng> polygon;
//    Polygon polygon;
    FloatingActionButton refresh;
//    RadioGroup routePoint;
    List<StoreData> storeList=new ArrayList<>();
    Marker clickedMarker=null;
    FloatingActionButton picture,navigation,addStore;
    private PopupWindow popupWindow;
    FrameLayout rootlayout;
    private boolean isAudit=false;
    IOSDialog iosDialog;
    private boolean isMappable=false;
    private Button listview,mapview;
    RecyclerView recyclerView;
    AdapterClassHyd adapterClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_audit_page);
        rootlayout=(FrameLayout) findViewById(R.id.rootlayout);
        iosDialog=new IOSDialog.Builder(HydAuditPage.this).setTitle("Updating Status").setCancelable(false)
        .build();
        if(ProductApplication.surveyId != ConstantValues.COMMON_MAPPING && ProductApplication.surveyId != ConstantValues.COMMON_AUDIT){
            isMappable=true;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        tracker = new LocationTracker(getApplicationContext(), new TrackerSettings().setTimeBetweenUpdates(1).setMetersBetweenUpdates(1).setUseNetwork(true).setUseGPS(true)) {
            @Override
            public void onLocationFound(@NonNull Location location) {
                lastLocation=location;

            }

            @Override
            public void onTimeout() {
                Toast.makeText(getApplicationContext(),"Unable To Get Your Location",Toast.LENGTH_SHORT).show();
            }
        };
        data=((ProductApplication)getApplication()).getData();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapterClass = new AdapterClassHyd(storeList, HydAuditPage.this,getApplication());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterClass);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_audit);
        mapFragment.getMapAsync(this);
//        routePoint=(RadioGroup) findViewById(R.id.route_point);
//        routePoint.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
//                if(i==R.id.route){
//                    drawRoute();
//                }else if(i==R.id.point){
//                    drawPoint();
//                }
//            }
//        });
        addStore=(FloatingActionButton) findViewById(R.id.add_new_store);
        if(getIntent().getBooleanExtra("isAudit",false)){
            isAudit=true;
            addStore.setVisibility(View.VISIBLE);
        }
        addStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),AuditPage.class);
                intent.putExtra("isAudit",true);
                startActivity(intent);
            }
        });
        refresh=(FloatingActionButton) findViewById(R.id.refresh_audit);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
                clickedMarker=null;
                picture.setVisibility(View.GONE);
                navigation.setVisibility(View.GONE);
            }
        });
        navigation=(FloatingActionButton) findViewById(R.id.navigate_list);
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo();
            }
        });
        picture=(FloatingActionButton) findViewById(R.id.request_image_list);
        listview=(Button)findViewById(R.id.listview);
        mapview=(Button)findViewById(R.id.mapview);
        listview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getView().setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
//                adapterClass.notifyDataSetChanged();
                listview.setAlpha(1f);
                listview.setClickable(false);
                mapview.setClickable(true);
                mapview.setAlpha(0.5f);
                picture.setVisibility(View.GONE);
                navigation.setVisibility(View.GONE);
                clickedMarker=null;
                findViewById(R.id.list_header).setVisibility(View.VISIBLE);
            }
        });
        mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                mapFragment.getView().setVisibility(View.VISIBLE);
                listview.setClickable(true);
                listview.setAlpha(0.5f);
                mapview.setClickable(false);
                mapview.setAlpha(1f);
                findViewById(R.id.list_header).setVisibility(View.GONE);
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickedMarker==null){
                    return;
                }else {
                    StoreData tag = (StoreData) clickedMarker.getTag();
                    if (tag != null) {
//                        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
//                        intent.putExtra("storeid", entity.getStoreId());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                        LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View popUpView=inflater.inflate(R.layout.image_popup,null);
                        popupWindow=new PopupWindow(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if(Build.VERSION.SDK_INT>=21){
                            popupWindow.setElevation(5.0f);
                        }
                        final ProgressBar popUpProgress=(ProgressBar) popUpView.findViewById(R.id.popup_progress);
                        final ImageView popUpImage=(ImageView) popUpView.findViewById(R.id.popup_image);
                        final String url="https://firebasestorage.googleapis.com/v0/b/storechecker-9a5cb.appspot.com/o/images%2F"+tag.getStoreid()+"-outer.jpeg?alt=media&token=a630e4ae-f7f2-4a25-af9f-e98685b0bd03";
                        final String url2="https://firebasestorage.googleapis.com/v0/b/storechecker-9a5cb.appspot.com/o/images%2F"+tag.getStoreid()+".jpeg?alt=media&token=a630e4ae-f7f2-4a25-af9f-e98685b0bd03";
                        Glide.with(getApplicationContext()).load(url).listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                popUpProgress.setVisibility(View.GONE);
                                Log.i(TAG, "image exception=" + e.toString()+" .is first="+isFirstResource);
                                Log.i(TAG, "url=" + url);
                                Glide.with(getApplicationContext()).load(url2).into(popUpImage);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                popUpProgress.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(popUpImage);
//                        Picasso.with(getApplicationContext()).load(url).error(R.drawable.error).into(popUpImage);
                        Button closeButton = (Button) popUpView.findViewById(R.id.close_popup);
                        final TextView storeName=(TextView) popUpView.findViewById(R.id.popup_store_name);
                        final TextView landmark=(TextView) popUpView.findViewById(R.id.landmark_popup);
                        final TextView mapper=(TextView) popUpView.findViewById(R.id.mapper_popup);
                        storeName.setText("Store Name:"+tag.getStore_name());
                        landmark.setText("Landmark:"+tag.getLandmark());
                        mapper.setText("Mapper Id:"+tag.getMapperId());
                        popUpView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                ColorPickerDialogBuilder
                                        .with(HydAuditPage.this)
                                        .setTitle("Choose Text color")
                                        .initialColor(Color.BLUE)
                                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                        .density(12)
                                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                                            @Override
                                            public void onColorSelected(int selectedColor) {

                                            }
                                        })
                                        .setPositiveButton("ok", new ColorPickerClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                                                selected.setTextColor(selectedColor);
                                                storeName.setTextColor(selectedColor);
                                                landmark.setTextColor(selectedColor);
                                                mapper.setTextColor(selectedColor);
//                                                popUpView.setBackgroundColor(selectedColor);
                                            }
                                        })
                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .build()
                                        .show();
                                return true;
                            }
                        });
                        // Set a click listener for the popup window close button
                        closeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Dismiss the popup window
                                popupWindow.dismiss();
                            }
                        });
                        popupWindow.showAtLocation(rootlayout, Gravity.CENTER,0,0);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        isMapReady=true;
        CustomInfoWindowAdapter adapter=new CustomInfoWindowAdapter(HydAuditPage.this);
        mMap.setInfoWindowAdapter(adapter);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                if(clickedMarker!=marker){
                    clickedMarker=marker;
                    Log.i(TAG,"clicked marker updated");
                }
                final StoreData store=(StoreData) marker.getTag();
                performAction(store);

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if(marker.getTag()==null){
                    Log.i(TAG,"null tag");
                    return false;
                }
                clickedMarker=marker;
                final StoreData store=(StoreData) marker.getTag();
                if(store.getSurveyStatus()==-1){
                    if(store.getSurveyStatus()==-1){
                        if(isMappable){
                            AlertDialog.Builder builder=new AlertDialog.Builder(HydAuditPage.this);
                            builder.setCancelable(false);
                            builder.setTitle("Delete");
                            builder.setMessage("Do You Want To Delete Auto Store?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteDummyStore(store,marker);
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog=builder.create();
                            dialog.show();
                        }else {

                            AlertDialog.Builder builder=new AlertDialog.Builder(HydAuditPage.this);
                            builder.setCancelable(false);
                            builder.setTitle("Complete");
                            builder.setMessage("Do You Want To Complete Auto Store?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Location myLocation=mMap.getMyLocation();
                                    if(myLocation!=null){
                                        Location location=new Location("");
                                        location.setLatitude(marker.getPosition().latitude);
                                        location.setLongitude(marker.getPosition().longitude);
                                        float distanceTo = myLocation.distanceTo(location);
                                        if(distanceTo >100 || distanceTo==0){
                                            Toast.makeText(getApplicationContext(),"You Are Far From Point",Toast.LENGTH_SHORT).show();
                                return;
                                        }
                                        completeDummyStore(store,marker);
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Unable To Get Your Location",Toast.LENGTH_SHORT).show();
                                    }
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog=builder.create();
                            dialog.show();

                        }
                    }
                }else {
                    navigation.setVisibility(View.VISIBLE);
                    picture.setVisibility(View.VISIBLE);
                    if (ConstantValues.audit_id.contentEquals("super")) {
//                    edit.setVisibility(View.VISIBLE);
//                    delete.setVisibility(View.VISIBLE);
                    } else {
//                    edit.setVisibility(View.GONE);
//                    delete.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                clickedMarker=null;
                navigation.setVisibility(View.GONE);
                picture.setVisibility(View.GONE);
//                edit.setVisibility(View.GONE);
//                delete.setVisibility(View.GONE);
//                showMapTypeSelectorDialog();
            }
        });
        try {
            mMap.setMyLocationEnabled(true);
            lastLocation = mMap.getMyLocation();
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    lastLocation=location;
//                loadData();
//                Log.i(Tag,"location change from maps"+location.toString());
                }
            });
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if(lastLocation!=null){
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                        return true;
                    }
                    return false;
                }
            });

        }catch (SecurityException e){
            Log.i(TAG,"exception="+e.toString());
        }

    }

    private void drawRoute(){

    }
    private void drawPoint(){

        mMap.clear();
        if(storeList.size()<=0){
            return;
        }
        clickedMarker=null;
        picture.setVisibility(View.GONE);
        navigation.setVisibility(View.GONE);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(storeList.get(0).getLatitude(),storeList.get(0).getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        for(int i=0;i<storeList.size();i++){
//            mMap.addMarker(new MarkerOptions().position(new LatLng(storeList.get(i).getLatitude(),storeList.get(i).getLongitude())).title(storeList.get(i).getStore_name())).setTag(storeList.get(i));
            switch (storeList.get(i).getSurveyStatus()){
                case -1:
                    mMap.addMarker(new MarkerOptions().position(storeList.get(i).getLatLng()).title(storeList.get(i).getStore_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).setTag(storeList.get(i));
                    break;
                case 4:
                    mMap.addMarker(new MarkerOptions().position(storeList.get(i).getLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title(storeList.get(i).getStore_name())).setTag(storeList.get(i));
                    break;
                case 5:
                    mMap.addMarker(new MarkerOptions().position(storeList.get(i).getLatLng()).title(storeList.get(i).getStore_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(storeList.get(i));
                    break;
                default:
                    if(storeList.get(i).isComplete()){
                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(storeList.get(i).getLatitude(), storeList.get(i).getLongitude())).title(storeList.get(i).getStore_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        marker.setTag(storeList.get(i));
                    }else {
                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(storeList.get(i).getLatitude(), storeList.get(i).getLongitude())).title(storeList.get(i).getStore_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        marker.setTag(storeList.get(i));
                    }
                    break;
            }
        }
    }

    private void getData(){
        if(lastLocation==null){
            Toast.makeText(getApplicationContext(),"Please Wait, Getting Your Location",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject object=new JSONObject();
        try {
            object.put("current_latitude", lastLocation.getLatitude());
            object.put("current_longitude", lastLocation.getLongitude());
        }catch (JSONException e){
            e.printStackTrace();
            Log.i(TAG,"exception="+e.toString());
            return;
        }
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.baseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<JsonArray> dataCall=api.getNearStores(object);
        dataCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null response");
                    return;
                }

                storeList.clear();
                for(JsonElement element:response.body()){
                    try {
                        JSONObject jsonObject=new JSONObject(element.toString());
                        storeList.add(new StoreData(jsonObject.getString("store_name"),jsonObject.getDouble("store_latitude"),jsonObject.getDouble("store_longitude"),jsonObject.getDouble("distance"),jsonObject.getString("store_id"),jsonObject.getString("store_landmark"),jsonObject.getString("owner_mobile_no"),"",jsonObject.getString("store_type"),jsonObject.getInt("store_status")==1,jsonObject.getString("mapper_id"),jsonObject.getInt("survey_status")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG,"exception="+e.toString());
                    }

                }
                adapterClass.notifyDataSetChanged();
                drawPoint();

            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"error="+t.toString());
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if(popupWindow!=null){
            if(popupWindow.isShowing()){
                popupWindow.dismiss();
                return;
            }
        }
        super.onBackPressed();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        return super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.audit_page_menu,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.add_new_store:
//                Intent intent=new Intent(getApplicationContext(),CommonAuditActivity.class);
//                intent.putExtra("isAudit",true);
//                startActivity(intent);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    private void navigateTo(){
        if(clickedMarker!=null){
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+clickedMarker.getPosition().latitude+","+clickedMarker.getPosition().longitude+"("+clickedMarker.getTitle()+")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
//            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
                navigation.setVisibility(View.GONE);
            }
        }
    }

    private void makeStoreJunk(final StoreData store){

        if(iosDialog!=null)
            iosDialog.show();
        Log.i(TAG,"making store junk");
        JSONObject object=new JSONObject();
        try {
            object.put("store_id",store.getStoreid());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG,"exception="+e.toString());
            Toast.makeText(getApplicationContext(),"Error Getting Store Id",Toast.LENGTH_LONG).show();
            if(iosDialog.isShowing())
                iosDialog.dismiss();
            return;
        }
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.baseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<String> call=api.makeStoreJunk(object);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null junk response");
                    showToastMsg("Error Response From Server While Updating Store Status");
                    if(iosDialog.isShowing())
                        iosDialog.dismiss();
                    return;
                }

                Log.i(TAG,"junk response="+response.body());
                if(response.body().contentEquals("success")){
                    showToastMsg("Store Status Updated Successfully");
                    if(clickedMarker!=null){

                        clickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    }
                    if(storeList.indexOf(store)!=-1){
                        try {
                            storeList.get(storeList.indexOf(store)).setComplete(true);
                        }catch (IndexOutOfBoundsException e){
                            Log.i(TAG,"store list index exception"+e.toString());
                        }
                    }
                    adapterClass.notifyDataSetChanged();
                }else {
                    showToastMsg("Error Response From Server While Updating Store Status");
                }
                if(iosDialog.isShowing())
                    iosDialog.dismiss();

            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"error="+t.toString());
                showToastMsg("Network Error While Updating Store Status");
                if(iosDialog.isShowing())
                    iosDialog.dismiss();

            }
        });


    }
    private void makeStoreGenuine(final StoreData store){
        if(iosDialog!=null)
        iosDialog.show();

        Log.i(TAG,"making store genuine");
        JSONObject object=new JSONObject();
        try {
            object.put("store_id",store.getStoreid());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG,"exception="+e.toString());
            Toast.makeText(getApplicationContext(),"Error Getting Store Id",Toast.LENGTH_LONG).show();
            if(iosDialog.isShowing())
                iosDialog.dismiss();
            return;
        }
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.baseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<String> call=api.makeStoreGenuine(object);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null genuine response");
                    showToastMsg("Error Response From Server While Updating Store Status");
                    if(iosDialog.isShowing())
                        iosDialog.dismiss();

                    return;
                }
                Log.i(TAG,"genuine response="+response.body());
                if(iosDialog.isShowing())
                    iosDialog.dismiss();

                if(response.body().contentEquals("success")){
                    showToastMsg("Store Status Updated Successfully");
                    if(clickedMarker!=null){

                        clickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                    if(storeList.indexOf(store)!=-1){
                        try {
                            storeList.get(storeList.indexOf(store)).setComplete(false);
                        }catch (IndexOutOfBoundsException e){
                            Log.i(TAG,"store list index exception"+e.toString());
                        }
                    }
                    adapterClass.notifyDataSetChanged();
                }else {
                    showToastMsg("Error Response From Server While Updating Store Status");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if(iosDialog.isShowing())
                    iosDialog.dismiss();

                Log.i(TAG,"error="+t.toString());
                showToastMsg("Network Error While Updating Store Status");
            }
        });

    }

    private void showToastMsg(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addDummyStore(final LatLng latLng, String landmark){
        JSONObject object=new JSONObject();
        try {
            object.put("latitude",latLng.latitude);
            object.put("longitude",latLng.longitude);
            object.put("landmark",landmark);
            object.put("audit_id",ProductApplication.auditId);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG,"exception="+e.toString());
            Toast.makeText(getApplicationContext(),"Unable To Add Marker",Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.baseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<JsonObject> call=api.addDummyStore(object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"add dummy store null response");
                    Toast.makeText(getApplicationContext(),"Unable To Add Marker",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"response="+response.body().toString());
                try {
                    JSONObject jsonObject=new JSONObject(response.body().toString());
                    if(jsonObject.getString("status").toString().contentEquals("success")){
                        mMap.addMarker(new MarkerOptions().position(latLng).title("auto check store").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).setTag(new storePoints("auto Check store",latLng,jsonObject.getString("store_id"),-1,false));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG,"exception="+e.toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"add dummy store error="+t.toString());
                Toast.makeText(getApplicationContext(),"Unable To Add Marker",Toast.LENGTH_SHORT).show();
                return;
            }
        });

    }

    private void completeDummyStore(StoreData store, final Marker marker){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Unable To Complete Point Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        if(store==null){
            Log.i(TAG,"null tag returning");
            Toast.makeText(getApplicationContext(),"Unable To Complete Point Please Refresh Data",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("store_id",store.getStoreid());
            jsonObject.put("audit_id",ProductApplication.auditId);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG,"exception="+e.toString());
        }
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.baseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<String> call=api.completeDummyStore(jsonObject);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.body()==null){
                    Toast.makeText(getApplicationContext(),"Unable To Complete Point Network Issue",Toast.LENGTH_SHORT).show();
                    Log.i(TAG,"null complete response");
                    return;
                }
                Log.i(TAG,"response="+response.body());
                if(response.body().contentEquals("success")){
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }else {
                    Toast.makeText(getApplicationContext(),"Unable To Complete Point Server Issue",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(),"Unable To Complete Point Network Issue",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"error="+t.toString());
            }
        });
    }

    private void deleteDummyStore(StoreData store,final Marker marker){

        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject object=new JSONObject();
        try {
            object.put("store_id", store.getStoreid());
            object.put("audit_id",ProductApplication.auditId);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG,"exception="+e.toString());
            Toast.makeText(getApplicationContext(),"Error Getting Data Try Refresh List",Toast.LENGTH_SHORT).show();
            return;
        }
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.baseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<String> call=api.deleteDummyStore(object);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.body()==null){
                    Toast.makeText(getApplicationContext(),"Unable To Delete Point Network Issue",Toast.LENGTH_SHORT).show();
                    Log.i(TAG,"null complete response");
                    return;
                }
                Log.i(TAG,"response="+response.body());
                if(response.body().contentEquals("success")){
                    Toast.makeText(getApplicationContext(),"Point Deleted Successfully",Toast.LENGTH_SHORT).show();
                    marker.remove();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(),"Unable To Delete Point Network Issue",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"error="+t.toString());

            }
        });

    }
    public void performAction(final StoreData store){
        if(!isAudit) {
            Toast.makeText(getApplicationContext(), "Only Assigned Audits Can Change Store Status", Toast.LENGTH_LONG).show();
            return;
        }
        if(store==null){
            Log.i(TAG,"null store tagged");
            Toast.makeText(getApplicationContext(),"Internal Store Tagging Error",Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(HydAuditPage.this);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!store.isComplete()){
                    makeStoreJunk(store);
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(store.isComplete()){
                    makeStoreGenuine(store);

                }
                dialogInterface.dismiss();
            }
        });
        builder.setTitle("Store Check");
        builder.setMessage("Is The Store Junk?");
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
