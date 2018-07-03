package com.adjointtechnologies.mapper;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adjointtechnologies.mapper.database.MapperInfoEntity;
import com.adjointtechnologies.mapper.database.PolygonCornersEntity;
import com.adjointtechnologies.mapper.database.StoreDataKhmEntity;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.quentinklein.slt.LocationTracker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.query.Tuple;
import io.requery.reactivex.ReactiveEntityStore;

public class AuditActivityAnt extends AppCompatActivity {

    public static long lastTime=0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private ReactiveEntityStore<Persistable> data;
    String mCurrentPhotoPath;
    final String TAG="auditactivityant";
    private boolean goback=false;
    long back_time=0;
    double lat_value,lng_value;
    ImageView mImageView;
    static final int REQUEST_TAKE_PHOTO = 1;
    LocationTracker tracker = null;
    TextView accuracy;
    EditText store_name, landmark, mobile_number;
    RadioGroup store_condition,store_type,sell_cigar, sell_total;
    private boolean is_sell_cigar=false;
    private boolean is_sell_total =false;
    float acc=100;
    Button take_pic,submit;
    String storeid="";
    boolean isImage=false;
    String open_close="";
    String typeofstore="";
    LinearLayout root,cigarDepend;
    File imgfolder;
    RadioGroup isItcSalesMan;
    boolean isItcSales=false;
    boolean isItcChecked=false;
    CheckBox near_by_teashop, wine_shop, dhaba, school, railway_station, road_junction,petrol_pump,temple,hospital;
    CheckBox glow_sign,non_lit;
    boolean isInside=false;
    LinearLayout open_close_depend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_ant);
        data=((ProductApplication)getApplication()).getData();
        ConstantValues.audit_id=ProductApplication.auditId;
        storeid=""+ConstantValues.audit_id+System.currentTimeMillis();
        mImageView=(ImageView)findViewById(R.id.imageview_khm);
        imgfolder=new File(ConstantValues.imagepath);
        take_pic=(Button)findViewById(R.id.take_pic_khm);
        submit=(Button)findViewById(R.id.submit_khm);
        cigarDepend=(LinearLayout)findViewById(R.id.cigar_depend_khm);
        accuracy = (TextView) findViewById(R.id.accuracy_khm);
        store_name = (EditText) findViewById(R.id.store_name_khm);
        open_close_depend=(LinearLayout)findViewById(R.id.open_close_depend);
        landmark = (EditText) findViewById(R.id.landmark_khm);
        mobile_number = (EditText) findViewById(R.id.mobile_khm);
        store_condition = (RadioGroup) findViewById(R.id.open_close_khm);
        store_type = (RadioGroup) findViewById(R.id.store_type_khm);
        root=(LinearLayout)findViewById(R.id.rootlayout_khm);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(AuditActivityAnt.this);
            }
        });
        near_by_teashop = (CheckBox) findViewById(R.id.near_by_teashop_new);
        wine_shop = (CheckBox) findViewById(R.id.wine_shop_new);
        dhaba = (CheckBox) findViewById(R.id.dhaba_restaurant_new);
        school = (CheckBox) findViewById(R.id.near_by_school_new);
        railway_station = (CheckBox) findViewById(R.id.railway_station_new);
        road_junction = (CheckBox) findViewById(R.id.road_junction_new);
        petrol_pump=(CheckBox)findViewById(R.id.near_by_petrol_pump_new);
        temple=(CheckBox)findViewById(R.id.near_temple_new);
        hospital=(CheckBox)findViewById(R.id.near_hospital_new);
        sell_cigar=(RadioGroup)findViewById(R.id.is_sell_cigar_khm);
        glow_sign=(CheckBox)findViewById(R.id.glow_sign_dealor_board);
        non_lit=(CheckBox)findViewById(R.id.non_lit_dealor_board);
        isItcSalesMan=(RadioGroup)findViewById(R.id.is_itc_salesman);
        isItcSalesMan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                isItcChecked=true;
                if(i==R.id.yes_itc_salesman){
                    isItcSales=true;
                }else if(i==R.id.no_itc_salesman){
                    isItcSales=false;
                }
            }
        });
        sell_cigar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.yes_sell_cigar_khm){
                    is_sell_cigar=true;
                    cigarDepend.setVisibility(View.VISIBLE);
                    sell_total.clearCheck();
                    is_sell_total =false;
                }else if(i==R.id.no_sell_cigar_khm){
                    is_sell_cigar=false;
                    cigarDepend.setVisibility(View.GONE);
                    sell_total.check(R.id.no_sell_edition_khm);
                    is_sell_total =false;
                }
            }
        });
        sell_total =(RadioGroup)findViewById(R.id.is_sell_total_ant);
        sell_total.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.yes_sell_total_ant){
                    is_sell_total =true;
                }else if(i==R.id.no_sell_total_ant){
                    is_sell_total =false;
                }
            }
        });
        store_condition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.open_khm:
                        open_close="open";
                        open_close_depend.setVisibility(View.VISIBLE);
                        break;
                    case R.id.close_khm:
                        open_close="close";
                        open_close_depend.setVisibility(View.GONE);
                        break;
                }
            }
        });
        store_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.tea_shop_khm:
                        typeofstore="Cigarette + Tea";
                        break;
                    case R.id.pan_shop_khm:
                        typeofstore="Cigarette + Pan";
                        break;
                    case R.id.kirana_shop_khm:
                        typeofstore="Kirana/ General Store";
                        break;
                    case R.id.bakery_khm:
                        typeofstore="Bakery/Sweet Shop";
                        break;
                    case R.id.chemist_khm:
                        typeofstore="Pharmacy/ Medical store";
                        break;
                    case R.id.cosmetic_khm:
                        typeofstore="Cosmetic Store";
                        break;
                    case R.id.wholesale_khm:
                        typeofstore="Wholesale";
                        break;
                    case R.id.supermarket_khm:
                        typeofstore="Supermarket";
                        break;
                    case R.id.type_dhaba_restaurant_khm:
                        typeofstore="Restaurant / Bar/ Wine Shop";
                        break;
                    case R.id.tiffin_breakfast_centre_khm:
                        typeofstore="Tiffin/ Breakfast Centre";
                        break;
                    case R.id.stationary_shop_khm:
                        typeofstore="Stationery Shop";
                        break;
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(),"Enable Gps",Toast.LENGTH_SHORT).show();
        }else {
            tracker = new LocationTracker(getApplicationContext()) {
                @Override
                public void onLocationFound(@NonNull Location location) {
                    Log.i(TAG,"location acc="+location.getAccuracy());
                    Log.i(TAG,"lat_value="+lat_value+","+"lng_value="+lng_value);
                    if(location.getAccuracy()<acc){
                        lat_value=location.getLatitude();
                        lng_value=location.getLongitude();
                        acc=location.getAccuracy();
                        isInside=checkLatLngStatus();
                        if(!isInside){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"You Are Outside Of Your Area",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        if(acc<15){
                            accuracy.setText("Location : A");
                        }else if(acc>25){
                            accuracy.setText("Location : C");
                        }else {
                            accuracy.setText("Location : B");
                        }
                    }
                }

                @Override
                public void onTimeout() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Unable To Get Your Location",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            };
            tracker.startListening();
        }

        take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isImage){
                    Toast.makeText(getApplicationContext(),"Take Picture",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(acc>25){
                    Toast.makeText(getApplicationContext(),"Check Should Be A or B",Toast.LENGTH_SHORT).show();
                    if(tracker.isListening()){
                        tracker.stopListening();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tracker.startListening();
                            }catch (SecurityException e){
                                Log.i(TAG,"exception="+e.toString());
                            }
                        }
                    },1000);
                    return;
                }
                if(store_name.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Store Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(typeofstore.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Select Type Of Store",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(open_close.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Select Store Condition",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(open_close.contentEquals("close")){
                    saveData();
                    return;
                }
                if(sell_cigar.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If Store Sells Cigareete Or Not",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(is_sell_cigar){
                    if(sell_total.getCheckedRadioButtonId()==-1){
                        Toast.makeText(getApplicationContext(),"Select If Store Sells Edition Or Not",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(!isItcChecked){
                    Toast.makeText(getApplicationContext(),"Check If ITC Sales Man Visits The Store",Toast.LENGTH_SHORT).show();
                    return;
                }
                saveData();
            }
        });
    }
    private void saveData(){
        StoreDataKhmEntity entity=new StoreDataKhmEntity();
        entity.setStoreId(storeid);
        entity.setLatitude(lat_value);
        entity.setLongitude(lng_value);
        entity.setAccuracy(acc);
        entity.setStoreName(store_name.getText().toString());
        entity.setLandmark(landmark.getText().toString());
        entity.setMobileNo(mobile_number.getText().toString());
        entity.setStoreCondition(open_close);
        entity.setStoreType(typeofstore);
        entity.setIsSellCigar(is_sell_cigar);
        entity.setIsSellEdition(is_sell_total);
        entity.setIsNearByTeaShop(near_by_teashop.isChecked());
        entity.setIsNearByWine(wine_shop.isChecked());
        entity.setIsNearByDhaba(dhaba.isChecked());
        entity.setisNearByEducation(school.isChecked());
        entity.setIsNearByrailbus(railway_station.isChecked());
        entity.setIsNearByJunction(road_junction.isChecked());
        entity.setIsNearByPetrolPump(petrol_pump.isChecked());
        entity.setIsNearByTemple(temple.isChecked());
        entity.setIsNearByHospital(hospital.isChecked());
        entity.setSurveyTime("" + new SimpleDateFormat("yyy.MM.dd.HH.mm.ss", Locale.ENGLISH).format(new Date()));
        entity.setSyncStatus(false);
        entity.setAudit_Id(ConstantValues.audit_id);
        entity.setIsItcSalesMan(isItcSales);
        entity.setIsGlow_Sign_Dealor_Board(glow_sign.isChecked());
        entity.setISNon_Lit_Dealor_Board(non_lit.isChecked());
        entity.setIsInside(isInside);
        data.insert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<StoreDataKhmEntity>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull StoreDataKhmEntity storeDataKhmEntity) throws Exception {
                Toast.makeText(getApplicationContext(), "details saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.adjointtechnologies.mapper",
                        photoFile);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File imgfile=new File(mCurrentPhotoPath);
            Bitmap bitmap= BitmapFactory.decodeFile(mCurrentPhotoPath);
            FileOutputStream out=null;
            try{
                out=new FileOutputStream(imgfile);
                boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                isImage=compress;
            }catch (FileNotFoundException e){
                e.printStackTrace();
                Log.i(TAG,"image error="+e.toString());
            }finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG,"out error="+e.toString());
                }
            }
            bitmap= Bitmap.createScaledBitmap(bitmap,mImageView.getWidth(),mImageView.getHeight(),true);
            mImageView.setImageBitmap(bitmap);

        }

    }
    private File createImageFile() throws IOException {
        File image = new File(ConstantValues.imagepath+"/"+storeid+".jpeg");
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("currentpath","="+mCurrentPhotoPath);
        return image;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tracker.isListening()) {
            tracker.stopListening();
        }
//        data.close();
    }

    @Override
    public void onBackPressed() {
        if(goback && back_time+2000>System.currentTimeMillis()) {
            super.onBackPressed();
        }else {
            Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
            goback=true;
            back_time=System.currentTimeMillis();
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i(TAG,"in post create");
        if(!storeid.startsWith("ANT") && !storeid.startsWith("AUD")){
            Log.i(TAG,"id error 1");
            if(ConstantValues.audit_id.startsWith("ANT") || ConstantValues.audit_id.startsWith("AUD")){
                storeid=ConstantValues.audit_id+System.currentTimeMillis();
            }else {
                Log.i(TAG,"id error 2 Constant values");
                MapperInfoEntity mapperInfoEntity = data.select(MapperInfoEntity.class).get().firstOrNull();
                if(mapperInfoEntity==null){
                    data.delete(MapperInfoEntity.class).get().value();
                    Toast.makeText(getApplicationContext(),"Login Error Please Try Again",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),LoginReg.class));//LoginActivity.class));
                    finish();
                }else{
                    ConstantValues.audit_id=mapperInfoEntity.getAuditId();
                    if(ConstantValues.audit_id.startsWith("ANT") || ConstantValues.audit_id.startsWith("AUD")){
                        storeid=ConstantValues.audit_id+System.currentTimeMillis();
                    }else {
                        Toast.makeText(getApplicationContext(),"Login Error Please Try Again",Toast.LENGTH_SHORT).show();
                        data.delete(MapperInfoEntity.class).get().value();
                        startActivity(new Intent(getApplicationContext(),LoginReg.class));//LoginActivity.class));
                        finish();
                    }
                }
            }
        }
    }
    private boolean checkLatLngStatus(){
        LatLng latLng=new LatLng(lat_value,lng_value);
        if(latLng==null){
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
}
