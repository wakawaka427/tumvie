package jp.co.wakawaka.tumvie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.tumblr.jumblr.request.RequestBuilder;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.PhotoSize;
import com.tumblr.jumblr.types.Post;

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

/**
 * Created by wakabayashieisuke on 2016/07/07.
 */
public class BitmapFromUrl {

    private String url;

    public void loadBotmap(ImageView imageViewr, String url) {
        this.url = url;
        photoObservable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new MyObserver<Bitmap>(imageViewr));
    }

    private Observable<Bitmap> photoObservable = Observable.create(
            new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
                    Bitmap bitmap = getBitmap(url.toString());
                    subscriber.onNext(bitmap);
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

    public class MyObserver<T> implements Observer<T> {
        private ImageView imageView;
        private Bitmap bitmap;
        public MyObserver(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        public void onCompleted() {
            // 処理完了コールバック
            imageView.setImageBitmap(bitmap);
        }
        @Override
        public void onError(Throwable e) {
            // 処理内で例外が発生すると自動的にonErrorが呼ばれる
        }
        @Override
        public void onNext(T bitmap) {
            this.bitmap = (Bitmap) bitmap;
        }
    }
}
