package jp.co.wakawaka.tumvie.searchlist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    private JumblrClient jumblrClient;

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

        // リストビューを取得
        searchList = (ListView) view.findViewById(R.id.search_list);
        searchList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                offset += adapter.getCount();
                subscribeVideo();
                return false;
            }
        });
        adapter = new SearchListViewAdapter(searchList.getContext());
        searchList.setAdapter(adapter);
        subscribeVideo();

        return view;
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
                    RequestBuilder requestBuilder = new RequestBuilder(jumblrClient);
                    Map<String, Object> options = new HashMap<>();
                    options.put("api_key", BuildConfig.CONSUMER_KEY);
                    options.put("type", CallbackActivity.POST_TYPE_VIDEO);
                    options.put("offset", offset) ;
                    options.put("limit", 2);
                    List<Post> posts = requestBuilder.get("/blog/glitteradio/posts", options).getPosts();
                    int count = 0;
                    for (Post post : posts) {
                        VideoPost videoPost = (VideoPost) post;
                        if (videoPost.getPermalinkUrl() != null && !"".equals(videoPost.getPermalinkUrl())) {
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
//                            item.videoThumbnailBitmap = getBitmap(videoPost.getThumbnailUrl());
                            item.videoThumbnailUrl = videoPost.getThumbnailUrl();
                            item.videoUrl = b[1];
                            subscriber.onNext(item);
                            count++;
                            // 1つの動画に3つのサイズ(250,400,500)がある。暫定で一番小さいのを再生してbreak
                            break;
                        }
//                        if (count > 10) {
//                            break;
//                        }
                    }
                    // TODO：ダッシュボードの続きを取得する方法は？
                    // TODO：RequestBuilder自前で用意したらいけるかも
                    subscriber.onCompleted();
                }
            }
    );
}
