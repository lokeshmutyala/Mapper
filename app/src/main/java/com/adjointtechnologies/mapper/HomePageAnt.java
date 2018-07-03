package com.adjointtechnologies.mapper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adjointtechnologies.mapper.database.DeviceInfoEntity;
import com.adjointtechnologies.mapper.database.GpsCoordinatedEntity;
import com.adjointtechnologies.mapper.database.ImageUrlEntity;
import com.adjointtechnologies.mapper.database.MapperInfoEntity;
import com.adjointtechnologies.mapper.database.PolygonCornersEntity;
import com.adjointtechnologies.mapper.database.StoreDataEntity;
import com.adjointtechnologies.mapper.database.StoreDataKhm;
import com.adjointtechnologies.mapper.database.StoreDataKhmEntity;
import com.adjointtechnologies.mapper.database.SyncInfoEntity;
import com.adjointtechnologies.mapper.service.GpsService;
import com.adjointtechnologies.mapper.sync.SyncUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.query.Tuple;
import io.requery.reactivex.ReactiveEntityStore;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.adjointtechnologies.mapper.ConstantValues.baseUrl;
import static com.adjointtechnologies.mapper.ConstantValues.commonMapperBaseUrl;

public class HomePageAnt extends AppCompatActivity {

    final String TAG="homepagekhm";
    private StorageReference mStorageRef;
    File imgFolder;
    boolean isSyncing;
    File imgProcess;
    File imgComplete;
    File databsePath;
    boolean isImgError=false;
    private ReactiveEntityStore<Persistable> data;
    Button start,dataSync,imageSync,logout,details;
    TextView todayData,pendingData,pendingImages;
    TextView tableTodayOutCount,tableMonthOutCount,tableTodayInCount,tableMonthInCount,tableTodayTotal,tableMonthTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SyncUtils.CreateSyncAccount(getApplicationContext());
        mStorageRef = FirebaseStorage.getInstance().getReference();
        data=((ProductApplication)getApplication()).getData();
        startService(new Intent(this,GpsService.class));
        setContentView(R.layout.activity_home_page_ant);
        imgFolder=new File(ConstantValues.imagepath);
        imgProcess=new File(ConstantValues.imageprocess);
        imgComplete=new File(ConstantValues.imagecomplete);
        databsePath=new File(ConstantValues.dataBasePath);
        tableTodayOutCount=(TextView)findViewById(R.id.table_today_outcount_khm);
        tableMonthOutCount=(TextView)findViewById(R.id.table_month_outcount_khm);
        tableTodayInCount=(TextView)findViewById(R.id.table_today_incount_khm);
        tableMonthInCount=(TextView)findViewById(R.id.table_month_incount_khm);
        tableTodayTotal=(TextView)findViewById(R.id.table_today_totalcount_khm);
        tableMonthTotal=(TextView)findViewById(R.id.table_month_totalcount_khm);
        ConstantValues.audit_id=ProductApplication.auditId;
        if(!ConstantValues.audit_id.startsWith("ANT")){
            if(!ConstantValues.audit_id.contentEquals("AUD999")){
                Log.i(TAG,"Login error id="+ConstantValues.audit_id);
                Toast.makeText(getApplicationContext(),"Login Error Please Try Again",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),LoginReg.class));//LoginActivity.class));
                finish();
            }
        }
        if (isNetworkAvailable()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("agent_id", ConstantValues.audit_id);
                getPolygonCoordinates(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        start=(Button)findViewById(R.id.start_khm);
        dataSync=(Button)findViewById(R.id.sync_data_khm);
        details=(Button)findViewById(R.id.details_khm);
        imageSync=(Button)findViewById(R.id.sync_images_khm);
        todayData=(TextView) findViewById(R.id.today_surveys_khm);
        pendingData=(TextView)findViewById(R.id.data_sync_pending_khm);
        pendingImages=(TextView)findViewById(R.id.images_pending_khm);
        updateRecords();
        exportDatabse();
        logout=(Button)findViewById(R.id.logout_khm);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),DetailsActivity.class);
                intent.putExtra("isaudit",true);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.delete(MapperInfoEntity.class).get().value();
                if(ConstantValues.audit_id.contentEquals("AUD999")){
                    data.delete(DeviceInfoEntity.class).get().value();
                }
                ConstantValues.audit_id="";
                stopService(new Intent(getApplicationContext(),GpsService.class));
                startActivity(new Intent(getApplicationContext(),LoginReg.class));//LoginActivity.class));
                finish();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkLatLngStatus()){
                    startActivityForResult(new Intent(getApplicationContext(), AuditActivityAnt.class),123);
                }else {
                    Toast.makeText(getApplicationContext(),"You Are OutSide Of Your Allocated Area",Toast.LENGTH_LONG).show();
                    startActivityForResult(new Intent(getApplicationContext(), AuditActivityAnt.class),123);
                }
            }
        });
        dataSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncData();
                UploadImageUrls();
                syncGpsData();
            }
        });
        imageSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImages();
            }
        });

    }
    private void updateRecords(){
        final SyncInfoEntity entity = data.select(SyncInfoEntity.class).get().firstOrNull();
        if(entity!=null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tableTodayInCount.setText(""+entity.getTodayIn());
                    tableTodayOutCount.setText(""+entity.getTodayOut());
                    tableMonthInCount.setText(""+entity.getMonthIn());
                    tableMonthOutCount.setText(""+entity.getMonthOut());
                    tableTodayTotal.setText(""+(entity.getTodayOut()+entity.getTodayIn()));
                    tableMonthTotal.setText(""+(entity.getMonthOut()+entity.getMonthIn()));
                }
            });
        }
        Calendar tm=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        String tmmp=sdf.format(tm.getTime());
        todayData.setText(""+data.count(StoreDataKhmEntity.class).where(StoreDataKhmEntity.SURVEY_TIME.like(tmmp+"%")).get().value());
        File[] images=imgFolder.listFiles();
        int no=0;
        if(images!=null){no=images.length;}
        File[] process=imgProcess.listFiles();
        if(process!=null){
            no+=process.length;
        }
        final int total=no;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pendingData.setText(String.valueOf(data.count(StoreDataKhmEntity.class).where(StoreDataKhmEntity.SYNC_STATUS.eq(false)).get().value()));
                pendingImages.setText(""+total);
            }
        });

    }
    public void exportDatabse() {
        try {
            if (databsePath.canWrite()) {
                String currentDBPath = "//data//data//"+getPackageName()+"//databases//default";
                File backupDB = new File(databsePath,"backup.db");
                File currentDB=new File(currentDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(),"Database Copied Successfully",Toast.LENGTH_SHORT).show();
                }else {
                }
            }else {
            }
        } catch (Exception e) {
            Log.i(TAG,"export error="+e.toString());
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void syncData(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"No Network Available",Toast.LENGTH_SHORT).show();
            return;
        }
        dataSync.setEnabled(false);
        Toast.makeText(getApplicationContext(),"sync started",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<StoreDataKhmEntity> entities = data.select(StoreDataKhmEntity.class).where(StoreDataKhmEntity.SYNC_STATUS.eq(false)).get().toList();
                if(entities.size()<=0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isSyncing=false;
                            Toast.makeText(getApplicationContext(),"No data to sync",Toast.LENGTH_SHORT).show();
                            dataSync.setEnabled(true);
                            getSyncInfo();
                        }
                    });
                }else {
                    JSONArray array=new JSONArray();
                    for(StoreDataKhmEntity entity:entities){
                        JSONObject object=new JSONObject();
                        try {
                            object.put("store_id",entity.getStoreId());
                            if(entity.getAudit_Id().isEmpty()){
                                Log.i(TAG,"empty changing="+ConstantValues.audit_id);
                                object.put("audit_id",ConstantValues.audit_id);
                            }else {
                                Log.i(TAG,"not empty="+entity.getAudit_Id());
                                object.put("audit_id", entity.getAudit_Id());
                            }
                            object.put("latitude",entity.getLatitude());
                            object.put("longitude",entity.getLongitude());
                            object.put("accuracy",entity.getAccuracy());
                            object.put("store_name",entity.getStoreName());
                            object.put("landmark",entity.getLandmark());
                            object.put("owner_mobile",entity.getMobileNo());
                            object.put("store_condition",entity.getStoreCondition());
                            object.put("store_type",entity.getStoreType());
                            object.put("is_sell_cigar",entity.getIsSellCigar());
                            object.put("isnearbyteashop",entity.getIsNearByTeaShop());
                            object.put("isnearbywine",entity.getIsNearByWine());
                            object.put("isnearbydhaba",entity.getIsNearByDhaba());
                            object.put("isnearbyeducation",entity.getisNearByEducation());
                            object.put("isnearbyrail",entity.getIsNearByrailbus());
                            object.put("isnearbyjunction",entity.getIsNearByJunction());
                            object.put("isnearbypetrolpump",entity.getIsNearByPetrolPump());
                            object.put("isnearbytemple",entity.getIsNearByTemple());
                            object.put("isnearbyhospital",entity.getIsNearByHospital());
                            object.put("is_sell_total",entity.getIsSellEdition());
                            object.put("time",entity.getSurveyTime());
                            object.put("is_itc_sales_man",entity.getIsItcSalesMan());
                            object.put("is_glow_sign",entity.getIsGlow_Sign_Dealor_Board());
                            object.put("is_non_lit",entity.getISNon_Lit_Dealor_Board());
                            object.put("is_inside",entity.getIsInside());
                            array.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG,"json exception="+e.toString());
                        }

                    }
                    Retrofit retrofit=new Retrofit.Builder()
                            .baseUrl(commonMapperBaseUrl)
                            .addConverterFactory(new ToStringConverterFactory())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitProductApi syncdata=retrofit.create(RetrofitProductApi.class);
                    Call<String> upload=syncdata.syncantData(array);
                    upload.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Response<String> response, Retrofit retrofit) {
                            if(response.body()==null){
                                Log.i(TAG,"null responce");
                                return;
                            }
                            Log.i(TAG,"response="+response.body());
                            if(response.body().contains("success")) {
                                getSyncInfo();
                                for (StoreDataKhmEntity store:entities
                                        ) {
                                    store.setSyncStatus(true);
                                    data.update(store);
                                }
                                isSyncing =false;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateRecords();
                                    }
                                });
                                data.update(entities).observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io()).subscribe(new Consumer<Iterable<StoreDataKhmEntity>>() {
                                    @Override
                                    public void accept(@NonNull Iterable<StoreDataKhmEntity> storeDataEntities) throws Exception {
                                        //do nothing
                                        Log.i(TAG,"completed");
                                        isSyncing =false;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateRecords();
                                            }
                                        });
                                    }
                                });
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isSyncing=false;
                                        Toast.makeText(getApplicationContext(),"data sync completed",Toast.LENGTH_SHORT).show();
                                        dataSync.setEnabled(true);
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isSyncing=false;
                                        Toast.makeText(getApplicationContext(),"error while syncing",Toast.LENGTH_SHORT).show();
                                        dataSync.setEnabled(true);
                                    }
                                });

                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.i(TAG,"error="+t.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"error while syncing",Toast.LENGTH_SHORT).show();
                                    dataSync.setEnabled(true);
                                }
                            });

                        }
                    });


                }

            }
        }).start();

    }
    private void UploadImageUrls(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!isNetworkAvailable()){
                    return;
                }
                JSONArray urlArray=new JSONArray();
                final List<ImageUrlEntity> imageUrlEntities = data.select(ImageUrlEntity.class).get().toList();
                for(int i=0; i<imageUrlEntities.size();i++){
                    JSONObject object=new JSONObject();
                    try {
                        object.put("storeid",imageUrlEntities.get(i).getStoreId());
                        object.put("imageurl",imageUrlEntities.get(i).getImageUrl());
                        urlArray.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl)
                        .addConverterFactory(new ToStringConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create()).build();
                RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
                Call<String> upload=api.synImageUrls(urlArray);
                upload.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response, Retrofit retrofit) {
                        if(response.body()==null){
                            Log.i(TAG,"responce=null");
                            return;
                        }
                        if(response.body().contains("success")){
                            data.delete(ImageUrlEntity.class).get().value();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("imageurl","url upload error="+t.toString());
                    }
                });
            }
        }).start();

    }
    private void UploadImages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageSync.setClickable(false);
                        imageSync.setAlpha(0.5f);
                    }
                });
                if(!isNetworkAvailable()){
                    return;
                }
                if(imgFolder.isDirectory() && imgProcess.isDirectory() && imgComplete.isDirectory()){
                    File[] listcompletedimgs=imgComplete.listFiles();
                    for(int k=0;k<listcompletedimgs.length;k++){
                        listcompletedimgs[k].delete();
                    }
                    File[] listimgs=imgProcess.listFiles();
                    if(listimgs.length>0){
                        for(int i=0;i<listimgs.length;i++){
                            if (listimgs[i].renameTo(new File(imgFolder+"/" + listimgs[i].getName()))) {
                                Log.i("firebase", "success copying file");
                            } else {
                                Log.i("firebase", "file moving error");
                                return;
                            }
                        }
                    }
                    listimgs=imgFolder.listFiles();
                    if(listimgs.length>0){
                        isImgError=false;
                        for(int i=0;i<listimgs.length && i<1 && !isImgError ;i++){ //replace 1 with
                            if (listimgs[i].renameTo(new File(imgProcess+"/" + listimgs[i].getName()))) {
                                Log.i("firebase", "success copying file");
                            } else {
                                Log.i("firebase", "file moving error");
                                return;
                            }
                            File[]  listimgProcess=imgProcess.listFiles();
                            if(listimgProcess.length>0){
                                Uri imgfile=Uri.fromFile(new File(imgProcess+"/" + listimgs[i].getName()));//listimgProcess[listimgProcess.length-1]);
                                StorageReference imgref=mStorageRef.child("images/"+listimgProcess[0].getName());
                                imgref.putFile(imgfile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(getApplicationContext(), "Upload image success", Toast.LENGTH_SHORT).show();
                                        @SuppressWarnings("VisibleForTests") String s = taskSnapshot.getDownloadUrl().toString();
                                        ImageUrlEntity urlEntity=new ImageUrlEntity();
                                        urlEntity.setImageUrl(s);
                                        s=s.substring(s.indexOf("images%2F")+9,s.indexOf("?alt"));
                                        urlEntity.setStoreId(s.substring(0,s.indexOf('.')));
                                        data.insert(urlEntity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                                                .subscribe(new Consumer<ImageUrlEntity>() {
                                                    @Override
                                                    public void accept(@NonNull ImageUrlEntity imageUrlEntity) throws Exception {
                                                        Log.i("imageurl","saved successfully");
                                                    }
                                                });
                                        File tmp=new File(imgProcess+"/"+s);
                                        if(tmp.exists()) {
                                            Log.i("firebase", "image name=" + s+"exists");
                                            if (tmp.renameTo(new File(imgComplete + "/" + tmp.getName()))) {
                                                Log.i("firebase", "success moving image");
                                                updateRecords();
                                                UploadImages();
                                            } else {
                                                tmp.delete();
                                            }
                                        }else {
                                            Log.i("firebase","image name="+s+" not exiists");
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@android.support.annotation.NonNull Exception e) {
//                                       File tmp=new File(imgfile.getPath());
//                                       if(!tmp.renameTo(new File(imgFolder+"/"+tmp.getName()))){
//                                           Log.i("firebase","file upload error but stayed in process folder");
//                                       }
                                        isImgError = true;
//                                       Log.i("Firebase", "error=" + e.toString());
                                    }
                                });
                            }
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageSync.setClickable(true);
                                imageSync.setAlpha(1f);
                                Toast.makeText(getApplicationContext(),"Images updated successfully",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    if(!imgFolder.mkdirs() ){
                        Log.i("folder","creation error persists for imgfolder");
                    }
                    if(!imgProcess.mkdirs()){
                        Log.i("folder","creation error persists for imgProcess");
                    }
                    if(!imgComplete.mkdirs()){
                        Log.i("folder","creation error persists for imgComplete");
                    }
                }
            }

        }).start();
    }
    private void syncGpsData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<GpsCoordinatedEntity> coordinatedEntities = data.select(GpsCoordinatedEntity.class).get().toList();
                Log.i(TAG,"list size="+coordinatedEntities.size());
                if(coordinatedEntities.size()>0) {
                    JSONArray jsonArray = new JSONArray();
                    int i = 0;
                    for (GpsCoordinatedEntity coordinate : coordinatedEntities
                            ) {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("latitude", coordinate.getLatitude());
                            object.put("longitude", coordinate.getLongitude());
                            object.put("accuracy", coordinate.getAccuracy());
                            object.put("time", coordinate.getcaptureTime());
                            if(coordinate.getAgentId().isEmpty()){
                                object.put("agent_id", ConstantValues.audit_id);
                            }else {
                                object.put("agent_id", coordinate.getAgentId());
                            }
                            object.put("speed",coordinate.getSpeed());
                            object.put("provider",coordinate.getProvider());
                            jsonArray.put(object);
                            i++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "json error=" + e.toString());
                        }
                    }
                    final int size = i;
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(new ToStringConverterFactory())
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    RetrofitProductApi api = retrofit.create(RetrofitProductApi.class);
                    Call<String> syncGps = api.syncGpsCoordinates(jsonArray);
                    syncGps.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Response<String> response, Retrofit retrofit) {
                            if(response.body()==null){
                                Log.i(TAG,"responce=null");
                                return;
                            }
                            Log.i(TAG, "resonce="+response.body());
//                    isGpsSyncComplete = true;
                            if (response.body().contains("success")) {
                                if (size < coordinatedEntities.size()) {
                                    Log.i(TAG, "list size" + size);
                                    //List<GpsCoordinatedEntity> tmplist= coordinatedEntities.subList(0,size-1);
                                }
                                for(GpsCoordinatedEntity enti:coordinatedEntities) {
                                    data.delete(enti).subscribeOn(AndroidSchedulers.mainThread())
                                            .observeOn(Schedulers.io()).subscribe();
                                }
                                Log.i(TAG,"coordinates deleted");
                            }
//                    stopSelf();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.i(TAG, "error in network" + t.toString());
//                    isGpsSyncComplete = true;
//                    stopSelf();
                        }

                    });
                }else {

                }

            }
        }).start();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateRecords();
        dataSync.setEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.app_info,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                final String fDialogTitle = "App Info";
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(fDialogTitle);
                View inflatedView = LayoutInflater.from(HomePageAnt.this).inflate(R.layout.about_info, null, false);
                final TextView appName=(TextView)inflatedView.findViewById(R.id.app_name);
                final TextView appVersion=(TextView) inflatedView.findViewById(R.id.app_version);
                TextView myid=(TextView) inflatedView.findViewById(R.id.my_audit_id);
                appName.setText("Khammam Mapper Application");
                appVersion.setText("Mapper "+ConstantValues.app_version);
                myid.setText(ProductApplication.auditId);
                builder.setView(inflatedView);
                builder.setCancelable(true);
                final AlertDialog alertDialog=builder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void getPolygonCoordinates(final JSONObject object) {
        Log.i("polygon", "entered");
        Log.i("polygon", "audit=" + ConstantValues.audit_id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(ConstantValues.baseUrl)
                        .addConverterFactory(new ToStringConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitProductApi retrofitProductApi = retrofit.create(RetrofitProductApi.class);
                Call<JsonArray> getcorners = retrofitProductApi.getPolygon(object);
                getcorners.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                        if(response.body()==null){
                            Log.i(TAG,"null responce");
                            return;
                        }
                        Log.i(TAG, "responce=" + response.body().toString());
                        for (JsonElement element : response.body()
                                ) {
                            try {
                                JSONObject jsonObject = new JSONObject(element.toString());
                                String polyId = jsonObject.getString("polygon_id");
                                String polyname = jsonObject.getString("polygon_name");
                                String corners = jsonObject.getString("corner_coordinates");
                                String mapperId=jsonObject.getString("audit_id");
                                Log.i("polygon", "corners=" + corners);
                                JSONArray array = new JSONArray(corners);
                                if (array.length() > 0) {
                                    data.delete(PolygonCornersEntity.class).get().value();
                                }
                                for (int i = 0; i < array.length() - 1; i++) {
                                    String latlngs[] = array.getString(i).split(",");
                                    double lat = Double.parseDouble(latlngs[0]);
                                    double lng = Double.parseDouble(latlngs[1]);
                                    PolygonCornersEntity entity = new PolygonCornersEntity();
                                    entity.setLatitude(lat);
                                    entity.setLongitude(lng);
                                    entity.setPolygonKey(polyId);
                                    entity.setPolygonName(polyname);
                                    entity.setMapperId(mapperId);
                                    data.insert(entity).subscribeOn(AndroidSchedulers.mainThread())
                                            .observeOn(Schedulers.io())
                                            .subscribe(new Consumer<PolygonCornersEntity>() {
                                                @Override
                                                public void accept(@NonNull PolygonCornersEntity polygonCornersEntity) throws Exception {
                                                    Log.i("polygon", "inserted successfully");
                                                    startService(new Intent(getApplicationContext(), GpsService.class));
                                                }
                                            });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.i("polygon", "error json=" + e.toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("polygon", "error=" + t.toString());

                    }
                });
            }
        }).start();
    }
    private boolean checkLatLngStatus(){
        if(GpsService.lastLatlng==null){
            Toast.makeText(getApplicationContext(),"Unable Get Your Location",Toast.LENGTH_LONG).show();
            return false;
        }
        boolean pointStatus = false;
        List<PolyCorners> polyCornerses=new ArrayList<>();
        List<LatLng> corners;
        List<Tuple> tuples = data.select(PolygonCornersEntity.POLYGON_KEY).distinct().get().toList();
        for(int i=0;i<tuples.size();i++){
            corners=new ArrayList<>();
            Log.i("polygon","tuples[i]="+tuples.get(i));
            List<PolygonCornersEntity> test = data.select(PolygonCornersEntity.class).where(PolygonCornersEntity.POLYGON_KEY.eq(tuples.get(i).toString().substring(tuples.get(i).toString().indexOf('[')+2,tuples.get(i).toString().indexOf(']')-1))).get().toList();
            for(int j=0;j<test.size();j++){
                corners.add(new LatLng(test.get(j).getLatitude(),test.get(j).getLongitude()));
            }
            polyCornerses.add(new PolyCorners(corners,"",test.get(0).getPolygonKey()));
            Log.i("polygon","corners size before adding ="+corners.size());
        }
        LatLng latLng=GpsService.lastLatlng;
        if (polyCornerses.size() > 0) {
            for (int i = 0; i < polyCornerses.size() && !pointStatus; i++) {
                if (polyCornerses.get(i).getCorners().size() > 0) {
                    pointStatus = PolyUtil.containsLocation(latLng, polyCornerses.get(i).corners, false);
                    Log.i("fusedapi", "pointstatus=" + pointStatus);
                }
            }
        }

        return pointStatus;
    }
    private void getSyncInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject object=new JSONObject();
                try {
                    object.put("mapper_id",ConstantValues.audit_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG,"json exception="+e.toString());
                    return;
                }
                Retrofit retrofit =new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(new ToStringConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create()).build();
                RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
                Call<String> getdata=api.getSyncData(object);
                getdata.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response, Retrofit retrofit) {
                        if(response.body()==null){
                            Log.i(TAG," get data null response");
                            return;
                        }
                        Log.i(TAG,"response="+response.body());
                        data.delete(SyncInfoEntity.class).get().value();
                        SyncInfoEntity entity=new SyncInfoEntity();
                        try {
                            JSONObject jsonObject=new JSONObject(response.body().toString());
                            entity.setTodayIn(jsonObject.getInt("today_in"));
                            entity.setTodayOut(jsonObject.getInt("today_out"));
                            entity.setMonthIn(jsonObject.getInt("month_in"));
                            entity.setMonthOut(jsonObject.getInt("month_out"));
                            data.insert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<SyncInfoEntity>() {
                                        @Override
                                        public void accept(@NonNull SyncInfoEntity syncInfoEntity) throws Exception {
                                            Log.i(TAG,"info data inserted");
                                            updateRecords();
                                        }
                                    });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG,"exception="+e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i(TAG,"error="+t.toString());
                    }
                });
            }
        }).start();

    }
}
