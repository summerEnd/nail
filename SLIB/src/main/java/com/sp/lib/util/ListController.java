package com.sp.lib.util;

import android.content.Context;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.logging.Logger;

/**
 * 控制ListView GridView滚动到底部自动加载
 */
public class ListController implements AbsListView.OnScrollListener {


    public interface Callback {
        public void onLoadMore(AbsListView listView, int page);
    }

    private static final int DEFAULT_PAGE_SIZE = 15;
    Callback    callback;
    AbsListView mListView;
    boolean isBottom = false;
    int pageSize;
    int mSavedSize = 0;

    public ListController() {
        this(DEFAULT_PAGE_SIZE);
    }


    public ListController(int pageSize) {
        this.pageSize = pageSize;
    }

    public ListController(AbsListView listView, Callback callback) {
        this(DEFAULT_PAGE_SIZE);
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        //滚动停止时，如果在最底部，判断是否需要加载更多。
        if (scrollState == SCROLL_STATE_IDLE&&isBottom) {
            int count = mListView.getAdapter().getCount();
            int loadedCount = count - mSavedSize;
            mSavedSize=count;
            if (loadedCount >=pageSize ){
                int page = count / pageSize + 1;
                callback.onLoadMore(mListView, page);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isBottom = firstVisibleItem + visibleItemCount == totalItemCount;
    }
}
