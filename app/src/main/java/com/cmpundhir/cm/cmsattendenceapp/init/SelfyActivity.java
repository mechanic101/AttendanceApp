package com.cmpundhir.cm.cmsattendenceapp.init;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cmpundhir.cm.cmsattendenceapp.MainActivity;
import com.cmpundhir.cm.cmsattendenceapp.R;
import com.cmpundhir.cm.cmsattendenceapp.util.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.OnClick;

public class SelfyActivity extends AppCompatActivity implements View.OnClickListener
{

    LinearLayout ll1;
    ImageView img;
    Button btn;
    private static final String TAG = "SelfyActivity";
    private void init(){
        img = findViewById(R.id.imageView2);
        ll1 = findViewById(R.id.ll1);
        btn = findViewById(R.id.button);
        btn.setOnClickListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfy);
        init();

    }

    @Override
    public void onClick(View view) {
        dispatchTakePictureIntent();
    }

    String currentPhotoPath;
    Uri photoURI;
    private File createImageFile() throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFileName = FirebaseAuth.getInstance().getUid();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.cmpundhir.cm.cmsattendenceapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    Bitmap bitmap;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==REQUEST_TAKE_PHOTO) {
            //Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                //bitmap = resizeBitmap(bitmap);
                img.setImageBitmap(bitmap);
                addToStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap){
        return Bitmap.createScaledBitmap(bitmap, 240, 240, false);
    }
    private StorageReference mStorageRef;
    private void addToStorage(){
        ll1.setVisibility(View.VISIBLE);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference riversRef = mStorageRef.child(Constants.PROFILE_PICTURES).child(FirebaseAuth.getInstance().getUid()+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        riversRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Log.d(TAG,taskSnapshot.toString());
                        Intent intent = new Intent(SelfyActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        ll1.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        ll1.setVisibility(View.GONE);
                        Toast.makeText(SelfyActivity.this, "Error in uploading...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,exception.getMessage());
                    }
                });
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG,uri.toString());
                Log.d(TAG,"url : "+riversRef.getDownloadUrl());
                Toast.makeText(SelfyActivity.this, "Success : "+uri, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
