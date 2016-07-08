package jp.co.wakawaka.tumvie.listfragmenttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.request.RequestBuilder;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

import jp.co.wakawaka.tumvie.BuildConfig;
import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.activity.CallbackActivity;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;


    private JumblrClient jumblrClient;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        jumblrClient = new JumblrClient(
                BuildConfig.CONSUMER_KEY
                , BuildConfig.CONSUMER_SECRET
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.video_list);

        // TODO：仮のデータ
        subscribeVideo(recyclerView);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Item item);
    }

    private void subscribePhoto(final RecyclerView recyclerView) {
        photoObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    private Bitmap captureImageBitmap;

                    @Override
                    public void onCompleted() {
                        Context context = recyclerView.getContext();
                        if (mColumnCount <= 1) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        } else {
                            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                        }
                        List<Item> items = new ArrayList<>();
                        for (int i = 0; i < 20; i++) {
                            Item item = new Item();
                            item.userName = "test";
                            item.videoThumbnailBitmap = captureImageBitmap;
                            items.add(item);
                        }
                        recyclerView.setAdapter(new MyItemRecyclerViewAdapter(items, mListener));
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        captureImageBitmap = bitmap;
                    }
                });
    }

    private Observable<Bitmap> photoObservable = Observable.create(
            new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
                    InputStream imageIs;
                    try {
                        URL imageUrl = new URL("https://cdn.qiita.com/assets/qiita-fb-f1d6559f13f7e8de7260c6cec4d3b8f9c2eab8322a69fd786baea877d220278b.png");
                        imageIs = imageUrl.openStream();
                        Bitmap image = BitmapFactory.decodeStream(imageIs);
                        subscriber.onNext(image);
                    } catch (MalformedURLException e) {
                    } catch (IOException e) {
                    }
                    // TODO：ダッシュボードの続きを取得する方法は？
                    // TODO：RequestBuilder自前で用意したらいけるかも
                    subscriber.onCompleted();
                }
            }
    );

    private void subscribeVideo(final RecyclerView recyclerView) {
        videoObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Item>() {
                    List<Item> items = new ArrayList<>();

                    @Override
                    public void onCompleted() {
                        Context context = recyclerView.getContext();
                        if (mColumnCount <= 1) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        } else {
                            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                        }
                        recyclerView.setAdapter(new MyItemRecyclerViewAdapter(items, mListener));
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(Item item) {
                        item.userName = "dummy";
                        items.add(item);
                    }
                });
    }

    private Observable<Item> videoObservable = Observable.create(
            new Observable.OnSubscribe<Item>() {
                @Override
                public void call(Subscriber<? super Item> subscriber) {
//                        List<Post> posts = jumblrClient.userDashboard();
                    RequestBuilder requestBuilder = new RequestBuilder(jumblrClient);
                    Map<String, String> options = new HashMap<>();
                    options.put("api_key", BuildConfig.CONSUMER_KEY);
                    options.put("type", CallbackActivity.POST_TYPE_VIDEO);
                    List<Post> posts = requestBuilder.get("/blog/glitteradio/posts", options).getPosts();
                    for (Post post : posts) {
                        VideoPost videoPost = (VideoPost) post;
                        if (videoPost.getPermalinkUrl() != null && !"".equals(videoPost.getPermalinkUrl())) {
                            // TODO：vineとかインスタとかはpermalinkがある（permalinkがあることがその保証かはわからん）
                            // TODO：こいつらの場合はwebviewにするとか考えたほうがよさげ
                            Item item = new Item();
                            item.videoUrl = videoPost.getPermalinkUrl();
                            subscriber.onNext(item);
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
                            item.videoThumbnailBitmap = getBitmap(videoPost.getThumbnailUrl());
                            item.videoUrl = b[1];
                            subscriber.onNext(item);
                            // 1つの動画に3つのサイズ(250,400,500)がある。暫定で一番小さいのを再生してbreak
                            break;
                        }
                    }
                    // TODO：ダッシュボードの続きを取得する方法は？
                    // TODO：RequestBuilder自前で用意したらいけるかも
                    subscriber.onCompleted();
                }
                private Bitmap getBitmap(String url) {
                    InputStream imageIs;
                    try {
                        URL imageUrl = new URL(url);
                        imageIs = imageUrl.openStream();
                        return BitmapFactory.decodeStream(imageIs);
                    } catch (MalformedURLException e) {
                    } catch (IOException e) {
                    }
                    return null;
                }
            }
    );
}