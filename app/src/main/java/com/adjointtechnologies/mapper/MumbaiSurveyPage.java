package com.adjointtechnologies.mapper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adjointtechnologies.mapper.database.CommonStoreData;
import com.adjointtechnologies.mapper.database.CommonStoreDataEntity;
import com.adjointtechnologies.mapper.database.ExtraStoreDataEntity;
import com.adjointtechnologies.mapper.database.PolygonCornersEntity;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

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

public class MumbaiSurveyPage extends AppCompatActivity {
    private ReactiveEntityStore<Persistable> data;
    String mCurrentPhotoPath="";
    final String TAG="commonauditactivity";
    private boolean goback=false;
    long back_time=0;
    double lat_value,lng_value;
    ImageView mImageView, innerImageView;
    boolean isMsgSent = false;
    boolean isMobileVerified = false;
    boolean isMsgSent2=false;
    boolean isMobileVerified2=false;
    Verification mVerification;
    String inOrOut="";
    String INNER="inner";
    String OUTER="outer";
    boolean isCommonInserted=false;
    boolean isExtraInserted=false;
    LocationTracker tracker = null;
    TextView accuracy;
    private boolean isAudit=false;
    EditText store_name, landmark, owner_mobile_number,owner_name, alternate_mobile_number;
    RadioGroup dealorBoard,storeCategory,nearSchool,nearRailBus,nearApartment,sell_cigarette,sellBiscuits,sellChipsNamkeen,sellSoaps,sellDeodorants
            ,pepsiCokeCooler,cadburyNestleDispenser,openClose,tempPerm;
    private boolean is_sell_cigar=false;
    float acc=100;
    Button take_pic,inner_picture_button,submit, verify_mobile,verify_alternate_mobile;
    String storeid="";
    LinearLayout root;
    File imgfolder;
    boolean isInside=false;
    final int cameraTestReqCode=608;
    AppCompatSpinner issuesSpinner;
    String issue="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mumbai_survey_page);
        data=((ProductApplication)getApplication()).getData();
        storeid=""+ConstantValues.audit_id+System.currentTimeMillis();
        mImageView=(ImageView)findViewById(R.id.imageview_mumbai);
        isAudit=getIntent().getBooleanExtra("isAudit",false);
        innerImageView=(ImageView)findViewById(R.id.imageview_inner_picture_mumbai);
        imgfolder=new File(ConstantValues.imagepath);
        take_pic=(Button)findViewById(R.id.take_pic_mumbai);
        inner_picture_button=(Button)findViewById(R.id.inner_picture_mumbai);
        submit=(Button)findViewById(R.id.submit_mumbai);
        accuracy = (TextView) findViewById(R.id.accuracy_mumbai);
        store_name = (EditText) findViewById(R.id.store_name_mumbai);
        verify_mobile = (Button) findViewById(R.id.verify_mobile);
        verify_alternate_mobile=(Button) findViewById(R.id.verify_alternate_mobile);
        alternate_mobile_number=(EditText) findViewById(R.id.alternate_mobile_number);
        landmark = (EditText) findViewById(R.id.landmark_mumbai);
        owner_mobile_number = (EditText) findViewById(R.id.mobile_mumbai);
        owner_name=(EditText) findViewById(R.id.owner_name);
        issuesSpinner=(AppCompatSpinner) findViewById(R.id.itc_issues);
        root=(LinearLayout)findViewById(R.id.rootlayout_mumbai);
        dealorBoard=(RadioGroup) findViewById(R.id.dealor_board);
        storeCategory=(RadioGroup) findViewById(R.id.store_type_mumbai);
        nearSchool=(RadioGroup) findViewById(R.id.nearby_school);
        nearRailBus=(RadioGroup) findViewById(R.id.nearby_bus_rail);
        nearApartment=(RadioGroup) findViewById(R.id.nearby_apartment);
        sell_cigarette=(RadioGroup) findViewById(R.id.is_sell_cigar_mumbai);
        sellBiscuits=(RadioGroup) findViewById(R.id.is_sell_biscuits_mumbai);
        sellChipsNamkeen=(RadioGroup) findViewById(R.id.is_sell_chips_namkeen_mumbai);
        sellSoaps=(RadioGroup) findViewById(R.id.is_sell_soaps_mumbai);
        sellDeodorants=(RadioGroup) findViewById(R.id.is_sell_deodorants_mumbai);
        pepsiCokeCooler=(RadioGroup) findViewById(R.id.pepsi_coke_cooler);
        cadburyNestleDispenser=(RadioGroup) findViewById(R.id.cadburys_nestle_chocolate_dispenser);
        openClose=(RadioGroup) findViewById(R.id.open_close_mumbai);
        tempPerm=(RadioGroup) findViewById(R.id.perm_temp_mumbai);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(MumbaiSurveyPage.this);
            }
        });
        issuesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                issue=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MumbaiSurveyPage.this);
                        builder.setTitle("Enter Otp");
                        View inflatedView = LayoutInflater.from(MumbaiSurveyPage.this).inflate(R.layout.otp_layout, null, false);
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
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MumbaiSurveyPage.this);
                        builder.setTitle("Enter Otp");
                        View inflatedView = LayoutInflater.from(MumbaiSurveyPage.this).inflate(R.layout.otp_layout, null, false);
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
                                        .config("" + no).message("OTP ##OTP## To Verify Your Mobile Number. Click On goo.gl/y7YMmm To Download Retail Assist Application For Any Retail Distribution And Supply Problems.")
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
            tracker = new LocationTracker(getApplicationContext(),new TrackerSettings().setTimeBetweenUpdates(1).setMetersBetweenUpdates(1).setUseNetwork(true).setUseGPS(true)) {
                @Override
                public void onLocationFound(@NonNull Location location) {
                    Log.i(TAG,"location acc="+location.getAccuracy());
                    Log.i(TAG,"lat_value="+lat_value+","+"lng_value="+lng_value);
                    if(location.getAccuracy()<acc || location.getAccuracy()<10){
                        lat_value=location.getLatitude();
                        lng_value=location.getLongitude();
                        acc=location.getAccuracy();
//                        if(isAudit){
//                            isInside=true;
//                        }else {
                        isInside=checkLatLngStatus();
//                        }
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
                if(mImageView.getDrawable()==null){
                    Toast.makeText(getApplicationContext(),"Take Outer Picture",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(store_name.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Store Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(storeCategory.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select Store Category",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(owner_mobile_number.getText().toString().length()!=10 && owner_mobile_number.getText().toString().length()!=11 && owner_mobile_number.getText().toString().length()!=0){
                    Toast.makeText(getApplicationContext(),"Enter Valid Mobile number",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(nearSchool.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If There Is Any Educational Institute Near By",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(nearRailBus.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If There Is Any Railway Station/Bus Stand Near By",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(nearApartment.getCheckedRadioButtonId()==-1){
//                    Toast.makeText(getApplicationContext(),"Select If There Is Any Apartment Near By",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(dealorBoard.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If Store Contains Dealer Board",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(pepsiCokeCooler.getCheckedRadioButtonId()==-1){
//                    Toast.makeText(getApplicationContext(),"Select If Store Contains Pepsi/Coke Cooler",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(cadburyNestleDispenser.getCheckedRadioButtonId()==-1){
//                    Toast.makeText(getApplicationContext(),"Select If Store Contains Cadburys/Nestle Chocolate Dispenser",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(tempPerm.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select Store Type",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(openClose.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select Stpre Condition",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sell_cigarette.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If Store Sells Cigarettes",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sellBiscuits.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If Store Sells Biscuits",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sellChipsNamkeen.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If Store Sells Chips/Namkeen",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sellSoaps.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If Store Sells Soaps",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sellDeodorants.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Select If Store Sells Deodorants",Toast.LENGTH_SHORT).show();
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

                saveData();
            }
        });
    }
    private void saveData(){
        CommonStoreDataEntity entity=new CommonStoreDataEntity();
        ExtraStoreDataEntity extraData=new ExtraStoreDataEntity();
        extraData.setStoreId(storeid);
        entity.setStoreId(storeid);
        entity.setLatitude(lat_value);
        entity.setLongitude(lng_value);
        entity.setAccuracy(acc);
        entity.setStoreName(store_name.getText().toString());
        entity.setLandmark(landmark.getText().toString());
        entity.setOwnerName(owner_name.getText().toString());
        entity.setMobileNo(owner_mobile_number.getText().toString());
        entity.setStoreType(((RadioButton)findViewById(storeCategory.getCheckedRadioButtonId())).getText().toString());
        entity.setStoreCondition(((RadioButton)findViewById(openClose.getCheckedRadioButtonId())).getText().toString());
        entity.setPermTemp(((RadioButton) findViewById(tempPerm.getCheckedRadioButtonId())).getText().toString());
        entity.setisNearByEducation(nearSchool.getCheckedRadioButtonId()==R.id.yes_nearby_school);
        entity.setIsNearByrailbus(nearRailBus.getCheckedRadioButtonId()==R.id.yes_nearby_bus_rail);
        entity.setIsNearByApartments(nearApartment.getCheckedRadioButtonId()==R.id.yes_nearby_apartment);
        entity.setAlternateMobile(alternate_mobile_number.getText().toString());
        entity.setOtpSent(isMsgSent);
        entity.setOtpVerified(isMobileVerified);
        entity.setOtpSent2(isMsgSent2);
        entity.setOtpVerified2(isMobileVerified2);
        extraData.setIsSellCigarettes(sell_cigarette.getCheckedRadioButtonId()==R.id.yes_sell_cigar_mumbai?sell_cigarette.getCheckedRadioButtonId()==R.id.yes_sell_cigar_mumbai:false);
        extraData.setIsSellBiscuits(sellBiscuits.getCheckedRadioButtonId()==R.id.yes_sell_biscuits_mumbai);
        extraData.setIsSellChipsNamkeen(sellChipsNamkeen.getCheckedRadioButtonId()==R.id.yes_sell_chips_namkeen_mumbai);
        extraData.setIsSellSoaps(sellSoaps.getCheckedRadioButtonId()==R.id.yes_sell_soaps_mumbai);
        extraData.setIsSellDeodorants(sellDeodorants.getCheckedRadioButtonId()==R.id.yes_sell_deodorants_mumbai);
        extraData.setIsPepsiCokeCooler(pepsiCokeCooler.getCheckedRadioButtonId()==R.id.yes_pepsi_coke_cooler);
        extraData.setIsCadburyNestleChocolateDispenser(cadburyNestleDispenser.getCheckedRadioButtonId()==R.id.yes_cadburys_nestle_chocolate_dispenser);
        entity.setSurveyTime("" + new SimpleDateFormat("yyy.MM.dd.HH.mm.ss", Locale.ENGLISH).format(new Date()));
        entity.setSyncStatus(false);
        extraData.setSurveyTime("" + new SimpleDateFormat("yyy.MM.dd.HH.mm.ss", Locale.ENGLISH).format(new Date()));
        extraData.setSyncStatus(false);
        extraData.setItcIsuue(issue);
        extraData.setAudit_Id(ProductApplication.auditId);
        entity.setAudit_Id(ProductApplication.auditId);
//        entity.setIsGlow_Sign_Dealor_Board(companyDealorBoard.getCheckedRadioButtonId()==R.id.yes_company_dealor_board);
        entity.setISNon_Lit_Dealor_Board(dealorBoard.getCheckedRadioButtonId()==R.id.yes_dealor_board);
        entity.setIsInside(isInside);
        data.insert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<CommonStoreDataEntity>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull CommonStoreDataEntity storeDataKhmEntity) throws Exception {
                isCommonInserted=true;
                if(isExtraInserted) {
                    Toast.makeText(getApplicationContext(), "details saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        data.insert(extraData).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<ExtraStoreDataEntity>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull ExtraStoreDataEntity extraStoreDataEntity) throws Exception {
                isExtraInserted=true;
                if(isCommonInserted) {
                    Toast.makeText(getApplicationContext(), "details saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void dispatchTakePictureIntent() {
        Intent newIntent=new Intent(getApplicationContext(),CameraTest.class);
        newIntent.putExtra("storeid",storeid);
        newIntent.putExtra("inorout",inOrOut);
        startActivityForResult(newIntent,cameraTestReqCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(requestCode == cameraTestReqCode && resultCode == RESULT_OK){
            Log.i(TAG,"result ok cameratest");
            mCurrentPhotoPath=ConstantValues.imagepath+"/"+storeid+"-"+inOrOut+".jpeg";
            File imgfile=new File(mCurrentPhotoPath);
            if(imgfile==null || !imgfile.exists()){
                Toast.makeText(getApplicationContext(),"Error Viewing Image",Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap bitmap= BitmapFactory.decodeFile(mCurrentPhotoPath);
            FileOutputStream out=null;
            try{
                out=new FileOutputStream(imgfile);
                if(out !=null){
                    boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                }
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
                Glide.with(getApplicationContext()).load(mCurrentPhotoPath).into(innerImageView);
            }else if(inOrOut.contentEquals(OUTER)){
                Glide.with(getApplicationContext()).load(mCurrentPhotoPath).into(mImageView);
            }
        }else {
            Log.i(TAG,"unknown result");
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
    private boolean checkLatLngStatus(){
        LatLng latLng=new LatLng(lat_value,lng_value);
        if(latLng==null){
            Toast.makeText(getApplicationContext(),"Unable Get Your Location",Toast.LENGTH_LONG).show();
            return false;
        }
        boolean pointStatus = false;
        List<PolyCorners> polyCornerses=new ArrayList<>();
        List<LatLng> corners;
        List<Tuple> tuples = data.select(PolygonCornersEntity.POLYGON_KEY).distinct().where(PolygonCornersEntity.MAPPER_ID.eq(ProductApplication.auditId)).get().toList();
        for(int i=0;i<tuples.size();i++){
            corners=new ArrayList<>();
            Log.i("polygon","tuples[i]="+tuples.get(i));
            List<PolygonCornersEntity> test = data.select(PolygonCornersEntity.class).where(PolygonCornersEntity.POLYGON_KEY.eq(tuples.get(i).toString().substring(tuples.get(i).toString().indexOf('[')+2,tuples.get(i).toString().indexOf(']')-1))).get().toList();
            for(int j=0;j<test.size();j++){
                corners.add(new LatLng(test.get(j).getLatitude(),test.get(j).getLongitude()));
            }
            if(test.size()>0)
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
