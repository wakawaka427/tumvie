package jp.co.wakawaka.tumvie.favoritelist;

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
import jp.co.wakawaka.tumvie.realm.Favorite;

/**
 * お気に入りイストのFragmentクラス
 */
public class FavoriteListFragment extends Fragment {

    private Realm realm;
    private FavoriteListViewAdapter favoriteListAdapter;
    private ListView favoriteList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);
        favoriteList = (ListView) view.findViewById(R.id.favorite_list);

        return view;
    }

    public void reloadList() {
        if (favoriteList == null) {
            favoriteList = (ListView) getActivity().findViewById(R.id.favorite_list);
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        RealmResults<Favorite> favorites = realm
                .where(Favorite.class)
                .findAllSorted("currentTimeMillis", Sort.DESCENDING);
        favoriteListAdapter = new FavoriteListViewAdapter(this.favoriteList.getContext(), favorites);
        this.favoriteList.setAdapter(favoriteListAdapter);
    }

    /**
     * 指定した行を削除する。
     * @param id FavoriteテーブルのID
     */
    public void deleteFavorite(final long id) {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        Favorite favorite = realm.where(Favorite.class).equalTo("id", id).findFirst();
        if (favorite != null) {
            favorite.deleteFromRealm();
        }
        realm.commitTransaction();
//        reloadList();
        favoriteListAdapter.notifyDataSetChanged();
    }
}