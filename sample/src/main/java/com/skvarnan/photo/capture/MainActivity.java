package com.skvarnan.photo.capture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.skvarnan.photo.capture.library.BitmapHelper;

public class MainActivity extends AppCompatActivity {

    private BitmapHelper mBitmapHelper;
    private static final int CAPTURE_IMAGE = 1001;

    private Bitmap mSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBitmapHelper = new BitmapHelper(this);
        setContentView(R.layout.activity_main);
    }

    public void click(View v) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mBitmapHelper.setImageUri());
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE) {
                mSelectedImage = mBitmapHelper.rotateImage(mBitmapHelper.getImagePath());
                ((ImageView) findViewById(R.id.image)).setImageBitmap(mSelectedImage);
            }
        }

    }

}
