package jp.co.wakawaka.tumvie.searchlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.co.wakawaka.tumvie.R;

/**
 * 動画リスト用Adapter
 * Created by wakabayashieisuke on 2016/07/07.
 */
public class SearchListViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater = null;
    List<Item> itemList;

    public SearchListViewAdapter(Context context) {
        this.itemList = new ArrayList<>();
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_search_list_item, parent,false);
        }

        Item item = itemList.get(position);

//        ((TextView) convertView.findViewById(R.id.debug_thumbnail_url)).setText("URL:" + item.videoThumbnailUrl);
//        ((TextView) convertView.findViewById(R.id.debug_id)).setText("Id:" + item.postId);
        if (item.sourceBlogName != null && !"".equals(item.sourceBlogName)) {
            ((TextView) convertView.findViewById(R.id.search_list_source_blog_name_text)).setText(item.sourceBlogName);
        }

        ImageView thumbnailImageView = (ImageView) convertView.findViewById(R.id.fragment_search_list_thumbnail);
        Item tagItem = (Item) thumbnailImageView.getTag();
        // ImageViewにタグをつけておいて、同じURLじゃなかったら表示する
        if (tagItem == null ||
                !tagItem.videoThumbnailUrl.equals(item.videoThumbnailUrl)) {
            String videoThumbnailUrl = item.videoThumbnailUrl;
            Picasso.with(context).load(videoThumbnailUrl).into(thumbnailImageView, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    // TODO：No thumbnailみたいな画像をここで表示する
                }
            });
            thumbnailImageView.setTag(item);
        }

        ImageButton favoriteButton = (ImageButton) convertView.findViewById(R.id.search_favorite_button);
        ImageButton wasFavoriteButton = (ImageButton) convertView.findViewById(R.id.search_was_favorite_button);
        if (item.isFavorite) {
            favoriteButton.setVisibility(View.GONE);
            wasFavoriteButton.setVisibility(View.VISIBLE);
            wasFavoriteButton.setTag(item);
        } else {
            favoriteButton.setVisibility(View.VISIBLE);
            wasFavoriteButton.setVisibility(View.GONE);
            favoriteButton.setTag(item);
        }

        return convertView;
    }

    public boolean add(Item item){
        boolean ress = itemList.add(item);
        if (ress) {
            notifyDataSetChanged();
        }
        return ress;
    }
}
