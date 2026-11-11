package net.irext.ircontrol.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import net.irext.decode.sdk.utils.Constants;
import net.irext.ircontrol.IRApplication;
import net.irext.ircontrol.R;
import net.irext.ircontrol.ui.activity.CreateActivity;
import net.irext.ircontrol.ui.adapter.CategoryAdapter;
import net.irext.ircontrol.ui.widget.PullToRefreshListView;
import net.irext.ircontrol.utils.MessageUtils;
import net.irext.webapi.WebAPICallbacks;
import net.irext.webapi.model.Category;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Filename:       CategoryFragment.java
 * Revised:        Date: 2017-04-07
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Category Fragment
 * <p>
 * Revision log:
 * 2017-04-07: created by strawmanbobi
 */
@SuppressWarnings("unused")
public class CategoryFragment extends BaseCreateFragment {

    private static final String TAG = CategoryFragment.class.getSimpleName();

    private static final int CMD_REFRESH_CATEGORY_LIST = 0;

    private PullToRefreshListView mCategoryList;
    private CategoryAdapter mCategoryAdapter;

    private List<Category> mCategories;

    private MsgHandler mMsgHandler;
    private IRApplication mApp;

    private WebAPICallbacks.ListCategoriesCallback mListCategoriesCallback = new WebAPICallbacks.ListCategoriesCallback() {
        @Override
        public void onListCategoriesSuccess(List<Category> categories) {
            mCategories = categories;
            if (null == mCategories) {
                mCategories = new ArrayList<>();
            }
            MessageUtils.postMessage(mMsgHandler, CMD_REFRESH_CATEGORY_LIST);
        }

        @Override
        public void onListCategoriesFailed() {
            Log.w(TAG, "list categories failed");
        }

        @Override
        public void onListCategoriesError() {
            Log.e(TAG, "list categories error");
        }
    };

    public CategoryFragment() {
    }

    private void listCategories() {
        new Thread() {
            @Override
            public void run() {
                mApp.mWeAPIs.listCategories(0, 20, mListCategoriesCallback);
            }
        }.start();
    }

    private void refreshCategories() {
        mCategoryAdapter = new CategoryAdapter(mParent, mCategories);
        mCategoryList.setAdapter(mCategoryAdapter);
        mCategoryList.onRefreshComplete();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mFrom = -1;
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mApp = (IRApplication) requireActivity().getApplication();

        mMsgHandler = new MsgHandler(this);

        mCategoryList = (PullToRefreshListView) view.findViewById(R.id.lv_category_list);

        mCategoryList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listCategories();
            }
        });

        mCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category)mCategoryAdapter.getItem(position);
                mParent.setCurrentCategory(category);
                if (category.getId() != Constants.CategoryID.STB.getValue()) {
                    MessageUtils.postMessage(mParent.mMsgHandler,
                            CreateActivity.PAGE_BRAND,
                            CreateActivity.PAGE_CATEGORY);
                } else {
                    MessageUtils.postMessage(mParent.mMsgHandler,
                            CreateActivity.PAGE_CITY,
                            CreateActivity.PAGE_CATEGORY);
                }
            }
        });
        mCategoryList.setRefreshing();
        return view;
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    private static class MsgHandler extends Handler {

        WeakReference<CategoryFragment> mCategoryFragment;

        MsgHandler(CategoryFragment fragment) {
            mCategoryFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            int cmd = msg.getData().getInt(MessageUtils.KEY_CMD);
            Log.d(TAG, "handle message " + cmd);

            CategoryFragment categoryFragment = mCategoryFragment.get();
            if (cmd == CMD_REFRESH_CATEGORY_LIST) {
                categoryFragment.refreshCategories();
            }
        }
    }
}
