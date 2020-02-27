package io.virtualapp.home.listapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import io.virtualapp.R;
import io.virtualapp.VCommends;
import io.virtualapp.abs.ui.VFragment;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.home.adapters.CloneAppListAdapter;
import io.virtualapp.home.adapters.decorations.ItemOffsetDecoration;
import io.virtualapp.home.models.AppInfo;
import io.virtualapp.home.models.AppInfoLite;
import io.virtualapp.widgets.DragSelectRecyclerView;

/**
 * @author Lody
 */
public class ListAppFragment extends VFragment<ListAppPresenterImpl> implements ListAppContract.ListAppView {
    private static final String KEY_SELECT_FROM = "key_select_from";

    @BindView(R.id.select_app_recycler_view)
    DragSelectRecyclerView mRecyclerView;
    @BindView(R.id.select_app_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.select_app_install_btn)
     Button mInstallButton;
    private CloneAppListAdapter mAdapter;

    public static ListAppFragment newInstance(File selectFrom) {
        Bundle args = new Bundle();
        if (selectFrom != null) {
            args.putString(KEY_SELECT_FROM, selectFrom.getPath());
        }
        ListAppFragment fragment = new ListAppFragment();
//        args.putInt("type", position);
        fragment.setArguments(args);
        return fragment;
    }

    private File getSelectFrom() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String selectFrom = bundle.getString(KEY_SELECT_FROM);
            if (selectFrom != null) {
                return new File(selectFrom);
            }
        }
        return null;
    }

    @Override
    public int setViewRes() {
        return R.layout.fragment_list_app;
    }

    @Override
    public void initView(View view) {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(VUiKit.dpToPx(getContext(), 2)));
        mAdapter = new CloneAppListAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CloneAppListAdapter.ItemEventListener() {
            @Override
            public void onItemClick(AppInfo info, int position) {
                if (info.type == 1) {
                    onAddAppFromDiskClick();
                    return;
                }
                int count = mAdapter.getSelectedCount();
                if (!mAdapter.isIndexSelected(position)) {
                    if (count >= 9) {
                        Toast.makeText(getContext(), R.string.install_too_much_once_time, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                mAdapter.toggleSelected(position);
            }

            @Override
            public boolean isSelectable(int position) {
                return mAdapter.isIndexSelected(position) || mAdapter.getSelectedCount() < 9;
            }
        });
        mAdapter.setSelectionListener(count -> {
            mInstallButton.setEnabled(count > 0);
            mInstallButton.setText(String.format(Locale.ENGLISH, getResources().getString(R.string.install_d), count));
        });
        mInstallButton.setOnClickListener(v -> {
            Integer[] selectedIndices = mAdapter.getSelectedIndices();
            ArrayList<AppInfoLite> dataList = new ArrayList<AppInfoLite>(selectedIndices.length);
            for (int index : selectedIndices) {
                AppInfo info = mAdapter.getItem(index);
                dataList.add(new AppInfoLite(info.packageName, info.path, info.fastOpen));
            }
            Intent data = new Intent();
            data.putParcelableArrayListExtra(VCommends.EXTRA_APP_INFO_LIST, dataList);
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        });
    }

    @Override
    protected void setPresenterParm(ListAppPresenterImpl mPresenter) {
        mPresenter.setParm(getActivity(), this, getSelectFrom());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.saveInstanceState(outState);
    }

    private void onAddAppFromDiskClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getActivity().startActivityForResult(intent, 9101);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void startLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void loadFinish(List<AppInfo> infoList) {
        mAdapter.setList(infoList);
        mRecyclerView.setDragSelectActive(false, 0);
        mAdapter.setSelected(0, false);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        AppInfo appInfo = new AppInfo();
        appInfo.icon = getResources().getDrawable(R.drawable.ic_add_circle);
        appInfo.name = "从磁盘添加";
        appInfo.type = 1;
        infoList.add(0, appInfo);
    }


}
