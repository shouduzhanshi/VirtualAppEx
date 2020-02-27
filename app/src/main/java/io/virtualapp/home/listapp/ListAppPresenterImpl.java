package io.virtualapp.home.listapp;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import java.io.File;

import io.virtualapp.home.repo.AppDataSource;
import io.virtualapp.home.repo.AppRepository;

/**
 * @author Lody
 */
public class ListAppPresenterImpl implements ListAppContract.ListAppPresenter {

	private Activity mActivity;
	private ListAppContract.ListAppView mView;
	private AppDataSource mRepository;

	private File from;

	@Override
	public void onCreate() {
		mView.startLoading();
		if (from == null)
			mRepository.getInstalledApps(mActivity).done(mView::loadFinish);
		else
			mRepository.getStorageApps(mActivity, from).done(mView::loadFinish);
	}

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setParm(FragmentActivity activity, ListAppContract.ListAppView view, File selectFrom) {
        mActivity = activity;
        mView = view;
        mRepository = new AppRepository(activity);
        this.from = selectFrom;
    }
}
