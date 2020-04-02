package com.example.lab1;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    PictureCallback rawCallback;
    ShutterCallback shutterCallback;
    PictureCallback jpegCallback;
    Button capture;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        capture = (Button) findViewById(R.id.capture);
        capture.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        rawCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("Log", "onPictureTaken");
            }
        };

        shutterCallback = new ShutterCallback() {
            public void onShutter() {
                Log.i("Log", "onShutter");
            }
        };

        jpegCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/img" + System.currentTimeMillis() + ".jpg");
                    outStream.write(data);
                    outStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        };
    }

    private void start_camera() {
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            Log.e("Open camera", "init_camera: " + e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();
        //modify parameter
        param.setPreviewFrameRate(20);
        param.setPreviewSize(176, 144);
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            Log.e("Open camera", "init_camera: " + e);
            return;
        }
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        start_camera();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }
}


