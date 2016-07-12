package jp.co.wakawaka.tumvie.historylist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.realm.History;

/**
 * Created by wakabayashieisuke on 2016/07/07.
 */
public class HistoryListViewAdapter
        extends RealmBasedRecyclerViewAdapter<History, HistoryListViewAdapter.ViewHolder> {

    public class ViewHolder extends RealmViewHolder {

        public TextView todoTextView;
        public ViewHolder(LinearLayout container) {
            super(container);
            this.todoTextView = (TextView) container.findViewById(R.id.keyword);
        }
    }

    public HistoryListViewAdapter(
            Context context,
            RealmResults<History> realmResults,
            boolean automaticUpdate,
            boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.fragment_history_list_item, viewGroup, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        final History history = realmResults.get(position);
        viewHolder.todoTextView.setText(history.getKeyword());
//        viewHolder.itemView.setBackgroundColor(
//                COLORS[(int) (toDoItem.getId() % COLORS.length)]
//        );
    }
}