package jp.co.wakawaka.tumvie.searchlist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.request.RequestBuilder;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import jp.co.wakawaka.tumvie.BuildConfig;
import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.activity.CallbackActivity;
import jp.co.wakawaka.tumvie.listfragmenttest.Item;
import jp.co.wakawaka.tumvie.realm.History;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */
public class SearchListFragment extends Fragment {

    /** テスト用tumblrアカウントメモ：edielec */

    private JumblrClient jumblrClient;
    private InputMethodManager inputMethodManager;
    private CustomProgressDialog progressDialog;

    private int offset = 0;

    private SearchListViewAdapter adapter;
    private ListView searchList;

    private SwipyRefreshLayout swipeRefreshLayout;
    private EditText searchKeywordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jumblrClient = new JumblrClient(
                BuildConfig.CONSUMER_KEY
                , BuildConfig.CONSUMER_SECRET
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        setSearchKeywordEditText(view);

        // リストビューを取得
        searchList = (ListView) view.findViewById(R.id.search_list);
        swipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (SwipyRefreshLayoutDirection.BOTTOM.equals(direction)) {
                    subscribeGetVideoList();
                }
            }
        });

        progressDialog = new CustomProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        return view;
    }

    /**
     * 履歴キーワードを元に検索を行う（Activityから呼び出されるメソッド）
     * @param hitosryKeyword
     */
    public void searchFromHistoryKeywird(String hitosryKeyword) {
        searchKeywordEditText.setText(hitosryKeyword);
    }

    /**
     * 検索よ用EditTextの初期設定を行う。
     * @param view
     */
    private void setSearchKeywordEditText(final View view) {
        searchKeywordEditText = (EditText) view.findViewById(R.id.editText);
        searchKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (!keyEvent.isShiftPressed()) {
                        searchFromText(textView);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 入力したキーワードによる検索処理を実行する。
     * @param textView
     */
    private void searchFromText(TextView textView) {
        offset = 0;

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        History history = realm.createObject(History.class);
        history.setValue(String.valueOf(textView.getText()));
        realm.commitTransaction();

        adapter = new SearchListViewAdapter(searchList.getContext());
        searchList.setAdapter(adapter);
        subscribeGetVideoList();
        inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 動画リストAPIの呼び出しを申し込む。
     */
    private void subscribeGetVideoList() {
        if (adapter.getCount() == 0) {
            progressDialog.show();
        }
        videoListObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Item>() {
                    @Override
                    public void onCompleted() {
                        swipeRefreshLayout.setRefreshing(false);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ERROR", e.toString());
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onNext(Item item) {
                        item.userName = "dummy";
                        adapter.add(item);
                    }
                });
    }

    /**
     * 動画リストAPI呼び出し用Observable
     */
    private Observable<Item> videoListObservable = Observable.create(
            new Observable.OnSubscribe<Item>() {
                private static final int LIMIT = 10;
                @Override
                public void call(Subscriber<? super Item> subscriber) {
                    List<Post> posts = getPosts();
                    offset += LIMIT;
                    for (Post post : posts) {
                        VideoPost videoPost = (VideoPost) post;
                        if (videoPost.getThumbnailUrl() == null || "".equals(videoPost.getThumbnailUrl()))  {
                            // サムネイルのないものは表示しない。（vineやinstagram等）
                            continue;
                        }
                        List<Video> videos = videoPost.getVideos();
                        for (Video video : videos) {
                            String html = video.getEmbedCode();
                            String[] a = html.split("src=");
                            String[] b = a[1].split("\"");
                            if (b[1].contains("www.youtube.com")) {
                                // TODO：youtubeはvideoviewで再生できない。youtubeapiが必要
                                // TODO：vineも無理。現段階で確認しているのはこの２つ
                                continue;
                            }
                            Item item = new Item();
                            item.videoThumbnailUrl = videoPost.getThumbnailUrl();
                            item.videoUrl = b[1];
                            item.postId = String.valueOf(videoPost.getId());
                            subscriber.onNext(item);
                            // 1つの動画に3つのサイズ(250,400,500)がある。暫定で一番小さいのを再生してbreak
                            break;
                        }
                    }
                    subscriber.onCompleted();
                }

                private List<Post> getPosts() {
                    RequestBuilder requestBuilder = new RequestBuilder(jumblrClient);
                    Map<String, Object> options = new HashMap<>();
                    options.put("api_key", BuildConfig.CONSUMER_KEY);
                    options.put("type", CallbackActivity.POST_TYPE_VIDEO);
                    options.put("limit", LIMIT);
                    options.put("offset", offset);
                    return requestBuilder.get("/blog/" + String.valueOf(searchKeywordEditText.getText()) + "/posts", options).getPosts();
                }
            }
    );
}
