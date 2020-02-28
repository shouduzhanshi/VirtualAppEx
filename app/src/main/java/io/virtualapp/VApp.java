package io.virtualapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.stub.VASettings;
import com.yc.nonsdk.NonSdkManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import io.virtualapp.delegate.MyAppRequestListener;
import io.virtualapp.delegate.MyComponentDelegate;
import io.virtualapp.delegate.MyPhoneInfoDelegate;
import io.virtualapp.delegate.MyTaskDescriptionDelegate;
import jonathanfinerty.once.Once;
import timber.log.Timber;

/**
 * @author Lody
 */
public class VApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static VApp gApp;

    private SharedPreferences mPreferences;

    public static VApp getApp() {
        return gApp;
    }

    private static List<Activity> activitys = new ArrayList<>();

    public static List<Activity> getActivitys() {
        return activitys;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mPreferences = base.getSharedPreferences("va", Context.MODE_MULTI_PROCESS);
        VASettings.ENABLE_IO_REDIRECT = true;
        VASettings.ENABLE_INNER_SHORTCUT = false;
        initLog();
        NonSdkManager.getInstance().visibleAllApi();
        try {
            VirtualCore.get().startup(base);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void initLog() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, String message, Throwable t) {
                switch (priority) {
                    case Log.DEBUG:
                        if (t != null) {
                            Log.d(tag, message);
                        } else {
                            Log.d(tag, message, t);
                        }
                        break;
                    case Log.ERROR:
                        if (t != null) {
                            Log.e(tag, message);
                        } else {
                            Log.e(tag, message, t);
                        }
                        break;
                    case Log.INFO:
                        if (t != null) {
                            Log.i(tag, message);
                        } else {
                            Log.i(tag, message, t);
                        }
                        break;
                    case Log.WARN:
                        if (t != null) {
                            Log.w(tag, message);
                        } else {
                            Log.w(tag, message, t);
                        }
                        break;

                }
            }
        });
    }

    @Override
    public void onCreate() {
        gApp = this;
        super.onCreate();
        VirtualCore virtualCore = VirtualCore.get();
        virtualCore.initialize(new VirtualCore.VirtualInitializer() {
            @Override
            public void onMainProcess() {
                Once.initialise(VApp.this);
            }

            @Override
            public void onVirtualProcess() {
                //listener components
                virtualCore.setComponentDelegate(new MyComponentDelegate());
                //fake phone imei,macAddress,BluetoothAddress
                virtualCore.setPhoneInfoDelegate(new MyPhoneInfoDelegate());
                //fake task description's icon and title
                virtualCore.setTaskDescriptionDelegate(new MyTaskDescriptionDelegate());
            }

            @Override
            public void onServerProcess() {
                virtualCore.setAppRequestListener(new MyAppRequestListener(VApp.this));
                virtualCore.addVisibleOutsidePackage("com.tencent.mobileqq");
                virtualCore.addVisibleOutsidePackage("com.tencent.mobileqqi");
                virtualCore.addVisibleOutsidePackage("com.tencent.minihd.qq");
                virtualCore.addVisibleOutsidePackage("com.tencent.qqlite");
                virtualCore.addVisibleOutsidePackage("com.facebook.katana");
                virtualCore.addVisibleOutsidePackage("com.whatsapp");
                virtualCore.addVisibleOutsidePackage("com.tencent.mm");
                virtualCore.addVisibleOutsidePackage("com.immomo.momo");
            }
        });
        registerActivityLifecycleCallbacks(this);
    }

    public static SharedPreferences getPreferences() {
        return getApp().mPreferences;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.e("App", activity.getLocalClassName());
        activitys.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activitys.remove(activity);
    }
}
