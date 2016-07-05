package jp.co.wakawaka.tumvie.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.fragment.Item;
import jp.co.wakawaka.tumvie.fragment.ItemFragment;

public class ListActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    public void onListFragmentInteraction(Item item) {
        // TODO：FragmentからのCallback
    }
}
