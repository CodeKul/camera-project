package com.codekul.cameraproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

public class MainActivity extends AppCompatActivity {

    private CameraView camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = (CameraView) findViewById(R.id.cameraView);


    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        if(camera.isStarted())
            camera.stop();
        super.onPause();
    }

    public void onCapture(View view) {
        final long startTime = System.currentTimeMillis();

        camera.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                long callbackTime = System.currentTimeMillis();

                final Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                ResultHolder.dispose();
                ResultHolder.setImage(bitmap);
                ResultHolder.setNativeCaptureSize(
                        camera.getCaptureSize()
                );
                ResultHolder.setTimeToCallback(callbackTime - startTime);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.cameraView).setVisibility(View.GONE);
                        findViewById(R.id.imageView).setVisibility(View.VISIBLE);
                        ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);

                        camera.stop();
                    }
                });
            }
        });

        camera.captureImage();
    }

    public void onRetake(View view) {
        recreate();
    }
}
