package com.adjointtechnologies.mapper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adjointtechnologies.mapper.commonadmins.CommonAgencyPage;
import com.adjointtechnologies.mapper.commonadmins.CommonCityPage;
import com.adjointtechnologies.mapper.commonadmins.CommonSupervisorPage;
import com.adjointtechnologies.mapper.database.DeviceInfoEntity;
import com.adjointtechnologies.mapper.database.MapperInfo;
import com.adjointtechnologies.mapper.database.MapperInfoEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.adjointtechnologies.mapper.ConstantValues.ANT_MAPPING;
import static com.adjointtechnologies.mapper.ConstantValues.BENGALURU_MAPPING;
import static com.adjointtechnologies.mapper.ConstantValues.COMMON_AGENCY;
import static com.adjointtechnologies.mapper.ConstantValues.COMMON_AUDIT;
import static com.adjointtechnologies.mapper.ConstantValues.COMMON_CITY;
import static com.adjointtechnologies.mapper.ConstantValues.COMMON_DASHBOARD;
import static com.adjointtechnologies.mapper.ConstantValues.COMMON_MAPPING;
import static com.adjointtechnologies.mapper.ConstantValues.COMMON_SUPERVISOR;
import static com.adjointtechnologies.mapper.ConstantValues.HYD_AUDIT;
import static com.adjointtechnologies.mapper.ConstantValues.HYD_MAPPING;
import static com.adjointtechnologies.mapper.ConstantValues.KHM_MAPPING;
import static com.adjointtechnologies.mapper.ConstantValues.MUMBAI_MAPPING;

public class LoginReg extends AppCompatActivity {

    final String TAG = "loginreg";
    EditText loginid, mobile, vectorName;
    Button login, newUser, verify, register, loginView;
    LinearLayout loginForm, regForm;
    boolean isMsgSent = false;
    boolean isMobileVerified = false;
    Verification mVerification;
    LinearLayout mLoginFormView;
    ProgressBar mProgressView;
    private ReactiveEntityStore<Persistable> data;
    boolean islogging = false;
    int surveyId = 0;
    //    AppCompatSpinner
    com.toptoche.searchablespinnerlibrary.SearchableSpinner citySpinner;
    //    List<String> cityList=new ArrayList<>();
    ArrayAdapter<String> spinnerAdapter;

    com.toptoche.searchablespinnerlibrary.SearchableSpinner superviser_spinner;
    //    List<String> superviserList =new ArrayList<>();
    ArrayAdapter<String> superviserSpinnerAdapter;
    String city = "";
    String superviser = "";
    String agency = "";

    SearchableSpinner agencySpinner;
    ArrayAdapter<String> agencySpinnerAdapter;


    private List<String> cityIdList = new ArrayList<>();
    private List<String> cityNameList = new ArrayList<>();

    private List<String> agencyIdList = new ArrayList<>();
    private List<String> agencyNameList = new ArrayList<>();

    private List<String> superVisorIdList = new ArrayList<>();
    private List<String> superVisorNameList = new ArrayList<>();

    Retrofit retrofit;
    RetrofitProductApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_reg);
        retrofit = new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(RetrofitProductApi.class);
        loginid = (EditText) findViewById(R.id.login_id);
        mobile = (EditText) findViewById(R.id.mobile_no);
        vectorName = (EditText) findViewById(R.id.vector_name);
        login = (Button) findViewById(R.id.login);
        newUser = (Button) findViewById(R.id.new_user);
        verify = (Button) findViewById(R.id.verify_mobile);
        register = (Button) findViewById(R.id.register);
        loginForm = (LinearLayout) findViewById(R.id.login_form);
        regForm = (LinearLayout) findViewById(R.id.regestration_form);
        loginView = (Button) findViewById(R.id.login_view);
        data = ((ProductApplication) getApplication()).getData();
        mLoginFormView = (LinearLayout) findViewById(R.id.root_login_reg);
        mProgressView = (ProgressBar) findViewById(R.id.progress_login_reg);
        citySpinner = (SearchableSpinner) findViewById(R.id.city_spinner);
        citySpinner.setTitle("Select City");
        agencySpinner = (SearchableSpinner) findViewById(R.id.agency_spinner);
        agencySpinner.setTitle("Select Agency");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempOfflineLogin();
                if (loginid.getText().toString().length() != 10 && !islogging) {
                    Toast.makeText(getApplicationContext(), "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginCheck();
            }
        });
        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, cityNameList);//cityList);
        citySpinner.setAdapter(spinnerAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                agent_id=adapterView.getItemAtPosition(i).toString();
                if (cityIdList.size() > i) {
                    city = cityIdList.get(i);
                    getAgencyIdList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        agencySpinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, agencyNameList);
        agencySpinner.setAdapter(agencySpinnerAdapter);
        agencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (agencyIdList.size() > i) {
                    agency = agencyIdList.get(i);
                    getSuperVisorIdList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        superviser_spinner = (SearchableSpinner) findViewById(R.id.superviser_spinner);
        superviser_spinner.setTitle("Team");
        superviserSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, superVisorNameList);//superviserList);
        superviser_spinner.setAdapter(superviserSpinnerAdapter);
        superviser_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (superVisorIdList.size() > i) {
                    superviser = superVisorIdList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regForm.setVisibility(View.GONE);
                loginForm.setVisibility(View.VISIBLE);
            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginForm.getVisibility() == View.VISIBLE) {
                    loginForm.setVisibility(View.GONE);
                    regForm.setVisibility(View.VISIBLE);
                } else {
                    regForm.setVisibility(View.GONE);
                    loginForm.setVisibility(View.VISIBLE);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMobileVerified && !vectorName.getText().toString().isEmpty()) {
                    regesterMobile();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Verify Your Mobile Number To Proceed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          verify.setEnabled(false);
                                          if (!isNetworkAvailable()) {
                                              Toast.makeText(getApplicationContext(), "No Network Available", Toast.LENGTH_SHORT).show();
                                              return;
                                          }
                                          if (isMobileVerified) {
                                              if (isMsgSent) {
                                                  Toast.makeText(getApplicationContext(), "Message Already Sent", Toast.LENGTH_LONG).show();
                                                  return;
                                              }
                                          } else {
                                              String no = mobile.getText().toString();
                                              if (no.isEmpty() || no.length() < 10) {
                                                  Log.i(TAG, "no format error");
                                                  verify.setEnabled(true);
                                                  Toast.makeText(getApplicationContext(), "Enter valid mobile no", Toast.LENGTH_SHORT).show();
                                                  return;
                                              }
                                              if (no.charAt(0) == '7' || no.charAt(0) == '8' || no.charAt(0) == '9' || no.charAt(0) == '6') {
                                                  Log.i(TAG, "sending msg");
                                                  final AlertDialog.Builder builder = new AlertDialog.Builder(LoginReg.this);
                                                  builder.setTitle("Enter Otp");
                                                  View inflatedView = LayoutInflater.from(LoginReg.this).inflate(R.layout.otp_layout, null, false);
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
                                                          verify.setEnabled(true);
                                                      }
                                                  });
                                                  final AlertDialog alertDialog = builder.create();
//                            builder.create();
                                                  mVerification = SendOtpVerification.createSmsVerification
                                                          (SendOtpVerification
                                                                  .config("" + no).message("Share Your OTP To Verify Your Mobile Number. Your OTP is ##OTP##")
                                                                  .context(getApplicationContext())
                                                                  .autoVerification(true)
                                                                  .build(), new VerificationListener() {
                                                              @Override
                                                              public void onInitiated(String response) {
                                                                  Log.i(TAG, "otp initiated=" + response);
                                                                  isMsgSent = true;
                                                                  alertDialog.show();
                                                              }

                                                              @Override
                                                              public void onInitiationFailed(Exception paramException) {
                                                                  Log.i(TAG, "otp initiated error=" + paramException.toString());
                                                                  Toast.makeText(getApplicationContext(), "network error while sending OTP", Toast.LENGTH_SHORT).show();
                                                                  verify.setEnabled(true);
                                                              }

                                                              @Override
                                                              public void onVerified(String response) {
                                                                  Log.i(TAG, "otp verified=" + response);
                                                                  alertDialog.dismiss();
                                                                  Toast.makeText(getApplicationContext(), "OTP Verification Successful", Toast.LENGTH_SHORT).show();
                                                                  isMobileVerified = true;
                                                                  isMsgSent = true;
                                                                  mobile.setKeyListener(null);
                                                                  verify.setClickable(false);
                                                              }

                                                              @Override
                                                              public void onVerificationFailed(Exception paramException) {
                                                                  Log.i(TAG, "otp verification failed=" + paramException.toString());
                                                                  Toast.makeText(getApplicationContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();
                                                                  if (!alertDialog.isShowing()) {
                                                                      alertDialog.show();
                                                                  }
                                                              }
                                                          });
                                                  mVerification.initiate();
                                              } else {
                                                  Toast.makeText(getApplicationContext(), "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                                                  Log.i(TAG, "first char error=" + no.charAt(0));
                                              }
                                          }
                                      }
                                  }
        );
        getCityList();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loginCheck() {
        if (islogging) {
            Log.i(TAG, "logging returning");
            return;
        }
        final String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String serviceType = Context.TELEPHONY_SERVICE;
        int sdkVersion = Build.VERSION.SDK_INT;
        String codename = Build.VERSION.RELEASE;
        TelephonyManager m_telephonyManager = (TelephonyManager) this.getSystemService(serviceType);
//    deviceId = "" + m_telephonyManager.getDeviceId();
        final String OsVersion = m_telephonyManager.getDeviceSoftwareVersion();
        String ownerName = "";
        final AccountManager manager = AccountManager.get(this);
        final Account[] accounts = manager.getAccountsByType("com.google");
        final int size = accounts.length;
//    String[] names = new String[size];
//    for (int i = 0; i < size; i++) {
//        names[i] = accounts[i].name;
//        Log.i(Tag,"name="+accounts[i].toString());
//    }
        if (size > 0)
            ownerName = accounts[0].name;
        JSONObject object = new JSONObject();
        try {
            object.put("deviceid", deviceId);
            object.put("osversion", OsVersion);
            object.put("application_version", ConstantValues.app_version);
            object.put("sdk_version", sdkVersion);
            object.put("version_release", codename);
            object.put("owner_name", ownerName);
            object.put("mobile", loginid.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error While Logging", Toast.LENGTH_SHORT).show();
            return;
        }
//        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(new ToStringConverterFactory())
//                .addConverterFactory(GsonConverterFactory.create()).build();
//        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<String> login = api.checkLogin(object);
        login.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.body() != null) {
                    Log.i(TAG, "responce=" + response.body());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        if (jsonObject.getString("status").contentEquals("OK")) {
//                        data.delete(DeviceInfoEntity.class).get().value();
                            data.delete(MapperInfoEntity.class).get().value();
                            jsonObject = jsonObject.getJSONObject("0");
                            data.delete(DeviceInfoEntity.class).get().value();
                            DeviceInfoEntity entity = new DeviceInfoEntity();
                            ConstantValues.audit_id = jsonObject.getString("mapper_id");
                            surveyId = jsonObject.getInt("survey_id");
                            ProductApplication.auditId = ConstantValues.audit_id;
                            ProductApplication.auditName = jsonObject.getString("owner_name");
                            entity.setAuditId(ConstantValues.audit_id);
                            entity.setDeviceId(deviceId);
                            entity.setOsVersion(OsVersion);
                            entity.setMobileNo(loginid.getText().toString());
                            entity.setAppVersion(ConstantValues.app_version);
                            data.insert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<DeviceInfoEntity>() {
                                @Override
                                public void accept(@NonNull DeviceInfoEntity deviceInfoEntity) throws Exception {
//                                ConstantValues.audit_id=loginid.getText().toString();
//                                ((ProductApplication)getApplication()).setVector_id(ConstantValues.audit_id);
                                    MapperInfoEntity entity = new MapperInfoEntity();
                                    entity.setAuditId(ConstantValues.audit_id);
                                    entity.setAuditName(ProductApplication.auditName);
                                    entity.setSurveyId(surveyId);
                                    data.insert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<MapperInfoEntity>() {
                                        @Override
                                        public void accept(@NonNull MapperInfoEntity mapperInfoEntity) throws Exception {
                                            startNextActivity();
                                        }
                                    });
                                }
                            });
                        } else {
                            if (jsonObject.getString("status").contentEquals("error")) {
                                Toast.makeText(getApplicationContext(), "Mobile Number Or Device Changed Try Register Again", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error Logging Contact Support Team", Toast.LENGTH_SHORT).show();
                            }
                            mLoginFormView.setVisibility(View.VISIBLE);
                            mProgressView.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "exception=" + e.toString());
                        mLoginFormView.setVisibility(View.VISIBLE);
                        mProgressView.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error Logging Contact Support Team", Toast.LENGTH_SHORT).show();
                    }

                }
                mLoginFormView.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(),"Error Logging Contact Support Team",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG, "error=" + t.toString());
                mLoginFormView.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Error Logging Contact Support Team", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void regesterMobile() {
        if (city.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select City", Toast.LENGTH_SHORT).show();
            return;
        }
//        if(superviser.isEmpty()){
//            Toast.makeText(getApplicationContext(),"Select Team",Toast.LENGTH_SHORT).show();
//            return;
//        }
        final String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String serviceType = Context.TELEPHONY_SERVICE;
        int sdkVersion = Build.VERSION.SDK_INT;
        String codename = Build.VERSION.RELEASE;
        TelephonyManager m_telephonyManager = (TelephonyManager) this.getSystemService(serviceType);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(),"Enable Telephone Permission To Continue",Toast.LENGTH_LONG).show();
            return;
        }
        final String OsVersion = m_telephonyManager.getDeviceSoftwareVersion();
        String ownerName="";
        final AccountManager manager = AccountManager.get(this);
        final Account[] accounts = manager.getAccountsByType("com.google");
        final int size = accounts.length;
        if(size>0)
            ownerName=accounts[0].name;
        JSONObject object=new JSONObject();
        try {
            object.put("deviceid",deviceId);
            object.put("osversion",OsVersion);
            object.put("application_version",ConstantValues.app_version);
            object.put("sdk_version",sdkVersion);
            object.put("version_release",codename);
            object.put("owner_name",vectorName.getText().toString());
            object.put("mobile",mobile.getText().toString());
            object.put("city",city);
            object.put("agency",agency);
            object.put("supervisor",superviser);
        } catch (JSONException e) {
            e.printStackTrace();
            mLoginFormView.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Error While Registering",Toast.LENGTH_SHORT).show();
            return;
        }
//        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(new ToStringConverterFactory())
//                .addConverterFactory(GsonConverterFactory.create()).build();
//        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<String> reg=api.register(object);
        reg.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null response");
                    mLoginFormView.setVisibility(View.VISIBLE);
                    mProgressView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Error While Regestering",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"register response="+response.body());
                try {
                    JSONObject jsonObject=new JSONObject(response.body());
                    if(jsonObject.getString("status").contentEquals("OK")){
                        data.delete(DeviceInfoEntity.class).get().value();
                        data.delete(MapperInfoEntity.class).get().value();
                        DeviceInfoEntity entity=new DeviceInfoEntity();
                        ConstantValues.audit_id=jsonObject.getString("mapper_id");
                        surveyId=jsonObject.getInt("survey_id");
                        ProductApplication.auditId=ConstantValues.audit_id;
                        ProductApplication.auditName=vectorName.getText().toString();
                        entity.setAuditId(ConstantValues.audit_id);
                        entity.setDeviceId(deviceId);
                        entity.setOsVersion(OsVersion);
                        entity.setMobileNo(loginid.getText().toString());
                        entity.setAppVersion(ConstantValues.app_version);
                        data.insert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<DeviceInfoEntity>() {
                            @Override
                            public void accept(@NonNull DeviceInfoEntity deviceInfoEntity) throws Exception {
//                                ConstantValues.audit_id=loginid.getText().toString();
//                                ((ProductApplication)getApplication()).setVector_id(ConstantValues.audit_id);
                                MapperInfoEntity entity=new MapperInfoEntity();
                                entity.setAuditId(ConstantValues.audit_id);
                                entity.setSurveyId(surveyId);
                                entity.setAuditName(vectorName.getText().toString());
                                data.insert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<MapperInfoEntity>() {
                                    @Override
                                    public void accept(@NonNull MapperInfoEntity mapperInfoEntity) throws Exception {
                                        startNextActivity();
                                    }
                                });
                            }
                        });
//                        ConstantValues.audit_id=jsonObject.getString("mapper_id");
//                        ((ProductApplication)getApplication()).setVector_id(ConstantValues.audit_id);
//                        startNextActivity();
                    }else {
                        mLoginFormView.setVisibility(View.VISIBLE);
                        mProgressView.setVisibility(View.GONE);

                        Toast.makeText(getApplicationContext(),"Error While Registering",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mLoginFormView.setVisibility(View.VISIBLE);
                    mProgressView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Error While Registering",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                mLoginFormView.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Error While Registering",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"error="+t.toString());
            }
        });

    }

    private void startNextActivity(){
//        if(ConstantValues.audit_id.contentEquals("admin") || ConstantValues.audit_id.contentEquals("super") || ConstantValues.audit_id.contentEquals("itcadmin")){
//            //TO DO start admin activity
//            startActivity(new Intent(getApplicationContext(),AdminPage.class));
//            finish();
//        }else {
//            startActivity(new Intent(getApplicationContext(),HomePage.class));
//            finish();
//        }
        ProductApplication.surveyId=surveyId;
        if(ConstantValues.audit_id.contentEquals("admin") || ConstantValues.audit_id.contentEquals("super")){
            Log.i(TAG,"starting admin activity");
            startActivity(new Intent(getApplicationContext(),AdminPage.class));
            finish();
        }else if(ConstantValues.audit_id.isEmpty() || surveyId==0){
            Log.i(TAG,"auditid empty starting login activity id="+ConstantValues.audit_id+" surveyid="+surveyId);
            startActivity(new Intent(getApplicationContext(), LoginReg.class));// LoginActivity.class));
            finish();
        }else if(ConstantValues.audit_id.contentEquals("AUD999")){
            Log.i(TAG,"starting home activity for aud999");
            startActivity(new Intent(getApplicationContext(), MultiHomePageForAud999.class));
            finish();
        }else {
            switch (surveyId){
                case KHM_MAPPING:
                    startActivity(new Intent(getApplicationContext(),HomePageKhm.class));
                    finish();
                    break;
                case HYD_MAPPING:
                    startActivity(new Intent(getApplicationContext(),HomePage.class));
                    finish();;
                    break;
                case ANT_MAPPING:
                    startActivity(new Intent(getApplicationContext(),HomePageAnt.class));
                    finish();
                    break;
                case COMMON_MAPPING:
                case MUMBAI_MAPPING:
                case BENGALURU_MAPPING:
                    startActivity(new Intent(getApplicationContext(),CommonHomePage.class));
                    finish();
                    break;
                case COMMON_DASHBOARD:
                    Intent intent=new Intent(getApplicationContext(),CommonDashBoard.class);
                    intent.putExtra("type",surveyId);
                    startActivity(intent);
                    finish();
                    break;
                case COMMON_AGENCY:
                    Intent intent1=new Intent(getApplicationContext(),CommonAgencyPage.class); //CommonDashBoard.class)
                    intent1.putExtra("type",surveyId);
                    startActivity(intent1);
                    finish();
                    break;
                case COMMON_CITY:
                    Intent cityIntent=new Intent(getApplicationContext(), CommonCityPage.class);
                    cityIntent.putExtra("type",surveyId);
                    startActivity(cityIntent);
                    finish();
                    break;
                case COMMON_SUPERVISOR:
                    Intent intent2=new Intent(getApplicationContext(), CommonSupervisorPage.class);//CommonDashBoard.class)
                    intent2.putExtra("type",surveyId);
                    startActivity(intent2);
                    finish();
                    break;
                case COMMON_AUDIT:
                    Intent intent3=new Intent(getApplicationContext(),CommonHomePage.class);
                    intent3.putExtra("isAudit",true);
                    startActivity(intent3);
                    finish();
                    break;
                case HYD_AUDIT:
                    Intent hydAudit=new Intent(getApplicationContext(),HomePage.class);
                    hydAudit.putExtra("isAudit",true);
                    startActivity(hydAudit);
                    finish();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Try Close The App And Open Again If Problem Persists Contact Support Team",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    private void attempOfflineLogin(){
//        mLoginFormView.setVisibility(View.GONE);
//        mProgressView.setVisibility(View.VISIBLE);
        islogging=true;
        List<DeviceInfoEntity> deviceInfoEntities = data.select(DeviceInfoEntity.class).get().toList();
        final HashMap<String,String> loginIds=new HashMap<>();
//        loginIds.put("itcadmin","admin");
        loginIds.put("super","1199");
        loginIds.put("AUD999","5566");
//        loginIds.put("SUR001","1111");
//        loginIds.put("SUR002","2222");
//        loginIds.put("SUR003","3333");
//        loginIds.put("SUR004","4444");
//        loginIds.put("SUR005","5555");
//        loginIds.put("SUR006","6666");
//        loginIds.put("SUR007","7777");
//        loginIds.put("SUR008","8888");
//        loginIds.put("SUR009","9999");
//        loginIds.put("SUR010","1010");
//        loginIds.put("SUR011","1111");
//        loginIds.put("SUR012","1212");
//        loginIds.put("SUR013","1313");
//        loginIds.put("SUR014","1414");
//        loginIds.put("SUR015","1515");
//        loginIds.put("SUR016","1616");
//        loginIds.put("SUR017","1717");
//        loginIds.put("SUR018","1818");
//        loginIds.put("SUR019","1919");
//        loginIds.put("SUR020","2020");
//        loginIds.put("SUR021","2121");
//        loginIds.put("SUR022","2222");
//        loginIds.put("SUR023","2323");
//        loginIds.put("SUR024","2424");
//        loginIds.put("SUR025","2525");
//        loginIds.put("SUR026","2626");
//        loginIds.put("SUR027","2727");
//        loginIds.put("SUR028","2828");
//        loginIds.put("SUR029","2929");
//        loginIds.put("SUR030","3030");
//        loginIds.put("SUR031","3131");
        loginIds.put("admin","admin");
        if(!loginIds.containsKey(loginid.getText().toString())){
//            Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();
//            mLoginFormView.setVisibility(View.VISIBLE);
//            mProgressView.setVisibility(View.GONE);
            islogging=false;
            return;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginReg.this);
        builder.setTitle("Enter Password");
        View inflatedView = LayoutInflater.from(LoginReg.this).inflate(R.layout.password_popup, null, false);
        final EditText otp = (EditText) inflatedView.findViewById(R.id.input);
//                            final ProgressBar progressBar = (ProgressBar) inflatedView.findViewById(R.id.otp_progress);
        builder.setView(inflatedView);
        builder.setCancelable(false);
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                                    progressBar.setVisibility(View.VISIBLE);
//                                    otp.setVisibility(View.GONE);
                if(loginIds.get(loginid.getText().toString()).contentEquals(otp.getText().toString())){
                    int count=data.count(DeviceInfoEntity.class).where(DeviceInfoEntity.AUDIT_ID.eq(loginid.getText().toString())).get().value();
                    Log.i(TAG,"count="+count);
                    if(count==0){
//                        Toast.makeText(getApplicationContext(),"Try Online Login",Toast.LENGTH_SHORT).show();
                        attemptOnlineLogin();
                        islogging=false;
                        return;
                    }
                    MapperInfoEntity entity=new MapperInfoEntity();
                    entity.setAuditId(loginid.getText().toString());
//                    entity.setSurveyId();
                    data.insert(entity).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io())
                            .subscribe(new Consumer<MapperInfoEntity>() {
                                @Override
                                public void accept(@NonNull MapperInfoEntity auditInfoEntity) throws Exception {
                                    ConstantValues.audit_id=loginid.getText().toString();
                                    ProductApplication.auditId=ConstantValues.audit_id;
                                    startNextActivity();
                                }
                            });
                }else {
                    mLoginFormView.setVisibility(View.VISIBLE);
                    mProgressView.setVisibility(View.GONE);
                    islogging=false;
                    Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                verify.setEnabled(true);
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void attemptOnlineLogin() {
        islogging=true;
        final String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String serviceType = Context.TELEPHONY_SERVICE;
        int sdkVersion = Build.VERSION.SDK_INT;
        String codename = Build.VERSION.RELEASE;
        TelephonyManager m_telephonyManager = (TelephonyManager) this.getSystemService(serviceType);
//    deviceId = "" + m_telephonyManager.getDeviceId();
        final String OsVersion = m_telephonyManager.getDeviceSoftwareVersion();
        String ownerName = "";
        final AccountManager manager = AccountManager.get(this);
        final Account[] accounts = manager.getAccountsByType("com.google");
        final int size = accounts.length;
//    String[] names = new String[size];
//    for (int i = 0; i < size; i++) {
//        names[i] = accounts[i].name;
//        Log.i(Tag,"name="+accounts[i].toString());
//    }
        if (size > 0)
            ownerName = accounts[0].name;
        JSONObject object = new JSONObject();
        try {
            object.put("deviceid", deviceId);
            object.put("audit_id", loginid.getText().toString());
            object.put("osversion", OsVersion);
            object.put("application_version", ConstantValues.app_version);
            object.put("sdk_version", sdkVersion);
            object.put("version_release", codename);
            object.put("owner_name", ownerName);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error While Logging", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<String> login = api.checkDevice(object);
        login.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                islogging=false;
                if (response.body() != null) {
                    Log.i(TAG, "responce=" + response.body());
                    if (response.body().contains("success")) {
                        try {
                            Log.i(TAG,""+response.body().charAt(response.body().indexOf('=') + 1));
                            surveyId = Character.getNumericValue(response.body().charAt(response.body().indexOf('=') + 1));
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.i(TAG,"exception="+e.toString());
                        }
                        data.delete(DeviceInfoEntity.class).get().value();
                        DeviceInfoEntity entity = new DeviceInfoEntity();
                        ConstantValues.audit_id = loginid.getText().toString();
                        ProductApplication.auditId=ConstantValues.audit_id;
                        entity.setAuditId(loginid.getText().toString());
                        entity.setDeviceId(deviceId);
                        entity.setOsVersion(OsVersion);
                        entity.setAppVersion(ConstantValues.app_version);
                        data.insert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<DeviceInfoEntity>() {
                            @Override
                            public void accept(@NonNull DeviceInfoEntity deviceInfoEntity) throws Exception {
                                ConstantValues.audit_id = loginid.getText().toString();
                                ProductApplication.auditId=ConstantValues.audit_id;
                                MapperInfoEntity entity = new MapperInfoEntity();
                                entity.setAuditId(loginid.getText().toString());
                                entity.setSurveyId(surveyId);
                                data.insert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<MapperInfoEntity>() {
                                    @Override
                                    public void accept(@NonNull MapperInfoEntity mapperInfoEntity) throws Exception {
                                        startNextActivity();
                                    }
                                });
                            }
                        });
                    } else {
                        mLoginFormView.setVisibility(View.VISIBLE);
                        mProgressView.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error Logging Contact Support Team", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.i(TAG,"null response");
                }
                islogging=false;
                mLoginFormView.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), "Error Logging Contact Support Team", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG, "error=" + t.toString());
                mLoginFormView.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Error Logging Contact Support Team", Toast.LENGTH_SHORT).show();
                islogging=false;
            }
        });
    }
    private void getCityList(){
        Retrofit retrofit =new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<JsonArray> call=api.getCityList();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null response");
                    Toast.makeText(getApplicationContext(),"Error Getting City List",Toast.LENGTH_SHORT).show();
                    mProgressView.setVisibility(View.GONE);
                    mLoginFormView.setVisibility(View.VISIBLE);
                    return;
                }
                Log.i(TAG,"response="+response.body().toString());
                cityIdList.clear();
                cityNameList.clear();
                for (JsonElement element:response.body()){
                    try {
                        JSONObject object=new JSONObject(element.toString());
//                        cityList.add(object.getString("city_name"));
                        cityIdList.add(object.getString("city_username_code"));
                        cityNameList.add(object.getString("city_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG,"exception="+e.toString());
                        Toast.makeText(getApplicationContext(),"Error Getting City List",Toast.LENGTH_SHORT).show();
                        mProgressView.setVisibility(View.GONE);
                        mLoginFormView.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                spinnerAdapter.notifyDataSetChanged();
                mProgressView.setVisibility(View.GONE);
                mLoginFormView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"error="+t.toString());
                Toast.makeText(getApplicationContext(),"Error Getting City List",Toast.LENGTH_SHORT).show();
                mProgressView.setVisibility(View.GONE);
                mLoginFormView.setVisibility(View.VISIBLE);

            }
        });/*
        final Call<JsonArray> superList=api.getSupervisorList();
        superList.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                if(response.body()==null){
                    Toast.makeText(getApplicationContext(),"Error Getting Team List",Toast.LENGTH_SHORT).show();
                    Log.i(TAG,"supervisor null response");
                    return;
                }
                superviserList.clear();
                for (JsonElement element:response.body()){
                    try {
                        JSONObject jsonObject=new JSONObject(element.toString());
                        superviserList.add(jsonObject.getString("supervisor_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Error Getting Team List",Toast.LENGTH_SHORT).show();
                    }

                }
                superviserSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(),"Error Getting City List",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"supervisor error"+t.toString());
            }
        });*/

    }
    private void getAgencyIdList(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        if(city.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please Select City",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject object=new JSONObject();
        try {
            object.put("parent_id",city);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG,"exception="+e.toString());
            Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
            return;
        }
        Call<JsonArray> getTeam=api.getTeamList(object);
        getTeam.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"team data null response");
                    Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"agency response for city "+city+" "+response.body().toString());
                agencyIdList.clear();
                agencyNameList.clear();
                Log.i(TAG,"team response="+response.body().toString());
                for(JsonElement element:response.body()){
                    try {
                        JSONObject jsonObject=new JSONObject(element.toString());
                        agencyIdList.add(jsonObject.getString("mapper_id"));
                        agencyNameList.add(jsonObject.getString("owner_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG,"exception="+e.toString());
                        Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
                    }
                }
                mProgressView.setVisibility(View.GONE);
                mLoginFormView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"agency team error="+t.toString());
            }
        });
    }
    private void getSuperVisorIdList(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        if(agency.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please Select Agency",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject object=new JSONObject();
        try {
            object.put("parent_id",agency);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG,"exception="+e.toString());
            Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
            return;
        }
        Call<JsonArray> getTeam=api.getTeamList(object);
        getTeam.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"team data null response");
                    Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
                    return;
                }
                superVisorIdList.clear();
                superVisorNameList.clear();
                Log.i(TAG,"team response="+response.body().toString());
                for(JsonElement element:response.body()){
                    try {
                        JSONObject jsonObject=new JSONObject(element.toString());
                        superVisorIdList.add(jsonObject.getString("mapper_id"));
                        superVisorNameList.add(jsonObject.getString("owner_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG,"exception="+e.toString());
                        Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
                    }
                }
                mProgressView.setVisibility(View.GONE);
                mLoginFormView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"super team error="+t.toString());
            }
        });
    }
}
