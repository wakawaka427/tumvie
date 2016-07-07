package jp.co.wakawaka.tumvie.searchlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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

    public SearchListViewAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_search_list_item, parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        (new BitmapFromUrl()).loadBotmap(holder.fragmentSearchListThumbnail, itemList.get(position).videoThumbnailUrl);

        return convertView;
    }

    public class ViewHolder {
        public final View view;
        public final ImageView fragmentSearchListThumbnail;

        public ViewHolder(View view) {
            this.view = view;
            this.fragmentSearchListThumbnail = (ImageView) view.findViewById(R.id.fragment_search_list_thumbnail);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
