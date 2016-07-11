package jp.co.wakawaka.tumvie.historylist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.realm.History;
import jp.co.wakawaka.tumvie.searchlist.EndlessScrollListener;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class HistoryListFragment extends Fragment {

    private Realm realm;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history_list, container, false);

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
            HistoryListViewAdapter historyListAdapter = new HistoryListViewAdapter(getContext(), histories, true, true);
            RealmRecyclerView realmRecyclerView = (RealmRecyclerView) view.findViewById(R.id.history_list);
            realmRecyclerView.setAdapter(historyListAdapter);
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