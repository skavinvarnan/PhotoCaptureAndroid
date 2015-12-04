package com.skvarnan.photo.capture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.skvarnan.photo.capture.library.PhotoCaptureHelper;

public class MainActivity extends AppCompatActivity {

    private PhotoCaptureHelper mPhotoCaptureHelper;
    private static final int CAPTURE_IMAGE = 1001;

    private Bitmap mSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoCaptureHelper = new PhotoCaptureHelper(this);
        mPhotoCaptureHelper.setDirectoryName("MyDir");
        mPhotoCaptureHelper.setPhotoHeightWidth(512, 1024);
        setContentView(R.layout.activity_main);
    }

    public void click(View v) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {

        PhotoCaptureHelper.verifyStoragePermissions(this);
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoCaptureHelper.setImageUri());
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE) {
                mSelectedImage = mPhotoCaptureHelper.getImageBitmap();
                ((ImageView) findViewById(R.id.image)).setImageBitmap(mSelectedImage);
            }
        }

    }

}
