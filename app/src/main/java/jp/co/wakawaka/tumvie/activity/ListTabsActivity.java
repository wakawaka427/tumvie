package jp.co.wakawaka.tumvie.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.favoritelist.FavoriteListFragment;
import jp.co.wakawaka.tumvie.historylist.HistoryListFragment;
import jp.co.wakawaka.tumvie.searchlist.Item;
import jp.co.wakawaka.tumvie.favoritelist.Favorite;
import jp.co.wakawaka.tumvie.searchlist.SearchListFragment;

public class ListTabsActivity extends AppCompatActivity {

    // TODO：どっかに移す
    public static final String POST_TYPE_TEXT = "text";     //テキスト
    public static final String POST_TYPE_QUOTE = "quote";   //引用
    public static final String POST_TYPE_PHOTO = "photo";   //画像
    public static final String POST_TYPE_LINK = "link";     //リンク
    public static final String POST_TYPE_CHAT = "chat";     //チャット
    public static final String POST_TYPE_AUDIO = "audio";   //音声
    public static final String POST_TYPE_VIDEO = "video";   //動画
    public static final String POST_TYPE_ANSWER = "answer"; //アンサー

    private enum Tab {
        FAVORITE(0),
        SEARCH(1),
        HISTORY(2);
        private final int id;
        Tab(final int id) {
            this.id = id;
        }
        public int getValue() {
            return this.id;
        }
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter sectionsPagerAdapter;

    private TabLayout tabLayout;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tabs);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.tabs_view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (Tab.FAVORITE.getValue() == position) {
                    ((FavoriteListFragment) sectionsPagerAdapter.getItem(position)).reloadList();
                }
                if (Tab.HISTORY.getValue() == position) {
                    ((HistoryListFragment) sectionsPagerAdapter.getItem(position)).reloadList();
                    fab.setVisibility(View.VISIBLE);
                } else {
                    // 履歴画面以外では邪魔なのでFloatingActionButtonを消しておく。
                    fab.setVisibility(View.GONE);
                }
                if (Tab.SEARCH.getValue() != position) {
                    // 検索画面から他のタブに移動するときにキーボードが表示されたままになるので消す。
                    ((SearchListFragment) sectionsPagerAdapter.getItem(Tab.SEARCH.getValue())).onFocusLoss();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setCurrentItem(Tab.SEARCH.getValue());

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(Tab.FAVORITE.getValue()).setCustomView(getTabImage(R.drawable.icon_favorite));
        tabLayout.getTabAt(Tab.SEARCH.getValue()).setCustomView(getTabImage(R.drawable.icon_search));
        tabLayout.getTabAt(Tab.HISTORY.getValue()).setCustomView(getTabImage(R.drawable.icon_history));
    }

    private ImageView getTabImage(int tabIconId) {
        ImageView tab = (ImageView) LayoutInflater.from(this).inflate(R.layout.icon_tab_layout, null);
        tab.setImageResource(tabIconId);
        return tab;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ブログ名クリック処理
     * @param view View
     */
    public void onClickSearchListBlogName(View view) {
        String blogName = String.valueOf(((TextView) view).getText());
        if (blogName == null || "".equals(blogName)) {
            // blogNameが存在しない場合何もせずreturn
            return;
        }
        ((SearchListFragment) sectionsPagerAdapter.getItem(Tab.SEARCH.getValue())).searchFromKeyword(blogName);
    }

    /**
     * 履歴画面の検索キーワードクリック処理
     * @param view View
     */
    public void onClickHistorySearchKeyword(View view) {
        String keyword = String.valueOf(((TextView) view).getText());
        viewPager.setCurrentItem(Tab.SEARCH.getValue());
        ((SearchListFragment) sectionsPagerAdapter.getItem(Tab.SEARCH.getValue())).searchFromKeyword(keyword);
    }

    /**
     * 履歴画面の削除ボタンクリック処理
     * @param view View
     */
    public void onClickHistoryDeleteButton(View view) {
        ((HistoryListFragment) sectionsPagerAdapter.getItem(Tab.HISTORY.getValue())).deleteHistory((long) view.getTag());
    }

    /**
     * キーワード削除ボタンタップ
     * @param view View
     */
    public void onClickSearchKeywordDeleteButton(View view) {
        ((SearchListFragment) sectionsPagerAdapter.getItem(Tab.SEARCH.getValue())).searchFromKeyword("");
    }

    /**
     * お気に入り追加ボタン
     * @param view
     */
    public void onClickSearchFavoriteButton(View view) {
        Item item = (Item) view.getTag();
        ((SearchListFragment) sectionsPagerAdapter.getItem(Tab.SEARCH.getValue())).addFavorite(item);
    }

    /**
     * お気に入り追加済みボタン
     * @param view
     */
    public void onClickSearchWasFavoriteButton(View view) {
//        Item item = (Item) view.getTag();
//        ((SearchListFragment) sectionsPagerAdapter.getItem(Tab.SEARCH.getValue())).deleteFavorite(item);
    }

    /**
     * お気に入りのブログ名クリック処理
     * @param view
     */
    public void onClickFavoriteListBlogName(View view) {
        String blogName = String.valueOf(((TextView) view).getText());
        if (blogName == null || "".equals(blogName)) {
            // blogNameが存在しない場合何もせずreturn
            return;
        }
        viewPager.setCurrentItem(Tab.SEARCH.getValue());
        ((SearchListFragment) sectionsPagerAdapter.getItem(Tab.SEARCH.getValue())).searchFromKeyword(blogName);
    }

    /**
     * お気に入り削除処理
     * @param view View
     */
    public void onClickFavoriteDeleteButton(View view) {
        ((FavoriteListFragment) sectionsPagerAdapter.getItem(Tab.FAVORITE.getValue())).deleteFavorite((long) view.getTag());
    }

    /**
     * ビデオ再生
     * @param view View
     */
    public void onClickThumbnailFromFavorite(View view) {
        Favorite favorite = (Favorite) view.getTag();
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("videoUrl", favorite.getVideoUrl());
        startActivity(intent);
    }

    /**
     * ビデオ再生
     * @param view View
     */
    public void onClickThumbnail(View view) {
        Item item = (Item) view.getTag();
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("videoUrl", item.videoUrl);
        startActivity(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        Fragment[] fragments = {new FavoriteListFragment(), new SearchListFragment(), new HistoryListFragment()};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return Tab.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
