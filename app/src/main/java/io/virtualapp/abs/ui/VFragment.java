package io.virtualapp.abs.ui;

import org.jdeferred.android.AndroidDeferredManager;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

//import butterknife.ButterKnife;
import io.virtualapp.abs.BasePresenter;

/**
 * @author Lody
 */
public abstract class VFragment<T extends BasePresenter> extends Fragment {

    protected T mPresenter;

    public T getPresenter() throws Exception, IllegalAccessException {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            Type[] actualTypeArguments = p.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                Class c = (Class) actualTypeArguments[0];
                return (T) c.newInstance();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    protected AndroidDeferredManager defer() {
        return VUiKit.defer();
    }

    public void finishActivity() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    public void destroy() {
        finishActivity();
    }

    public abstract int setViewRes();

    public abstract void initView(View view);

    public View setView() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    View fragmentRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (fragmentRootView == null) {
            int layoutRes = setViewRes();
            if (layoutRes == 0) {
                View view = setView();
                if (view != null) {
                    fragmentRootView = inflaterView(view);
                }
            } else {
                fragmentRootView = inflaterView(inflater.inflate(layoutRes, null, false));
            }
            return fragmentRootView;
        } else {
            container.removeView(fragmentRootView);
            return fragmentRootView;
        }
    }

    private View inflaterView(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initView(view);
                try {
                    mPresenter = getPresenter();
                    if (mPresenter != null) {
                        setPresenterParm(mPresenter);
                        mPresenter.onCreate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        ButterKnife.bind(view);
        return view;
    }

    protected void setPresenterParm(T mPresenter) {

    }
}
