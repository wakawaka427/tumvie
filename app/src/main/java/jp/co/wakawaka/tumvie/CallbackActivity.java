package jp.co.wakawaka.tumvie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.request.RequestBuilder;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.PhotoSize;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CallbackActivity extends AppCompatActivity {

    private JumblrClient jumblrClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);

        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        if (token != null) {
            Token.saveAccessToken(this, token);
        }
        String secret = intent.getStringExtra("secret");
        if (secret != null) {
            Token.saveAccessSecretToken(this, secret);
        }

        jumblrClient = new JumblrClient(
                BuildConfig.CONSUMER_KEY
                , BuildConfig.CONSUMER_SECRET
                , Token.getAccessToken(this)
                , Token.getAccessTokenSecret(this)
        );

        Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        User user = jumblrClient.user();
                        subscriber.onNext(user.getName());
                        subscriber.onCompleted();
                    }
                }
        )
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                // 処理完了コールバック
            }

            @Override
            public void onError(Throwable e) {
                // 処理内で例外が発生すると自動的にonErrorが呼ばれる
            }

            @Override
            public void onNext(String str) {
                TextView name = (TextView) findViewById(R.id.name);
                name.setText(str);
            }
        });

        photoObservable.subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Bitmap>() {
            @Override
            public void onCompleted() {
                // 処理完了コールバック
            }
            @Override
            public void onError(Throwable e) {
                // 処理内で例外が発生すると自動的にonErrorが呼ばれる
            }
            @Override
            public void onNext(Bitmap bitmap) {
                ImageView image = (ImageView) findViewById(R.id.image);
                image.setImageBitmap(bitmap);
            }
        });

        videoObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        // 処理完了コールバック
                    }
                    @Override
                    public void onError(Throwable e) {
                        // 処理内で例外が発生すると自動的にonErrorが呼ばれる
                    }
                    @Override
                    public void onNext(String str) {
                        Context context = CallbackActivity.this.getApplicationContext();

                        Allocator allocator = new DefaultAllocator(256); // TODO：引数適当
                        DataSource dataSource = new DefaultUriDataSource(context, null, "userAgent");
                        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                                Uri.parse("http://html5demos.com/assets/dizzy.mp4"), dataSource, allocator, 64 * 1024); // TODO：引数適当
                        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(
                                context, sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(
                                sampleSource, MediaCodecSelector.DEFAULT);

                        SurfaceView surface = (SurfaceView) findViewById(R.id.surface);
                        ExoPlayer player = ExoPlayer.Factory.newInstance(2);
                        player.prepare(videoRenderer, audioRenderer);
                        player.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
                        player.setPlayWhenReady(true);
                    }
                });
    }

    private Observable<Bitmap> photoObservable = Observable.create(
            new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
//                        List<Post> posts = jumblrClient.userDashboard();
                    RequestBuilder requestBuilder = new RequestBuilder(jumblrClient);
                    requestBuilder.setConsumer(BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET);
                    requestBuilder.setToken(Token.getAccessToken(CallbackActivity.this), Token.getAccessTokenSecret(CallbackActivity.this));
                    Map<String, String> options = new HashMap<>();
                    options.put("type", POST_TYPE_PHOTO);
                    List<Post> posts = requestBuilder.get("/user/dashboard", options).getPosts();
                    for (Post post : posts) {
                        if (POST_TYPE_PHOTO.equals(post.getType())) {
                            PhotoPost photoPost = (PhotoPost) post;
                            List<Photo> photos = photoPost.getPhotos();
                            for (Photo photo : photos) {
                                List<PhotoSize> sizes = photo.getSizes();
                                for (PhotoSize size : sizes) {
                                    InputStream imageIs;
                                    try {
                                        URL imageUrl = new URL(size.getUrl());
                                        imageIs = imageUrl.openStream();
                                        Bitmap image = BitmapFactory.decodeStream(imageIs);
                                        subscriber.onNext(image);
                                    } catch (MalformedURLException e) {
                                    } catch (IOException e) {
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                        break;
                    }
                    // TODO：ダッシュボードの続きを取得する方法は？
                    // TODO：RequestBuilder自前で用意したらいけるかも
                    subscriber.onCompleted();
                }
            }
    );

    private Observable<String> videoObservable = Observable.create(
            new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {RequestBuilder requestBuilder = new RequestBuilder(jumblrClient);
                    requestBuilder.setConsumer(BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET);
                    requestBuilder.setToken(Token.getAccessToken(CallbackActivity.this), Token.getAccessTokenSecret(CallbackActivity.this));
                    Map<String, String> options = new HashMap<>();
                    options.put("type", POST_TYPE_VIDEO);
                    List<Post> posts = requestBuilder.get("/user/dashboard", options).getPosts();
                    for (Post post : posts) {
                        VideoPost videoPost = (VideoPost) post;
                        List<Video> videos = videoPost.getVideos();
                        for (Video video : videos) {
                            String html = video.getEmbedCode();
                            String[] a = html.split("source");
                            String[] b = a[1].split("src");
                            String[] c = b[1].split("\"");
                            subscriber.onNext(c[1]);
                            break;
                        }
                        break;
                    }
                }
            }
    );

    // TODO：どっかに移す
    public static final String POST_TYPE_TEXT = "text";     //テキスト
    public static final String POST_TYPE_QUOTE = "quote";   //引用
    public static final String POST_TYPE_PHOTO = "photo";   //画像
    public static final String POST_TYPE_LINK = "link";     //リンク
    public static final String POST_TYPE_CHAT = "chat";     //チャット
    public static final String POST_TYPE_AUDIO = "audio";   //音声
    public static final String POST_TYPE_VIDEO = "video";   //動画
    public static final String POST_TYPE_ANSWER = "answer"; //アンサー
}
