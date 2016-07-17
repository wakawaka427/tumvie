package jp.co.wakawaka.tumvie.historylist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.realm.History;

/**
 * 履歴リストのFragmentクラス
 */
public class HistoryListFragment extends Fragment {

    private Realm realm;
    private HistoryListViewAdapter historyListAdapter;
    private ListView historyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);
        historyList = (ListView) view.findViewById(R.id.history_list);

        return view;
    }

    public void reloadList() {
        if (historyList == null) {
            historyList = (ListView) getActivity().findViewById(R.id.history_list);
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        RealmResults<History> histories = realm
                .where(History.class)
                .findAllSorted("currentTimeMillis", Sort.DESCENDING);
        historyListAdapter = new HistoryListViewAdapter(historyList.getContext(), histories);
        historyList.setAdapter(historyListAdapter);
    }

    /**
     * 指定した行を削除する。
     * @param id HistoryテーブルのID
     */
    public void deleteHistory(final long id) {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        History history = realm.where(History.class).equalTo("id", id).findFirst();
        if (history != null) {
            history.deleteFromRealm();
        }
        realm.commitTransaction();
        reloadList();
    }
}