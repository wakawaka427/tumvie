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

    private static final Object LOCK = new Object();

    private JumblrClient jumblrClient;
    private String searchText;
    private InputMethodManager inputMethodManager;

    private SearchListViewAdapter adapter;
    private ListView searchList;
    private int offset = 0;
    private CustomProgressDialog progressDialog;

    private SwipyRefreshLayout mSwipeRefreshLayout;

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

        setSearchText(view);

        // リストビューを取得
        searchList = (ListView) view.findViewById(R.id.search_list);

        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (SwipyRefreshLayoutDirection.BOTTOM.equals(direction)) {
                    subscribeVideo();
                }
            }
        });

        progressDialog = new CustomProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        return view;
    }

    private void setSearchText(final View view) {
        EditText textView = (EditText) view.findViewById(R.id.editText);
        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (!keyEvent.isShiftPressed()) {
                        offset = 0;
                        searchText = String.valueOf(textView.getText());

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        History history = realm.createObject(History.class);
                        history.setValue(searchText);
                        realm.commitTransaction();

                        adapter = new SearchListViewAdapter(searchList.getContext());
                        searchList.setAdapter(adapter);
                        subscribeVideo();
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void subscribeVideo() {
        synchronized (LOCK) {
            if (adapter.getCount() == 0) {
                progressDialog.show();
            }
            videoObservable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Item>() {
                        @Override
                        public void onCompleted() {
                            mSwipeRefreshLayout.setRefreshing(false);
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
    }

    private Observable<Item> videoObservable = Observable.create(
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
                    return requestBuilder.get("/blog/" + searchText + "/posts", options).getPosts();
                }
            }
    );
}
