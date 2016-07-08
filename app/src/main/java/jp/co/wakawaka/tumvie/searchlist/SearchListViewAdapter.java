package jp.co.wakawaka.tumvie.searchlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.co.wakawaka.tumvie.BitmapFromUrl;
import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.listfragmenttest.Item;

/**
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

        ImageView thumbnailImageView = (ImageView) convertView.findViewById(R.id.fragment_search_list_thumbnail);

        // ImageViewにタグをつけておいて、同じURLじゃなかったら表示する
        if (thumbnailImageView.getTag() == null ||
                !thumbnailImageView.getTag().equals(itemList.get(position).videoThumbnailUrl)) {
            String videoThumbnailUrl = itemList.get(position).videoThumbnailUrl;
            Picasso.with(context).load(videoThumbnailUrl).into(thumbnailImageView);
            thumbnailImageView.setTag(videoThumbnailUrl);
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
