package io.virtualapp.abs;

/**
 * @author Lody
 */
public interface BasePresenter {

    void onCreate();

    void onResume();

    void onPause();

    void onDestroy();
}
