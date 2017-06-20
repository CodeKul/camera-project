package com.codekul.cameraproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

                final File file = new File(getFilesDir(), "my.txt");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(picture);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long callbackTime = System.currentTimeMillis();

                final Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            findViewById(R.id.cameraView).setVisibility(View.GONE);
                            findViewById(R.id.imageView).setVisibility(View.VISIBLE);
                            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(handleSamplingAndRotationBitmap(Uri.fromFile(file)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        //img.recycle();
        return rotatedImg;
    }

    public Bitmap handleSamplingAndRotationBitmap(Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);
        return img;
    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }
}
