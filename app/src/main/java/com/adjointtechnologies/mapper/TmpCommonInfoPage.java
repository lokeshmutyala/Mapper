package com.adjointtechnologies.mapper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class TmpCommonInfoPage extends AppCompatActivity {
    final String TAG="tmpcommoninfopage";
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
    TmpInfoAdapter tmpInfoAdapter;
    RecyclerView recyclerView;
    List<TmpInfoDataObject> tmpInfoDataObjectList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_common_info_page);
        mycal=Calendar.getInstance();
        from=(EditText)findViewById(R.id.from_tmp_common_info);
        to=(EditText) findViewById(R.id.to_tmp_common_info);
        tmpInfoAdapter=new TmpInfoAdapter(tmpInfoDataObjectList,TmpCommonInfoPage.this);
        recyclerView= (RecyclerView) findViewById(R.id.info_list_tmp_common_info);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tmpInfoAdapter);

        from.setKeyListener(null);
        to.setKeyListener(null);
        refresh=(FloatingActionButton)findViewById(R.id.refresh_list_tmp_common_info);
        mProgressView=findViewById(R.id.progress_tmp_common_info);
        frameLayout=(FrameLayout) findViewById(R.id.framelayout);
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedView="from";
                new DatePickerDialog(TmpCommonInfoPage.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedView="to";
                new DatePickerDialog(TmpCommonInfoPage.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();
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
                getData();
            }
        });
        from.setText(new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date()));
        to.setText(new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date()));
        fromdate=new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date());
        todate=new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(new Date());
        getData();
    }

    private void getData(){
         if(!isNetworkAvailable()){
             Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
             return;
         }
         Log.i(TAG,"getting data");
         frameLayout.setVisibility(View.GONE);
         mProgressView.setVisibility(View.VISIBLE);
        JSONObject object=new JSONObject();
        try {
            object.put("from",fromdate);
            object.put("to",todate);
            object.put("parent_id",ProductApplication.auditId);
            Log.i(TAG,object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            errorMsg("Error Getting Details");
            Log.i(TAG,"exception="+e.toString());
        }
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.commonMapperBaseUrl).addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<List<TmpInfoDataObject>> dataCal=api.getTmpInfoData(object);
        dataCal.enqueue(new Callback<List<TmpInfoDataObject>>() {
            @Override
            public void onResponse(Response<List<TmpInfoDataObject>> response, Retrofit retrofit) {
                if(response.body()==null){
                    Log.i(TAG,"null response");
                    errorMsg("Error Getting Details");
                    return;
                }
                Log.i(TAG,"reponse="+response.body().toString());
                tmpInfoDataObjectList.clear();
                tmpInfoDataObjectList.addAll(response.body());
                tmpInfoAdapter.notifyDataSetChanged();
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
}
