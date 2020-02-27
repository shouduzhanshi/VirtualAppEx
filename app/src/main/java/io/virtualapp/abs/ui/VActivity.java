package io.virtualapp.abs.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import org.jdeferred.android.AndroidDeferredManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

//import butterknife.ButterKnife;
import butterknife.ButterKnife;
import io.virtualapp.abs.BasePresenter;
import io.virtualapp.abs.BaseView;

/**
 * @author Lody
 */
public abstract class VActivity<P extends BasePresenter> extends AppCompatActivity {

    protected P mPresenter;

    protected final String TAG = getClass().getSimpleName();

    /**
     * Implement of {@link BaseView#getActivity()}
     */
    public Activity getActivity() {
        return this;
    }

    /**
     * Implement of {@link BaseView#getContext()} ()}
     */
    public Context getContext() {
        return this;
    }

    protected AndroidDeferredManager defer() {
        return VUiKit.defer();
    }

    public Fragment findFragmentById(@IdRes int id) {
        return getSupportFragmentManager().findFragmentById(id);
    }

    public void replaceFragment(@IdRes int id, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(id, fragment).commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        int layoutRes = setViewRes();
        if (layoutRes == 0) {
            View view = setView();
            if (view != null) {
                inflaterView(view,savedInstanceState);
            }
        } else {
            inflaterView(getLayoutInflater().inflate(layoutRes, null, false),savedInstanceState);
        }
    }

    private void inflaterView(View view,Bundle savedInstanceState) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initView(savedInstanceState);
                try {
                    mPresenter = getPresenter();
                    if (mPresenter != null){
                        setPresenterParm(mPresenter);
                        mPresenter.onCreate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setContentView(view);
        ButterKnife.bind(this);
    }

    protected  void setPresenterParm(P mPresenter){

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    public abstract int setViewRes();

    public abstract void initView(Bundle savedInstanceState);

    public View setView() {
        return null;
    }

    public P getPresenter() throws Exception, IllegalAccessException {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            Type[] actualTypeArguments = p.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                Class c = (Class) actualTypeArguments[0];
                return (P) c.newInstance();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


}
