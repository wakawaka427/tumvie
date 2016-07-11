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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.request.RequestBuilder;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.wakawaka.tumvie.BuildConfig;
import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.activity.CallbackActivity;
import jp.co.wakawaka.tumvie.listfragmenttest.Item;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */
public class SearchListFragment extends Fragment {
    private static final int SEARCH_TARGET_BLOG = 0;
    private static final int SEARCH_TARGET_TAG = 1;

    private JumblrClient jumblrClient;
    private int searchTarget;
    private String searchText;
    private InputMethodManager inputMethodManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jumblrClient = new JumblrClient(
                BuildConfig.CONSUMER_KEY
                , BuildConfig.CONSUMER_SECRET
        );
    }

    private SearchListViewAdapter adapter;
    private ListView searchList;
    private int offset = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        setSearchSpinner(view);
        setSearchText(view);

        // リストビューを取得
        searchList = (ListView) view.findViewById(R.id.search_list);
        searchList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (offset == 0) {
                    return false;
                }
                subscribeVideo();
                return false;
            }
        });

        return view;
    }

    private void setSearchSpinner(View view) {
        // スピナー設定
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        // ArrayAdapter を、string-array とデフォルトのレイアウトを引数にして生成
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.search_targets, android.R.layout.simple_spinner_item);
        // 選択肢が表示された時に使用するレイアウトを指定
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // スピナーにアダプターを設定
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Spinner spinner = (Spinner) adapterView;
                searchTarget = (int) spinner.getSelectedItemId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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
        videoObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Item>() {
                    @Override
                    public void onCompleted() {
//                        searchList.setAdapter(adapter);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e("ERROR", e.toString());
                    }
                    @Override
                    public void onNext(Item item) {
                        item.userName = "dummy";
                        adapter.add(item);
                    }
                });
    }

    private Observable<Item> videoObservable = Observable.create(
            new Observable.OnSubscribe<Item>() {
                @Override
                public void call(Subscriber<? super Item> subscriber) {
                    List<Post> posts = getPosts();
                    for (Post post : posts) {
                        offset++;
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
                    options.put("offset", offset);
                    if (SEARCH_TARGET_BLOG == searchTarget) {
                        return requestBuilder.get("/blog/" + searchText + "/posts", options).getPosts();
                    } else {
                        options.put("tag", searchText);
                        return requestBuilder.get("/tagged", options).getPosts();
                    }
                }
            }
    );
}
