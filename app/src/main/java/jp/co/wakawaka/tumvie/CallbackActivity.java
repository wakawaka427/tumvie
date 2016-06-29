package jp.co.wakawaka.tumvie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.PhotoSize;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CallbackActivity extends AppCompatActivity {

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

        final JumblrClient jumblrClient = new JumblrClient(
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

        Observable.create(
                new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {
                        List<Post> posts =  jumblrClient.userDashboard();
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

                        subscriber.onCompleted();
                    }
                }
        )
        .subscribeOn(Schedulers.newThread())
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
    }

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
