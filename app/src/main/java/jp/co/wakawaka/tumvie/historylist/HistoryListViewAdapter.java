package jp.co.wakawaka.tumvie.historylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.realm.History;

/**
 * 動画リスト用Adapter
 * Created by wakabayashieisuke on 2016/07/07.
 */
public class HistoryListViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<History> histories;
    Bitmap iconTrach;

    public HistoryListViewAdapter(Context context, List<History> histories) {
        this.context = context;
        this.histories = histories;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.iconTrach = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_trash);
    }

    @Override
    public int getCount() {
        return histories.size();
    }

    @Override
    public Object getItem(int position) {
        return histories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return histories.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_history_list_item, null);
            holder = new ViewHolder();
            holder.historySearchKeyword = (TextView) convertView.findViewById(R.id.history_search_keyword);
            holder.trashButton = (ImageButton) convertView.findViewById(R.id.trash_button);
            holder.trashButton.setImageBitmap(iconTrach);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        History history = (History) getItem(position);
        holder.historySearchKeyword.setText(history.getKeyword());

        return convertView;
    }

    private class ViewHolder {
        private TextView historySearchKeyword;
        private ImageButton trashButton;
    }
}
