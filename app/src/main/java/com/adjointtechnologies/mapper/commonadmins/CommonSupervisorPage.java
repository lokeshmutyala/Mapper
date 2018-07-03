package com.adjointtechnologies.mapper.commonadmins;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adjointtechnologies.mapper.CommonHomePage;
import com.adjointtechnologies.mapper.ConstantValues;
import com.adjointtechnologies.mapper.DetailsActivity;
import com.adjointtechnologies.mapper.ImagesActivity;
import com.adjointtechnologies.mapper.LoginReg;
import com.adjointtechnologies.mapper.ProductApplication;
import com.adjointtechnologies.mapper.R;
import com.adjointtechnologies.mapper.RetrofitProductApi;
import com.adjointtechnologies.mapper.TmpCommonInfoPage;
import com.adjointtechnologies.mapper.database.DeviceInfoEntity;
import com.adjointtechnologies.mapper.database.MapperInfoEntity;
import com.adjointtechnologies.mapper.service.GpsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CommonSupervisorPage extends AppCompatActivity {

    final String TAG="commonsupervisorpage";
    DatePickerDialog.OnDateSetListener date;
    Calendar mycal;
    final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    EditText from;
    EditText to;
    String fromdate="";
    String todate="";
    String clickedView="";
    FloatingActionButton refresh;
    FrameLayout frameLayout;
    private View mProgressView;
    SupervisorInfoAdapter supervisorInfoAdapter;
    RecyclerView recyclerViewToday;
    List<SupervisorInfoDataObject> infoDataObjectList=new ArrayList<>();

    SupervisorInfoAdapterTotal supervisorInfoAdapterTotal;
    RecyclerView recyclerViewTotal;
    List<SupervisorInfoDataObjectTotal> infoDataObjectTotalList=new ArrayList<>();
    private ReactiveEntityStore<Persistable> data;

    LinearLayout todayHeader,totalHeader;
    RelativeLayout dateHeader;
    String parentId="";
    private int surveyId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coomon_supervisor_page);
        data=((ProductApplication)getApplication()).getData();
        mycal=Calendar.getInstance();
        dateHeader=(RelativeLayout)findViewById(R.id.date_header_supervisor);
        todayHeader=(LinearLayout)findViewById(R.id.today_header_supervisor);
        totalHeader=(LinearLayout)findViewById(R.id.total_header_supervisor);
        from=(EditText)findViewById(R.id.from_supervisor_admin);
        to=(EditText) findViewById(R.id.to_supervisor_admin);
        supervisorInfoAdapter=new SupervisorInfoAdapter(infoDataObjectList,CommonSupervisorPage.this);
        recyclerViewToday = (RecyclerView) findViewById(R.id.info_list_supervisor_admin);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewToday.setLayoutManager(layoutManager);
        recyclerViewToday.setItemAnimator(new DefaultItemAnimator());
        recyclerViewToday.setAdapter(supervisorInfoAdapter);

        if(getIntent().getBooleanExtra("is_agency",false)){
            parentId=getIntent().getStringExtra("parent_id");
            surveyId=ConstantValues.COMMON_AGENCY;
        }else {
            parentId=ProductApplication.auditId;
            surveyId=ConstantValues.COMMON_SUPERVISOR;
        }

        supervisorInfoAdapterTotal=new SupervisorInfoAdapterTotal(infoDataObjectTotalList,CommonSupervisorPage.this);
        recyclerViewTotal=(RecyclerView) findViewById(R.id.info_list_supervisor_admin_total);
        RecyclerView.LayoutManager layoutManagerTotal = new LinearLayoutManager(getApplicationContext());
        recyclerViewTotal.setLayoutManager(layoutManagerTotal);
        recyclerViewTotal.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTotal.setAdapter(supervisorInfoAdapterTotal);

        from.setKeyListener(null);
        to.setKeyListener(null);
        refresh=(FloatingActionButton)findViewById(R.id.refresh_list_tmp_common_infsupervisor_admin);
        mProgressView=findViewById(R.id.progress_tmp_common_infosupervisor_admin);
        frameLayout=(FrameLayout) findViewById(R.id.framelayout);
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedView="from";
                new DatePickerDialog(CommonSupervisorPage.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedView="to";
                new DatePickerDialog(CommonSupervisorPage.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();
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
                getTodayData();
                getTotalData();
            }
        });
//        from.setText(new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date()));
//        to.setText(new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date()));
//        fromdate=new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date());
//        todate=new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date());
        getTodayData();
        getTotalData();
    }
    private void getTodayData(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG,"getting today data");
        frameLayout.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
        JSONObject object=new JSONObject();
        try {
//            object.put("from",fromdate);
//            object.put("to",todate);
            object.put("parent_id", parentId);
            Log.i(TAG,object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            errorMsg("Error Getting Details");
            Log.i(TAG,"exception="+e.toString());
        }
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<List<SupervisorInfoDataObject>> dataCal=api.getSuperVisisorInfoData(object);
        dataCal.enqueue(new Callback<List<SupervisorInfoDataObject>>() {
            @Override
            public void onResponse(Response<List<SupervisorInfoDataObject>> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null response");
                    errorMsg("Error Getting Details");
                    return;
                }
                Log.i(TAG,"today reponse="+response.body().toString());
                infoDataObjectList.clear();
                infoDataObjectList.addAll(response.body());
                supervisorInfoAdapter.notifyDataSetChanged();
                mProgressView.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"error="+t.toString());
                errorMsg("Error Getting Details");
            }
        });
    }
    private void getTotalData(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG,"getting total data");
        frameLayout.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
        JSONObject object=new JSONObject();
        try {
            object.put("from",fromdate);
            object.put("to",todate);
            object.put("parent_id", parentId);
            Log.i(TAG,object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            errorMsg("Error Getting Total Details");
            Log.i(TAG,"exception="+e.toString());
        }
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<List<SupervisorInfoDataObjectTotal>> dataCal=api.getSuperVisorInfoDataTotal(object);
        dataCal.enqueue(new Callback<List<SupervisorInfoDataObjectTotal>>() {
            @Override
            public void onResponse(Response<List<SupervisorInfoDataObjectTotal>> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null response");
                    errorMsg("Error Getting Details");
                    return;
                }
                Log.i(TAG,"total reponse="+response.body().toString());
                infoDataObjectTotalList.clear();
                infoDataObjectTotalList.addAll(response.body());
                supervisorInfoAdapterTotal.notifyDataSetChanged();
                mProgressView.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG,"error="+t.toString());
                errorMsg("Error Getting Details");
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void errorMsg(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressView.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.common_supervisor_page_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.map_view:
                Intent mapIntent=new Intent(getApplicationContext(), DetailsActivity.class);
                mapIntent.putExtra("parent_id",parentId);
                mapIntent.putExtra("is_parent",true);
                startActivity(mapIntent);
                break;
            case R.id.today_data:
                totalHeader.setVisibility(View.GONE);
                dateHeader.setVisibility(View.GONE);
                recyclerViewTotal.setVisibility(View.GONE);
                recyclerViewToday.setVisibility(View.VISIBLE);
                todayHeader.setVisibility(View.VISIBLE);
                break;
            case R.id.total_data:
                recyclerViewToday.setVisibility(View.GONE);
                todayHeader.setVisibility(View.GONE);
                totalHeader.setVisibility(View.VISIBLE);
                dateHeader.setVisibility(View.VISIBLE);
                recyclerViewTotal.setVisibility(View.VISIBLE);
                break;
            case R.id.check_image:
                Intent imgIntent=new Intent(getApplicationContext(), ImagesActivity.class);
                imgIntent.putExtra("parent_id",parentId);
                imgIntent.putExtra("is_parent",true);
                startActivity(imgIntent);
                break;
            case R.id.table_view:
                startActivity(new Intent(getApplicationContext(),TmpCommonInfoPage.class));
                break;
            case R.id.audit:
                if(surveyId==ConstantValues.COMMON_SUPERVISOR){
                    Intent intent3=new Intent(getApplicationContext(),CommonHomePage.class);
                    intent3.putExtra("isAudit",true);
                    startActivity(intent3);
//                    finish();
                }else {
                    Intent auditIntent = new Intent(getApplicationContext(), CommonAuditPage.class);
                    startActivity(auditIntent);
                }
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
}
