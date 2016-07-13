package jp.co.wakawaka.tumvie.historylist;

import android.content.Context;
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
 *
 */
public class HistoryListFragment extends Fragment {

    private Realm realm;
    private View view;
    private ListView historyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history_list, container, false);
        historyList = (ListView) view.findViewById(R.id.history_list);

        return view;
    }

    public void reloadList() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        RealmResults<History> histories = realm
                .where(History.class)
                .findAllSorted("currentTimeMillis", Sort.DESCENDING);
        if (histories != null && histories.size() != 0) {
            HistoryListViewAdapter historyListAdapter = new HistoryListViewAdapter(historyList.getContext(), histories);
            historyList.setAdapter(historyListAdapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }
}