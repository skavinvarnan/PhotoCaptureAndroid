package com.skvarnan.photo.capture.library;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created on : 05-12-2015
 * Author     : Kavin Varnan
 */
public class PhotoCaptureHelper {

    private Activity mActivity;

    public PhotoCaptureHelper(Activity activity) {
        this.mActivity = activity;
    }

    private String mImagePath;

    private String mStorageLocation = "PhotoCapture";

    private int mDesiredWidth = 1024;
    private int mDesiredHeight = 1024;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Set the height and width of the picture you wanted to be.
     * By default its 1024x1024
     * @param width
     * @param height
     */
    public void setPhotoHeightWidth(int width, int height) {
        mDesiredHeight = height;
        mDesiredWidth = width;
    }

    public Bitmap getImageBitmap() {
        String path = getImagePath();
        Bitmap b = decodeFileFromPath(path);
        if (b != null) {
            try {
                ExifInterface ei = new ExifInterface(path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                        break;
                    default:
                        b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            FileOutputStream out1 = null;
            File file;
            try {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    file = new File(Environment.getExternalStorageDirectory() + "/" + mStorageLocation +
                            "/", "image" + new Date().getTime() + ".jpg");
                } else {
                    file = new File(mActivity.getFilesDir(), "image" + new Date().getTime() + ".jpg");
                }
                out1 = new FileOutputStream(file);
                scaleCenterCrop(b, mDesiredWidth, mDesiredHeight).compress(Bitmap.CompressFormat.JPEG, 90, out1);
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out1 != null) {
                        out1.close();
                    }
                } catch (Exception ignore) {

                }
            }
        }
        return null;
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    private Bitmap decodeFileFromPath(String path){
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = mActivity.getContentResolver().openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();
            int scale = 1;
            int inSampleSize = 1024;
            if (o.outHeight > inSampleSize || o.outWidth > inSampleSize) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(inSampleSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mActivity.getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();
            return b;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    public Uri setImageUri() {
        createFolderIfNeeded();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(Environment.getExternalStorageDirectory() + "/" + mStorageLocation +
                    "/", "image" + new Date().getTime() + ".jpg");
            Uri imgUri = Uri.fromFile(file);
            this.mImagePath = file.getAbsolutePath();
            return imgUri;
        } else {
            File file = new File(mActivity.getFilesDir() , "image" + new Date().getTime() + ".jpg");
            Uri imgUri = Uri.fromFile(file);
            this.mImagePath = file.getAbsolutePath();
            return imgUri;
        }
    }

    private void createFolderIfNeeded() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + mStorageLocation);
        try{
            if(dir.mkdir()) {
                Log.d(getClass().getName(), "Directory named " + mStorageLocation + " created");
            } else {
                Log.d(getClass().getName(), "Directory named " + mStorageLocation + " not created");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Directory name cannot have spaces or special characters
     * @param directoryName
     */
    public void setDirectoryName(String directoryName) {
        mStorageLocation = directoryName;
    }

    public String getImagePath() {

        return mImagePath;
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
