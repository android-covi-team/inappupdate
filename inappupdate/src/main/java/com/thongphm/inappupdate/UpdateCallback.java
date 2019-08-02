package com.thongphm.inappupdate;

/**
 * Created by thongphm on 2019-08-02
 */
public interface UpdateCallback {
    void onUpdateNotAvailable();

    void onImmediateUpdateFailed();

    void onFlexibleUpdateFailed();

    void onNewAppIsDownloaded();

    void onCancelledFlexibleUpdate();

    void onCancelledImmediateUpdate();
}
