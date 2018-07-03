package com.adjointtechnologies.mapper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.adjointtechnologies.mapper.database.DeviceInfoEntity;
import com.adjointtechnologies.mapper.database.MapperInfoEntity;
import com.adjointtechnologies.mapper.database.PolygonCornersEntity;
import com.adjointtechnologies.mapper.service.GpsService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class CommonDashBoard extends AppCompatActivity implements OnMapReadyCallback{
    private final String TAG="commondashboard";
    DatePickerDialog.OnDateSetListener date;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    List<PolyCorners> polyCornerses=new ArrayList<>();
    List<LatLng> latLngList=new ArrayList<>();
    List<storePoints> storePointsList=new ArrayList<>();
    List<LatLng> corners = new ArrayList<>();
    private Marker clickedMarker=null;

    Calendar mycal;
    final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    EditText from;
    EditText to;
    String fromdate="";
    String todate="";
    String clickedView="";
    private ReactiveEntityStore<Persistable> data;
    ToggleButton pointsOrRoute;

    public static List<String> cityIdList=new ArrayList<>();
    public static List<String> cityNameList=new ArrayList<>();

    public static List<String> agencyIdList=new ArrayList<>();
    public static List<String> agencyNameList=new ArrayList<>();

    public static List<String> superVisorIdList=new ArrayList<>();
    public static List<String> superVisorNameList=new ArrayList<>();

    public static List<String> mapperIdList=new ArrayList();
    public static List<String> mapperNameList=new ArrayList<>();

    Retrofit retrofit;
    RetrofitProductApi api;
    SearchableSpinner spinner;
    ArrayAdapter<String> spinnerAdapter;
    String selectedMapperId="";

    SearchableSpinner citySpinner;
    ArrayAdapter<String> citySpinnerAdapter;
    String selectedCityId="";

    SearchableSpinner agencySpinner;
    ArrayAdapter<String> agencySpinnerAdapter;
    String selectedAgencyId="";

    SearchableSpinner supervisorSpinner;
    ArrayAdapter<String> supervisorSpinnerAdapter;
    String selectedSuperVisorId="";

    ProgressBar progressBar;
    LinearLayout rootLayout;
    LinearLayout dataRootLayout;
    FloatingActionButton refresh,navigate,picture;
    TextView mapperName,noOfStores,cigaretteStores,openStores,otpStores,insideStores;
    private int loginType=0;
    private boolean isMapReady=false;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getAllPolygons();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_dash_board);
        data=((ProductApplication)getApplication()).getData();
        loginType=getIntent().getIntExtra("type",0);
        if(loginType==0){
            Toast.makeText(getApplicationContext(),"Autherisation Error",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),LoginReg.class));
            finish();
        }
        mapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_dashboard);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);
        retrofit=new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        api=retrofit.create(RetrofitProductApi.class);
        mycal=Calendar.getInstance();
        from=(EditText)findViewById(R.id.from);
        to=(EditText) findViewById(R.id.to);
        from.setKeyListener(null);
        to.setKeyListener(null);
        progressBar=(ProgressBar) findViewById(R.id.progress_dashboard);
        rootLayout=(LinearLayout) findViewById(R.id.root_dashboard);
        dataRootLayout=(LinearLayout) findViewById(R.id.dashboard_data_root);
        refresh=(FloatingActionButton) findViewById(R.id.refresh_dashboard);
        navigate=(FloatingActionButton) findViewById(R.id.navigate_dashboard);
        picture=(FloatingActionButton) findViewById(R.id.request_image_dashboard);
        pointsOrRoute=(ToggleButton) findViewById(R.id.road_point_toggle_dashboard);
        pointsOrRoute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                navigate.setVisibility(View.GONE);
                picture.setVisibility(View.GONE);
                clickedMarker=null;
                if(b){
                    drawPoints();
                }else {
                    drawRoute();
                }
            }
        });

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo();
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickedMarker==null){
                    return;
                }else {
                    storePoints tag = (storePoints) clickedMarker.getTag();
                    Intent intent = new Intent(getApplicationContext(), SingleImage.class);
                    intent.putExtra("storeid", tag.getStoreId());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        spinner=(SearchableSpinner) findViewById(R.id.mapper_spinner_dashboard);
        spinnerAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,mapperNameList);
        spinner.setAdapter(spinnerAdapter);
        spinner.setTitle("Select Mapper");

        citySpinner=(SearchableSpinner) findViewById(R.id.city_spinner_dashboard);
        citySpinnerAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,cityNameList);
        citySpinner.setAdapter(citySpinnerAdapter);
        citySpinner.setTitle("Select City");

        agencySpinner=(SearchableSpinner) findViewById(R.id.agency_spinner_dashboard);
        agencySpinnerAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,agencyNameList);
        agencySpinner.setAdapter(agencySpinnerAdapter);
        agencySpinner.setTitle("Select Agency");

        supervisorSpinner=(SearchableSpinner) findViewById(R.id.supervisor_spinner_dashboard);
        supervisorSpinnerAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,superVisorNameList);
        supervisorSpinner.setAdapter(supervisorSpinnerAdapter);
        supervisorSpinner.setTitle("Select Supervisor");

        mapperName=(TextView)findViewById(R.id.tl_name);
        noOfStores=(TextView)findViewById(R.id.no_of_stores);
        cigaretteStores=(TextView) findViewById(R.id.cigarette_stores);
        openStores=(TextView)findViewById(R.id.open_stores);
        otpStores=(TextView)findViewById(R.id.otp_stores);
        insideStores=(TextView) findViewById(R.id.inside_stores);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
             if(mapperIdList.size()>i){
                 selectedMapperId=mapperIdList.get(i);
                 Log.i(TAG,"selected position="+i);
             }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(cityIdList.size()>i){
                    selectedCityId=cityIdList.get(i);
                    getAgencyIdList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        agencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(agencyIdList.size()>i){
                    selectedAgencyId=agencyIdList.get(i);
                    getSuperVisorIdList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        supervisorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(superVisorIdList.size()>i){
                    selectedSuperVisorId=superVisorIdList.get(i);
                    getMapperIdList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedView="from";
                new DatePickerDialog(CommonDashBoard.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedView="to";
                new DatePickerDialog(CommonDashBoard.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        date= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,month);
                mycal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                mycal.set(Calendar.HOUR_OF_DAY,0);
                mycal.set(Calendar.MINUTE,0);
                mycal.set(Calendar.SECOND,0);
                mycal.set(Calendar.MILLISECOND,0);
                String selectedate=sdf.format(mycal.getTime());
                Log.i("dateselected","date="+selectedate);
                if(clickedView.contentEquals("to")){
                    todate=selectedate;
                    to.setText(selectedate);
                }else if(clickedView.contentEquals("from")){
                    fromdate=selectedate;
                    from.setText(selectedate);
                }

            }
        };

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedMapperId.isEmpty()){
                    if(selectedSuperVisorId.isEmpty()){
                        if(selectedAgencyId.isEmpty()){
                            if(selectedCityId.isEmpty()){
                                Toast.makeText(getApplicationContext(),"Please Select City",Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                Toast.makeText(getApplicationContext(),"Please Select Agency",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Please Select Supervisor",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Please Select Mapper",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                getData();
                JSONObject object=new JSONObject();
                try {
                    object.put("from",fromdate);
                    object.put("to",todate);
                    object.put("agent_id",selectedMapperId);
//                    if(agent_id.startsWith("KHM") || agent_id.startsWith("AUD") || agent_id.startsWith("ANT")){
                    getKhmData(object);
//                    }else {
//                        getData(object);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    rootLayout.setVisibility(View.VISIBLE);
                }
            }
        });
//        getMapperIdList();
        from.setText(new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date()));
        to.setText(new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date()));
        fromdate=new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date());
        todate=new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date());
//        getData();
        switch (loginType){
            case ConstantValues.COMMON_DASHBOARD:
                getCityIdList();
                break;
            case ConstantValues.COMMON_AGENCY:
                citySpinner.setVisibility(View.GONE);
                agencySpinner.setVisibility(View.GONE);
                selectedAgencyId=ProductApplication.auditId;
                getSuperVisorIdList();
                break;
            case ConstantValues.COMMON_SUPERVISOR:
                citySpinner.setVisibility(View.GONE);
                agencySpinner.setVisibility(View.GONE);
                supervisorSpinner.setVisibility(View.GONE);
                selectedSuperVisorId=ProductApplication.auditId;
                getMapperIdList();
                break;
            default:
                Toast.makeText(getApplicationContext(),"Error Getting Details Try Restart App ",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void getData(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedMapperId.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please Select Mapper",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("from",fromdate);
            jsonObject.put("to",todate);
            jsonObject.put("mapper_id",selectedMapperId);
            jsonObject.put("parent_id",ProductApplication.auditId);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG,"exception="+e.toString());
            Toast.makeText(getApplicationContext(),"Error Getting Details",Toast.LENGTH_SHORT).show();
            return;
        }
        Call<String> getCall=api.getMyTeamData(jsonObject);
        getCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"data null response");
                    Toast.makeText(getApplicationContext(),"Error Getting Details",Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG,"data response="+response.body());
                try {
                    JSONObject object=new JSONObject(response.body());
                    if(spinner.getSelectedItem()!=null) {
                        mapperName.setText(spinner.getSelectedItem().toString());
                    }
                    noOfStores.setText(""+object.getInt("store_no"));
                    cigaretteStores.setText(""+object.getInt("cigarette_no"));
                    openStores.setText(""+object.getInt("open_no"));
                    otpStores.setText(""+object.getInt("otp_no"));
                    insideStores.setText(""+object.getInt("inside_no"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG,"exception="+e.toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(),"Error Getting Details",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"error="+t.toString());
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dashboard_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.map_view:
//                Intent intent=new Intent(getApplicationContext(),DetailsActivity.class);
//                intent.putExtra("is_dashboard",true);
//                startActivity(intent);
                dataRootLayout.setVisibility(View.GONE);
                mapFragment.getView().setVisibility(View.VISIBLE);
                pointsOrRoute.setVisibility(View.VISIBLE);
                break;
            case R.id.data_view:
                mapFragment.getView().setVisibility(View.GONE);
                pointsOrRoute.setVisibility(View.GONE);
                dataRootLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.table_view:
                startActivity(new Intent(getApplicationContext(),TmpCommonInfoPage.class));
                break;
            case R.id.logout:
                data.delete(MapperInfoEntity.class).get().value();
                if(ConstantValues.audit_id.contentEquals("AUD999")){
                    data.delete(DeviceInfoEntity.class).get().value();
                }
                ConstantValues.audit_id="";
                stopService(new Intent(getApplicationContext(),GpsService.class));
                startActivity(new Intent(getApplicationContext(),LoginReg.class));//LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMapperIdList(){
     if(!isNetworkAvailable()){
         Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
         return;
     }
     if(selectedSuperVisorId.isEmpty()){
         Toast.makeText(getApplicationContext(),"Please Select Supervisor",Toast.LENGTH_SHORT).show();
         return;
     }
        JSONObject object=new JSONObject();
        try {
            object.put("parent_id",selectedSuperVisorId);
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
                mapperIdList.clear();
                mapperNameList.clear();
                Log.i(TAG,"team response="+response.body().toString());
                for(JsonElement element:response.body()){
                    try {
                        JSONObject jsonObject=new JSONObject(element.toString());
                        mapperIdList.add(jsonObject.getString("mapper_id"));
                        mapperNameList.add(jsonObject.getString("owner_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG,"exception="+e.toString());
                        Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"team error="+t.toString());
            }
        });
    }
    private void getCityIdList(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }

//        JSONObject object=new JSONObject();
//        try {
//            object.put("parent_id",ProductApplication.auditId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.i(TAG,"exception="+e.toString());
//            Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
//            return;
//        }
        Call<JsonArray> call=api.getCityList();
        call.enqueue(new Callback<JsonArray>() {
                         @Override
                         public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                             if (response.body() == null) {
                                 Log.i(TAG, "null response");
                                 Toast.makeText(getApplicationContext(), "Error Getting City List", Toast.LENGTH_SHORT).show();
                                 progressBar.setVisibility(View.GONE);
                                 rootLayout.setVisibility(View.VISIBLE);
                                 return;
                             }
                             Log.i(TAG, "response=" + response.body().toString());
                             cityIdList.clear();
                             cityNameList.clear();
                             for (JsonElement element : response.body()) {
                                 try {
                                     JSONObject object = new JSONObject(element.toString());
//                        cityList.add(object.getString("city_name"));
                                     cityIdList.add(object.getString("city_username_code"));
                                     cityNameList.add(object.getString("city_name"));
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                     Log.i(TAG, "exception=" + e.toString());
                                     Toast.makeText(getApplicationContext(), "Error Getting City List", Toast.LENGTH_SHORT).show();
                                     progressBar.setVisibility(View.GONE);
                                     rootLayout.setVisibility(View.VISIBLE);
                                     return;
                                 }
                             }
                             refresh.setVisibility(View.VISIBLE);
                             progressBar.setVisibility(View.GONE);
                             rootLayout.setVisibility(View.VISIBLE);
                         }

                         @Override
                         public void onFailure(Throwable t) {
                             Log.i(TAG, "error=" + t.toString());
                             Toast.makeText(getApplicationContext(), "Error Getting City List", Toast.LENGTH_SHORT).show();
                             progressBar.setVisibility(View.GONE);
                             rootLayout.setVisibility(View.VISIBLE);

                         }

//        Call<JsonArray> getTeam=api.getTeamList(object);
//        getTeam.enqueue(new Callback<JsonArray>() {
//            @Override
//            public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
//                if(response.body()==null){
//                    Log.i(TAG,"team data null response");
//                    Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                cityIdList.clear();
//                cityNameList.clear();
//                Log.i(TAG,"team response="+response.body().toString());
//                for(JsonElement element:response.body()){
//                    try {
//                        JSONObject jsonObject=new JSONObject(element.toString());
//                        cityIdList.add(jsonObject.getString("mapper_id"));
//                        cityNameList.add(jsonObject.getString("owner_name"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Log.i(TAG,"exception="+e.toString());
//                        Toast.makeText(getApplicationContext(),"Error Getting Team Data",Toast.LENGTH_SHORT).show();
//                    }
//                }
//                progressBar.setVisibility(View.GONE);
//                rootLayout.setVisibility(View.VISIBLE);
//                refresh.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.i(TAG,"city team error="+t.toString());
//            }
        });
        }
    private void getAgencyIdList(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedCityId.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please Select City",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject object=new JSONObject();
        try {
            object.put("parent_id",selectedCityId);
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
                progressBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.VISIBLE);
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
        if(selectedAgencyId.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please Select Agency",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject object=new JSONObject();
        try {
            object.put("parent_id",selectedAgencyId);
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
                progressBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"super team error="+t.toString());
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        isMapReady=true;
//        if(isMapper && isRequested){
//            drawPolygon();
//        }
        try {
            mMap.setMyLocationEnabled(true);

        }catch (SecurityException e){
            Log.i(TAG,"exception="+e.toString());
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clickedMarker=marker;
                navigate.setVisibility(View.VISIBLE);
                picture.setVisibility(View.VISIBLE);
//                if(ConstantValues.audit_id.contentEquals("super")){
//                    edit.setVisibility(View.VISIBLE);
//                    delete.setVisibility(View.VISIBLE);
//                }else {
//                    edit.setVisibility(View.GONE);
//                    delete.setVisibility(View.GONE);
//                }
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                clickedMarker=null;
                navigate.setVisibility(View.GONE);
                picture.setVisibility(View.GONE);
//                edit.setVisibility(View.GONE);
//                delete.setVisibility(View.GONE);
                showMapTypeSelectorDialog();
            }
        });
    }
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }
    private void drawRoute(){
        if(latLngList.size()<=0){
            return;
        }
        mMap.clear();
        drawPolygon();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngList.get(0)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        Polyline polyline=mMap.addPolyline(new PolylineOptions().width(5).addAll(latLngList).color(Color.RED));
        polyline.setVisible(true);

    }
    private void drawPoints(){
        mMap.clear();
        drawPolygon();
        if(storePointsList.size()<=0){
            return;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(storePointsList.get(0).getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        for(int i=0;i<storePointsList.size();i++){
//            mMap.addMarker(new MarkerOptions().position(storelatLngList.get(i)).title(""+storelatLngList.get(i).latitude+","+storelatLngList.get(i).longitude));
            mMap.addMarker(new MarkerOptions().position(storePointsList.get(i).getLatLng()).title(storePointsList.get(i).getStoreName())).setTag(storePointsList.get(i));
        }
    }
    private void drawPolygon(){
//        if(isAdmin){
            polyCornerses.clear();
            List<Tuple> tuples = data.select(PolygonCornersEntity.POLYGON_KEY).where(PolygonCornersEntity.MAPPER_ID.eq(selectedMapperId)).groupBy(PolygonCornersEntity.POLYGON_KEY).get().toList();
            for(int i=0;i<tuples.size();i++){
                corners=new ArrayList<>();
                Log.i("polygon","tuples[i]="+tuples.get(i));
                List<PolygonCornersEntity> test = data.select(PolygonCornersEntity.class).where(PolygonCornersEntity.POLYGON_KEY.eq(tuples.get(i).toString().substring(tuples.get(i).toString().indexOf('[')+2,tuples.get(i).toString().indexOf(']')-1))).get().toList();
                for(int j=0;j<test.size();j++){
                    corners.add(new LatLng(test.get(j).getLatitude(),test.get(j).getLongitude()));
                }
                polyCornerses.add(new PolyCorners(corners,test.get(0).getPolygonName(),test.get(0).getPolygonKey()));
                Log.i("polygon","corners size before adding ="+corners.size());
//            polycorners.add(temmp);
            }
            if (polyCornerses.size() <= 0) {
                Toast.makeText(getApplicationContext(), "No Polygon Assigned For "+selectedMapperId, Toast.LENGTH_SHORT).show();
            }// else {
//                // corners.add(new LatLng(polygonCornersEntities.get(0).getLatitude(),polygonCornersEntities.get(0).getLongitude()));
//                if (isMapReady) {
//                    drawPolygon();
//                } else {
//                    isRequested = true;
//                    //Toast.makeText(getApplicationContext(), "no active internet connection trying to get data", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
        if(polyCornerses.size()<=0){
            Log.i(TAG,"no details");
            return;
        }
        Log.i("polygon","inside drawPolygon");
        mMap.clear();
        for(int i=0;i<polyCornerses.size();i++){
//            corners.clear();
//            corners=new ArrayList<>(polycorners.get(i));
            if(polyCornerses.get(i).getCorners().size()>0) {
//                Log.i("polygon","trying to draw polygon");
//                for(int k=0;k<polyCornerses.get(i).getCorners().size();k++){
////                    Log.i("polygon",polycorners.get(i).get(k).latitude+","+polycorners.get(i).get(k).longitude);
//                }
                Log.i(TAG,"completed");
                mMap.addPolygon(addpolygon(polyCornerses.get(i).corners)).setTag(polyCornerses.get(i).name);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(polyCornerses.get(i).corners.get(0)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
//                if(i==0) {
//                    Polygon pol = mMap.addPolygon(new PolygonOptions().addAll(polycorners.get(i)).fillColor(Color.RED).geodesic(false));
//                    pol.setVisible(true);
//                }else {
//                    Polygon pol1 = mMap.addPolygon(new PolygonOptions().addAll(polycorners.get(i)).fillColor(Color.RED).geodesic(false));
//                    pol1.setVisible(true);
//                }
//                mMap.addPolygon(new PolygonOptions().addAll(polycorners.get(i)).fillColor(Color.RED));
//                Polyline polyline = mMap.addPolyline(new PolylineOptions().width(5).addAll(polycorners.get(i)).color(Color.RED));
//                polyline.setVisible(true);
                Log.i(TAG,"ply line success");
            }
        }
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                navigate.setVisibility(View.GONE);
                picture.setVisibility(View.GONE);
                Object tag = polygon.getTag();
                if(tag!=null){
                    Toast.makeText(getApplicationContext(),(String)tag,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public PolygonOptions addpolygon(List<LatLng> arg) {
//        LatLng [] data = arg;//.toArray();
        PolygonOptions polygonOptions;
        polygonOptions = new PolygonOptions();
        for(int i=0; i <arg.size(); i++)
        {
            Log.i(TAG,"error=size="+arg.size());
            polygonOptions.add(arg.get(i));
        }
        polygonOptions.strokeColor(Color.RED).strokeWidth(2);
        polygonOptions.fillColor(Color.parseColor("#51000000"));
        polygonOptions.clickable(true);
        return polygonOptions;
    }
    private void navigateTo(){
        if(clickedMarker!=null){
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+clickedMarker.getPosition().latitude+","+clickedMarker.getPosition().longitude+"("+clickedMarker.getTitle()+")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
//            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
                navigate.setVisibility(View.GONE);
            }
        }
    }
    private void getKhmData(JSONObject object){
        latLngList.clear();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<String> coordinates=api.getcoordinates(object);
        coordinates.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null response");
                    return;
                }
                Log.i(TAG,"responce="+response.body().toString());
                mMap.clear();
                try {
                    JSONObject obj=new JSONObject(response.body().toString());//.get(i).toString());
                    JSONArray points=new JSONArray(obj.getString("points"));
                    JSONArray route=new JSONArray(obj.getString("route"));
                    for(int k=0;k<route.length();k++){
                        JSONObject obj1=new JSONObject(route.get(k).toString());
                        LatLng latLng=new LatLng(obj1.getDouble("latitude"),obj1.getDouble("longitude"));
                        latLngList.add(latLng);
                    }
//                    storePointsList.clear();
                    for(int j=0;j<points.length();j++){
                        JSONObject obj2=new JSONObject(points.get(j).toString());
                        LatLng latLng=new LatLng(obj2.getDouble("store_latitude"),obj2.getDouble("store_longitude"));
//                        storelatLngList.add(latLng);
                        storePointsList.add(new storePoints(obj2.getString("store_name"),latLng,obj2.getString("store_id"),obj2.getInt("survey_status"),obj2.getInt("store_status")==1));
                    }
                } catch (JSONException e) {
                    Log.i("coordinates","json error="+e.toString());
                    e.printStackTrace();
                }finally {
                    progressBar.setVisibility(View.GONE);
                    rootLayout.setVisibility(View.VISIBLE);
                }

                if(latLngList.size()>0){
                    if(pointsOrRoute.isChecked()){
                        drawPoints();
                    }else {
                        drawRoute();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("coordinates","error="+t.toString());
            }
        });
    }
    private void getAllPolygons(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        rootLayout.setVisibility(View.GONE);
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.baseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<JsonArray> getPolygons=api.getAllPolygons();
        getPolygons.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null response");
                    Toast.makeText(getApplicationContext(), "Error Getiing Polygon Details", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    rootLayout.setVisibility(View.VISIBLE);
                    return;
                }
//                Log.i(Tag, "responce=" + response.body().toString());
                if (response.body().size() > 0) {
                    data.delete(PolygonCornersEntity.class).get().value();
                }
                List<PolygonCornersEntity> entityList=new ArrayList<PolygonCornersEntity>();
                for (JsonElement element : response.body()
                        ) {
                    try {
                        JSONObject jsonObject = new JSONObject(element.toString());
                        String polyId = jsonObject.getString("polygon_id");
                        String polyname = jsonObject.getString("polygon_name");
                        String corners = jsonObject.getString("corner_coordinates");
                        String mapperId=jsonObject.getString("audit_id");
//                        Log.i(Tag, "corners=" + corners);
                        JSONArray array = new JSONArray(corners);

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
                            entityList.add(entity);
//                            data.insert(entity).subscribeOn(AndroidSchedulers.mainThread())
//                                    .observeOn(Schedulers.io())
//                                    .subscribe(new Consumer<PolygonCornersEntity>() {
//                                        @Override
//                                        public void accept(@NonNull PolygonCornersEntity polygonCornersEntity) throws Exception {
////                                            Log.i(Tag, "inserted successfully");
////                                            startService(new Intent(getApplicationContext(), GpsService.class));
//                                        }
//                                    });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "error json=" + e.toString());
                    }
                }
                data.insert(entityList).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<Iterable<PolygonCornersEntity>>() {
                    @Override
                    public void accept(@NonNull Iterable<PolygonCornersEntity> polygonCornersEntities) throws Exception {
                        Log.i(TAG,"polygon corners inserted");
                    }
                });
//                isPolygonReady=true;
//                if(isListReady){
                    progressBar.setVisibility(View.GONE);
                    rootLayout.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG, "error=" + t.toString());
                Toast.makeText(getApplicationContext(),"Error Getting Polygon Details",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
