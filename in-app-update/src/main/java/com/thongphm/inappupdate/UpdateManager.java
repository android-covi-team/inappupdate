package com.thongphm.inappupdate;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.IntentSender;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

/**
 * Created by thongphm on 2019-08-02
 */
public class UpdateManager implements OnSuccessListener<AppUpdateInfo>, LifecycleObserver {

    // Request code
    private static final int REQUEST_CODE_UPDATE = 1001;

    // Update Type
    public static final int UPDATE_FLEXIBLE = AppUpdateType.FLEXIBLE;
    public static final int UPDATE_IMMEDIATE = AppUpdateType.IMMEDIATE;
    private static int UPDATE_TYPE;

    private Activity mActivity;
    // Update Manager
    private AppUpdateManager mAppUpdateManager;
    // Callback
    private UpdateCallback mCallback;

    public UpdateManager(Activity activity, int updateType, LifecycleOwner owner, UpdateCallback callback) {
        mActivity = activity;
        mCallback = callback;
        UPDATE_TYPE = updateType;

        // Observer Life Cycle
        owner.getLifecycle().addObserver(this);

        checkUpdate();
        if (UPDATE_TYPE == AppUpdateType.FLEXIBLE) {
            mAppUpdateManager.registerListener(mInstallStateUpdatedListener);
        }
    }

    /**
     * Check update available or not
     */
    public void checkUpdate() {
        mAppUpdateManager = AppUpdateManagerFactory.create(mActivity.getApplicationContext());
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void resumeUpdate() {
        mAppUpdateManager = AppUpdateManagerFactory.create(mActivity.getApplicationContext());
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            switch (UPDATE_TYPE) {
                case AppUpdateType.FLEXIBLE:
                    checkInstallStatus(appUpdateInfo.installStatus());
                    break;

                case AppUpdateType.IMMEDIATE:
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        startUpdate(appUpdateInfo);
                    }
                    break;
            }
        });
    }

    private void startUpdate(AppUpdateInfo appUpdateInfo) {
        if (mActivity != null) {
            try {
                mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                        UPDATE_TYPE,
                        mActivity,
                        REQUEST_CODE_UPDATE);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Install downloaded app from Flexible update
     */
    public void installNewApp() {
        if (mAppUpdateManager != null) {
            mAppUpdateManager.completeUpdate();
        }
    }

    /**
     * Add this function below your onActivityForResult
     *
     * @param requestCode
     * @param resultCode
     */
    public void onActivityForResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CODE_UPDATE) {

            /*
             * The user has accepted the update.
             * For immediate updates, you might not receive this callback
             * because the update should already be completed by Google Play
             * by the time the control is given back to your app.
             */
            if (resultCode != Activity.RESULT_OK) {
                // If the update is cancelled or fails,
                // you can request to start the update again.
                if (resultCode == Activity.RESULT_CANCELED) {
                    switch (UPDATE_TYPE) {
                        case AppUpdateType.FLEXIBLE:
                            if (mCallback != null) {
                                mCallback.onCancelledFlexibleUpdate();
                            }
                            break;

                        case AppUpdateType.IMMEDIATE:
                            if (mCallback != null) {
                                mCallback.onCancelledImmediateUpdate();
                            }
                            break;
                    }
                } else {
                    switch (UPDATE_TYPE) {
                        case AppUpdateType.FLEXIBLE:
                            if (mCallback != null) {
                                mCallback.onFlexibleUpdateFailed();
                            }
                            break;

                        case AppUpdateType.IMMEDIATE:
                            if (mCallback != null) {
                                mCallback.onImmediateUpdateFailed();
                            }
                            break;
                    }
                }
            }
        }
    }

    private InstallStateUpdatedListener mInstallStateUpdatedListener = installState -> checkInstallStatus(installState.installStatus());

    private void checkInstallStatus(int installStatus) {
        switch (installStatus) {
            case InstallStatus.DOWNLOADED:
                if (mCallback != null) {
                    mCallback.onNewAppIsDownloaded();
                }
                break;

            case InstallStatus.INSTALLED:
                mAppUpdateManager.unregisterListener(mInstallStateUpdatedListener);
                break;
        }
    }

    @Override
    public void onSuccess(AppUpdateInfo appUpdateInfo) {
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdate(appUpdateInfo);
            }
        } else {
            if (mCallback != null) {
                mCallback.onUpdateNotAvailable();
            }
        }
    }
}
