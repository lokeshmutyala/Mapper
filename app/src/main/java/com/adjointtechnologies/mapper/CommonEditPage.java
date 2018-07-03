package com.adjointtechnologies.mapper;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adjointtechnologies.mapper.database.CommonStoreDataEntity;
import com.adjointtechnologies.mapper.database.ExtraStoreDataEntity;
import com.adjointtechnologies.mapper.database.ImageUrlEntity;
import com.adjointtechnologies.mapper.database.PolygonCornersEntity;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.PolyUtil;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import org.json.JSONException;
import org.json.JSONObject;

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
import fr.quentinklein.slt.TrackerSettings;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

public class CommonEditPage extends AppCompatActivity {
    public static long lastTime=0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private ReactiveEntityStore<Persistable> data;
    String mCurrentPhotoPath;
    final String TAG="commoneditpage";
    private boolean goback=false;
    long back_time=0;
//    double lat_value,lng_value;
    ImageView mImageView, innerImageView;
    boolean isMsgSent = false;
    boolean isMobileVerified = false;
    boolean isMsgSent2=false;
    boolean isMobileVerified2=false;
    Verification mVerification;
    static final int REQUEST_TAKE_PHOTO = 1;
    String inOrOut="";
    String INNER="inner";
    String OUTER="outer";
    boolean isCommonInserted=false;
    boolean isExtraInserted=false;
//    LocationTracker tracker = null;
    TextView accuracy;
    EditText store_name, landmark, owner_mobile_number,owner_name, alternate_mobile_number;
    RadioGroup store_condition,store_type,sell_cigarette, cigarette_salesman_visit_store, non_cigarette_salesman_vist_store,permTemp;
    //    RadioGroup itc_salesman_visit_store;
    private boolean is_sell_cigar=false;
    private boolean itc_salesman_visit=false;
//    float acc=100;
    Button take_pic,inner_picture_button,submit, verify_mobile,verify_alternate_mobile;
    String storeid="";
    boolean isImage=false;
    String open_close="";
    String typeofstore="";
    LinearLayout root,cigarDepend;
    File imgfolder;
    RadioGroup isItcSalesMan;
    boolean isItcSales=false;
    boolean isItcChecked=false;
    CheckBox near_by_teashop, wine_shop, dhaba, railway_station, road_junction,petrol_pump,temple,hospital;
//    CheckBox glow_sign,non_lit;
    RadioGroup dealorBoard,companyDealorBoard,nearSchool;
    boolean isInside=false;
    LinearLayout open_close_depend,open_close_depend_2, layout_itc_cigarette_salesman, layout_itc_non_cigarette_salesman,dealorBoardDepend;
    ProgressBar progressBar;
    boolean isImgChange=false;
    boolean isImgUpdated=false;
    boolean isDetailsUpdated=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_edit_page);
        data=((ProductApplication)getApplication()).getData();
        storeid=getIntent().getStringExtra("storeid");
        if(storeid.isEmpty()){
            Toast.makeText(getApplicationContext(),"Empty Data",Toast.LENGTH_SHORT).show();
            finish();
        }
        mImageView=(ImageView)findViewById(R.id.imageview_khm);
        innerImageView=(ImageView)findViewById(R.id.imageview_inner_picture);
        imgfolder=new File(ConstantValues.imagepath);
        take_pic=(Button)findViewById(R.id.take_pic_khm);
        inner_picture_button=(Button)findViewById(R.id.inner_picture);
        submit=(Button)findViewById(R.id.submit_khm);
        cigarDepend=(LinearLayout)findViewById(R.id.cigar_depend_khm);
        accuracy = (TextView) findViewById(R.id.accuracy_khm);
        store_name = (EditText) findViewById(R.id.store_name_khm);
        verify_mobile = (Button) findViewById(R.id.verify_mobile);
        verify_alternate_mobile=(Button) findViewById(R.id.verify_alternate_mobile);
        alternate_mobile_number=(EditText) findViewById(R.id.alternate_mobile_number);
        open_close_depend=(LinearLayout)findViewById(R.id.open_close_depend_common);
        open_close_depend_2=(LinearLayout) findViewById(R.id.open_close_depend_2);
        landmark = (EditText) findViewById(R.id.landmark_khm);
        owner_name=(EditText) findViewById(R.id.owner_name);
        owner_mobile_number = (EditText) findViewById(R.id.mobile_khm);
        store_condition = (RadioGroup) findViewById(R.id.open_close_khm);
        store_type = (RadioGroup) findViewById(R.id.store_type_khm);
        permTemp=(RadioGroup) findViewById(R.id.perm_temp);
        dealorBoard=(RadioGroup) findViewById(R.id.dealor_board);
        companyDealorBoard=(RadioGroup) findViewById(R.id.company_dealor_board);
        dealorBoardDepend=(LinearLayout) findViewById(R.id.dealor_board_depend);
        nearSchool=(RadioGroup) findViewById(R.id.nearby_school);
        root=(LinearLayout)findViewById(R.id.rootlayout_khm);
        progressBar=(ProgressBar) findViewById(R.id.progress_common_edit);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(CommonEditPage.this);
            }
        });
//        glow_sign=(CheckBox)findViewById(R.id.glow_sign_dealor_board);
//        non_lit=(CheckBox)findViewById(R.id.non_lit_dealor_board);
//        non_lit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b) {
//                    glow_sign.setVisibility(View.VISIBLE);
//                }else {
//                    glow_sign.setVisibility(View.GONE);
//                }
//            }
//        });
        dealorBoard.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.yes_dealor_board){
                    dealorBoardDepend.setVisibility(View.VISIBLE);
                }else {
                    dealorBoardDepend.setVisibility(View.GONE);
                }
            }
        });
        near_by_teashop = (CheckBox) findViewById(R.id.near_by_teashop_new);
        wine_shop = (CheckBox) findViewById(R.id.wine_shop_new);
        dhaba = (CheckBox) findViewById(R.id.dhaba_restaurant_new);
//        school = (CheckBox) findViewById(R.id.near_by_school_new);
        railway_station = (CheckBox) findViewById(R.id.railway_station_new);
        road_junction = (CheckBox) findViewById(R.id.road_junction_new);
        petrol_pump=(CheckBox)findViewById(R.id.near_by_petrol_pump_new);
        temple=(CheckBox)findViewById(R.id.near_temple_new);
        hospital=(CheckBox)findViewById(R.id.near_hospital_new);
        sell_cigarette=(RadioGroup)findViewById(R.id.is_sell_cigar_khm);
//        itc_salesman_visit_store=(RadioGroup)findViewById(R.id.itc_salesman_visits_store);
        cigarette_salesman_visit_store=(RadioGroup)findViewById(R.id.itc_cigarette_salesman_visits_store);
        non_cigarette_salesman_vist_store=(RadioGroup)findViewById(R.id.itc_non_cigarette_salesman_visits_store);
        layout_itc_cigarette_salesman=(LinearLayout)findViewById(R.id.layout_itc_cigarette_salesman);
        layout_itc_non_cigarette_salesman=(LinearLayout)findViewById(R.id.layout_itc_non_cigarette_salesman);



        verify_alternate_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_alternate_mobile.setEnabled(false);
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(),"No Network Available",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isMobileVerified2) {
                    if(isMsgSent2){
                        Toast.makeText(getApplicationContext(),"Message Already Sent",Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    String no = alternate_mobile_number.getText().toString();
                    if (no.isEmpty() || no.length() < 10) {
                        Log.i(TAG, "no format error");
                        verify_alternate_mobile.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"Enter valid mobile no",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (no.charAt(0) == '6' || no.charAt(0) == '7' || no.charAt(0) == '8' || no.charAt(0) == '9') {
                        Log.i(TAG, "sending msg");
                        final AlertDialog.Builder builder = new AlertDialog.Builder(CommonEditPage.this);
                        builder.setTitle("Enter Otp");
                        View inflatedView = LayoutInflater.from(CommonEditPage.this).inflate(R.layout.otp_layout, null, false);
                        final EditText otp = (EditText) inflatedView.findViewById(R.id.input);
//                            final ProgressBar progressBar = (ProgressBar) inflatedView.findViewById(R.id.otp_progress);
                        builder.setView(inflatedView);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                    progressBar.setVisibility(View.VISIBLE);
//                                    otp.setVisibility(View.GONE);
                                mVerification.verify(otp.getText().toString());
                                Log.i(TAG, "entered otp=" + otp.getText().toString());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                verify_alternate_mobile.setEnabled(true);
                            }
                        });
                        final AlertDialog alertDialog=builder.create();
//                            builder.create();
                        mVerification = SendOtpVerification.createSmsVerification
                                (SendOtpVerification
                                        .config("" + no).message("OTP ##OTP## To Verify Your Mobile Number. Click On goo.gl/y7YMmm To Download Retail Assist Application For Any Retail Distribution And Supply Problems.")
                                        .context(getApplicationContext())
                                        .autoVerification(false)
                                        .build(), new VerificationListener() {
                                    @Override
                                    public void onInitiated(String response) {
                                        Log.i(TAG, "otp initiated=" + response);
                                        isMsgSent2=true;
                                        alertDialog.show();
                                    }

                                    @Override
                                    public void onInitiationFailed(Exception paramException) {
                                        Log.i(TAG, "otp initiated error=" + paramException.toString());
                                        Toast.makeText(getApplicationContext(), "network error", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onVerified(String response) {
                                        Log.i(TAG, "otp verified=" + response);
                                        alertDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "OTP Verification Successful", Toast.LENGTH_SHORT).show();
                                        isMobileVerified2 = true;
                                        isMsgSent2 = true;
                                        alternate_mobile_number.setKeyListener(null);
                                        verify_alternate_mobile.setClickable(false);
                                    }

                                    @Override
                                    public void onVerificationFailed(Exception paramException) {
                                        Log.i(TAG, "otp verification failed=" + paramException.toString());
                                        Toast.makeText(getApplicationContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();
                                        if(!alertDialog.isShowing()){
                                            alertDialog.show();
                                        }
                                    }
                                });
                        mVerification.initiate();
                    } else {
                        Toast.makeText(getApplicationContext(),"Enter Valid Mobile Number",Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "first char error=" + no.charAt(0));
                    }
                }
            }
        });
        //When Verify Mobile Button Clicked
        verify_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_mobile.setEnabled(false);
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(),"No Network Available",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isMobileVerified) {
                    if(isMsgSent){
                        Toast.makeText(getApplicationContext(),"Message Already Sent",Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    String no = owner_mobile_number.getText().toString();
                    if (no.isEmpty() || no.length() < 10) {
                        Log.i(TAG, "no format error");
                        verify_mobile.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"Enter valid mobile no",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (no.charAt(0) == '6' || no.charAt(0) == '7' || no.charAt(0) == '8' || no.charAt(0) == '9') {
                        Log.i(TAG, "sending msg");
                        final AlertDialog.Builder builder = new AlertDialog.Builder(CommonEditPage.this);
                        builder.setTitle("Enter Otp");
                        View inflatedView = LayoutInflater.from(CommonEditPage.this).inflate(R.layout.otp_layout, null, false);
                        final EditText otp = (EditText) inflatedView.findViewById(R.id.input);
//                            final ProgressBar progressBar = (ProgressBar) inflatedView.findViewById(R.id.otp_progress);
                        builder.setView(inflatedView);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                    progressBar.setVisibility(View.VISIBLE);
//                                    otp.setVisibility(View.GONE);
                                mVerification.verify(otp.getText().toString());
                                Log.i(TAG, "entered otp=" + otp.getText().toString());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                verify_mobile.setEnabled(true);
                            }
                        });
                        final AlertDialog alertDialog=builder.create();
//                            builder.create();
                        mVerification = SendOtpVerification.createSmsVerification
                                (SendOtpVerification
                                        .config("" + no).message("Share Your OTP To Verify Your Mobile Number. Your OTP is ##OTP##")
                                        .context(getApplicationContext())
                                        .autoVerification(false)
                                        .build(), new VerificationListener() {
                                    @Override
                                    public void onInitiated(String response) {
                                        Log.i(TAG, "otp initiated=" + response);
                                        isMsgSent=true;
                                        alertDialog.show();
                                    }

                                    @Override
                                    public void onInitiationFailed(Exception paramException) {
                                        Log.i(TAG, "otp initiated error=" + paramException.toString());
                                        Toast.makeText(getApplicationContext(), "network error", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onVerified(String response) {
                                        Log.i(TAG, "otp verified=" + response);
                                        alertDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "OTP Verification Successful", Toast.LENGTH_SHORT).show();
                                        isMobileVerified = true;
                                        isMsgSent = true;
                                        owner_mobile_number.setKeyListener(null);
                                        verify_mobile.setClickable(false);
                                    }

                                    @Override
                                    public void onVerificationFailed(Exception paramException) {
                                        Log.i(TAG, "otp verification failed=" + paramException.toString());
                                        Toast.makeText(getApplicationContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();
                                        if(!alertDialog.isShowing()){
                                            alertDialog.show();
                                        }
                                    }
                                });
                        mVerification.initiate();
                    } else {
                        Toast.makeText(getApplicationContext(),"Enter Valid Mobile Number",Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "first char error=" + no.charAt(0));
                    }
                }

            }
        });


//        itc_salesman_visit_store.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
//                isItcChecked=true;
//                if(i==R.id.yes_itc_salesman_visits_store){
//                    itc_salesman_visit=true;
//                    if(is_sell_cigar == true){
//                        layout_itc_cigarette_salesman.setVisibility(View.VISIBLE);
//                        layout_itc_non_cigarette_salesman.setVisibility(View.VISIBLE);
//
//                    }else {
//                        layout_itc_non_cigarette_salesman.setVisibility(View.VISIBLE);
//                    }
//
//                }else if(i==R.id.no_itc_salesman_visits_store){
//                    itc_salesman_visit=false;
//                    layout_itc_cigarette_salesman.setVisibility(View.GONE);
//                    layout_itc_non_cigarette_salesman.setVisibility(View.GONE);
//                }
//            }
//        });

        sell_cigarette.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.yes_sell_cigar_khm){
                    is_sell_cigar=true;
//                    if(itc_salesman_visit == true){
                    layout_itc_cigarette_salesman.setVisibility(View.VISIBLE);
//                        layout_itc_non_cigarette_salesman.setVisibility(View.VISIBLE);
//                    }
                }else if(i==R.id.no_sell_cigar_khm){
                    is_sell_cigar=false;
                    layout_itc_cigarette_salesman.setVisibility(View.GONE);
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
                        open_close_depend_2.setVisibility(View.VISIBLE);
                        break;
                    case R.id.close_khm:
                        open_close="close";
                        open_close_depend.setVisibility(View.GONE);
                        open_close_depend_2.setVisibility(View.GONE);
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
                    case R.id.poojashop:
                        typeofstore="Pooja Shop";
                        break;
                }
            }
        });
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            Toast.makeText(getApplicationContext(),"Enable Gps",Toast.LENGTH_SHORT).show();
//        }else {
//            tracker = new LocationTracker(getApplicationContext(),new TrackerSettings().setTimeBetweenUpdates(1).setMetersBetweenUpdates(1).setUseNetwork(true).setUseGPS(true)) {
//                @Override
//                public void onLocationFound(@NonNull Location location) {
//                    Log.i(TAG,"location acc="+location.getAccuracy());
//                    Log.i(TAG,"lat_value="+lat_value+","+"lng_value="+lng_value);
//                    if(location.getAccuracy()<acc){
//                        lat_value=location.getLatitude();
//                        lng_value=location.getLongitude();
//                        acc=location.getAccuracy();
//                        isInside=checkLatLngStatus();
//                        if(!isInside){
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(),"You Are Outside Of Your Area",Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                        if(acc<15){
//                            accuracy.setText("Location : A");
//                        }else if(acc>25){
//                            accuracy.setText("Location : C");
//                        }else {
//                            accuracy.setText("Location : B");
//                        }
//                    }
//                }
//
//                @Override
//                public void onTimeout() {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(),"Unable To Get Your Location",Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            };
//            tracker.startListening();
//        }

        take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inOrOut=OUTER;
                dispatchTakePictureIntent();
            }
        });

        inner_picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inOrOut=INNER;
                dispatchTakePictureIntent();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Value", "1"+is_sell_cigar);
                Log.i("Value", "2"+itc_salesman_visit);


                if(mImageView.getDrawable()==null){
                    Toast.makeText(getApplicationContext(),"Take Picture",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(acc>25){
//                    Toast.makeText(getApplicationContext(),"Check Should Be A or B",Toast.LENGTH_SHORT).show();
//                    if(tracker.isListening()){
//                        tracker.stopListening();
//                    }
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                tracker.startListening();
//                            }catch (SecurityException e){
//                                Log.i(TAG,"exception="+e.toString());
//                            }
//                        }
//                    },1000);
//                    return;
//                }
                if(store_name.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Store Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(typeofstore.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Select Category Of Store",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(permTemp.getCheckedRadioButtonId()==-1){
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
//                if(owner_mobile_number.getText().toString().length()<10 && alternate_mobile_number.getText().toString().length()<10){
//                    Toast.makeText(getApplicationContext(),"Enter Atleast One Mobile No",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(sell_cigarette.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If Store Sells Cigareete Or Not",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(itc_salesman_visit_store.getCheckedRadioButtonId()==-1){
//                    Toast.makeText(getApplicationContext(), "Select If ITC Salesman Visits The Store",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(is_sell_cigar == false && itc_salesman_visit == true){
//                    if(non_cigarette_salesman_vist_store.getCheckedRadioButtonId()==-1) {
//                        Toast.makeText(getApplicationContext(), "Select If ITC Non Cigarette Salesman Visits The Store", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
                if(is_sell_cigar == true && cigarette_salesman_visit_store.getCheckedRadioButtonId()==-1){
//                    if(cigarette_salesman_visit_store.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(getApplicationContext(), "Select If ITC Cigarette Salesman Visits The Store", Toast.LENGTH_SHORT).show();
                    return;
//                    }
                }
//                if(is_sell_cigar == true && itc_salesman_visit == true){
                if(non_cigarette_salesman_vist_store.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(getApplicationContext(), "Select If ITC Non Cigarette Salesman Visits The Store", Toast.LENGTH_SHORT).show();
                    return;
                }
//                }
                if(nearSchool.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Check If There Is Any Educational Institue Near By",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(sell_cigarette.getCheckedRadioButtonId() == R.id.yes_sell_cigar_khm && itc_salesman_visit_store.getCheckedRadioButtonId() == R.id.yes_itc_cigarette_salesman_visits_store && cigarette_salesman_visit_store.getCheckedRadioButtonId()==-1){
//                    Toast.makeText(getApplicationContext(), "Select If ITC Cigarette Salesman Visits The Store",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(sell_cigarette.getCheckedRadioButtonId() == R.id.yes_sell_cigar_khm && itc_salesman_visit_store.getCheckedRadioButtonId() == R.id.yes_itc_cigarette_salesman_visits_store && non_cigarette_salesman_vist_store.getCheckedRadioButtonId()==-1){
//                    Toast.makeText(getApplicationContext(), "Select If ITC Non Cigarette Salesman Visits The Store",Toast.LENGTH_SHORT).show();
//                    return;
//                }

                saveData();
            }
        });
        updateViews();
    }
    private void saveData(){
        root.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if(isImgChange){
            updateImage();
        }
        JSONObject object =new JSONObject();
        try {
            object.put("store_id", storeid);
//            if (entity.getAudit_Id().isEmpty()) {
//                Log.i(TAG, "empty changing=" + ProductApplication.auditId);
//                object.put("audit_id", ProductApplication.auditId);
//            } else {
//                Log.i(TAG, "not empty=" + entity.getAudit_Id());
//                object.put("audit_id", entity.getAudit_Id());
//            }
//            object.put("latitude", entity.getLatitude());
//            object.put("longitude", entity.getLongitude());
//            object.put("accuracy", entity.getAccuracy());
            object.put("store_name", store_name.getText().toString());
            object.put("landmark", landmark.getText().toString());
            object.put("owner_mobile", owner_mobile_number.getText().toString());
            object.put("store_condition", open_close);
            object.put("store_type", typeofstore);
            object.put("isnearbyteashop", near_by_teashop.isChecked());
            object.put("isnearbywine", wine_shop.isChecked());
            object.put("isnearbydhaba", dhaba.isChecked());
            object.put("isnearbyeducation", nearSchool.getCheckedRadioButtonId()==R.id.yes_nearby_school);
            object.put("isnearbyrail", railway_station.isChecked());
            object.put("isnearbyjunction", road_junction.isChecked());
            object.put("isnearbypetrolpump", petrol_pump.isChecked());
            object.put("isnearbytemple", temple.isChecked());
            object.put("isnearbyhospital", hospital.isChecked());
            object.put("otp_sent", isMsgSent);
            object.put("otp_verified", isMobileVerified);
            object.put("alternate_otp_sent", isMsgSent2);
            object.put("alternate_otp_verified", isMobileVerified2);
            object.put("alternate_mobile", alternate_mobile_number.getText().toString());
            object.put("time", "" + new SimpleDateFormat("yyy.MM.dd.HH.mm.ss", Locale.ENGLISH).format(new Date()));
//            object.put("is_inside", entity.getIsInside());
            object.put("owner_name", owner_name.getText().toString());
            object.put("perm_temp", ((RadioButton) findViewById(permTemp.getCheckedRadioButtonId())).getText().toString());
            object.put("company_dealer_board", companyDealorBoard.getCheckedRadioButtonId()==R.id.yes_company_dealor_board);
            object.put("own_dealer_board", dealorBoard.getCheckedRadioButtonId()==R.id.yes_dealor_board && companyDealorBoard.getCheckedRadioButtonId()==R.id.no_company_dealor_board);
            object.put("is_sell_cigar",sell_cigarette.getCheckedRadioButtonId()==R.id.yes_sell_cigar_khm?sell_cigarette.getCheckedRadioButtonId()==R.id.yes_sell_cigar_khm:false);
//            object.put("time",entity.getSurveyTime());
//                            object.put("is_itc_sales_man",entity.getIsItcSalesmanVisitStore());
            object.put("is_itc_cigarette_sales_man",cigarette_salesman_visit_store.getCheckedRadioButtonId()==R.id.yes_itc_cigarette_salesman_visits_store?cigarette_salesman_visit_store.getCheckedRadioButtonId()==R.id.yes_itc_cigarette_salesman_visits_store:false);
            object.put("is_itc_non_cigarette_sales_man",non_cigarette_salesman_vist_store.getCheckedRadioButtonId()==R.id.yes_itc_non_cigarette_salesman_visits_store?non_cigarette_salesman_vist_store.getCheckedRadioButtonId()==R.id.yes_itc_non_cigarette_salesman_visits_store:false);
        }catch (JSONException e){
            e.printStackTrace();
            Log.i(TAG,"saving exception="+e.toString());
        }
        Retrofit retrofit =new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<String> save=api.saveCommonEditData(object);
        save.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"save null response");
                    Toast.makeText(getApplicationContext(),"Error Updating Details",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"save response="+response.body());
                if(response.body().contentEquals("success")){
                    Toast.makeText(getApplicationContext(),"Details Updated Successfully",Toast.LENGTH_SHORT).show();
                }else if(response.body().contentEquals("no updates")){
                    Toast.makeText(getApplicationContext(),"No Changes Detected For Update",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Error Updating Details",Toast.LENGTH_SHORT).show();
                }
                if(isImgUpdated){
                    finish();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(),"Error Updating Details",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"save error="+t.toString());
                progressBar.setVisibility(View.GONE);
                root.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateViews(){
        Glide.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/storechecker-9a5cb.appspot.com/o/images%2F"+storeid+"-outer.jpeg?alt=media&token=00add794-5450-4cab-b6fc-a6f2072cc57a")
                .into(mImageView);
        Glide.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/storechecker-9a5cb.appspot.com/o/images%2F"+storeid+"-inner.jpeg?alt=media&token=00add794-5450-4cab-b6fc-a6f2072cc57a")
                .into(innerImageView);
        JSONObject object=new JSONObject();
        try {
            object.put("store_id",storeid);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error Getting Details",Toast.LENGTH_SHORT).show();
            return;
        }
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<String> getData=api.getCommonStoreIdDetails(object);
        getData.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.body()==null){
                    Log.i(TAG,"null id details");
                    Toast.makeText(getApplicationContext(),"Error Getting Details",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"id response="+response.body());
                try {
                    JSONObject jsonObject=new JSONObject(response.body());
                    store_name.setText(jsonObject.getString("store_name"));
                    landmark.setText(jsonObject.getString("store_landmark"));
                    if(jsonObject.getString("store_condition").contentEquals("open")) {
                        store_condition.check(R.id.open_khm);
                        open_close="open";
                    }else {
                        store_condition.check(R.id.close_khm);
                        open_close="close";
                    }
                    if(jsonObject.getString("temp_or_permanent").contentEquals("Permanent")){
                        permTemp.check(R.id.permanent_store);
                    }else {
                        permTemp.check(R.id.temporary_store);
                    }
                    owner_name.setText(jsonObject.getString("owner_name"));
                    owner_mobile_number.setText(jsonObject.getString("owner_mobile_no"));
                    if(jsonObject.getInt("otp_verified")==1){
                        verify_mobile.setEnabled(false);
                        isMsgSent=true;
                        isMobileVerified=true;
                    }
                    if (jsonObject.getInt("otp_verify_alternate")==1){
                        verify_alternate_mobile.setEnabled(false);
                        isMsgSent2=true;
                        isMobileVerified2=true;
                    }
                    typeofstore=jsonObject.getString("type_of_store");
                    if(typeofstore.contentEquals("Cigarette + Tea")){
                        store_type.check(R.id.tea_shop_khm);
                    }else if(typeofstore.contentEquals("Cigarette + Pan")){
                        store_type.check(R.id.pan_shop_khm);
                    }else if(typeofstore.contentEquals("Kirana/ General Store")){
                        store_type.check(R.id.kirana_shop_khm);
                    }else if(typeofstore.contentEquals("Bakery/Sweet Shop")){
                        store_type.check(R.id.bakery_khm);
                    }else if(typeofstore.contentEquals("Pharmacy/ Medical store")){
                        store_type.check(R.id.chemist_khm);
                    }else if(typeofstore.contentEquals("Cosmetic Store")){
                        store_type.check(R.id.cosmetic_khm);
                    }else if(typeofstore.contentEquals("Wholesale")){
                        store_type.check(R.id.wholesale_khm);
                    }else if(typeofstore.contentEquals("Supermarket")){
                        store_type.check(R.id.supermarket_khm);
                    }else if(typeofstore.contentEquals("Restaurant / Bar/ Wine Shop")){
                        store_type.check(R.id.type_dhaba_restaurant_khm);
                    }else if(typeofstore.contentEquals("Tiffin/ Breakfast Centre")){
                        store_type.check(R.id.tiffin_breakfast_centre_khm);
                    }else if(typeofstore.contentEquals("Stationery Shop")){
                        store_type.check(R.id.stationary_shop_khm);
                    }else if(typeofstore.contentEquals("Pooja Shop")){
                        store_type.check(R.id.poojashop);
                    }
                    near_by_teashop.setChecked(jsonObject.getInt("near_by_tea_shop")==1);
                    wine_shop.setChecked(jsonObject.getInt("near_by_wines")==1);
                    dhaba.setChecked(jsonObject.getInt("near_by_dhaba")==1);
                    if(jsonObject.getInt("near_by_educational_institute")==1){
                        nearSchool.check(R.id.yes_nearby_school);
                    }else {
                        nearSchool.check(R.id.no_nearby_school);
                    }
                    railway_station.setChecked(jsonObject.getInt("near_by_bus_rail_station")==1);
                    road_junction.setChecked(jsonObject.getInt("near_by_road_junction")==1);
                    petrol_pump.setChecked(jsonObject.getInt("near_by_petrol_pump")==1);
                    temple.setChecked(jsonObject.getInt("near_by_temple")==1);
                    hospital.setChecked(jsonObject.getInt("near_by_hospital")==1);
                    if(jsonObject.getInt("sell_cigarettes")==1){
                        sell_cigarette.check(R.id.yes_sell_cigar_khm);
                        is_sell_cigar=true;
                    }else {
                        sell_cigarette.check(R.id.no_sell_cigar_khm);
                        is_sell_cigar=false;
                    }
                    if(jsonObject.getInt("itc_cig_salesman_visit_store")==1){
                        cigarette_salesman_visit_store.check(R.id.yes_itc_cigarette_salesman_visits_store);
                    }else {
                        cigarette_salesman_visit_store.check(R.id.no_itc_cigarette_salesman_visits_store);
                    }
                    if(jsonObject.getInt("itc_non_cig_salesman_visit_store")==1){
                        non_cigarette_salesman_vist_store.check(R.id.yes_itc_non_cigarette_salesman_visits_store);
                    }else {
                        non_cigarette_salesman_vist_store.check(R.id.no_itc_non_cigarette_salesman_visits_store);
                    }
                    if(jsonObject.getInt("own_dealer_board")==1 || jsonObject.getInt("company_dealer_board")==1){
                        dealorBoard.check(R.id.yes_dealor_board);
                    }else {
                        dealorBoard.check(R.id.no_dealor_board);
                    }
                    if(jsonObject.getInt("company_dealer_board")==1){
                        companyDealorBoard.check(R.id.yes_company_dealor_board);
                    }else {
                        companyDealorBoard.check(R.id.no_company_dealor_board);
                    }
//                    glow_sign.setChecked(jsonObject.getInt("company_dealer_board")==1);
//                    non_lit.setChecked(jsonObject.getInt("own_dealer_board")==1);
                    progressBar.setVisibility(View.GONE);
                    root.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Error Getting Details",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(),"Error Getting Details",Toast.LENGTH_SHORT).show();
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
            isImgChange=true;
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
            if(inOrOut.contentEquals(INNER)){
                bitmap= Bitmap.createScaledBitmap(bitmap,innerImageView.getWidth(),innerImageView.getHeight(),true);
                innerImageView.setImageBitmap(bitmap);
            }else if(inOrOut.contentEquals(OUTER)){
                bitmap= Bitmap.createScaledBitmap(bitmap,mImageView.getWidth(),mImageView.getHeight(),true);
                mImageView.setImageBitmap(bitmap);
            }

        }

    }
    private File createImageFile() throws IOException {
        File image = new File(ConstantValues.imagepath+"/"+storeid+"-"+inOrOut+".jpeg");
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("currentpath","="+mCurrentPhotoPath);
        return image;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(tracker.isListening()) {
//            tracker.stopListening();
//        }
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
        if(ProductApplication.auditId.isEmpty()){
            Toast.makeText(getApplicationContext(),"Login Error Please Try Again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),LoginReg.class));//LoginActivity.class));
            finish();
        }
    }

    private void updateImage(){
        isImgUpdated=true;
        /*
        StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
        final Uri imgfile=Uri.fromFile(new File(mCurrentPhotoPath));//listimgProcess[listimgProcess.length-1]);
        StorageReference imgref=mStorageRef.child("images/"+storeid+".jpeg");
        imgref.putFile(imgfile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Image Updated Successfully", Toast.LENGTH_SHORT).show();
                File tmp=new File(imgfile.getPath());
                if(!tmp.delete()){
                    boolean b = tmp.renameTo(new File(ConstantValues.imagecomplete + "/" + storeid + ".jpeg"));
                    if(!b){
                        Log.i(TAG,"file not deleted not moved");
                    }
                }
                isImgUpdated=true;
                if(isDetailsUpdated){
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                Log.i(TAG,"image error="+e.toString());
                if(isDetailsUpdated){
                    Toast.makeText(getApplicationContext(),"Error Updating Image But Details Updated You May Exit Manually",Toast.LENGTH_SHORT).show();
                }
            }
        });
        */
    }
}
