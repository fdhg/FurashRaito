package com.fdhg.projects.furashraito;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;


public class Flashlight extends Activity implements View.OnClickListener {

    private static ImageView ivSwitch;
    private static Camera camera;
    private static boolean isFlashOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);

        checkFlashSupport();
        initialize();
    }

    private void initialize() {
        ivSwitch = (ImageView) findViewById(R.id.ivSwitch);

        toggleButtonImage();
        ivSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isFlashOn)
            turnOffFlash();
        else
            turnOnFlash();
    }

    // check if device has flash support
    private void checkFlashSupport() {
        boolean hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        // shows alert message and closes application if device has no flash support
        if (!hasFlash) {
            AlertDialog alert = new AlertDialog.Builder(Flashlight.this).create();
            alert.setTitle(getString(R.string.dialogTitle));
            alert.setMessage(getString(R.string.dialogMessage));
            alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialogOK),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // closes application when ok button is pressed
                            finish();
                        }
                    });
            alert.show();
        }
    }

    // open camera and turn on flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            camera = Camera.open();
            if (camera != null) {
                playSound();
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();
                isFlashOn = true;

                toggleButtonImage();
            }
        }
    }

    // turn off flash and release camera
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera != null) {
                playSound();
                camera.stopPreview();
                camera.release();
                camera = null;
                isFlashOn = false;
                toggleButtonImage();
            }
        }
    }

    // play clicking sound when button is pressed
    private void playSound(){
        MediaPlayer mp;
        if (isFlashOn)
            mp = MediaPlayer.create(Flashlight.this, R.raw.power_off);
        else
            mp = MediaPlayer.create(Flashlight.this, R.raw.power_on);

        // release sound resource after playing
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    // toggle switch image based on flash state
    private void toggleButtonImage() {
        if (isFlashOn)
            ivSwitch.setImageResource(R.drawable.power_on);
        else
            ivSwitch.setImageResource(R.drawable.power_off);
    }

    // save flash state after pressing back button
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fdhg", isFlashOn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFlashOn = savedInstanceState.getBoolean("fdhg");
    }
}