package com.example.cheatchat.cheatchatapps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String Description;
    private StorageReference PostsImageRefrence;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;
    private long countPosts = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostsImageRefrence = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        SelectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        UpdatePostButton = (Button) findViewById(R.id.update_post_button);
        PostDescription = (EditText) findViewById(R.id.post_description);
        loadingBar = new ProgressDialog(this);



        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });


    }

    private void ValidatePostInfo() {
        Description = PostDescription.getText().toString();

        if (ImageUri == null){
            Toast.makeText(this, "Please Select Post Image ...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please Write Post Description ...", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait, while your updating your new post");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();
        }
    }

    private void StoringImageToFirebaseStorage() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostsImageRefrence.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri.toString();

                                }
                            });
                        }
                    });

                    Toast.makeText(PostActivity.this,"Image Uploaded Successfully to Storage ...", Toast.LENGTH_SHORT).show();
                    SavingPostInformationToDatabase();

                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error Ocurred" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void SavingPostInformationToDatabase() {
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.exists()){
                  countPosts = dataSnapshot.getChildrenCount();
              }else{
                  countPosts = 0;
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                String userFullName = dataSnapshot.child("name").getValue().toString();
                String userProfileImage = dataSnapshot.child("image").getValue().toString();

                HashMap postsMap = new HashMap();
                postsMap.put("uid", current_user_id);
                postsMap.put("date", saveCurrentDate);
                postsMap.put("time", saveCurrentTime);
                postsMap.put("description", Description);
                postsMap.put("postimage", downloadUrl);
                postsMap.put("profileimage", userProfileImage);
                postsMap.put("fullname", userFullName);
                postsMap.put("counter", countPosts);
                PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(PostActivity.this, "Post is Updated Successfully ...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
//                            SendUserToMainActivity();

                        }else {
                            Toast.makeText(PostActivity.this, "Error Occurred While Your Updated Post ...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                });
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_Pick && resultCode== RESULT_OK && data!= null){
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id== android.R.id.home){
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {
        Intent MainIntent = new Intent(PostActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

}
