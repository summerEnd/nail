package com.sp.lib.util;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.logging.Logger;

/**
 * 控制ListView GridView滚动到底部自动加载
 */
public class ListController implements AbsListView.OnScrollListener {

    final String TAG = "listController";

    public interface Callback {
        public void onLoadMore(AbsListView listView, int nextPage);
    }

    private static final int DEFAULT_PAGE_SIZE = 15;
    Callback    callback;
    AbsListView mListView;
    boolean isBottom   = false;
    int     pageSize   = DEFAULT_PAGE_SIZE;
    int     mSavedSize = 0;
    //上一次加载了多少
    int lastLoadedCount;
    boolean dataSetObserverRegistered = false;


    public ListController(AbsListView listView, Callback callback) {
        control(listView, callback);
    }

    public int getPageSize() {
        return pageSize;
    }

    private void control(AbsListView listView, Callback callback) {
        this.callback = callback;
        this.mListView = listView;
        mListView.setOnScrollListener(this);


    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        lastLoadedCount = pageSize;
    }

    /**
     * 数据变化观察者,当{@link android.widget.BaseAdapter#notifyDataSetChanged()}时触发
     */
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            ListAdapter adapter = mListView.getAdapter();
            lastLoadedCount = adapter.getCount() - mSavedSize;
            mSavedSize = adapter.getCount();

            Log.d(TAG, String.format("notifyDataSetChanged--> saved size:%d new page:%d", mSavedSize, lastLoadedCount));
        }
    };

    public void registerDataObserver() {
        ListAdapter adapter = mListView.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("AbsListView doesn't have a adapter");
        }
        mSavedSize = adapter.getCount();
        adapter.registerDataSetObserver(dataSetObserver);
        dataSetObserverRegistered = true;
    }

    public void unRegisterDataObserver() {
        ListAdapter adapter = mListView.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("AbsListView doesn't have a adapter");
        }

        if (dataSetObserverRegistered)
            adapter.unregisterDataSetObserver(dataSetObserver);
        dataSetObserverRegistered = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //滚动停止时，如果在最底部，判断是否需要加载更多。
        if (scrollState == SCROLL_STATE_IDLE && isBottom) {

            ListAdapter adapter = mListView.getAdapter();
            if (!dataSetObserverRegistered)
                registerDataObserver();
            Log.d(TAG, String.format("loadMore:%s mSavedSize：%d count:%d lastPage:%d ", (lastLoadedCount >= pageSize ? "true" : "false"), mSavedSize, adapter.getCount(), lastLoadedCount));
            if (lastLoadedCount >= pageSize) {
                int page = adapter.getCount() / pageSize + 1;
                callback.onLoadMore(mListView, page);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isBottom = firstVisibleItem + visibleItemCount == totalItemCount;
    }

}
