package com.pal.mapd.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.pal.mapd.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private CircleImageView user_image;
    private EditText user_name;
    private Button next;
    private static final int GALLERY_PICK = 1;
    private StorageReference mStorage;
    private DatabaseReference mRootRef;
    private FirebaseUser mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mStorage = FirebaseStorage.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        user_image =findViewById(R.id.user_img);
        findViewById(R.id.user_img).setOnClickListener(this);
        user_name = findViewById(R.id.user_name);
         findViewById(R.id.profile_next_btn).setOnClickListener(this);

    }

    private void sendToIntent() {
        Intent i = new Intent(ProfileActivity.this, AppPageActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                CropImage.activity(imageUri).setAspectRatio(1, 1).start(ProfileActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final Uri imageResult = result.getUri();

                final String user_id = mUser.getUid();

                final StorageReference filepath = mStorage.child("profile_images").child(user_id + ".jpg");
                UploadTask upload = filepath.putFile(imageResult);

                Task<Uri> url = upload.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        final Uri downloadUri = task.getResult();

                        if (downloadUri != null) {

                            String image = downloadUri.toString();
                            mRootRef.child("image").setValue(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Picasso.get()
                                                .load(downloadUri)
                                                .into(user_image);
                                    } else {

                                        Picasso.get().load(R.mipmap.ic_launcher).into(user_image);

                                        Toast.makeText(ProfileActivity.this, "Could not collect the profile picture", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();



        if(mUser!=null){
            mRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
            final HashMap<String ,String> userMap = new HashMap<>();
            userMap.put("name" , " ");
            userMap.put("image" , " ");
            mRootRef.setValue(userMap);


        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.user_img:{

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
                break;
            }

            case R.id.profile_next_btn:{

                String s = user_name.getText().toString();
                mRootRef.child("name").setValue(s);
                sendToIntent();
                break;
            }


        }

    }
}
