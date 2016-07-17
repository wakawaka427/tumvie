package jp.co.wakawaka.tumvie.favoritelist;

import android.content.Context;
import android.net.Uri;
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
import jp.co.wakawaka.tumvie.historylist.HistoryListViewAdapter;
import jp.co.wakawaka.tumvie.realm.Favorite;

public class FavoriteListFragment extends Fragment {

    private Realm realm;
    private FavoriteListViewAdapter historyListAdapter;
    private ListView favorites;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_list, container, false);
    }

    public void reloadList() {
        if (favorites == null) {
            favorites = (ListView) getActivity().findViewById(R.id.favorite_list);
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        RealmResults<Favorite> favorites = realm
                .where(Favorite.class)
                .findAllSorted("currentTimeMillis", Sort.DESCENDING);
        historyListAdapter = new FavoriteListViewAdapter(this.favorites.getContext(), favorites);
        this.favorites.setAdapter(historyListAdapter);
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
        Favorite favorite = realm.where(Favorite.class).equalTo("id", id).findFirst();
        if (favorite != null) {
            favorite.deleteFromRealm();
        }
        realm.commitTransaction();
        reloadList();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
