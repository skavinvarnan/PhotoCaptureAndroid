# PhotoCaptureAndroid
An easy library to capture the picture with Android camera and convert to Bitmap


### Features ###

1. Capture picture from the camera and convert it directly to bitmap
2. Get the size of the picture in and desired size
3. Automatically handles the auto rotate issues on non nexus devices


##How to use

Add this to your **build.gradle**:
```java

dependencies {
  compile 'com.skvarnan.photo.capture:photo-capture:0.2'
}
```

Add this to your **AndroidManifest.xml**:
```xml
<uses-feature
    android:name="android.hardware.camera"
    android:required="true" />

<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

Create a new **Activity**:
```java
public class MainActivity extends AppCompatActivity {

    private PhotoCaptureHelper mPhotoCaptureHelper;
    private static final int CAPTURE_IMAGE = 1001;

    private Bitmap mSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        mPhotoCaptureHelper = new PhotoCaptureHelper(this);
        //Set the name of the folder where the pictures are saved in the sd card
        mPhotoCaptureHelper.setDirectoryName("MyDir");
        //Set the desired height and width of the photo
        mPhotoCaptureHelper.setPhotoHeightWidth(512, 1024);
        
        
        setContentView(R.layout.activity_main);
    }

    public void click(View v) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        // From Marshmellow you have to get the permission before accessing the phone memory
        PhotoCaptureHelper.verifyStoragePermissions(this);
        
        //Start the intent
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoCaptureHelper.setImageUri());
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE) {
            
                //Capture the intent and directly convert to bitmap
                mSelectedImage = mPhotoCaptureHelper.getImageBitmap();
                
                ((ImageView) findViewById(R.id.image)).setImageBitmap(mSelectedImage);
            }
        }

    }

}
```

## Bugs
This library is still in beta phase and if you find any bugs in it please open and [new issue](https://github.com/skavinvarnan/PhotoCaptureAndroid/issues/new)!


## Apps using it
If you are using AppIntro in your app and would like to be listed here, please let us know by adding a [comment](https://github.com/skavinvarnan/PhotoCaptureAndroid/issues/1)!

