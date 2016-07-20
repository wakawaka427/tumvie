package jp.co.wakawaka.tumvie.favoritelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.realm.Favorite;

/**
 * お気に入りリスト用Adapter
 * Created by wakabayashieisuke on 2016/07/07.
 */
public class FavoriteListViewAdapter extends RealmBaseAdapter<Favorite> {

    public FavoriteListViewAdapter(Context context, RealmResults<Favorite> favorites) {
        super(context, favorites);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_favorite_list_item, parent,false);
        }

        Favorite favorite = getItem(position);
        ((TextView) convertView.findViewById(R.id.favorite_list_source_blog_name_text)).setText(favorite.getSourceBlogName());

        ImageView thumbnailImageView = (ImageView) convertView.findViewById(R.id.fragment_favorite_list_thumbnail);
        String videoThumbnailUrl = favorite.getVideoCaptionThumbnailUrl();
        Picasso.with(context).load(videoThumbnailUrl).into(thumbnailImageView, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                // TODO：No thumbnailみたいな画像をここで表示する
            }
        });

        ImageButton trashButton = (ImageButton) convertView.findViewById(R.id.favorite_trash_button);
        trashButton.setTag(favorite.getId());

        return convertView;
    }
}
