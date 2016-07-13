package jp.co.wakawaka.tumvie.searchlist;

import android.widget.AbsListView;

/**
 * 参考：https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView
 * Created by wakabayashieisuke on 2016/07/08.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
    // The minimum number of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 3;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    /**
     * This happens many times a second during a scroll, so be wary of the code you place here.
     * We are given a few useful parameters to help us work out if we need to load some more data,
     * but first we check if we are waiting for the previous load to finish.
     * @param view View
     * @param firstVisibleItem 表示されているitemのindex
     * @param visibleItemCount 表示されているitem総数
     * @param totalItemCount item総数
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // If it's still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading
                && (totalItemCount > previousTotalItemCount)
                && totalItemCount != 0) {
            loading = false;
        }
        previousTotalItemCount = totalItemCount;

        // If it isn't currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (firstVisibleItem + visibleItemCount) >= totalItemCount ) {
            loading = onLoadMore(totalItemCount);
        }
    }

    // Defines the process for actually loading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    public abstract boolean onLoadMore(int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }
}
