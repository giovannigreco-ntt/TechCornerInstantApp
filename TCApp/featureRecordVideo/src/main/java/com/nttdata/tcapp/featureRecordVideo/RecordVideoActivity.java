package com.nttdata.tcapp.featureRecordVideo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RecordVideoActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA = 100;
    private final int PICK_VIDEO_REQUEST = 101;

    private TextView consoleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);

        initUIRefs();

    }

    private void initUIRefs() {
        consoleTV = findViewById(R.id.console_tv);

        findViewById(R.id.startRecording_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionsAndStartCamera();
            }
        });
    }

    private void checkPermissionsAndStartCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);

        } else {
            startVideoRecording();
        }

    }

    private void startVideoRecording() {

        startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE), PICK_VIDEO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i=0; i<permissions.length; i++) {
            if(permissions[i].equals(Manifest.permission.CAMERA)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    startVideoRecording();
                }
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_VIDEO_REQUEST) {
                log("Video recorded");
                Uri selectedVideoUri = data.getData();
                //TODO: upload video to firebase
            }
        }
    }

    private void log(String log) {

        consoleTV.setText(log);

    }
}