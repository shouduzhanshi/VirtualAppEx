package io.virtualapp.home.listapp;

import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.util.List;

import io.virtualapp.abs.BasePresenter;
import io.virtualapp.abs.BaseView;
import io.virtualapp.home.models.AppInfo;

/**
 * @author Lody
 * @version 1.0
 */
/*package*/ class ListAppContract {
    interface ListAppView extends BaseView<ListAppPresenter> {

        void startLoading();

        void loadFinish(List<AppInfo> infoList);
    }

    interface ListAppPresenter extends BasePresenter {

        void setParm(FragmentActivity activity, ListAppContract.ListAppView view, File selectFrom);
    }
}
