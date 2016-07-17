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

import java.util.List;

import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.realm.Favorite;

/**
 * 動画リスト用Adapter
 * Created by wakabayashieisuke on 2016/07/07.
 */
public class FavoriteListViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater = null;
    List<Favorite> favorites;

    public FavoriteListViewAdapter(Context context, List<Favorite> favorites) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.favorites = favorites;
    }

    @Override
    public int getCount() {
        return favorites.size();
    }

    @Override
    public Object getItem(int position) {
        return favorites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return favorites.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_favorite_list_item, parent,false);
        }

        Favorite favorite = favorites.get(position);
        ((TextView) convertView.findViewById(R.id.favorite_list_source_blog_name_text)).setText(favorite.getSourceBlogName());

        ImageView thumbnailImageView = (ImageView) convertView.findViewById(R.id.fragment_favorite_list_thumbnail);
        Favorite tagItem = (Favorite) thumbnailImageView.getTag();
        // ImageViewにタグをつけておいて、同じURLじゃなかったら表示する
        if (tagItem == null ||
                !tagItem.getVideoCaptionThumbnailUrl().equals(favorite.getVideoCaptionThumbnailUrl())) {
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
            thumbnailImageView.setTag(favorite);
        }

        ImageButton trashButton = (ImageButton) convertView.findViewById(R.id.favorite_trash_button);
        trashButton .setTag(favorite.getId());

        return convertView;
    }

    public boolean add(Favorite favorite){
        boolean ress = favorites.add(favorite);
        if (ress) {
            notifyDataSetChanged();
        }
        return ress;
    }
}
