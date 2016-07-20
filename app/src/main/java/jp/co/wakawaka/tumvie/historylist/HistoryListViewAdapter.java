package jp.co.wakawaka.tumvie.historylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import jp.co.wakawaka.tumvie.R;

/**
 * 履歴リスト用Adapter
 * Created by wakabayashieisuke on 2016/07/07.
 */
public class HistoryListViewAdapter extends RealmBaseAdapter<History> {
    Bitmap iconTrash;

    public HistoryListViewAdapter(Context context, RealmResults<History> histories) {
        super(context, histories);
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.icon_trash);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        this.iconTrash = bitmap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_history_list_item, parent, false);
            holder = new ViewHolder();
            holder.historySearchKeyword = (TextView) convertView.findViewById(R.id.history_search_keyword);
            holder.trashButton = (ImageButton) convertView.findViewById(R.id.history_trash_button);
            holder.trashButton.setImageBitmap(iconTrash);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final History history = getItem(position);
        holder.historySearchKeyword.setText(history.getKeyword());
        holder.trashButton.setTag(history.getId());

        return convertView;
    }

    private static class ViewHolder {
        private TextView historySearchKeyword;
        private ImageButton trashButton;
    }
}
