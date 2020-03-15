package io.virtualapp.delegate;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.lody.virtual.client.hook.delegate.ComponentDelegate;
import com.lody.virtual.helper.utils.Reflect;

import java.io.File;

import timber.log.Timber;


public class MyComponentDelegate implements ComponentDelegate {

    @Override
    public void beforeApplicationCreate(Application application) {
//        Timber.e("beforeApplicationCreate pn" + application.getPackageName());
//        Timber.e("beforeApplicationCreate name" + application.getApplicationInfo().loadLabel(application.getPackageManager()));
    }

    @Override
    public void afterApplicationCreate(Application application) {
//        Timber.e("afterApplicationCreate pn" + application.getPackageName());
//        Timber.e("afterApplicationCreate name" + application.getApplicationInfo().loadLabel(application.getPackageManager()));
    }

    @Override
    public void beforeActivityCreate(Activity activity) {

    }

    @Override
    public void beforeActivityResume(Activity activity) {

    }

    @Override
    public void beforeActivityPause(Activity activity) {

    }

    @Override
    public void beforeActivityDestroy(Activity activity) {

    }

    @Override
    public void afterActivityCreate(Activity activity) {

    }

    @Override
    public void afterActivityResume(Activity activity) {

    }

    @Override
    public void afterActivityPause(Activity activity) {

    }

    @Override
    public void afterActivityDestroy(Activity activity) {

    }

    @Override
    public void onSendBroadcast(Intent intent) {

    }
}
