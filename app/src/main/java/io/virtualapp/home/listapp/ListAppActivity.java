package io.virtualapp.home.listapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.os.VUserInfo;
import com.lody.virtual.os.VUserManager;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.virtualapp.R;
import io.virtualapp.VCommends;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.home.adapters.AppPagerAdapter;
import io.virtualapp.home.models.AppInfoLite;
import io.virtualapp.home.models.MultiplePackageAppData;
import io.virtualapp.home.models.PackageAppData;
import io.virtualapp.home.repo.PackageAppDataStorage;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * @author Lody
 */
public class ListAppActivity extends VActivity {

    private Toolbar mToolBar;

    public static void gotoListApp(Activity activity) {
        Intent intent = new Intent(activity, ListAppActivity.class);
        activity.startActivityForResult(intent, VCommends.REQUEST_SELECT_APP);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
    }

    @Override
    public int setViewRes() {
        return R.layout.activity_clone_app;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        mToolBar = (Toolbar) findViewById(R.id.clone_app_tool_bar);
//        mTabLayout = (TabLayout) mToolBar.findViewById(R.id.clone_app_tab_layout);
//        mViewPager = (ViewPager) findViewById(R.id.clone_app_view_pager);
        setupToolBar();
//        mViewPager.setAdapter(new AppPagerAdapter(getSupportFragmentManager()));
//        mTabLayout.setupWithViewPager(mViewPager);
        // Request permission to access external storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }else{
                getSupportFragmentManager().beginTransaction().add(R.id.clone_app_view_pager,ListAppFragment.newInstance(null)).commit();
            }
        }else{
            getSupportFragmentManager().beginTransaction().add(R.id.clone_app_view_pager,ListAppFragment.newInstance(null)).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (9101 == requestCode && data != null) {
            VUiKit.defer().when(() -> {
                ArrayList<AppInfoLite> dataList = new ArrayList<>();
                try {
                    Uri uri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Source source = Okio.source(inputStream);
                    BufferedSource buffer = Okio.buffer(source);
                    String s = getCacheDir().getPath() + "/"+System.currentTimeMillis()+".apk";
                    BufferedSink buffer1 = Okio.buffer(Okio.sink(new File(s)));
                    buffer1.writeAll(buffer);
                    buffer1.flush();
                    buffer1.close();
                    buffer.close();
                    dataList.add(new AppInfoLite(null,s,false));
                    data.putParcelableArrayListExtra(VCommends.EXTRA_APP_INFO_LIST, dataList);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK,data);
                finish();
            }).then((res) -> {

            }).done(res -> {

            });
        }
    }

    private void setupToolBar() {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
//                mViewPager.setAdapter(new AppPagerAdapter(getSupportFragmentManager()));
                getSupportFragmentManager().beginTransaction().add(R.id.clone_app_view_pager,ListAppFragment.newInstance(null)).commit();
                break;
            }
        }
    }
}
