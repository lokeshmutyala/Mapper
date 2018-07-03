package com.adjointtechnologies.mapper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraTest extends AppCompatActivity {

    final String TAG="cameratest";
    CameraView cameraView;
    FloatingActionButton capture,complete,cancel;
    String storeid="";
    String inOrOut="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        storeid=getIntent().getStringExtra("storeid");
        cameraView= (CameraView) findViewById(R.id.camera);
        capture=(FloatingActionButton) findViewById(R.id.capture);
        complete=(FloatingActionButton)findViewById(R.id.complete);
        cancel=(FloatingActionButton)findViewById(R.id.cancel);
        inOrOut=getIntent().getStringExtra("inorout");
        if(inOrOut==null || storeid==null){
            Toast.makeText(getApplicationContext(),"Exception Occured Try Again",Toast.LENGTH_SHORT).show();
            finish();
        }
        if(inOrOut.contentEquals("outer")){
            Toast.makeText(getApplicationContext(),"Please Include Dealer Board in Outer Picture",Toast.LENGTH_SHORT).show();
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File img=new File(ConstantValues.imagepath+"/"+storeid+"-"+inOrOut+".jpeg");
                setResult(RESULT_CANCELED);
                if(img.exists()){
                    img.delete();
                    finish();
                }
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File img=new File(ConstantValues.imagepath+"/"+storeid+"-"+inOrOut+".jpeg");
                if(img.exists()) {
                    setResult(RESULT_OK);
                    Toast.makeText(getApplicationContext(), "Image Saved Successfully", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onCameraError(@NonNull CameraException exception) {
                super.onCameraError(exception);
                Log.i(TAG,"exception="+exception.toString());
            }

            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);

                File img=new File(ConstantValues.imagepath+"/"+storeid+"-"+inOrOut+".jpeg");
                try {
                    if(img.createNewFile()){
                        Log.i(TAG,"created successfully");
                    }else {
                        Log.i(TAG,"unable to create new file");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG,"exception="+e.toString());
                }
                if(img.exists()){
                    try {
                        BufferedOutputStream outputStream=new BufferedOutputStream(new FileOutputStream(img));
                        outputStream.write(jpeg);
                        outputStream.flush();
                        outputStream.close();
                        Bitmap bitmap= BitmapFactory.decodeFile(img.getAbsolutePath());
                        cameraView.stop();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i(TAG,"exception="+e.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG,"exception="+e.toString());
                    }
                }else {
                    Log.i(TAG,"file not exists");
                }

            }
        });
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.capturePicture();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }
}
