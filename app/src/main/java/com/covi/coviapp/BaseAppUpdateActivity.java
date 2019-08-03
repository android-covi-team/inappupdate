package com.covi.coviapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.thongphm.inappupdate.UpdateCallback;
import com.thongphm.inappupdate.UpdateManager;

/**
 * Created by thongphm on 2019-08-02
 */
public abstract class BaseAppUpdateActivity extends AppCompatActivity implements UpdateCallback {

    private UpdateManager mUpdateManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUpdateManager = new UpdateManager(this, UpdateManager.UPDATE_FLEXIBLE, this, this);
    }

    @Override
    public void onUpdateNotAvailable() {
        Toast.makeText(this, "You're using the latest app", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImmediateUpdateFailed() {
        showPopupUpdateFailed();
    }

    @Override
    public void onFlexibleUpdateFailed() {
        showPopupUpdateFailed();
    }

    @Override
    public void onCancelledFlexibleUpdate() {
        Toast.makeText(this, "Cancelled flexible update", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelledImmediateUpdate() {
        Toast.makeText(this, "Cancelled immediate update", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onNewAppIsDownloaded() {
        View.OnClickListener positiveListener = (view) -> mUpdateManager.installNewApp();
        showPopup("New app is already downloaded", "INSTALL", "IGNORE",
                positiveListener, null);
    }

    private void showPopupUpdateFailed() {
        View.OnClickListener positiveListener = (view) -> mUpdateManager.checkUpdate();
        showPopup("Update failed. Do you want to try again?", "RETRY", "CANCEL",
                positiveListener, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUpdateManager.onActivityForResult(requestCode, resultCode);
    }

    private void showPopup(String message, String positive, String negative,
                           @Nullable View.OnClickListener positiveClick,
                           @Nullable View.OnClickListener negativeClick) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton(positive, null);
        dialogBuilder.setNegativeButton(negative, null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {

            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                dialog.dismiss();
                if (positiveClick != null) {
                    positiveClick.onClick(v);
                }
            });

            Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(v -> {
                dialog.dismiss();
                if (negativeClick != null) {
                    negativeClick.onClick(v);
                }
            });
        });

        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (isFinishing()) {
            return;
        }
        alertDialog.show();
    }
}
