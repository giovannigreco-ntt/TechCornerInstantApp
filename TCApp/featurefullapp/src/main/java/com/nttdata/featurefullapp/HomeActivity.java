package com.nttdata.featurefullapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nttdata.tcapp.featureRecordVideo.RecordVideoActivity;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RecordVideoActivity.class);
                startActivity(intent);
            }
        });

        container = findViewById(R.id.container);

        signIn();

    }

    private void downloadImagesUrl() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("images");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String,String> values = (Map<String, String>)dataSnapshot.getValue();
                Log.d("downloadImages", "Value is: " + values);

                if(values!=null)

                    downloadImages(values);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("downloadImages", "Failed to read value.", error.toException());
            }
        });

    }

    private void downloadImages(Map<String, String> values) {

        container.removeAllViews();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference;

        for (String key : values.keySet()) {
            gsReference = storage.getReferenceFromUrl(values.get(key));
            downloadAndShowImage(gsReference);
        }

    }

    private void downloadAndShowImage(StorageReference storageReference) {

        ImageView imageView = new ImageView(this);

        GlideApp.with(this /* context */)
                .load(storageReference)
                .into(imageView);

        container.addView(imageView);
    }


    private FirebaseAuth mAuth;
    private void signIn() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            downloadImagesUrl();
        } else {
            signInAnonymously();
        }
    }

    private void signInAnonymously(){
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override public void onSuccess(AuthResult authResult) {

                downloadImagesUrl();
            }
        }) .addOnFailureListener(this, new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception exception) {
                Log.e("TAG", "signInAnonymously:FAILURE", exception);
            }
        });
    }



}
