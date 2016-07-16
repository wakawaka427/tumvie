package jp.co.wakawaka.tumvie.searchlist;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.request.RequestBuilder;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

import org.scribe.exceptions.OAuthConnectionException;

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
    private Snackbar notFoundSnackBar;

    private int offset = 0;

    private SearchListViewAdapter adapter;
    private ListView searchList;

    private LinearLayout searchListLayout;
    private ProgressBar searchListProgress;
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

        searchListLayout = (LinearLayout) view.findViewById(R.id.search_list_layout);
        setSearchKeywordEditText(view);
        // リストビューを取得
        searchList = (ListView) view.findViewById(R.id.search_list);

        searchList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int totalItemsCount) {
                if (offset == 0) {
                    return true;
                }
                subscribeGetVideoList();
                return true;
            }
        });

        progressDialog = new CustomProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        searchListProgress = (ProgressBar) view.findViewById(R.id.search_list_progress);

        return view;
    }

    /**
     * 履歴キーワードを元に検索を行う（Activityから呼び出されるメソッド）
     * @param keyword keyword
     */
    public void searchFromKeyword(String keyword) {
        searchKeywordEditText.setText(keyword);
        searchFromText();
    }

    /**
     * ソフトウェアキーボードを非表示にする
     */
    public void onFocusLoss() {
        dissmissKeyboard();
        dismissNotFoundSnackBar();
    }

    /**
     * 検索用EditTextの初期設定を行う。
     * @param view
     */
    private void setSearchKeywordEditText(final View view) {
        searchKeywordEditText = (EditText) view.findViewById(R.id.search_keyword_edit_text);
        searchKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (!keyEvent.isShiftPressed()) {
                        searchFromText();
                        return true;
                    }
                }
                return false;
            }
        });
        searchKeywordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismissNotFoundSnackBar();
                return false;
            }
        });
    }

    /**
     * 入力したキーワードによる検索処理を実行する。
     */
    private void searchFromText() {
        dismissNotFoundSnackBar();
        offset = 0;

        String keyword = String.valueOf(searchKeywordEditText.getText());
        if (keyword != null && !"".equals(keyword)) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            History history = realm.createObject(History.class);
            history.setValue(keyword);
            realm.commitTransaction();

            adapter = new SearchListViewAdapter(searchList.getContext());
            searchList.setAdapter(adapter);
            subscribeGetVideoList();
            dissmissKeyboard();
        }
    }

    /**
     * 動画リストAPIの呼び出しを申し込む。
     */
    private void subscribeGetVideoList() {
        if (adapter.getCount() == 0) {
            progressDialog.show();
        } else {
            searchListProgress.setVisibility(View.VISIBLE);
        }
        videoListObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Item>() {
                    @Override
                    public void onCompleted() {
                        searchListProgress.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        if (adapter.getCount() == 0) {
                            showNotFoundSnackBar(getActivity().getString(R.string.search_video_not_found));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ERROR", e.toString());
                        searchListProgress.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        if (e instanceof OAuthConnectionException) {
                            // 機内モード等でネットワークに接続していない場合のエラー
                            showNotFoundSnackBar(getActivity().getString(R.string.network_error_message));
                        } else {
                            showNotFoundSnackBar(getActivity().getString(R.string.search_user_not_found));
                        }
                    }

                    @Override
                    public void onNext(Item item) {
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
                        if (videoPost.getThumbnailUrl() == null || "".equals(videoPost.getThumbnailUrl())) {
                            // サムネイルのないものは表示しない。（vineやinstagram等）
                            // TODO：vimeoの動画はサムネイル取得できるけどVideoViewで再生できない・・
                            continue;
                        }
                        List<Video> videos = videoPost.getVideos();
                        // videosの最後のvideoを取得（一番サイズが大きい）
                        String html = videos.get(videos.size() - 1).getEmbedCode();
                        String[] a = html.split("src=");
                        String[] b = a[1].split("\"");
                        if (!b[1].contains("www.youtube.com")) {
                            // TODO：youtubeはvideoviewで再生できない。youtubeapiが必要
                            // TODO：vineも無理。現段階で確認しているのはこの２つ
                            // TODO：youtubeの場合そもそもvideo.getEmbededCode()で"false"が返る
                            Item item = new Item();
                            item.videoThumbnailUrl = videoPost.getThumbnailUrl();
                            item.videoUrl = b[1];
                            item.sourceBlogName = videoPost.getSourceTitle();
                            item.postId = String.valueOf(videoPost.getId());
                            subscriber.onNext(item);
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

    /**
     * 検索結果がなかった場合のスナックバーを表示する。
     * @param message 表示するメッセージ
     */
    private void showNotFoundSnackBar(String message) {
        if (notFoundSnackBar == null) {
            notFoundSnackBar = Snackbar.make(searchListLayout, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(getActivity().getString(R.string.not_found_snack_bar_dissmiss_button), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notFoundSnackBar.dismiss();
                        }
                    });
        }
        notFoundSnackBar.show();
    }

    /**
     * 検索結果がなかった場合のスナックバーを消す。
     */
    private void dismissNotFoundSnackBar() {
        if (notFoundSnackBar != null) {
            notFoundSnackBar.dismiss();
            notFoundSnackBar = null;
        }
    }

    /**
     * キーボードを消す。
     */
    private void dissmissKeyboard() {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        inputMethodManager.hideSoftInputFromWindow(searchKeywordEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
